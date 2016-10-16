/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ai.group_template;

import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.datatables.SpawnTable;
import l2server.gameserver.instancemanager.ZoneManager;
import l2server.gameserver.model.L2Spawn;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2MonsterInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.quest.Quest;
import l2server.gameserver.model.zone.L2ZoneType;
import l2server.gameserver.model.zone.type.L2EffectZone;
import l2server.gameserver.util.Util;
import l2server.util.Rnd;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SeedOfAnnihilation extends Quest
{
	private static final String qn = "SeedOfAnnihilation";
	private static final Map<Integer, int[]> teleportZones = new HashMap<Integer, int[]>();
	private static final int ANNIHILATION_FURNACE = 18928;
	// Strength, Agility, Wisdom
	private static final int[] ZONE_BUFFS = {0, 6443, 6444, 6442};
	private static final int[][] ZONE_BUFFS_LIST = {{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 2, 1}, {3, 1, 2}};

	// 0: Bistakon, 1: Reptilikon, 2: Cokrakon
	private SeedRegion[] regionsData = new SeedRegion[3];
	private Long seedsNextStatusChange;

	private static class SeedRegion
	{
		public int[] elite_mob_ids;
		public int[][] minion_lists;
		public int buff_zone;
		public int[][] af_spawns;
		public L2Npc[] af_npcs = new L2Npc[2];
		public int activeBuff = 0;

		public SeedRegion(int[] emi, int[][] ml, int bz, int[][] as)
		{
			elite_mob_ids = emi;
			minion_lists = ml;
			buff_zone = bz;
			af_spawns = as;
		}
	}

	static
	{
		teleportZones.put(60002, new int[]{-213175, 182648, -10992});
		teleportZones.put(60003, new int[]{-181217, 186711, -10528});
		teleportZones.put(60004, new int[]{-180211, 182984, -15152});
		teleportZones.put(60005, new int[]{-179275, 186802, -10720});
	}

	public void loadSeedRegionData()
	{
		// Bistakon data
		regionsData[0] = new SeedRegion(new int[]{22750, 22751, 22752, 22753},
				new int[][]{{22746, 22746, 22746}, {22747, 22747, 22747}, {22748, 22748, 22748}, {22749, 22749, 22749}},
				60006, new int[][]{{-180450, 185507, -10544, 11632}, {-180005, 185489, -10544, 11632}});

		// Reptilikon data
		regionsData[1] = new SeedRegion(new int[]{22757, 22758, 22759}, new int[][]{{22754, 22755, 22756}}, 60007,
				new int[][]{{-179600, 186998, -10704, 11632}, {-179295, 186444, -10704, 11632}});

		// Cokrakon data
		regionsData[2] = new SeedRegion(new int[]{22763, 22764, 22765}, new int[][]{
				{22760, 22760, 22761},
				{22760, 22760, 22762},
				{22761, 22761, 22760},
				{22761, 22761, 22762},
				{22762, 22762, 22760},
				{22762, 22762, 22761}
		}, 60008, new int[][]{{-180971, 186361, -10528, 11632}, {-180758, 186739, -10528, 11632}});
		int buffsNow = 0;
		String var = loadGlobalQuestVar("SeedNextStatusChange");
		if (var.equalsIgnoreCase("") || Long.parseLong(var) < System.currentTimeMillis())
		{
			buffsNow = Rnd.get(ZONE_BUFFS_LIST.length);
			saveGlobalQuestVar("SeedBuffsList", String.valueOf(buffsNow));
			seedsNextStatusChange = getNextSeedsStatusChangeTime();
			saveGlobalQuestVar("SeedNextStatusChange", String.valueOf(seedsNextStatusChange));
		}
		else
		{
			seedsNextStatusChange = Long.parseLong(var);
			buffsNow = Integer.parseInt(loadGlobalQuestVar("SeedBuffsList"));
		}
		for (int i = 0; i < regionsData.length; i++)
		{
			regionsData[i].activeBuff = ZONE_BUFFS_LIST[buffsNow][i];
		}
	}

	private Long getNextSeedsStatusChangeTime()
	{
		Calendar reenter = Calendar.getInstance();
		reenter.set(Calendar.SECOND, 0);
		reenter.set(Calendar.MINUTE, 0);
		reenter.set(Calendar.HOUR_OF_DAY, 13);
		reenter.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		if (reenter.getTimeInMillis() <= System.currentTimeMillis())
		{
			reenter.add(Calendar.DAY_OF_MONTH, 7);
		}
		return reenter.getTimeInMillis();
	}

	public SeedOfAnnihilation(int questId, String name, String descr)
	{
		super(questId, name, descr);
		loadSeedRegionData();
		for (int i : teleportZones.keySet())
		{
			addEnterZoneId(i);
		}
		for (SeedRegion element : regionsData)
		{
			for (int elite_mob_id : element.elite_mob_ids)
			{
				addSpawnId(elite_mob_id);
			}
		}
		addStartNpc(32739);
		addTalkId(32739);
		initialMinionsSpawn();
		startEffectZonesControl();
	}

	private void startEffectZonesControl()
	{
		for (int i = 0; i < regionsData.length; i++)
		{
			for (int j = 0; j < regionsData[i].af_spawns.length; j++)
			{
				regionsData[i].af_npcs[j] =
						addSpawn(ANNIHILATION_FURNACE, regionsData[i].af_spawns[j][0], regionsData[i].af_spawns[j][1],
								regionsData[i].af_spawns[j][2], regionsData[i].af_spawns[j][3], false, 0);
				regionsData[i].af_npcs[j].setDisplayEffect(regionsData[i].activeBuff);
			}
			ZoneManager.getInstance().getZoneById(regionsData[i].buff_zone, L2EffectZone.class)
					.addSkill(ZONE_BUFFS[regionsData[i].activeBuff], 1);
		}
		startQuestTimer("ChangeSeedsStatus", seedsNextStatusChange - System.currentTimeMillis(), null, null);
	}

	private void initialMinionsSpawn()
	{
		for (L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
		{
			if (spawn == null)
			{
				continue;
			}
			for (SeedRegion element : regionsData)
			{
				if (Util.contains(element.elite_mob_ids, spawn.getNpcId()))
				{
					L2MonsterInstance mob = (L2MonsterInstance) spawn.getNpc();
					if (mob != null)
					{
						spawnGroupOfMinion(mob, element.minion_lists[Rnd.get(element.minion_lists.length)]);
					}
				}
			}
		}
	}

	private void spawnGroupOfMinion(L2MonsterInstance npc, int[] mobIds)
	{
		for (int mobId : mobIds)
		{
			addMinion(npc, mobId);
		}
	}

	@Override
	public String onSpawn(L2Npc npc)
	{
		for (SeedRegion element : regionsData)
		{
			if (Util.contains(element.elite_mob_ids, npc.getNpcId()))
			{
				spawnGroupOfMinion((L2MonsterInstance) npc, element.minion_lists[Rnd.get(element.minion_lists.length)]);
			}
		}
		return super.onSpawn(npc);
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("ChangeSeedsStatus"))
		{
			int buffsNow = Rnd.get(ZONE_BUFFS_LIST.length);
			saveGlobalQuestVar("SeedBuffsList", String.valueOf(buffsNow));
			seedsNextStatusChange = getNextSeedsStatusChangeTime();
			saveGlobalQuestVar("SeedNextStatusChange", String.valueOf(seedsNextStatusChange));
			for (int i = 0; i < regionsData.length; i++)
			{
				regionsData[i].activeBuff = ZONE_BUFFS_LIST[buffsNow][i];

				for (L2Npc af : regionsData[i].af_npcs)
				{
					af.setDisplayEffect(regionsData[i].activeBuff);
				}

				L2EffectZone zone = ZoneManager.getInstance().getZoneById(regionsData[i].buff_zone, L2EffectZone.class);
				zone.clearSkills();
				zone.addSkill(ZONE_BUFFS[regionsData[i].activeBuff], 1);
			}
			startQuestTimer("ChangeSeedsStatus", seedsNextStatusChange - System.currentTimeMillis(), null, null);
		}
		else if (event.equalsIgnoreCase("transform"))
		{
			if (player.getFirstEffect(6408) != null)
			{
				npc.showChatWindow(player, 2);
			}
			else
			{
				npc.setTarget(player);
				npc.doCast(SkillTable.getInstance().getInfo(6408, 1));
				npc.doCast(SkillTable.getInstance().getInfo(6649, 1));
				npc.showChatWindow(player, 1);
			}
		}
		return null;
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		return super.onKill(npc, killer, isPet);
	}

	@Override
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if (teleportZones.containsKey(zone.getId()))
		{
			int[] teleLoc = teleportZones.get(zone.getId());
			character.teleToLocation(teleLoc[0], teleLoc[1], teleLoc[2]);
		}
		return super.onEnterZone(character, zone);
	}

	public static void main(String[] args)
	{
		new SeedOfAnnihilation(-1, qn, "ai");
	}
}
