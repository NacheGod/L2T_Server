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

import l2server.gameserver.instancemanager.ClanRecruitManager;
import l2server.gameserver.instancemanager.ClanRecruitManager.ClanRecruitWaitingUser;
import l2server.gameserver.instancemanager.MailManager;
import l2server.gameserver.model.L2Clan;
import l2server.gameserver.model.L2World;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.entity.Message;
import l2server.gameserver.model.entity.Message.SendBySystem;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.AskJoinPledge;
import l2server.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Pere
 */
public final class RequestPledgeApplicationAccept extends L2GameClientPacket
{
	private boolean accept;
	private int applicantId;
	private int pledgeType;

	@Override
	protected void readImpl()
	{
		this.accept = readD() == 1;
		this.applicantId = readD();
		this.pledgeType = readD();
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null || !activeChar.isClanLeader())
		{
			return;
		}

		ClanRecruitWaitingUser applicant = ClanRecruitManager.getInstance().getApplicant(this.applicantId);
		if (applicant == null)
		{
			return;
		}

		if (this.accept)
		{
			final L2Clan clan = activeChar.getClan();
			if (clan == null)
			{
				return;
			}

			final L2PcInstance target = L2World.getInstance().getPlayer(this.applicantId);
			if (target == null)
			{
				activeChar
						.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET));
				return;
			}

			if (!clan.checkClanJoinCondition(activeChar, target, this.pledgeType))
			{
				return;
			}

			if (!activeChar.getRequest().setRequest(target, this))
			{
				return;
			}

			final String pledgeName = clan.getName();
			final String subPledgeName =
					clan.getSubPledge(this.pledgeType) != null ? activeChar.getClan().getSubPledge(this.pledgeType).getName() :
							null;
			target.sendPacket(new AskJoinPledge(activeChar.getObjectId(), subPledgeName, this.pledgeType, pledgeName));
		}
		else
		{
			Message msg = new Message(this.applicantId, "Clan Application Rejected",
					"Sorry, your clan application has been rejected.", SendBySystem.SYSTEM);
			MailManager.getInstance().sendMessage(msg);

			ClanRecruitManager.getInstance().removeApplicant(this.applicantId);
		}
	}

	public int getPledgeType()
	{
		return this.pledgeType;
	}
}
