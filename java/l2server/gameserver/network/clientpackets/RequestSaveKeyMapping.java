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

package l2server.gameserver.network.clientpackets;

import l2server.Config;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.entity.ActionKey;
import l2server.gameserver.network.L2GameClient.GameClientState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mrTJO
 */
public class RequestSaveKeyMapping extends L2GameClientPacket
{
	int tabNum;

	Map<Integer, List<ActionKey>> keyMap = new HashMap<>();
	Map<Integer, List<Integer>> catMap = new HashMap<>();

	/**
	 * @see l2server.gameserver.network.clientpackets.L2GameClientPacket#readImpl()
	 */
	@Override
	protected void readImpl()
	{
		int category = 0;

		readD(); // Unknown
		readD(); // Unknown
		this.tabNum = readD();
		for (int i = 0; i < this.tabNum; i++)
		{
			int cmd1Size = readC();
			for (int j = 0; j < cmd1Size; j++)
			{
				int cmdId = readC();
				insertCategory(category, cmdId);
			}
			category++;

			int cmd2Size = readC();
			for (int j = 0; j < cmd2Size; j++)
			{
				int cmdId = readC();
				insertCategory(category, cmdId);
			}
			category++;

			int cmdSize = readD();
			for (int j = 0; j < cmdSize; j++)
			{
				int cmd = readD();
				int key = readD();
				int tgKey1 = readD();
				int tgKey2 = readD();
				int show = readD();
				insertKey(i, cmd, key, tgKey1, tgKey2, show);
			}
		}
		readD();
		readD();
	}

	public void insertCategory(int cat, int cmd)
	{
		if (this.catMap.containsKey(cat))
		{
			this.catMap.get(cat).add(cmd);
		}
		else
		{
			List<Integer> tmp = new ArrayList<>();
			tmp.add(cmd);
			this.catMap.put(cat, tmp);
		}
	}

	public void insertKey(int cat, int cmdId, int key, int tgKey1, int tgKey2, int show)
	{
		ActionKey tmk = new ActionKey(cat, cmdId, key, tgKey1, tgKey2, show);
		if (this.keyMap.containsKey(cat))
		{
			this.keyMap.get(cat).add(tmk);
		}
		else
		{
			List<ActionKey> tmp = new ArrayList<>();
			tmp.add(tmk);
			this.keyMap.put(cat, tmp);
		}
	}

	/**
	 * @see l2server.gameserver.network.clientpackets.L2GameClientPacket#runImpl()
	 */
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();

		if (player == null)
		{
			return;
		}
		if (getClient().getState() != GameClientState.IN_GAME)
		{
			return;
		}
		if (Config.STORE_UI_SETTINGS)
		{
			player.getUISettings().storeAll(this.catMap, this.keyMap);
		}
	}
}
