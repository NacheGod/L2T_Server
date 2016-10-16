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

package l2server.gameserver.model.zone.type;

import l2server.gameserver.datatables.MapRegionTable;
import l2server.gameserver.instancemanager.CastleManager;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.entity.Castle;
import l2server.gameserver.model.zone.L2SpawnZone;
import lombok.Getter;

/**
 * A castle zone
 *
 * @author durgus
 */
public class L2CastleZone extends L2SpawnZone
{
	@Getter private int castleId;
	private Castle castle = null;

	public L2CastleZone(int id)
	{
		super(id);
	}

	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("castleId"))
		{
			castleId = Integer.parseInt(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}

	@Override
	protected void onEnter(L2Character character)
	{
		if (getCastle() != null)
		{
			character.setInsideZone(L2Character.ZONE_CASTLE, true);
		}
	}

	@Override
	protected void onExit(L2Character character)
	{
		if (getCastle() != null)
		{
			character.setInsideZone(L2Character.ZONE_CASTLE, false);
		}
	}

	@Override
	public void onDieInside(L2Character character, L2Character killer)
	{
	}

	@Override
	public void onReviveInside(L2Character character)
	{
	}

	/**
	 * Removes all foreigners from the castle
	 *
	 * @param owningClanId
	 */
	public void banishForeigners(int owningClanId)
	{
		for (L2Character temp : characterList.values())
		{
			if (!(temp instanceof L2PcInstance))
			{
				continue;
			}

			if (temp.getInstanceId() != 0) //Don't kick players which are at another instance
			{
				continue;
			}

			if (((L2PcInstance) temp).getClanId() == owningClanId)
			{
				continue;
			}

			temp.teleToLocation(MapRegionTable.TeleportWhereType.Town);
		}
	}

	private Castle getCastle()
	{
		if (castle == null)
		{
			castle = CastleManager.getInstance().getCastleById(castleId);
		}
		return castle;
	}
}
