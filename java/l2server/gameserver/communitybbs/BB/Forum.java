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

package l2server.gameserver.communitybbs.BB;

import l2server.L2DatabaseFactory;
import l2server.gameserver.communitybbs.Manager.ForumsBBSManager;
import l2server.gameserver.communitybbs.Manager.TopicBBSManager;
import l2server.log.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Forum
{
	//type
	public static final int ROOT = 0;
	public static final int NORMAL = 1;
	public static final int CLAN = 2;
	public static final int MEMO = 3;
	public static final int MAIL = 4;
	//perm
	public static final int INVISIBLE = 0;
	public static final int ALL = 1;
	public static final int CLANMEMBERONLY = 2;
	public static final int OWNERONLY = 3;

	private List<Forum> children;
	private Map<Integer, Topic> topic;
	private int forumId;
	private String forumName;
	//private int ForumParent;
	private int forumType;
	private int forumPost;
	private int forumPerm;
	private Forum fParent;
	private int ownerID;
	private boolean loaded = false;

	/**
	 * Creates new instance of Forum. When you create new forum, use
	 * {@link l2server.gameserver.communitybbs.Manager.ForumsBBSManager#
	 * addForum(l2server.gameserver.communitybbs.BB.Forum)} to add forum
	 * to the forums manager.
	 */
	public Forum(int Forumid, Forum FParent)
	{
		this.forumId = Forumid;
		this.fParent = FParent;
		this.children = new ArrayList<>();
		this.topic = new HashMap<>();

		/*load();
		getChildren();	*/
	}

	/**
	 * @param name
	 * @param parent
	 * @param type
	 * @param perm
	 */
	public Forum(String name, Forum parent, int type, int perm, int OwnerID)
	{
		this.forumName = name;
		this.forumId = ForumsBBSManager.getInstance().getANewID();
		//_ForumParent = parent.getID();
		this.forumType = type;
		this.forumPost = 0;
		this.forumPerm = perm;
		this.fParent = parent;
		this.ownerID = OwnerID;
		this.children = new ArrayList<>();
		this.topic = new HashMap<>();
		parent.children.add(this);
		ForumsBBSManager.getInstance().addForum(this);
		this.loaded = true;
	}

	/**
	 *
	 */
	private void load()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM forums WHERE forum_id=?");
			statement.setInt(1, this.forumId);
			ResultSet result = statement.executeQuery();

			if (result.next())
			{
				this.forumName = result.getString("forum_name");
				//_ForumParent = result.getInt("forum_parent");
				this.forumPost = result.getInt("forum_post");
				this.forumType = result.getInt("forum_type");
				this.forumPerm = result.getInt("forum_perm");
				this.ownerID = result.getInt("forum_owner_id");
			}
			result.close();
			statement.close();
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "Data error on Forum " + this.forumId + " : " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement =
					con.prepareStatement("SELECT * FROM topic WHERE topic_forum_id=? ORDER BY topic_id DESC");
			statement.setInt(1, this.forumId);
			ResultSet result = statement.executeQuery();

			while (result.next())
			{
				Topic t = new Topic(Topic.ConstructorType.RESTORE, result.getInt("topic_id"),
						result.getInt("topic_forum_id"), result.getString("topic_name"), result.getLong("topic_date"),
						result.getString("topic_ownername"), result.getInt("topic_ownerid"),
						result.getInt("topic_type"), result.getInt("topic_reply"));
				this.topic.put(t.getID(), t);
				if (t.getID() > TopicBBSManager.getInstance().getMaxID(this))
				{
					TopicBBSManager.getInstance().setMaxID(t.getID(), this);
				}
			}
			result.close();
			statement.close();
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "Data error on Forum " + this.forumId + " : " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	/**
	 *
	 */
	private void getChildren()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT forum_id FROM forums WHERE forum_parent=?");
			statement.setInt(1, this.forumId);
			ResultSet result = statement.executeQuery();

			while (result.next())
			{
				Forum f = new Forum(result.getInt("forum_id"), this);
				this.children.add(f);
				ForumsBBSManager.getInstance().addForum(f);
			}
			result.close();
			statement.close();
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "Data error on Forum (children): " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	public int getTopicSize()
	{
		vload();
		return this.topic.size();
	}

	public Topic getTopic(int j)
	{
		vload();
		return this.topic.get(j);
	}

	public void addTopic(Topic t)
	{
		vload();
		this.topic.put(t.getID(), t);
	}

	/**
	 * @return
	 */
	public int getID()
	{
		return this.forumId;
	}

	public String getName()
	{
		vload();
		return this.forumName;
	}

	public int getType()
	{
		vload();
		return this.forumType;
	}

	/**
	 * @param name
	 * @return
	 */
	public Forum getChildByName(String name)
	{
		vload();
		for (Forum f : this.children)
		{
			if (f.getName().equals(name))
			{
				return f;
			}
		}
		return null;
	}

	/**
	 * @param id
	 */
	public void rmTopicByID(int id)
	{
		this.topic.remove(id);
	}

	/**
	 *
	 */
	public void insertIntoDb()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(
					"INSERT INTO forums (forum_id,forum_name,forum_parent,forum_post,forum_type,forum_perm,forum_owner_id) VALUES (?,?,?,?,?,?,?)");
			statement.setInt(1, this.forumId);
			statement.setString(2, this.forumName);
			statement.setInt(3, this.fParent.getID());
			statement.setInt(4, this.forumPost);
			statement.setInt(5, this.forumType);
			statement.setInt(6, this.forumPerm);
			statement.setInt(7, this.ownerID);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			Log.log(Level.WARNING, "Error while saving new Forum to db " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}

	/**
	 *
	 */
	public void vload()
	{
		if (!this.loaded)
		{
			load();
			getChildren();
			this.loaded = true;
		}
	}
}
