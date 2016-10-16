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

import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.templates.item.L2Henna;

/**
 * @author KenM
 */
public class GMHennaInfo extends L2GameServerPacket
{
	private final L2PcInstance activeChar;
	private final L2Henna[] hennas = new L2Henna[4];
	private int count;

	public GMHennaInfo(L2PcInstance activeChar)
	{
		this.activeChar = activeChar;

		int j = 0;
		for (int i = 0; i < 3; i++)
		{
			L2Henna h = this.activeChar.getHenna(i + 1);
			if (h != null)
			{
				this.hennas[j++] = h;
			}
		}
		this.count = j;
	}

    /*
	  @see l2server.gameserver.network.serverpackets.L2GameServerPacket#getType()
     */

	/**
	 * @see l2server.gameserver.network.serverpackets.L2GameServerPacket#writeImpl()
	 */
	@Override
	protected final void writeImpl()
	{
		writeC(this.activeChar.getHennaStatINT());
		writeC(this.activeChar.getHennaStatSTR());
		writeC(this.activeChar.getHennaStatCON());
		writeC(this.activeChar.getHennaStatMEN());
		writeC(this.activeChar.getHennaStatDEX());
		writeC(this.activeChar.getHennaStatWIT());
		writeC(this.activeChar.getHennaStatLUC());
		writeC(this.activeChar.getHennaStatCHA());
		writeD(4); // slots?
		writeD(this.count); //size
		for (int i = 0; i < this.count; i++)
		{
			writeD(this.hennas[i].getSymbolId());
			writeD(0x01);
		}

		L2Henna specialDye = this.activeChar.getHenna(4);
		if (specialDye != null)
		{
			writeD(specialDye.getSymbolId());
			writeD((int) (specialDye.getExpiryTime() - System.currentTimeMillis()) / 1000); // Seconds
			writeD(0x01);
		}
	}
}
