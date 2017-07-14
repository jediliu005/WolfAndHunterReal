package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.myObj.gameObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

/**
 * Created by Administrator on 2017/5/10.
 */

public class SmellButton extends View {
    static Bitmap smellBitmap;
    public int buttonSize;
    public Paint normalPaint;
    public Paint alphaPaint;
    public int bitmapLeft;
    public int bitmapTop;
    private int lastTouchX;
    private int lastTouchY;
    private boolean isHolding=false;
    public BaseCharacterView bindingCharacter;

    public SmellButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public SmellButton(Context context) {
        super(context);
        init();
    }



    public void init() {

        int windowWidth = MyVirtualWindow.getWindowWidth(getContext());
        int windowHeight = MyVirtualWindow.getWindowHeight(getContext());
        buttonSize = (int) (windowWidth / 8);

        normalPaint = new Paint();
        normalPaint.setStyle(Paint.Style.FILL);
        normalPaint.setColor(Color.WHITE);
        normalPaint.setAntiAlias(true);



        alphaPaint = new Paint();
        alphaPaint.setAlpha(100);
        alphaPaint.setAntiAlias(true);




        if (smellBitmap == null) {
            smellBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nose);

            Matrix matrix = new Matrix();
            matrix.postScale((float) (buttonSize * 0.8) / smellBitmap.getWidth(), (float) (buttonSize * 0.8) / smellBitmap.getHeight());
            smellBitmap = Bitmap.createBitmap(smellBitmap, 0, 0, smellBitmap.getWidth(), smellBitmap.getHeight(), matrix, true);
        }
        bitmapLeft = (buttonSize - smellBitmap.getWidth()) / 2;
        bitmapTop = (buttonSize - smellBitmap.getHeight()) / 2;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getX();
        int y = (int) event.getY();
        lastTouchX = x;
        lastTouchY = y;
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                isHolding=true;
                break;


            case MotionEvent.ACTION_UP:

                if (lastTouchX > 0 && lastTouchX < getWidth() && lastTouchY > 0 && lastTouchY < getHeight()) {
                    bindingCharacter.smell();
                }
                isHolding=false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (lastTouchX > 0 && lastTouchX < getWidth() && lastTouchY > 0 && lastTouchY < getHeight()) {
                    isHolding=true;
                }else{
                    isHolding=false;
                }
                break;



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
        if(smellBitmap!=null) {
            if(isHolding)
                canvas.drawBitmap(smellBitmap, bitmapLeft, bitmapTop, alphaPaint);
            else
                canvas.drawBitmap(smellBitmap, bitmapLeft, bitmapTop, normalPaint);
        }
        if (bindingCharacter.nowSmellCount>0) {
            float percent = (float)bindingCharacter.nowSmellCount/BaseCharacterView.smellTotalCount;
            float sweepAngle = 360 * percent;
            if (sweepAngle > 360)
                sweepAngle = 359;
            if (sweepAngle > 0)
                Log.i("", "");
            canvas.drawArc(new RectF(0, 0, buttonSize, buttonSize), 0, sweepAngle, true, normalPaint);
        }
        invalidate();
    }
}
