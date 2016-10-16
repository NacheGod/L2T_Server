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

import l2server.gameserver.model.L2ItemInstance;

/**
 * sample
 * 0000: 17  1a 95 20 48  9b da 12 40  44 17 02 00  03 f0 fc ff  98 f1 ff ff									 .....
 * <p>
 * format  ddddd
 *
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public final class GetItem extends L2GameServerPacket
{
	private L2ItemInstance item;
	private int playerId;

	public GetItem(L2ItemInstance item, int playerId)
	{
		this.item = item;
		this.playerId = playerId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(this.playerId);
		writeD(this.item.getObjectId());

		writeD(this.item.getX());
		writeD(this.item.getY());
		writeD(this.item.getZ());
	}
}
