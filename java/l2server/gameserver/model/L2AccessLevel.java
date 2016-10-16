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

import l2server.gameserver.datatables.AccessLevels;
import l2server.log.Log;
import lombok.Getter;

/**
 * @author FBIagent<br>
 */
public class L2AccessLevel
{
	/**
	 * The access level<br>
	 */
	private int accessLevel = 0;
	/**
	 * The access level name<br>
	 */
	@Getter private String name = null;
	/**
	 * Child access levels
	 */
	L2AccessLevel[] childsAccessLevel = null;
	/**
	 * Child access levels
	 */
	private String childs = null;
	/**
	 * The name color for the access level<br>
	 */
	@Getter private int nameColor = 0;
	/**
	 * The title color for the access level<br>
	 */
	@Getter private int titleColor = 0;
	/**
	 * Flag to determine if the access level has gm access<br>
	 */
	@Getter private boolean isGm = false;
	/**
	 * Flag for peace zone attack
	 */
	private boolean allowPeaceAttack = false;
	/**
	 * Flag for fixed res
	 */
	private boolean allowFixedRes = false;
	/**
	 * Flag for transactions
	 */
	private boolean allowTransaction = false;
	/**
	 * Flag for AltG commands
	 */
	private boolean allowAltG = false;
	/**
	 * Flag to give damage
	 */
	private boolean giveDamage = false;
	/**
	 * Flag to take aggro
	 */
	private boolean takeAggro = false;
	/**
	 * Flag to gain exp in party
	 */
	private boolean gainExp = false;

	/**
	 * Initializes members<br><br>
	 *
	 * @param accessLevel      as int<br>
	 * @param name             as String<br>
	 * @param nameColor        as int<br>
	 * @param titleColor       as int<br>
	 * @param childs           as String<br>
	 * @param isGm             as boolean<br>
	 * @param allowPeaceAttack as boolean<br>
	 * @param allowFixedRes    as boolean<br>
	 * @param allowTransaction as boolean<br>
	 * @param allowAltG        as boolean<br>
	 * @param giveDamage       as boolean<br>
	 * @param takeAggro        as boolean<br>
	 * @param gainExp          as boolean<br>
	 */
	public L2AccessLevel(int accessLevel, String name, int nameColor, int titleColor, String childs, boolean isGm, boolean allowPeaceAttack, boolean allowFixedRes, boolean allowTransaction, boolean allowAltG, boolean giveDamage, boolean takeAggro, boolean gainExp)
	{
		this.accessLevel = accessLevel;
		this.name = name;
		this.nameColor = nameColor;
		this.titleColor = titleColor;
		this.childs = childs;
		this.isGm = isGm;
		this.allowPeaceAttack = allowPeaceAttack;
		this.allowFixedRes = allowFixedRes;
		this.allowTransaction = allowTransaction;
		this.allowAltG = allowAltG;
		this.giveDamage = giveDamage;
		this.takeAggro = takeAggro;
		this.gainExp = gainExp;
	}

	/**
	 * Returns the access level<br><br>
	 *
	 * @return int: access level<br>
	 */
	public int getLevel()
	{
		return accessLevel;
	}

	/**
	 * Returns if the access level is allowed to attack in peace zone or not<br><br>
	 *
	 * @return boolean: true if the access level is allowed to attack in peace zone, otherwise false<br>
	 */
	public boolean allowPeaceAttack()
	{
		return allowPeaceAttack;
	}

	/**
	 * Retruns if the access level is allowed to use fixed res or not<br><br>
	 *
	 * @return: true if the access level is allowed to use fixed res, otherwise false<br>
	 */
	public boolean allowFixedRes()
	{
		return allowFixedRes;
	}

	/**
	 * Returns if the access level is allowed to perform transactions or not<br><br>
	 *
	 * @return boolean: true if access level is allowed to perform transactions, otherwise false<br>
	 */
	public boolean allowTransaction()
	{
		return allowTransaction;
	}

	/**
	 * Returns if the access level is allowed to use AltG commands or not<br><br>
	 *
	 * @return boolean: true if access level is allowed to use AltG commands, otherwise false<br>
	 */
	public boolean allowAltG()
	{
		return allowAltG;
	}

	/**
	 * Returns if the access level can give damage or not<br><br>
	 *
	 * @return boolean: true if the access level can give damage, otherwise false<br>
	 */
	public boolean canGiveDamage()
	{
		return giveDamage;
	}

	/**
	 * Returns if the access level can take aggro or not<br><br>
	 *
	 * @return boolean: true if the access level can take aggro, otherwise false<br>
	 */
	public boolean canTakeAggro()
	{
		return takeAggro;
	}

	/**
	 * Returns if the access level can gain exp or not<br><br>
	 *
	 * @return boolean: true if the access level can gain exp, otherwise false<br>
	 */
	public boolean canGainExp()
	{
		return gainExp;
	}

	/**
	 * Returns if the access level contains allowedAccess as child<br><br>
	 *
	 * @param accessLevel as AccessLevel<br><br>
	 * @return boolean: true if a child access level is equals to allowedAccess, otherwise false<br>
	 */
	public boolean hasChildAccess(L2AccessLevel accessLevel)
	{
		if (childsAccessLevel == null)
		{
			if (childs == null)
			{
				return false;
			}

			setChildAccess(childs);
			for (L2AccessLevel childAccess : childsAccessLevel)
			{
				if (childAccess != null &&
						(childAccess.getLevel() == accessLevel.getLevel() || childAccess.hasChildAccess(accessLevel)))
				{
					return true;
				}
			}
		}
		else
		{
			for (L2AccessLevel childAccess : childsAccessLevel)
			{
				if (childAccess != null &&
						(childAccess.getLevel() == accessLevel.getLevel() || childAccess.hasChildAccess(accessLevel)))
				{
					return true;
				}
			}
		}
		return false;
	}

	private void setChildAccess(String childs)
	{
		String[] childsSplit = childs.split(";");

		childsAccessLevel = new L2AccessLevel[childsSplit.length];

		for (int i = 0; i < childsSplit.length; ++i)
		{
			L2AccessLevel accessLevelInst = AccessLevels.getInstance().getAccessLevel(Integer.parseInt(childsSplit[i]));

			if (accessLevelInst == null)
			{
				Log.warning("AccessLevel: Undefined child access level " + childsSplit[i]);
				continue;
			}

			if (accessLevelInst.hasChildAccess(this))
			{
				Log.warning(
						"AccessLevel: Child access tree overlapping for " + name + " and " + accessLevelInst.getName());
				continue;
			}

			childsAccessLevel[i] = accessLevelInst;
		}
	}
}
