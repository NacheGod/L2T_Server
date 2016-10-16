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

package l2server.gameserver.datatables;

import l2server.Config;
import l2server.gameserver.model.L2Skill;
import lombok.Getter;

/**
 * @author -Nemesiss-
 */
public class NobleSkillTable
{
	@Getter private static final L2Skill[] nobleSkills = new L2Skill[8];

	private NobleSkillTable()
	{
		if (Config.IS_CLASSIC)
		{
			return;
		}

		nobleSkills[0] = SkillTable.getInstance().getInfo(1323, 1);
		nobleSkills[1] = SkillTable.getInstance().getInfo(325, 1);
		nobleSkills[2] = SkillTable.getInstance().getInfo(326, 1);
		nobleSkills[3] = SkillTable.getInstance().getInfo(327, 1);
		nobleSkills[4] = SkillTable.getInstance().getInfo(1324, 1);
		nobleSkills[5] = SkillTable.getInstance().getInfo(1325, 1);
		nobleSkills[6] = SkillTable.getInstance().getInfo(1326, 1);
		nobleSkills[7] = SkillTable.getInstance().getInfo(1327, 1);
	}

	public static NobleSkillTable getInstance()
	{
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final NobleSkillTable instance = new NobleSkillTable();
	}
}
