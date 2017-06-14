package com.jedi.wolf_and_hunter.myObj;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import com.jedi.wolf_and_hunter.utils.BluetoothController;

/**
 * Created by Administrator on 2017/6/14.
 */

public class MyBluetoothDevice  {
    BluetoothDevice device;
    boolean isRoomOwner=false;
    public MyBluetoothDevice(@NonNull BluetoothDevice device, boolean isRoomOwner){
        this.device=device;
        this.isRoomOwner=isRoomOwner;
    }
}
