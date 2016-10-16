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

package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.util.Point3D;

/**
 * This class ...
 *
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class ValidateLocationInVehicle extends L2GameServerPacket
{

	private int charObjId;
	private int boatObjId;
	private int heading;
	private Point3D pos;

	/**
	 * 0x80 ValidateLocationInVehicle		 hdd
	 */
	public ValidateLocationInVehicle(L2PcInstance player)
	{
		this.charObjId = player.getObjectId();
		this.boatObjId = player.getBoat().getObjectId();
		this.heading = player.getHeading();
		this.pos = player.getInVehiclePosition();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(this.charObjId);
		writeD(this.boatObjId);
		writeD(this.pos.getX());
		writeD(this.pos.getY());
		writeD(this.pos.getZ());
		writeD(this.heading);
	}
}
