package com.jedi.wolf_and_hunter.myObj;

import com.jedi.wolf_and_hunter.myViews.SightView;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/11.
 */

public class GameInfo {

    public final static int CONTROL_MODE_NORMAL = 0;
    public final static int CONTROL_MODE_MASTER = 1;
    public int controlMode = CONTROL_MODE_NORMAL;
    public volatile boolean isStop = false;
    public PlayerInfo myPlayerInfo;
    public SightView mySight;
    public ArrayList<PlayerInfo> playerInfos;
    public Map<BaseCharacterView,BaseCharacterView> needToBeKilledMap;
    public String playMode;
    public String serverMac;
    public volatile ArrayList<BaseCharacterView> allCharacters;
    public int targetKillCount = 10;
    public int mapWidth = 3000;
    public int mapHeight = 3000;
    public GameInfo(){
        isStop=false;
        needToBeKilledMap=new HashMap<BaseCharacterView,BaseCharacterView>();
        playerInfos =new ArrayList<PlayerInfo>();
        allCharacters=new ArrayList<BaseCharacterView>();
    }
    public void dealNeedToBeKilled(){

    }
}
