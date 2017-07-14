package com.jedi.wolf_and_hunter.myViews.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.gameObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.Trajectory;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/21.
 */

public class NormalHunter extends BaseCharacterView {
    private static final String TAG = "NormalHunter";
    private final static String characterName = "普通猎人";
    private final static int defaultExtraAttackRevise = 0;
    private final static int defaultMaxAttackCount = 2;
    private final static int defauleReloadAttackSpeed = 25;
    public final static int defaultAttackRadius = 700;
    public final static int defaultViewRadius = 600;
    public final static int defaultViewAngle = 90;
    public final static int defaultHearRadius = 400;
    public final static int defaultForceViewRadius = 200;
    public final static int defaultWalkWaitTime = 800;
    public final static int defaultRunWaitTime = 300;
    public final static int defaultSpeed = 10;
    public final static int defaultAngleChangSpeed = 5;
    public final static int defaultHealthPoint = 2;
    public final static int defaultKnockAwayStrength = 300;




    //下面一行控制bitmap是否自适应分辨率，不强制设flase可能出现图片分辨率和draw分辨率不一致
    BitmapFactory.Options option = new BitmapFactory.Options();

    {
        option.inScaled = false;
    }

    public static final int defaultHiddenLevel = BaseCharacterView.HIDDEN_LEVEL_NO_HIDDEN;

    public NormalHunter(Context context) {
        super(context);
        init();
    }

    /**
     * myCharacter最好用这个构造方法
     *
     * @param context
     * @param virtualWindow
     */
    public NormalHunter(Context context, MyVirtualWindow virtualWindow) {
        super(context);
        this.virtualWindow = virtualWindow;
        init();
    }

    public NormalHunter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        characterType = CHARACTER_TYPE_HUNTER;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.normal_hunter, option);
        Matrix matrixForCP = new Matrix();
        matrixForCP.postScale((float) characterBodySize / bitmap.getWidth() * (float) 0.8, (float) characterBodySize / bitmap.getHeight() * (float) 0.8);
        characterPic = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixForCP, true);

        reloadMediaPlayer = MediaPlayer.create(getContext(), R.raw.reload_bollet);
        attackMediaPlayer = MediaPlayer.create(getContext(), R.raw.gun_fire);
        moveMediaPlayer = MediaPlayer.create(getContext(), R.raw.hunter_move);
        super.nowReloadAttackSpeed = defauleReloadAttackSpeed;
        attackCount = defaultMaxAttackCount;
        maxAttackCount = defaultMaxAttackCount;
        nowExtraAttackRevise=defaultExtraAttackRevise;
        nowAttackRadius = defaultAttackRadius;
        nowViewRadius = defaultViewRadius;
        nowViewAngle = defaultViewAngle;
        nowHearRadius = defaultHearRadius;
        nowWalkWaitTime = defaultWalkWaitTime;
        nowRunWaitTime = defaultRunWaitTime;
        nowForceViewRadius = defaultForceViewRadius;
        nowSpeed = defaultSpeed;
        nowHealthPoint = defaultHealthPoint;
        nowKnockAwayStrength = defaultKnockAwayStrength;

        FrameLayout.LayoutParams viewRangeLP = (FrameLayout.LayoutParams) viewRange.getLayoutParams();
        viewRangeLP.width = 2 * defaultViewRadius;
        viewRange.setLayoutParams(viewRangeLP);

        FrameLayout.LayoutParams attackRangeLP = (FrameLayout.LayoutParams) attackRange.getLayoutParams();
        attackRangeLP.width = 2 * defaultAttackRadius;
        attackRange.setLayoutParams(attackRangeLP);

        super.nowAngleChangSpeed = defaultAngleChangSpeed;
        if (this.virtualWindow == null)
            this.virtualWindow = GameBaseAreaActivity.virtualWindow;
    }

    @Override
    public void initCharacterState() {
        nowAttackRadius = defaultAttackRadius;
        nowViewRadius = defaultViewRadius;
        nowViewAngle = defaultViewAngle;
        nowHearRadius = defaultHearRadius;
        nowForceViewRadius = defaultForceViewRadius;
        nowSpeed = defaultSpeed;
        nowAngleChangSpeed = defaultAngleChangSpeed;
    }

    @Override
    public void switchLockingState(Boolean isLocking) {
        synchronized (this) {
            super.switchLockingState(isLocking);
            if (isLocking) {

                this.nowViewRadius = (int) (1.25 * defaultViewRadius);
                this.nowForceViewRadius = (int) (2 * defaultForceViewRadius);
                this.nowViewAngle = (float) (0.5 * defaultViewAngle);
                this.nowSpeed = (int) (0.5 * defaultSpeed);
                this.nowAngleChangSpeed = (int) (0.4 * defaultAngleChangSpeed);
            } else {
                this.nowViewRadius = defaultViewRadius;
                this.nowForceViewRadius = defaultForceViewRadius;
                this.nowViewAngle = defaultViewAngle;
                this.nowSpeed = defaultSpeed;
                this.nowAngleChangSpeed = defaultAngleChangSpeed;
            }
        }
    }

    @Override
    public void reloadAttackCount() {
        if (isReloadingAttack == true)
            return;
        super.reloadAttackCount();
        initCharacterState();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            isReloadingAttack = true;
                            lockingCharacter = null;
                            isLocking = false;
                            nowSpeed = defaultSpeed / 2;
                            nowViewRadius = defaultViewRadius / 2;
                            nowForceViewRadius = defaultForceViewRadius / 2;
                            nowHearRadius = defaultHearRadius / 2;
                        }
                        while (nowReloadingAttackCount < reloadAttackTotalCount) {
                            nowReloadingAttackCount += nowReloadAttackSpeed;
                            if (nowReloadingAttackCount > reloadAttackTotalCount)
                                nowReloadingAttackCount = reloadAttackTotalCount;
                            try {
                                Thread.sleep(reloadAttackSleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
//
                        synchronized (this) {
                            nowSpeed = defaultSpeed;
                            nowViewRadius = defaultViewRadius;
                            nowForceViewRadius = defaultForceViewRadius;
                            nowHearRadius = defaultHearRadius;
                            attackCount = maxAttackCount;
                            nowReloadingAttackCount = 0;
                            isReloadingAttack = false;
                        }
                    }
                }

        ).start();

    }


    @Override
    public void deadReset() {
        super.deadReset();
        attackCount = defaultMaxAttackCount;
        nowHealthPoint=defaultHealthPoint;
        switchLockingState(false);
    }


    @Override
    public void attack() {
        if (attackCount <= 0 || isReloadingAttack || isDead) {
            return;
        }
        super.attack();

        attackCount -= 1;

        for (BaseCharacterView targetCharacter : GameBaseAreaActivity.gameInfo.allCharacters) {

            if (this == targetCharacter || targetCharacter.isDead == true || targetCharacter.getTeamID() == this.getTeamID())
                continue;


            int targetCharacterCenterX = targetCharacter.centerX;
            int targetCharacterCenterY = targetCharacter.centerY;

            double distance = MyMathsUtils.getDistance(new Point(centerX, centerY), new Point(targetCharacterCenterX, targetCharacterCenterY));
            if (distance > nowAttackRadius)
                continue;


            int targetCharacterSize = targetCharacter.characterBodySize;
            int relateX = targetCharacterCenterX - centerX;
            int relateY = targetCharacterCenterY - centerY;

            float angleBetweenXAxus = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);
            float relateAngle = Math.abs(angleBetweenXAxus - nowFacingAngle);
            if (relateAngle > 90 && relateAngle < 270) {//这类角度表示目标在此角色身后,不用考虑
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
            if (pointToLineDistance <=  targetCharacterSize+nowExtraAttackRevise) {
                HashMap<BaseCharacterView,BaseCharacterView> map=new HashMap<BaseCharacterView,BaseCharacterView>();
                map.put(this,targetCharacter);
                GameBaseAreaActivity.gameInfo.beAttackedList.add(map);
//                float relateFacingAngle = Math.abs(targetCharacter.nowFacingAngle - nowFacingAngle);
//
//                if (relateFacingAngle < 90 || relateFacingAngle > 270) {//背击
//                    targetCharacter.nowHealthPoint -= 2;
//                } else {
//                    targetCharacter.nowHealthPoint -= 1;
//                }
//                if (targetCharacter.nowHealthPoint <= 0) {
//                    targetCharacter.isDead = true;
//                    this.killCount++;
//                    targetCharacter.dieCount++;
//                    targetCharacter.deadTime = new Date().getTime();
//                } else {
//                    if (targetCharacter.knockedAwayThread != null && targetCharacter.knockedAwayThread.getState().equals(Thread.State.TERMINATED) == false) {
//                        targetCharacter.isKnockedAway = false;
//                        targetCharacter.knockedAwayThread.interrupt();
//                    }
//                    double cosAlpha = Math.cos(Math.toRadians(nowFacingAngle));
//                    double endX = cosAlpha * nowKnockAwayStrength;
//
//                    double endY = Math.sqrt(nowKnockAwayStrength * nowKnockAwayStrength - endX * endX);
//                    if (nowFacingAngle >= 180)
//                        endY = -endY;
//                    endX = endX + centerX;
//                    endY = endY + centerY;
////        Point fromPoint = new Point(centerX, centerY);
//                    Point toPoint = new Point((int) endX, (int) endY);
//                    targetCharacter.knockedAwayThread = new Thread(new KnockedAwayThread(toPoint));
//                    targetCharacter.knockedAwayThread.setDaemon(true);
//                    targetCharacter.knockedAwayThread.start();
//                }

            }


        }

        double cosAlpha = Math.cos(Math.toRadians(nowFacingAngle));
//        double cosAlpha=Math.cos(Math.toRadians(30));
        double endX = cosAlpha * nowAttackRadius;

        double endY = Math.sqrt(nowAttackRadius * nowAttackRadius - endX * endX);
        if (nowFacingAngle >= 180)
            endY = -endY;
        endX = endX + centerX;
        endY = endY + centerY;
        Point fromPoint = new Point(centerX, centerY);
        Point toPoint = new Point((int) endX, (int) endY);
        Trajectory trajectory = new Trajectory(getContext(), fromPoint, toPoint, this);
        Message msg = gameHandler.obtainMessage(GameBaseAreaActivity.GameHandler.ADD_TRAJECTORY, trajectory);
        gameHandler.sendMessage(msg);

    }


}
