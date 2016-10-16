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

package l2server.gameserver.model.itemcontainer;

import l2server.gameserver.model.L2ItemInstance.ItemLocation;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import lombok.Getter;

/**
 * @author Erlandys
 */
public class PcAuction extends ItemContainer
{
	@Getter private L2PcInstance owner;

	public PcAuction(L2PcInstance owner)
	{
		this.owner = owner;
	}

	@Override
	public String getName()
	{
		return "Auction";
	}

	@Override
	public ItemLocation getBaseLocation()
	{
		return ItemLocation.AUCTION;
	}

	@Override
	public boolean validateCapacity(long slots)
	{
		return items.size() + slots <= 10;
	}
}
