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

package instances.DimensionalDoor.Baylor;

import java.util.ArrayList;
import java.util.List;

import ai.group_template.L2AttackableAIScript;
import instances.DimensionalDoor.DimensionalDoor;
import l2server.Config;
import l2server.gameserver.ai.CtrlIntention;
import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.instancemanager.InstanceManager;
import l2server.gameserver.instancemanager.InstanceManager.InstanceWorld;
import l2server.gameserver.model.L2Abnormal;
import l2server.gameserver.model.L2Skill;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.entity.Instance;
import l2server.gameserver.model.quest.QuestTimer;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.MagicSkillUse;
import l2server.gameserver.network.serverpackets.NpcSay;
import l2server.gameserver.network.serverpackets.SocialAction;
import l2server.gameserver.network.serverpackets.SpecialCamera;
import l2server.gameserver.network.serverpackets.SystemMessage;
import l2server.log.Log;
import l2server.util.Rnd;

/**
 * @author LasTravel
 *         <p>
 *         Source: -https://www.youtube.com/watch?v=huIIQ89tmgM
 *         -http://boards.lineage2.com/showthread.php?t=265663
 */

public class Baylor extends L2AttackableAIScript
{
    private static final boolean debug = false;
    private static final String qn = "Baylor";

    //Config
    private static final int alarmReuse = 2;

    //Ids
    private static final int prisonKey = 10015;
    private static final int instanceTemplateId = 166;
    private static final int alarmId = 18474;
    private static final int cameraMinionId = 29104;
    private static final int baylorId = 29213;
    private static final int cameraId = 29120;

    private static final int[][] alarmSpawns = {
            {153571, 142858, -12744, 48779},
            {152777, 142075, -12744, 82},
            {153573, 141275, -12744, 16219},
            {154359, 142075, -12744, 33274}
    };

    //Others
    private static final L2Skill baylorBerserk = SkillTable.getInstance().getInfo(5224, 1);
    private static final L2Skill baylorInvincibility = SkillTable.getInstance().getInfo(5225, 1);

    private class BaylorWorld extends InstanceWorld
    {
        private L2Npc baylorOne;
        private L2Npc baylorTwo;
        private L2Npc camera;
        private List<L2Npc> cameraMinions;
        private ArrayList<L2PcInstance> rewardedPlayers;

        private BaylorWorld()
        {
            cameraMinions = new ArrayList<L2Npc>();
            rewardedPlayers = new ArrayList<L2PcInstance>();
        }
    }

    public Baylor(int questId, String name, String descr)
    {
        super(questId, name, descr);

        addTalkId(DimensionalDoor.getNpcManagerId());
        addStartNpc(DimensionalDoor.getNpcManagerId());

        addAttackId(baylorId);
        addKillId(baylorId);

        addKillId(alarmId);
    }

    @Override
    public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
    {
        if (debug)
        {
            Log.warning(getName() + ": onAdvEvent: " + event);
        }

        InstanceWorld wrld = null;
        if (npc != null)
        {
            wrld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
        }
        else if (player != null)
        {
            wrld = InstanceManager.getInstance().getPlayerWorld(player);
        }
        else
        {
            Log.warning(getName() + ": onAdvEvent: Unable to get world.");
            return null;
        }

        if (wrld != null && wrld instanceof BaylorWorld)
        {
            BaylorWorld world = (BaylorWorld) wrld;
            if (event.equalsIgnoreCase("stage_1_start"))
            {
                InstanceManager.getInstance().stopWholeInstance(world.instanceId);

                world.baylorOne =
                        addSpawn(baylorId, 153751, 142333, -12738, 10617, false, 0, false, world.instanceId);
                world.baylorOne.setIsParalyzed(true);

                world.baylorTwo =
                        addSpawn(baylorId, 153832, 141930, -12738, 60191, false, 0, false, world.instanceId);
                world.baylorTwo.setIsParalyzed(true);

                world.camera = addSpawn(cameraId, 153273, 141400, -12738, 10800, false, 0, false, world.instanceId);
                world.camera.broadcastPacket(
                        new SpecialCamera(world.camera.getObjectId(), 700, -45, 160, 500, 15200, 0, 0, 1, 0));

                startQuestTimer("stage_1_spawn_camera_minions", 2000, world.camera, null);
            }
            else if (event.equalsIgnoreCase("stage_1_spawn_camera_minions"))
            {
                for (int i = 0; i < 10; i++)
                {
                    int radius = 300;
                    int x = (int) (radius * Math.cos(i * 0.618));
                    int y = (int) (radius * Math.sin(i * 0.618));

                    L2Npc mob = addSpawn(cameraMinionId, 153571 + x, 142075 + y, -12737, 0, false, 0, false,
                            world.instanceId);
                    mob.setIsParalyzed(true);
                    world.cameraMinions.add(mob);
                }
                startQuestTimer("stage_1_camera_1", 200, world.camera, null);
            }
            else if (event.equalsIgnoreCase("stage_1_camera_1"))
            {
                world.baylorOne.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                world.baylorOne.broadcastPacket(new SocialAction(world.baylorOne.getObjectId(), 1));

                world.baylorTwo.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                world.baylorTwo.broadcastPacket(new SocialAction(world.baylorTwo.getObjectId(), 1));

                startQuestTimer("stage_1_camera_2", 11000, world.camera, null);
                startQuestTimer("stage_1_camera_3", 19000, world.camera, null);
            }
            else if (event.equalsIgnoreCase("stage_1_camera_2"))
            {
                world.baylorOne.broadcastPacket(
                        new SpecialCamera(world.baylorOne.getObjectId(), 500, -45, 170, 5000, 9000, 0, 0, 1, 0));
            }
            else if (event.equalsIgnoreCase("stage_1_camera_3"))
            {
                world.baylorOne.broadcastPacket(
                        new SpecialCamera(world.baylorOne.getObjectId(), 300, 0, 120, 2000, 5000, 0, 0, 1, 0));
                world.baylorOne.broadcastPacket(new SocialAction(world.baylorOne.getObjectId(), 3));

                world.baylorTwo.broadcastPacket(new SocialAction(world.baylorTwo.getObjectId(), 3));

                startQuestTimer("stage_1_camera_4", 4000, world.camera, null);
            }
            else if (event.equalsIgnoreCase("stage_1_camera_4"))
            {
                world.baylorOne.broadcastPacket(
                        new SpecialCamera(world.baylorOne.getObjectId(), 747, 0, 160, 2000, 3000, 0, 0, 1, 0));
                world.baylorOne
                        .broadcastPacket(new MagicSkillUse(world.baylorOne, world.baylorOne, 5402, 1, 2000, 0, 0));

                world.baylorTwo
                        .broadcastPacket(new MagicSkillUse(world.baylorTwo, world.baylorTwo, 5402, 1, 2000, 0, 0));

                startQuestTimer("stage_2_start", 2000, world.camera, null);
            }
            else if (event.equalsIgnoreCase("stage_2_start"))
            {
                world.camera.decayMe();
                world.baylorOne.setIsParalyzed(false);
                world.baylorTwo.setIsParalyzed(false);

                for (L2Npc mob : world.cameraMinions)
                {
                    mob.doDie(mob);
                }

                world.cameraMinions.clear();

                InstanceManager.getInstance().startWholeInstance(world.instanceId);

                startQuestTimer("stage_all_spawn_alarm", 60000, world.baylorOne, null);
            }
            else if (event.equalsIgnoreCase("stage_all_spawn_alarm"))
            {
                if (world.baylorOne.isDead() && world.baylorTwo.isDead())
                {
                    return "";
                }

                if (Rnd.nextBoolean())
                {
                    int[] rndAlarm = alarmSpawns[Rnd.get(alarmSpawns.length)];
                    L2Npc alarm =
                            addSpawn(alarmId, rndAlarm[0], rndAlarm[1], rndAlarm[2], rndAlarm[3], false, 0, false,
                                    world.instanceId);
                    alarm.broadcastPacket(new NpcSay(alarm.getObjectId(), 0, alarm.getTemplate().TemplateId, 1800031));

                    startQuestTimer("stage_all_alarm_check", 15000, alarm, null);
                }
                else
                {
                    startQuestTimer("stage_all_spawn_alarm", 60000, npc, null);
                }
            }
            else if (event.equalsIgnoreCase("stage_all_alarm_check"))
            {
                //At this point the alarm hasn't been killed
                startQuestTimer("stage_all_spawn_alarm", alarmReuse * 60000, npc, null);

                npc.decayMe();

                if (world.baylorOne != null && !world.baylorOne.isDead())
                {
                    baylorBerserk.getEffects(world.baylorOne, world.baylorOne);
                }
                if (world.baylorTwo != null && !world.baylorTwo.isDead())
                {
                    baylorBerserk.getEffects(world.baylorTwo, world.baylorTwo);
                }
            }
        }

        if (event.equalsIgnoreCase("enterToInstance"))
        {
            try
            {
                enterInstance(player);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public final String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
    {
        if (debug)
        {
            Log.warning(getName() + ": onAttack: " + npc.getName());
        }

        final InstanceWorld tmpWorld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
        if (tmpWorld instanceof BaylorWorld)
        {
            if (npc.getNpcId() == baylorId)
            {
                L2Abnormal ab = npc.getFirstEffect(baylorInvincibility);
                if (ab != null)
                {
                    if (attacker.isBehindTarget())
                    {
                        if (Rnd.get(100) == 50)
                        {
                            ab.exit();
                        }
                    }
                }
            }
        }
        return "";
    }

    @Override
    public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
    {
        if (debug)
        {
            Log.warning(getName() + ": onKill: " + npc.getName());
        }

        InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
        if (tmpworld instanceof BaylorWorld)
        {
            BaylorWorld world = (BaylorWorld) tmpworld;
            if (npc.getNpcId() == baylorId)
            {
                npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getTemplate().TemplateId, 1800067));
                if (world.baylorOne.isDead() && world.baylorTwo.isDead())
                {
                    if (player.isInParty())
                    {
                        for (L2PcInstance pMember : player.getParty().getPartyMembers())
                        {
                            if (pMember == null || pMember.getInstanceId() != world.instanceId)
                            {
                                continue;
                            }

                            //Reward
                            if (InstanceManager.getInstance().canGetUniqueReward(pMember, world.rewardedPlayers))
                            {
                                world.rewardedPlayers.add(pMember);
                                pMember.addItem(qn, DimensionalDoor.getDimensionalDoorRewardId(),
                                        Rnd.get(8 * DimensionalDoor.getDimensionalDoorRewardRate(),
                                                13 * DimensionalDoor.getDimensionalDoorRewardRate()), player, true);
                            }
                            else
                            {
                                pMember.sendMessage("Nice attempt, but you already got a reward!");
                            }
                        }
                    }
                    InstanceManager.getInstance().setInstanceReuse(world.instanceId, instanceTemplateId, 6, 30);
                    InstanceManager.getInstance().finishInstance(world.instanceId, true);
                }
            }
            else if (npc.getNpcId() == alarmId)
            {
                QuestTimer activityTimer = getQuestTimer("stage_all_alarm_check", npc, null);
                if (activityTimer != null)
                {
                    activityTimer.cancel();
                    startQuestTimer("stage_all_spawn_alarm", alarmReuse * 60000, npc, null);
                }
            }
        }
        return "";
    }

    @Override
    public final String onTalk(L2Npc npc, L2PcInstance player)
    {
        if (debug)
        {
            Log.warning(getName() + ": onTalk: " + player.getName());
        }

        if (npc.getNpcId() == DimensionalDoor.getNpcManagerId())
        {
            return qn + ".html";
        }

        return super.onTalk(npc, player);
    }

    private final synchronized void enterInstance(L2PcInstance player)
    {
        InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
        if (world != null)
        {
            if (!(world instanceof BaylorWorld))
            {
                player.sendPacket(
                        SystemMessage.getSystemMessage(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER));
                return;
            }

            Instance inst = InstanceManager.getInstance().getInstance(world.instanceId);
            if (inst != null)
            {
                if (inst.getInstanceEndTime() > 300600 && world.allowed.contains(player.getObjectId()))
                {
                    player.setInstanceId(world.instanceId);
                    player.teleToLocation(153568, 142867, -12744, 49098, true);
                }
            }
            return;
        }
        else
        {
            if (!debug && !InstanceManager.getInstance()
                    .checkInstanceConditions(player, instanceTemplateId, 7, 7, 99, Config.MAX_LEVEL))
            {
                return;
            }

            final int instanceId = InstanceManager.getInstance().createDynamicInstance(qn + ".xml");
            world = new BaylorWorld();
            world.instanceId = instanceId;
            world.status = 0;

            InstanceManager.getInstance().addWorld(world);

            List<L2PcInstance> allPlayers = new ArrayList<L2PcInstance>();
            if (debug)
            {
                allPlayers.add(player);
            }
            else
            {
                allPlayers.addAll(player.getParty().getPartyMembers());
            }

            for (L2PcInstance enterPlayer : allPlayers)
            {
                if (enterPlayer == null)
                {
                    continue;
                }

                world.allowed.add(enterPlayer.getObjectId());

                enterPlayer.deleteAllItemsById(prisonKey);

                enterPlayer.stopAllEffectsExceptThoseThatLastThroughDeath();
                enterPlayer.setInstanceId(instanceId);
                enterPlayer.teleToLocation(153568, 142867, -12744, 49098, true);
            }

            startQuestTimer("stage_1_start", 60000, null, player);

            Log.fine(getName() + ": instance started: " + instanceId + " created by player: " + player.getName());
            return;
        }
    }

    @Override
    public int getOnKillDelay(int npcId)
    {
        return 0;
    }

    public static void main(String[] args)
    {
        new Baylor(-1, qn, "instances/DimensionalDoor");
    }
}
