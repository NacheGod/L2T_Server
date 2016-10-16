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

package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.actor.L2Character;

/**
 * sample
 * <p>
 * 0000: 5a  d8 a8 10 48  d8 a8 10 48  10 04 00 00  01 00 00	Z...H...H.......
 * 0010: 00  f0 1a 00 00  68 28 00 00						 .....h(..
 * <p>
 * format   dddddd dddh (h)
 *
 * @version $Revision: 1.4.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public final class MagicSkillUse extends L2GameServerPacket
{
	private int targetId, tx, ty, tz;
	private int gauge;
	private int skillId;
	private int skillLevel;
	private int hitTime;
	private int reuseDelay;
	private int reuseGroup;
	private int charObjId, x, y, z;
	private boolean ground = false;
	private int groundX, groundY, groundZ;
	private int skillActionId;

	//private int flags;

	public MagicSkillUse(L2Character cha, L2Character target, int skillId, int skillLevel, int hitTime, int reuseDelay, int reuseGroup, int gauge, int skillActionId)
	{
		this(cha, target, skillId, skillLevel, hitTime, reuseDelay, reuseGroup, gauge, false, skillActionId);
	}

	public MagicSkillUse(L2Character cha, L2Character target, int skillId, int skillLevel, int hitTime, int reuseDelay, int skillActionId)
	{
		this(cha, target, skillId, skillLevel, hitTime, reuseDelay, reuseDelay > 0 ? 0 : -1, 0, skillActionId);
	}

	public MagicSkillUse(L2Character cha, L2Character target, int skillId, int skillLevel, int hitTime, int reuseDelay, int reuseGroup, int gauge, boolean ground, int skillActionId)
	{
		this.charObjId = cha.getObjectId();
		this.targetId = target.getObjectId();
		this.gauge = gauge;
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		this.hitTime = hitTime;
		this.reuseDelay = reuseDelay;
		this.reuseGroup = reuseGroup;
		this.x = cha.getX();
		this.y = cha.getY();
		this.z = cha.getZ();
		this.tx = target.getX();
		this.ty = target.getY();
		this.tz = target.getZ();
		//_flags |= 0x20;

		this.ground = ground;
		if (this.ground)
		{
			this.groundX = cha.getSkillCastPosition().getX();
			this.groundY = cha.getSkillCastPosition().getY();
			this.groundZ = this.z + 10;
		}

		this.skillActionId = skillActionId;
	}

	public MagicSkillUse(L2Character cha, int skillId, int skillLevel, int hitTime, int reuseDelay)
	{
		this.charObjId = cha.getObjectId();
		this.targetId = cha.getTargetId();
		this.gauge = 0;
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		this.hitTime = hitTime;
		this.reuseDelay = reuseDelay;
		this.reuseGroup = reuseDelay > 0 ? 0 : -1;
		this.x = cha.getX();
		this.y = cha.getY();
		this.z = cha.getZ();
		this.tx = cha.getX();
		this.ty = cha.getY();
		this.tz = cha.getZ();
		//_flags |= 0x20;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(this.gauge); // Don't show casting bar if 1
		writeD(this.charObjId);
		writeD(this.targetId);
		writeD(this.skillId);
		writeD(this.skillLevel);
		writeD(this.hitTime);
		writeD(this.reuseGroup);
		writeD(this.reuseDelay);
		writeD(this.x);
		writeD(this.y);
		writeD(this.z);
		writeH(0x00);

		if (this.ground)
		{
			writeH(0x01);
			writeD(this.groundX);
			writeD(this.groundY);
			writeD(this.groundZ);
		}
		else
		{
			writeH(0x00);
		}

		writeD(this.tx);
		writeD(this.ty);
		writeD(this.tz);

		if (this.skillActionId == 0)
		{
			writeD(0x00);
			writeD(0x00);
		}
		else
		{
			writeD(0x01);
			writeD(this.skillActionId);
		}
	}
}
