package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;

/**
 * Created by Administrator on 2017/4/26.
 */

public class SFViewRange extends SurfaceView implements SurfaceHolder.Callback {
    private boolean isStop = true;
    public int nowViewRadius;
    public int centerX, centerY;
    public int nowLeft;
    public int nowTop;
    public int nowRight;
    public int nowBottom;
    public double nowFaceingRadian;
    public double nowViewRadian;
    public FrameLayout.LayoutParams layoutParams;
    BaseCharacterView bindingCharacter;
    Paint borderPaint;

    public SFViewRange(Context context) {
        super(context);
        if (GameBaseAreaActivity.myCharacter != null)
            bindingCharacter = GameBaseAreaActivity.myCharacter;
        init();
    }

    public SFViewRange(Context context, BaseCharacterView character) {
        super(context);
        bindingCharacter = character;
        init();
    }

    public SFViewRange(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (GameBaseAreaActivity.myCharacter != null)
            bindingCharacter = GameBaseAreaActivity.myCharacter;
        init();
    }

    public SFViewRange(Context context, @Nullable AttributeSet attrs, BaseCharacterView character) {
        super(context, attrs);
        bindingCharacter = character;
        init();
    }

    private void init() {
        SurfaceHolder mHolder = getHolder();
        mHolder.addCallback(this);
        //以下两句必须在构造方法里做，否则各种奇妙poorguy
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        setZOrderOnTop(true);

        if (bindingCharacter != null) {
            nowViewRadius = bindingCharacter.nowViewRadius;
            centerX = bindingCharacter.centerX;
            centerY = bindingCharacter.centerY;
            nowLeft = centerX - nowViewRadius;
            nowRight = centerX + nowViewRadius;
            nowTop = centerY - nowViewRadius;
            nowBottom = centerY + nowViewRadius;
            DashPathEffect pathEffect = new DashPathEffect(new float[]{10, 10}, 0);
            borderPaint = new Paint();
            borderPaint.setPathEffect(pathEffect);
            borderPaint.setColor(Color.YELLOW);
            borderPaint.setAlpha(20);
            borderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            borderPaint.setStrokeWidth(5);
            borderPaint.setAntiAlias(true);
        }
        if (this.getLayoutParams() == null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = bindingCharacter.centerX - nowViewRadius;
            layoutParams.topMargin = bindingCharacter.centerY - nowViewRadius;
            this.setLayoutParams(layoutParams);
            this.layoutParams = layoutParams;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Thread drawThread = new Thread(new ViewRangeDraw());
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    class ViewRangeDraw implements Runnable {


        @Override
        public void run() {
            isStop = false;
            while (!isStop) {
                Canvas canvas = getHolder().lockCanvas();
                try {


                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕
                    float startAngle = bindingCharacter.nowFacingAngle - bindingCharacter.nowViewAngle / 2;
                    canvas.drawArc(new RectF(0, 0, 2 * nowViewRadius, 2 * nowViewRadius), startAngle, bindingCharacter.nowViewAngle, true, borderPaint);

//
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        getHolder().unlockCanvasAndPost(canvas);
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
