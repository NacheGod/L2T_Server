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

import l2server.gameserver.model.L2Party;

/**
 * @author chris_00
 *         <p>
 *         ch Sddd
 */
public class ExMPCCPartyInfoUpdate extends L2GameServerPacket
{
	private L2Party party;
	private int mode, LeaderOID, memberCount;
	private String name;

	/**
	 * @param party
	 * @param mode  0 = Remove, 1 = Add
	 */
	public ExMPCCPartyInfoUpdate(L2Party party, int mode)
	{
		this.party = party;
		name = this.party.getLeader().getName();
		LeaderOID = this.party.getPartyLeaderOID();
		memberCount = this.party.getMemberCount();
		this.mode = mode;
	}

	/**
	 * @see l2server.gameserver.network.serverpackets.L2GameServerPacket#writeImpl()
	 */
	@Override
	protected final void writeImpl()
	{
		writeS(name);
		writeD(LeaderOID);
		writeD(memberCount);
		writeD(mode); //mode 0 = Remove Party, 1 = AddParty, maybe more...
	}
}
