package com.jedi.wolf_and_hunter.myViews.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.Trajectory;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/4/21.
 */

public class NormalHunter extends BaseCharacterView {
    private static final String TAG = "NormalHunter";
    private final static String characterName = "普通猎人";
    private final static int defaultMaxAttackCount = 2;
    private final static int defauleReloadAttackNeedTime = 3000;
    public final static int defaultAttackRadius = 700;
    public final static int defaultViewRadius = 600;
    public final static int defaultViewAngle = 90;
    public final static int defaultHearRadius = 400;
    public final static int defaultForceViewRadius = 200;
    public final static int defaultWalkWaitTime = 800;
    public final static int defaultRunWaitTime = 300;
    public final static int defaultSpeed = 10;
    public final static int defaultAngleChangSpeed = 3;
    private int bolletWidth = 0;


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
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.word, option);
        characterPic = Bitmap.createBitmap(bitmap, 24, 5, 76, 76, matrixForCP, true);
        reloadMediaPlayer = MediaPlayer.create(getContext(), R.raw.reload_bollet);
        attackMediaPlayer = MediaPlayer.create(getContext(), R.raw.gun_fire);
        moveMediaPlayer = MediaPlayer.create(getContext(), R.raw.hunter_move);
        super.reloadAttackNeedTime = defauleReloadAttackNeedTime;
        attackCount = defaultMaxAttackCount;
        maxAttackCount = defaultMaxAttackCount;
        nowAttackRadius = defaultAttackRadius;
        nowViewRadius = defaultViewRadius;
        nowViewAngle=defaultViewAngle;
        nowHearRadius = defaultHearRadius;
        nowWalkWaitTime = defaultWalkWaitTime;
        nowRunWaitTime = defaultRunWaitTime;
        nowForceViewRadius = defaultForceViewRadius;
        nowSpeed = defaultSpeed;

        FrameLayout.LayoutParams viewRangeLP= (FrameLayout.LayoutParams) viewRange.getLayoutParams();
        viewRangeLP.width=2*defaultViewRadius;
        viewRange.setLayoutParams(viewRangeLP);

        FrameLayout.LayoutParams attackRangeLP= (FrameLayout.LayoutParams) attackRange.getLayoutParams();
        attackRangeLP.width=2*defaultAttackRadius;
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
        nowForceViewRadius=defaultForceViewRadius;
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
                this.nowAngleChangSpeed = (int) (0.5 * defaultAngleChangSpeed);
            } else {
                this.nowViewRadius = defaultViewRadius;
                this.nowForceViewRadius = defaultForceViewRadius;
                this.nowViewAngle = defaultViewAngle;
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
                            lockingCharacter=null;
                            isLocking=false;
                            nowSpeed = defaultSpeed/2;
                            nowViewRadius = defaultViewRadius/2;
                            nowForceViewRadius = defaultForceViewRadius/2;
                            nowHearRadius = defaultHearRadius/2;
                        }
//
                        Date now = new Date();
                        reloadAttackStartTime = now.getTime();//这参数用于攻击按钮饼图显示
                        try {
                            Thread.sleep(reloadAttackNeedTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

//
                        synchronized (this) {
                            nowSpeed = defaultSpeed;
                            nowViewRadius = defaultViewRadius ;
                            nowForceViewRadius =defaultForceViewRadius;
                            nowHearRadius = defaultHearRadius ;
                            attackCount = maxAttackCount;
                            reloadAttackStartTime = 0;
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
    }


    @Override
    public void judgeAttack() {
        if (attackCount <= 0 || isReloadingAttack || isDead) {
            return;
        }
        super.judgeAttack();

        attackCount -= 1;

        for (BaseCharacterView targetCharacter : GameBaseAreaActivity.allCharacters) {

            if (this == targetCharacter || targetCharacter.getTeamID() == this.getTeamID())
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
            if (pointToLineDistance <= bolletWidth / 2 + targetCharacterSize) {
                targetCharacter.isDead = true;
                this.killCount++;
                targetCharacter.dieCount++;
                targetCharacter.deadTime = new Date().getTime();
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
