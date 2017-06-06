package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jedi.wolf_and_hunter.R;

import java.util.ArrayList;
import java.util.Set;

public class BlueToothTestActivity extends Activity {
    private static final long SCAN_PERIOD = 10000;
    BluetoothAdapter bluetoothAdapter;
    Boolean isBlueToothEnabled;
    ArrayAdapter<BluetoothDevice> mLeDeviceListAdapter;
    ListView devicesListView;
    private Handler mHandler;
    boolean mScanning;

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.add(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    public void searchDevice(View view) {
        scanLeDevice(true);
//        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
//        ArrayList<String> nameList=new ArrayList<String>();
//        for(BluetoothDevice device:deviceSet){
//            String name=device.getName();
//            nameList.add(name);
//        }
//
//        adapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_multiple_choice);
//        adapter.addAll(nameList);
//        devicesListView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth_test);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isBlueToothEnabled = bluetoothAdapter.isEnabled();
        devicesListView = (ListView) findViewById(R.id.list_view_devices);

//        adapter=new ArrayAdapter<BluetoothDevice>(this,R.id.list_view_devices);
        if (isBlueToothEnabled) {
            Toast.makeText(this, "蓝牙可用", Toast.LENGTH_LONG).show();
        } else {
//            Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_LONG).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.startDiscovery();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothAdapter.startDiscovery();
        } else {
            mScanning = false;
            bluetoothAdapter.startDiscovery();
        }
    }

}
