package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.engine.GameMainEngine;
import com.jedi.wolf_and_hunter.myObj.gameObj.GameInfo;
import com.jedi.wolf_and_hunter.myObj.gameObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.mapBase.GameMap;
import com.jedi.wolf_and_hunter.myViews.mapBase.MapBaseFrame;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

public class GameBaseAreaActivity extends Activity {
    //    public TextView t1;
//    public TextView t2;
//    public TextView t3;
//    public TextView t4;
//    public TextView t5;
//    public TextView t6;
//    public TextView gameResult;
    public static MapBaseFrame mapBaseFrame;
    public static BaseCharacterView myCharacter;
    public static FrameLayout baseFrame;
    public static GameInfo gameInfo;
    public static GameMap gameMap;
    public static MyVirtualWindow virtualWindow;
    public static GameMainEngine engine;
    BluetoothAdapter bluetoothAdapter;
    BluetoothServerSocket bluetoothServerSocket;
    BluetoothSocket bluetoothSocket;
//    BluetoothAcceptThread bluetoothAcceptThread;
//    BluetoothConnectThread bluetoothConnectThread;
//    BluetoothAcceptThread.DealBluetoothServerDataThread dealBluetoothServerDataThread;

//    LeftRocker leftRocker;
//    RightRocker rightRocker;
//    //    AttackButton leftAtttackButton;
//    AttackButton rightAtttackButton;
//    LockingButton lockingButton;
//    SmellButton smellButton;

//    public GameHandler gameHandler = new GameHandler();
//    Timer timerForAllMoving = new Timer();
//    Timer timerForTrajectory = new Timer();
//    ArrayList<Timer> timerForAIList = new ArrayList<Timer>();

//    private MediaPlayer backGround;


//    private class GameMainTask extends TimerTask {
//        @Override
//        public void run() {
//            if (backGround.isPlaying() == false) {
//                backGround.setLooping(true);
//                backGround.seekTo(0);
//                backGround.start();
//            }
////            if (backGroundMusicThread == null) {
////                backGroundMusicThread = new Thread(new Runnable() {
////                    @Override
////                    public void run() {
////
////                        while (gameInfo.isStop == false) {
////                            if (backGround.isPlaying() == false) {
////                                backGround.seekTo(0);
////                                backGround.start();
////                            }
////                            try {
////                                Thread.sleep(1000);
////                            } catch (InterruptedException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                        backGround.stop();
////
////                    }
////                });
////                backGroundMusicThread.setDaemon(true);
////                backGroundMusicThread.start();
////            }
//
//            gameHandler.sendEmptyMessage(0);
//        }
//    }


//    private class RemoveTrajectoryTask extends TimerTask {
//        @Override
//        public void run() {
//            gameHandler.sendEmptyMessage(GameHandler.REMOVE_TRAJECTORY);
//        }
//    }


//    public class GameHandler extends Handler {
//        public static final int ADD_TRAJECTORY = 1;
//        public static final int REMOVE_TRAJECTORY = 2;
//        public static final int UPDATE_OTHER_ONLINE_PLAYER = 3;
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case ADD_TRAJECTORY:
//                    Trajectory trajectory = (Trajectory) (msg.obj);
//                    trajectory.addTime = new Date().getTime();
//                    gameInfo.allTrajectories.add(trajectory);
//                    trajectory.addTrajectory(mapBaseFrame);
//                    break;
//                case REMOVE_TRAJECTORY:
//                    long nowTime = new Date().getTime();
//                    ArrayList<Trajectory> removeTrajectories = new ArrayList<Trajectory>();
//                    for (Trajectory t : gameInfo.allTrajectories) {
//                        if (nowTime - t.addTime > 1000) {
//                            removeTrajectories.add(t);
//                            t.parent.removeView(t);
//                        }
//                    }
//                    for (Trajectory removeTarjectory : removeTrajectories) {
//                        gameInfo.allTrajectories.remove(removeTarjectory);
//                    }
//                    break;
//                default:
//                    int team1KillCount = 0;
//                    int team2KillCount = 0;
//                    int team3KillCount = 0;
//                    int team4KillCount = 0;
//                    for (BaseCharacterView character : gameInfo.allCharacters) {
//                        if (character.getTeamID() == 1)
//                            team1KillCount += character.killCount;
//                        else if (character.getTeamID() == 2)
//                            team2KillCount += character.killCount;
//                        else if (character.getTeamID() == 3)
//                            team3KillCount += character.killCount;
//                        else if (character.getTeamID() == 4)
//                            team4KillCount += character.killCount;
//                    }
//                    if (team1KillCount >= gameInfo.targetKillCount) {
//                        gameResult.setText("1队胜");
//                        gameInfo.isStop = true;
//                    }
//                    if (team2KillCount >= gameInfo.targetKillCount) {
//                        gameResult.setText("2队胜");
//                        gameInfo.isStop = true;
//                    }
//                    if (team3KillCount >= gameInfo.targetKillCount) {
//                        gameResult.setText("3队胜");
//                        gameInfo.isStop = true;
//                    }
//                    if (team4KillCount >= gameInfo.targetKillCount) {
//                        gameResult.setText("4队胜");
//                        gameInfo.isStop = true;
//
//                    }
//                    if (gameInfo.isStop)
//                        return;
//                    reflashCharacterState();
//            }
//
//
//            for (int i = 0; i < gameInfo.allCharacters.size(); i++) {
//                BaseCharacterView c = gameInfo.allCharacters.get(i);
//                TextView target = null;
//                if (i == 0)
//                    target = t1;
//                else if (i == 1)
//                    target = t2;
//                else if (i == 2)
//                    target = t3;
//                else if (i == 3)
//                    target = t4;
//                if (target == null)
//                    continue;
//                target.setText((i + 1) + "P:杀" + Integer.toString(c.killCount) + "  挂" + Integer.toString(c.dieCount));
//
//
//            }
////            t5.setText("intent:"+testingAI.intent);
////            t6.setText("nowLeft:"+testingAI.bindingCharacter.nowLeft);
////            t7.setText("nowTop:"+testingAI.bindingCharacter.nowTop);
//
////            t1.invalidate();
////            t2.invalidate();
////            t3.invalidate();
////            t4.invalidate();
////            t5.invalidate();
//
//        }
//
//    }

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
//    public class BluetoothAcceptThread extends Thread {
//
//
//        public BluetoothAcceptThread() {
//            try {
//                BluetoothServerSocket tmp = null;
//                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothServer", UUID.fromString(BluetoothController.mUUID));
//                bluetoothServerSocket = tmp;
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("AcceptThread", e.getMessage());
//            }
//        }
//
//        @Override
//        public void run() {
//            super.run();
//
//            BluetoothController.cancelDiscovery();
//            //不断监听直到返回连接或者发生异常
//            try {
//
//                while (true) {
//                    //据说处于查找状态会影响性能哦
//
//                    //启连接请求，这是一个阻塞方法，必须放在子线程
////                    if (bluetoothSocket != null) {
////                        bluetoothServerSocket.close();//根据demo描述，close应该在accept成功后执行，不会影响已链接bluetoothSocket
////                        bluetoothServerSocket = null;
////                    }
//
//                    bluetoothSocket = bluetoothServerSocket.accept();
//
//                    if (bluetoothSocket != null) {
//                        if (dealBluetoothServerDataThread == null || dealBluetoothServerDataThread.getState() == State.TERMINATED) {
//                            dealBluetoothServerDataThread = new DealBluetoothServerDataThread(bluetoothSocket);
//                            dealBluetoothServerDataThread.start();
//                            bluetoothServerSocket.close();
//                        } else {
//                            bluetoothSocket.close();
//                        }
//                    }
//
//
////                    manageConnectedSocket(bluetoothSocket);
//                }
//            } catch (Exception e) {
//                Log.e("BluetoothAcceptThread", "哎呀，当个服务器不容易啊，不知干嘛又挂了。。。。。。。。。。。");
//                finish();
//            } finally {
//
//            }
//        }
//
//        /**
//         * 取消正在监听的接口
//         */
//
//
//        class DealBluetoothServerDataThread extends Thread {
//            BluetoothSocket bluetoothSocket;
//
//            public DealBluetoothServerDataThread(BluetoothSocket bluetoothSocket) {
//                this.bluetoothSocket = bluetoothSocket;
//            }
//
//            @Override
//            public void run() {
//
//
//                InputStream is = null;
//                OutputStream os = null;
//                PlayerInfo remotePlayerInfo;
//                try {
//                    while (true) {
//                        os = bluetoothSocket.getOutputStream();
//
//                        is = bluetoothSocket.getInputStream();
//                        ObjectInputStream ois = new ObjectInputStream(is);
//                        remotePlayerInfo = (PlayerInfo) ois.readObject();
//                        synchronized (gameInfo.playerInfos) {
//                            gameInfo.playerInfos.set(1, remotePlayerInfo);
//                            Log.e("DealServerDataThread", "personInfoTeamID:" + remotePlayerInfo.teamID);
//                            Message message = gameHandler.obtainMessage();
//                            message.what = GameHandler.UPDATE_OTHER_ONLINE_PLAYER;
//                            message.obj = remotePlayerInfo;
//                            gameHandler.sendMessage(message);
//                            os = bluetoothSocket.getOutputStream();
//                            ObjectOutputStream oos = new ObjectOutputStream(os);
//                            oos.writeObject(gameInfo.playerInfos.get(0));
//                        }
//                    }
//                } catch (Exception e) {
//                    initBluetoothConnection();
//                    finish();
////                    Log.e("DealServerDataThread", e.getMessage());
//                } finally {
////                bluetoothAcceptThread=new BluetoothAcceptThread();
////                bluetoothAcceptThread.setDaemon(true);
////                bluetoothAcceptThread.start();
//                }
//            }
//        }
//
//
//    }
//
//
//    class BluetoothConnectThread extends Thread {
//        private BluetoothDevice serverDevice;
//
//        public BluetoothConnectThread(BluetoothDevice serverDevice) {
//            this.serverDevice = serverDevice;
//
//            BluetoothSocket tmp = null;
//            try {
//                tmp = serverDevice.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothController.mUUID)); //应该是这里导致Service discovery failed问题
//
//            } catch (IOException e) {
//                Log.d("BLUETOOTH_CLIENT", e.getMessage());
//            }
//            bluetoothSocket = tmp;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            //取消搜索因为搜索会让连接变慢
//            BluetoothController.cancelDiscovery();
//            try {
////                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(mUUID));
//                //通过BluetoothSocket连接设备，这是一个阻塞操作，知道连接成功或发生异常
//                if (bluetoothSocket != null) {
//                    bluetoothSocket.connect();
//                    PlayerInfo remotePlayerInfo;
//                    while (true) {
//                        OutputStream os = null;
//                        InputStream is = null;
//                        os = bluetoothSocket.getOutputStream();
//                        ObjectOutputStream oos = new ObjectOutputStream(os);
//                        synchronized (gameInfo.playerInfos) {
//                            oos.writeObject(gameInfo.playerInfos.get(0));
//                            is = bluetoothSocket.getInputStream();
//
//                            ObjectInputStream ois = new ObjectInputStream(is);
//                            remotePlayerInfo = (PlayerInfo) ois.readObject();
//                            gameInfo.playerInfos.set(1, remotePlayerInfo);
//                            Message message = gameHandler.obtainMessage();
//                            message.what = BluetoothOnlineGameBaseAreaActivity.GameHandler.UPDATE_OTHER_PLAYER;
//                            message.obj = remotePlayerInfo;
//                            gameHandler.sendMessage(message);
//                            Log.e("ConnectThread", "teamId:" + remotePlayerInfo.teamID);
//                        }
//                    }
//                }
//
//            } catch (Exception e) {
//                Log.e("BluetoothConnectThread", "他妈的，当个客户端不容易啊，服务器又不理我了。。。。。。。。。。。");
//                initBluetoothConnection();
//                finish();
//                //无法连接，关闭BluetoothSocket并且退出
//
//            } finally {
////                bluetoothConnectThread=new BluetoothConnectThread(serverDevice);
////                bluetoothConnectThread.setDaemon(true);
////                bluetoothConnectThread.start();
//            }
//
//
//            //管理连接(在独立的线程)
//            // manageConnectedSocket(mmSocket);
//        }
//
//
//    }
//
//    /**
//     * 这方法重要性说你都不信，不重置这些TM的一大堆问题
//     */
//    public void initBluetoothConnection() {
//        try {
//
//
//            if (bluetoothAcceptThread != null && bluetoothAcceptThread.getState() != Thread.State.TERMINATED) {
//                bluetoothAcceptThread.interrupt();
//                bluetoothAcceptThread = null;
//            }
//            if (bluetoothConnectThread != null && bluetoothConnectThread.getState() != Thread.State.TERMINATED) {
//                bluetoothConnectThread.interrupt();
//                bluetoothConnectThread = null;
//            }
//            if (dealBluetoothServerDataThread != null && dealBluetoothServerDataThread.getState() != Thread.State.TERMINATED) {
//                dealBluetoothServerDataThread.interrupt();
//                dealBluetoothServerDataThread = null;
//            }
//            if (bluetoothServerSocket != null) {
//                bluetoothServerSocket.close();
//                bluetoothServerSocket = null;
//            }
//            if (bluetoothSocket != null) {
//                bluetoothSocket.close();
//                bluetoothSocket = null;
//            }
//
//        } catch (IOException e) {
//            Log.e("cancel", e.getMessage());
//        }
//    }


//    private synchronized void reflashCharacterState() {
//
//        if (myCharacter == null || leftRocker == null || rightRocker == null || mapBaseFrame == null)
//            return;
//        boolean isMyCharacterMoving = myCharacter.needMove;
//        boolean needChange = false;
//        if (gameInfo.beAttackedList.size() > 0)
//            gameInfo.dealNeedToBeKilled();
//        synchronized (myCharacter) {
//myCharacter.updateInvincibleState();
//            myCharacter.hasUpdatedPosition = false;
//            virtualWindow.hasUpdatedWindowPosition = false;
//            //获得当前位置
//            myCharacter.updateNowPosition();
//            if (gameInfo.mySight != null) {
//                gameInfo.mySight.hasUpdatedPosition = false;
//                gameInfo.mySight.updateNowPosition();
//            }
//            if (myCharacter.isDead == true) {
//                myCharacter.deadReset();
//
//            } else if (myCharacter.isKnockedAway) {
//                myCharacter.beKnockedAway(0, 0, mapBaseFrame.getWidth(), mapBaseFrame.getHeight());
//            } else if (myCharacter.isJumping) {
//                myCharacter.keepDirectionAndJump(0, 0, mapBaseFrame.getWidth(), mapBaseFrame.getHeight());
//            } else {
//                if (gameInfo.controlMode == GameInfo.CONTROL_MODE_MASTER) {//GameInfo.CONTROL_MODE_MASTER这种操控方式已经过期，也许有用,留着玩儿
//                    if (myCharacter.needMove == true) {
//                        myCharacter.masterModeOffsetLRTBParams();
//                    }
//                    if (gameInfo.mySight != null && gameInfo.mySight.needMove == true) {
//                        gameInfo.mySight.masterModeOffsetLRTBParams(isMyCharacterMoving);
//                    }
//                } else if (gameInfo.controlMode == GameInfo.CONTROL_MODE_NORMAL) {
//                    if (myCharacter.needMove == true) {
//                        if (myCharacter.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER)
//                            myCharacter.normalModeOffsetLRTBParams();
//                        else if (myCharacter.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF)
//                            myCharacter.normalModeOffsetWolfLRTBParams();
//                    }
//                    if (gameInfo.mySight != null && gameInfo.mySight.needMove == true) {
//                        gameInfo.mySight.normalModeOffsetLRTBParams();
//                    } else if (myCharacter.isLocking) {
//                        myCharacter.dealLocking();
//                    }
//
//
//                }
//            }
//            FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) myCharacter.getLayoutParams();
//            mLayoutParams.leftMargin = myCharacter.nowLeft;
//            mLayoutParams.topMargin = myCharacter.nowTop;
//            myCharacter.setLayoutParams(mLayoutParams);
//            myCharacter.centerX = myCharacter.nowLeft + myCharacter.getWidth() / 2;
//            myCharacter.centerY = myCharacter.nowTop + myCharacter.getHeight() / 2;
//            if (gameInfo.controlMode == GameInfo.CONTROL_MODE_MASTER) {
//                gameInfo.mySight.mLayoutParams.leftMargin = gameInfo.mySight.nowLeft;
//                gameInfo.mySight.mLayoutParams.topMargin = gameInfo.mySight.nowTop;
//                gameInfo.mySight.centerX = gameInfo.mySight.nowLeft + gameInfo.mySight.getWidth() / 2;
//                gameInfo.mySight.centerY = gameInfo.mySight.nowTop + gameInfo.mySight.getHeight() / 2;
//            } else if (gameInfo.controlMode == GameInfo.CONTROL_MODE_NORMAL) {
//                gameInfo.mySight.mLayoutParams.leftMargin = myCharacter.centerX - gameInfo.mySight.getWidth() / 2;
//                gameInfo.mySight.mLayoutParams.topMargin = myCharacter.centerY - gameInfo.mySight.getHeight() / 2;
//                gameInfo.mySight.centerX = myCharacter.centerX;
//                gameInfo.mySight.centerY = myCharacter.centerY;
//            }
//
//            gameInfo.mySight.setLayoutParams(gameInfo.mySight.mLayoutParams);
//
//
//            myCharacter.changeThisCharacterOnLandformses();
//            //master模式下nowFacingAngle由sight和Character共同决定；需要在这里调用changeRotate()；
//            //而normal模式下nowFacingAngle在sight的normalModeOffsetLRTBParams()下已经计算获得。
//            if (gameInfo.controlMode == GameInfo.CONTROL_MODE_MASTER) {
//                myCharacter.changeRotate();
//            }
//            myCharacter.attackRange.centerX = myCharacter.centerX;
//            myCharacter.attackRange.centerY = myCharacter.centerY;
//            myCharacter.attackRange.layoutParams.leftMargin = myCharacter.attackRange.centerX - myCharacter.nowAttackRadius;
//            myCharacter.attackRange.layoutParams.topMargin = myCharacter.attackRange.centerY - myCharacter.nowAttackRadius;
//
//            myCharacter.attackRange.setLayoutParams(myCharacter.attackRange.layoutParams);
//
//            myCharacter.viewRange.centerX = myCharacter.centerX;
//            myCharacter.viewRange.centerY = myCharacter.centerY;
//
//            FrameLayout.LayoutParams viewRangeLP = (FrameLayout.LayoutParams) myCharacter.viewRange.getLayoutParams();
//            viewRangeLP.leftMargin = myCharacter.viewRange.centerX - myCharacter.nowViewRadius;
//            viewRangeLP.topMargin = myCharacter.viewRange.centerY - myCharacter.nowViewRadius;
//            myCharacter.viewRange.setLayoutParams(viewRangeLP);
//
//            if (myCharacter.promptView != null) {
//                myCharacter.promptView.centerX = myCharacter.centerX;
//                myCharacter.promptView.centerY = myCharacter.centerY;
//                myCharacter.promptView.layoutParams.leftMargin = myCharacter.promptView.centerX - myCharacter.promptView.viewSize / 2;
//                myCharacter.promptView.layoutParams.topMargin = myCharacter.promptView.centerY - myCharacter.promptView.viewSize / 2;
//
//                myCharacter.promptView.setLayoutParams(myCharacter.promptView.layoutParams);
//            }
//
////            myCharacter.viewRange.invalidate();
//
//            gameInfo.mySight.virtualWindowPassiveFollow();
//            myCharacter.startMovingMediaThread();
//
////            }
//        }
//
//
//        for (BaseCharacterView c : gameInfo.allCharacters) {
//
//            if (c == myCharacter)
//                continue;
//            synchronized (c) {
//                c.updateInvincibleState();
//                c.updateNowPosition();
//                if (c.isDead == true) {
//                    c.deadReset();
//                    c.invalidate();
//                    continue;
//                }
//                 if (c.isKnockedAway) {
//                    c.beKnockedAway(0, 0, mapBaseFrame.getWidth(), mapBaseFrame.getHeight());
//                }
//                else if (c.jumpToX > -99999 && c.jumpToY > -99999) {
//                    c.keepDirectionAndJump(0, 0, mapBaseFrame.getWidth(), mapBaseFrame.getHeight());
//                } else {
//                    if (c.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER)
//                        c.reactOtherPlayerHunterMove();
//                    else if (c.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF)
//                        c.reactOtherPlayerWolfMove();
//                }
////                        if(c.getTeamID()==2){
////                            Log.i("Player2 nowLeft",Integer.toString(c.nowLeft));
////                        }
//                FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) c.getLayoutParams();
//                mLayoutParams.leftMargin = c.nowLeft;
//                mLayoutParams.topMargin = c.nowTop;
//                c.centerX = c.nowLeft + c.getWidth() / 2;
//                c.centerY = c.nowTop + c.getHeight() / 2;
//                c.changeThisCharacterOnLandformses();
//                myCharacter.changeOtherCharacterState(c);
//                c.setLayoutParams(mLayoutParams);
//
//
//                c.attackRange.centerX = c.centerX;
//                c.attackRange.centerY = c.centerY;
//                c.attackRange.layoutParams.leftMargin = c.attackRange.centerX - c.nowAttackRadius;
//                c.attackRange.layoutParams.topMargin = c.attackRange.centerY - c.nowAttackRadius;
//                c.attackRange.setLayoutParams(c.attackRange.layoutParams);
//
//                c.viewRange.centerX = c.centerX;
//                c.viewRange.centerY = c.centerY;
//                FrameLayout.LayoutParams viewRangeLP = (FrameLayout.LayoutParams) c.viewRange.getLayoutParams();
//                viewRangeLP.leftMargin = c.viewRange.centerX - c.nowViewRadius;
//                viewRangeLP.topMargin = c.viewRange.centerY - c.nowViewRadius;
//                c.viewRange.setLayoutParams(viewRangeLP);
//                c.viewRange.invalidate();
//                c.hasUpdatedPosition = false;
//                int relateX = myCharacter.centerX - c.centerX;
//                int relateY = myCharacter.centerY - c.centerY;
//                double distance = Math.sqrt(relateX * relateX + relateY * relateY);
//                if (distance < myCharacter.nowHearRadius) {
//                    c.startMovingMediaThread();
//                }
//
//            }
//        }
//
//        virtualWindow.reflashWindowPosition();
//        mapBaseFrame.invalidate();
//
//
//    }

//    private void startAI() {
//        for (int i = 1; i < gameInfo.playerInfos.size(); i++) {
//            PlayerInfo playerInfo = gameInfo.playerInfos.get(i);
//            BaseAI ai = null;
//            if (playerInfo.isAvailable == false)
//                continue;
//            BaseCharacterView aiCharacter = null;
//            if (playerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER) {
//                aiCharacter = new NormalHunter(this, virtualWindow);
//                ai = new HunterAI(aiCharacter);
//            } else if (playerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF) {
//                aiCharacter = new NormalWolf(this, virtualWindow);
//                ai = new WolfAI(aiCharacter);
//            }
//            aiCharacter.gameHandler = gameHandler;
//            aiCharacter.setTeamID(playerInfo.teamID);
//
//
//            gameInfo.allCharacters.add(aiCharacter);
////            if (true)
////                continue;
//            Timer timerForAI = new Timer("AIPlayer1", true);
//            timerForAI.schedule(ai, 1000, 30);
//            timerForAIList.add(timerForAI);
//
//        }
//
//
//    }


//    private void addElementToMap() throws Exception {
//
//
//
//
////        for (int i = 0; i < gameMap.landformses.length; i++) {
////            if (Math.abs(i) % 3 == 0) {
////                for (int j = 0; j < gameMap.landformses[i].length; j++) {
////                    if (Math.abs(i - j) % 3 == 0)
////                        gameMap.landformses[i][j] = new TallGrassland(this);
////                }
////            }
////        }
//
//
//        //添加地形
//        gameMap = new GameMap(this);
//
//        mapBaseFrame.addView(gameMap);
//        gameMap.buildLandforms(this);
//
//        gameInfo.allCharacters = new ArrayList<BaseCharacterView>();
//        //添加我的角色
//        gameInfo.myPlayerInfo = gameInfo.playerInfos.get(0);
//        if (gameInfo.myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER)
//            myCharacter = new NormalHunter(this, virtualWindow);
//        else {
//            myCharacter = new NormalWolf(this, virtualWindow);
//        }
//        PromptView promptView = new PromptView(this, myCharacter);
//        mapBaseFrame.addView(promptView);
//        myCharacter.promptView = promptView;
//
//        myCharacter.setTeamID(gameInfo.myPlayerInfo.teamID);
//        myCharacter.isMyCharacter = true;
//        myCharacter.gameHandler = gameHandler;
//
//        gameInfo.allCharacters.add(myCharacter);
////        mapBaseFrame.addView(myCharacter);
//        mapBaseFrame.myCharacter = myCharacter;
//
//
////        NormalHunter testCharacter = new NormalHunter(this, virtualWindow);
////        testCharacter.setTeamID(2);
////        gameInfo.allCharacters.add(testCharacter);
//
//        //添加视点
//        gameInfo.mySight = new SightView(this);
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
//
//
//        rightAtttackButton = (AttackButton) this.findViewById(R.id.attack_button_right);
//        int buttonSize = rightAtttackButton.buttonSize;
//        rightAtttackButton.bindingCharacter = myCharacter;
//        FrameLayout.LayoutParams rabp = (FrameLayout.LayoutParams) rightAtttackButton.getLayoutParams();
//        if (rabp == null) {
//            rabp = new FrameLayout.LayoutParams(buttonSize, buttonSize);
//        }
//        //添加摇杆
//        leftRocker = (LeftRocker) this.findViewById(R.id.rocker_left);
//        leftRocker.setBindingCharacter(myCharacter);
//        mapBaseFrame.leftRocker = leftRocker;
//        rightRocker = (RightRocker) this.findViewById(R.id.rocker_right);
//
//        if (gameInfo.myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER) {
//            rightAtttackButton.reCreateBitmap();
//            rightRocker.setBindingCharacter(myCharacter);
//            mapBaseFrame.rightRocker = rightRocker;
//            FrameLayout.LayoutParams rrlp=(FrameLayout.LayoutParams) rightRocker.getLayoutParams();
//            rrlp.rightMargin=rightAtttackButton.buttonSize-JRocker.rockerRadius;
//
//            rabp.leftMargin = MyVirtualWindow.getWindowWidth(this) - rightAtttackButton.buttonSize;
//            rabp.topMargin = rightRocker.getTop()+rightRocker.getHeight()/2 -rightAtttackButton.buttonSize;
//
//
//            lockingButton = new LockingButton(this);
//            int lockingButtonSize = lockingButton.buttonSize;
//            lockingButton.bindingCharacter = myCharacter;
//            FrameLayout.LayoutParams lblp = (FrameLayout.LayoutParams) lockingButton.getLayoutParams();
//            if (lblp == null) {
//                lblp = new FrameLayout.LayoutParams(lockingButtonSize, lockingButtonSize);
//            }
//            lblp.leftMargin = rabp.leftMargin;
//            lblp.topMargin = rightRocker.getTop()+rightRocker.getHeight()/2+10;
//            lockingButton.setLayoutParams(lblp);
//            baseFrame.addView(lockingButton);
//
//
//        } else if (gameInfo.myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF) {
//            rightAtttackButton.reCreateBitmap();
//            baseFrame.removeView(rightRocker);
//            rightAtttackButton.buttonSize=(int)(rightAtttackButton.buttonSize*5/4);
//            rightAtttackButton.reCreateBitmap();
//            rabp.leftMargin = MyVirtualWindow.getWindowWidth(this) - rightAtttackButton.buttonSize - 50;
//            rabp.topMargin = MyVirtualWindow.getWindowHeight(this) - (leftRocker.getHeight() / 2 + rightAtttackButton.buttonSize / 2);
//            smellButton = new SmellButton(this);
//            int smellButtonSize = smellButton.buttonSize;
//            smellButton.bindingCharacter = myCharacter;
//            FrameLayout.LayoutParams sblp = (FrameLayout.LayoutParams) smellButton.getLayoutParams();
//            if (sblp == null) {
//                sblp = new FrameLayout.LayoutParams(smellButtonSize, smellButtonSize);
//            }
//            sblp.leftMargin = rabp.leftMargin - smellButtonSize;
//            sblp.topMargin = rabp.topMargin;
//            smellButton.setLayoutParams(sblp);
//            baseFrame.addView(smellButton);
//        }
//        rightAtttackButton.setLayoutParams(rabp);
//        leftRocker.bringToFront();
//        rightRocker.bringToFront();
//        rightAtttackButton.bringToFront();
////
////        if (myCharacter.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER) {
////
////        } else if (myCharacter.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF) {
////
////        }
//        if (gameInfo.playMode.equals("single")) {
//            startAI();
//        } else if (gameInfo.playMode.equals("bluetooth")) {
//            PlayerInfo remotePlayerInfo = gameInfo.playerInfos.get(1);
//            BaseCharacterView otherCharacter;
//            if (remotePlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER)
//                otherCharacter = new NormalHunter(this, virtualWindow);
//            else {
//                otherCharacter = new NormalWolf(this, virtualWindow);
//            }
//            otherCharacter.setTeamID(remotePlayerInfo.teamID);
//            otherCharacter.isMyCharacter = false;
//            otherCharacter.gameHandler = gameHandler;
//
//            gameInfo.allCharacters.add(otherCharacter);
//
//        }
//        for (BaseCharacterView character : gameInfo.allCharacters) {
////            int left = -1;
////            int top = -1;
////            float facingAngle = -1;
////
////            if (character.getTeamID() == 1) {
////                left = 50;
////                top = 50;
////                facingAngle = 45;
////
////            } else if (character.getTeamID() == 2) {
////                left = MyVirtualWindow.getWindowWidth(this) - character.characterBodySize - 50;
////                top = 50;
////                facingAngle = 135;
////            } else if (character.getTeamID() == 3) {
////                left = 50;
////                top = MyVirtualWindow.getWindowHeight(this) - character.characterBodySize - 50;
////                facingAngle = 315;
////            } else if (character.getTeamID() == 4) {
////                left = MyVirtualWindow.getWindowWidth(this) - character.characterBodySize - 50;
////                top = MyVirtualWindow.getWindowHeight(this) - character.characterBodySize - 50;
////                facingAngle = 225;
////            }
////
////            if (left > 0 && top > 0 && facingAngle > 0) {
////                FrameLayout.LayoutParams characterParams = (FrameLayout.LayoutParams) character.getLayoutParams();
////                characterParams.leftMargin = left;
////                characterParams.topMargin = top;
////                character.nowFacingAngle = facingAngle;
////                character.centerX=left+character.getWidth()/2;
////                character.centerY=top+character.getHeight()/2;
////                new AttackRange(this, character);
////                new ViewRange(this, character);
////                character.setLayoutParams(characterParams);
////
//            if (character.isMyCharacter) {
//                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mapBaseFrame.getLayoutParams();
//                params.leftMargin = -(character.centerX - MyVirtualWindow.getWindowWidth(this) / 2);
//                params.topMargin = -(character.centerY - MyVirtualWindow.getWindowHeight(this) / 2);
//                mapBaseFrame.setLayoutParams(params);
//            }
//            mapBaseFrame.addView(character);
//            mapBaseFrame.addView(character.attackRange);
//            mapBaseFrame.addView(character.viewRange);
////            }
//        }
//        mapBaseFrame.invalidate();
//        t1.bringToFront();
//        t2.bringToFront();
//        t3.bringToFront();
//        t4.bringToFront();
//        t5.bringToFront();
//        if (lockingButton != null)
//            lockingButton.bringToFront();
//        if (smellButton != null)
//            smellButton.bringToFront();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_base_area);
//        initBluetoothConnection();

        engine =new GameMainEngine(this);
        gameInfo = engine.getGameInfo();
        gameMap = engine.getGameMap();
        baseFrame = engine.getBaseFrame();
        mapBaseFrame = engine.getMapBaseFrame();
        virtualWindow=engine.getVirtualWindow();
        engine.runEngine();

//        virtualWindow = new MyVirtualWindow(this, mapBaseFrame);
//        gameInfo = (GameInfo) getIntent().getExtras().get("gameInfo");
//        if(gameInfo==null)
//            finish();
//        gameInfo.isStop = false;
//        gameInfo.allTrajectories = new Vector<Trajectory>();
//        backGround = MediaPlayer.create(this, R.raw.background);
//        ViewUtils.initWindowParams(this);
//        DisplayMetrics dm = ViewUtils.getWindowsDisplayMetrics();
//        baseFrame = (FrameLayout) findViewById(R.id.baseFrame);
//        mapBaseFrame = new MapBaseFrame(this, gameInfo.mapWidth, gameInfo.mapHeight);
//        baseFrame.addView(mapBaseFrame);


//        t1 = new TextView(this);
//        t1.setTextColor(Color.WHITE);
//        t1.setTextSize(15);
//        t2 = new TextView(this);
//        t2.setTextColor(Color.WHITE);
//        t2.setTextSize(15);
//        t3 = new TextView(this);
//        t3.setTextColor(Color.WHITE);
//        t3.setTextSize(15);
//        t4 = new TextView(this);
//        t4.setTextColor(Color.WHITE);
//        t4.setTextSize(15);
//        t5 = new TextView(this);
//        t5.setTextColor(Color.WHITE);
//        t5.setTextSize(15);
//        t6 = new TextView(this);
//        t6.setTextColor(Color.WHITE);
//        t6.setTextSize(15);
//        gameResult = new TextView(this);
//        gameResult.setTextColor(Color.WHITE);
//        gameResult.setTextSize(100);
//        FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        p1.leftMargin = 0;
//        p1.topMargin = 50;
//        t1.setLayoutParams(p1);
//        FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        p2.leftMargin = 0;
//        p2.topMargin = 100;
//        t2.setLayoutParams(p2);
//        FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        p3.leftMargin = 0;
//        p3.topMargin = 150;
//        t3.setLayoutParams(p3);
//        FrameLayout.LayoutParams p4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        p4.leftMargin = 0;
//        p4.topMargin = 200;
//        t4.setLayoutParams(p4);
//        FrameLayout.LayoutParams p5 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        p5.leftMargin = 0;
//        p5.topMargin = 250;
//        t5.setLayoutParams(p5);
//        FrameLayout.LayoutParams p6 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        p6.leftMargin = 0;
//        p6.topMargin = 300;
//        t6.setLayoutParams(p6);
//        FrameLayout.LayoutParams p7 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        p7.leftMargin = MyVirtualWindow.getWindowWidth(this) / 2 - 200;
//        p7.topMargin = MyVirtualWindow.getWindowHeight(this) / 2;
//        gameResult.setLayoutParams(p7);
//
//        baseFrame.addView(t1);
//        baseFrame.addView(t2);
//        baseFrame.addView(t3);
//        baseFrame.addView(t4);
//        baseFrame.addView(t5);
//        baseFrame.addView(t6);
//        baseFrame.addView(gameResult);
//        if (gameInfo.playMode.equals("bluetooth")) {
//            if (gameInfo.playerInfos != null) {
//                for (PlayerInfo pi : gameInfo.playerInfos) {
//                    if (pi.isServer == true)
//                        gameInfo.serverMac = pi.mac;
//                }
//            }
//            bluetoothAdapter = BluetoothController.mBluetoothAdapter;
//            if (gameInfo.playerInfos.get(0).isServer) {
//                bluetoothAcceptThread = new BluetoothAcceptThread();
//                bluetoothAcceptThread.setDaemon(true);
//                bluetoothAcceptThread.start();
//            } else {
//                BluetoothDevice serverDevice = bluetoothAdapter.getRemoteDevice(gameInfo.serverMac);
//                bluetoothConnectThread = new BluetoothConnectThread(serverDevice);
//                bluetoothConnectThread.setDaemon(true);
//                bluetoothConnectThread.start();
//            }
//        }
//
//        mapBaseFrame.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    addElementToMap();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

//        FrameLayout.LayoutParams paramsForMapBase = (FrameLayout.LayoutParams) mapBaseFrame.getLayoutParams();
//        paramsForMapBase.width = 2000;
//        paramsForMapBase.height = 1500;
//        mapBaseFrame.setLayoutParams(paramsForMapBase);


        //scheduleAtFixedRate后一次Task不以前一个Task执行完毕的时间为起点延时执行
//        timerForAllMoving.scheduleAtFixedRate(new GameMainTask(), 1000, 30);
//        timerForTrajectory.scheduleAtFixedRate(new RemoveTrajectoryTask(), 1000, 300);
//        timerForAllMoving.scheduleAtFixedRate(new GameMainTask(), 1000, 30);
//        timerForTrajectory.schedule(new RemoveTrajectoryTask(), 1000, 300);
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
        engine.stopEngine();
//        initBluetoothConnection();
//        gameInfo.isStop = true;
//        timerForTrajectory.cancel();
//        backGround.release();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        if (timerForAllMoving != null)
//            timerForAllMoving.cancel();
//
//
//        for (Timer timer : timerForAIList) {
//            timer.cancel();
//        }

        super.onDestroy();

    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        engine.stopEngine();
//        gameInfo.isStop = true;
//        timerForAllMoving.cancel();
//
//        timerForTrajectory.cancel();
//        for (Timer timer : timerForAIList) {
//            timer.cancel();
//        }
//        backGround.release();
        super.onBackPressed();

    }
}
