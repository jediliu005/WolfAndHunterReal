package com.jedi.wolf_and_hunter.myViews.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.gameObj.CharacterPosition;
import com.jedi.wolf_and_hunter.myObj.gameObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myObj.gameObj.PlayerInfo;
import com.jedi.wolf_and_hunter.myViews.range.AttackRange;
import com.jedi.wolf_and_hunter.myViews.mapBase.GameMap;
import com.jedi.wolf_and_hunter.myViews.rocker.JRocker;
import com.jedi.wolf_and_hunter.myViews.range.PromptView;
import com.jedi.wolf_and_hunter.myViews.SightView;
import com.jedi.wolf_and_hunter.myViews.range.ViewRange;
import com.jedi.wolf_and_hunter.myViews.landform.Landform;
import com.jedi.wolf_and_hunter.myViews.tempView.InjuryView;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Administrator on 2017/3/13.
 */

public class BaseCharacterView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "BaseCharacterView";
    public static final int HIDDEN_LEVEL_NO_HIDDEN = 0;
    public static final int HIDDEN_LEVEL_LOW_HIDDEN = 1;
    public static final int HIDDEN_LEVEL_HIGHT_HIDDEN = 2;
    public static final int HIDDEN_LEVEL_ABSOLUTE_HIDDEN = 3;
    public static final int MOVINT_TYPE_STAY = 0;
    public static final int MOVINT_TYPE_WALK = 1;
    public static final int MOVINT_TYPE_RUN = 2;
    public static final int CHARACTER_TYPE_UNDEFINED = 0;
    public static final int CHARACTER_TYPE_HUNTER = 1;
    public static final int CHARACTER_TYPE_WOLF = 2;
    //以下为移动相关的临时参数
    public int lastX;
    public int lastY;
    public int offX;
    public int offY;
    public float targetFacingAngle;
    public int jumpToX = -99999;
    public int jumpToY = -99999;
    public int knockedAwayX = -99999;
    public int knockedAwayY = -99999;
    public Thread knockedAwayThread;
    public volatile boolean needMove = false;
    public int nowAngleChangSpeed = 1;

    //以下为角色View实际或最终位置以及实际状态属性
    public boolean isInvincible = false;
    public long invincibleStartTime;
    public long invincibleLastTime;
    public int characterType = 0;
    public boolean hasUpdatedPosition = false;
    public int centerX = -1, centerY = -1;
    public int nowLeft = -1;
    public int nowTop = -1;
    public int nowRight = -1;
    public int nowBottom = -1;
    public float nowFacingAngle;
    public float nowViewAngle = 90;
    public int characterBodySize;
    public boolean isMyCharacter = false;
    public int nowHiddenLevel = 0;
    public volatile int attackCount;
    public int maxAttackCount;
    public volatile int nowSmellCount;
    public volatile int nowSmellSpeed;
    public static final int smellTotalCount = 1000;
    public static final int smellSleepTime = 100;
    public static final int reloadAttackTotalCount = 1000;
    public static final int reloadAttackSleepTime = 100;
    public volatile boolean isAttackting;
    public volatile int nowExtraAttackRevise = 10;
    public volatile int nowReloadingAttackCount = 0;
    public volatile int nowReloadAttackSpeed;
    public int nowHealthPoint;
    public int killCount;
    public int dieCount;
    public final int defaultHiddenLevel = HIDDEN_LEVEL_NO_HIDDEN;
    public volatile int nowAttackRadius = 600;
    public volatile int nowViewRadius = 500;
    public volatile int nowHearRadius = 500;
    public volatile int nowForceViewRadius = 200;
    public volatile int nowSmellRadius = 2000;
    public volatile int nowKnockAwayStrength = 100;
    public int nowWalkWaitTime = 600;
    public int nowRunWaitTime = 300;
    public volatile int nowSpeed = 10;
    public SightView sight;//这个view已经废弃
    private int teamID;
    public int id;
    public long lastInjureTime;
    public int nowRecoverTime;
    public volatile boolean isDead = false;
    public volatile boolean isKnockedAway = false;
    public long deadTime;
    public volatile boolean isForceToBeSawByMe = false;//注意！这属性只针对本机玩家视觉，对AI判行为无效
    public volatile boolean isJumping = false;
    public volatile boolean isReloadingAttack = false;
    public int runOrWalk = MOVINT_TYPE_RUN;
    public volatile boolean isStay;
    public volatile boolean isLocking;
    public volatile boolean isSmelling;
    public BaseCharacterView lockingCharacter;

    //各种标识用附加view
    public AttackRange attackRange;//攻击范围View
    public ViewRange viewRange;//视觉范围View
    public PromptView promptView;//探测提示View
    public MyVirtualWindow virtualWindow;//虚拟窗口，把屏幕可视区域看成一个View；
    public Vector<InjuryView> injuryViews = new Vector<InjuryView>();
    //各种线程
    public volatile boolean isStop = false;
    public Thread movingMediaThread;//移动音效线程
    public Thread reloadAttackCountThread;//装弹运算线程
    Thread smellThread;//嗅觉探测相关线程
    //各种集合
    public volatile Vector<Integer> seeMeTeamIDs;//存储发现这角色的队伍ID
    public volatile Vector<BaseCharacterView> theyDiscoverMe;//存储发现这角色的角色
    public volatile Vector<CharacterPosition> enemiesPositionSet;//存储探测到的其他角色位置


    //地形影响相关
    public int lastEffectX = -1;
    public int lastEffectY = -1;
    public Landform lastLandform;

    //多媒体
    public MediaPlayer moveMediaPlayer;
    public MediaPlayer attackMediaPlayer;
    public MediaPlayer reloadMediaPlayer;
    public MediaPlayer smellMediaPlayer;

    //绘图相关
    public Bitmap characterPic;
    public static Bitmap starPic;
    public SurfaceHolder mHolder;
    int borderWidth;
    Paint normalPaint;
    Paint alphaPaint;
    Paint transparentPaint;
    Paint textNormalPaint;
    Paint textAlphaPaint;
    public Handler gameHandler;


    BitmapFactory.Options option = new BitmapFactory.Options();

    {
        option.inScaled = false;
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;


        if (teamID == 1) {
            nowLeft = 50;
            nowTop = 50;
            nowFacingAngle = 45;
            normalPaint.setColor(Color.RED);
            alphaPaint.setColor(Color.RED);


        } else if (teamID == 2) {
            nowLeft = GameBaseAreaActivity.gameInfo.mapWidth - characterBodySize - 50;
            nowTop = 50;
            nowFacingAngle = 135;
            normalPaint.setColor(Color.LTGRAY);
            alphaPaint.setColor(Color.LTGRAY);
        } else if (teamID == 3) {
            nowLeft = 50;
            nowTop = GameBaseAreaActivity.gameInfo.mapHeight - characterBodySize - 50;
            nowFacingAngle = 315;
            normalPaint.setColor(Color.YELLOW);
            alphaPaint.setColor(Color.YELLOW);
        } else if (teamID == 4) {
            nowLeft = GameBaseAreaActivity.gameInfo.mapWidth - characterBodySize - 50;
            nowTop = GameBaseAreaActivity.gameInfo.mapHeight - characterBodySize - 50;
            nowFacingAngle = 225;
            normalPaint.setColor(Color.BLUE);
            alphaPaint.setColor(Color.BLUE);
        }
        alphaPaint.setAlpha(80);

        if (nowLeft > 0 && nowTop > 0 && nowFacingAngle > 0) {
            FrameLayout.LayoutParams characterParams = (FrameLayout.LayoutParams) getLayoutParams();
            characterParams.leftMargin = nowLeft;
            characterParams.topMargin = nowTop;

            centerX = nowLeft + characterBodySize / 2;
            centerY = nowTop + characterBodySize / 2;
            new AttackRange(getContext(), this);
            new ViewRange(getContext(), this);
            this.setLayoutParams(characterParams);
            if (viewRange != null) {
                viewRange.centerX = this.centerX;
                viewRange.centerY = this.centerY;
//                viewRange.nowLeft = centerX - nowViewRadius;
//                viewRange.nowRight = centerX + nowViewRadius;
//                viewRange.nowTop = centerY - nowViewRadius;
//                viewRange.nowBottom = centerY + nowViewRadius;
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewRange.getLayoutParams();
                layoutParams.leftMargin = centerX - nowViewRadius;
                layoutParams.topMargin = centerY - nowViewRadius;
                viewRange.setLayoutParams(layoutParams);
            }
            if (attackRange != null) {
                attackRange.centerX = this.centerX;
                attackRange.centerY = this.centerY;
//                attackRange.nowLeft = centerX - nowAttackRadius;
//                attackRange.nowRight = centerX + nowAttackRadius;
//                attackRange.nowTop = centerY - nowAttackRadius;
//                attackRange.nowBottom = centerY + nowAttackRadius;
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) attackRange.getLayoutParams();
                layoutParams.leftMargin = centerX - nowAttackRadius;
                layoutParams.topMargin = centerY - nowAttackRadius;
                attackRange.setLayoutParams(layoutParams);
            }


        }

    }


    public BaseCharacterView(Context context) {
        super(context);
        init();
    }

    public BaseCharacterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseCharacterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //sight仅对玩家操控角色有意义，不在这里统一创建
    private void init() {
        enemiesPositionSet = new Vector<CharacterPosition>();
        nowReloadingAttackCount = 0;
        theyDiscoverMe = new Vector<BaseCharacterView>();
        seeMeTeamIDs = new Vector<Integer>();

        characterBodySize = 60;
        mHolder = getHolder();
        mHolder.addCallback(this);
        //以下两句必须在构造方法里做，否则各种奇妙poorguy
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);

        if (starPic == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.star, option);
            Matrix matrixForSP = new Matrix();
            matrixForSP.postScale((float) characterBodySize / bitmap.getWidth() * (float) 0.8, (float) characterBodySize / bitmap.getHeight() * (float) 0.8);
            starPic = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixForSP, true);
        }
        borderWidth = 5;
        normalPaint = new Paint();
        normalPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        normalPaint.setStrokeWidth(borderWidth);
        normalPaint.setAntiAlias(true);

        textNormalPaint = new Paint();
        textNormalPaint.setColor(Color.BLACK);
        textNormalPaint.setFakeBoldText(false);
        textNormalPaint.setTextAlign(Paint.Align.CENTER);
        textNormalPaint.setTextSize(characterBodySize * 2 / 3);
        textNormalPaint.setAntiAlias(true);


        transparentPaint = new Paint();
        transparentPaint.setColor(Color.BLACK);
        transparentPaint.setStyle(Paint.Style.STROKE);
        transparentPaint.setTextAlign(Paint.Align.CENTER);
        transparentPaint.setTextSize(characterBodySize);
        transparentPaint.setStrokeWidth(borderWidth);
        transparentPaint.setAlpha(0);
        transparentPaint.setAntiAlias(true);


        alphaPaint = new Paint();
        alphaPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        alphaPaint.setTextAlign(Paint.Align.CENTER);
        alphaPaint.setTextSize(characterBodySize);
        alphaPaint.setStrokeWidth(borderWidth);
        alphaPaint.setAlpha(80);
        alphaPaint.setAntiAlias(true);

        textAlphaPaint = new Paint();
        textAlphaPaint.setColor(Color.BLACK);
        textNormalPaint.setFakeBoldText(false);
        textAlphaPaint.setTextAlign(Paint.Align.CENTER);
        int textSize = characterBodySize * 2 / 3;
        textAlphaPaint.setTextSize(textSize);
        textAlphaPaint.setAlpha(50);
        textAlphaPaint.setAntiAlias(true);


//        arrowBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
//        matrixForArrow = new Matrix();
//
//        matrixForArrow.postScale((float) (0.5 * characterBodySize / arrowBitMap.getWidth()), (float) (0.5 * characterBodySize / arrowBitMap.getHeight()));
//        arrowBitMap = Bitmap.createBitmap(arrowBitMap, 0, 0, arrowBitMap.getWidth(), arrowBitMap.getHeight(),
//                matrixForArrow, true);
//        arrowBitmapHeight = arrowBitMap.getHeight();
//        arrowBitmapWidth = arrowBitMap.getHeight();
//        aroundSize = 2 * arrowBitmapWidth;

        FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
        if (mLayoutParams == null)
            mLayoutParams = new FrameLayout.LayoutParams(characterBodySize, characterBodySize);
//        mLayoutParams.height = characterBodySize + aroundSize;
//        mLayoutParams.width = characterBodySize + aroundSize;
//        centerX = getLeft() + (characterBodySize + aroundSize) / 2;
//        centerY = getTop() + (characterBodySize + aroundSize) / 2;
        else {
            mLayoutParams.height = characterBodySize;
            mLayoutParams.width = characterBodySize;

        }

//        if (nowLeft < 0 || nowTop < 0) {
//            nowLeft = 100;
//            nowTop = 100;
//            nowRight = nowLeft + characterBodySize;
//            nowBottom = nowTop + characterBodySize;
//        }
//        mLayoutParams.leftMargin = nowLeft;
//        mLayoutParams.topMargin = nowTop;
        if (nowLeft >= 0) {
            centerX = nowLeft + (characterBodySize) / 2;
        }
        if (nowTop >= 0) {
            centerY = nowTop + (characterBodySize) / 2;
        }
        this.setLayoutParams(mLayoutParams);
        new AttackRange(getContext(), this);
        new ViewRange(getContext(), this);


    }

    public SightView getSight() {
        return sight;
    }

    public void setSight(SightView sight) {
        this.sight = sight;
        sight.bindingCharacter = this;
    }

    public void dealInjury() {

        if (injuryViews.size() == 0)
            return;
        long nowTime = new Date().getTime();
        Iterator<InjuryView> iterator = injuryViews.iterator();
        while (iterator.hasNext()) {
            InjuryView injuryView = iterator.next();
            if (isMyCharacter) {
                if (injuryView.hasAddedToBaseFrame) {
                    if (nowTime - injuryView.createTime > nowRecoverTime) {
                        nowHealthPoint++;
                        GameBaseAreaActivity.baseFrame.removeView(injuryView);
                        iterator.remove();
                    }
                } else {
                    GameBaseAreaActivity.baseFrame.addView(injuryView);
                    injuryView.hasAddedToBaseFrame = true;
                    injuryView.bringToFront();
                }
            } else {
                if (injuryView.hasAddedToBaseFrame) {
                    if (nowTime - injuryView.createTime > nowRecoverTime) {
                        nowHealthPoint++;
                        GameBaseAreaActivity.mapBaseFrame.removeView(injuryView);
                        iterator.remove();
                    }
                } else {
                    GameBaseAreaActivity.mapBaseFrame.addView(injuryView);
                    injuryView.hasAddedToBaseFrame = true;
                    injuryView.bringToFront();
                }
            }

        }

    }


    public void startMovingMediaThread() {
        if (movingMediaThread != null) {
            if (movingMediaThread.getState() == Thread.State.TERMINATED)
                movingMediaThread = null;
            else
                return;
        }
        if (GameBaseAreaActivity.gameInfo.isStop == true || needMove == false || isDead == true)
            return;
        movingMediaThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (GameBaseAreaActivity.gameInfo.isStop == false && needMove && isDead == false) {
                    if (isStay) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    float leftVol = 1;
                    float rightVol = 1;
                    if (isMyCharacter == false) {
                        BaseCharacterView myCharacter = GameBaseAreaActivity.myCharacter;
                        int relateX = myCharacter.centerX - centerX;
                        int relateY = myCharacter.centerY - centerY;
                        double distance = Math.sqrt(relateX * relateX + relateY * relateY);
                        if (distance > nowHearRadius) {
                            return;
                        }

                        if (relateX > 0) {
                            rightVol = (float) (nowHearRadius - distance) / nowHearRadius;
                            leftVol = rightVol / 2;
                        } else if (relateX < 0) {
                            leftVol = (float) (nowHearRadius - distance) / nowHearRadius;
                            rightVol = leftVol / 2;
                        }

                        if (leftVol > 1)
                            leftVol = 1;
                        if (rightVol > 1)
                            rightVol = 1;


                    }
                    if (runOrWalk == MOVINT_TYPE_WALK) {
                        leftVol = leftVol / 3;
                        rightVol = rightVol / 3;
                    }
                    try {
                        moveMediaPlayer.setVolume(leftVol, rightVol);
                        if (isStay == false && needMove == true)
                            moveMediaPlayer.start();

                        if (runOrWalk == MOVINT_TYPE_WALK || isReloadingAttack == true || isLocking == true)
                            Thread.sleep(nowWalkWaitTime);
                        else
                            Thread.sleep(nowRunWaitTime);
                        moveMediaPlayer.seekTo(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                movingMediaThread = null;
            }
        });
        movingMediaThread.setDaemon(true);
        movingMediaThread.start();
    }

    public void updateNowPosition() {
        if (hasUpdatedPosition == true)
            return;
        nowLeft = getLeft();
        nowTop = getTop();
        nowRight = getRight();
        nowBottom = getBottom();
        hasUpdatedPosition = true;
    }

    @Deprecated
    public void masterModeOffsetLRTBParams() {
        int nowOffX = offX;
        int nowOffY = offY;


        //根据设定速度修正位移量
        double offDistance = Math.sqrt(nowOffX * nowOffX + nowOffY * nowOffY);
        int nowMoveSpeed = nowSpeed;
        if (offDistance < JRocker.padRadius * 3 / 4)
            nowMoveSpeed = nowSpeed / 2;

        nowOffX = (int) (nowSpeed * nowOffX / offDistance);
        nowOffY = (int) (nowSpeed * nowOffY / offDistance);
        //保证不超出父View边界
        try {
            nowOffX = ViewUtils.reviseOffX(this, (View) this.getParent(), nowOffX);

            nowOffY = ViewUtils.reviseOffY(this, (View) this.getParent(), nowOffY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        nowLeft = nowLeft + nowOffX;
        nowTop = nowTop + nowOffY;
        nowRight = nowLeft + getWidth();
        nowBottom = nowTop + getHeight();
        //判定character位置修正是否在当前视窗内，若不在，根据sight和character位置修正视窗位置
        if (sight.isCharacterInWindow() == false) {

            sight.goWatchingCharacter();

        }

        //两种跟随修正
        if (sight.isSightInWindow() == false) {//当视点不在屏幕内，将保持视点角度平移至屏幕边缘


            sight.keepDirectionAndMove(virtualWindow.left, virtualWindow.top, virtualWindow.right, virtualWindow.bottom);

        } else if (sight.needMove == false)//当视点在屏幕内且右摇杆不在操作的时候，视点需要伴随角色平移
        {
            sight.followCharacter(nowOffX, nowOffY);
        }

//        changeRotate();


    }


    public void initCharacterState() {

    }

    public void reactHunterMove() {
        if (isStay)
            return;
        int nowOffX = offX;
        int nowOffY = offY;

        //根据设定速度修正位移量
        float nowMoveSpeed = nowSpeed;
        if (runOrWalk == MOVINT_TYPE_WALK) {
            nowMoveSpeed = nowSpeed / 3;
        }
        double offDistance = Math.sqrt(nowOffX * nowOffX + nowOffY * nowOffY);
        if (offDistance > nowMoveSpeed) {
            nowOffX = Math.round((float) (nowMoveSpeed * nowOffX / offDistance));
            nowOffY = Math.round((float) (nowMoveSpeed * nowOffY / offDistance));
        }
        //保证不超出父View边界
        try {
            nowOffX = ViewUtils.reviseOffX(this, (View) this.getParent(), nowOffX);
            nowOffY = ViewUtils.reviseOffY(this, (View) this.getParent(), nowOffY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        nowLeft = nowLeft + nowOffX;
        nowTop = nowTop + nowOffY;
        nowRight = nowLeft + getWidth();
        nowBottom = nowTop + getHeight();

        if (targetFacingAngle > 0) {
            float relateAngle = targetFacingAngle - nowFacingAngle;
            if (Math.abs(relateAngle) > 180) {//处理旋转最佳方向
                if (relateAngle > 0)
                    relateAngle = relateAngle - 360;

                else
                    relateAngle = 360 - relateAngle;
            }
            if (Math.abs(relateAngle) > nowAngleChangSpeed)
                relateAngle = Math.abs(relateAngle) / relateAngle * nowAngleChangSpeed;

            nowFacingAngle = nowFacingAngle + relateAngle;


            if (nowFacingAngle < 0)
                nowFacingAngle = nowFacingAngle + 360;
            else if (nowFacingAngle > 360)
                nowFacingAngle = nowFacingAngle - 360;

        }
    }

    public void reactOtherOnlinePlayerHunterMove(PlayerInfo playerInfo) {
        int targetCenterX = playerInfo.nowCenterX;
        int targetCenterY = playerInfo.nowCenterY;
        int nowOffX = targetCenterX - centerX;
        int nowOffY = targetCenterY - centerY;
        if (playerInfo.nowSpeed != nowSpeed)
            nowSpeed = playerInfo.nowSpeed;


        //根据设定速度修正位移量
        double offDistance = Math.sqrt(nowOffX * nowOffX + nowOffY * nowOffY);
        float nowMoveSpeed = nowSpeed;
        if (offDistance < 5 * nowMoveSpeed) {
            if (offDistance > 2 * nowMoveSpeed)
                nowMoveSpeed = 2 * nowSpeed;
            if (offDistance > nowMoveSpeed) {
                nowOffX = Math.round((float) (nowMoveSpeed * nowOffX / offDistance));
                nowOffY = Math.round((float) (nowMoveSpeed * nowOffY / offDistance));
            }
        }


        //保证不超出父View边界
        try {
            nowOffX = ViewUtils.reviseOffX(this, (View) this.getParent(), nowOffX);
            nowOffY = ViewUtils.reviseOffY(this, (View) this.getParent(), nowOffY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        nowLeft = nowLeft + nowOffX;
        nowTop = nowTop + nowOffY;
        nowRight = nowLeft + getWidth();
        nowBottom = nowTop + getHeight();

    }

    public void reactWolfMove() {
        isSmelling = false;
        int nowOffX = offX;
        int nowOffY = offY;
        if (nowOffX == 0 && nowOffY == 0) {
            if (targetFacingAngle >= 0)
                nowFacingAngle = targetFacingAngle;
            return;
        }
        float realRelateAngle = 0;
        float targetFacingAngle = 0;

        try {
            targetFacingAngle = MyMathsUtils.getAngleBetweenXAxus(nowOffX, nowOffY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        float relateAngle = targetFacingAngle - nowFacingAngle;
        if (Math.abs(relateAngle) > 180) {//处理旋转最佳方向
            if (relateAngle > 0)
                relateAngle = relateAngle - 360;

            else
                relateAngle = 360 - relateAngle;
        }
        if (Math.abs(relateAngle) > nowAngleChangSpeed * 2)
            realRelateAngle = Math.abs(relateAngle) / relateAngle * nowAngleChangSpeed * 2;
        else
            realRelateAngle = relateAngle;

        nowFacingAngle = nowFacingAngle + realRelateAngle;
        if (nowFacingAngle < 0)
            nowFacingAngle = nowFacingAngle + 360;
        else if (nowFacingAngle > 360)
            nowFacingAngle = nowFacingAngle - 360;

        if (isStay == false) {

            double offDistance = Math.sqrt(nowOffX * nowOffX + nowOffY * nowOffY);
            int nowMoveSpeed = nowSpeed;
            if (runOrWalk == MOVINT_TYPE_WALK) {
                nowMoveSpeed = nowSpeed / 3;
            }

            if (Math.abs(relateAngle) > 45)
                nowMoveSpeed = nowMoveSpeed / 10;
            double cosNowFacingAngle = Math.cos(Math.toRadians(nowFacingAngle));
            nowOffX = (int) Math.round(cosNowFacingAngle * offDistance);
            nowOffY = (int) Math.round(Math.sqrt(offDistance * offDistance - nowOffX * nowOffX));
            if (nowFacingAngle > 180)
                nowOffY = -nowOffY;
            //根据设定速度修正位移量
            nowOffX = Math.round((float) (nowMoveSpeed * nowOffX / offDistance));
            nowOffY = Math.round((float) (nowMoveSpeed * nowOffY / offDistance));

            //保证不超出父View边界
            try {
                nowOffX = ViewUtils.reviseOffX(this, (View) this.getParent(), nowOffX);

                nowOffY = ViewUtils.reviseOffY(this, (View) this.getParent(), nowOffY);

            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
            nowLeft = nowLeft + nowOffX;
            nowTop = nowTop + nowOffY;
            nowRight = nowLeft + getWidth();
            nowBottom = nowTop + getHeight();
        }
    }


    public void reactOtherOnlinePlayerWolfMove(PlayerInfo playerInfo) {
        int nowOffX = offX;
        int nowOffY = offY;

        if (nowOffX == 0 && nowOffY == 0)
            return;
        float realRelateAngle = 0;
        float targetFacingAngle = 0;
        try {
            targetFacingAngle = MyMathsUtils.getAngleBetweenXAxus(nowOffX, nowOffY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        float relateAngle = targetFacingAngle - nowFacingAngle;
        if (Math.abs(relateAngle) > 180) {//处理旋转最佳方向
            if (relateAngle > 0)
                relateAngle = relateAngle - 360;

            else
                relateAngle = 360 - relateAngle;
        }
        if (Math.abs(relateAngle) > nowAngleChangSpeed * 2)
            realRelateAngle = Math.abs(relateAngle) / relateAngle * nowAngleChangSpeed * 2;
        else
            realRelateAngle = relateAngle;
        nowFacingAngle = nowFacingAngle + realRelateAngle;
        if (nowFacingAngle < 0)
            nowFacingAngle = nowFacingAngle + 360;
        else if (nowFacingAngle > 360)
            nowFacingAngle = nowFacingAngle - 360;

        if (isStay == false) {

            double offDistance = Math.sqrt(nowOffX * nowOffX + nowOffY * nowOffY);
            int nowMoveSpeed = nowSpeed;


            if (Math.abs(relateAngle) > 45)
                nowMoveSpeed = nowMoveSpeed / 10;
            double cosNowFacingAngle = Math.cos(Math.toRadians(nowFacingAngle));
            nowOffX = (int) Math.round(cosNowFacingAngle * offDistance);
            nowOffY = (int) Math.round(Math.sqrt(offDistance * offDistance - nowOffX * nowOffX));
            if (nowFacingAngle > 180)
                nowOffY = -nowOffY;
            //根据设定速度修正位移量
            nowOffX = Math.round((float) (nowMoveSpeed * nowOffX / offDistance));
            nowOffY = Math.round((float) (nowMoveSpeed * nowOffY / offDistance));

            //保证不超出父View边界
            try {
                nowOffX = ViewUtils.reviseOffX(this, (View) this.getParent(), nowOffX);

                nowOffY = ViewUtils.reviseOffY(this, (View) this.getParent(), nowOffY);

            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
            nowLeft = nowLeft + nowOffX;
            nowTop = nowTop + nowOffY;
            nowRight = nowLeft + getWidth();
            nowBottom = nowTop + getHeight();
        }
    }

    public void changeThisCharacterOnLandformses() {

        int x = (centerX - 1) / 100;
        int y = (centerY - 1) / 100;
        if (x != lastEffectX || y != lastEffectY) {
            if (lastLandform != null)
                lastLandform.removeEffect(this);
            lastEffectX = x;
            lastEffectY = y;
        }
        if (GameMap.landformses != null) {
            if (x < 0 || y < 0)
                Log.i("", "");
            if (GameMap.landformses.length <= y || GameMap.landformses[y].length <= x)
                return;
            Landform landform = GameMap.landformses[y][x];
            if (landform != null) {
                landform.effect(this);
                lastLandform = landform;
            } else {

            }
        } else {
            lastLandform = null;
        }
    }

    /**
     * 本方法仅为myCharacter所用，ai不应使用
     */
    public void changeOtherCharacterLandformState() {
        //处理隐身
        for (BaseCharacterView otherCharacter : GameBaseAreaActivity.gameInfo.allCharacters) {
            if (otherCharacter == this)
                continue;
            boolean isInViewRange = isInViewRange(otherCharacter, nowViewRadius);
            boolean isDiscoverByMe = false;
            if (otherCharacter.teamID == teamID)
                return;

            //在基本可视范围内
            if (isInViewRange) {
                //对方没有隐藏，直接可见
                if (otherCharacter.nowHiddenLevel == HIDDEN_LEVEL_NO_HIDDEN) {
                    isDiscoverByMe = true;

                }
                //有隐身，判是否在强制可视范围内
                else if (otherCharacter.nowHiddenLevel > HIDDEN_LEVEL_NO_HIDDEN) {
                    boolean isInForceViewRange = isInViewRange(otherCharacter, nowForceViewRadius);
                    if (isInForceViewRange) {
                        isDiscoverByMe = true;
                    } else {
                        isDiscoverByMe = false;
                    }
                }
            }
            //不在基本可视范围内
            else {
                isDiscoverByMe = false;
            }


            if (isDiscoverByMe == true) {//处理闯入本角色视觉范围的情况
                if (otherCharacter.seeMeTeamIDs.contains(this.teamID)) {//已经被本队发现
                    if (otherCharacter.theyDiscoverMe.contains(this) == false) {//第一发现人不是本角色
                        otherCharacter.theyDiscoverMe.add(this);
                    }
                } else {//自己是第一发现人
                    otherCharacter.seeMeTeamIDs.add(this.teamID);
                    otherCharacter.theyDiscoverMe.add(this);

                    otherCharacter.isForceToBeSawByMe = true;
                }
            } else {//处理不在本角色视觉范围内的情况
                if (otherCharacter.seeMeTeamIDs.contains(this.teamID)) {//已经被我队发现
                    if (otherCharacter.theyDiscoverMe.contains(this)) {
                        otherCharacter.theyDiscoverMe.remove(this);
                    }
                    boolean hasMyTeammate = false;
                    Iterator<BaseCharacterView> iterator = otherCharacter.theyDiscoverMe.iterator();
                    while (iterator.hasNext()) {
                        BaseCharacterView c = iterator.next();
                        if (c.teamID == this.teamID) {
                            hasMyTeammate = true;
                            break;
                        }
                    }

                    if (hasMyTeammate == false) {
                        int index = otherCharacter.seeMeTeamIDs.indexOf(this.teamID);
                        if (index >= 0)
                            otherCharacter.seeMeTeamIDs.remove(index);
                        otherCharacter.isForceToBeSawByMe = false;
                    } else {
                        otherCharacter.isForceToBeSawByMe = true;
                    }
                }


            }
        }

    }

    public void changeRotate() {
        int relateX = sight.centerX - this.centerX;
        int relateY = sight.centerY - this.centerY;


        try {
            nowFacingAngle = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(wSize, hSize);


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


//    public boolean onTouchEvent(MotionEvent event) {
//        //获取到手指处的横坐标和纵坐标
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//
//        switch(event.getAction())
//        {
//            case MotionEvent.ACTION_DOWN:
//
//                lastX = x;
//                lastY = y;
//
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                int offX ;
//                int offY ;
//                int[] movementArr=new int[4];
//                //计算移动的距离
//                offX = x - lastX;
//                offY = y - lastY;
//                movementArr= new ViewUtils().reviseTwoRectViewMovement(this,(View)this.getParent(),offX,offY);
//                nowLeft=movementArr[0];
//                nowTop=movementArr[1];
//                nowRight=movementArr[2];
//                nowBotton=movementArr[3];
//
//                mLayoutParams = (FrameLayout.LayoutParams)this.getLayoutParams();
//                mLayoutParams.setMargins(movementArr[0],movementArr[1], movementArr[2], movementArr[3]);
//                layout(nowLeft,nowTop, nowRight, nowBotton);
//        }
//
//        return true;
//    }


    public boolean isInViewRange(BaseCharacterView otherCharacter, int viewRadius) {
        boolean returnResult = false;

        boolean isInCircle = MyMathsUtils.isInCircle(new Point(this.centerX
                        , this.centerY)
                , viewRadius
                , new Point(otherCharacter.centerX
                        , otherCharacter.centerY)
        );
        if (isInCircle) {
            int relateX = otherCharacter.centerX - this.centerX;
            int relateY = otherCharacter.centerY - this.centerY;
            double cosAlpha = relateX / Math.sqrt(relateX * relateX + relateY * relateY);
            double alphaDegrees = Math.toDegrees(Math.acos(cosAlpha));
            if (relateY < 0)
                alphaDegrees = 360 - alphaDegrees;
            float startAngle = this.nowFacingAngle - this.nowViewAngle / 2;
            float endAngle = this.nowFacingAngle + this.nowViewAngle / 2;
            if (alphaDegrees > startAngle && alphaDegrees < endAngle)
                returnResult = true;
            if (startAngle < 0 && alphaDegrees > 360 + startAngle)
                returnResult = true;
            if (endAngle > 360 && alphaDegrees < endAngle - 360)
                returnResult = true;

        } else
            returnResult = false;

        return returnResult;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        setZOrderOnTop(true);
//        holder.setFormat(PixelFormat.TRANSLUCENT);


//        this.setLayoutParams(mLayoutParams);
//        gameHandler.sendEmptyMessage(GameBaseAreaActivity.GameHandler.ADD_ATTACT_RANGE);
//        gameHandler.sendEmptyMessage(GameBaseAreaActivity.GameHandler.ADD_VIEW_RANGE);
        Thread drawThread = new Thread(new CharacterDraw());
        drawThread.setDaemon(true);
        drawThread.start();
    }

    class CharacterDraw implements Runnable {


        @Override
        public void run() {

            if (isMyCharacter == false) {
                Log.i("", "");
            }
            while (GameBaseAreaActivity.gameInfo.isStop == false && isStop == false) {

                SurfaceHolder holder = getHolder();
                if (holder == null)
                    continue;
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    if (canvas == null) {
                        isStop = true;
                        break;
                    }


                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕

//                    if (isDead) {
//                        canvas.drawColor(Color.RED);
//
//                        continue;
//                    }


                    //这对象是myCharacter或队友情况下
                    if (isMyCharacter || teamID == GameBaseAreaActivity.myCharacter.teamID) {
                        if (nowHiddenLevel == HIDDEN_LEVEL_NO_HIDDEN) {
                            canvas.drawCircle(characterBodySize / 2, characterBodySize / 2, characterBodySize / 2 - borderWidth, normalPaint);
                            canvas.drawBitmap(characterPic, (int) (characterBodySize * 0.1), (int) (characterBodySize * 0.1), normalPaint);
                            canvas.rotate(nowFacingAngle, characterBodySize / 2, characterBodySize / 2);
                            if (isInvincible) {
                                canvas.drawBitmap(starPic, (int) (characterBodySize * 0.1), (int) (characterBodySize * 0.1), normalPaint);

                            }
                            if (isDead) {
                                canvas.drawARGB(255, 255, 0, 0);

                                continue;
                            }
//                        canvas.drawRect(0, 0, characterBodySize, characterBodySize, normalPaint);
//                            canvas.drawBitmap(arrowBitMap, characterBodySize - arrowBitmapWidth, (characterBodySize - arrowBitmapHeight) / 2, normalPaint);

                        } else if (nowHiddenLevel > HIDDEN_LEVEL_NO_HIDDEN) {
                            canvas.drawCircle(characterBodySize / 2, characterBodySize / 2, characterBodySize / 2 - borderWidth, alphaPaint);
                            canvas.drawBitmap(characterPic, (int) (characterBodySize * 0.1), (int) (characterBodySize * 0.1), alphaPaint);
                            canvas.rotate(nowFacingAngle, characterBodySize / 2, characterBodySize / 2);
                            if (isInvincible) {
                                canvas.drawBitmap(starPic, (int) (characterBodySize * 0.1), (int) (characterBodySize * 0.1), alphaPaint);

                            }
                            if (isDead) {
                                canvas.drawARGB(128, 255, 0, 0);

                                continue;
                            }
//                        canvas.drawRect(0, 0, characterBodySize, characterBodySize, alphaPaint);
//                            canvas.drawBitmap(arrowBitMap, characterBodySize - arrowBitmapWidth, (characterBodySize - arrowBitmapHeight) / 2, alphaPaint);

                        }
                    } else {//不是队友
                        if (isForceToBeSawByMe) {
                            canvas.drawCircle(characterBodySize / 2, characterBodySize / 2, characterBodySize / 2 - borderWidth, normalPaint);
                            canvas.drawBitmap(characterPic, (int) (characterBodySize * 0.1), (int) (characterBodySize * 0.1), normalPaint);
                            canvas.rotate(nowFacingAngle, characterBodySize / 2, characterBodySize / 2);
                            if (isInvincible) {
                                canvas.drawBitmap(starPic, (int) (characterBodySize * 0.1), (int) (characterBodySize * 0.1), normalPaint);

                            }
                            if (isDead) {
                                canvas.drawARGB(255, 255, 0, 0);

                                continue;
                            }
//                        canvas.drawRect(0, 0, characterBodySize, characterBodySize, normalPaint);
//                            canvas.drawBitmap(arrowBitMap, characterBodySize - arrowBitmapWidth, (characterBodySize - arrowBitmapHeight) / 2, normalPaint);
                            viewRange.isHidden = false;
                            attackRange.isHidden = true;

                        } else {
                            canvas.drawCircle(characterBodySize / 2, characterBodySize / 2, characterBodySize / 2 - borderWidth, transparentPaint);
                            canvas.drawBitmap(characterPic, (int) (characterBodySize * 0.1), (int) (characterBodySize * 0.1), transparentPaint);
                            canvas.rotate(nowFacingAngle, characterBodySize / 2, characterBodySize / 2);
                            if (isInvincible) {
                                canvas.drawBitmap(starPic, (int) (characterBodySize * 0.1), (int) (characterBodySize * 0.1), transparentPaint);

                            }
                            if (isDead) {
                                canvas.drawARGB(0, 255, 0, 0);

                                continue;
                            }
//                            canvas.drawBitmap(arrowBitMap, characterBodySize - arrowBitmapWidth, (characterBodySize - arrowBitmapHeight) / 2, transparentPaint);
                            viewRange.isHidden = true;
                            attackRange.isHidden = true;
                        }
                    }


                } catch (Exception e) {
                    isStop = true;
                    e.printStackTrace();
                } finally {

                    if (canvas != null) {
                        getHolder().unlockCanvasAndPost(canvas);
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public void updateInvincibleState() {
        if (isInvincible == true && new Date().getTime() - invincibleStartTime > invincibleLastTime) {
            isInvincible = false;
            invincibleStartTime = 0;
            invincibleLastTime = 0;
        }
    }

    public void deadReset() {
        targetFacingAngle = -1;
        lastInjureTime = -1;
        isReloadingAttack = false;
        isAttackting = false;
        nowReloadingAttackCount = 0;
        isKnockedAway = false;
        knockedAwayX = -99999;
        knockedAwayY = -99999;
        isJumping = false;
        jumpToX = -99999;
        jumpToY = -99999;

        if (injuryViews.size() > 0) {

            Iterator<InjuryView> iterator = injuryViews.iterator();
            if (isMyCharacter) {
                while (iterator.hasNext()) {
                    InjuryView injuryView = iterator.next();
                    GameBaseAreaActivity.baseFrame.removeView(injuryView);
                }
            }
            injuryViews.clear();

        }

        int myBaseWidth = GameBaseAreaActivity.gameInfo.mapWidth / 2;
        int myBaseHeight = GameBaseAreaActivity.gameInfo.mapHeight / 2;
        if (myBaseHeight < characterBodySize || myBaseWidth < characterBodySize) {
            Log.e("deadReset", "地图设得这么小，玩毛啊？");
            return;
        }
        Random random = new Random();
        int offX = random.nextInt(myBaseWidth);
        int offY = random.nextInt(myBaseHeight);
        synchronized (enemiesPositionSet) {
            enemiesPositionSet.clear();
        }
        long nowTime = new Date().getTime();
        if (nowTime - deadTime > 2000 && isInvincible == false) {
            isInvincible = true;
            invincibleStartTime = new Date().getTime();
            invincibleLastTime = 3000;
            if (teamID == 1) {
                nowLeft = offX;
                nowTop = offY;
                nowFacingAngle = 45;

            } else if (teamID == 2) {
                nowLeft = GameBaseAreaActivity.gameInfo.mapWidth - offX - characterBodySize;
                nowTop = offY;
                nowFacingAngle = 135;
            } else if (teamID == 3) {
                nowLeft = offX;
                nowTop = GameBaseAreaActivity.gameInfo.mapHeight - offY - characterBodySize;
                nowFacingAngle = 315;
            } else if (teamID == 4) {
                nowLeft = GameBaseAreaActivity.gameInfo.mapWidth - offX - characterBodySize;
                nowTop = GameBaseAreaActivity.gameInfo.mapHeight - offY - characterBodySize;
                nowFacingAngle = 225;
            }

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();
            layoutParams.leftMargin = nowLeft;
            layoutParams.topMargin = nowTop;
            this.setLayoutParams(layoutParams);
            centerX = nowLeft + getWidth() / 2;
            centerY = nowTop + getHeight() / 2;
            if (isMyCharacter) {
                virtualWindow.targetLeft = centerX - MyVirtualWindow.getWindowWidth(getContext()) / 2;
                virtualWindow.targetTop = centerY - MyVirtualWindow.getWindowHeight(getContext()) / 2;
            }

        }
        if (nowTime - deadTime > 3000) {
            isDead = false;
            deadTime = 0;
        }

    }

//    public void offsetLRTBParamsForOtherPlayer() {
//        if (isMyCharacter)
//            return;
//        if (isDead == true) {
//            deadReset();
//            return;
//        }
//
//
//        reactOtherPlayerMove();
//        FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) getLayoutParams();
//        mLayoutParams.leftMargin = nowLeft;
//        mLayoutParams.topMargin = nowTop;
//        centerX = nowLeft + getWidth() / 2;
//        centerY = nowTop + getHeight() / 2;
//        changeThisCharacterOnLandformses();
//        GameBaseAreaActivity.myCharacter.changeOtherCharacterState(this);
//        setLayoutParams(mLayoutParams);
//
//
//        attackRange.centerX = centerX;
//        attackRange.centerY = centerY;
//        attackRange.layoutParams.leftMargin = attackRange.centerX - attackRange.nowAttackRadius;
//        attackRange.layoutParams.topMargin = attackRange.centerY - attackRange.nowAttackRadius;
//        attackRange.setLayoutParams(attackRange.layoutParams);
//
//        viewRange.centerX = centerX;
//        viewRange.centerY = centerY;
//        viewRange.layoutParams.leftMargin = viewRange.centerX - nowViewRadius;
//        viewRange.layoutParams.topMargin = viewRange.centerY - nowViewRadius;
//        viewRange.setLayoutParams(viewRange.layoutParams);
//
//    }

    public void startKnockedAwayThread(Point toPoint) {
        if (isJumping == true) {
            isJumping = false;
            jumpToX = -99999;
            jumpToY = -99999;
        }
        knockedAwayThread = new Thread(new KnockedAwayThread(toPoint));
        knockedAwayThread.setDaemon(true);
        knockedAwayThread.start();
    }

    public class KnockedAwayThread implements Runnable {

        public Point knockedAwayToPoint;

        public KnockedAwayThread(Point knockedAwayToPoint) {
            this.knockedAwayToPoint = knockedAwayToPoint;
        }

        @Override
        public void run() {
            isKnockedAway = true;
            while (GameBaseAreaActivity.gameInfo.isStop == false && isKnockedAway) {

                int nowCenterX = (getLeft() + getRight()) / 2;
                int nowCenterY = (getTop() + getBottom()) / 2;
                int nowKnockedAwayToPointOffX = knockedAwayToPoint.x - nowCenterX;
                int nowKnockedAwayToPointOffY = knockedAwayToPoint.y - nowCenterY;
                double nowJumpToPointDistance = Math.sqrt(nowKnockedAwayToPointOffX * nowKnockedAwayToPointOffX + nowKnockedAwayToPointOffY * nowKnockedAwayToPointOffY);
                int knockedSpeed = 5 * nowSpeed;
                int realOffX = 0;
                int realOffY = 0;
                if (nowJumpToPointDistance > knockedSpeed) {
                    realOffX = (int) (knockedSpeed * nowKnockedAwayToPointOffX / nowJumpToPointDistance);
                    realOffY = (int) (knockedSpeed * nowKnockedAwayToPointOffY / nowJumpToPointDistance);
                } else {
                    realOffX = nowKnockedAwayToPointOffX;
                    realOffY = nowKnockedAwayToPointOffY;
                    isKnockedAway = false;
                }


                knockedAwayX = nowCenterX + realOffX;
                knockedAwayY = nowCenterY + realOffY;


                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isKnockedAway = false;
            knockedAwayThread = null;
        }
    }


    public void beKnockedAway(int limitLeft, int limitTop, int limitRight, int limitBottom) {
        if (isJumping == true) {
            isJumping = false;
            jumpToX = -99999;
            jumpToY = -99999;
        }
        if (knockedAwayX == -99999 || knockedAwayY == -99999)
            return;
        centerX = (nowLeft + nowRight) / 2;
        centerY = (nowTop + nowBottom) / 2;


        //注意添加Character本身宽度修正
        int realLimitLeft = limitLeft + getWidth() / 2;
        int realLimitTop = limitTop + getHeight() / 2;
        int realLimitRight = limitRight - getWidth() / 2;
        int realLimitBottom = limitBottom - getHeight() / 2;


        int realRelateLimitLeft = realLimitLeft - centerX;
        int realRelateLimitTop = realLimitTop - centerY;
        int realRelateLimitRight = realLimitRight - centerX;
        int realRelateLimitBottom = realLimitBottom - centerY;

        int resultRelateX = 0;
        int resultRelateY = 0;

        if (knockedAwayX > realLimitLeft && knockedAwayX < realLimitRight && knockedAwayY > realLimitTop && knockedAwayY < realLimitBottom) {
            nowLeft = knockedAwayX - characterBodySize / 2;
            nowTop = knockedAwayY - characterBodySize / 2;
        } else {
            isKnockedAway = false;

            int relateX = knockedAwayX - centerX;
            int relateY = knockedAwayY - centerY;

            if (relateX == 0) {
                if (relateY > 0)
                    resultRelateY = realRelateLimitBottom;
                else {
                    resultRelateY = realRelateLimitTop;
                }
            } else if (relateY == 0) {
                if (relateX > 0)
                    resultRelateX = realRelateLimitRight;
                else {
                    resultRelateX = realRelateLimitLeft;
                }
            } else {

                double tanAlpha = 0;
                try {

                    tanAlpha = (double) relateY / relateX;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (tanAlpha > 0) {
                    if (relateX > 0) {
                        //BR
                        resultRelateY = (int) (tanAlpha * realRelateLimitRight);

                        if (resultRelateY > realRelateLimitBottom) {
                            resultRelateY = realRelateLimitBottom;
                            resultRelateX = (int) (realRelateLimitBottom / tanAlpha);
                        } else {
                            resultRelateX = realRelateLimitRight;
                        }

                    } else if (relateX < 0) {
                        //TL
                        resultRelateY = (int) (tanAlpha * realRelateLimitLeft);

                        if (resultRelateY < realRelateLimitTop) {
                            resultRelateY = realRelateLimitTop;
                            resultRelateX = (int) (realRelateLimitTop / tanAlpha);
                        } else {
                            resultRelateX = realRelateLimitLeft;
                        }
                    }

                } else if (tanAlpha < 0) {
                    if (relateX > 0) {
                        //TR
                        resultRelateY = (int) (tanAlpha * realRelateLimitRight);

                        if (resultRelateY < realRelateLimitTop) {
                            resultRelateY = realRelateLimitTop;
                            resultRelateX = (int) (realRelateLimitTop / tanAlpha);
                        } else {
                            resultRelateX = realRelateLimitRight;
                        }
                    } else if (relateX < 0) {
                        //BL
                        resultRelateY = (int) (tanAlpha * realRelateLimitLeft);

                        if (resultRelateY > realRelateLimitBottom) {
                            resultRelateY = realRelateLimitBottom;
                            resultRelateX = (int) (realRelateLimitBottom / tanAlpha);
                        } else {
                            resultRelateX = realRelateLimitLeft;
                        }
                    }

                }
//            else {
//                Log.i("", "");
//            }
//            if (resultRelateX == 0 || resultRelateY == 0) {
//                Log.i("", "");
//            }
            }
            int newCenterX = centerX + resultRelateX;
            int newCenterY = centerY + resultRelateY;
//        mLayoutParams.leftMargin=bindingCharacter.centerX+resultRelateX-this.getWidth()/2;
//        mLayoutParams.topMargin=bindingCharacter.centerY+resultRelateY-this.getHeight()/2;

            nowLeft = newCenterX - getWidth() / 2;
            nowTop = newCenterY - getHeight() / 2;

        }
        nowRight = nowLeft + getWidth();
        nowBottom = nowTop + getHeight();
        if (isKnockedAway == false) {
            knockedAwayX = -99999;
            knockedAwayY = -99999;
        }
    }

    public void keepDirectionAndJump(int limitLeft, int limitTop, int limitRight, int limitBottom) {
        if (jumpToX == -99999 || jumpToY == -99999)
            return;
        centerX = (nowLeft + nowRight) / 2;
        centerY = (nowTop + nowBottom) / 2;


        //注意添加Character本身宽度修正
        int realLimitLeft = limitLeft + getWidth() / 2;
        int realLimitTop = limitTop + getHeight() / 2;
        int realLimitRight = limitRight - getWidth() / 2;
        int realLimitBottom = limitBottom - getHeight() / 2;


        int realRelateLimitLeft = realLimitLeft - centerX;
        int realRelateLimitTop = realLimitTop - centerY;
        int realRelateLimitRight = realLimitRight - centerX;
        int realRelateLimitBottom = realLimitBottom - centerY;

        int resultRelateX = 0;
        int resultRelateY = 0;

        if (jumpToX > realLimitLeft && jumpToX < realLimitRight && jumpToY > realLimitTop && jumpToY < realLimitBottom) {
            nowLeft = jumpToX - characterBodySize / 2;
            nowTop = jumpToY - characterBodySize / 2;
        } else {
            isJumping = false;

            int relateX = jumpToX - centerX;
            int relateY = jumpToY - centerY;

            if (relateX == 0) {
                if (relateY > 0)
                    resultRelateY = realRelateLimitBottom;
                else {
                    resultRelateY = realRelateLimitTop;
                }
            } else if (relateY == 0) {
                if (relateX > 0)
                    resultRelateX = realRelateLimitRight;
                else {
                    resultRelateX = realRelateLimitLeft;
                }
            } else {

                double tanAlpha = 0;
                try {

                    tanAlpha = (double) relateY / relateX;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (tanAlpha > 0) {
                    if (relateX > 0) {
                        //BR
                        resultRelateY = (int) (tanAlpha * realRelateLimitRight);

                        if (resultRelateY > realRelateLimitBottom) {
                            resultRelateY = realRelateLimitBottom;
                            resultRelateX = (int) (realRelateLimitBottom / tanAlpha);
                        } else {
                            resultRelateX = realRelateLimitRight;
                        }

                    } else if (relateX < 0) {
                        //TL
                        resultRelateY = (int) (tanAlpha * realRelateLimitLeft);

                        if (resultRelateY < realRelateLimitTop) {
                            resultRelateY = realRelateLimitTop;
                            resultRelateX = (int) (realRelateLimitTop / tanAlpha);
                        } else {
                            resultRelateX = realRelateLimitLeft;
                        }
                    }

                } else if (tanAlpha < 0) {
                    if (relateX > 0) {
                        //TR
                        resultRelateY = (int) (tanAlpha * realRelateLimitRight);

                        if (resultRelateY < realRelateLimitTop) {
                            resultRelateY = realRelateLimitTop;
                            resultRelateX = (int) (realRelateLimitTop / tanAlpha);
                        } else {
                            resultRelateX = realRelateLimitRight;
                        }
                    } else if (relateX < 0) {
                        //BL
                        resultRelateY = (int) (tanAlpha * realRelateLimitLeft);

                        if (resultRelateY > realRelateLimitBottom) {
                            resultRelateY = realRelateLimitBottom;
                            resultRelateX = (int) (realRelateLimitBottom / tanAlpha);
                        } else {
                            resultRelateX = realRelateLimitLeft;
                        }
                    }

                }
//            else {
//                Log.i("", "");
//            }
//            if (resultRelateX == 0 || resultRelateY == 0) {
//                Log.i("", "");
//            }
            }
            int newCenterX = centerX + resultRelateX;
            int newCenterY = centerY + resultRelateY;
//        mLayoutParams.leftMargin=bindingCharacter.centerX+resultRelateX-this.getWidth()/2;
//        mLayoutParams.topMargin=bindingCharacter.centerY+resultRelateY-this.getHeight()/2;

            nowLeft = newCenterX - getWidth() / 2;
            nowTop = newCenterY - getHeight() / 2;

        }
        nowRight = nowLeft + getWidth();
        nowBottom = nowTop + getHeight();
        if (isJumping == false) {
            jumpToX = -99999;
            jumpToY = -99999;
        }
    }


    /**
     * 这方法也是myCharacter专用
     */
    public void dealLocking() {
        if (isLocking == false)
            return;
        if (isReloadingAttack)
            return;
        if (targetFacingAngle >= 0)
            return;
        int relateX = 0;
        int relateY = 0;

        if (lockingCharacter != null) {

            if (lockingCharacter.isDead || lockingCharacter.isForceToBeSawByMe == false) {
                lockingCharacter = null;
                return;
            } else {
                relateX = lockingCharacter.centerX - this.centerX;
                relateY = lockingCharacter.centerY - this.centerY;
                if (relateX == 0 && relateY == 0) {
                    lockingCharacter = null;
                    return;
                }
                try {
                    targetFacingAngle = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                float relateAngle = targetFacingAngle - this.nowFacingAngle;
                if (Math.abs(relateAngle) > 180) {//处理旋转最佳方向
                    if (relateAngle > 0)
                        relateAngle = relateAngle - 360;

                    else
                        relateAngle = 360 - relateAngle;
                }
                if (Math.abs(relateAngle) > this.nowAngleChangSpeed)
                    relateAngle = Math.abs(relateAngle) / relateAngle * this.nowAngleChangSpeed;

                this.nowFacingAngle = this.nowFacingAngle + relateAngle;

                if (this.nowFacingAngle < 0)
                    this.nowFacingAngle = this.nowFacingAngle + 360;
                else if (this.nowFacingAngle > 360)
                    this.nowFacingAngle = this.nowFacingAngle - 360;

            }
            return;
        } else {
            for (BaseCharacterView character : GameBaseAreaActivity.gameInfo.allCharacters) {
                if (character.teamID == this.teamID)
                    continue;
                if (character.isForceToBeSawByMe) {
                    lockingCharacter = character;
                    return;
                }
            }
        }

    }


    public void switchLockingState(Boolean isLocking) {
        this.isLocking = isLocking;
    }

    public synchronized void attack() {
        if (attackMediaPlayer == null || GameBaseAreaActivity.gameInfo.isStop == true)
            return;
        if (isMyCharacter == false) {
            BaseCharacterView myCharacter = GameBaseAreaActivity.myCharacter;
            int myHearRadius = myCharacter.nowHearRadius;
            if (this.characterType == CHARACTER_TYPE_HUNTER)
                myHearRadius = 3 * myHearRadius;
            int relateX = myCharacter.centerX - centerX;
            int relateY = myCharacter.centerY - centerY;
            double distance = Math.sqrt(relateX * relateX + relateY * relateY);
            if (distance > myHearRadius) {
                return;
            }
            float leftVol = 0;
            float rightVol = 0;
            if (relateX > 0) {
                rightVol = (float) (myHearRadius - distance) / myHearRadius;
                leftVol = rightVol / 2;
            } else if (relateX < 0) {
                leftVol = (float) (myHearRadius - distance) / myHearRadius;
                rightVol = leftVol / 2;
            }
            if (leftVol > 1)
                leftVol = 1;
            if (rightVol > 1)
                rightVol = 1;
            try {
                attackMediaPlayer.setVolume(leftVol, rightVol);
            } catch (Exception e) {
                attackMediaPlayer.release();
            }

            Point nowPosition = new Point(centerX, centerY);
            CharacterPosition characterPosition = new CharacterPosition(nowPosition, this, new Date().getTime(), 3000);
            Vector<CharacterPosition> enemiesPositionSet = GameBaseAreaActivity.myCharacter.enemiesPositionSet;
            synchronized (enemiesPositionSet) {
                Iterator<CharacterPosition> iterator = enemiesPositionSet.iterator();
                while (iterator.hasNext()) {
                    CharacterPosition oldPosition = iterator.next();
                    if (oldPosition.character == this) {
                        iterator.remove();
                    }
                }
                GameBaseAreaActivity.myCharacter.enemiesPositionSet.add(characterPosition);
            }
        }
        attackMediaPlayer.seekTo(0);
        attackMediaPlayer.start();


    }

    public void reloadAttackCount() {
        if (reloadAttackCountThread != null && reloadAttackCountThread.getState() != Thread.State.TERMINATED)
            return;
        if (reloadMediaPlayer != null) {
            reloadMediaPlayer.seekTo(0);
            if (reloadMediaPlayer == null)
                return;
            if (isMyCharacter == false) {
                BaseCharacterView myCharacter = GameBaseAreaActivity.myCharacter;
                int relateX = myCharacter.centerX - centerX;
                int relateY = myCharacter.centerY - centerY;
                double distance = Math.sqrt(relateX * relateX + relateY * relateY);
                if (distance > nowHearRadius) {
                    return;
                }
                float leftVol = 0;
                float rightVol = 0;
                if (relateX > 0) {
                    rightVol = (float) (nowHearRadius - distance) / nowHearRadius;
                    leftVol = rightVol / 2;
                } else if (relateX < 0) {
                    leftVol = (float) (nowHearRadius - distance) / nowHearRadius;
                    rightVol = leftVol / 2;
                }
                if (leftVol > 1)
                    leftVol = 1;
                if (rightVol > 1)
                    rightVol = 1;
                attackMediaPlayer.setVolume(leftVol, rightVol);

            }
        }
        reloadMediaPlayer.start();
    }


    public synchronized void smell() {
        if (smellThread != null) {
            return;
//
//            if(isSmelling){
//                isSmelling=false;
//                try {
//                    smellThread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                smellThread=null;
//                return;
//            }
        }
        nowSmellCount = 0;
        smellThread = new SmellThread(this);
        smellThread.setDaemon(true);
        smellThread.start();
    }

    class SmellThread extends Thread {
        BaseCharacterView bindingCharacter;

        SmellThread(BaseCharacterView bindingCharacter) {
            this.bindingCharacter = bindingCharacter;
        }


        @Override
        public void run() {
            synchronized (enemiesPositionSet) {
                enemiesPositionSet.clear();
                isSmelling = true;
                try {
                    while (GameBaseAreaActivity.gameInfo.isStop == false && isSmelling) {
                        nowSmellCount += nowSmellSpeed;
                        if (nowSmellCount > smellTotalCount)
                            nowSmellCount = smellTotalCount;
                        if (nowSmellCount == smellTotalCount) {
                            long nowTime = new Date().getTime();
                            Point thisCharacterPosition = new Point(bindingCharacter.centerX, bindingCharacter.centerY);
                            for (BaseCharacterView character : GameBaseAreaActivity.gameInfo.allCharacters) {
                                if (character.teamID == bindingCharacter.teamID)
                                    continue;

                                Point positionPoint = new Point(character.centerX, character.centerY);
                                CharacterPosition characterPosition = new CharacterPosition(positionPoint, character, nowTime, 3000);
                                double distance = MyMathsUtils.getDistance(positionPoint, thisCharacterPosition);
                                if (distance <= nowSmellRadius) {
                                    enemiesPositionSet.add(characterPosition);
                                }
                            }
                            break;
                        }

                        Thread.sleep(smellSleepTime);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    nowSmellCount = 0;
                    isSmelling = false;
                    smellThread = null;
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isStop = true;
        if (moveMediaPlayer != null)
            moveMediaPlayer.release();
        if (attackMediaPlayer != null)
            attackMediaPlayer.release();
        if (reloadMediaPlayer != null)
            reloadMediaPlayer.release();

    }


}

