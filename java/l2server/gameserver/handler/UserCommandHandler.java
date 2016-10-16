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

package l2server.gameserver.handler;

import gnu.trove.TIntObjectHashMap;
import l2server.Config;
import l2server.log.Log;

/**
 * This class ...
 *
 * @version $Revision: 1.1.2.1.2.5 $ $Date: 2005/03/27 15:30:09 $
 */
public class UserCommandHandler
{

	private TIntObjectHashMap<IUserCommandHandler> datatable;

	public static UserCommandHandler getInstance()
	{
		return SingletonHolder.instance;
	}

	private UserCommandHandler()
	{
		this.datatable = new TIntObjectHashMap<>();
	}

	public void registerUserCommandHandler(IUserCommandHandler handler)
	{
		int[] ids = handler.getUserCommandList();
		for (int id : ids)
		{
			if (Config.DEBUG)
			{
				Log.fine("Adding handler for user command " + id);
			}
			this.datatable.put(id, handler);
		}
	}

	public IUserCommandHandler getUserCommandHandler(int userCommand)
	{
		if (Config.DEBUG)
		{
			Log.fine("getting handler for user command: " + userCommand);
		}
		return this.datatable.get(userCommand);
	}

	/**
	 * @return
	 */
	public int size()
	{
		return this.datatable.size();
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final UserCommandHandler instance = new UserCommandHandler();
	}
}
