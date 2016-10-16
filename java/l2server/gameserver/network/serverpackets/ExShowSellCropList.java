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

import l2server.gameserver.instancemanager.CastleManorManager.CropProcure;
import l2server.gameserver.model.L2ItemInstance;
import l2server.gameserver.model.L2Manor;
import l2server.gameserver.model.actor.instance.L2PcInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * format(packet 0xFE) ch dd [ddddcdcdddc] c - id h - sub id
 * <p>
 * d - manor id d - size
 * [ d - Object id d - crop id d - seed level c d - reward 1 id c d - reward 2
 * id d - manor d - buy residual d - buy price d - reward ]
 *
 * @author l3x
 */

public class ExShowSellCropList extends L2GameServerPacket
{

	private int manorId = 1;
	private final HashMap<Integer, L2ItemInstance> cropsItems;
	private final HashMap<Integer, CropProcure> castleCrops;

	public ExShowSellCropList(L2PcInstance player, int manorId, List<CropProcure> crops)
	{
		this.manorId = manorId;
		this.castleCrops = new HashMap<>();
		this.cropsItems = new HashMap<>();

		ArrayList<Integer> allCrops = L2Manor.getInstance().getAllCrops();
		for (int cropId : allCrops)
		{
			L2ItemInstance item = player.getInventory().getItemByItemId(cropId);
			if (item != null)
			{
				this.cropsItems.put(cropId, item);
			}
		}

		for (CropProcure crop : crops)
		{
			if (this.cropsItems.containsKey(crop.getId()) && crop.getAmount() > 0)
			{
				this.castleCrops.put(crop.getId(), crop);
			}
		}
	}

	@Override
	public void runImpl()
	{
		// no long running
	}

	@Override
	protected final void writeImpl()
	{
		writeD(this.manorId); // manor id
		writeD(this.cropsItems.size()); // size

		for (L2ItemInstance item : this.cropsItems.values())
		{
			writeD(item.getObjectId()); // Object id
			writeD(item.getItemId()); // crop id
			writeD(L2Manor.getInstance().getSeedLevelByCrop(item.getItemId())); // seed level
			writeC(1);
			writeD(L2Manor.getInstance().getRewardItem(item.getItemId(), 1)); // reward 1 id
			writeC(1);
			writeD(L2Manor.getInstance().getRewardItem(item.getItemId(), 2)); // reward 2 id

			if (this.castleCrops.containsKey(item.getItemId()))
			{
				CropProcure crop = this.castleCrops.get(item.getItemId());
				writeD(this.manorId); // manor
				writeQ(crop.getAmount()); // buy residual
				writeQ(crop.getPrice()); // buy price
				writeC(crop.getReward()); // reward
			}
			else
			{
				writeD(0xFFFFFFFF); // manor
				writeQ(0); // buy residual
				writeQ(0); // buy price
				writeC(0); // reward
			}
			writeQ(item.getCount()); // my crops
		}
	}
}
