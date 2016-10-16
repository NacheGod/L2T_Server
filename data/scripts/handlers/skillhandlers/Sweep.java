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

package handlers.skillhandlers;

import l2server.Config;
import l2server.gameserver.handler.ISkillHandler;
import l2server.gameserver.model.L2ItemInstance;
import l2server.gameserver.model.L2Object;
import l2server.gameserver.model.L2Skill;
import l2server.gameserver.model.actor.L2Attackable;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.InventoryUpdate;
import l2server.gameserver.network.serverpackets.ItemList;
import l2server.gameserver.network.serverpackets.StatusUpdate;
import l2server.gameserver.network.serverpackets.SystemMessage;
import l2server.gameserver.stats.skills.L2SkillSweeper;
import l2server.gameserver.templates.skills.L2SkillType;

/**
 * @author _drunk_
 *         <p>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class Sweep implements ISkillHandler
{
	//private static Logger log = Logger.getLogger(Sweep.class.getName());

	private static final L2SkillType[] SKILL_IDS = {L2SkillType.SWEEP};

	/**
	 * @see l2server.gameserver.handler.ISkillHandler#useSkill(l2server.gameserver.model.actor.L2Character, l2server.gameserver.model.L2Skill, l2server.gameserver.model.L2Object[])
	 */
	@Override
	public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
	{
		if (!(activeChar instanceof L2PcInstance))
		{
			return;
		}

		final L2PcInstance player = (L2PcInstance) activeChar;

		InventoryUpdate iu = Config.FORCE_INVENTORY_UPDATE ? null : new InventoryUpdate();
		boolean send = false;

		for (L2Object tgt : targets)
		{
			if (!(tgt instanceof L2Attackable))
			{
				continue;
			}
			L2Attackable target = (L2Attackable) tgt;
			L2Attackable.RewardItem[] items = null;
			boolean isSweeping = false;
			synchronized (target)
			{
				if (target.isSweepActive())
				{
					items = target.takeSweep();
					isSweeping = true;
				}
			}
			if (isSweeping)
			{
				if (items == null || items.length == 0)
				{
					continue;
				}
				for (L2Attackable.RewardItem ritem : items)
				{
					if (player.isInParty())
					{
						player.getParty().distributeItem(player, ritem, true, target);
					}
					else
					{
						L2ItemInstance item = player.getInventory()
								.addItem("Sweep", ritem.getItemId(), ritem.getCount(), player, target);
						if (iu != null)
						{
							iu.addItem(item);
						}
						send = true;

						SystemMessage smsg;
						if (ritem.getCount() > 1)
						{
							smsg = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S); // earned $s2$s1
							smsg.addItemName(ritem.getItemId());
							smsg.addNumber(ritem.getCount());
						}
						else
						{
							smsg = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1); // earned $s1
							smsg.addItemName(ritem.getItemId());
						}
						player.sendPacket(smsg);
					}
				}
			}
			target.endDecayTask();

			L2SkillSweeper sweep = (L2SkillSweeper) skill;
			if (sweep.getAbsorbAbs() != -1)
			{
				if (sweep.isAbsorbHp())
				{
					int hpAdd = sweep.getAbsorbAbs();
					double hp = activeChar.getCurrentHp() + hpAdd > activeChar.getMaxHp() ? activeChar.getMaxHp() :
							activeChar.getCurrentHp() + hpAdd;
					int restored = (int) (hp - activeChar.getCurrentHp());
					activeChar.setCurrentHp(hp);

					StatusUpdate suhp = new StatusUpdate(activeChar);
					suhp.addAttribute(StatusUpdate.CUR_HP, (int) hp);
					activeChar.sendPacket(suhp);

					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED);
					sm.addNumber(restored);
					sm.addHpChange(activeChar.getObjectId(), activeChar.getObjectId(), restored);
					activeChar.sendPacket(sm);
				}
				else
				{
					int mpAdd = sweep.getAbsorbAbs();
					double mp = activeChar.getCurrentMp() + mpAdd > activeChar.getMaxMp() ? activeChar.getMaxMp() :
							activeChar.getCurrentMp() + mpAdd;
					int restored = (int) (mp - activeChar.getCurrentMp());
					activeChar.setCurrentMp(mp);

					StatusUpdate suhp = new StatusUpdate(activeChar);
					suhp.addAttribute(StatusUpdate.CUR_MP, (int) mp);
					activeChar.sendPacket(suhp);

					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MP_RESTORED);
					sm.addNumber(restored);
					activeChar.sendPacket(sm);
				}
			}
		}

		if (send)
		{
			if (iu != null)
			{
				player.sendPacket(iu);
			}
			else
			{
				player.sendPacket(new ItemList(player, false));
			}
		}
	}

	/**
	 * @see l2server.gameserver.handler.ISkillHandler#getSkillIds()
	 */
	@Override
	public L2SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}
