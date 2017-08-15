package com.jedi.wolf_and_hunter.engine;

import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.ai.BaseAI;
import com.jedi.wolf_and_hunter.ai.HunterAI;
import com.jedi.wolf_and_hunter.ai.WolfAI;
import com.jedi.wolf_and_hunter.myObj.gameObj.GameInfo;
import com.jedi.wolf_and_hunter.myObj.gameObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myObj.gameObj.PlayerInfo;
import com.jedi.wolf_and_hunter.myViews.button.AttackButton;
import com.jedi.wolf_and_hunter.myViews.mapBase.GameMap;
import com.jedi.wolf_and_hunter.myViews.rocker.JRocker;
import com.jedi.wolf_and_hunter.myViews.rocker.LeftRocker;
import com.jedi.wolf_and_hunter.myViews.button.LockingButton;
import com.jedi.wolf_and_hunter.myViews.mapBase.MapBaseFrame;
import com.jedi.wolf_and_hunter.myViews.range.PromptView;
import com.jedi.wolf_and_hunter.myViews.rocker.RightRocker;
import com.jedi.wolf_and_hunter.myViews.SightView;
import com.jedi.wolf_and_hunter.myViews.button.SmellButton;
import com.jedi.wolf_and_hunter.myViews.tempView.InjuryView;
import com.jedi.wolf_and_hunter.myViews.tempView.Trajectory;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.myViews.characters.NormalHunter;
import com.jedi.wolf_and_hunter.myViews.characters.NormalWolf;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by Administrator on 2017/7/17.
 */

public class GameMainEngine {
    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView t4;
    private TextView t5;
    private TextView t6;
    private TextView gameResult;
    private GameInfo gameInfo;
    private MediaPlayer backGroundMediaPlayer;
    private FrameLayout baseFrame;
    private MapBaseFrame mapBaseFrame;
    private MyVirtualWindow virtualWindow;

    private GameMap gameMap;
    private GameBaseAreaActivity gameBaseAreaActivity;
    private GameHandler gameHandler = new GameHandler();

    private LeftRocker leftRocker;
    private RightRocker rightRocker;
    private AttackButton atttackButton;
    private LockingButton lockingButton;
    private SmellButton smellButton;
    private BaseCharacterView myCharacter;
    private BaseCharacterView focusCharacter;
    private Timer timerForAllMoving = new Timer();
    private Timer timerForTrajectory = new Timer();
    private ArrayList<Timer> timerForAIList = new ArrayList<Timer>();
    public boolean isStop = false;
    public boolean isPause = false;

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public MediaPlayer getBackGroundMediaPlayer() {
        return backGroundMediaPlayer;
    }

    public FrameLayout getBaseFrame() {
        return baseFrame;
    }

    public MapBaseFrame getMapBaseFrame() {
        return mapBaseFrame;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public MyVirtualWindow getVirtualWindow() {
        return virtualWindow;
    }

    public GameMainEngine(GameBaseAreaActivity gameBaseAreaActivity) {
        this.gameBaseAreaActivity = gameBaseAreaActivity;
        gameInfo = (GameInfo) gameBaseAreaActivity.getIntent().getExtras().get("gameInfo");
        if (gameInfo == null)
            gameBaseAreaActivity.finish();

        backGroundMediaPlayer = MediaPlayer.create(gameBaseAreaActivity, R.raw.background);
        ViewUtils.initWindowParams(gameBaseAreaActivity);
        DisplayMetrics dm = ViewUtils.getWindowsDisplayMetrics();
        baseFrame = (FrameLayout) gameBaseAreaActivity.findViewById(R.id.baseFrame);
        mapBaseFrame = new MapBaseFrame(gameBaseAreaActivity, gameInfo.mapWidth, gameInfo.mapHeight);
        baseFrame.addView(mapBaseFrame);
        virtualWindow = new MyVirtualWindow(gameBaseAreaActivity, mapBaseFrame);
        gameMap = new GameMap(gameBaseAreaActivity);


        t1 = new TextView(gameBaseAreaActivity);
        t1.setTextColor(Color.WHITE);
        t1.setTextSize(15);
        t2 = new TextView(gameBaseAreaActivity);
        t2.setTextColor(Color.WHITE);
        t2.setTextSize(15);
        t3 = new TextView(gameBaseAreaActivity);
        t3.setTextColor(Color.WHITE);
        t3.setTextSize(15);
        t4 = new TextView(gameBaseAreaActivity);
        t4.setTextColor(Color.WHITE);
        t4.setTextSize(15);
        t5 = new TextView(gameBaseAreaActivity);
        t5.setTextColor(Color.WHITE);
        t5.setTextSize(15);
        t6 = new TextView(gameBaseAreaActivity);
        t6.setTextColor(Color.WHITE);
        t6.setTextSize(15);
        gameResult = new TextView(gameBaseAreaActivity);
        gameResult.setTextColor(Color.WHITE);
        gameResult.setTextSize(100);
        FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p1.leftMargin = 0;
        p1.topMargin = 50;
        t1.setLayoutParams(p1);
        FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p2.leftMargin = 0;
        p2.topMargin = 100;
        t2.setLayoutParams(p2);
        FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p3.leftMargin = 0;
        p3.topMargin = 150;
        t3.setLayoutParams(p3);
        FrameLayout.LayoutParams p4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p4.leftMargin = 0;
        p4.topMargin = 200;
        t4.setLayoutParams(p4);
        FrameLayout.LayoutParams p5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p5.leftMargin = 0;
        p5.topMargin = 250;
        t5.setLayoutParams(p5);
        FrameLayout.LayoutParams p6 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p6.leftMargin = 0;
        p6.topMargin = 300;
        t6.setLayoutParams(p6);
        FrameLayout.LayoutParams p7 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p7.leftMargin = MyVirtualWindow.getWindowWidth(gameBaseAreaActivity) / 2 - 200;
        p7.topMargin = MyVirtualWindow.getWindowHeight(gameBaseAreaActivity) / 2;
        gameResult.setLayoutParams(p7);

        baseFrame.addView(t1);
        baseFrame.addView(t2);
        baseFrame.addView(t3);
        baseFrame.addView(t4);
        baseFrame.addView(t5);
        baseFrame.addView(t6);
        baseFrame.addView(gameResult);


    }


    public class GameHandler extends Handler {
        public static final int ADD_TRAJECTORY = 1;
        public static final int REMOVE_TRAJECTORY = 2;
        public static final int UPDATE_OTHER_ONLINE_PLAYER = 3;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_TRAJECTORY:
                    Trajectory trajectory = (Trajectory) (msg.obj);
                    trajectory.addTime = new Date().getTime();
                    gameInfo.allTrajectories.add(trajectory);
                    trajectory.addTrajectory(mapBaseFrame);
                    break;
                case REMOVE_TRAJECTORY:
                    long nowTime = new Date().getTime();
                    ArrayList<Trajectory> removeTrajectories = new ArrayList<Trajectory>();
                    Iterator<Trajectory> iterator = gameInfo.allTrajectories.iterator();
                    while (iterator.hasNext()) {
                        Trajectory t = iterator.next();
                        if (nowTime - t.addTime > 1000) {
                            iterator.remove();
                            t.parent.removeView(t);
                        }
                    }
                    break;
                default:
                    int team1KillCount = 0;
                    int team2KillCount = 0;
                    int team3KillCount = 0;
                    int team4KillCount = 0;
                    for (BaseCharacterView character : gameInfo.allCharacters) {
                        if (character.getTeamID() == 1)
                            team1KillCount += character.killCount;
                        else if (character.getTeamID() == 2)
                            team2KillCount += character.killCount;
                        else if (character.getTeamID() == 3)
                            team3KillCount += character.killCount;
                        else if (character.getTeamID() == 4)
                            team4KillCount += character.killCount;
                    }
                    if (team1KillCount >= gameInfo.targetKillCount) {
                        gameResult.setText("1队胜");
                        isStop = true;
                    }
                    if (team2KillCount >= gameInfo.targetKillCount) {
                        gameResult.setText("2队胜");
                        isStop = true;
                    }
                    if (team3KillCount >= gameInfo.targetKillCount) {
                        gameResult.setText("3队胜");
                        isStop = true;
                    }
                    if (team4KillCount >= gameInfo.targetKillCount) {
                        gameResult.setText("4队胜");
                        isStop = true;

                    }
                    if (isStop)
                        return;
                    reflash();
            }


            for (int i = 0; i < gameInfo.allCharacters.size(); i++) {
                BaseCharacterView c = gameInfo.allCharacters.get(i);
                TextView target = null;
                if (i == 0)
                    target = t1;
                else if (i == 1)
                    target = t2;
                else if (i == 2)
                    target = t3;
                else if (i == 3)
                    target = t4;
                if (target == null)
                    continue;
                target.setText((i + 1) + "P(" + c.nowHealthPoint + "):杀" + Integer.toString(c.killCount) + "  挂" + Integer.toString(c.dieCount));


            }
//            t5.setText("intent:"+testingAI.intent);
//            t6.setText("nowLeft:"+testingAI.bindingCharacter.nowLeft);
//            t7.setText("nowTop:"+testingAI.bindingCharacter.nowTop);

//            t1.invalidate();
//            t2.invalidate();
//            t3.invalidate();
//            t4.invalidate();
//            t5.invalidate();

        }

    }

    private class GameMainTask extends TimerTask {
        @Override
        public void run() {

            try {
                if(isStop||isPause)
                    backGroundMediaPlayer.pause();
                if (backGroundMediaPlayer.isPlaying() == false) {
                    backGroundMediaPlayer.setLooping(true);
                    backGroundMediaPlayer.seekTo(0);
                    backGroundMediaPlayer.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
                backGroundMediaPlayer=MediaPlayer.create(gameBaseAreaActivity, R.raw.background);
            }
//            if (backGroundMusicThread == null) {
//                backGroundMusicThread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        while (gameInfo.isStop == false) {
//                            if (backGround.isPlaying() == false) {
//                                backGround.seekTo(0);
//                                backGround.start();
//                            }
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        backGround.stop();
//
//                    }
//                });
//                backGroundMusicThread.setDaemon(true);
//                backGroundMusicThread.start();
//            }

            gameHandler.sendEmptyMessage(0);
        }
    }

    private class RemoveTrajectoryTask extends TimerTask {
        @Override
        public void run() {
            gameHandler.sendEmptyMessage(GameHandler.REMOVE_TRAJECTORY);
        }
    }

    public void runEngine() {


        //添加地形


        mapBaseFrame.addView(gameMap);
        gameMap.buildLandforms(gameBaseAreaActivity);


        //添加我的角色
        gameInfo.myPlayerInfo = gameInfo.playerInfos.get(0);


        if (gameInfo.myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER)
            myCharacter = new NormalHunter(gameBaseAreaActivity, virtualWindow);
        else {
            myCharacter = new NormalWolf(gameBaseAreaActivity, virtualWindow);
        }
        focusCharacter = myCharacter;
        PromptView promptView = new PromptView(gameBaseAreaActivity, myCharacter);
        mapBaseFrame.addView(promptView);
        myCharacter.promptView = promptView;

        myCharacter.setTeamID(gameInfo.myPlayerInfo.teamID);
        myCharacter.isMyCharacter = true;
        myCharacter.gameHandler = gameHandler;

        gameInfo.allCharacters.add(myCharacter);
//        mapBaseFrame.addView(myCharacter);
        gameBaseAreaActivity.myCharacter = myCharacter;
        mapBaseFrame.myCharacter = myCharacter;


//        NormalHunter testCharacter = new NormalHunter(this, virtualWindow);
//        testCharacter.setTeamID(2);
//        gameInfo.allCharacters.add(testCharacter);

//        //添加视点
//        gameInfo.mySight = new SightView(gameBaseAreaActivity);
//        gameInfo.mySight.virtualWindow = this.virtualWindow;
//        gameInfo.mySight.sightSize = myCharacter.characterBodySize;
//        if (gameInfo.controlMode == GameInfo.CONTROL_MODE_NORMAL)
//            gameInfo.mySight.isHidden = true;
//
//        myCharacter.setSight(gameInfo.mySight);
//        if (gameInfo.mySight.isHidden == false) {
//            mapBaseFrame.addView(gameInfo.mySight);
//            mapBaseFrame.mySight = gameInfo.mySight;
//        }


        atttackButton = (AttackButton) gameBaseAreaActivity.findViewById(R.id.attack_button_right);
        int buttonSize = atttackButton.buttonSize;
        atttackButton.bindingCharacter = myCharacter;
        FrameLayout.LayoutParams rabp = (FrameLayout.LayoutParams) atttackButton.getLayoutParams();
        if (rabp == null) {
            rabp = new FrameLayout.LayoutParams(buttonSize, buttonSize);
        }
        //添加摇杆
        leftRocker = (LeftRocker) gameBaseAreaActivity.findViewById(R.id.rocker_left);
        leftRocker.setBindingCharacter(myCharacter);
        mapBaseFrame.leftRocker = leftRocker;
        rightRocker = (RightRocker) gameBaseAreaActivity.findViewById(R.id.rocker_right);

        if (gameInfo.myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER) {
            atttackButton.reCreateBitmap();
            rightRocker.setBindingCharacter(myCharacter);
            mapBaseFrame.rightRocker = rightRocker;
            FrameLayout.LayoutParams rrlp = (FrameLayout.LayoutParams) rightRocker.getLayoutParams();
            rrlp.rightMargin = atttackButton.buttonSize - JRocker.rockerRadius;

            rabp.leftMargin = MyVirtualWindow.getWindowWidth(gameBaseAreaActivity) - atttackButton.buttonSize;
            rabp.topMargin = MyVirtualWindow.getWindowHeight(gameBaseAreaActivity) - JRocker.viewWidth / 2 - atttackButton.buttonSize;


            lockingButton = new LockingButton(gameBaseAreaActivity);
            int lockingButtonSize = lockingButton.buttonSize;
            lockingButton.bindingCharacter = myCharacter;
            FrameLayout.LayoutParams lblp = (FrameLayout.LayoutParams) lockingButton.getLayoutParams();
            if (lblp == null) {
                lblp = new FrameLayout.LayoutParams(lockingButtonSize, lockingButtonSize);
            }
            lblp.leftMargin = rabp.leftMargin;
            lblp.topMargin = MyVirtualWindow.getWindowHeight(gameBaseAreaActivity) - JRocker.viewWidth / 2 + 10;
            lockingButton.setLayoutParams(lblp);
            baseFrame.addView(lockingButton);


        } else if (gameInfo.myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF) {
            atttackButton.reCreateBitmap();
            baseFrame.removeView(rightRocker);
            atttackButton.buttonSize = (int) (atttackButton.buttonSize * 5 / 4);
            atttackButton.reCreateBitmap();
            rabp.leftMargin = MyVirtualWindow.getWindowWidth(gameBaseAreaActivity) - atttackButton.buttonSize - 50;
            rabp.topMargin = MyVirtualWindow.getWindowHeight(gameBaseAreaActivity) - JRocker.viewWidth / 2;
            smellButton = new SmellButton(gameBaseAreaActivity);
            int smellButtonSize = smellButton.buttonSize;
            smellButton.bindingCharacter = myCharacter;
            FrameLayout.LayoutParams sblp = (FrameLayout.LayoutParams) smellButton.getLayoutParams();
            if (sblp == null) {
                sblp = new FrameLayout.LayoutParams(smellButtonSize, smellButtonSize);
            }
            sblp.leftMargin = rabp.leftMargin - smellButtonSize;
            sblp.topMargin = rabp.topMargin;
            smellButton.setLayoutParams(sblp);
            baseFrame.addView(smellButton);
        }
        atttackButton.setLayoutParams(rabp);
        leftRocker.bringToFront();
        rightRocker.bringToFront();
        atttackButton.bringToFront();
//
//        if (myCharacter.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER) {
//
//        } else if (myCharacter.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF) {
//
//        }
        if (gameInfo.playMode.equals("single")) {
            startAI();
        } else if (gameInfo.playMode.equals("bluetooth")) {
            PlayerInfo remotePlayerInfo = gameInfo.playerInfos.get(1);
            BaseCharacterView otherCharacter;
            if (remotePlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER)
                otherCharacter = new NormalHunter(gameBaseAreaActivity, virtualWindow);
            else {
                otherCharacter = new NormalWolf(gameBaseAreaActivity, virtualWindow);
            }
            otherCharacter.setTeamID(remotePlayerInfo.teamID);
            otherCharacter.isMyCharacter = false;
            otherCharacter.gameHandler = gameHandler;

            gameInfo.allCharacters.add(otherCharacter);

        }
        for (BaseCharacterView character : gameInfo.allCharacters) {
//            int left = -1;
//            int top = -1;
//            float facingAngle = -1;
//
//            if (character.getTeamID() == 1) {
//                left = 50;
//                top = 50;
//                facingAngle = 45;
//
//            } else if (character.getTeamID() == 2) {
//                left = MyVirtualWindow.getWindowWidth(this) - character.characterBodySize - 50;
//                top = 50;
//                facingAngle = 135;
//            } else if (character.getTeamID() == 3) {
//                left = 50;
//                top = MyVirtualWindow.getWindowHeight(this) - character.characterBodySize - 50;
//                facingAngle = 315;
//            } else if (character.getTeamID() == 4) {
//                left = MyVirtualWindow.getWindowWidth(this) - character.characterBodySize - 50;
//                top = MyVirtualWindow.getWindowHeight(this) - character.characterBodySize - 50;
//                facingAngle = 225;
//            }
//
//            if (left > 0 && top > 0 && facingAngle > 0) {
//                FrameLayout.LayoutParams characterParams = (FrameLayout.LayoutParams) character.getLayoutParams();
//                characterParams.leftMargin = left;
//                characterParams.topMargin = top;
//                character.nowFacingAngle = facingAngle;
//                character.centerX=left+character.getWidth()/2;
//                character.centerY=top+character.getHeight()/2;
//                new AttackRange(this, character);
//                new ViewRange(this, character);
//                character.setLayoutParams(characterParams);
//
            if (character == focusCharacter) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mapBaseFrame.getLayoutParams();
                params.leftMargin = -(character.centerX - MyVirtualWindow.getWindowWidth(gameBaseAreaActivity) / 2);
                params.topMargin = -(character.centerY - MyVirtualWindow.getWindowHeight(gameBaseAreaActivity) / 2);
                mapBaseFrame.setLayoutParams(params);
            }
            mapBaseFrame.addView(character);
            mapBaseFrame.addView(character.attackRange);
            mapBaseFrame.addView(character.viewRange);
//            }
        }
        mapBaseFrame.invalidate();
        t1.bringToFront();
        t2.bringToFront();
        t3.bringToFront();
        t4.bringToFront();
        t5.bringToFront();
        if (lockingButton != null)
            lockingButton.bringToFront();
        if (smellButton != null)
            smellButton.bringToFront();

        timerForAllMoving.scheduleAtFixedRate(new GameMainTask(), 1000, 30);
        timerForTrajectory.schedule(new RemoveTrajectoryTask(), 1000, 300);
    }

    public void stopEngine() {
        isStop = true;
        timerForTrajectory.cancel();
        backGroundMediaPlayer.release();
        if (timerForAllMoving != null)
            timerForAllMoving.cancel();


        for (Timer timer : timerForAIList) {
            timer.cancel();
        }


    }

    public void pauseEngine() {
        isPause = true;
    }

    public void restartEngine() {
        isPause = false;
        for (BaseCharacterView character : gameInfo.allCharacters) {
            character.initBitmapAndMedia();
            if (character.drawThread == null || character.drawThread.getState() == Thread.State.TERMINATED) {
                character.runDrawThread();
            }
        }
    }

    private void startAI() {
        for (int i = 1; i < gameInfo.playerInfos.size(); i++) {
            PlayerInfo playerInfo = gameInfo.playerInfos.get(i);
            BaseAI ai = null;
            if (playerInfo.isAvailable == false)
                continue;
            BaseCharacterView aiCharacter = null;
            if (playerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER) {
                aiCharacter = new NormalHunter(gameBaseAreaActivity, virtualWindow);
                ai = new HunterAI(aiCharacter);
            } else if (playerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF) {
                aiCharacter = new NormalWolf(gameBaseAreaActivity, virtualWindow);
                ai = new WolfAI(aiCharacter);
            }
            aiCharacter.gameHandler = gameHandler;
            aiCharacter.setTeamID(playerInfo.teamID);


            gameInfo.allCharacters.add(aiCharacter);
//            if (true)
//                continue;
            Timer timerForAI = new Timer("AIPlayer1", true);
            timerForAI.schedule(ai, 1000, 30);
            timerForAIList.add(timerForAI);

        }


    }

    public void dealNeedToBeKilled() {
        synchronized (gameInfo.beAttackedList) {
            Iterator<HashMap<BaseCharacterView, BaseCharacterView>> iterator = gameInfo.beAttackedList.iterator();

            while (iterator.hasNext()) {
                Map<BaseCharacterView, BaseCharacterView> attackMap = iterator.next();
                Set<Map.Entry<BaseCharacterView, BaseCharacterView>> entrySet = attackMap.entrySet();
                for (Map.Entry<BaseCharacterView, BaseCharacterView> entry : entrySet) {
                    BaseCharacterView attackCharacter = entry.getKey();
                    BaseCharacterView beAttackedCharacter = entry.getValue();
                    if (beAttackedCharacter.isInvincible)
                        break;
                    if (beAttackedCharacter.isDead)
                        break;
                    int relateX = beAttackedCharacter.centerX - attackCharacter.centerX;
                    int relateY = beAttackedCharacter.centerY - attackCharacter.centerY;
                    if (relateX == 0 && relateY == 0) {
                        beAttackedCharacter.isDead = true;
                        beAttackedCharacter.dieCount++;
                        beAttackedCharacter.deadTime = new Date().getTime();
                        attackCharacter.killCount++;
                        continue;
                    }
                    float angleBetweenXAxus = 0;
                    try {
                        angleBetweenXAxus = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    float relateFacingAngle = Math.abs(beAttackedCharacter.nowFacingAngle - angleBetweenXAxus);

                    if (relateFacingAngle < 90 || relateFacingAngle > 270) {//背击
                        beAttackedCharacter.nowHealthPoint = 0;
                    } else {
                        beAttackedCharacter.nowHealthPoint -= 1;
                    }
                    if (beAttackedCharacter.nowHealthPoint <= 0) {
                        beAttackedCharacter.isDead = true;
                        beAttackedCharacter.dieCount++;
                        beAttackedCharacter.deadTime = new Date().getTime();
                        attackCharacter.killCount++;
                    } else {
                        if (beAttackedCharacter.knockedAwayThread == null || beAttackedCharacter.knockedAwayThread.getState() == Thread.State.TERMINATED) {
                            double cosAlpha = Math.cos(Math.toRadians(attackCharacter.nowFacingAngle));
                            double offX = cosAlpha * attackCharacter.nowKnockAwayStrength;

                            double offY = Math.sqrt(attackCharacter.nowKnockAwayStrength * attackCharacter.nowKnockAwayStrength - offX * offX);
                            if (attackCharacter.nowFacingAngle >= 180)
                                offY = -offY;
                            double endX = offX + beAttackedCharacter.centerX;
                            double endY = offY + beAttackedCharacter.centerY;
                            Point toPoint = new Point((int) endX, (int) endY);
                            beAttackedCharacter.startKnockedAwayThread(toPoint);
                        }

                        InjuryView injuryView = new InjuryView(gameBaseAreaActivity, beAttackedCharacter, angleBetweenXAxus);
                        int left = 0;
                        int top = 0;
                        if (beAttackedCharacter.isMyCharacter) {
                            Random r = new Random();
                            left = r.nextInt(MyVirtualWindow.getWindowWidth(gameBaseAreaActivity) - injuryView.viewSize);
                            top = r.nextInt(MyVirtualWindow.getWindowHeight(gameBaseAreaActivity) - injuryView.viewSize);

                        } else {
                            left = beAttackedCharacter.centerX - injuryView.viewSize / 2;
                            top = beAttackedCharacter.centerY - injuryView.viewSize / 2;
                        }
                        injuryView.centerX = left + injuryView.viewSize / 2;
                        injuryView.centerY = top + injuryView.viewSize / 2;
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) injuryView.getLayoutParams();
                        if (layoutParams == null) {
                            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        }
                        layoutParams.leftMargin = left;
                        layoutParams.topMargin = top;

                        injuryView.setLayoutParams(layoutParams);

                        beAttackedCharacter.injuryViews.add(injuryView);

                        beAttackedCharacter.lastInjureTime = new Date().getTime();

                    }
                }

            }

            gameInfo.beAttackedList.clear();
        }
    }

    private void dealInjury() {
        for (BaseCharacterView character : gameInfo.allCharacters) {

            if (character.injuryViews.size() == 0)
                continue;
            long nowTime = new Date().getTime();
            Iterator<InjuryView> iterator = character.injuryViews.iterator();
            while (iterator.hasNext()) {
                InjuryView injuryView = iterator.next();
                if (character.isMyCharacter) {
                    if (injuryView.hasAddedToBaseFrame) {
                        if (nowTime - injuryView.createTime > character.nowRecoverTime) {
                            character.nowHealthPoint++;
                            baseFrame.removeView(injuryView);
                            iterator.remove();
                        }
                    } else {
                        baseFrame.addView(injuryView);
                        injuryView.hasAddedToBaseFrame = true;
                        injuryView.bringToFront();
                    }
                } else {
                    if (nowTime - injuryView.createTime > character.nowRecoverTime) {
                        character.nowHealthPoint++;
                        iterator.remove();
                    }
                }

            }
        }
    }

    private synchronized void reflash() {

        if (myCharacter == null || (leftRocker == null && rightRocker == null) || mapBaseFrame == null||isPause||isStop)
            return;

        if (gameInfo.beAttackedList.size() > 0) {
            dealNeedToBeKilled();
        }
        //处理攻击导致的受伤或死亡


        for (BaseCharacterView c : gameInfo.allCharacters) {


            synchronized (c) {
                c.hasUpdatedPosition = false;
                c.updateInvincibleState();
                c.updateNowPosition();
                c.dealInjury();
                if (c.isAttackting) {
                    c.attack();
                }
                if (c.isReloadingAttack == true && c.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER)
                    c.reloadAttackCount();
                if (c.isDead == true) {
                    c.deadReset();
                    continue;
                }
                if (c.isKnockedAway) {
                    c.beKnockedAway(0, 0, mapBaseFrame.getWidth(), mapBaseFrame.getHeight());
                } else if (c.isJumping) {
                    c.keepDirectionAndJump(0, 0, mapBaseFrame.getWidth(), mapBaseFrame.getHeight());
                } else {
                    if (c.needMove) {
                        if (c.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER)
                            c.reactHunterMove();
                        else if (c.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF)
                            c.reactWolfMove();
                    }
                }
                if (c.isMyCharacter && c.isLocking) {
                    c.dealLocking();
                }
//                        if(c.getTeamID()==2){
//                            Log.i("Player2 nowLeft",Integer.toString(c.nowLeft));
//                        }
                FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) c.getLayoutParams();
                mLayoutParams.leftMargin = c.nowLeft;
                mLayoutParams.topMargin = c.nowTop;
                c.centerX = c.nowLeft + c.getWidth() / 2;
                c.centerY = c.nowTop + c.getHeight() / 2;

                c.setLayoutParams(mLayoutParams);


                //处理地形影响
                c.changeThisCharacterOnLandformses();

                //攻击范围view跟随移动
                c.attackRange.centerX = c.centerX;
                c.attackRange.centerY = c.centerY;
                c.attackRange.layoutParams.leftMargin = c.attackRange.centerX - c.nowAttackRadius;
                c.attackRange.layoutParams.topMargin = c.attackRange.centerY - c.nowAttackRadius;
                c.attackRange.setLayoutParams(c.attackRange.layoutParams);

                //视觉范围view跟随移动
                c.viewRange.centerX = c.centerX;
                c.viewRange.centerY = c.centerY;
                FrameLayout.LayoutParams viewRangeLP = (FrameLayout.LayoutParams) c.viewRange.getLayoutParams();
                viewRangeLP.leftMargin = c.viewRange.centerX - c.nowViewRadius;
                viewRangeLP.topMargin = c.viewRange.centerY - c.nowViewRadius;
                c.viewRange.setLayoutParams(viewRangeLP);

                //探测View跟随移动
                if (c.promptView != null) {
                    c.promptView.centerX = c.centerX;
                    c.promptView.centerY = c.centerY;
                    c.promptView.layoutParams.leftMargin = c.promptView.centerX - c.promptView.viewSize / 2;
                    c.promptView.layoutParams.topMargin = c.promptView.centerY - c.promptView.viewSize / 2;

                    c.promptView.setLayoutParams(c.promptView.layoutParams);
                }

//            myCharacter.viewRange.invalidate();

                c.targetFacingAngle = -1;
                c.hasUpdatedPosition = false;
                int relateX = myCharacter.centerX - c.centerX;
                int relateY = myCharacter.centerY - c.centerY;
                double distance = Math.sqrt(relateX * relateX + relateY * relateY);
                if (c.isMyCharacter || distance < myCharacter.nowHearRadius) {
                    c.startMovingMediaThread();
                }

            }
        }
        virtualWindow.virtualWindowPassiveFollow(gameBaseAreaActivity, focusCharacter);
        myCharacter.changeOtherCharacterLandformState();
        virtualWindow.reflashWindowPosition();
        mapBaseFrame.invalidate();


    }
}
