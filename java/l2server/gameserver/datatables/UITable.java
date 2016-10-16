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
import l2server.gameserver.model.entity.ActionKey;
import l2server.log.Log;
import l2server.util.xml.XmlDocument;
import l2server.util.xml.XmlNode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mrTJO
 */
public class UITable
{
	private Map<Integer, List<ActionKey>> storedKeys;
	private Map<Integer, List<Integer>> storedCategories;

	public static UITable getInstance()
	{
		return SingletonHolder.instance;
	}

	private UITable()
	{
		this.storedKeys = new HashMap<>();
		this.storedCategories = new HashMap<>();

		parseCatData();
		parseKeyData();
		Log.info("UITable: Loaded " + this.storedCategories.size() + " Categories.");
		Log.info("UITable: Loaded " + this.storedKeys.size() + " Keys.");
	}

	private void parseCatData()
	{
		File file = new File(Config.DATAPACK_ROOT, Config.DATA_FOLDER + "uiCatsEn.xml");
		XmlDocument doc = new XmlDocument(file);

		for (XmlNode n : doc.getChildren())
		{
			if (n.getName().equalsIgnoreCase("list"))
			{
				for (XmlNode d : n.getChildren())
				{
					if (d.getName().equalsIgnoreCase("category"))
					{
						int cat = d.getInt("cat");
						int cmd = d.getInt("cmd");
						insertCategory(cat, cmd);
					}
				}
			}
		}
	}

	private void parseKeyData()
	{
		File file = new File(Config.DATAPACK_ROOT, Config.DATA_FOLDER + "uiKeysEn.xml");
		XmlDocument doc = new XmlDocument(file);
		for (XmlNode n : doc.getChildren())
		{
			if (n.getName().equalsIgnoreCase("list"))
			{
				for (XmlNode d : n.getChildren())
				{
					if (d.getName().equalsIgnoreCase("key"))
					{
						int cat = d.getInt("cat");
						int cmd = d.getInt("cmd");
						int key = d.getInt("key");
						int tk1 = d.getInt("tk1");
						int tk2 = d.getInt("tk2");
						int shw = d.getInt("shw");
						insertKey(cat, cmd, key, tk1, tk2, shw);
					}
				}
			}
		}
	}

	private void insertCategory(int cat, int cmd)
	{
		if (this.storedCategories.containsKey(cat))
		{
			this.storedCategories.get(cat).add(cmd);
		}
		else
		{
			List<Integer> tmp = new ArrayList<>();
			tmp.add(cmd);
			this.storedCategories.put(cat, tmp);
		}
	}

	private void insertKey(int cat, int cmdId, int key, int tgKey1, int tgKey2, int show)
	{
		ActionKey tmk = new ActionKey(cat, cmdId, key, tgKey1, tgKey2, show);
		if (this.storedKeys.containsKey(cat))
		{
			this.storedKeys.get(cat).add(tmk);
		}
		else
		{
			List<ActionKey> tmp = new ArrayList<>();
			tmp.add(tmk);
			this.storedKeys.put(cat, tmp);
		}
	}

	public Map<Integer, List<Integer>> getCategories()
	{
		return this.storedCategories;
	}

	public Map<Integer, List<ActionKey>> getKeys()
	{
		return this.storedKeys;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final UITable instance = new UITable();
	}
}
