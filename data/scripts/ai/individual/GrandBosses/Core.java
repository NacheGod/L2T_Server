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

package ai.individual.GrandBosses;

import ai.group_template.L2AttackableAIScript;
import l2server.gameserver.instancemanager.GrandBossManager;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2GrandBossInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.quest.QuestTimer;
import l2server.gameserver.network.serverpackets.NpcSay;
import l2server.gameserver.network.serverpackets.PlaySound;
import l2server.log.Log;
import l2server.util.Rnd;

/**
 * @author LasTravel
 *         <p>
 *         Core AI (Based on DrLecter & Emperorc work)
 */

public class Core extends L2AttackableAIScript
{
    //Quest
    private static final boolean _debug = false;

    //Id's
    private static final int _coreId = 29006;

    //Vars
    private static long _LastAction;
    private static boolean _alreadyAttacked = false;

    public Core(int id, String name, String descr)
    {
        super(id, name, descr);

        addAttackId(_coreId);
        addKillId(_coreId);

        //Unlock
        startQuestTimer("unlock_core", GrandBossManager.getInstance().getUnlockTime(_coreId), null, null);
    }

    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
    {
        if (_debug)
        {
            Log.warning(getName() + ": onAdvEvent: " + event);
        }

        if (event.equalsIgnoreCase("unlock_core"))
        {
            _alreadyAttacked = false;

            L2GrandBossInstance core = (L2GrandBossInstance) addSpawn(_coreId, 17726, 108915, -6480, 0, false, 0);

            GrandBossManager.getInstance().addBoss(core);

            GrandBossManager.getInstance().setBossStatus(_coreId, GrandBossManager.getInstance().ALIVE);
        }
        else if (event.equalsIgnoreCase("end_core"))
        {
            QuestTimer activityTimer = getQuestTimer("check_activity_task", null, null);
            if (activityTimer != null)
            {
                activityTimer.cancel();
            }

            _alreadyAttacked = false;

            if (GrandBossManager.getInstance().getBossStatus(_coreId) != GrandBossManager.getInstance().DEAD)
            {
                GrandBossManager.getInstance().setBossStatus(_coreId, GrandBossManager.getInstance().ALIVE);
            }
        }
        else if (event.equalsIgnoreCase("check_activity_task"))
        {
            if (!GrandBossManager.getInstance().isActive(_coreId, _LastAction))
            {
                notifyEvent("end_core", null, null);
            }
        }
        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
    {
        if (_debug)
        {
            Log.warning(getName() + ": onAttack: " + npc.getName());
        }

        if (npc.getNpcId() == _coreId)
        {
            _LastAction = System.currentTimeMillis();

            if (GrandBossManager.getInstance().getBossStatus(_coreId) == GrandBossManager.getInstance().ALIVE)
            {
                GrandBossManager.getInstance().setBossStatus(_coreId, GrandBossManager.getInstance().FIGHTING);

                startQuestTimer("check_activity_task", 60000, null, null, true);
            }

            if (_alreadyAttacked)
            {
                if (Rnd.get(100) == 0)
                {
                    npc.broadcastPacket(
                            new NpcSay(npc.getObjectId(), 0, npc.getNpcId(), 1000003)); // Removing intruders.
                }
            }
            else
            {
                _alreadyAttacked = true;
                npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(),
                        1000001)); // A non-permitted target has been discovered.
                npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getNpcId(),
                        1000002)); // Intruder removal system initiated.
            }
        }
        return super.onAttack(npc, attacker, damage, isPet);
    }

    @Override
    public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
    {
        if (_debug)
        {
            Log.warning(getName() + ": onKill: " + npc.getName());
        }

        if (npc.getNpcId() == _coreId)
        {
            GrandBossManager.getInstance().notifyBossKilled(_coreId);

            notifyEvent("end_core", null, null);

            npc.broadcastPacket(new PlaySound(1, "BS02_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
            npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, _coreId, 1000004)); // A fatal error has occurred.
            npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, _coreId, 1000005)); // System is being shut down...
            npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, _coreId, 1000006)); // ......

            //Exit Cubics
            addSpawn(31842, 16502, 110165, -6394, 0, false, 900000);
            addSpawn(31842, 18948, 110166, -6397, 0, false, 900000);

            startQuestTimer("unlock_core", GrandBossManager.getInstance().getUnlockTime(_coreId), null, null);
        }
        return super.onKill(npc, killer, isPet);
    }

    public static void main(String[] args)
    {
        new Core(-1, "Core", "ai/individual/GrandBosses");
    }
}
