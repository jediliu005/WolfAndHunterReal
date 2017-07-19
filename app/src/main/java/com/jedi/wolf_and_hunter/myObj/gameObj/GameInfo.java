package com.jedi.wolf_and_hunter.myObj.gameObj;

import android.graphics.Point;

import com.jedi.wolf_and_hunter.myViews.SightView;
import com.jedi.wolf_and_hunter.myViews.Trajectory;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Administrator on 2017/7/11.
 */

public class GameInfo implements Serializable{

    public final static int CONTROL_MODE_NORMAL = 0;
    public final static int CONTROL_MODE_MASTER = 1;
    public int controlMode = CONTROL_MODE_NORMAL;
    public volatile boolean isStop = false;
    public PlayerInfo myPlayerInfo;
    public SightView mySight;
    public Vector<PlayerInfo> playerInfos;
    public static Vector<Trajectory> allTrajectories;
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

    public void dealNeedToBeKilled() {

        for (Map<BaseCharacterView, BaseCharacterView> attackMap : beAttackedList) {
            Set<Map.Entry<BaseCharacterView, BaseCharacterView>> entrySet = attackMap.entrySet();
            for (Map.Entry<BaseCharacterView, BaseCharacterView> entry : entrySet) {
                BaseCharacterView attackCharacter = entry.getKey();
                BaseCharacterView beAttackedCharacter = entry.getValue();
                if (beAttackedCharacter.isInvincible)
                    break;
                if (beAttackedCharacter.isDead)
                    break;
                int relateX = beAttackedCharacter.centerX - attackCharacter.centerX;
                int relateY = beAttackedCharacter.centerY - attackCharacter.centerY;
                if(relateX==0&&relateY==0) {
                    beAttackedCharacter.isDead = true;
                    beAttackedCharacter.dieCount++;
                    beAttackedCharacter.deadTime = new Date().getTime();
                    attackCharacter.killCount++;
                    continue;
                }
                float angleBetweenXAxus = 0;
                try {
                    angleBetweenXAxus = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                float relateFacingAngle = Math.abs(beAttackedCharacter.nowFacingAngle - angleBetweenXAxus);

                if (relateFacingAngle < 90 || relateFacingAngle > 270) {//背击
                    beAttackedCharacter.nowHealthPoint -= 2;
                } else {
                    beAttackedCharacter.nowHealthPoint -= 1;
                }
                if (beAttackedCharacter.nowHealthPoint <= 0) {
                    beAttackedCharacter.isDead = true;
                    beAttackedCharacter.dieCount++;
                    beAttackedCharacter.deadTime = new Date().getTime();
                    attackCharacter.killCount++;
                } else {
                    if (beAttackedCharacter.knockedAwayThread == null || beAttackedCharacter.knockedAwayThread.getState() == Thread.State.TERMINATED) {
                        double cosAlpha = Math.cos(Math.toRadians(attackCharacter.nowFacingAngle));
                        double offX = cosAlpha * attackCharacter.nowKnockAwayStrength;

                        double offY = Math.sqrt(attackCharacter.nowKnockAwayStrength * attackCharacter.nowKnockAwayStrength - offX * offX);
                        if (attackCharacter.nowFacingAngle >= 180)
                            offY = -offY;
                        double endX = offX + beAttackedCharacter.centerX;
                        double endY = offY + beAttackedCharacter.centerY;
//        Point fromPoint = new Point(centerX, centerY);
                        Point toPoint = new Point((int) endX, (int) endY);
                        beAttackedCharacter.startKnockedAwayThread(toPoint);
                    }
                }
            }

        }

        beAttackedList.clear();
    }
}
