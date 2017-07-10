package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.myObj.ClientScanResult;
import com.jedi.wolf_and_hunter.myObj.ConnectThreadBox;
import com.jedi.wolf_and_hunter.utils.WifiHotspotController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.jedi.wolf_and_hunter.myObj.ConnectThreadBox.CONNECT_ROLE_CLIENT;
import static com.jedi.wolf_and_hunter.myObj.ConnectThreadBox.CONNECT_ROLE_SERVER;
import static com.jedi.wolf_and_hunter.myObj.ConnectThreadBox.nowRole;

public class WifiOnlineActivity extends Activity {
    public int count = 0;

    WifiManager wifiManager;
    MyWifiHandler myWifiHandler = null;
    String hotSpotSSID = "wifiTest";
    WifiReceiver receiver;
    Button startGameButton;
    Timer timerForScanClients;
    ArrayList<ClientScanResult> clients;
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

                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
//                    Toast.makeText(WifiOnlineActivity.this, "已断开现有网络", Toast.LENGTH_SHORT).show();
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    String ssid = WifiHotspotController.getCleanSSID(wifiManager);

                    boolean result = ssid.equals(hotSpotSSID);
                    if (result) {
                        Toast.makeText(WifiOnlineActivity.this, "已经链接到指定热点", Toast.LENGTH_SHORT).show();
                        ConnectThreadBox.nowRole = CONNECT_ROLE_CLIENT;
                        startGameButton.setEnabled(true);
                    } else {
                        Toast.makeText(WifiOnlineActivity.this, "未链接到指定热点", Toast.LENGTH_SHORT).show();
                        ConnectThreadBox.nowRole = ConnectThreadBox.CONNECT_ROLE_NONE;
                        startGameButton.setEnabled(false);
                    }
                    //获取当前wifi名称
//                    Toast.makeText(WifiOnlineActivity.this, "连接到网络 " + wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();

                }

            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//wifi打开与否
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                    startGameButton.setEnabled(false);
//                    Toast.makeText(WifiOnlineActivity.this, "wifi已处于关闭状态", Toast.LENGTH_SHORT).show();
                } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {

//                    Toast.makeText(WifiOnlineActivity.this, "wifi已经开启", Toast.LENGTH_SHORT).show();
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
        startGameButton = (Button) findViewById(R.id.button_start_wifi_game);
        initWifiParams();
        myWifiHandler = new MyWifiHandler();
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        receiver = new WifiReceiver();
        registerReceiver(receiver, filter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this) == false) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
            }
        }


    }

//    public void runPhoneServer(View view) {
//
//            if (ConnectThreadBox.serverConnectThread == null || ConnectThreadBox.serverConnectThread.getState() == Thread.State.TERMINATED) {
//                ConnectThreadBox.clear();
//                ConnectThreadBox.startTempServerThread(this, myWifiHandler);
//            } else {
//                Toast.makeText(getBaseContext(), "服务器线程已经启动，无需重复开启", Toast.LENGTH_SHORT).show();
//            }
//
//
//
//    }
//    public void runEMClient(View view) {
//        if (ConnectThreadBox.clientConnectThread == null || ConnectThreadBox.clientConnectThread.getState() == Thread.State.TERMINATED) {
//            ConnectThreadBox.clear();
//            ConnectThreadBox.startTempConnectThread(this, myWifiHandler);
//        } else {
//            Toast.makeText(getBaseContext(), "客户端线程已经启动，无需重复开启", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    public void createHotSpot(View view) {
        initWifiParams();

        WifiConfiguration wifiConfiguration = WifiHotspotController.createWifiConfiguration(wifiManager, hotSpotSSID, "987654321", 3, "ap");
        boolean result = WifiHotspotController.createWifiAP(wifiManager, wifiConfiguration, true);
        if (result) {
            Toast.makeText(WifiOnlineActivity.this, "热点创建成功", Toast.LENGTH_SHORT).show();
            ConnectThreadBox.nowRole = ConnectThreadBox.CONNECT_ROLE_SERVER;
            timerForScanClients=new Timer("timerForScanClients");
            timerForScanClients.schedule(new CheckConnectedResultTask(),0 ,2000);
        } else {
            Toast.makeText(WifiOnlineActivity.this, "热点创建失败:", Toast.LENGTH_SHORT).show();
            ConnectThreadBox.nowRole = ConnectThreadBox.CONNECT_ROLE_NONE;
            if(timerForScanClients!=null)
                timerForScanClients.cancel();
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
        startGameButton.setEnabled(false);
        ConnectThreadBox.nowRole = ConnectThreadBox.CONNECT_ROLE_NONE;
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        if(wifiInfo!=null){
            int id=wifiManager.getConnectionInfo().getNetworkId();
            WifiHotspotController.disconnectWifi(id);
        }

        ConnectThreadBox.clear();
        Toast.makeText(this, "已经断开所有链接", Toast.LENGTH_SHORT).show();
    }

    public void connectHotSpot(View view) {
        initWifiParams();
        wifiManager.setWifiEnabled(true);
        WifiConfiguration wifiConfiguration = WifiHotspotController.createWifiConfiguration(wifiManager, hotSpotSSID, "987654321", 3, "wifi");
        boolean result = WifiHotspotController.connectHotspot(wifiManager, wifiConfiguration);

        if (result == false)
            Toast.makeText(getBaseContext(), "热点链接失败", Toast.LENGTH_SHORT).show();

    }

    class CheckConnectedResultTask extends TimerTask{


        @Override
        public void run() {
            if(ConnectThreadBox.nowRole==CONNECT_ROLE_SERVER) {
                clients = getConnectedClientScanResult();
                myWifiHandler.sendEmptyMessage(MyWifiHandler.UPDATE_CLIENT_RESULT);

            }
        }
    }

    public class MyWifiHandler extends Handler {
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */

        public static final int ACCEPT_SUCCESS = 1;
        public static final int ACCEPT_FAIL = -1;
        public static final int ONE_ACCEPT_FAIL = -11;
        public static final int CONNECT_SUCCESS = 2;
        public static final int CONNECT_FAIL = -2;
        public static final int START_CONNECTING = 22;
        public static final int GAME_START = 666;
        public static final int REFRESH_PLAYER_LIST_VIEW = 7777;
        public static final int CONNECT_WRONG_HOTSPOT = -222;
        public static final int UPDATE_CLIENT_RESULT = 3;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case ACCEPT_SUCCESS:
                    Log.i("SERVER", "完成" + ++count + "次服务器端任务");
//                    Toast.makeText(getBaseContext(), "成功接收第" + ++count + "份客户端数据", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_SUCCESS:
                    Log.i("Client", "完成" + ++count + "次客户端任务");
//                    Toast.makeText(getBaseContext(), "成功接收第" + ++count + "份服务器端数据", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    Log.i("Client", "与服务器连接失败");
                    break;
                case ACCEPT_FAIL:
                    Log.i("SERVER", "服务器停摆了");
                    break;
                case ONE_ACCEPT_FAIL:
                    Log.i("SERVER", "与其中一个服务端链接出错");
                    break;
                case CONNECT_WRONG_HOTSPOT:
                    Log.i("Client", "连接到的热点不是目标热点" + hotSpotSSID);
                    break;
                case START_CONNECTING:
                    Toast.makeText(getBaseContext(), "客户端开始发送数据", Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_CLIENT_RESULT:
                    if(clients!=null&&clients.size()>0){
                        startGameButton.setText("开始游戏("+clients.size()+")");
                        startGameButton.setEnabled(true);
                    }else{
                        startGameButton.setText("开始游戏");
                        startGameButton.setEnabled(false);
                    }
                    break;

            }
        }
    }


    public void startWifiGame(View view) {
        if (ConnectThreadBox.nowRole == ConnectThreadBox.CONNECT_ROLE_NONE) {
            Toast.makeText(getBaseContext(), "请先开启热点或连接热点", Toast.LENGTH_SHORT).show();
        } else if (ConnectThreadBox.nowRole == ConnectThreadBox.CONNECT_ROLE_SERVER) {

            if (ConnectThreadBox.serverConnectThread == null || ConnectThreadBox.serverConnectThread.getState() == Thread.State.TERMINATED) {
                ConnectThreadBox.clear();
                ConnectThreadBox.startWifiServerThread(this, myWifiHandler, wifiManager);
            } else {
                Toast.makeText(getBaseContext(), "服务器线程已经启动，无需重复开启", Toast.LENGTH_SHORT).show();
            }

        } else if (ConnectThreadBox.nowRole == CONNECT_ROLE_CLIENT) {
            if (ConnectThreadBox.clientConnectThread == null || ConnectThreadBox.clientConnectThread.getState() == Thread.State.TERMINATED) {
                ConnectThreadBox.clear();
                ConnectThreadBox.startWifiConnectThread(this, myWifiHandler, wifiManager);
            } else {
                Toast.makeText(getBaseContext(), "客户端线程已经启动，无需重复开启", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getBaseContext(), "请先开启热点或连接热点", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<ClientScanResult> getConnectedClientScanResult() {
        BufferedReader br = null;
        ArrayList<ClientScanResult> result = null;

        try {
            result = new ArrayList<>();
            br = new BufferedReader(new FileReader("/proc/net/arp"));//读取这个文件
            String ss = br.toString();
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");//将文件里面的字段分割开来
                if (splitted.length >= 4) {
                    // Basic sanity check
                    String mac = splitted[3];// 文件中分别是IP address  HW type Flags HW address mask Device
//然后我们拿取HW address  也就是手机的mac地址进行匹配  如果有 就证明是手机
                    if (mac.matches("..:..:..:..:..:..")) {
                        boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(1000);

                        if (isReachable) {
                            result.add(new ClientScanResult(splitted[0], splitted[3], splitted[5], isReachable));//最后如果能匹配 那就证明是连接了热点的手机  加到这个集合里 里面有所有需要的信息
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("server", e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                Log.e("server", e.getMessage());
            }
            return result;
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
        if(timerForScanClients!=null)
            timerForScanClients.cancel();
        unregisterReceiver(receiver);
        ConnectThreadBox.clear();
        WifiHotspotController.closeConnection(wifiManager);
    }

}
