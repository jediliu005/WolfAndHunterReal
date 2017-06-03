package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

/**
 * Created by Administrator on 2017/4/26.
 */

public class SmellView extends View {
//    public int nowAttackRadius;
    public int centerX, centerY;
//    public int nowLeft;
//    public int nowTop;
//    public int nowRight;
//    public int nowBottom;
    public boolean isHidden;
    public FrameLayout.LayoutParams layoutParams;
    BaseCharacterView bindingCharacter;
    Paint borderPaint;
    Paint transparentPaint;
    public SmellView(Context context) {
        super(context);
        if(GameBaseAreaActivity.myCharacter!=null)
            bindingCharacter=GameBaseAreaActivity.myCharacter;
        init();
    }
    public SmellView(Context context, BaseCharacterView character) {
        super(context);
        bindingCharacter=character;

        init();
    }

    public SmellView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(GameBaseAreaActivity.myCharacter!=null)
            bindingCharacter=GameBaseAreaActivity.myCharacter;
        init();
    }

    public SmellView(Context context, @Nullable AttributeSet attrs, BaseCharacterView character) {
        super(context, attrs);
        bindingCharacter=character;
        init();
    }

    private void init(){

        if(bindingCharacter!=null) {
//            nowAttackRadius = bindingCharacter.nowAttackRadius;
            centerX=bindingCharacter.centerX;
            centerY=bindingCharacter.centerY;
//            nowLeft=centerX-nowAttackRadius;
//            nowRight=centerX+nowAttackRadius;
//            nowTop=centerY-nowAttackRadius;
//            nowBottom=centerY+nowAttackRadius;
            DashPathEffect pathEffect=new DashPathEffect(new float[]{10,10},0);
            borderPaint = new Paint();
            borderPaint.setPathEffect(pathEffect);
            borderPaint.setColor(Color.RED);
            borderPaint.setAlpha(70);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(5);
            borderPaint.setAntiAlias(true);

            transparentPaint=new Paint();
            transparentPaint.setAlpha(0);
            transparentPaint.setStyle(Paint.Style.FILL);
            transparentPaint.setStrokeWidth(5);
        }
        if(this.getLayoutParams()==null){
            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin=bindingCharacter.centerX-bindingCharacter.nowAttackRadius;
            layoutParams.topMargin=bindingCharacter.centerY-bindingCharacter.nowAttackRadius;
            this.setLayoutParams(layoutParams);
            this.layoutParams=layoutParams;
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(2*bindingCharacter.nowAttackRadius,2*bindingCharacter.nowAttackRadius);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int nowAttackRadius=bindingCharacter.nowAttackRadius;

        double cosAlpha=Math.cos(Math.toRadians(bindingCharacter.nowFacingAngle));
        double endX=cosAlpha*nowAttackRadius;

        double endY=Math.sqrt(nowAttackRadius*nowAttackRadius-endX*endX);
        if(bindingCharacter.nowFacingAngle>=180)
            endY=-endY;
        endX=endX+nowAttackRadius;
        endY=endY+nowAttackRadius;
        if(isHidden||bindingCharacter.isReloadingAttack){
//            canvas.drawCircle(nowAttackRadius,nowAttackRadius,nowAttackRadius,transparentPaint);
//            canvas.drawLine(nowAttackRadius,nowAttackRadius,(int)endX,(int)endY,transparentPaint);
        }else{
            canvas.drawCircle(nowAttackRadius,nowAttackRadius,nowAttackRadius,borderPaint);
            canvas.drawLine(nowAttackRadius,nowAttackRadius,(int)endX,(int)endY,borderPaint);
        }
    }
}
