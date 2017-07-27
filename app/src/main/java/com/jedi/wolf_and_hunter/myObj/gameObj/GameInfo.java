package com.jedi.wolf_and_hunter.myObj.gameObj;

import com.jedi.wolf_and_hunter.myViews.SightView;
import com.jedi.wolf_and_hunter.myViews.tempView.InjuryView;
import com.jedi.wolf_and_hunter.myViews.tempView.Trajectory;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by Administrator on 2017/7/11.
 */

public class GameInfo implements Serializable{

    public final static int CONTROL_MODE_NORMAL = 0;

    public int controlMode = CONTROL_MODE_NORMAL;
    public volatile boolean isStop = false;
    public PlayerInfo myPlayerInfo;
    public Vector<PlayerInfo> playerInfos;
    public  Vector<Trajectory> allTrajectories=new Vector<Trajectory>();
    public Vector<InjuryView> injuryViews=new Vector<InjuryView>();
    public int tallGrasslandDensity=50;
    public List<HashMap<BaseCharacterView, BaseCharacterView>> beAttackedList;
    public String playMode="single";
    public String serverMac;
    public volatile ArrayList<BaseCharacterView> allCharacters;
    public int targetKillCount = 10;
    public int mapWidth = 2500;
    public int mapHeight = 2500;

    public GameInfo() {
        isStop = false;
        beAttackedList = new ArrayList<HashMap<BaseCharacterView, BaseCharacterView>>();
        playerInfos = new Vector<PlayerInfo>();
        allCharacters = new ArrayList<BaseCharacterView>();
    }


}
