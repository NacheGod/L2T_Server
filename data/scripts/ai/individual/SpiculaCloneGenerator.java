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

package ai.individual;

import java.util.HashMap;
import java.util.Map;

import ai.group_template.L2AttackableAIScript;
import l2server.gameserver.ai.CtrlIntention;
import l2server.gameserver.datatables.SpawnTable;
import l2server.gameserver.model.L2Skill;
import l2server.gameserver.model.L2Spawn;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2MonsterInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author LasTravel
 *         <p>
 *         Spicula Clone Generator AI
 */

public class SpiculaCloneGenerator extends L2AttackableAIScript
{
	private static final int yin = 19320;
	private static final int yinFragment = 19308;
	private static final int spiculaElite = 23303;
	private static Map<Integer, Long> yinControl = new HashMap<Integer, Long>();

	public SpiculaCloneGenerator(int id, String name, String descr)
	{
		super(id, name, descr);

		addKillId(this.yinFragment);
		addAttackId(this.yin);
		addSpawnId(this.yin);
		addSpawnId(this.yinFragment);

		for (L2Spawn spawn : SpawnTable.getInstance().getSpawnTable())
		{
			if (spawn == null)
			{
				continue;
			}

			if (spawn.getNpcId() == this.yin || spawn.getNpcId() == this.yinFragment)
			{
				notifySpawn(spawn.getNpc());
			}
		}
	}

	@Override
	public final String onSpawn(L2Npc npc)
	{
		if (npc.getNpcId() == this.yin)
		{
			npc.setIsInvul(true);
		}

		npc.setIsImmobilized(true);

		return super.onSpawn(npc);
	}

	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet, L2Skill skill)
	{
		if (this.yinControl.containsKey(npc.getObjectId()))
		{
			if (System.currentTimeMillis() >= this.yinControl.get(npc.getObjectId()) + 180000)
			{
				this.yinControl.put(npc.getObjectId(), System.currentTimeMillis());

				spawnSpiculas(npc, attacker);
			}
		}
		else
		{
			this.yinControl.put(npc.getObjectId(), System.currentTimeMillis());

			spawnSpiculas(npc, attacker);
		}

		return super.onAttack(npc, attacker, damage, isPet, skill);
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		spawnSpiculas(npc, killer);

		return super.onKill(npc, killer, isPet);
	}

	private void spawnSpiculas(L2Npc npc, L2PcInstance killer)
	{
		npc.broadcastPacket(new ExShowScreenMessage(
				"$s1 has summoned Elite Soldiers through the Clone Generator.".replace("$s1", killer.getName()),
				3000)); //id: 1802277

		for (int a = 0; a <= (npc.getNpcId() == this.yinFragment ? 2 : 4); a++)
		{
			L2Npc minion = addSpawn(this.spiculaElite, killer.getX(), killer.getY(), killer.getZ(), 0, true, 180000, true);

			minion.setIsRunning(true);

			minion.setTarget(killer);

			((L2MonsterInstance) minion).addDamageHate(killer, 500, 99999);

			minion.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, killer);
		}
	}

	@Override
	public int getOnKillDelay(int npcId)
	{
		return 0;
	}

	public static void main(String[] args)
	{
		new SpiculaCloneGenerator(-1, "SpiculaCloneGenerator", "ai");
	}
}
