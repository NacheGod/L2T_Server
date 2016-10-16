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

package l2server.gameserver.model.multisell;

import l2server.gameserver.model.Elementals;
import l2server.gameserver.model.L2ItemInstance;
import lombok.Getter;

/**
 * @author DS
 */
public class ItemInfo
{
	@Getter private final int enchantLevel;
	@Getter private int[] ensoulEffectIds;
	@Getter private int[] ensoulSpecialEffectIds;
	@Getter private final long augmentId;
	@Getter private final byte elementId;
	@Getter private final int elementPower;
	@Getter private final int[] elementals = new int[6];

	public ItemInfo(L2ItemInstance item)
	{
		enchantLevel = item.getEnchantLevel();
		ensoulEffectIds = item.getEnsoulEffectIds();
		ensoulSpecialEffectIds = item.getEnsoulSpecialEffectIds();
		augmentId = item.getAugmentation() != null ? item.getAugmentation().getId() : 0;
		elementId = item.getAttackElementType();
		elementPower = item.getAttackElementPower();
		elementals[0] = item.getElementDefAttr(Elementals.FIRE);
		elementals[1] = item.getElementDefAttr(Elementals.WATER);
		elementals[2] = item.getElementDefAttr(Elementals.WIND);
		elementals[3] = item.getElementDefAttr(Elementals.EARTH);
		elementals[4] = item.getElementDefAttr(Elementals.HOLY);
		elementals[5] = item.getElementDefAttr(Elementals.DARK);
	}
}
