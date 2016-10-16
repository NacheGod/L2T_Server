package l2server.gameserver.events.instanced.types;

import l2server.Config;
import l2server.gameserver.Announcements;
import l2server.gameserver.datatables.NpcTable;
import l2server.gameserver.datatables.SpawnTable;
import l2server.gameserver.events.instanced.EventConfig;
import l2server.gameserver.events.instanced.EventInstance;
import l2server.gameserver.events.instanced.EventTeam;
import l2server.gameserver.events.instanced.EventTeleporter;
import l2server.gameserver.instancemanager.PlayerAssistsManager;
import l2server.gameserver.model.L2Spawn;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.instance.L2EventFlagInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.templates.chars.L2NpcTemplate;
import l2server.log.Log;

import java.util.List;

/**
 * @author Pere
 */
public class FieldDomination extends EventInstance
{

	private boolean FDflagsSpawned = false;
	private L2Spawn[] FDFlagSpawns = new L2Spawn[5];

	public FieldDomination(int id, EventConfig config)
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

		if (!this.FDflagsSpawned)
		{
			spawnFlags();
		}

		return true;
	}

	@Override
	public void calculateRewards()
	{
		for (int i = 0; i < 5; i++)
		{
			if (this.FDFlagSpawns[i] != null && ((L2EventFlagInstance) this.FDFlagSpawns[i].getNpc()).getTeam() != null)
			{
				((L2EventFlagInstance) this.FDFlagSpawns[i].getNpc()).getTeam().increasePoints();
			}
		}
		EventTeam team;
		if (this.config.getLocation().getTeamCount() != 4)
		{
			if (this.teams[0].getPoints() == this.teams[1].getPoints())
			{
				// Check if one of the teams have no more players left
				if (this.teams[0].getParticipatedPlayerCount() == 0 || this.teams[1].getParticipatedPlayerCount() == 0)
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
			team = this.teams[this.teams[0].getPoints() > this.teams[1].getPoints() ? 0 : 1];

			if (team == this.teams[0])
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
			if (this.teams[0].getPoints() > this.teams[1].getPoints() && this.teams[0].getPoints() > this.teams[2].getPoints() &&
					this.teams[0].getPoints() > this.teams[3].getPoints())
			{
				rewardTeams(0);
				team = this.teams[0];
			}
			else if (this.teams[1].getPoints() > this.teams[0].getPoints() && this.teams[1].getPoints() > this.teams[2].getPoints() &&
					this.teams[1].getPoints() > this.teams[3].getPoints())
			{
				rewardTeams(1);
				team = this.teams[1];
			}
			else if (this.teams[2].getPoints() > this.teams[0].getPoints() && this.teams[2].getPoints() > this.teams[1].getPoints() &&
					this.teams[2].getPoints() > this.teams[3].getPoints())
			{
				rewardTeams(2);
				team = this.teams[2];
			}
			else if (this.teams[3].getPoints() > this.teams[0].getPoints() && this.teams[3].getPoints() > this.teams[1].getPoints() &&
					this.teams[3].getPoints() > this.teams[2].getPoints())
			{
				rewardTeams(3);
				team = this.teams[3];
			}
			else
			{
				Announcements.getInstance().announceToAll("The event has ended in a tie");
				return;
			}
		}
		if (team.getPoints() > config.getLocation().getTeamCount())
		{
			Announcements.getInstance()
					.announceToAll("The event has ended. Team " + team.getName() + " won by owning all the flags.");
		}
		else
		{
			Announcements.getInstance().announceToAll(
					"The event has ended. Team " + team.getName() + " won with " + team.getPoints() + " points");
		}

	}

	@Override
	public void stopFight()
	{
		super.stopFight();
		unspawnFlags();
	}

	@Override
	public String getRunningInfo(L2PcInstance player)
	{
		String html = "";
		L2EventFlagInstance flag;
		String flagStatus;
		for (int i = 0; i < 5; i++)
		{
			if (this.FDFlagSpawns[i] != null && this.FDFlagSpawns[i].getNpc() != null)
			{
				flag = (L2EventFlagInstance) this.FDFlagSpawns[i].getNpc();
				if (flag.getTeam() == null)
				{
					flagStatus = "Neutral";
				}
				else
				{
					flagStatus = "Owned by the " + flag.getTeam().getName() + " team";
				}
				int id = i + 1;
				if (id == 5)
				{
					id = this.config.getLocation().getTeamCount() + 1;
				}
				html += "Flag " + id + " status: " + flagStatus + ".<br>";
			}
		}
		if (html.length() > 4)
		{
			html = html.substring(0, html.length() - 4);
		}
		return html;
	}

	@Override
	public void onKill(L2Character killerCharacter, L2PcInstance killedPlayer)
	{
		if (killedPlayer == null || !isState(EventState.STARTED))
		{
			return;
		}

		byte killedTeamId = getParticipantTeamId(killedPlayer.getObjectId());
		if (killedTeamId == -1)
		{
			return;
		}

		L2PcInstance killerPlayer = killerCharacter.getActingPlayer();
		if (killerPlayer == null)
		{
			return;
		}

		killerPlayer.addEventPoints(3);
		List<L2PcInstance> assistants =
				PlayerAssistsManager.getInstance().getAssistants(killerPlayer, killedPlayer, true);
		for (L2PcInstance assistant : assistants)
		{
			assistant.addEventPoints(1);
		}

		new EventTeleporter(killedPlayer, this.teams[killedTeamId].getCoords(), false, false);
	}

	private void spawnFlags()
	{
		spawnNeutralFlag(this.teams[0]);
		spawnNeutralFlag(this.teams[1]);
		if (this.config.getLocation().getTeamCount() == 4)
		{
			spawnNeutralFlag(this.teams[2]);
			spawnNeutralFlag(this.teams[3]);
		}
		spawnNeutralFlag(null);
		this.FDflagsSpawned = true;
	}

	private void unspawnFlags()
	{
		for (int i = 0; i < 5; i++)
		{
			if (this.FDFlagSpawns[i] != null)
			{
				if (this.FDFlagSpawns[i].getNpc() != null)
				{
					((L2EventFlagInstance) this.FDFlagSpawns[i].getNpc()).shouldBeDeleted();
					this.FDFlagSpawns[i].getNpc().deleteMe();
				}
				this.FDFlagSpawns[i].stopRespawn();
				SpawnTable.getInstance().deleteSpawn(this.FDFlagSpawns[i], true);
			}
		}
		this.FDflagsSpawned = false;
	}

	public void spawnNeutralFlag(EventTeam team)
	{
		L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(44008);

		try
		{
			int x = 0;
			int y = 0;
			for (int i = 0; i < this.config.getLocation().getTeamCount(); i++)
			{
				x += this.teams[i].getCoords().getX();
				y += this.teams[i].getCoords().getY();
			}
			x /= this.config.getLocation().getTeamCount();
			y /= this.config.getLocation().getTeamCount();

			int heading = 0;

			L2Spawn flagSpawn = new L2Spawn(tmpl);

			if (team == null)
			{
				flagSpawn.setX(x);
				flagSpawn.setY(y);
				flagSpawn.setZ(this.config.getLocation().getGlobalZ());
			}
			else
			{
				heading = (int) Math
						.round(Math.atan2(y - team.getCoords().getY(), x - team.getCoords().getX()) / Math.PI * 32768);
				if (heading < 0)
				{
					heading = 65535 + heading;
				}

				flagSpawn.setX(team.getCoords().getX());
				flagSpawn.setY(team.getCoords().getY());
				flagSpawn.setZ(team.getCoords().getZ());
			}

			flagSpawn.setHeading(heading);
			flagSpawn.setInstanceId(getInstanceId());

			SpawnTable.getInstance().addNewSpawn(flagSpawn, false);

			flagSpawn.stopRespawn();
			flagSpawn.doSpawn();
			if (team == null)
			{
				this.FDFlagSpawns[4] = flagSpawn;
			}
			else
			{
				this.FDFlagSpawns[team.getFlagId() - 44004] = flagSpawn;
			}

			L2EventFlagInstance flag = (L2EventFlagInstance) flagSpawn.getNpc();
			flag.setEvent(this);
			flag.setTeam(null);
			flag.setTitle("Neutral");
			flag.updateAbnormalEffect();
		}
		catch (Exception e)
		{
			Log.warning("Field Domination exception:");
			e.printStackTrace();
		}
	}

	public void convertFlag(L2EventFlagInstance flag, EventTeam team, L2PcInstance player)
	{
		int flagId = team == null ? 44008 : team.getFlagId();
		L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(flagId);

		try
		{
			int x = 0;
			int y = 0;
			for (int i = 0; i < this.config.getLocation().getTeamCount(); i++)
			{
				x += this.teams[i].getCoords().getX();
				y += this.teams[i].getCoords().getY();
			}
			x /= this.config.getLocation().getTeamCount();
			y /= this.config.getLocation().getTeamCount();

			int heading = 0;
			if (!(flag.getX() == x && flag.getY() == y))
			{
				heading = (int) Math.round(Math.atan2(y - flag.getY(), x - flag.getX()) / Math.PI * 32768);
				if (heading < 0)
				{
					heading = 65535 + heading;
				}
			}

			L2Spawn flagSpawn = new L2Spawn(tmpl);

			flagSpawn.setX(flag.getX());
			flagSpawn.setY(flag.getY());
			flagSpawn.setZ(flag.getZ());
			flagSpawn.setHeading(heading);
			flagSpawn.setInstanceId(getInstanceId());

			SpawnTable.getInstance().addNewSpawn(flagSpawn, false);

			flagSpawn.stopRespawn();
			flagSpawn.doSpawn();

			L2EventFlagInstance newFlag = (L2EventFlagInstance) flagSpawn.getNpc();
			newFlag.setEvent(this);
			newFlag.setTeam(team);

			if (team == null)
			{
				newFlag.setTitle("Neutral");
				sendToAllParticipants(
						getParticipantTeam(player.getObjectId()).getName() + " team's member " + player.getName() +
								" has neutralized a flag that was taken by the " + flag.getTeam().getName() + " team!");
			}
			else
			{
				newFlag.setTitle(team.getName());
				sendToAllParticipants(team.getName() + " team's member " + player.getName() + " has taken a flag!");
			}

			player.addEventPoints(20);

			newFlag.updateAbnormalEffect();

			boolean allFlagsOwned = true;
			for (int i = 0; i < 5; i++)
			{
				if (this.FDFlagSpawns[i] != null)
				{
					if (this.FDFlagSpawns[i].getX() == flagSpawn.getX() && this.FDFlagSpawns[i].getY() == flagSpawn.getY())
					{
						this.FDFlagSpawns[i] = flagSpawn;
					}
					if (((L2EventFlagInstance) this.FDFlagSpawns[i].getNpc()).getTeam() == null || team == null ||
							((L2EventFlagInstance) this.FDFlagSpawns[i].getNpc()).getTeam().getFlagId() != team.getFlagId())
					{
						allFlagsOwned = false;
					}
				}
			}

			if (allFlagsOwned)
			{
				stopFight();
			}

			flag.shouldBeDeleted();
			flag.deleteMe();
			flag.getSpawn().stopRespawn();
			SpawnTable.getInstance().deleteSpawn(flag.getSpawn(), false);
		}
		catch (Exception e)
		{
			Log.warning("Field Domination exception:");
			e.printStackTrace();
		}
	}
}
