package com.jedi.wolf_and_hunter.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by Administrator on 2017/6/7.
 */

public class BluetoothController {
    public BluetoothAdapter mBluetoothAdapter;
    public Set<BluetoothDevice> devicesSet;
    public Context context;

    public BluetoothController(Context context) {
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //发现了设备
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Toast.makeText(context, "发现设备", Toast.LENGTH_SHORT).show();
                    //从Intent中获取设备的BluetoothDevice对象
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devicesSet.add(device);

                }
            }
        };
    }

    private BroadcastReceiver mReceiver;


    /**
     * 判断当前设备是否支持蓝牙
     *
     * @return
     */
    public boolean isSupportBluetooth() {
        if (mBluetoothAdapter != null) {
            return true;
        }
        return false;
    }

    /**
     * 获取蓝牙的状态
     *
     * @return
     */
    public boolean getBluetoothStatus() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    /**
     * 打开蓝牙
     *
     * @param activity
     * @param requestCode
     */
    public void turnOnBluetooth(Activity activity, int requestCode) {
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 关闭蓝牙
     */
    public void turnOffBluetooth() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }


    public Set<BluetoothDevice> getConnetedDevices() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            return mBluetoothAdapter.getBondedDevices();
        }
        return null;
    }


    public void startDiscovery() {
        mBluetoothAdapter.startDiscovery();
    }

    public void cancelDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }

    @Deprecated
    public void setDiscoverableTimeout(int timeout) {

        try {


            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(mBluetoothAdapter, timeout);
            setScanMode.invoke(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, timeout);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeDiscoverableTimeout();
                }
            }, timeout*1000);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void closeDiscoverableTimeout() {
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(mBluetoothAdapter, 1);
            setScanMode.invoke(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}