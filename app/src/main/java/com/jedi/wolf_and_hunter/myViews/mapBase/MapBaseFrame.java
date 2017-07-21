package com.jedi.wolf_and_hunter.myViews.mapBase;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.engine.GameMainEngine;
import com.jedi.wolf_and_hunter.myViews.SightView;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.myViews.landform.Landform;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myViews.rocker.LeftRocker;
import com.jedi.wolf_and_hunter.myViews.rocker.RightRocker;
import com.jedi.wolf_and_hunter.myViews.tempView.InjuryView;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import java.util.Vector;

/**
 * Created by Administrator on 2017/3/15.
 */

public class MapBaseFrame extends FrameLayout {
    private int lastX;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private int lastY;
    public SightView mySight;
    public BaseCharacterView myCharacter;
    public LeftRocker leftRocker;
    public RightRocker rightRocker;
    private LayoutParams mLayoutParams;

    public  Landform[][] landformses;
    public int mapWidth;
    public int mapHeight;


    public MapBaseFrame(@NonNull Context context,int mapWidth,int mapHeight) {
        super(context);
        this.mapWidth=mapWidth;
        this.mapHeight=mapHeight;
        mLayoutParams=new LayoutParams(mapWidth,mapHeight);

        this.setLayoutParams(mLayoutParams);

    }

    public MapBaseFrame(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);



    }

    public MapBaseFrame(@NonNull Context context, @Nullable AttributeSet attrs, GameMainEngine.GameHandler gameHandler) {
        super(context, attrs);



    }

    public MapBaseFrame(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DisplayMetrics dm=ViewUtils.getWindowsDisplayMetrics();
        if(mapWidth==0)
            mapWidth=dm.widthPixels;
        if(mapHeight==0)
            mapHeight=dm.heightPixels;
        int width=measureDimension(mapWidth,widthMeasureSpec);
        int height=measureDimension(mapHeight,heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    public int measureDimension(int defaultSize, int measureSpec){
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if(specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = defaultSize;   //UNSPECIFIED
        }
        return result;
    }

    public boolean onTouchEvent(MotionEvent event) {


        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        int offX ;
        int offY ;

        switch(event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                lastX = x;
                lastY = y;

                break;

            case MotionEvent.ACTION_UP:
                lastX = 0;
                lastY = 0;
                offX = 0;
                offY = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if(leftRocker==null||rightRocker==null)
                    break;
                if (leftRocker.isHoldingRocker == false && (rightRocker==null||rightRocker.isHoldingRocker == false)) {
                    int[] movementArr = new int[4];
                    //计算移动的距离
                    offX = x - lastX;
                    offY = y - lastY;

                    movementArr = new ViewUtils().reviseTwoRectViewMovement(this, (View) getParent(), offX, offY);
                    int width=mLayoutParams.width;
                    mLayoutParams.leftMargin=movementArr[0];
                    mLayoutParams.topMargin=movementArr[1];
                    GameBaseAreaActivity.virtualWindow.targetLeft=-movementArr[0];
                    GameBaseAreaActivity.virtualWindow.targetTop=-movementArr[1];
                    this.setLayoutParams(mLayoutParams);
                }
        }
        return true;
    }


}
