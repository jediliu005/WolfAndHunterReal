package com.jedi.wolf_and_hunter.myViews.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.util.AttributeSet;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.Trajectory;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/4/21.
 */

public class NormalHunter extends BaseCharacterView {
    private static final String TAG = "NormalHunter";
    private final static String characterName="普通猎人";
    private final static int defaultMaxAttackCount=2;
    private final static int reloadAttackNeedTime=3000;
    public  final static int defaultAttackRadius=700;
    public  final static int defaultViewRadius=500;
    public  final static int defaultSpeed=10;
    public  final static int defaultAngleChangSpeed=2;
    private int bolletWidth=1;
    boolean isReloading=false;
    static MediaPlayer fireMediaPlayer;
    static MediaPlayer reloadBolletMediaPlayer;
    //下面一行控制bitmap是否自适应分辨率，不强制设flase可能出现图片分辨率和draw分辨率不一致
    BitmapFactory.Options option=new BitmapFactory.Options();
    {option.inScaled=false;}
    public static final int defaultHiddenLevel=BaseCharacterView.HIDDEN_LEVEL_NO_HIDDEN;

    public NormalHunter(Context context) {
        super(context);
        initNormalHunter();
    }
    /**
     * myCharacter最好用这个构造方法
     * @param context
     * @param virtualWindow
     */
    public NormalHunter(Context context, MyVirtualWindow virtualWindow) {
        super(context);
        this.virtualWindow=virtualWindow;
        initNormalHunter();
    }

    public NormalHunter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initNormalHunter();
    }

    public void initNormalHunter(){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.word,option);
        characterPic=Bitmap.createBitmap(bitmap,24,5,76,76,matrixForCP,true);
        reloadBolletMediaPlayer=MediaPlayer.create(getContext(),R.raw.reload_bollet);
        super.reloadAttackNeedTime=reloadAttackNeedTime;
        attackCount=defaultMaxAttackCount;
        maxAttackCount=defaultMaxAttackCount;
        nowAttackRadius=defaultAttackRadius;
        nowViewRadius=defaultViewRadius;
        nowSpeed=defaultSpeed;
        super.angleChangSpeed=defaultAngleChangSpeed;
        if(this.virtualWindow==null)
            this.virtualWindow=GameBaseAreaActivity.virtualWindow;
    }

    @Override
    public void reloadAttackCount(){
        if(isReloading==true)
            return;
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        isReloading=true;
                        reloadBolletMediaPlayer.start();
                        Date now=new Date();
                        reloadAttackStartTime=now.getTime();//这参数用于攻击按钮饼图显示
                        try {
                            Thread.sleep(reloadAttackNeedTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        attackCount=maxAttackCount;

                        reloadAttackStartTime=0;
                        isReloading=false;
                    }
                }

        ).start();

    }
    @Override
    public void judgeAttack() {
        if(attackCount<=0||isReloading||isDead){
            return;
        }
        super.judgeAttack();
        fireMediaPlayer=MediaPlayer.create(getContext(),R.raw.gun_fire);
        fireMediaPlayer.start();
        attackCount-=1;
        synchronized (GameBaseAreaActivity.allCharacters) {
            for (BaseCharacterView targetCharacter : GameBaseAreaActivity.allCharacters) {

                    if (this == targetCharacter||targetCharacter.getTeamID()==this.getTeamID())
                        continue;
                    double distance = MyMathsUtils.getDistance(new Point(centerX, centerY), new Point(targetCharacter.centerX, targetCharacter.centerY));
                    if (distance > attackRange.nowAttackRadius)
                        continue;


                    int targetCharacterSize = targetCharacter.characterBodySize;
                    int relateX = targetCharacter.centerX - centerX;
                    int relateY = targetCharacter.centerY - centerY;

                    float angleBetweenXAxus = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);
                    float relateAngle = Math.abs(angleBetweenXAxus - nowFacingAngle);
                    if (relateAngle > 90 && relateAngle < 270) {//这类角度表示目标在此角色身后
                        continue;
                    }
                    double pointToLineDistance = 0;
                    if (nowFacingAngle == 0 || nowFacingAngle == 180)
                        pointToLineDistance = relateY;
                    else if (nowFacingAngle == 90 || nowFacingAngle == 270)
                        pointToLineDistance = relateX;
                    else {
                        double k = Math.tan(Math.toRadians(nowFacingAngle));
                        pointToLineDistance = MyMathsUtils.getPointToLineDistance(new Point(relateX, relateY), k, 0);

                    }
                if (pointToLineDistance <= bolletWidth / 2 + targetCharacterSize) {
                    targetCharacter.isDead = true;
                    this.killCount++;
                    targetCharacter.dieCount++;
                    targetCharacter.deadTime = new Date().getTime();
                }

            }
        }
        double cosAlpha=Math.cos(Math.toRadians(nowFacingAngle));
//        double cosAlpha=Math.cos(Math.toRadians(30));
        double endX=cosAlpha*nowAttackRadius;

        double endY=Math.sqrt(nowAttackRadius*nowAttackRadius-endX*endX);
        if(nowFacingAngle>=180)
            endY=-endY;
        endX=endX+centerX;
        endY=endY+centerY;
        Point fromPoint=new Point(centerX,centerY);
        Point toPoint=new Point((int)endX,(int)endY);
        Trajectory trajectory=new Trajectory(getContext(),fromPoint,toPoint,this);
        Message msg=gameHandler.obtainMessage(GameBaseAreaActivity.GameHandler.ADD_TRAJECTORY,trajectory);
        gameHandler.sendMessage(msg);

    }

}
