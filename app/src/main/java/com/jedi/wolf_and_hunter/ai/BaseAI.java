package com.jedi.wolf_and_hunter.ai;

import android.graphics.Point;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myViews.tempView.Trajectory;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.util.Iterator;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/4/29.
 */

public class BaseAI extends TimerTask {
    public int intent = 2;
    public static final int INTENT_DAZE = 0;//发呆
    public static final int INTENT_MOVE = 1;//移动，暂定主要用来逃跑，此状态下不主动攻击
    public static final int INTENT_HUNT = 2;//搜寻猎物，遇到主动攻击
    public static final int INTENT_AMBUSH = 3;//埋伏，静止并在一定条件下主动攻击
    public static final int INTENT_ATTACK = 4;//主动攻击
    public static final int INTENT_TRACK_CHARACTER = 5;//追踪玩家
    public static final int INTENT_TRACK_TRAJECTORY = 6;//追踪弹道
    public int targetX = -1;
    public int targetY = -1;
    public int targetLastX = -1;
    public int targetLastY = -1;

    public BaseCharacterView targetCharacter;
    //    Thread facingThread;
    public float chanceAngle = 5;
    public int angleChangSpeed = 2;
    public static int mapWidth = 0;
    public static int mapHeight = 0;
    public BaseCharacterView bindingCharacter;
    public boolean hasDealTrackOnce = false;
    public Trajectory trackTrajectory;

    public BaseAI(BaseCharacterView character) {
        super();
        this.bindingCharacter = character;
        if (mapWidth == 0 || mapHeight == 0) {
            mapWidth = GameBaseAreaActivity.gameInfo.mapWidth;
            mapHeight = GameBaseAreaActivity.gameInfo.mapHeight;
        }
    }


//@Deprecated
//    public void addFacingThread() {
//        if (facingThread == null) {
//            facingThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (true) {
//                        if (bindingCharacter == null || bindingCharacter.isDead) {
//                            try {
//                                Thread.sleep(300);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            continue;
//                        }
//                        synchronized (bindingCharacter) {
//                            if (GameBaseAreaActivity.gameInfo.isStop == true)
//                                break;
//
//
//
//
////                        if(bindingCharacter.nowFacingAngle<0||bindingCharacter.nowFacingAngle>360)
////                            Log.i("","");
//                            if (targetFacingAngle < 0 && intent == INTENT_HUNT) {//这一句判断是否需要重新取targetFacingAngle
//                                targetFacingAngle = new Random().nextInt(360);
//
//                            }
//
//                            float relateAngle = targetFacingAngle - bindingCharacter.nowFacingAngle;
//                            if (Math.abs(relateAngle) > 180) {//处理旋转最佳方向
//                                if (relateAngle > 0)
//                                    relateAngle = relateAngle - 360;
//
//                                else
//                                    relateAngle = 360 - relateAngle;
//                            }
//                            if (Math.abs(relateAngle) > angleChangSpeed)
//                                relateAngle = Math.abs(relateAngle) / relateAngle * angleChangSpeed;
//
//                            bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + relateAngle;
//                            if (bindingCharacter.nowFacingAngle < 0)
//                                bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + 360;
//                            else if (bindingCharacter.nowFacingAngle > 360)
//                                bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle - 360;
//                            if (targetFacingAngle == bindingCharacter.nowFacingAngle && (intent == INTENT_HUNT))
//                                targetFacingAngle = -1;
//                        }
//                        try {
//                            Thread.sleep(50);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//            });
//            facingThread.setDaemon(true);
//            facingThread.start();
//        }
//    }

    @Override
    public void run() {
//        addFacingThread();
        if (GameBaseAreaActivity.gameInfo.isStop)
            return;
//        addFacingThread();
        decideWhatToDo();

//        if (bindingCharacter.attackCount == 0)
//            bindingCharacter.reloadAttackCount();
        if (intent == INTENT_DAZE) {
            return;
        } else if (intent == INTENT_HUNT) {
            hunt();
        } else if (intent == INTENT_ATTACK) {
            attack();
        } else if (intent == INTENT_TRACK_CHARACTER) {
            trackCharacter();
        } else if (intent == INTENT_TRACK_TRAJECTORY) {
            trackTrajectory();
        }

    }

    public synchronized void decideWhatToDo() {
        if (bindingCharacter == null) {
            return;
        }
//        boolean isDiscover = false;
        boolean isDiscoverByMe = false;
        boolean isInViewRange;
        intent = INTENT_HUNT;
        if (bindingCharacter.isDead) {
            reset();
        }
        for (BaseCharacterView character : GameBaseAreaActivity.gameInfo.allCharacters) {

            //忽略队友
            if (character == bindingCharacter || character.getTeamID() == bindingCharacter.getTeamID())
                continue;
            if (character.isDead == true) {
                continue;
            }

            isInViewRange = bindingCharacter.isInViewRange(character, bindingCharacter.nowViewRadius);

            if (isInViewRange == true) {
                if (character.nowHiddenLevel == 0)
                    isDiscoverByMe = true;
                else {
                    boolean isInForceViewRange = bindingCharacter.isInViewRange(character, bindingCharacter.nowForceViewRadius);
                    if (isInForceViewRange)
                        isDiscoverByMe = true;

                }
            }
            if (isDiscoverByMe == true) {//处理闯入本AI视觉范围的情况
                if (character.seeMeTeamIDs.contains(bindingCharacter.getTeamID())) {//已经被AI本队发现
                    if (character.theyDiscoverMe.contains(bindingCharacter) == false) {//第一发现人不是本AI
                        character.theyDiscoverMe.add(bindingCharacter);
                    }
                } else {//本AI是第一发现人
                    character.seeMeTeamIDs.add(bindingCharacter.getTeamID());
                    character.theyDiscoverMe.add(bindingCharacter);
                    if (bindingCharacter.getTeamID() == GameBaseAreaActivity.myCharacter.getTeamID())
                        character.isForceToBeSawByMe = true;

                }
            } else {//处理不在本AI视觉范围内的情况
                if (character.seeMeTeamIDs.contains(bindingCharacter.getTeamID())) {//已经被AI本队发现
                    if (character.theyDiscoverMe.contains(bindingCharacter)) {
                        character.theyDiscoverMe.remove(bindingCharacter);
                    }
                    boolean hasMyTeammate = false;
                    Iterator<BaseCharacterView> iterator = character.theyDiscoverMe.iterator();
                    while (iterator.hasNext()) {
                        BaseCharacterView c = iterator.next();
                        if (c.getTeamID() == bindingCharacter.getTeamID()) {
                            hasMyTeammate = true;
                            break;
                        }
                    }

                    if (hasMyTeammate == false) {
                        int index = character.seeMeTeamIDs.indexOf(bindingCharacter.getTeamID());
                        character.seeMeTeamIDs.remove(index);
                    }
                } else if (GameBaseAreaActivity.myCharacter != null && bindingCharacter.getTeamID() == GameBaseAreaActivity.myCharacter.getTeamID()) {
                    character.isForceToBeSawByMe = false;
                }


            }
            //对方比我方任何玩家发现
            if (character.seeMeTeamIDs.contains(bindingCharacter.getTeamID())) {
                reset();
                targetCharacter = character;
                intent = INTENT_ATTACK;
                return;
            } else {//对方此刻没被发现
                //如果对方是突然消失的话即展开追击
                if (targetCharacter != null) {
                    hasDealTrackOnce = false;
                    intent = INTENT_TRACK_CHARACTER;
                    continue;
                }
                //对方已经被执行过一次追击，但还没完成追击则继续追
                else if (hasDealTrackOnce == true || (targetLastX > 0 && targetLastY > 0)) {//处理被发现的目标突然消失的情况，执行追踪
                    intent = INTENT_TRACK_CHARACTER;
                    continue;

                }
            }

        }
        if (intent == INTENT_HUNT && trackTrajectory == null) {
            double minDistance = -1;
            Iterator<Trajectory> iterator = GameBaseAreaActivity.gameInfo.allTrajectories.iterator();
            while (iterator.hasNext()) {
                Trajectory trajectory = iterator.next();
                double distance = MyMathsUtils.getDistance(trajectory.fromPointRelateParent, new Point(bindingCharacter.centerX, bindingCharacter.centerY));
                minDistance = Math.min(minDistance, distance);
                if (minDistance == -1 || minDistance == distance)
                    trackTrajectory = trajectory;
            }


        }
        if (trackTrajectory != null) {
            intent = INTENT_TRACK_TRAJECTORY;
        }

        if (targetCharacter != null && targetCharacter.isDead == true) {
            targetCharacter = null;
            intent = INTENT_HUNT;
            hasDealTrackOnce = false;
        }


    }

    public void trackTrajectory() {

    }

    public void trackCharacter() {

    }

    public void reset() {
        targetCharacter = null;
        targetLastX = -1;
        targetLastY = -1;
        targetX = -1;
        targetY = -1;
        targetLastX = -1;
        targetLastY = -1;
        trackTrajectory = null;
        hasDealTrackOnce = false;
        bindingCharacter.targetFacingAngle=-1;
        bindingCharacter.isStay = false;
    }


    public void attack() {

    }

    public void hunt() {

    }

    public void escape() {
    }
}