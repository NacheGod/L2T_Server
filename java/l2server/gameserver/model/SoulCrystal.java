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

package l2server.gameserver.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pere
 */
public class SoulCrystal
{
	@Getter private final int id;
	@Getter private final boolean special;
	@Getter private final List<EnsoulEffect> effects = new ArrayList<>();

	public SoulCrystal(int id, boolean special)
	{
		this.id = id;
		this.special = special;
	}

	public void addEffect(EnsoulEffect effect)
	{
		effects.add(effect);
	}
}
