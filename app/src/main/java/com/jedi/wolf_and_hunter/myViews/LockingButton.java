package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.gameObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

/**
 * Created by Administrator on 2017/5/10.
 */

public class LockingButton extends View {
    static Bitmap lockingBitmap;
    public int buttonSize;
    public Paint normalPaint;
    public Paint alphaPaint;
    public Paint backgroundPaint;
    public TextPaint textPaint;
    public int baselineY;
    public int bitmapLeft;
    public int bitmapTop;
    private int lastTouchX=-999;
    private int lastTouchY=-999;
    private boolean isHolding;
    public BaseCharacterView bindingCharacter;

    public LockingButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public LockingButton(Context context) {
        super(context);
        init();
    }

    public void reCreateBitmap() {
        if (GameBaseAreaActivity.myCharacter != null) {
//            if (GameBaseAreaActivity.myCharacter.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER){
            lockingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aim64);
//                }else if (GameBaseAreaActivity.myCharacter.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF){
//                lockingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wolf_attack);
//            }
            Matrix matrix = new Matrix();
            matrix.postScale((float) (buttonSize * 0.8) / lockingBitmap.getWidth(), (float) (buttonSize * 0.8) / lockingBitmap.getHeight());
            lockingBitmap = Bitmap.createBitmap(lockingBitmap, 0, 0, lockingBitmap.getWidth(), lockingBitmap.getHeight(), matrix, true);

        }
    }


    public void init() {

        int windowWidth = MyVirtualWindow.getWindowWidth(getContext());
        int windowHeight = MyVirtualWindow.getWindowHeight(getContext());
        buttonSize = (int) (windowWidth / 10);

        backgroundPaint = new Paint();

        backgroundPaint.setColor(Color.GRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAlpha(120);
        backgroundPaint.setAntiAlias(true);


        normalPaint = new Paint();
        normalPaint.setColor(Color.WHITE);
        normalPaint.setStyle(Paint.Style.FILL);
        normalPaint.setAntiAlias(true);
        
        alphaPaint = new Paint();
        alphaPaint.setAlpha(100);
        alphaPaint.setStyle(Paint.Style.FILL);
        alphaPaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(buttonSize / 2);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        baselineY = (buttonSize - fontMetrics.bottom - fontMetrics.top) / 2;

        if (lockingBitmap == null) {

            lockingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aim64);
            Matrix matrix = new Matrix();
            matrix.postScale((float) (buttonSize * 0.8) / lockingBitmap.getWidth(), (float) (buttonSize * 0.8) / lockingBitmap.getHeight());
            lockingBitmap = Bitmap.createBitmap(lockingBitmap, 0, 0, lockingBitmap.getWidth(), lockingBitmap.getHeight(), matrix, true);
        }
        bitmapLeft = (buttonSize - lockingBitmap.getWidth()) / 2;
        bitmapTop = (buttonSize - lockingBitmap.getHeight()) / 2;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        lastTouchX = x;
        lastTouchY = y;
        if (lastTouchX > 0 && lastTouchX < getWidth() && lastTouchY > 0 && lastTouchY < getHeight())
            isHolding = true;
        else
            isHolding = false;
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastTouchX = x;
                lastTouchY = y;

                isHolding = true;
                
                break;


            case MotionEvent.ACTION_UP:

                if (bindingCharacter.isReloadingAttack == false
                        && lastTouchX > 0 && lastTouchX < getWidth()
                        && lastTouchY > 0 && lastTouchY < getHeight()) {
                    if (bindingCharacter.isLocking == false) {
                        bindingCharacter.switchLockingState(true);
                    }
                    else {
                        bindingCharacter.switchLockingState(false);
                    }
                }
                isHolding=false;

                break;

            case MotionEvent.ACTION_MOVE:


        }

        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int realSize = buttonSize;
        int width = realSize;
        int height = realSize;
        setMeasuredDimension(width, height);
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bindingCharacter == null) {
            bindingCharacter = GameBaseAreaActivity.myCharacter;
            return;
        }
        if (bindingCharacter.isLocking){
            if(isHolding) {
                canvas.drawBitmap(lockingBitmap, bitmapLeft, bitmapTop, alphaPaint);
            }else{
                canvas.drawBitmap(lockingBitmap, bitmapLeft, bitmapTop, normalPaint);
            }
        }else{
            if(isHolding) {
                canvas.drawBitmap(lockingBitmap, bitmapLeft, bitmapTop, normalPaint);
            }else{
                canvas.drawBitmap(lockingBitmap, bitmapLeft, bitmapTop, alphaPaint);
            }
        }
//        if(bindingCharacter.attackCount<bindingCharacter.maxAttackCount&&bindingCharacter.reloadAttackStartTime!=0){
//            long startTime=bindingCharacter.reloadAttackStartTime;
//            float percent=(float) (new Date().getTime()-startTime)/bindingCharacter.reloadAttackNeedTime;
//            float sweepAngle=360*percent;
//            if (sweepAngle>360)
//                sweepAngle=359;
//            if(sweepAngle>0)
//                Log.i("","");
//            canvas.drawArc(new RectF(0,0,buttonSize,buttonSize),0,sweepAngle,true,normalPaint);
//        }
//        canvas.drawText(new Integer(bindingCharacter.attackCount).toString(),buttonSize/2,baselineY,textPaint);
        invalidate();
    }
}
