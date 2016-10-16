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

package l2server.gameserver.stats.skills;

import l2server.gameserver.datatables.NpcTable;
import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.idfactory.IdFactory;
import l2server.gameserver.model.L2Object;
import l2server.gameserver.model.L2Skill;
import l2server.gameserver.model.L2Spawn;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.L2Trap;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.actor.instance.L2TrapInstance;
import l2server.gameserver.templates.StatsSet;
import l2server.gameserver.templates.chars.L2NpcTemplate;
import lombok.Getter;

public class L2SkillTrap extends L2SkillSummon
{
	@Getter private int triggerSkillId = 0;
	private int triggerSkillLvl = 0;
	private int trapNpcId = 0;
	protected L2Spawn trapSpawn;

	/**
	 * @param set
	 */
	public L2SkillTrap(StatsSet set)
	{
		super(set);
		triggerSkillId = set.getInteger("triggerSkillId");
		triggerSkillLvl = set.getInteger("triggerSkillLvl");
		trapNpcId = set.getInteger("trapNpcId");
	}

	/**
	 * @see l2server.gameserver.model.L2Skill#useSkill(l2server.gameserver.model.actor.L2Character, l2server.gameserver.model.L2Object[])
	 */
	@Override
	public void useSkill(L2Character caster, L2Object[] targets)
	{
		if (caster.isAlikeDead() || !(caster instanceof L2PcInstance))
		{
			return;
		}

		if (trapNpcId == 0)
		{
			return;
		}

		L2PcInstance activeChar = (L2PcInstance) caster;

		if (activeChar.inObserverMode())
		{
			return;
		}

		if (activeChar.isMounted())
		{
			return;
		}

		if (triggerSkillId == 0 || triggerSkillLvl == 0)
		{
			return;
		}

		L2Trap trap = activeChar.getTrap();
		if (trap != null)
		{
			trap.unSummon();
		}

		L2Skill skill = SkillTable.getInstance().getInfo(triggerSkillId, triggerSkillLvl);

		if (skill == null)
		{
			return;
		}

		L2NpcTemplate TrapTemplate = NpcTable.getInstance().getTemplate(trapNpcId);
		trap = new L2TrapInstance(IdFactory.getInstance().getNextId(), TrapTemplate, activeChar, getTotalLifeTime(),
				skill);
		trap.setCurrentHp(trap.getMaxHp());
		trap.setCurrentMp(trap.getMaxMp());
		trap.setIsInvul(true);
		trap.setHeading(activeChar.getHeading());
		activeChar.setTrap(trap);
		//L2World.getInstance().storeObject(trap);
		trap.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());
	}
}
