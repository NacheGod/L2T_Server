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

package l2server.gameserver.network.clientpackets;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.StopMoveInVehicle;
import l2server.util.Point3D;

/**
 * @author Maktakien
 */
public final class CannotMoveAnymoreInVehicle extends L2GameClientPacket
{
	private int x;
	private int y;
	private int z;
	private int heading;
	private int boatId;

	@Override
	protected void readImpl()
	{
		boatId = readD();
		x = readD();
		y = readD();
		z = readD();
		heading = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		if (player.isInBoat())
		{
			if (player.getBoat().getObjectId() == boatId)
			{
				player.setInVehiclePosition(new Point3D(x, y, z));
				player.getPosition().setHeading(heading);
				StopMoveInVehicle msg = new StopMoveInVehicle(player, boatId);
				player.broadcastPacket(msg);
			}
		}
	}
}
