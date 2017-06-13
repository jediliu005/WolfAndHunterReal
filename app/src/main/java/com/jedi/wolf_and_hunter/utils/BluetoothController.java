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
    public static final BluetoothAdapter mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    public static final String mUUID = "e6adf90a-b0d3-4fa2-aa6a-97c119f1f1c6";
    public Context context;
    public BluetoothController(Context context) {
        this.context = context;
//        mReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                //发现了设备
//                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                    Toast.makeText(context, "发现设备", Toast.LENGTH_SHORT).show();
//                    //从Intent中获取设备的BluetoothDevice对象
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    devicesSet.add(device);
//
//                }
//            }
//        };
    }

    private BroadcastReceiver mReceiver;


    /**
     * 判断当前设备是否支持蓝牙
     *
     * @return
     */
    public static boolean isSupportBluetooth() {
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
    public static boolean getBluetoothStatus() {
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
    public static void turnOnBluetooth(Activity activity, int requestCode) {
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 关闭蓝牙
     */
    public static void turnOffBluetooth() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }


    public static Set<BluetoothDevice> getConnetedDevices() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            return mBluetoothAdapter.getBondedDevices();
        }
        return null;
    }


    public static void startDiscovery() {
        mBluetoothAdapter.startDiscovery();
    }

    public static void cancelDiscovery() {
        mBluetoothAdapter.cancelDiscovery();
    }

    @Deprecated
    public static void setDiscoverableTimeout(int timeout) {

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

    public static void closeDiscoverableTimeout() {
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