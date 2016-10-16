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

import l2server.gameserver.datatables.AccessLevels;
import lombok.Getter;

/**
 * @author FBIagent<br>
 */
public class L2AdminCommandAccessRight
{
	/**
	 * The admin command<br>
	 */
	@Getter private String adminCommand = null;
	/**
	 * The access levels which can use the admin command<br>
	 */
	private L2AccessLevel[] accessLevels = null;
	@Getter private boolean requireConfirm;

	/**
	 * Initialized members
	 *
	 * @param adminCommand as String
	 * @param accessLevels as String
	 */
	public L2AdminCommandAccessRight(String adminCommand, String accessLevels, boolean confirm)
	{
		this.adminCommand = adminCommand;
		requireConfirm = confirm;

		String[] accessLevelsSplit = accessLevels.split(",");
		int numLevels = accessLevelsSplit.length;

		this.accessLevels = new L2AccessLevel[numLevels];

		for (int i = 0; i < numLevels; ++i)
		{
			try
			{
				this.accessLevels[i] =
						AccessLevels.getInstance().getAccessLevel(Integer.parseInt(accessLevelsSplit[i]));
			}
			catch (NumberFormatException nfe)
			{
				this.accessLevels[i] = null;
			}
		}
	}

	/**
	 * Checks if the given characterAccessLevel is allowed to use the admin command which belongs to this access right<br><br>
	 *
	 * @param characterAccessLevel<br><br>
	 * @return boolean: true if characterAccessLevel is allowed to use the admin command which belongs to this access right, otherwise false<br>
	 */
	public boolean hasAccess(L2AccessLevel characterAccessLevel)
	{
		for (L2AccessLevel accessLevel : accessLevels)
		{
			if (accessLevel != null && (accessLevel.getLevel() == characterAccessLevel.getLevel() ||
					characterAccessLevel.hasChildAccess(accessLevel)))
			{
				return true;
			}
		}

		return false;
	}
}
