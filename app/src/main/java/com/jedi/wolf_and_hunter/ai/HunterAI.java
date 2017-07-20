package com.jedi.wolf_and_hunter.ai;

import android.graphics.Point;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.util.Random;

/**
 * Created by Administrator on 2017/4/29.
 */

public class HunterAI extends BaseAI {
    public float targetFacingAngle = -1;//视觉想要转到的目标角度


    public HunterAI(BaseCharacterView character) {
        super(character);
        this.bindingCharacter = character;
        if (mapWidth == 0 || mapHeight == 0) {
            mapWidth = GameBaseAreaActivity.gameInfo.mapWidth;
            mapHeight = GameBaseAreaActivity.gameInfo.mapHeight;
        }
    }

    public void changeFacing() {
        if (bindingCharacter == null || bindingCharacter.isDead) {
            return;
        }
        if (GameBaseAreaActivity.gameInfo.isStop == true)
            return;
        synchronized (bindingCharacter) {

            if (targetFacingAngle < 0 && intent == INTENT_HUNT) {//这一句判断是否需要重新取targetFacingAngle
                targetFacingAngle = new Random().nextInt(360);

            }

            float relateAngle = targetFacingAngle - bindingCharacter.nowFacingAngle;
            if (Math.abs(relateAngle) > 180) {//处理旋转最佳方向
                if (relateAngle > 0)
                    relateAngle = relateAngle - 360;

                else
                    relateAngle = 360 - relateAngle;
            }
            if (Math.abs(relateAngle) > angleChangSpeed)
                relateAngle = Math.abs(relateAngle) / relateAngle * angleChangSpeed;

            bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + relateAngle;
            if (bindingCharacter.nowFacingAngle < 0)
                bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + 360;
            else if (bindingCharacter.nowFacingAngle > 360)
                bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle - 360;
            if (targetFacingAngle == bindingCharacter.nowFacingAngle && intent == INTENT_HUNT)
                targetFacingAngle = -1;
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

        if (bindingCharacter.attackCount == 0)
            bindingCharacter.isReloadingAttack=true;
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
        changeFacing();
        if (bindingCharacter.offX != 0 || bindingCharacter.offY != 0) {
            bindingCharacter.needMove = true;
        } else {
            bindingCharacter.needMove = false;
        }
    }

    public synchronized void decideWhatToDo() {
        super.decideWhatToDo();
//
////        boolean isDiscover = false;
//        boolean isDiscoverByMe = false;
//        boolean isInViewRange = false;
//        intent = INTENT_HUNT;
//        if (bindingCharacter.isDead) {
//            reset();
//        }
//        for (BaseCharacterView character : GameBaseAreaActivity.gameInfo.allCharacters) {
//
//            if (bindingCharacter == null) {
//                return;
//            }
//            //忽略队友
//            if (character == bindingCharacter || character.getTeamID() == bindingCharacter.getTeamID())
//                continue;
//            if (character.isDead == true) {
//                continue;
//            }
//
//            isInViewRange = bindingCharacter.isInViewRange(character, bindingCharacter.nowViewRadius);
//
//            if (isInViewRange == true) {
//                if (character.nowHiddenLevel == 0)
//                    isDiscoverByMe = true;
//                else {
//                    boolean isInForceViewRange = bindingCharacter.isInViewRange(character, bindingCharacter.nowForceViewRadius);
//                    if (isInForceViewRange)
//                        isDiscoverByMe = true;
//
//                }
//            }
//            if (isDiscoverByMe == true) {//处理闯入本AI视觉范围的情况
//                if (character.seeMeTeamIDs.contains(bindingCharacter.getTeamID())) {//已经被AI本队发现
//                    if (character.theyDiscoverMe.contains(bindingCharacter) == false) {//第一发现人不是本AI
//                        character.theyDiscoverMe.add(bindingCharacter);
//                    }
//                } else {//本AI是第一发现人
//                    character.seeMeTeamIDs.add(bindingCharacter.getTeamID());
//                    character.theyDiscoverMe.add(bindingCharacter);
//                    if (GameBaseAreaActivity.myCharacter!=null&&bindingCharacter.getTeamID() == GameBaseAreaActivity.myCharacter.getTeamID())
//                        character.isForceToBeSawByMe = true;
//
//                }
//            } else {//处理不在本AI视觉范围内的情况
//                if (character.seeMeTeamIDs.contains(bindingCharacter.getTeamID())) {//已经被AI本队发现
//                    if (character.theyDiscoverMe.contains(bindingCharacter)) {
//                        character.theyDiscoverMe.remove(bindingCharacter);
//                    }
//                    boolean hasMyTeammate = false;
//                    for (BaseCharacterView c : character.theyDiscoverMe) {
//                        if (c.getTeamID() == bindingCharacter.getTeamID()) {
//                            hasMyTeammate = true;
//                            break;
//                        }
//                    }
//                    if (hasMyTeammate == false) {
//                        character.seeMeTeamIDs.remove(bindingCharacter.getTeamID());
//                    }
//                } else if (GameBaseAreaActivity.myCharacter != null && bindingCharacter.getTeamID() == GameBaseAreaActivity.myCharacter.getTeamID()) {
//                    character.isForceToBeSawByMe = false;
//                }
//
//
//            }
//            //对方比我方任何玩家发现
//            if (character.seeMeTeamIDs.contains(bindingCharacter.getTeamID())) {
//                reset();
//                targetCharacter = character;
//                intent = INTENT_ATTACK;
//                return;
//            } else {//对方此刻没被发现
//                //如果对方是突然消失的话即展开追击
//                if (targetCharacter != null) {
//                    hasDealTrackOnce = false;
//                    intent = INTENT_TRACK_CHARACTER;
//                    continue;
//                }
//                //对方已经被执行过一次追击，但还没完成追击则继续追
//                else if (hasDealTrackOnce == true || (targetLastX > 0 && targetLastY > 0)) {//处理被发现的目标突然消失的情况，执行追踪
//                    intent = INTENT_TRACK_CHARACTER;
//                    continue;
//
//                }
//            }
//            //下面这句估计测试时有用
//            if (GameBaseAreaActivity.myCharacter == null)
//                character.isForceToBeSawByMe = true;
//        }
//        if (intent == INTENT_HUNT && trackTrajectory == null) {
//            double minDistance = -1;
//            for (Trajectory trajectory : GameBaseAreaActivity.gameInfo.allTrajectories) {
//                if (trajectory.createCharacter.getTeamID() == bindingCharacter.getTeamID())
//                    continue;
//                double distance = MyMathsUtils.getDistance(trajectory.fromPointRelateParent, new Point(bindingCharacter.centerX, bindingCharacter.centerY));
//                minDistance = Math.min(minDistance, distance);
//                if (minDistance == -1 || minDistance == distance)
//                    trackTrajectory = trajectory;
//            }
//
//        }
//        if (trackTrajectory != null) {
//            intent = INTENT_TRACK_TRAJECTORY;
//        }
//
//
//        if (targetCharacter != null && targetCharacter.isDead == true) {
//            targetCharacter = null;
//            intent = INTENT_HUNT;
//            hasDealTrackOnce = false;
//        }


    }

    public void trackTrajectory() {
        synchronized (bindingCharacter) {
            if (trackTrajectory == null) {
                reset();
                return;
            }

            int searchRelateX = trackTrajectory.fromPointRelateParent.x - bindingCharacter.centerX;
            int searchRelateY = trackTrajectory.fromPointRelateParent.y - bindingCharacter.centerY;
            float searchToAngle = 0;
            if(searchRelateX==0&&searchRelateY==0){
                reset();
                return;
            }

            try {
                searchToAngle = MyMathsUtils.getAngleBetweenXAxus(searchRelateX, searchRelateY);
            } catch (Exception e) {
                e.printStackTrace();
            }
            targetFacingAngle = searchToAngle;

            int nowDistance = (int) MyMathsUtils.getDistance(trackTrajectory.fromPointRelateParent,
                    new Point(bindingCharacter.centerX, bindingCharacter.centerY));
            if (nowDistance > bindingCharacter.nowForceViewRadius) {
                targetX = trackTrajectory.fromPointRelateParent.x;
                targetY = trackTrajectory.fromPointRelateParent.y;

            } else {
                targetX = bindingCharacter.centerX;
                targetY = bindingCharacter.centerY;
            }


            if (bindingCharacter.nowFacingAngle == targetFacingAngle && targetX == bindingCharacter.centerX && targetY == bindingCharacter.centerY) {
                trackTrajectory = null;
                reset();

            } else {
                bindingCharacter.offX = targetX - bindingCharacter.centerX;
                bindingCharacter.offY = targetY - bindingCharacter.centerY;
            }
        }
    }

    public void trackCharacter() {
        synchronized (bindingCharacter) {
            if (hasDealTrackOnce == false) {
                if (targetCharacter == null || targetCharacter.isDead == true) {
                    reset();
                    return;
                }
                int searchRelateX = targetCharacter.centerX - bindingCharacter.centerX;
                int searchRelateY = targetCharacter.centerY - bindingCharacter.centerY;
                float searchToAngle = 0;
                if(searchRelateX==0&&searchRelateY==0){
                    reset();
                    return;
                }
                try {
                    searchToAngle = MyMathsUtils.getAngleBetweenXAxus(searchRelateX, searchRelateY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                targetFacingAngle = searchToAngle;

                int nowDistance = (int) MyMathsUtils.getDistance(new Point(targetCharacter.centerX, targetCharacter.centerY)
                        , new Point(bindingCharacter.centerX, bindingCharacter.centerY));
                if (nowDistance > bindingCharacter.nowForceViewRadius) {
                    targetX = targetLastX;
                    targetY = targetLastY;

                } else {
                    targetX = bindingCharacter.centerX;
                    targetY = bindingCharacter.centerY;
                }
                targetLastX = -1;
                targetLastY = -1;
                targetCharacter = null;
            }
            if (hasDealTrackOnce == false)
                hasDealTrackOnce = true;

            if (bindingCharacter.nowFacingAngle == targetFacingAngle && targetX == bindingCharacter.centerX & targetY == bindingCharacter.centerY) {
                hasDealTrackOnce = false;

            } else {
                bindingCharacter.offX = targetX - bindingCharacter.centerX;
                bindingCharacter.offY = targetY - bindingCharacter.centerY;
            }
        }
    }

    public void reset() {
        super.reset();
        targetFacingAngle = -1;

    }


    public void attack() {
        boolean isChance = false;
        synchronized (bindingCharacter) {
            if (targetCharacter == null || targetCharacter.isDead == true) {
                reset();
                return;
            }
            targetLastX = targetCharacter.centerX;
            targetLastY = targetCharacter.centerY;
            int relateX = targetCharacter.centerX - bindingCharacter.centerX;
            int relateY = targetCharacter.centerY - bindingCharacter.centerY;
            if(relateX==0&&relateY==0){
                reset();
                return;
            }
            double distance = Math.sqrt(relateX * relateX + relateY * relateY);
            float angle = 0;
            try {
                angle = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);
            } catch (Exception e) {
                e.printStackTrace();
            }
            targetFacingAngle = angle;

            float relateAngle = targetFacingAngle - bindingCharacter.nowFacingAngle;
            if (Math.abs(relateAngle) > 360 - chanceAngle) {
                bindingCharacter.offX = 0;
                bindingCharacter.offY = 0;
                isChance = true;
            } else {
                float startAngle = relateAngle - chanceAngle;
                float endAngle = relateAngle + chanceAngle;
                if (Math.abs(relateAngle) < chanceAngle && bindingCharacter.nowAttackRadius > distance)
                    isChance = true;
                else if (bindingCharacter.nowAttackRadius < distance) {
                    bindingCharacter.offX = relateX;
                    bindingCharacter.offY = relateY;
                }
            }
        }

        if (isChance) {
            bindingCharacter.isStay = true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (bindingCharacter == null || bindingCharacter.isDead == true) {
                reset();
                return;
            }

            bindingCharacter.attack();
            bindingCharacter.isStay = false;


        }
    }

    public void hunt() {
        synchronized (bindingCharacter) {
            if (bindingCharacter.centerX == targetX && bindingCharacter.centerY == targetY) {
                targetX = -1;
                targetY = -1;
            }
            if (targetX < 0 || targetY < 0) {
                Random random = new Random();
                targetX = random.nextInt(mapWidth - bindingCharacter.getWidth());
                targetX += bindingCharacter.getWidth() / 2;
                targetY = random.nextInt(mapHeight - bindingCharacter.getHeight());
                targetY += bindingCharacter.getHeight() / 2;
            }

            bindingCharacter.offX = targetX - bindingCharacter.centerX;
            bindingCharacter.offY = targetY - bindingCharacter.centerY;

        }
    }

    public void escape() {
//        synchronized (bindingCharacter) {
        if (bindingCharacter.centerX <= 100) {
            targetX = 500;


        } else if (bindingCharacter.centerX >= 500) {
            targetX = 100;
        }
        if (bindingCharacter.centerX < targetX) {
            if (bindingCharacter.centerX + bindingCharacter.nowSpeed > targetX)
                bindingCharacter.nowLeft = targetX - bindingCharacter.getWidth() / 2;
            else
                bindingCharacter.nowLeft = bindingCharacter.nowLeft + bindingCharacter.nowSpeed;
        } else {
            if (bindingCharacter.centerX - bindingCharacter.nowSpeed < targetX)
                bindingCharacter.nowLeft = targetX - bindingCharacter.getWidth() / 2;
            else
                bindingCharacter.nowLeft = bindingCharacter.nowLeft - bindingCharacter.nowSpeed;
        }
        bindingCharacter.centerX = bindingCharacter.nowLeft + bindingCharacter.getWidth() / 2;
        bindingCharacter.centerY = bindingCharacter.nowTop + bindingCharacter.getHeight() / 2;

//        characterLayoutParams.leftMargin=bindingCharacter.nowLeft;
//        characterLayoutParams.topMargin=bindingCharacter.nowTop;
//        bindingCharacter.changeState();
//        bindingCharacter.setLayoutParams(characterLayoutParams);

//        bindingCharacter.attackRange.centerX=bindingCharacter.centerX;
//        bindingCharacter.attackRange.centerY=bindingCharacter.centerY;
//        bindingCharacter.attackRange.layoutParams.leftMargin=bindingCharacter.attackRange.centerX-bindingCharacter.attackRange.nowAttackRadius;
//        bindingCharacter.attackRange.layoutParams.topMargin=bindingCharacter.attackRange.centerY-bindingCharacter.attackRange.nowAttackRadius;
//        bindingCharacter.attackRange.setLayoutParams(bindingCharacter.attackRange.layoutParams);

//        bindingCharacter.viewRange.centerX=bindingCharacter.centerX;
//        bindingCharacter.viewRange.centerY=bindingCharacter.centerY;
//        bindingCharacter.viewRange.layoutParams.leftMargin=bindingCharacter.viewRange.centerX-bindingCharacter.viewRange.nowViewRadius;
//        bindingCharacter.viewRange.layoutParams.topMargin=bindingCharacter.viewRange.centerY-bindingCharacter.viewRange.nowViewRadius;
//        bindingCharacter.viewRange.setLayoutParams(bindingCharacter.viewRange.layoutParams);
//        }
    }
}