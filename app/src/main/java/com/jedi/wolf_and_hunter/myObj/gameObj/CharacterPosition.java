package com.jedi.wolf_and_hunter.myObj.gameObj;

import android.graphics.Point;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Administrator on 2017/7/14.
 */

public class CharacterPosition {
    public Point position;
    public BaseCharacterView character;
    public long createTime;
    public long lastTime;

    public CharacterPosition(Point position, BaseCharacterView character, long createTime, long lastTime) {
        this.position = position;
        this.character = character;
        this.createTime = createTime;
        this.lastTime = lastTime;
    }
    public static void removeOverdue(Vector<CharacterPosition> characterPositions){
        long nowTime=new Date().getTime();
        Iterator<CharacterPosition> iterator=characterPositions.iterator();
        if(iterator.hasNext()){
            CharacterPosition characterPosition=iterator.next();
            if(nowTime-characterPosition.createTime>characterPosition.lastTime){
                iterator.remove();
            }
        }
    }
}
