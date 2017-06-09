package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.ULocale;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.utils.BluetoothController;
import com.jedi.wolf_and_hunter.utils.StrTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlueToothTestActivity extends Activity {
    private static final long SCAN_PERIOD = 10000;
    BluetoothAdapter bluetoothAdapter;
    SimpleAdapter discoveredPlayerAdapter;
    SimpleAdapter joinedPlayerAdapter;
    ListView devicesListView;
    List<Map<String, String>> discoveredDeviceInfoList;
    List<Map<String, String>> joinedDeviceInfoList;
    String ServerDeviceMac;
    ArrayList<String> clientDeviceMacs;
    ListView joinedPlayerListView;
    BluetoothController bluetoothController;
    boolean isLoopSearching;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 1);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(context, "蓝牙已关闭", Toast.LENGTH_SHORT).show();

                            return;
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(context, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                            return;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Toast.makeText(context, "正在打开蓝牙", Toast.LENGTH_SHORT).show();
                            return;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Toast.makeText(context, "正在关闭蓝牙", Toast.LENGTH_SHORT).show();
                            return;

                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Toast.makeText(context, "开始搜索设备", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    if (isLoopSearching)
                        searchDevice(null);
                    else
                        Toast.makeText(context, "结束搜索设备", Toast.LENGTH_SHORT).show();

                    break;
                case BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE:
                    Toast.makeText(context, "本设备开启允许被发现", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.ACTION_REQUEST_ENABLE:
                    Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 10);
                    startActivity(in);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Toast.makeText(context, "发现设备", Toast.LENGTH_SHORT).show();
                    //从Intent中获取设备的BluetoothDevice对象
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceMAC = device.getAddress();
                    boolean hasDevice = false;
                    for (Map<String, String> deviceMap : discoveredDeviceInfoList) {
                        if (deviceMap.get("mac").equals(deviceMAC)) {
                            hasDevice = true;
                            break;
                        }
                    }
                    if (hasDevice == false) {
                        Map<String, String> dataMap = new HashMap<String, String>();
                        dataMap.put("name", device.getName());
                        dataMap.put("mac", device.getAddress());
                        discoveredDeviceInfoList.add(dataMap);
                        discoveredPlayerAdapter.notifyDataSetChanged();
                    }
                    break;

            }


        }
    };

    public void searchDevice(View view) {
        isLoopSearching = true;
        if (bluetoothAdapter.isEnabled() == false) {
            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
            return;
        }
//        Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
//        startActivity(in);
        bluetoothController.startDiscovery();

    }

    class DeviceListViewOnItemClickListener implements AdapterView.OnItemClickListener {


        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Map<String, String> discoveredDeviceMap = discoveredDeviceInfoList.get(position);
            String name = discoveredDeviceMap.get("name");
            String mac = discoveredDeviceMap.get("mac");

            if (clientDeviceMacs.contains(discoveredDeviceMap.get("mac")) == false) {
                clientDeviceMacs.add(mac);
                Map<String, String> dataMap = new HashMap<String, String>();
                dataMap.put("name", name);
                dataMap.put("mac", mac);
                joinedDeviceInfoList.add(dataMap);
                joinedPlayerAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth_test);
        clientDeviceMacs = new ArrayList<String>();
        bluetoothController = new BluetoothController(this);
        bluetoothAdapter = bluetoothController.mBluetoothAdapter;

        discoveredDeviceInfoList = new ArrayList<Map<String, String>>();
        devicesListView = (ListView) findViewById(R.id.list_view_devices);
        devicesListView.setOnItemClickListener(new DeviceListViewOnItemClickListener());
        discoveredPlayerAdapter = new SimpleAdapter(this, discoveredDeviceInfoList, R.layout.online_user_list_item, new String[]{"name", "mac"}, new int[]{R.id.device_info_name, R.id.device_info_mac});
        devicesListView.setAdapter(discoveredPlayerAdapter);


        joinedDeviceInfoList = new ArrayList<Map<String, String>>();
        joinedPlayerListView = (ListView) findViewById(R.id.list_view_joined_player);
        joinedPlayerAdapter = new SimpleAdapter(this, joinedDeviceInfoList, R.layout.online_user_list_item, new String[]{"name", "mac"}, new int[]{R.id.device_info_name, R.id.device_info_mac});
        joinedPlayerListView.setAdapter(joinedPlayerAdapter);


        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);//这条没用，不明原因，我猜是不广播这个？
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);//这条没用，不明原因，我猜是不广播这个？
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        if (bluetoothAdapter.isEnabled()) {
//            Toast.makeText(this, "蓝牙可用,本设备可见100秒", Toast.LENGTH_LONG).show();
        Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
        startActivity(in);
        } else {
            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
//        bluetoothController.closeDiscoverableTimeout();
        Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(in);
        bluetoothController.cancelDiscovery();
    }
}
