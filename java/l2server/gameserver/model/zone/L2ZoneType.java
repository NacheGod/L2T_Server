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

package l2server.gameserver.model.zone;

import l2server.gameserver.ThreadPoolManager;
import l2server.gameserver.ai.CtrlIntention;
import l2server.gameserver.datatables.MapRegionTable.TeleportWhereType;
import l2server.gameserver.datatables.ScenePlayerDataTable;
import l2server.gameserver.model.L2Object;
import l2server.gameserver.model.L2Object.InstanceType;
import l2server.gameserver.model.actor.L2Attackable;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.L2Playable;
import l2server.gameserver.model.actor.instance.L2BoatInstance;
import l2server.gameserver.model.actor.instance.L2NpcInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.quest.Quest;
import l2server.gameserver.network.serverpackets.ExServerPrimitive;
import l2server.gameserver.network.serverpackets.ExStartScenePlayer;
import l2server.gameserver.network.serverpackets.L2GameServerPacket;
import l2server.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base class for any zone type
 * Handles basic operations
 *
 * @author durgus
 */
public abstract class L2ZoneType
{
	private final int id;
	protected L2ZoneForm zone;
	protected ConcurrentHashMap<Integer, L2Character> characterList;

	/**
	 * Parameters to affect specific characters
	 */
	private boolean checkAffected = false;

	private String name = null;
	private int minLvl;
	private int maxLvl;
	private int[] race;
	private int[] clazz;
	private char classType;
	private Map<Quest.QuestEventType, ArrayList<Quest>> questEvents;
	private InstanceType target = InstanceType.L2Character; // default all chars

	protected L2ZoneType(int id)
	{
		this.id = id;
		this.characterList = new ConcurrentHashMap<>();

		this.minLvl = 0;
		this.maxLvl = 0xFF;

		this.classType = 0;

		this.race = null;
		this.clazz = null;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return this.id;
	}

	/**
	 * Setup new parameters for this zone
	 *
	 * @param value
	 */
	public void setParameter(String name, String value)
	{
		this.checkAffected = true;

		// Zone name
		switch (name)
		{
			case "name":
				this.name = value;
				break;
			// Minimum level
			case "affectedLvlMin":
				this.minLvl = Integer.parseInt(value);
				break;
			// Maximum level
			case "affectedLvlMax":
				this.maxLvl = Integer.parseInt(value);
				break;
			// Affected Races
			case "affectedRace":
				// Create a new array holding the affected race
				if (this.race == null)
				{
					this.race = new int[1];
					this.race[0] = Integer.parseInt(value);
				}
				else
				{
					int[] temp = new int[this.race.length + 1];

					int i = 0;
					for (; i < this.race.length; i++)
					{
						temp[i] = this.race[i];
					}

					temp[i] = Integer.parseInt(value);

					this.race = temp;
				}
				break;
			// Affected classes
			case "affectedClassId":
				// Create a new array holding the affected classIds
				if (this.clazz == null)
				{
					this.clazz = new int[1];
					this.clazz[0] = Integer.parseInt(value);
				}
				else
				{
					int[] temp = new int[this.clazz.length + 1];

					int i = 0;
					for (; i < this.clazz.length; i++)
					{
						temp[i] = this.clazz[i];
					}

					temp[i] = Integer.parseInt(value);

					this.clazz = temp;
				}
				break;
			// Affected class type
			case "affectedClassType":
				if (value.equals("Fighter"))
				{
					this.classType = 1;
				}
				else
				{
					this.classType = 2;
				}
				break;
			case "targetClass":
				this.target = Enum.valueOf(InstanceType.class, value);
				break;
			default:
				Log.info(getClass().getSimpleName() + ": Unknown parameter - " + name + " in zone: " + getId());
				break;
		}
	}

	/**
	 * Checks if the given character is affected by this zone
	 *
	 * @param character
	 * @return
	 */
	private boolean isAffected(L2Character character)
	{
		// Check lvl
		if (character.getLevel() < this.minLvl || character.getLevel() > maxLvl)
		{
			return false;
		}

		// check obj class
		if (!character.isInstanceType(this.target))
		{
			return false;
		}

		if (character instanceof L2PcInstance)
		{
			// Check class type
			if (this.classType != 0)
			{
				if (((L2PcInstance) character).isMageClass())
				{
					if (this.classType == 1)
					{
						return false;
					}
				}
				else if (this.classType == 2)
				{
					return false;
				}
			}

			// Check race
			if (this.race != null)
			{
				boolean ok = false;

				for (int element : this.race)
				{
					if (((L2PcInstance) character).getRace().ordinal() == element)
					{
						ok = true;
						break;
					}
				}

				if (!ok)
				{
					return false;
				}
			}

			// Check class
			if (this.clazz != null)
			{
				boolean ok = false;

				for (int clas : this.clazz)
				{
					if (((L2PcInstance) character).getCurrentClass().getId() == clas)
					{
						ok = true;
						break;
					}
				}

				if (!ok)
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Set the zone for this L2ZoneType Instance
	 *
	 * @param zone
	 */
	public void setZone(L2ZoneForm zone)
	{
		if (this.zone != null)
		{
			throw new IllegalStateException("Zone already set");
		}
		this.zone = zone;
	}

	/**
	 * Returns this zones zone form
	 *
	 * @return
	 */
	public L2ZoneForm getZone()
	{
		return this.zone;
	}

	/**
	 * Set the zone name.
	 *
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns zone name
	 *
	 * @return
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Checks if the given coordinates are within zone's plane
	 *
	 * @param x
	 * @param y
	 */
	public boolean isInsideZone(int x, int y)
	{
		return this.zone.isInsideZone(x, y, this.zone.getHighZ());
	}

	/**
	 * Checks if the given coordinates are within the zone
	 *
	 * @param x
	 * @param y
	 * @param z
	 */
	public boolean isInsideZone(int x, int y, int z)
	{
		return this.zone.isInsideZone(x, y, z);
	}

	/**
	 * Checks if the given object is inside the zone.
	 *
	 * @param object
	 */
	public boolean isInsideZone(L2Object object)
	{
		return isInsideZone(object.getX(), object.getY(), object.getZ());
	}

	public double getDistanceToZone(int x, int y)
	{
		return getZone().getDistanceToZone(x, y);
	}

	public double getDistanceToZone(L2Object object)
	{
		return getZone().getDistanceToZone(object.getX(), object.getY());
	}

	public void revalidateInZone(L2Character character)
	{
		// If the character can't be affected by this zone return
		if (this.checkAffected)
		{
			if (!isAffected(character))
			{
				return;
			}
		}

		// If the object is inside the zone...
		if (isInsideZone(character.getX(), character.getY(), character.getZ()))
		{
			// Was the character not yet inside this zone?
			if (!this.characterList.containsKey(character.getObjectId()))
			{
				ArrayList<Quest> quests = getQuestByEvent(Quest.QuestEventType.ON_ENTER_ZONE);
				if (quests != null)
				{
					for (Quest quest : quests)
					{
						quest.notifyEnterZone(character, this);
					}
				}

				this.characterList.put(character.getObjectId(), character);
				onEnter(character);
			}
		}
		else
		{
			// Was the character inside this zone?
			if (this.characterList.containsKey(character.getObjectId()))
			{
				ArrayList<Quest> quests = getQuestByEvent(Quest.QuestEventType.ON_EXIT_ZONE);
				if (quests != null)
				{
					for (Quest quest : quests)
					{
						quest.notifyExitZone(character, this);
					}
				}
				this.characterList.remove(character.getObjectId());
				onExit(character);
			}
		}
	}

	/**
	 * Force fully removes a character from the zone
	 * Should use during teleport / logoff
	 *
	 * @param character
	 */
	public void removeCharacter(L2Character character)
	{
		if (this.characterList.containsKey(character.getObjectId()))
		{
			ArrayList<Quest> quests = getQuestByEvent(Quest.QuestEventType.ON_EXIT_ZONE);
			if (quests != null)
			{
				for (Quest quest : quests)
				{
					quest.notifyExitZone(character, this);
				}
			}
			this.characterList.remove(character.getObjectId());
			onExit(character);
		}
	}

	/**
	 * Will scan the zones char list for the character
	 *
	 * @param character
	 * @return
	 */
	public boolean isCharacterInZone(L2Character character)
	{
		return this.characterList.containsKey(character.getObjectId());
	}

	protected abstract void onEnter(L2Character character);

	protected abstract void onExit(L2Character character);

	public void onDieInside(L2Character character, L2Character killer)
	{
		if (this.characterList.containsKey(character.getObjectId()))
		{
			ArrayList<Quest> quests = getQuestByEvent(Quest.QuestEventType.ON_DIE_ZONE);
			if (quests != null)
			{
				for (Quest quest : quests)
				{
					quest.notifyDieZone(character, killer, this);
				}
			}
		}
	}

	public abstract void onReviveInside(L2Character character);

	public ConcurrentHashMap<Integer, L2Character> getCharactersInside()
	{
		return this.characterList;
	}

	public void addQuestEvent(Quest.QuestEventType EventType, Quest q)
	{
		if (this.questEvents == null)
		{
			this.questEvents = new HashMap<>();
		}
		ArrayList<Quest> questByEvents = this.questEvents.get(EventType);
		if (questByEvents == null)
		{
			questByEvents = new ArrayList<>();
		}
		if (!questByEvents.contains(q))
		{
			questByEvents.add(q);
		}
		this.questEvents.put(EventType, questByEvents);
	}

	public ArrayList<Quest> getQuestByEvent(Quest.QuestEventType EventType)
	{
		if (this.questEvents == null)
		{
			return null;
		}
		return this.questEvents.get(EventType);
	}

	/**
	 * Broadcasts packet to all players inside the zone
	 */
	public void broadcastPacket(L2GameServerPacket packet)
	{
		if (this.characterList.isEmpty())
		{
			return;
		}

		for (L2Character character : this.characterList.values())
		{
			if (character != null && character instanceof L2PcInstance)
			{
				character.sendPacket(packet);
			}
		}
	}

	public InstanceType getTargetType()
	{
		return this.target;
	}

	public void setTargetType(InstanceType type)
	{
		this.target = type;
		this.checkAffected = true;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " [" + this.id + "]";
	}

	public void visualizeZone(L2PcInstance viewer)
	{
		ExServerPrimitive packet = new ExServerPrimitive(toString());
		getZone().visualizeZone(packet, toString(), viewer.getZ() + 20);
		viewer.sendPacket(packet);
	}

	public void unVisualizeZone(L2PcInstance viewer)
	{
		viewer.sendPacket(new ExServerPrimitive(toString()));
	}

	public List<L2PcInstance> getPlayersInside()
	{
		List<L2PcInstance> players = new ArrayList<>();
		for (L2Character ch : this.characterList.values())
		{
			if (ch != null && ch instanceof L2PcInstance)
			{
				players.add(ch.getActingPlayer());
			}
		}

		return players;
	}

	public List<L2Npc> getNpcsInside()
	{
		List<L2Npc> npcs = new ArrayList<>();
		for (L2Character ch : this.characterList.values())
		{
			if (ch == null || ch instanceof L2Playable || ch instanceof L2BoatInstance ||
					!(ch instanceof L2Attackable) && !(ch instanceof L2NpcInstance))
			{
				continue;
			}

			npcs.add((L2Npc) ch);
		}

		return npcs;
	}

	public void showVidToZone(int vidId)
	{
		stopWholeZone();

		broadcastMovie(vidId);

		ThreadPoolManager.getInstance().scheduleGeneral(this::startWholeZone,
				ScenePlayerDataTable.getInstance().getVideoDuration(vidId) + 1000);
	}

	public void stopWholeZone()
	{
		for (L2Character ch : this.characterList.values())
		{
			if (ch == null)
			{
				continue;
			}

			ch.setTarget(null);
			ch.abortAttack();
			ch.abortCast();
			ch.disableAllSkills();
			ch.stopMove(null);
			ch.setIsInvul(true);
			ch.setIsImmobilized(true);
			ch.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		}
	}

	public void broadcastMovie(int vidId)
	{
		for (L2PcInstance pl : getPlayersInside())
		{
			if (pl == null)
			{
				continue;
			}

			pl.setMovieId(vidId);
			pl.sendPacket(new ExStartScenePlayer(vidId));
		}
	}

	public void startWholeZone()
	{
		for (L2Character ch : this.characterList.values())
		{
			if (ch == null)
			{
				continue;
			}

			ch.enableAllSkills();
			ch.setIsInvul(false);
			ch.setIsImmobilized(false);
		}
	}

	public void sendDelayedPacketToZone(final int delayMsSec, final L2GameServerPacket packet)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(() -> broadcastPacket(packet), delayMsSec);
	}

	public void oustAllPlayers()
	{
		if (this.characterList.isEmpty())
		{
			return;
		}

		for (L2Character character : this.characterList.values())
		{
			if (character == null)
			{
				continue;
			}

			if (character instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) character;
				if (player.isOnline())
				{
					player.teleToLocation(TeleportWhereType.Town);
				}
			}
		}
	}
}
