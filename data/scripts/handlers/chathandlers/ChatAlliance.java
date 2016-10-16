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

package handlers.chathandlers;

import l2server.gameserver.gui.ConsoleTab;
import l2server.gameserver.gui.ConsoleTab.ConsoleFilter;
import l2server.gameserver.handler.IChatHandler;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.CreatureSay;

public class ChatAlliance implements IChatHandler
{
	private static final int[] COMMAND_IDS = {9};

	/**
	 * Handle chat type 'alliance'
	 */
	@Override
	public void handleChat(int type, L2PcInstance activeChar, String target, String text)
	{
		/*if (activeChar.isGM())
		{
			CreatureSay cs = new CreatureSay(activeChar, type, activeChar.getName(), text);
			GmListTable.broadcastToGMs(cs);
		}
		else*/
		if (activeChar.getClan() != null)
		{
			CreatureSay cs = new CreatureSay(activeChar, type, activeChar.getName(), text);
			activeChar.getClan().broadcastToOnlineAllyMembers(cs);

			/*if (Config.isServer(Config.TENKAI))
			{
				cs = new CreatureSay(activeChar, type, "[" + activeChar.getClan().getAllyName() + "] " + activeChar.getName(), text);
				GmListTable.broadcastToGMs(cs);
			}*/

			while (text.contains("Type=") && text.contains("Title="))
			{
				int index1 = text.indexOf("Type=");
				int index2 = text.indexOf("Title=") + 6;
				text = text.substring(0, index1) + text.substring(index2);
			}

			String allyName = activeChar.getClan().getAllyName();
			ConsoleTab.appendMessage(ConsoleFilter.AllyChat, "[" + allyName + "] " + activeChar.getName() + ": " + text,
					allyName, activeChar.getName());
		}
	}

	/**
	 * Returns the chat types registered to this handler
	 *
	 * @see l2server.gameserver.handler.IChatHandler#getChatTypeList()
	 */
	@Override
	public int[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}
