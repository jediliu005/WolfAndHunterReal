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

public class RightRocker extends JRocker  {





    public RightRocker(Context context, AttributeSet attrs) {
        super(context, attrs);
//        actionButtonLeft=0;
//        actionButtonTop=0;
        FrameLayout.LayoutParams params=( FrameLayout.LayoutParams)getLayoutParams();
        params.gravity= Gravity.TOP | Gravity.RIGHT;
    }

    public void reactUsingTouchPadMode(MotionEvent event) {
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

                }
                break;
            case MotionEvent.ACTION_UP:
                isHoldingRocker=false;
                distance=0;
                rockerCircleCenter.set(padCircleCenter.x,padCircleCenter.y);
                synchronized (bindingCharacter.getSight()) {
                    bindingCharacter.getSight().needMove = false;
                    bindingCharacter.getSight().offX = 0;
                    bindingCharacter.getSight().offY = 0;
                }

                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if(isHoldingRocker==false) {
                    break;
                }

                int relateX=x-padCircleCenter.x;
                int relateY=y-padCircleCenter.y;


                Point newPosition=new Point(padCircleCenter.x+relateX,padCircleCenter.y+relateY);
                rockerCircleCenter= new ViewUtils().revisePointInCircleViewMovement(padCircleCenter,padRadius,newPosition);
                synchronized (bindingCharacter.getSight()) {
                    bindingCharacter.getSight().offX = rockerCircleCenter.x - padCircleCenter.x;
                    bindingCharacter.getSight().needMove = true;
                    bindingCharacter.getSight().offY = rockerCircleCenter.y - padCircleCenter.y;
                    bindingCharacter.getSight().needMove = true;
                }
                distance= MyMathsUtils.getDistance(rockerCircleCenter,padCircleCenter);
                invalidate();
        }
    }


    public void reactUsingRockerMode(MotionEvent event) {
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
                synchronized (bindingCharacter.getSight()) {
                    bindingCharacter.getSight().needMove = false;
                    bindingCharacter.getSight().offX = 0;
                    bindingCharacter.getSight().offY = 0;
                }
                startCenterX=padCircleCenter.x;
                startCenterY=padCircleCenter.y;
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if(isHoldingRocker==false) {
                    break;
                }

                int relateX=x-startCenterX;
                int relateY=y-startCenterY;


                Point newPosition=new Point(padCircleCenter.x+relateX,padCircleCenter.y+relateY);
                rockerCircleCenter= new ViewUtils().revisePointInCircleViewMovement(padCircleCenter,padRadius,newPosition);
                synchronized (bindingCharacter.getSight()) {
                    bindingCharacter.getSight().offX = rockerCircleCenter.x - padCircleCenter.x;
                    bindingCharacter.getSight().needMove = true;
                    bindingCharacter.getSight().offY = rockerCircleCenter.y - padCircleCenter.y;
                    bindingCharacter.getSight().needMove = true;
                }
                distance= MyMathsUtils.getDistance(rockerCircleCenter,padCircleCenter);
                invalidate();
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        reactUsingTouchPadMode(event);
//        reactUsingRockerMode(event);

        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawBitmap(fireBitmap,0,0,null);
//        if(bindingCharacter.attackCount<bindingCharacter.maxAttackCount&&bindingCharacter.reloadAttackStartTime!=0){
//            float sweepAngle=360*((new Date().getTime()-bindingCharacter.reloadAttackStartTime)/bindingCharacter.reloadAttackNeedTime);
//            if (sweepAngle>360)
//                sweepAngle=359;
//            canvas.drawArc(new RectF(actionButtonLeft,actionButtonTop,actionButtonLeft+fireBitmap.getWidth(),actionButtonTop+fireBitmap.getHeight()),0,sweepAngle,true,normalPaint);
//        }
//        canvas.drawText(new Integer(bindingCharacter.attackCount).toString(),5,baselineY,normalPaint);

    }
}
