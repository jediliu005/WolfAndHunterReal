package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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
    Boolean isBlueToothEnabled;
    SimpleAdapter mSimpleAdapter;
    ListView devicesListView;
    List<Map<String,String>> deviceInfoData;
    BluetoothController bluetoothController=new BluetoothController(this);
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 1);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(context, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(context, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Toast.makeText(context, "正在打开蓝牙", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Toast.makeText(context, "正在关闭蓝牙", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, "未知状态", Toast.LENGTH_SHORT).show();
            }

                String action = intent.getAction();

            switch (action){
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Toast.makeText(context, "开始搜索设备", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(context, "结束搜索设备", Toast.LENGTH_SHORT).show();
                    break;

            }
                //发现了设备
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    Toast.makeText(context, "发现设备", Toast.LENGTH_SHORT).show();
                    //从Intent中获取设备的BluetoothDevice对象
                    BluetoothDevice device =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Map<String,String> dataMap=new HashMap<String,String>();
                    dataMap.put("name",device.getName());
                    dataMap.put("mac",device.getAddress());
                    deviceInfoData.add(dataMap);
                    mSimpleAdapter.notifyDataSetChanged();

                }

        }
    };

    public void searchDevice(View view){
        isBlueToothEnabled = bluetoothAdapter.isEnabled();
        if(isBlueToothEnabled==false) {
            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
            return;
        }
        bluetoothController.startDiscovery();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth_test);
        deviceInfoData=new ArrayList<Map<String, String>>();
        bluetoothAdapter = bluetoothController.mBluetoothAdapter;
        devicesListView = (ListView) findViewById(R.id.list_view_devices);
        mSimpleAdapter=new SimpleAdapter(this,deviceInfoData,R.layout.online_user_list_item,new String[]{"name","mac"},new int[]{R.id.device_info_name,R.id.device_info_mac});
        Map<String,String> dataMap=new HashMap<String,String>();

        devicesListView.setAdapter(mSimpleAdapter);

        isBlueToothEnabled = bluetoothAdapter.isEnabled();
        if (isBlueToothEnabled) {
//            Toast.makeText(this, "蓝牙可用,本设备可见100秒", Toast.LENGTH_LONG).show();
            Intent in=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 100);
            startActivity(in);
        } else {
//            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        bluetoothController.cancelDiscovery();
    }
}
