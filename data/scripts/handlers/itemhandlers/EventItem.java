/*

 */

package handlers.itemhandlers;

import l2server.gameserver.handler.IItemHandler;
import l2server.gameserver.instancemanager.HandysBlockCheckerManager;
import l2server.gameserver.instancemanager.HandysBlockCheckerManager.ArenaParticipantsHolder;
import l2server.gameserver.model.L2ItemInstance;
import l2server.gameserver.model.L2Skill;
import l2server.gameserver.model.actor.L2Playable;
import l2server.gameserver.model.actor.instance.L2BlockInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.SystemMessage;
import l2server.log.Log;

import java.util.logging.Logger;

public class EventItem implements IItemHandler
{
	private static final Logger log = Logger.getLogger(EventItem.class.getName());

	/* (non-Javadoc)
	 * @see l2server.gameserver.handler.IItemHandler#useItem(l2server.gameserver.model.actor.L2Playable, l2server.gameserver.model.L2ItemInstance, boolean)
	 */
	@Override
	public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
		{
			return;
		}

		final L2PcInstance activeChar = (L2PcInstance) playable;

		final int itemId = item.getItemId();
		switch (itemId)
		{
			case 13787: // Handy's Block Checker Bond
				useBlockCheckerItem(activeChar, item);
				break;
			case 13788: // Handy's Block Checker Land Mine
				useBlockCheckerItem(activeChar, item);
				break;
			default:
				Log.warning("EventItemHandler: Item with id: " + itemId + " is not handled");
		}
	}

	private final void useBlockCheckerItem(final L2PcInstance castor, L2ItemInstance item)
	{
		final int blockCheckerArena = castor.getBlockCheckerArena();
		if (blockCheckerArena == -1)
		{
			SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
			msg.addItemName(item);
			castor.sendPacket(msg);
			return;
		}

		final L2Skill sk = item.getEtcItem().getSkills()[0].getSkill();
		if (sk == null)
		{
			return;
		}

		if (!castor.destroyItem("Consume", item, 1, castor, true))
		{
			return;
		}

		final L2BlockInstance block = (L2BlockInstance) castor.getTarget();

		final ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(blockCheckerArena);
		if (holder != null)
		{
			final int team = holder.getPlayerTeam(castor);
			for (final L2PcInstance pc : block.getKnownList().getKnownPlayersInRadius(sk.getEffectRange()))
			{
				final int enemyTeam = holder.getPlayerTeam(pc);
				if (enemyTeam != -1 && enemyTeam != team)
				{
					sk.getEffects(castor, pc);
				}
			}
		}
		else
		{
			Log.warning("Char: " + castor.getName() + "[" + castor.getObjectId() + "] has unknown block checker arena");
		}
	}
}
