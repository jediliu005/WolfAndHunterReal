package com.jedi.wolf_and_hunter.myViews.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Message;
import android.util.AttributeSet;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.JRocker;
import com.jedi.wolf_and_hunter.myViews.Trajectory;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/4/21.
 */

public class NormalWolf extends BaseCharacterView {
    private static final String TAG = "NormalHunter";
    private final static String characterName = "普通狼";
    private final static int defaultMaxAttackCount = 3;
    private final static int defauleReloadAttackSpeed = 50;
    public final static int defaultAngleChangSpeed = 2;
    public final static int defaultAttackRadius = 300;
//    public final static int defaultViewRadius = 200;
    public final static int defaultViewRadius = 500;
    public final static int defaultViewAngle = 90;
    public final static int defaultHearRadius = 600;
    public  final static int defaultForceViewRadius=300;
    public  final static int defaultSmellRadius=2000;
    public  final static int defaultSmellSpeed=30;
    public final static int defaultWalkWaitTime = 500;
    public final static int defaultRunWaitTime = 200;
    public final static int defaultSpeed = 15;
    boolean isStop = false;
    Thread attackThread;
    //下面一行控制bitmap是否自适应分辨率，不强制设flase可能出现图片分辨率和draw分辨率不一致
    BitmapFactory.Options option = new BitmapFactory.Options();

    {
        option.inScaled = false;
    }

    public static final int defaultHiddenLevel = BaseCharacterView.HIDDEN_LEVEL_NO_HIDDEN;

    public NormalWolf(Context context) {
        super(context);
        init();
    }

    /**
     * myCharacter最好用这个构造方法
     *
     * @param context
     * @param virtualWindow
     */
    public NormalWolf(Context context, MyVirtualWindow virtualWindow) {
        super(context);
        this.virtualWindow = virtualWindow;
        init();
    }

    public NormalWolf(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        characterType=CHARACTER_TYPE_WOLF;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.normal_wolf, option);
        matrixForCP = new Matrix();
        matrixForCP.postScale((float)characterBodySize / bitmap.getWidth()*(float)0.8 , (float)  characterBodySize /bitmap.getHeight()*(float)0.8 );
        characterPic = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixForCP, true);

        attackMediaPlayer = MediaPlayer.create(getContext(), R.raw.wolf_attack);
        smellMediaPlayer = MediaPlayer.create(getContext(), R.raw.smell);
        super.nowReloadAttackSpeed = defauleReloadAttackSpeed;
        attackCount = defaultMaxAttackCount;
        maxAttackCount = defaultMaxAttackCount;
        nowAttackRadius = defaultAttackRadius;
        nowViewRadius = defaultViewRadius;
        nowViewAngle = defaultViewAngle;
        nowHearRadius = defaultHearRadius;
        nowForceViewRadius=defaultForceViewRadius;
        nowSpeed = defaultSpeed;
        nowAngleChangSpeed = defaultAngleChangSpeed;
        nowWalkWaitTime=defaultWalkWaitTime;
        nowRunWaitTime=defaultRunWaitTime;
        nowSmellRadius=defaultSmellRadius;
        nowSmellSpeed=defaultSmellSpeed;
        moveMediaPlayer = MediaPlayer.create(getContext(), R.raw.wolf_move);
        if (this.virtualWindow == null)
            this.virtualWindow = GameBaseAreaActivity.virtualWindow;
        reloadAttackCount();
    }

    @Override
    public void initCharacterState() {
        nowAttackRadius = defaultAttackRadius;
        nowViewRadius = defaultViewRadius;
        nowViewAngle = defaultViewAngle;
        nowHearRadius = defaultHearRadius;
        nowForceViewRadius=defaultForceViewRadius;
        nowSpeed = defaultSpeed;
        nowAngleChangSpeed = defaultAngleChangSpeed;
    }

    @Override
    public void reloadAttackCount() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (GameBaseAreaActivity.isStop == false && isStop == false) {
                            if (attackCount == maxAttackCount) {
                                if(nowSpeed!=defaultSpeed)
                                    nowSpeed=defaultSpeed;
                                try {
                                    Thread.sleep(reloadAttackSleepTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                continue;

                            }else {
                                nowSpeed =(int)((0.3+ 0.7*attackCount/maxAttackCount)*defaultSpeed);
                            }
                            if(nowReloadingAttackCount<reloadAttackTotalCount)
                                nowReloadingAttackCount+=nowReloadAttackSpeed;
                            if(nowReloadingAttackCount>reloadAttackTotalCount)
                                nowReloadingAttackCount=reloadAttackTotalCount;
                            if(nowReloadingAttackCount==reloadAttackTotalCount) {
                                nowReloadingAttackCount = 0;
                                attackCount++;
                            }
                            try {
                                Thread.sleep(reloadAttackSleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

        ).start();

    }

    private class AttackThread implements Runnable {

        BaseCharacterView attackCharacter;
        Point jumpToPoint;

        public AttackThread(BaseCharacterView attackCharacter, Point jumpToPoint) {
            this.attackCharacter = attackCharacter;
            this.jumpToPoint = jumpToPoint;
        }

        @Override
        public void run() {
            judgeingAttack = true;
            while (GameBaseAreaActivity.isStop == false && judgeingAttack) {

                int nowCenterX = (getLeft() + getRight()) / 2;
                int nowCenterY = (getTop() + getBottom()) / 2;
                int nowJumpToPointOffX = jumpToPoint.x - nowCenterX;
                int nowJumpToPointOffY = jumpToPoint.y - nowCenterY;
                double nowJumpToPointDistance = Math.sqrt(nowJumpToPointOffX * nowJumpToPointOffX + nowJumpToPointOffY * nowJumpToPointOffY);
                int jumpSpeed = 6 * nowSpeed;
                boolean attackSuccess = false;
                int realOffX = 0;
                int realOffY = 0;
                if (nowJumpToPointDistance > jumpSpeed) {
                    realOffX = (int) (jumpSpeed * nowJumpToPointOffX / nowJumpToPointDistance);
                    realOffY = (int) (jumpSpeed * nowJumpToPointOffY / nowJumpToPointDistance);
                } else {
                    realOffX = nowJumpToPointOffX;
                    realOffY = nowJumpToPointOffY;
                    judgeingAttack = false;
                }

                for (BaseCharacterView targetCharacter : GameBaseAreaActivity.allCharacters) {


                    if (attackCharacter == targetCharacter || targetCharacter.getTeamID() == attackCharacter.getTeamID())
                        continue;

                    double distance = MyMathsUtils.getDistance(new Point(centerX, centerY), new Point(targetCharacter.centerX, targetCharacter.centerY));
                    double realOffDistance = Math.sqrt(realOffX * realOffX + realOffY * realOffY);
                    if (distance > realOffDistance + characterBodySize / 2)
                        continue;

                    int targetCharacterSize = targetCharacter.characterBodySize;
                    int relateX = targetCharacter.centerX - centerX;
                    int relateY = targetCharacter.centerY - centerY;

                    float angleBetweenXAxus = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);
                    float relateAngle = Math.abs(angleBetweenXAxus - nowFacingAngle);
                    if (relateAngle > 90 && relateAngle < 270) {//这类角度表示目标在此角色身后
                        continue;
                    }
                    double pointToLineDistance = 0;
                    if (nowFacingAngle == 0 || nowFacingAngle == 180)
                        pointToLineDistance = relateY;
                    else if (nowFacingAngle == 90 || nowFacingAngle == 270)
                        pointToLineDistance = relateX;
                    else {
                        double k = Math.tan(Math.toRadians(nowFacingAngle));
                        pointToLineDistance = MyMathsUtils.getPointToLineDistance(new Point(relateX, relateY), k, 0);

                    }

                    if (pointToLineDistance <= characterBodySize / 2 + targetCharacterSize) {
                        targetCharacter.isDead = true;
                        attackCharacter.killCount++;
                        targetCharacter.dieCount++;
                        targetCharacter.deadTime = new Date().getTime();
                        attackCharacter.jumpToX = targetCharacter.centerX;
                        attackCharacter.jumpToY = targetCharacter.centerY;
                        attackSuccess = true;

                        break;
                    }

                }
                if (attackSuccess == false) {
                    attackCharacter.jumpToX = nowCenterX + realOffX;
                    attackCharacter.jumpToY = nowCenterY + realOffY;
                } else {
                    break;
                }

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            judgeingAttack = false;
            attackThread = null;
        }
    }

    @Override
    public void deadReset() {
        super.deadReset();
        attackCount=defaultMaxAttackCount;
    }

    @Override
    public void attack() {
        if (judgeingAttack || attackCount <= 0 || isDead) {
            return;
        }
        if (attackThread != null&&attackThread.getState()!= Thread.State.TERMINATED) {
            return;
        }
        super.attack();
        attackCount -= 1;
        double cosAlpha = Math.cos(Math.toRadians(nowFacingAngle));
        double endX = cosAlpha * nowAttackRadius;

        double endY = Math.sqrt(nowAttackRadius * nowAttackRadius - endX * endX);
        if (nowFacingAngle >= 180)
            endY = -endY;
        endX = endX + centerX;
        endY = endY + centerY;
//        Point fromPoint = new Point(centerX, centerY);
        Point toPoint = new Point((int) endX, (int) endY);

        attackThread = new Thread(new AttackThread(this, toPoint));
        attackThread.setDaemon(true);
        attackThread.start();



    }

}
