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

package l2server.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.List;

public class DiscussionManager
{
	private List<Integer> voted = new ArrayList<>();
	private int[] votes = new int[10];
	private boolean votesEnabled = false;
	private boolean globalChatDisabled = false;

	public static DiscussionManager getInstance()
	{
		return SingletonHolder.instance;
	}

	public boolean vote(int objectId, byte option)
	{
		if (this.voted.contains(objectId))
		{
			return false;
		}
		this.voted.add(objectId);
		this.votes[option]++;
		return true;
	}

	public void startVotations()
	{
		this.voted.clear();
		for (int i = 0; i < this.votes.length; i++)
		{
			this.votes[i] = 0;
		}
		this.votesEnabled = true;
	}

	public int[] endVotations()
	{
		this.voted.clear();
		this.votesEnabled = false;
		return this.votes;
	}

	public boolean areVotesEnabled()
	{
		return this.votesEnabled;
	}

	public void setGlobalChatDisabled(boolean chatDisabled)
	{
		this.globalChatDisabled = chatDisabled;
	}

	public boolean isGlobalChatDisabled()
	{
		return this.globalChatDisabled;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final DiscussionManager instance = new DiscussionManager();
	}
}
