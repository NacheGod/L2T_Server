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

package l2server.gameserver.model.itemauction;

import l2server.gameserver.model.L2World;
import l2server.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Forsaiken
 */
public final class ItemAuctionBid
{
	private final int playerObjId;
	private long lastBid;

	public ItemAuctionBid(final int playerObjId, final long lastBid)
	{
		this.playerObjId = playerObjId;
		this.lastBid = lastBid;
	}

	public final int getPlayerObjId()
	{
		return this.playerObjId;
	}

	public final long getLastBid()
	{
		return this.lastBid;
	}

	final void setLastBid(final long lastBid)
	{
		this.lastBid = lastBid;
	}

	final void cancelBid()
	{
		this.lastBid = -1;
	}

	final boolean isCanceled()
	{
		return this.lastBid <= 0;
	}

	final L2PcInstance getPlayer()
	{
		return L2World.getInstance().getPlayer(this.playerObjId);
	}
}
