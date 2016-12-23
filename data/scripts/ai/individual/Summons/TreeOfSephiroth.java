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

package ai.individual.Summons;

import ai.group_template.L2AttackableAIScript;
import l2server.gameserver.GeoData;
import l2server.gameserver.ThreadPoolManager;
import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.model.L2Party;
import l2server.gameserver.model.actor.L2Summon;
import l2server.gameserver.model.actor.instance.L2PcInstance;

import java.util.concurrent.ScheduledFuture;

/**
 * @author LasTravel
 * @author Pere
 *         <p>
 *         Summon Tree of Sephiroth (skill id: 19210) AI
 */

public class TreeOfSephiroth extends L2AttackableAIScript
{
    private static final int _treeOfSephiroth = 15154;
    private static final int _blessingOfLifeId = 19219;

    public TreeOfSephiroth(int id, String name, String descr)
    {
        super(id, name, descr);

        addSpawnId(_treeOfSephiroth);
    }

    @Override
    public final String onSpawn(L2Summon npc)
    {
        TreeOfLifeAI ai = new TreeOfLifeAI(npc);

        ai.setSchedule(ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(ai, 5000, 10000));

        return null;
    }

    class TreeOfLifeAI implements Runnable
    {
        private L2Summon _treeOfLife;
        private L2PcInstance _owner;
        private ScheduledFuture<?> _schedule = null;

        protected TreeOfLifeAI(L2Summon npc)
        {
            _treeOfLife = npc;
            _owner = npc.getOwner();
        }

        public void setSchedule(ScheduledFuture<?> schedule)
        {
            _schedule = schedule;
        }

        @Override
        public void run()
        {
            if (_treeOfLife == null || _treeOfLife.isDead() || !_owner.getSummons().contains(_treeOfLife))
            {
                if (_schedule != null)
                {
                    _schedule.cancel(true);
                    return;
                }
            }

            L2Party party = _treeOfLife.getOwner().getParty();

            if (party != null)
            {
                for (L2PcInstance player : party.getPartyMembers())
                {
                    if (player == null || !GeoData.getInstance().canSeeTarget(_treeOfLife, player))
                    {
                        continue;
                    }

                    SkillTable.getInstance()
                            .getInfo(_blessingOfLifeId, _treeOfLife.getSkillLevelHash(_blessingOfLifeId))
                            .getEffects(_treeOfLife, player);
                }
            }
            else
            {
                if (GeoData.getInstance().canSeeTarget(_treeOfLife, _owner))
                {
                    SkillTable.getInstance()
                            .getInfo(_blessingOfLifeId, _treeOfLife.getSkillLevelHash(_blessingOfLifeId))
                            .getEffects(_treeOfLife, _owner);
                }
            }
        }
    }

    public static void main(String[] args)
    {
        new TreeOfSephiroth(-1, "TreeOfSephiroth", "ai/individual");
    }
}
