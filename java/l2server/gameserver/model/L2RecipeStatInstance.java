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

/**
 * This class describes a RecipeList statUse and altStatChange component.<BR><BR>
 */
public class L2RecipeStatInstance
{
	public enum StatType
	{
		HP, MP, XP, SP, GIM // grab item modifier:
		// GIM: the default function uses only the skilllevel to determine
		//	  how many item is grabbed in each step
		//	  with this stat changer you can multiple this
	}

	/**
	 * The Identifier of the statType
	 */
	@Getter private StatType type;

	/**
	 * The value of the statType
	 */
	@Getter private int value;

	/**
	 * Constructor of L2RecipeStatInstance.<BR><BR>
	 */
	public L2RecipeStatInstance(String type, int value)
	{
		try
		{
			this.type = Enum.valueOf(StatType.class, type);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException();
		}
		this.value = value;
	}
}
