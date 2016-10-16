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

import l2server.gameserver.model.TradeList;
import l2server.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 *
 * @version $Revision: 1.7.2.2.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public class PrivateStoreListBuy extends L2ItemListPacket
{
	private int objId;
	private long playerAdena;
	private TradeList.TradeItem[] items;

	public PrivateStoreListBuy(L2PcInstance player, L2PcInstance storePlayer)
	{
		this.objId = storePlayer.getObjectId();
		this.playerAdena = player.getAdena();
		storePlayer.getSellList().updateItems(); // Update SellList for case inventory content has changed
		this.items = storePlayer.getBuyList().getAvailableItems(player.getInventory());
	}

	@Override
	protected final void writeImpl()
	{
		writeD(this.objId);
		writeQ(this.playerAdena);
		writeD(0x00); // GoD ???

		writeD(this.items.length);

		for (TradeList.TradeItem item : this.items)
		{
			writeItem(item);

			writeD(item.getObjectId());
			writeQ(item.getPrice());
			writeQ(item.getItem().getReferencePrice() * 2);
			writeQ(item.getStoreCount());
		}
	}
}
