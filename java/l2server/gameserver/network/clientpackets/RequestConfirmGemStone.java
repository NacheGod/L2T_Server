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

import l2server.gameserver.datatables.LifeStoneTable;
import l2server.gameserver.datatables.LifeStoneTable.LifeStone;
import l2server.gameserver.model.L2ItemInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.ExPutCommissionResultForVariationMake;
import l2server.gameserver.network.serverpackets.SystemMessage;

/**
 * Format:(ch) dddd
 *
 * @author -Wooden-
 */
public final class RequestConfirmGemStone extends L2GameClientPacket
{
	private int targetItemObjId;
	private int refinerItemObjId;
	private int gemstoneItemObjId;
	private long gemStoneCount;

	/**
	 */
	@Override
	protected void readImpl()
	{
		targetItemObjId = readD();
		refinerItemObjId = readD();
		gemstoneItemObjId = readD();
		gemStoneCount = readQ();
	}

	/**
	 */
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		L2ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(targetItemObjId);
		if (targetItem == null)
		{
			return;
		}
		L2ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(refinerItemObjId);
		if (refinerItem == null)
		{
			return;
		}
		L2ItemInstance gemStoneItem = activeChar.getInventory().getItemByObjectId(gemstoneItemObjId);
		if (gemStoneItem == null)
		{
			return;
		}

		// Make sure the item is a gemstone
		if (!LifeStoneTable.getInstance().isValid(activeChar, targetItem, refinerItem, gemStoneItem))
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM));
			return;
		}

		// Check for gemstone count
		final LifeStone ls = LifeStoneTable.getInstance().getLifeStone(refinerItem.getItemId());
		if (ls == null)
		{
			return;
		}

		if (gemStoneCount != LifeStoneTable.getGemStoneCount(targetItem.getItem().getItemGrade(), ls.getGrade()))
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.GEMSTONE_QUANTITY_IS_INCORRECT));
			return;
		}

		activeChar.sendPacket(
				new ExPutCommissionResultForVariationMake(gemstoneItemObjId, gemStoneCount, gemStoneItem.getItemId()));
	}
}
