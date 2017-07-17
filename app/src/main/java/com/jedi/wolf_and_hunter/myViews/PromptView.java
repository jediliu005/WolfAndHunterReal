package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.gameObj.CharacterPosition;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;

import java.util.Date;
import java.util.Iterator;


/**
 * Created by Administrator on 2017/4/26.
 */

public class PromptView extends View {
    public int centerX, centerY;
    public FrameLayout.LayoutParams layoutParams;
    BaseCharacterView bindingCharacter;
    int redArrowSize=100;
    int yellowArrowSize=85;
    int greenArrowSize=70;
    int redRange=300;
    int yellowRange=1000;
    int greenRange=1500;
    public int viewSize=500;
    Bitmap redArrowBitmap;
    Bitmap yellowArrowBitmap;
    Bitmap greenArrowBitmap;
    Paint transparentPaint;


    public PromptView(Context context) {
        super(context);
        if (GameBaseAreaActivity.myCharacter != null)
            bindingCharacter = GameBaseAreaActivity.myCharacter;
        init();
    }

    public PromptView(Context context, BaseCharacterView character) {
        super(context);
        bindingCharacter = character;
        init();
    }

    public PromptView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (GameBaseAreaActivity.myCharacter != null)
            bindingCharacter = GameBaseAreaActivity.myCharacter;
        init();
    }

    public PromptView(Context context, @Nullable AttributeSet attrs, BaseCharacterView character) {
        super(context, attrs);
        bindingCharacter = character;
        init();
    }


    private void init() {

        if (bindingCharacter != null) {
            centerX = bindingCharacter.centerX;
            centerY = bindingCharacter.centerY;
            DashPathEffect pathEffect = new DashPathEffect(new float[]{10, 10}, 0);


            transparentPaint = new Paint();
            transparentPaint.setAlpha(0);
            transparentPaint.setStyle(Paint.Style.FILL);
            transparentPaint.setStrokeWidth(5);
        }
        layoutParams=(FrameLayout.LayoutParams) this.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.leftMargin = bindingCharacter.centerX - bindingCharacter.nowAttackRadius;
        layoutParams.topMargin = bindingCharacter.centerY - bindingCharacter.nowAttackRadius;
        this.setLayoutParams(layoutParams);

        redArrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_red);
        yellowArrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_yellow);
        greenArrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_green);
        Matrix matrixForRed = new Matrix();
        matrixForRed.postScale((float) (redArrowSize * 0.8) / redArrowBitmap.getWidth(), (float) (redArrowSize * 0.8) / redArrowBitmap.getHeight());
        Matrix matrixForYellow = new Matrix();
        matrixForYellow.postScale((float) (yellowArrowSize * 0.8) / yellowArrowBitmap.getWidth(), (float) (yellowArrowSize * 0.8) / yellowArrowBitmap.getHeight());
        Matrix matrixForGreen = new Matrix();
        matrixForGreen.postScale((float) (greenArrowSize * 0.8) / greenArrowBitmap.getWidth(), (float) (greenArrowSize * 0.8) / greenArrowBitmap.getHeight());

        redArrowBitmap =  Bitmap.createBitmap(redArrowBitmap, 0, 0, redArrowBitmap.getWidth(), redArrowBitmap.getHeight(), matrixForRed, true);
        yellowArrowBitmap = Bitmap.createBitmap(yellowArrowBitmap, 0, 0, yellowArrowBitmap.getWidth(), yellowArrowBitmap.getHeight(), matrixForYellow, true);
        greenArrowBitmap = Bitmap.createBitmap(greenArrowBitmap, 0, 0, greenArrowBitmap.getWidth(), greenArrowBitmap.getHeight(), matrixForGreen, true);
    
    
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(viewSize, viewSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(bindingCharacter==null||bindingCharacter.enemiesPositionSet==null)
            return;
//        if(new Date().getTime()-bindingCharacter.lastSmellTime>5000){
//            bindingCharacter.enemiesPositionSet.clear();
//            return;
//        }
        CharacterPosition.removeOverdue(bindingCharacter.enemiesPositionSet);
        Iterator<CharacterPosition> iterator= bindingCharacter.enemiesPositionSet.iterator();
        while(iterator.hasNext()){
            CharacterPosition characterPosition=iterator.next();
            Point position=characterPosition.position;
            canvas.save();
            int relateX=position.x-bindingCharacter.centerX;
            int relateY=position.y-bindingCharacter.centerY;
            double distance=Math.sqrt(relateX*relateX+relateY*relateY);
            float angle=MyMathsUtils.getAngleBetweenXAxus(relateX,relateY);
            canvas.rotate(angle,viewSize/2,viewSize/2);
            if(distance>greenRange){
                canvas.drawBitmap(greenArrowBitmap,viewSize-greenArrowBitmap.getWidth(),(viewSize-greenArrowBitmap.getHeight())/2,null);
            }else if(distance>yellowRange){
                canvas.drawBitmap(yellowArrowBitmap,viewSize-yellowArrowBitmap.getWidth(),(viewSize-yellowArrowBitmap.getHeight())/2,null);
            }else if(distance>redRange){
                canvas.drawBitmap(redArrowBitmap,viewSize-redArrowBitmap.getWidth(),(viewSize-redArrowBitmap.getHeight())/2,null);
            }

            canvas.restore();
        }



    }
}
