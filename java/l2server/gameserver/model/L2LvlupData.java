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

/**
 * This class ...
 *
 * @author NightMarez
 * @version $Revision: 1.2.2.1.2.1 $ $Date: 2005/03/27 15:29:32 $
 */
public class L2LvlupData
{
	private int classid;
	private int classLvl;
	private float classHpAdd;
	private float classHpBase;
	private float classHpModifier;
	private float classCpAdd;
	private float classCpBase;
	private float classCpModifier;
	private float classMpAdd;
	private float classMpBase;
	private float classMpModifier;

	/**
	 * @return Returns the this.classHpAdd.
	 */
	@Deprecated
	public float getClassHpAdd()
	{
		return this.classHpAdd;
	}

	/**
	 * @param hpAdd The this.classHpAdd to set.
	 */
	public void setClassHpAdd(float hpAdd)
	{
		this.classHpAdd = hpAdd;
	}

	/**
	 * @return Returns the this.classHpBase.
	 */
	@Deprecated
	public float getClassHpBase()
	{
		return this.classHpBase;
	}

	/**
	 * @param hpBase The this.classHpBase to set.
	 */
	public void setClassHpBase(float hpBase)
	{
		this.classHpBase = hpBase;
	}

	/**
	 * @return Returns the this.classHpModifier.
	 */
	@Deprecated
	public float getClassHpModifier()
	{
		return this.classHpModifier;
	}

	/**
	 * @param hpModifier The this.classHpModifier to set.
	 */
	public void setClassHpModifier(float hpModifier)
	{
		this.classHpModifier = hpModifier;
	}

	/**
	 * @return Returns the this.classCpAdd.
	 */
	@Deprecated
	public float getClassCpAdd()
	{
		return this.classCpAdd;
	}

	/**
	 */
	public void setClassCpAdd(float cpAdd)
	{
		this.classCpAdd = cpAdd;
	}

	/**
	 * @return Returns the this.classCpBase.
	 */
	@Deprecated
	public float getClassCpBase()
	{
		return this.classCpBase;
	}

	/**
	 */
	public void setClassCpBase(float cpBase)
	{
		this.classCpBase = cpBase;
	}

	/**
	 * @return Returns the this.classCpModifier.
	 */
	@Deprecated
	public float getClassCpModifier()
	{
		return this.classCpModifier;
	}

	/**
	 * @param cpModifier The this.classCpModifier to set.
	 */
	public void setClassCpModifier(float cpModifier)
	{
		this.classCpModifier = cpModifier;
	}

	/**
	 * @return Returns the this.classid.
	 */
	public int getCurrentClass()
	{
		return this.classid;
	}

	/**
	 */
	public void setClassid(int pClassid)
	{
		this.classid = pClassid;
	}

	/**
	 * @return Returns the this.classLvl.
	 */
	@Deprecated
	public int getClassLvl()
	{
		return this.classLvl;
	}

	/**
	 * @param lvl The this.classLvl to set.
	 */
	public void setClassLvl(int lvl)
	{
		this.classLvl = lvl;
	}

	/**
	 * @return Returns the this.classMpAdd.
	 */
	@Deprecated
	public float getClassMpAdd()
	{
		return this.classMpAdd;
	}

	/**
	 * @param mpAdd The this.classMpAdd to set.
	 */
	public void setClassMpAdd(float mpAdd)
	{
		this.classMpAdd = mpAdd;
	}

	/**
	 * @return Returns the this.classMpBase.
	 */
	@Deprecated
	public float getClassMpBase()
	{
		return this.classMpBase;
	}

	/**
	 * @param mpBase The this.classMpBase to set.
	 */
	public void setClassMpBase(float mpBase)
	{
		this.classMpBase = mpBase;
	}

	/**
	 * @return Returns the this.classMpModifier.
	 */
	@Deprecated
	public float getClassMpModifier()
	{
		return this.classMpModifier;
	}

	/**
	 * @param mpModifier The this.classMpModifier to set.
	 */
	public void setClassMpModifier(float mpModifier)
	{
		this.classMpModifier = mpModifier;
	}
}
