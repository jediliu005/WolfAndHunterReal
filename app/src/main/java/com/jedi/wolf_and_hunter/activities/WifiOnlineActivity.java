package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Toast;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.utils.WifiHotspotController;

public class WifiOnlineActivity extends Activity {

    WifiManager wifiManager;

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
//                    Toast.makeText(WifiOnlineActivity.this, "wifi网络连接断开", Toast.LENGTH_SHORT).show();
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                    //获取当前wifi名称
                    Toast.makeText(WifiOnlineActivity.this, "连接到网络 " + wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();

                }

            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {//wifi打开与否
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                    Toast.makeText(WifiOnlineActivity.this, "系统关闭wifi", Toast.LENGTH_SHORT).show();
                } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    Toast.makeText(WifiOnlineActivity.this, "系统开启wifi", Toast.LENGTH_SHORT).show();
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
        if (result)
            Toast.makeText(WifiOnlineActivity.this, "热点创建成功", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(WifiOnlineActivity.this, "热点创建失败:", Toast.LENGTH_SHORT).show();


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
        WifiHotspotController.closeConnection(wifiManager);
    }

    public void connectHotSpot(View view) {
        initWifiParams();
        wifiManager.setWifiEnabled(true);
        WifiConfiguration wifiConfiguration = WifiHotspotController.createWifiConfiguration(wifiManager, "wifiTest", "987654321", 3, "wifi");
        boolean result = WifiHotspotController.connectHotspot(wifiManager, wifiConfiguration);
        if (result)
            Toast.makeText(WifiOnlineActivity.this, "热点链接成功", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(WifiOnlineActivity.this, "热点链接失败:", Toast.LENGTH_SHORT).show();
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
