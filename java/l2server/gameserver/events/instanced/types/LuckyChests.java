package l2server.gameserver.events.instanced.types;

import l2server.Config;
import l2server.gameserver.Announcements;
import l2server.gameserver.datatables.NpcTable;
import l2server.gameserver.datatables.SpawnTable;
import l2server.gameserver.events.instanced.EventConfig;
import l2server.gameserver.events.instanced.EventInstance;
import l2server.gameserver.events.instanced.EventTeam;
import l2server.gameserver.events.instanced.EventTeleporter;
import l2server.gameserver.model.L2Spawn;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.clientpackets.Say2;
import l2server.gameserver.network.serverpackets.CreatureSay;
import l2server.gameserver.templates.chars.L2NpcTemplate;
import l2server.log.Log;
import l2server.util.Rnd;

/**
 * @author Pere
 */
public class LuckyChests extends EventInstance
{
	private boolean chestsSpawned = false;
	private L2Spawn[] chestSpawns = new L2Spawn[200];

	public LuckyChests(int id, EventConfig config)
	{
		super(id, config);
	}

	@Override
	public boolean startFight()
	{
		if (!super.startFight())
		{
			return false;
		}

		if (!chestsSpawned)
		{
			spawnChests();
		}

		return true;
	}

	@Override
	public void calculateRewards()
	{
		EventTeam team;
		if (config.getLocation().getTeamCount() != 4)
		{
			if (teams[0].getPoints() == teams[1].getPoints())
			{
				// Check if one of the teams have no more players left
				if (teams[0].getParticipatedPlayerCount() == 0 || teams[1].getParticipatedPlayerCount() == 0)
				{
					// set state to rewarding
					setState(EventState.REWARDING);
					// return here, the fight can't be completed
					Announcements.getInstance().announceToAll("The event has ended. No team won due to inactivity!");
					return;
				}

				// Both teams have equals points
				if (Config.INSTANCED_EVENT_REWARD_TEAM_TIE)
				{
					rewardTeams(-1);
				}
				Announcements.getInstance().announceToAll("The event has ended in a tie");
				return;
			}

			// Set state REWARDING so nobody can point anymore
			setState(EventState.REWARDING);

			// Get team which has more points
			team = teams[teams[0].getPoints() > teams[1].getPoints() ? 0 : 1];

			if (team == teams[0])
			{
				rewardTeams(0);
			}
			else
			{
				rewardTeams(1);
			}
		}
		else
		{
			// Set state REWARDING so nobody can point anymore
			setState(EventState.REWARDING);
			if (teams[0].getPoints() > teams[1].getPoints() && teams[0].getPoints() > teams[2].getPoints() &&
					teams[0].getPoints() > teams[3].getPoints())
			{
				rewardTeams(0);
				team = teams[0];
			}
			else if (teams[1].getPoints() > teams[0].getPoints() && teams[1].getPoints() > teams[2].getPoints() &&
					teams[1].getPoints() > teams[3].getPoints())
			{
				rewardTeams(1);
				team = teams[1];
			}
			else if (teams[2].getPoints() > teams[0].getPoints() && teams[2].getPoints() > teams[1].getPoints() &&
					teams[2].getPoints() > teams[3].getPoints())
			{
				rewardTeams(2);
				team = teams[2];
			}
			else if (teams[3].getPoints() > teams[0].getPoints() && teams[3].getPoints() > teams[1].getPoints() &&
					teams[3].getPoints() > teams[2].getPoints())
			{
				rewardTeams(3);
				team = teams[3];
			}
			else
			{
				Announcements.getInstance().announceToAll("The event has ended in a tie");
				return;
			}
		}

		Announcements.getInstance().announceToAll(
				"The event has ended. Team " + team.getName() + " won with " + team.getPoints() + " points.");
	}

	@Override
	public void stopFight()
	{
		super.stopFight();
		unspawnChests();
	}

	@Override
	public String getRunningInfo(L2PcInstance player)
	{
		String html = "";
		for (EventTeam team : teams)
		{
			if (team.getParticipatedPlayerCount() > 0)
			{
				html += "Team " + team.getName() + " points: " + team.getPoints() + "<br>";
			}
		}
		if (html.length() > 4)
		{
			html = html.substring(0, html.length() - 4);
		}
		return html;
	}

	public void chestPoints(L2PcInstance playerInstance, int points)
	{
		EventTeam team = getParticipantTeam(playerInstance.getObjectId());
		if (!isState(EventState.STARTED) || team == null)
		{
			return;
		}

		CreatureSay cs = null;
		if (points == 1)
		{
			playerInstance.addEventPoints(1);
			team.increasePoints();
			cs = new CreatureSay(playerInstance.getObjectId(), Say2.TELL, playerInstance.getName(),
					"I have opened a chest that contained 1 point.");
		}
		else if (points == 5)
		{
			playerInstance.addEventPoints(5);
			for (int i = 0; i < 5; i++)
			{
				team.increasePoints();
			}
			cs = new CreatureSay(playerInstance.getObjectId(), Say2.TELL, playerInstance.getName(),
					"I have opened a chest that contained 5 points!");
		}
		else if (points == 20)
		{
			playerInstance.addEventPoints(20);
			for (int i = 0; i < 20; i++)
			{
				team.increasePoints();
			}
			cs = new CreatureSay(playerInstance.getObjectId(), Say2.TELL, playerInstance.getName(),
					"I have opened a chest that contained 20 points!!!");
		}
		for (L2PcInstance character : team.getParticipatedPlayers().values())
		{
			if (character != null)
			{
				character.sendPacket(cs);
			}
		}
	}

	@Override
	public void onKill(L2Character killerCharacter, L2PcInstance killedPlayerInstance)
	{
		if (killedPlayerInstance == null || !isState(EventState.STARTED))
		{
			return;
		}

		byte killedTeamId = getParticipantTeamId(killedPlayerInstance.getObjectId());

		if (killedTeamId == -1)
		{
			return;
		}

		new EventTeleporter(killedPlayerInstance, teams[killedTeamId].getCoords(), false, false);
	}

	private void spawnChests()
	{
		L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(44000);

		try
		{
			int chestAmount;
			if (getId() == 100)
			{
				chestAmount = 200;
			}
			else if (config.getLocation().getTeamCount() == 4)
			{
				chestAmount = teams[0].getParticipatedPlayerCount() * 10;
			}
			else
			{
				chestAmount = teams[0].getParticipatedPlayerCount() * 5;
			}
			int i;
			for (i = 0; i < chestAmount && i < 200; i++)
			{
				chestSpawns[i] = new L2Spawn(tmpl);

				int[] pos = config.getLocation().getZone().getZone().getRandomPoint();
				chestSpawns[i].setX(pos[0]);
				chestSpawns[i].setY(pos[1]);
				chestSpawns[i].setZ(pos[2]);
				chestSpawns[i].setHeading(Rnd.get(65536));
				chestSpawns[i].setRespawnDelay(10);
				chestSpawns[i].setInstanceId(getInstanceId());

				SpawnTable.getInstance().addNewSpawn(chestSpawns[i], false);

				chestSpawns[i].stopRespawn();
				chestSpawns[i].doSpawn();
			}
			chestsSpawned = true;
		}
		catch (Exception e)
		{
			Log.warning("Chest event exception (" + config.getLocation().getName() + "):");
			e.printStackTrace();
		}
	}

	private void unspawnChests()
	{
		int i;
		for (i = 0; i < 200; i++)
		{
			if (chestSpawns[i] != null)
			{
				chestSpawns[i].getNpc().deleteMe();
				chestSpawns[i].stopRespawn();
				SpawnTable.getInstance().deleteSpawn(chestSpawns[i], false);
			}
		}
		chestsSpawned = false;
	}

	class UnspawnChestsTask implements Runnable
	{
		@Override
		@SuppressWarnings("synthetic-access")
		public void run()
		{
			unspawnChests();
		}
	}
}
