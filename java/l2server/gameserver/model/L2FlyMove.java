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

package l2server.gameserver.model;

import gnu.trove.TIntObjectHashMap;
import l2server.util.Point3D;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pere
 */
public class L2FlyMove
{
	public enum L2FlyMoveType
	{
		START, CHOOSE, MOVE
	}

	@Getter private int id;
	private TIntObjectHashMap<Point3D> steps = new TIntObjectHashMap<>();
	private TIntObjectHashMap<L2FlyMoveChoose> chooses = new TIntObjectHashMap<>();

	public L2FlyMove(int id)
	{
		this.id = id;
	}

	public void addStep(int id, Point3D s)
	{
		steps.put(id, s);
	}

	public Point3D getStep(int id)
	{
		return steps.get(id);
	}

	public void addChoose(int id, L2FlyMoveChoose c)
	{
		chooses.put(id, c);
	}

	public L2FlyMoveChoose getChoose(int id)
	{
		return chooses.get(id);
	}

	public class L2FlyMoveChoose
	{
		@Getter private int at;
		@Getter private List<L2FlyMoveOption> options = new ArrayList<>();

		public L2FlyMoveChoose(int at)
		{
			this.at = at;
		}

		public void addOption(L2FlyMoveOption o)
		{
			options.add(o);
		}
	}

	public class L2FlyMoveOption
	{
		@Getter private int start;
		@Getter private int end;
		@Getter private int last;

		public L2FlyMoveOption(int start, int end, int last)
		{
			this.start = start;
			this.end = end;
			this.last = last;
		}
	}
}
