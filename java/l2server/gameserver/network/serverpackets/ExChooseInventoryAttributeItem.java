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

import l2server.gameserver.model.Elementals;
import l2server.gameserver.model.L2ItemInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;

import java.util.ArrayList;

/**
 * @author Kerberos
 */
public class ExChooseInventoryAttributeItem extends L2GameServerPacket
{
	private int itemId;
	private ArrayList<L2ItemInstance> inventoryItems;
	private byte attribute;
	private int level;
	private long maxCount;

	public ExChooseInventoryAttributeItem(L2PcInstance player, L2ItemInstance item)
	{
		inventoryItems = new ArrayList<>();
		itemId = item.getItemId();
		for (L2ItemInstance invItem : player.getInventory().getItems())
		{
			if (invItem.isEquipable())
			{
				inventoryItems.add(invItem);
			}
		}
		attribute = Elementals.getItemElement(itemId);
		if (attribute == Elementals.NONE)
		{
			throw new IllegalArgumentException("Undefined Atribute item: " + item);
		}
		level = Elementals.getMaxElementLevel(itemId);

		// Armors have the opposite element
		if (item.isArmor())
		{
			attribute = Elementals.getOppositeElement(attribute);
		}

		maxCount = item.getCount();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(itemId);
		writeQ(maxCount); // Maximum items that can be attempted to use
		// Must be 0x01 for stone/crystal attribute type
		writeD(attribute == Elementals.FIRE ? 1 : 0); // Fire
		writeD(attribute == Elementals.WATER ? 1 : 0); // Water
		writeD(attribute == Elementals.WIND ? 1 : 0); // Wind
		writeD(attribute == Elementals.EARTH ? 1 : 0); // Earth
		writeD(attribute == Elementals.HOLY ? 1 : 0); // Holy
		writeD(attribute == Elementals.DARK ? 1 : 0); // Unholy
		writeD(level); // Item max attribute level
		writeD(inventoryItems.size()); //equipable items count
		for (L2ItemInstance item : inventoryItems)
		{
			writeD(item.getObjectId());
		}
	}
}
