package com.jedi.wolf_and_hunter.myObj;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/19.
 */

public  class  PlayerInfo implements Serializable {

    public boolean isAvailable=true;
    public int playerID;
    public int characterType= BaseCharacterView.CHARACTER_TYPE_HUNTER;
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
