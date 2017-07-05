package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.myObj.PlayerInfo;
import com.jedi.wolf_and_hunter.myObj.ThreadBoxes;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.utils.WifiHotspotController;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class WifiOnlineActivity extends Activity {
    public int count = 0;
    static final int CONNECT_ROLE_NONE = 0;
    static final int CONNECT_ROLE_SERVER = 1;
    static final int CONNECT_ROLE_CLIENT = 2;
    WifiManager wifiManager;
    MyWifiHandler myWifiHandler = null;
    int myRole = 0;

    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
           /* if(intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)){
                //signal strength changed
            }
            else
            */
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {//wifi连接上与否
                System.out.println("网络状态改变");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    Toast.makeText(WifiOnlineActivity.this, "已断开现有网络", Toast.LENGTH_SHORT).show();
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                    //获取当前wifi名称
                    Toast.makeText(WifiOnlineActivity.this, "连接到网络 " + wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();

                }

            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//wifi打开与否
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                    Toast.makeText(WifiOnlineActivity.this, "wifi已处于关闭状态", Toast.LENGTH_SHORT).show();
                } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                    Toast.makeText(WifiOnlineActivity.this, "wifi已经开启", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initWifiParams() {
        if (wifiManager == null)
            wifiManager = WifiHotspotController.getWifiManager(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_online);
        initWifiParams();
        myWifiHandler = new MyWifiHandler();
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        WifiReceiver receiver = new WifiReceiver();
        registerReceiver(receiver, filter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this) == false) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
            }
        }


    }

    public void createHotSpot(View view) {
        initWifiParams();

        WifiConfiguration wifiConfiguration = WifiHotspotController.createWifiConfiguration(wifiManager, "wifiTest", "987654321", 3, "ap");
        boolean result = WifiHotspotController.createWifiAP(wifiManager, wifiConfiguration, true);
        if (result) {
            Toast.makeText(WifiOnlineActivity.this, "热点创建成功", Toast.LENGTH_SHORT).show();
            myRole = CONNECT_ROLE_SERVER;
        } else {
            Toast.makeText(WifiOnlineActivity.this, "热点创建失败:", Toast.LENGTH_SHORT).show();
            myRole = CONNECT_ROLE_NONE;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (Settings.System.canWrite(this))
                Toast.makeText(this, "权限已开启", Toast.LENGTH_SHORT).show();
        }
    }

    public void closeConnection(View view) {
        initWifiParams();
        myRole = CONNECT_ROLE_NONE;
        WifiHotspotController.closeConnection(wifiManager);
    }

    public void connectHotSpot(View view) {
        initWifiParams();
        wifiManager.setWifiEnabled(true);
        WifiConfiguration wifiConfiguration = WifiHotspotController.createWifiConfiguration(wifiManager, "wifiTest", "987654321", 3, "wifi");
        boolean result = WifiHotspotController.connectHotspot(wifiManager, wifiConfiguration);
        if (result) {
            Toast.makeText(WifiOnlineActivity.this, "热点链接成功", Toast.LENGTH_SHORT).show();
            myRole = CONNECT_ROLE_CLIENT;
        } else {
            Toast.makeText(WifiOnlineActivity.this, "热点链接失败", Toast.LENGTH_SHORT).show();
            myRole = CONNECT_ROLE_NONE;
        }
    }

    class MyWifiHandler extends Handler {
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */

        public static final int ACCEPT_SUCCESS = 0;
        public static final int CONNECT_SUCCESS = 1;
        public static final int GAME_START = 2;
        public static final int REFRESH_PLAYER_LIST_VIEW = 3;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case ACCEPT_SUCCESS:
                    Toast.makeText(getBaseContext(), "成功接收第" + count + "份客户端数据", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_SUCCESS:
                    Toast.makeText(getBaseContext(), "成功接收第" + count + "份服务器端数据", Toast.LENGTH_SHORT).show();
                    break;


            }
        }
    }

    public class AcceptThread extends Thread {


        @Override
        public void run() {
            super.run();
            //不断监听直到返回连接或者发生异常
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(1357);
                while (true) {
                    Socket socket = serverSocket.accept();

                    if (socket != null) {
                        ServerDealDataThread sddt = new ServerDealDataThread(socket);
                        sddt.start();
                        ThreadBoxes.serverDealDataThreads.add(sddt);
                    }


//                    manageConnectedSocket(socket);
                }
            } catch (Exception e) {
                Log.e("AcceptThread", "哎呀，当个服务器不容易啊，不知干嘛又挂了。。。。。。。。。。。");
            } finally {

                try {
                    if (serverSocket != null)
                        serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        /**
         * 取消正在监听的接口
         */


        class ServerDealDataThread extends Thread {
            Socket socket;

            public ServerDealDataThread(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void run() {


                InputStream is = null;
                OutputStream os = null;

                try {
                    while (true) {
                        os = socket.getOutputStream();
                        is = socket.getInputStream();
                        ObjectInputStream ois = new ObjectInputStream(is);
                        PlayerInfo pi = (PlayerInfo) ois.readObject();
                        if (pi != null)
                            myWifiHandler.sendEmptyMessage(MyWifiHandler.ACCEPT_SUCCESS);
                        ObjectOutputStream oos = new ObjectOutputStream(os);
                        PlayerInfo myPlayerInfo = new PlayerInfo(true, 1, BaseCharacterView.CHARACTER_TYPE_HUNTER, 1, "", true);
                        oos.writeObject(myPlayerInfo);
                        oos.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e("ServerDealDataThread", e.getMessage());
                } finally {


                }
            }
        }

//        public void manageConnectedSocket(BluetoothSocket socket) {
//            BluetoothSocket socket1 = socket;
//            dealServerDataThread = new Thread(new ServerDealDataThread(socket));
//            dealServerDataThread.start();
//
//        }

    }


    class ConnectThread extends Thread {


        @Override
        public void run() {

            super.run();
            //取消搜索因为搜索会让连接变慢
            OutputStream os = null;
            InputStream is = null;
            Socket socket = null;
            try {
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                int serverIP = dhcpInfo.serverAddress;
                byte[] ipAddress = BigInteger.valueOf(serverIP).toByteArray();
                InetAddress ia = InetAddress.getByAddress(ipAddress);
                socket = new Socket(ia, 1357);
//                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(mUUID));
                //通过socket连接设备，这是一个阻塞操作，知道连接成功或发生异常


                os = socket.getOutputStream();
                is = socket.getInputStream();
                while (true) {
                    ObjectOutputStream oos = new ObjectOutputStream(os);
                    PlayerInfo myPlayerInfo = new PlayerInfo(true, 1, BaseCharacterView.CHARACTER_TYPE_HUNTER, 1, "", false);
                    oos.writeObject(myPlayerInfo);
                    oos.flush();
                    ObjectInputStream ois = new ObjectInputStream(is);
                    PlayerInfo serverPlayerInfo = (PlayerInfo) ois.readObject();
                    if (serverPlayerInfo != null)
                        myWifiHandler.sendEmptyMessage(MyWifiHandler.CONNECT_SUCCESS);
                }

            } catch (Exception e) {
                Log.e("ConnectThread", "他妈的，当个客户端不容易啊，服务器又不理我了。。。。。。。。。。。");

            } finally {
                try {
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                    if (socket != null && socket.isClosed() == false)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            //管理连接(在独立的线程)
            // manageConnectedSocket(mmSocket);
        }


    }


    public void testWifi(View view) {
        if (myRole == CONNECT_ROLE_NONE) {
            Toast.makeText(getBaseContext(), "请先开启热点或连接热点", Toast.LENGTH_SHORT).show();
        } else if (myRole == CONNECT_ROLE_SERVER) {
            if (ThreadBoxes.serverConnectThread == null && ThreadBoxes.serverConnectThread.getState() == Thread.State.TERMINATED) {
                AcceptThread acceptThread = new AcceptThread();
                acceptThread.start();
                ThreadBoxes.serverConnectThread = acceptThread;
            } else {
                Toast.makeText(getBaseContext(), "服务器线程已经启动，无需重复开启", Toast.LENGTH_SHORT).show();
            }

        } else if (myRole == CONNECT_ROLE_CLIENT) {
            if (ThreadBoxes.clientConnectThread == null && ThreadBoxes.clientConnectThread.getState() == Thread.State.TERMINATED) {
                ConnectThread connectThread = new ConnectThread();
                connectThread.start();
                ThreadBoxes.clientConnectThread = connectThread;
            } else {
                Toast.makeText(getBaseContext(), "请先开启热点或连接热点", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiHotspotController.closeConnection(wifiManager);
    }

}
