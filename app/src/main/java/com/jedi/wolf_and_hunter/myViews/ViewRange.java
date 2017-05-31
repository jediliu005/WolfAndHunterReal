package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;

/**
 * Created by Administrator on 2017/4/26.
 */

public class ViewRange extends View {
    public volatile int centerX, centerY;
//    public int nowLeft;
//    public int nowTop;
//    public int nowRight;
//    public int nowBottom;
//    public float nowViewAngle;
    public boolean isHidden;
//    public FrameLayout.LayoutParams layoutParams;
    BaseCharacterView bindingCharacter;
    Paint borderPaint;
    Paint transparentPaint;
    public ViewRange(Context context) {
        super(context);
        if(GameBaseAreaActivity.myCharacter!=null)
            bindingCharacter=GameBaseAreaActivity.myCharacter;
        init();
    }
    public ViewRange(Context context, BaseCharacterView character) {
        super(context);
        bindingCharacter=character;
        character.viewRange=this;
        init();
    }

    public ViewRange(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(GameBaseAreaActivity.myCharacter!=null)
            bindingCharacter=GameBaseAreaActivity.myCharacter;
        init();
    }

    public ViewRange(Context context, @Nullable AttributeSet attrs, BaseCharacterView character) {
        super(context, attrs);
        bindingCharacter=character;
        init();
    }

    private void init(){

        if(bindingCharacter!=null) {

            centerX=bindingCharacter.centerX;
            centerY=bindingCharacter.centerY;
//            nowLeft=centerX-bindingCharacter.nowViewRadius;
//            nowRight=centerX+bindingCharacter.nowViewRadius;
//            nowTop=centerY-bindingCharacter.nowViewRadius;
//            nowBottom=centerY+bindingCharacter.nowViewRadius;
            borderPaint = new Paint();
//            DashPathEffect pathEffect=new DashPathEffect(new float[]{10,10},0);
//            borderPaint.setPathEffect(pathEffect);
            borderPaint.setColor(Color.YELLOW);
            borderPaint.setAlpha(50);
            borderPaint.setStyle(Paint.Style.FILL);
            borderPaint.setStrokeWidth(5);
            borderPaint.setAntiAlias(true);

            transparentPaint=new Paint();
            transparentPaint.setAlpha(0);
            transparentPaint.setStyle(Paint.Style.FILL);
            transparentPaint.setStrokeWidth(5);
//            nowViewAngle=bindingCharacter.nowViewAngle;
        }
        if(this.getLayoutParams()==null){
            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin=bindingCharacter.centerX-bindingCharacter.nowViewRadius;
            layoutParams.topMargin=bindingCharacter.centerY-bindingCharacter.nowViewRadius;
            this.setLayoutParams(layoutParams);
//            this.layoutParams=layoutParams;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(2*bindingCharacter.nowViewRadius,2*bindingCharacter.nowViewRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawARGB(50,50,50,50);
        float startAngle=bindingCharacter.nowFacingAngle-bindingCharacter.nowViewAngle/2;
        //因为draw和layout不是一次完成的，直接用nowViewRadius会造成先画好再layout的闪动问题，这里迁就layout
        int drawRadius= this.getRight()-bindingCharacter.centerX;

        if(isHidden){
            canvas.drawArc(new RectF(0,0,2*drawRadius,2*drawRadius),startAngle,bindingCharacter.nowViewAngle,true,transparentPaint);
        }else{
            canvas.drawArc(new RectF(0,0,2*drawRadius,2*drawRadius),startAngle,bindingCharacter.nowViewAngle,true,borderPaint);
        }


    }
}
