package com.jedi.wolf_and_hunter.myObj;

import android.bluetooth.BluetoothDevice;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/19.
 */

public class PlayerInfo implements Serializable {
    public boolean isOtherOnlinePlayer = false;
    public String mac;
    public boolean isAvailable = true;
    public int playerID;
    public int characterType = BaseCharacterView.CHARACTER_TYPE_HUNTER;
    public int teamID;
    public int nowCenterX;
    public int nowCenterY;
    public int nowFacingAngle;
    public boolean judgeingAttack = false;
    public boolean isDead = false;
    public boolean isServer;
    public int jumpToX = -99999;
    public int jumpToY = -99999;
    public int characterBodySize;
    public int attackCount;
    public int nowAttackRadius = 600;
    public int nowViewRadius = 500;
    public int nowForceViewRadius = 200;
    public int nowSpeed = 10;


    public PlayerInfo(int playerID) {
        this.playerID = playerID;
    }

    public PlayerInfo(boolean isAvailable, int playerID, int characterType, int teamID, String mac,boolean isServer) {
        this.isAvailable = isAvailable;
        this.playerID = playerID;
        this.characterType = characterType;
        this.teamID = teamID;
        this.isOtherOnlinePlayer = true;
        this.mac = mac;
        this.isServer=isServer;
    }

    public PlayerInfo(boolean isAvailable, int playerID, int characterType, int teamID) {
        this.isAvailable = isAvailable;
        this.playerID = playerID;
        this.characterType = characterType;
        this.teamID = teamID;
    }
}
