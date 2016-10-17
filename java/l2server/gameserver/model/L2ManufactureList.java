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
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class ...
 *
 * @version $Revision: 1.1.2.1.2.2 $ $Date: 2005/03/27 15:29:33 $
 */
public class L2ManufactureList
{
	@Getter @Setter private List<L2ManufactureItem> list;
	private boolean confirmed;
	private String manufactureStoreName;

	public L2ManufactureList()
	{
		list = new ArrayList<>();
		confirmed = false;
	}

	public int size()
	{
		return list.size();
	}

	public void setConfirmedTrade(boolean x)
	{
		confirmed = x;
	}

	public boolean hasConfirmed()
	{
		return confirmed;
	}

	/**
	 */
	public void setStoreName(String manufactureStoreName)
	{
		this.manufactureStoreName = manufactureStoreName;
	}

	/**
	 * @return Returns the manufactureStoreName.
	 */
	public String getStoreName()
	{
		return manufactureStoreName;
	}

	public void add(L2ManufactureItem item)
	{
		list.add(item);
	}
}
