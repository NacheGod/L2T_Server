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

import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.stats.Env;
import l2server.gameserver.stats.funcs.Lambda;
import l2server.gameserver.templates.skills.L2AbnormalType;
import l2server.gameserver.templates.skills.L2EffectTemplate;
import l2server.gameserver.templates.skills.L2EffectType;

/**
 * This class ...
 *
 * @version $Revision: 1.1.2.1.2.12 $ $Date: 2005/04/11 10:06:07 $
 */
public abstract class L2Effect
{

	public enum EffectState
	{
		CREATED, ACTING, FINISHING
	}

	//member this.effector is the instance of L2Character that cast/used the spell/skill that is
	//causing this effect.  Do not confuse with the instance of L2Character that
	//is being affected by this effect.
	private final L2Character effector;

	//member this.effected is the instance of L2Character that was affected
	//by this effect.  Do not confuse with the instance of L2Character that
	//casted/used this effect.
	private final L2Character effected;

	// the value of an update
	private final Lambda lambda;

	//the skill that was used.
	private final L2Skill skill;

	private L2Abnormal abnormal;
	private L2EffectTemplate template;

	public boolean preventExitUpdate;

	/**
	 * <font color="FF0000"><b>WARNING: scheduleEffect nolonger inside constructor</b></font><br>
	 * So you must call it explicitly
	 */
	protected L2Effect(Env env, L2EffectTemplate template)
	{
		this.skill = env.skill;
		//_item = env.item == null ? null : env.item.getItem();
		this.template = template;
		this.effected = env.target;
		this.effector = env.player;
		this.lambda = template.lambda;
	}

	/**
	 * Special constructor to "steal" buffs. Must be implemented on
	 * every child class that can be stolen.<br><br>
	 * <p>
	 * <font color="FF0000"><b>WARNING: scheduleEffect nolonger inside constructor</b></font>
	 * <br>So you must call it explicitly
	 *
	 * @param env
	 * @param effect
	 */
	protected L2Effect(Env env, L2Effect effect)
	{
		this.template = effect.template;
		this.skill = env.skill;
		this.effected = env.target;
		this.effector = env.player;
		this.lambda = this.template.lambda;
	}

	public final double calc()
	{
		Env env = new Env();
		env.player = this.effector;
		env.target = this.effected;
		env.skill = this.skill;
		return this.lambda.calc(env);
	}

	public final L2Skill getSkill()
	{
		return this.skill;
	}

	public final L2Character getEffector()
	{
		return this.effector;
	}

	public final L2Character getEffected()
	{
		return this.effected;
	}

	/**
	 * Stop the L2Effect task and send Server->Client update packet.<BR><BR>
	 * <p>
	 * <B><U> Actions</U> :</B><BR><BR>
	 * <li>Cancel the effect in the the abnormal effect map of the L2Character </li>
	 * <li>Stop the task of the L2Effect, remove it and update client magic icon </li><BR><BR>
	 */
	public final void exit()
	{
		this.exit(false);
	}

	public final void exit(boolean preventUpdate)
	{
		preventExitUpdate = preventUpdate;
	}

	/**
	 * returns effect type
	 */
	public L2EffectType getEffectType()
	{
		return L2EffectType.NONE;
	}

	public L2AbnormalType getAbnormalType()
	{
		return L2AbnormalType.NONE;
	}

	public long getEffectMask()
	{
		return getEffectType().getMask();
	}

	/**
	 * Notify started
	 */
	public boolean onStart()
	{
		return true;
	}

	/**
	 * Cancel the effect in the the abnormal effect map of the effected L2Character.<BR><BR>
	 */
	public void onExit()
	{
	}

	/**
	 * Return true for continuation of this effect
	 */
	public abstract boolean onActionTime();

	public int getLevel()
	{
		return getSkill().getLevelHash();
	}

	public L2Abnormal getAbnormal()
	{
		return this.abnormal;
	}

	public void setAbnormal(L2Abnormal abnormal)
	{
		this.abnormal = abnormal;
	}

	public L2EffectTemplate getTemplate()
	{
		return this.template;
	}

	public boolean isSelfEffectType()
	{
		return false;
	}

	/**
	 * Return true if effect itself can be stolen
	 *
	 * @return
	 */
	protected boolean effectCanBeStolen()
	{
		return false;
	}

	@Override
	public String toString()
	{
		return "L2Effect [_skill=" + this.skill + ", _type=" + this.getClass().getCanonicalName() + "]";
	}
}
