package com.jedi.wolf_and_hunter.ai;

import android.graphics.Point;
import android.util.Log;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myViews.Trajectory;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.util.Random;

/**
 * Created by Administrator on 2017/5/23.
 */

public class WolfAI extends BaseAI {
    public WolfAI(BaseCharacterView character) {
        super(character);
    }

    @Override
    public void run() {
//        addFacingThread();
        if (GameBaseAreaActivity.gameInfo.isStop)
            return;
//        addFacingThread();
        decideWhatToDo();

        if (bindingCharacter.attackCount == 0)
            bindingCharacter.reloadAttackCount();
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

//    public void changeFacing() {
//
//        if (bindingCharacter == null || bindingCharacter.isDead) {
//            return;
//        }
//        if (GameBaseAreaActivity.gameInfo.isStop == true)
//            return;
//        synchronized (bindingCharacter) {
//            float relateAngle = targetFacingAngle - bindingCharacter.nowFacingAngle;
//            if (Math.abs(relateAngle) > 180) {//处理旋转最佳方向
//                if (relateAngle > 0)
//                    relateAngle = relateAngle - 360;
//
//                else
//                    relateAngle = 360 - relateAngle;
//            }
//            if (Math.abs(relateAngle) > angleChangSpeed)
//                relateAngle = Math.abs(relateAngle) / relateAngle * angleChangSpeed;
//
//            bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + relateAngle;
//            if (bindingCharacter.nowFacingAngle < 0)
//                bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + 360;
//            else if (bindingCharacter.nowFacingAngle > 360)
//                bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle - 360;
//        }
//    }


    public synchronized void decideWhatToDo() {
        super.decideWhatToDo();
    }

    @Override
    public void trackTrajectory() {
        synchronized (bindingCharacter) {
            if (trackTrajectory == null) {
                reset();
                return;
            }

            int searchRelateX = trackTrajectory.fromPointRelateParent.x - bindingCharacter.centerX;
            int searchRelateY = trackTrajectory.fromPointRelateParent.y - bindingCharacter.centerY;
            float searchToAngle = MyMathsUtils.getAngleBetweenXAxus(searchRelateX, searchRelateY);


            int nowDistance = (int) MyMathsUtils.getDistance(trackTrajectory.fromPointRelateParent,
                    new Point(bindingCharacter.centerX, bindingCharacter.centerY));



            if (bindingCharacter.nowFacingAngle == searchToAngle && nowDistance<=bindingCharacter.nowForceViewRadius/2) {
                trackTrajectory = null;
                reset();

            } else {
                targetX = trackTrajectory.fromPointRelateParent.x;
                targetY = trackTrajectory.fromPointRelateParent.y;
                if (nowDistance <= bindingCharacter.nowForceViewRadius/2) {
                    bindingCharacter.isStay=true;
                }else{
                    bindingCharacter.isStay=false;
                }
                bindingCharacter.offX = targetX - bindingCharacter.centerX;
                bindingCharacter.offY = targetY - bindingCharacter.centerY;
            }
        }
    }

    @Override
    public void trackCharacter() {
        synchronized (bindingCharacter) {
            if (hasDealTrackOnce == false) {
                if (targetCharacter == null || targetCharacter.isDead == true) {
                    reset();
                    return;
                }
//                int searchRelateX = targetCharacter.centerX - bindingCharacter.centerX;
//                int searchRelateY = targetCharacter.centerY - bindingCharacter.centerY;
//                float searchToAngle = MyMathsUtils.getAngleBetweenXAxus(searchRelateX, searchRelateY);
//                targetFacingAngle = searchToAngle;

                int nowDistance = (int) MyMathsUtils.getDistance(new Point(targetCharacter.centerX, targetCharacter.centerY)
                        , new Point(bindingCharacter.centerX, bindingCharacter.centerY));
                if (nowDistance > bindingCharacter.nowForceViewRadius) {
                    bindingCharacter.isStay=false;
                } else {
                    bindingCharacter.isStay=true;
                }
                targetX = targetLastX;
                targetY = targetLastY;
                targetLastX = -1;
                targetLastY = -1;
                targetCharacter = null;
                hasDealTrackOnce = true;
            }
//            if (hasDealTrackOnce == false)
//                hasDealTrackOnce = true;
            int relateX = targetX - bindingCharacter.centerX;
            int relateY = targetY - bindingCharacter.centerY;
            float targetNowAngle=MyMathsUtils.getAngleBetweenXAxus(relateX,relateY);
            double targetDistance = Math.sqrt(relateX * relateX + relateY * relateY);
            if (targetDistance == 0
                    ||(targetDistance <= bindingCharacter.nowForceViewRadius && targetNowAngle == bindingCharacter.nowFacingAngle)) {
                reset();
                return;
            }


            if (targetDistance <= bindingCharacter.nowForceViewRadius
                    && targetNowAngle != bindingCharacter.nowFacingAngle) {
                bindingCharacter.isStay = true;
            } else {
                bindingCharacter.isStay = false;
            }
            bindingCharacter.offX = relateX;
            bindingCharacter.offY = relateY;
        }
    }

    public void reset() {
        super.reset();
    }

    @Override
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
            double distance = Math.sqrt(relateX * relateX + relateY * relateY);
            float targetNowAngle = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);

            float relateAngle = targetNowAngle - bindingCharacter.nowFacingAngle;

            float bestTurningAngle=relateAngle;
            if (Math.abs(bestTurningAngle) > 180) {//处理旋转最佳方向
                if (bestTurningAngle > 0)
                    bestTurningAngle = bestTurningAngle - 360;

                else
                    bestTurningAngle = 360 - bestTurningAngle;
            }
            if (Math.abs(bestTurningAngle) > angleChangSpeed)
                bestTurningAngle = Math.abs(bestTurningAngle) / bestTurningAngle * angleChangSpeed;

            bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + bestTurningAngle;
            if (bindingCharacter.nowFacingAngle < 0)
                bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + 360;
            else if (bindingCharacter.nowFacingAngle > 360)
                bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle - 360;



            if (Math.abs(relateAngle) > 360 - chanceAngle) {
                bindingCharacter.offX = 0;
                bindingCharacter.offY = 0;
                isChance = true;
            } else {
                float startAngle = relateAngle - chanceAngle;
                float endAngle = relateAngle + chanceAngle;
                if (Math.abs(relateAngle) < chanceAngle && bindingCharacter.nowAttackRadius > distance) {
                    isChance = true;
                    bindingCharacter.offX = 0;
                    bindingCharacter.offY = 0;
                } else if (bindingCharacter.nowAttackRadius < distance) {
                    bindingCharacter.offX = relateX;
                    bindingCharacter.offY = relateY;
                }
            }
        }

        if (isChance) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (bindingCharacter) {
                if (bindingCharacter == null || bindingCharacter.isDead == true) {
                    reset();
                    return;
                }
                if (bindingCharacter.isJumping == true)
                    return;

                bindingCharacter.attack();
            }
        }
    }

    @Override
    public void hunt() {
        synchronized (bindingCharacter) {
            int relateX = targetX - bindingCharacter.centerX;
            int relateY = targetY - bindingCharacter.centerY;
            double distance = Math.sqrt(relateX * relateX + relateY * relateY);
            if (distance < bindingCharacter.nowSpeed) {//为什么要这么判？因为狼的移动模式特别，可能永远无法达到目标点
                reset();
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

    @Override
    public synchronized void escape() {

    }
}
