package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jedi.wolf_and_hunter.ai.BaseAI;
import com.jedi.wolf_and_hunter.ai.WolfAI;
import com.jedi.wolf_and_hunter.myObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myObj.PlayerInfo;
import com.jedi.wolf_and_hunter.myViews.AttackButton;
import com.jedi.wolf_and_hunter.myViews.AttackRange;
import com.jedi.wolf_and_hunter.myViews.GameMap;
import com.jedi.wolf_and_hunter.myViews.LeftRocker;
import com.jedi.wolf_and_hunter.myViews.MapBaseFrame;
import com.jedi.wolf_and_hunter.myViews.RightRocker;
import com.jedi.wolf_and_hunter.myViews.SightView;
import com.jedi.wolf_and_hunter.myViews.Trajectory;
import com.jedi.wolf_and_hunter.myViews.ViewRange;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.myViews.characters.NormalHunter;
import com.jedi.wolf_and_hunter.myViews.characters.NormalWolf;
import com.jedi.wolf_and_hunter.myViews.landform.Landform;
import com.jedi.wolf_and_hunter.myViews.landform.TallGrassland;
import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameBaseAreaActivity extends Activity {
    public TextView t1;
    public TextView t2;
    public TextView t3;
    public TextView t4;
    public TextView t5;
    public TextView t6;
    public TextView gameResult;
    public BaseAI testingAI;


    private final static int CONTROL_MODE_NORMAL = 0;
    private final static int CONTROL_MODE_MASTER = 1;
    int controlMode = CONTROL_MODE_NORMAL;
    public static boolean isStop = false;
    LeftRocker leftRocker;
    RightRocker rightRocker;
    //    AttackButton leftAtttackButton;
    AttackButton rightAtttackButton;
    public static PlayerInfo myPlayerInfo;
    public static volatile ArrayList<BaseCharacterView> allCharacters;
    public static ArrayList<Trajectory> allTrajectories;
    public static MapBaseFrame mapBaseFrame;
    public static BaseCharacterView myCharacter;
    public static FrameLayout baseFrame;
    SightView mySight;
    public GameHandler gameHandler = new GameHandler();
    Timer timerForAllMoving = new Timer();
    Timer timerForTrajectory = new Timer();
    ArrayList<Timer> timerForAIList = new ArrayList<Timer>();
    ArrayList<PlayerInfo> playerInfos;
    Landform[][] landformses;
    Thread backGroundMusicThread;
    public static MyVirtualWindow virtualWindow;
    private MediaPlayer backGround;
    private int targetKillCount = 10;


    private class GameMainTask extends TimerTask {
        @Override
        public void run() {

            if (backGroundMusicThread == null) {
                backGroundMusicThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (isStop == false) {
                            if (backGround.isPlaying() == false) {
                                backGround.seekTo(0);
                                backGround.start();
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        backGround.stop();

                    }
                });
                backGroundMusicThread.setDaemon(true);
                backGroundMusicThread.start();
            }

            gameHandler.sendEmptyMessage(0);
        }
    }


    private class RemoveTrajectoryTask extends TimerTask {
        @Override
        public void run() {
            gameHandler.sendEmptyMessage(GameHandler.REMOVE_TRAJECTORY);
        }
    }


    public class GameHandler extends Handler {
        public static final int ADD_TRAJECTORY = 1;
        public static final int REMOVE_TRAJECTORY = 2;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_TRAJECTORY:
                    Trajectory trajectory = (Trajectory) (msg.obj);
                    trajectory.addTime = new Date().getTime();
                    allTrajectories.add(trajectory);
                    trajectory.addTrajectory(mapBaseFrame);
                    break;
                case REMOVE_TRAJECTORY:
                    long nowTime = new Date().getTime();
                    ArrayList<Trajectory> removeTrajectories = new ArrayList<Trajectory>();
                    for (Trajectory t : allTrajectories) {
                        if (nowTime - t.addTime > 1000) {
                            removeTrajectories.add(t);
                            t.parent.removeView(t);
                        }
                    }
                    for (Trajectory removeTarjectory : removeTrajectories) {
                        allTrajectories.remove(removeTarjectory);
                    }
                    break;
                default:
                    int team1KillCount = 0;
                    int team2KillCount = 0;
                    int team3KillCount = 0;
                    int team4KillCount = 0;
                    for (BaseCharacterView character : allCharacters) {
                        if (character.getTeamID() == 1)
                            team1KillCount += character.killCount;
                        else if (character.getTeamID() == 2)
                            team2KillCount += character.killCount;
                        else if (character.getTeamID() == 3)
                            team3KillCount += character.killCount;
                        else if (character.getTeamID() == 4)
                            team4KillCount += character.killCount;
                    }
                    if (team1KillCount >= targetKillCount) {
                        gameResult.setText("1队胜");
                        isStop = true;
                    }
                    if (team2KillCount >= targetKillCount) {
                        gameResult.setText("2队胜");
                        isStop = true;
                    }
                    if (team3KillCount >= targetKillCount) {
                        gameResult.setText("3队胜");
                        isStop = true;
                    }
                    if (team4KillCount >= targetKillCount) {
                        gameResult.setText("4队胜");
                        isStop = true;

                    }
                    if (isStop)
                        return;
                    reflashCharacterState();
            }


            for (int i = 0; i < allCharacters.size(); i++) {
                BaseCharacterView c = allCharacters.get(i);
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
                target.setText((i + 1) + "P:杀" + Integer.toString(c.killCount) + "  挂" + Integer.toString(c.dieCount));


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

//    private synchronized void reflashWindowPosition() {
//        FrameLayout.LayoutParams movingLayoutParams = (FrameLayout.LayoutParams) virtualWindow.movingLayout.getLayoutParams();
//        int relateX = virtualWindow.targetLeft - virtualWindow.left;
//        int relateY = virtualWindow.targetTop - virtualWindow.top;
//        if (Math.abs(relateX) > virtualWindow.windowMoveSpeed)
//            movingLayoutParams.leftMargin = -(virtualWindow.left + virtualWindow.windowMoveSpeed * Math.abs(relateX) / relateX);
//        else
//            movingLayoutParams.leftMargin = -(virtualWindow.left + relateX);
//
//        if (Math.abs(relateY) > virtualWindow.windowMoveSpeed)
//            movingLayoutParams.topMargin = -(virtualWindow.top + virtualWindow.windowMoveSpeed * Math.abs(relateY) / relateY);
//        else
//            movingLayoutParams.topMargin = -(virtualWindow.top + relateY);
//        virtualWindow.movingLayout.setLayoutParams(movingLayoutParams);
//
//    }


    private synchronized void reflashCharacterState() {

        if (myCharacter == null || leftRocker == null || rightRocker == null || mapBaseFrame == null)
            return;
        boolean isMyCharacterMoving = myCharacter.needMove;
        boolean needChange = false;
        synchronized (myCharacter) {

            myCharacter.hasUpdatedPosition = false;
            virtualWindow.hasUpdatedWindowPosition = false;
            //获得当前位置
            myCharacter.updateNowPosition();
            if (mySight != null) {
                mySight.hasUpdatedPosition = false;
                mySight.updateNowPosition();
            }
            //获得视窗虚拟位置
//                virtualWindow.updateNowWindowPosition(mapBaseFrame);
//                mySight.updateNowWindowPosition();
            if (myCharacter.isDead == true) {
                myCharacter.deadReset();
//                needChange = true;

            } else if (myCharacter.jumpToX > -99999 && myCharacter.jumpToY > -99999) {
                myCharacter.keepDirectionAndJump(0, 0, mapBaseFrame.getWidth(), mapBaseFrame.getHeight());
//                needChange = true;
            } else {
                if (controlMode == CONTROL_MODE_MASTER) {//CONTROL_MODE_MASTER这种操控方式已经过期，也许有用
                    if (myCharacter.needMove == true) {
                        myCharacter.masterModeOffsetLRTBParams();
//                        needChange = true;
                    }
                    if (mySight != null && mySight.needMove == true) {
                        mySight.masterModeOffsetLRTBParams(isMyCharacterMoving);
//                        needChange = true;
                    }
                } else if (controlMode == CONTROL_MODE_NORMAL) {
//                        Log.i("Player1 offX", Integer.toString(myCharacter.offX));
//                        Log.i("Player1 nowLeft", Integer.toString(myCharacter.nowLeft));
                    if (myCharacter.needMove == true) {
                        if(myPlayerInfo.characterType==BaseCharacterView.CHARACTER_TYPE_HUNTER)
                            myCharacter.normalModeOffsetLRTBParams();
                        else if(myPlayerInfo.characterType==BaseCharacterView.CHARACTER_TYPE_WOLF)
                            myCharacter.normalModeOffsetWolfLRTBParams();
//                        needChange = true;
                    }
//                        Log.i("Player1 offX", Integer.toString(myCharacter.offX));
//                        Log.i("Player1 nowLeft", Integer.toString(myCharacter.nowLeft));
                    if (mySight != null && mySight.needMove == true) {
                        mySight.normalModeOffsetLRTBParams();
//                        needChange = true;
                    }
                    mySight.virtualWindowPassiveFollow();


                }
            }
//            if (needChange) {
            FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) myCharacter.getLayoutParams();
            mLayoutParams.leftMargin = myCharacter.nowLeft;
            mLayoutParams.topMargin = myCharacter.nowTop;
            myCharacter.setLayoutParams(mLayoutParams);
            myCharacter.centerX = myCharacter.nowLeft + myCharacter.getWidth() / 2;
            myCharacter.centerY = myCharacter.nowTop + myCharacter.getHeight() / 2;
            if (controlMode == CONTROL_MODE_MASTER) {
                mySight.mLayoutParams.leftMargin = mySight.nowLeft;
                mySight.mLayoutParams.topMargin = mySight.nowTop;
                mySight.centerX = mySight.nowLeft + mySight.getWidth() / 2;
                mySight.centerY = mySight.nowTop + mySight.getHeight() / 2;
            } else if (controlMode == CONTROL_MODE_NORMAL) {
                mySight.mLayoutParams.leftMargin = myCharacter.centerX - mySight.getWidth() / 2;
                mySight.mLayoutParams.topMargin = myCharacter.centerY - mySight.getHeight() / 2;
                mySight.centerX = myCharacter.centerX;
                mySight.centerY = myCharacter.centerY;
            }

            mySight.setLayoutParams(mySight.mLayoutParams);
//                    virtualWindow.offsetWindow();


            myCharacter.changeThisCharacterOnLandformses();
            //master模式下nowFacingAngle由sight和Character共同决定；需要在这里调用changeRotate()；
            //而normal模式下nowFacingAngle在sight的normalModeOffsetLRTBParams()下已经计算获得。
            if (controlMode == CONTROL_MODE_MASTER) {
                myCharacter.changeRotate();
            }
            myCharacter.attackRange.centerX = myCharacter.centerX;
            myCharacter.attackRange.centerY = myCharacter.centerY;
            myCharacter.attackRange.layoutParams.leftMargin = myCharacter.attackRange.centerX - myCharacter.attackRange.nowAttackRadius;
            myCharacter.attackRange.layoutParams.topMargin = myCharacter.attackRange.centerY - myCharacter.attackRange.nowAttackRadius;
            myCharacter.attackRange.setLayoutParams(myCharacter.attackRange.layoutParams);

            myCharacter.viewRange.centerX = myCharacter.centerX;
            myCharacter.viewRange.centerY = myCharacter.centerY;
            myCharacter.viewRange.layoutParams.leftMargin = myCharacter.viewRange.centerX - myCharacter.nowViewRadius;
            myCharacter.viewRange.layoutParams.topMargin = myCharacter.viewRange.centerY - myCharacter.nowViewRadius;
            myCharacter.viewRange.setLayoutParams(myCharacter.viewRange.layoutParams);

            myCharacter.viewRange.invalidate();


            myCharacter.startMovingMediaThread();

//            }
        }


        for (BaseCharacterView c : allCharacters) {
            if (c == myCharacter)
                continue;
            synchronized (c) {
                c.updateNowPosition();
                if (c.isDead == true) {
                    c.deadReset();
                    c.invalidate();
                    continue;
                }
                if (c.jumpToX > -99999 && c.jumpToY > -99999) {
                    c.keepDirectionAndJump(0, 0, mapBaseFrame.getWidth(), mapBaseFrame.getHeight());
                } else {
                    if(c.characterType==BaseCharacterView.CHARACTER_TYPE_HUNTER)
                        c.reactOtherPlayerMove();
                    else if(c.characterType==BaseCharacterView.CHARACTER_TYPE_WOLF)
                        c.reactOtherPlayerWolfMove();
                }
//                        if(c.getTeamID()==2){
//                            Log.i("Player2 nowLeft",Integer.toString(c.nowLeft));
//                        }
                FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) c.getLayoutParams();
                mLayoutParams.leftMargin = c.nowLeft;
                mLayoutParams.topMargin = c.nowTop;
                c.centerX = c.nowLeft + c.getWidth() / 2;
                c.centerY = c.nowTop + c.getHeight() / 2;
                c.changeThisCharacterOnLandformses();
                myCharacter.changeOtherCharacterState(c);
                c.setLayoutParams(mLayoutParams);


                c.attackRange.centerX = c.centerX;
                c.attackRange.centerY = c.centerY;
                c.attackRange.layoutParams.leftMargin = c.attackRange.centerX - c.attackRange.nowAttackRadius;
                c.attackRange.layoutParams.topMargin = c.attackRange.centerY - c.attackRange.nowAttackRadius;
                c.attackRange.setLayoutParams(c.attackRange.layoutParams);

                c.viewRange.centerX = c.centerX;
                c.viewRange.centerY = c.centerY;
                c.viewRange.layoutParams.leftMargin = c.viewRange.centerX - c.nowViewRadius;
                c.viewRange.layoutParams.topMargin = c.viewRange.centerY - c.nowViewRadius;
                c.viewRange.setLayoutParams(c.viewRange.layoutParams);
                c.viewRange.invalidate();
                c.hasUpdatedPosition = false;
                int relateX = myCharacter.centerX - c.centerX;
                int relateY = myCharacter.centerY - c.centerY;
                double distance = Math.sqrt(relateX * relateX + relateY * relateY);
                if (distance < myCharacter.nowHearRadius) {
                    c.startMovingMediaThread();
                }

            }
        }

        virtualWindow.reflashWindowPosition();
        mapBaseFrame.invalidate();


    }

    private void startAI() {
        for (int i = 1; i < playerInfos.size(); i++) {
            PlayerInfo playerInfo = playerInfos.get(i);
            BaseAI ai = null;
            if (playerInfo.isAvailable == false)
                continue;
            BaseCharacterView aiCharacter = null;
            if (playerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER) {
                aiCharacter = new NormalHunter(this, virtualWindow);
                ai = new BaseAI(aiCharacter);
            } else if(playerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF){
                aiCharacter = new NormalWolf(this, virtualWindow);
                ai = new WolfAI(aiCharacter);
            }
//            FrameLayout.LayoutParams c1LP = (FrameLayout.LayoutParams) aiCharacter.getLayoutParams();
//            c1LP.leftMargin = mapBaseFrame.getWidth() - 200;
//            c1LP.topMargin = mapBaseFrame.getHeight() - 200;
//            aiCharacter.nowFacingAngle = new Random().nextInt(359);
//            aiCharacter.setLayoutParams(c1LP);
            aiCharacter.gameHandler = gameHandler;
            aiCharacter.setTeamID(playerInfo.teamID);
//            aiCharacter.setTeamID(i%2+1);
//            ViewRange viewRange = new ViewRange(this, aiCharacter);
//            AttackRange attackRange = new AttackRange(this, aiCharacter);
//            mapBaseFrame.addView(viewRange);
//            mapBaseFrame.addView(attackRange);
//            mapBaseFrame.addView(aiCharacter);



            ai = new BaseAI(aiCharacter);
            allCharacters.add(aiCharacter);
            Timer timerForAI = new Timer("AIPlayer1", true);
            timerForAI.scheduleAtFixedRate(ai, 1000, 30);
            timerForAIList.add(timerForAI);
            if (i == 2) {
                testingAI = ai;
            }
        }


    }


    private void addElementToMap() throws Exception {


        int widthCount = mapBaseFrame.mapWidth / 100;
        int heightCount = mapBaseFrame.mapHeight / 100;
        landformses = new Landform[heightCount][widthCount];
        Random r = new Random();
        for (int i = 0; i < landformses.length; i++) {
            for (int j = 0; j < landformses[i].length; j++) {
                if (r.nextInt(10) > 5)
                    landformses[i][j] = new TallGrassland(this);
            }
        }
//        for (int i = 0; i < landformses.length; i++) {
//            if (Math.abs(i) % 3 == 0) {
//                for (int j = 0; j < landformses[i].length; j++) {
//                    if (Math.abs(i - j) % 3 == 0)
//                        landformses[i][j] = new TallGrassland(this);
//                }
//            }
//        }

        allTrajectories = new ArrayList<Trajectory>();

        //添加地形
        GameMap map = new GameMap(this);
        mapBaseFrame.addView(map);
        map.landformses = landformses;
        map.addLandforms();

        allCharacters = new ArrayList<BaseCharacterView>();
        //添加我的角色
        myPlayerInfo = playerInfos.get(0);
        if (myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER)
            myCharacter = new NormalHunter(this, virtualWindow);
        else
            myCharacter = new NormalWolf(this, virtualWindow);
        myCharacter.setTeamID(myPlayerInfo.teamID);

        allCharacters.add(myCharacter);
        myCharacter.isMyCharacter = true;
        myCharacter.gameHandler = gameHandler;
//        mapBaseFrame.addView(myCharacter);
        mapBaseFrame.myCharacter = myCharacter;


//        NormalHunter testCharacter = new NormalHunter(this, virtualWindow);
//        testCharacter.setTeamID(2);
//        allCharacters.add(testCharacter);

        //添加视点
        mySight = new SightView(this);
        mySight.virtualWindow = this.virtualWindow;
        mySight.sightSize = myCharacter.characterBodySize;
        if (controlMode == CONTROL_MODE_NORMAL)
            mySight.isHidden = true;

        myCharacter.setSight(mySight);
        if (mySight.isHidden == false) {
            mapBaseFrame.addView(mySight);
            mapBaseFrame.mySight = mySight;
        }

        rightAtttackButton = (AttackButton) this.findViewById(R.id.attack_button_right);
        int buttonSize = rightAtttackButton.buttonSize;
        rightAtttackButton.bindingCharacter = myCharacter;
        FrameLayout.LayoutParams rabp = (FrameLayout.LayoutParams) rightAtttackButton.getLayoutParams();
        if (rabp == null) {
            rabp = new FrameLayout.LayoutParams(buttonSize, buttonSize);
        }
        //添加摇杆
        leftRocker = (LeftRocker) this.findViewById(R.id.rocker_left);
        leftRocker.setBindingCharacter(myCharacter);
        mapBaseFrame.leftRocker = leftRocker;
        rightRocker = (RightRocker) this.findViewById(R.id.rocker_right);
        if (myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER) {
            rightAtttackButton.reCreateBitmap();
            rightRocker.setBindingCharacter(myCharacter);
            mapBaseFrame.rightRocker = rightRocker;

//        leftAtttackButton = (AttackButton) this.findViewById(R.id.attack_button_left);
//        leftAtttackButton.bindingCharacter = myCharacter;
//        FrameLayout.LayoutParams labp = (FrameLayout.LayoutParams) leftAtttackButton.getLayoutParams();
//        if (labp == null) {
//            labp = new FrameLayout.LayoutParams(buttonSize, buttonSize);
//        }
//        labp.leftMargin = leftRocker.getRight() - buttonSize;
//        labp.topMargin = leftRocker.getTop();
//        leftAtttackButton.setLayoutParams(labp);


            rabp.leftMargin = rightRocker.getRight()-rightAtttackButton.buttonSize-10;
            rabp.topMargin = rightRocker.getTop()-rightAtttackButton.buttonSize-30;
        } else if (myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF) {
            rightAtttackButton.reCreateBitmap();
            baseFrame.removeView(rightRocker);
            rabp.leftMargin = MyVirtualWindow.getWindowWidth(this)-rightAtttackButton.buttonSize-50;
            rabp.topMargin = MyVirtualWindow.getWindowHeight(this)-(leftRocker.getHeight()/2+rightAtttackButton.buttonSize/2);
        }
        rightAtttackButton.setLayoutParams(rabp);
        leftRocker.bringToFront();
        rightRocker.bringToFront();
        rightAtttackButton.bringToFront();


        startAI();

        for (BaseCharacterView character : allCharacters) {
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
            if (character.isMyCharacter) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mapBaseFrame.getLayoutParams();
                params.leftMargin = -(character.centerX - MyVirtualWindow.getWindowWidth(this) / 2);
                params.topMargin = -(character.centerY - MyVirtualWindow.getWindowHeight(this) / 2);
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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerInfos = (ArrayList<PlayerInfo>) getIntent().getExtras().get("playerInfos");
        isStop = false;
        backGround = MediaPlayer.create(this, R.raw.background);
        ViewUtils.initWindowParams(this);
        DisplayMetrics dm = ViewUtils.getWindowsDisplayMetrics();
        setContentView(R.layout.activity_game_base_area);
        ViewUtils.initWindowParams(this);
        baseFrame = (FrameLayout) findViewById(R.id.baseFrame);
        mapBaseFrame = new MapBaseFrame(this, (int) (dm.widthPixels * 1.5), (int) (dm.heightPixels * 1.5));


//        FrameLayout.LayoutParams mbfLP=(FrameLayout.LayoutParams) mapBaseFrame.getLayoutParams();
//        if(mbfLP==null)
//            mbfLP=new FrameLayout.LayoutParams((int)(dm.widthPixels*1.5),(int)(dm.widthPixels*1.5));
//        else {
//            mbfLP.width = (int) (dm.widthPixels * 1.5);
//            mbfLP.height = (int) (dm.widthPixels * 1.5);
//        }
//        mapBaseFrame.setLayoutParams(mbfLP);
        baseFrame.addView(mapBaseFrame);


        t1 = new TextView(this);
        t1.setTextColor(Color.WHITE);
        t1.setTextSize(15);
        t2 = new TextView(this);
        t2.setTextColor(Color.WHITE);
        t2.setTextSize(15);
        t3 = new TextView(this);
        t3.setTextColor(Color.WHITE);
        t3.setTextSize(15);
        t4 = new TextView(this);
        t4.setTextColor(Color.WHITE);
        t4.setTextSize(15);
        t5 = new TextView(this);
        t5.setTextColor(Color.WHITE);
        t5.setTextSize(15);
        t6 = new TextView(this);
        t6.setTextColor(Color.WHITE);
        t6.setTextSize(15);
        gameResult = new TextView(this);
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
        p7.leftMargin = MyVirtualWindow.getWindowWidth(this) / 2 - 200;
        p7.topMargin = MyVirtualWindow.getWindowHeight(this) / 2;
        gameResult.setLayoutParams(p7);

        baseFrame.addView(t1);
        baseFrame.addView(t2);
        baseFrame.addView(t3);
        baseFrame.addView(t4);
        baseFrame.addView(t5);
        baseFrame.addView(t6);
        baseFrame.addView(gameResult);


        virtualWindow = new MyVirtualWindow(this, mapBaseFrame);
        mapBaseFrame.post(new Runnable() {
            @Override
            public void run() {
                try {
                    addElementToMap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        allCharacters = new ArrayList<BaseCharacterView>();
//        FrameLayout.LayoutParams paramsForMapBase = (FrameLayout.LayoutParams) mapBaseFrame.getLayoutParams();
//        paramsForMapBase.width = 2000;
//        paramsForMapBase.height = 1500;
//        mapBaseFrame.setLayoutParams(paramsForMapBase);


        //scheduleAtFixedRate后一次Task不以前一个Task执行完毕的时间为起点延时执行
        timerForAllMoving.scheduleAtFixedRate(new GameMainTask(), 1000, 30);
        timerForTrajectory.scheduleAtFixedRate(new RemoveTrajectoryTask(), 1000, 300);
//        timerForWindowMoving.scheduleAtFixedRate(virtualWindow, 0, 20);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Called when you are no longer visible to the user.  You will next
     * receive either {@link #onRestart}, {@link #onDestroy}, or nothing,
     * depending on later user activity.
     * <p>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestart
     * @see #onResume
     * @see #onSaveInstanceState
     * @see #onDestroy
     */
//   tim
    @Override
    protected void onDestroy() {

        isStop = true;
        timerForTrajectory.cancel();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if (timerForAllMoving != null)
            timerForAllMoving.cancel();


        for (Timer timer : timerForAIList) {
            timer.cancel();
        }

        super.onDestroy();

    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        isStop = true;
        timerForAllMoving.cancel();

        timerForTrajectory.cancel();
        for (Timer timer : timerForAIList) {
            timer.cancel();
        }
        super.onBackPressed();

    }
}
