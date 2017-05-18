package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/3/29.
 */

public class LeftRocker extends JRocker  {



    public LeftRocker(Context context, AttributeSet attrs) {

        super(context, attrs);
//        actionButtonLeft=(padRadius+rockerRadius)*2-actionButtonsWidth;
//        actionButtonTop=0;

        FrameLayout.LayoutParams params=( FrameLayout.LayoutParams)getLayoutParams();
        params.gravity= Gravity.TOP | Gravity.LEFT;
        setLayoutParams(params);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(event.getAction())
        {

            case MotionEvent.ACTION_DOWN:

//                if(MyMathsUtils.isInRECT(actionButtonLeft,actionButtonTop
//                        ,actionButtonLeft+actionButtonsWidth,actionButtonTop+actionButtonsWidth
//                        ,new Point(x,y))){
//                    readyToFire=true;
//                }
//                else
                    if(MyMathsUtils.isInCircle(rockerCircleCenter,rockerRadius,new Point(x,y))) {
                    isHoldingRocker = true;
                    startCenterX=x;
                    startCenterY=y;
                }
                break;
            case MotionEvent.ACTION_UP:
//                if(readyToFire){
//                    GameBaseAreaActivity.myCharacter.judgeAttack();
//                    readyToFire=false;
//                }
                isHoldingRocker=false;
                distance=0;
                rockerCircleCenter.set(padCircleCenter.x,padCircleCenter.y);
                synchronized (bindingCharacter) {
                    bindingCharacter.needMove = false;
                    bindingCharacter.offX = 0;
                    bindingCharacter.offY = 0;
                    startCenterX = padCircleCenter.x;
                    startCenterY = padCircleCenter.y;
                    invalidate();
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                if(isHoldingRocker==false) {
                    break;
                }
                int relateX=x-startCenterX;
                int relateY=y-startCenterY;
                Point newPosition=new Point(padCircleCenter.x+relateX,padCircleCenter.y+relateY);
                rockerCircleCenter= new ViewUtils().revisePointInCircleViewMovement(padCircleCenter,padRadius,newPosition);
                distance= MyMathsUtils.getDistance(rockerCircleCenter,padCircleCenter);
                synchronized (bindingCharacter) {
                    bindingCharacter.offX = rockerCircleCenter.x - padCircleCenter.x;
                    bindingCharacter.needMove = true;
                    bindingCharacter.offY = rockerCircleCenter.y - padCircleCenter.y;
                    bindingCharacter.needMove = true;
                }
                invalidate();

        }

        return true;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawBitmap(fireBitmap,actionButtonLeft,actionButtonTop,null);
//        if(bindingCharacter.attackCount<bindingCharacter.maxAttackCount&&bindingCharacter.reloadAttackStartTime!=0){
//            float sweepAngle=360*((new Date().getTime()-bindingCharacter.reloadAttackStartTime)/bindingCharacter.reloadAttackNeedTime);
//            if (sweepAngle>360)
//                sweepAngle=359;
//            canvas.drawArc(new RectF(actionButtonLeft,actionButtonTop,actionButtonLeft+fireBitmap.getWidth(),actionButtonTop+fireBitmap.getHeight()),0,sweepAngle,true,normalPaint);
//        }
//        canvas.drawText(new Integer(bindingCharacter.attackCount).toString(),actionButtonLeft+5,baselineY,normalPaint);
    }

}
