package com.jedi.wolf_and_hunter.myViews.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Message;
import android.util.AttributeSet;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.JRocker;
import com.jedi.wolf_and_hunter.myViews.Trajectory;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/4/21.
 */

public class NormalWolf extends BaseCharacterView {
    private static final String TAG = "NormalHunter";
    private final static String characterName="普通狼";
    private final static int defaultMaxAttackCount=3;
    private final static int reloadAttackNeedTime=2000;
    public  final static int defaultAngleChangSpeed=2;
    private int bolletWidth=1;

    boolean isReloading=false;
    static MediaPlayer fireMediaPlayer;
    //下面一行控制bitmap是否自适应分辨率，不强制设flase可能出现图片分辨率和draw分辨率不一致
    BitmapFactory.Options option=new BitmapFactory.Options();
    {option.inScaled=false;}
    public static final int defaultHiddenLevel=BaseCharacterView.HIDDEN_LEVEL_NO_HIDDEN;

    public NormalWolf(Context context) {
        super(context);
        initNormalHunter();
    }
    /**
     * myCharacter最好用这个构造方法
     * @param context
     * @param virtualWindow
     */
    public NormalWolf(Context context, MyVirtualWindow virtualWindow) {
        super(context);
        this.virtualWindow=virtualWindow;
        initNormalHunter();
    }

    public NormalWolf(Context context, AttributeSet attrs) {
        super(context, attrs);
        initNormalHunter();
    }

    public void initNormalHunter(){
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.word,option);
        characterPic=Bitmap.createBitmap(bitmap,24,5,76,76,matrixForCP,true);
        super.reloadAttackNeedTime=reloadAttackNeedTime;
        attackCount=defaultMaxAttackCount;
        maxAttackCount=defaultMaxAttackCount;
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
                        Date now=new Date();
                        reloadAttackStartTime=now.getTime();//这参数用于攻击按钮饼图显示
                        try {
                            Thread.sleep(reloadAttackNeedTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(attackCount<maxAttackCount)
                              attackCount=attackCount++;

                        reloadAttackStartTime=0;
                        isReloading=false;
                    }
                }

        ).start();

    }

    private class AttackThread implements Runnable{

        BaseCharacterView attackCharacter;
        Point jumpToPoint;
        public AttackThread(BaseCharacterView attackCharacter,Point jumpToPoint){
            this.attackCharacter=attackCharacter;
            this.jumpToPoint=jumpToPoint;
        }

        @Override
        public void run() {
            while(isAttacking){
                synchronized (GameBaseAreaActivity.allCharacters) {
                    int nowCenterX=(getLeft()+getRight())/2;
                    int nowCenterY=(getTop()+getBottom())/2;
                    int nowJumpToPointOffX=jumpToPoint.x-nowCenterX;
                    int nowJumpToPointOffY=jumpToPoint.y-nowCenterY;
                    double nowJumpToPointDistance = Math.sqrt(nowJumpToPointOffX * nowJumpToPointOffX + nowJumpToPointOffY * nowJumpToPointOffY);
                    int jumpSpeed = 3*speed;
                    boolean attackSuccess=false;

                    int realOffX = (int) (jumpSpeed * nowJumpToPointOffX / nowJumpToPointDistance);
                    int realOffY = (int) (jumpSpeed * nowJumpToPointOffY / nowJumpToPointDistance);

                    for (BaseCharacterView targetCharacter : GameBaseAreaActivity.allCharacters) {

                        if (attackCharacter == targetCharacter||targetCharacter.getTeamID()==attackCharacter.getTeamID())
                            continue;
                        double distance = MyMathsUtils.getDistance(new Point(centerX, centerY), new Point(targetCharacter.centerX, targetCharacter.centerY));
                        if (distance > jumpSpeed+characterBodySize/2)
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

                        if (pointToLineDistance <= characterBodySize / 2 + targetCharacterSize) {
                            targetCharacter.isDead = true;
                            attackCharacter.killCount++;
                            targetCharacter.dieCount++;
                            targetCharacter.deadTime = new Date().getTime();
                            attackCharacter.jumpToX=targetCharacter.centerX;
                            attackCharacter.jumpToY=targetCharacter.centerY;
                            isAttacking=false;
                            attackSuccess=true;
                            break;
                        }

                    }
                    if(attackSuccess==false){
                        attackCharacter.jumpToX=nowCenterX+realOffX;
                        attackCharacter.jumpToY=nowCenterY+realOffY;
                    }

                }
            }
        }
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
        double cosAlpha=Math.cos(Math.toRadians(nowFacingAngle));
        double endX=cosAlpha*nowAttackRadius;

        double endY=Math.sqrt(nowAttackRadius*nowAttackRadius-endX*endX);
        if(nowFacingAngle>=180)
            endY=-endY;
        endX=endX+centerX;
        endY=endY+centerY;
        Point fromPoint=new Point(centerX,centerY);
        Point toPoint=new Point((int)endX,(int)endY);
        new Thread(new AttackThread(this,toPoint)).start();
//        synchronized (GameBaseAreaActivity.allCharacters) {
//            for (BaseCharacterView targetCharacter : GameBaseAreaActivity.allCharacters) {
//
//                    if (this == targetCharacter||targetCharacter.getTeamID()==this.getTeamID())
//                        continue;
//                    double distance = MyMathsUtils.getDistance(new Point(centerX, centerY), new Point(targetCharacter.centerX, targetCharacter.centerY));
//                    if (distance > attackRange.nowAttackRadius)
//                        continue;
//
//
//                    int targetCharacterSize = targetCharacter.characterBodySize;
//                    int relateX = targetCharacter.centerX - centerX;
//                    int relateY = targetCharacter.centerY - centerY;
//
//                    float angleBetweenXAxus = MyMathsUtils.getAngleBetweenXAxus(relateX, relateY);
//                    float relateAngle = Math.abs(angleBetweenXAxus - nowFacingAngle);
//                    boolean isInFrontOfCharacter = true;
//                    if (relateAngle > 90 && relateAngle < 270)
//                        isInFrontOfCharacter = false;
//                    if (isInFrontOfCharacter == false)
//                        continue;
//                    double pointToLineDistance = 0;
//                    if (nowFacingAngle == 0 || nowFacingAngle == 180)
//                        pointToLineDistance = relateY;
//                    else if (nowFacingAngle == 90 || nowFacingAngle == 270)
//                        pointToLineDistance = relateX;
//                    else {
//                        double k = Math.tan(Math.toRadians(nowFacingAngle));
//                        pointToLineDistance = MyMathsUtils.getPointToLineDistance(new Point(relateX, relateY), k, 0);
//
//                    }
//
//                if (pointToLineDistance <= characterBodySize / 2 + targetCharacterSize) {
//                    targetCharacter.isDead = true;
//                    this.killCount++;
//                    targetCharacter.dieCount++;
//                    targetCharacter.deadTime = new Date().getTime();
//                    this.jumpToX=targetCharacter.centerX;
//                    this.jumpToY=targetCharacter.centerY;
//                    break;
//                }
//
//            }
//
//        }


        Trajectory trajectory=new Trajectory(getContext(),fromPoint,toPoint,this);
        Message msg=gameHandler.obtainMessage(GameBaseAreaActivity.GameHandler.ADD_TRAJECTORY,trajectory);
        gameHandler.sendMessage(msg);

    }

}
