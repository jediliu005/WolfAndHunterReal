package com.jedi.wolf_and_hunter.myObj;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/19.
 */

public  class  PlayerInfo implements Serializable {
    public static final int CHARACTER_TYPE_NORMAL_HUNTER=0;
    public static final int CHARACTER_TYPE_NORMAL_WOLF=1;
    public boolean isAvailable=true;
    public int playerID;
    public int characterType=CHARACTER_TYPE_NORMAL_HUNTER;
    public int teamID;

    public PlayerInfo(int playerID){
        this.playerID = playerID;
    }

    public PlayerInfo(boolean isAvailable, int playerID, int characterType, int teamID) {
        this.isAvailable = isAvailable;
        this.playerID = playerID;
        this.characterType = characterType;
        teamID = teamID;
    }
}
