package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

import java.util.Date;

/**
 * Created by Administrator on 2017/5/10.
 */

public class SmellButton extends View {
    static Bitmap smellBitmap;
    public int buttonSize;
    public Paint normalPaint;
    public TextPaint redTextPaint;
    public TextPaint blackTextPaint;
    public int baselineY;
    public int bitmapLeft;
    public int bitmapTop;
    private long lastTouchTime;
    boolean isTouchingInside = true;
    private int lastTouchX;
    private int lastTouchY;
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

        normalPaint.setColor(Color.WHITE);
        normalPaint.setStyle(Paint.Style.FILL);
        normalPaint.setAntiAlias(true);


        redTextPaint = new TextPaint();
        redTextPaint.setColor(Color.RED);
        redTextPaint.setTextSize(buttonSize / 2);
        redTextPaint.setTextAlign(Paint.Align.CENTER);

        blackTextPaint = new TextPaint();
        blackTextPaint.setColor(Color.BLACK);
        blackTextPaint.setTextSize(buttonSize / 2);
        blackTextPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetricsInt fontMetrics = redTextPaint.getFontMetricsInt();
        baselineY = (buttonSize - fontMetrics.bottom - fontMetrics.top) / 2;




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
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastTouchX = x;
                lastTouchY = y;
                lastTouchTime = new Date().getTime();
                if (GameBaseAreaActivity.myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF) {
                    bindingCharacter.isStay = true;
                }
                isTouchingInside = true;

                break;


            case MotionEvent.ACTION_UP:
                lastTouchTime = 0;
                if (GameBaseAreaActivity.myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_WOLF) {
                    bindingCharacter.isStay = false;
                }
                if (lastTouchX > 0 && lastTouchX < getWidth() && lastTouchY > 0 && lastTouchY < getHeight())
                    bindingCharacter.attack();
                break;

            case MotionEvent.ACTION_MOVE:
                lastTouchX = x;
                lastTouchY = y;
                if (new Date().getTime() - lastTouchTime > 800) {
                    if (bindingCharacter.attackCount<bindingCharacter.maxAttackCount&&GameBaseAreaActivity.myPlayerInfo.characterType == BaseCharacterView.CHARACTER_TYPE_HUNTER) {
                        bindingCharacter.reloadAttackCount();
                    }
                }

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
            canvas.drawBitmap(smellBitmap, bitmapLeft, bitmapTop, null);
        }
    }
}
