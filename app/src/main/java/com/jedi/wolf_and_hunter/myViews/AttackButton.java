package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/10.
 */

public class AttackButton extends View {
    static Bitmap fireBitmap;
    public int buttonSize;
    public Paint normalPaint;
    public TextPaint textPaint;
    public int baselineY;
    public int bitmapLeft;
    public int bitmapTop;
    private long lastTouchTime;
    public BaseCharacterView bindingCharacter;
    public AttackButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public AttackButton(Context context) {
        super(context);
        init();
    }


    public void init(){

        int windowWidth=MyVirtualWindow.getWindowWidth(getContext());
        int windowHeight = MyVirtualWindow.getWindowHeight(getContext());
        buttonSize=(int)(windowWidth / 15);

        normalPaint = new Paint();

        normalPaint.setColor(Color.WHITE);
        normalPaint.setStyle(Paint.Style.FILL);
        normalPaint.setAntiAlias(true);
        Paint.FontMetricsInt fontMetrics = normalPaint.getFontMetricsInt();

        textPaint=new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(buttonSize/2);
        textPaint.setTextAlign(Paint.Align.CENTER);
        baselineY = (buttonSize - fontMetrics.bottom - fontMetrics.top) / 2;

        if(fireBitmap==null) {
            fireBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
            Matrix matrix = new Matrix();
            matrix.postScale((float)(buttonSize*0.8)/ fireBitmap.getWidth(), (float)(buttonSize*0.8) / fireBitmap.getHeight());
            fireBitmap = Bitmap.createBitmap(fireBitmap, 0, 0, fireBitmap.getWidth(), fireBitmap.getHeight(), matrix, true);
        }
        bitmapLeft=(buttonSize-fireBitmap.getWidth())/2;
        bitmapTop=(buttonSize-fireBitmap.getHeight())/2;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(event.getAction())
        {

            case MotionEvent.ACTION_DOWN:
                lastTouchTime=new Date().getTime();

                break;
            case MotionEvent.ACTION_UP:

                GameBaseAreaActivity.myCharacter.judgeAttack();
                lastTouchTime=0;
                break;

            case MotionEvent.ACTION_MOVE:
                if(new Date().getTime()-lastTouchTime>800){
                    try {
                        Method method=bindingCharacter.getClass().getMethod("reloadAttackCount",new Class[0]);
                        method.invoke(bindingCharacter,new Object[0]);
                        lastTouchTime=0;
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    bindingCharacter.reloadAttackCount();
                }

        }

        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int realSize=buttonSize;
        int width=realSize;
        int height=realSize;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(fireBitmap,bitmapLeft,bitmapTop,null);
        if(bindingCharacter==null){
            bindingCharacter=GameBaseAreaActivity.myCharacter;
            invalidate();
            return;
        }

        if(bindingCharacter.attackCount<bindingCharacter.maxAttackCount&&bindingCharacter.reloadAttackStartTime!=0){
            long startTime=bindingCharacter.reloadAttackStartTime;
            float percent=(float) (new Date().getTime()-startTime)/bindingCharacter.reloadAttackNeedTime;
            float sweepAngle=360*percent;
            if (sweepAngle>360)
                sweepAngle=359;
            if(sweepAngle>0)
                Log.i("","");
            canvas.drawArc(new RectF(0,0,buttonSize,buttonSize),0,sweepAngle,true,normalPaint);
        }
        canvas.drawText(new Integer(bindingCharacter.attackCount).toString(),buttonSize/2,baselineY,textPaint);
        invalidate();
    }
}
