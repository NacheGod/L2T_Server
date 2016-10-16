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

/**
 * Format: ch ddcdc
 *
 * @author KenM
 */
public class ExPCCafePointInfo extends L2GameServerPacket
{
	private int unk1, unk2, unk3, unk4, unk5 = 0;

	public ExPCCafePointInfo(int val1, int val2, int val3, int val4, int val5)
	{
		unk1 = val1;
		unk2 = val2;
		unk3 = val3;
		unk4 = val4;
		unk5 = val5;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(unk1); // num points
		writeD(unk2); // points inc display
		writeC(unk3); // period(0=don't show window,1=acquisition,2=use points)
		writeD(unk4); // period hours left
		writeC(unk5); // points inc display color(0=yellow,1=cyan-blue,2=red,all other black)
	}
}
