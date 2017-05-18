package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/29.
 */

public class JRocker extends View  {
    BaseCharacterView bindingCharacter;
    public GameBaseAreaActivity.GameHandler gameHandler;
    Point padCircleCenter=new Point();
    Point rockerCircleCenter=new Point();
    static Bitmap fireBitmap;
    int startCenterX;
    int startCenterY;
//    int actionButtonLeft;
//    int actionButtonTop;
//    int actionButtonsWidth;
    int windowWidth;
    int windowHeight;
    public static int padRadius;
    public static int rockerRadius;
    double distance=0;
    Paint paintForPad;
    Paint paintForRocker;
    Paint normalPaint;
    int baselineY;
    public boolean isHoldingRocker=false;
    boolean readyToFire=false;
    public BaseCharacterView getBindingCharacter() {
        return bindingCharacter;
    }

    public void setBindingCharacter(BaseCharacterView bindingCharacter) {
        this.bindingCharacter = bindingCharacter;
    }
    public void init(Context context,AttributeSet attrs){
//        setBackgroundColor(Color.GRAY);
        ViewUtils.initWindowParams(getContext());
        DisplayMetrics dm=ViewUtils.windowsDisplayMetrics;

        windowHeight =dm.heightPixels;
        windowWidth=dm.widthPixels;
        if(attrs!=null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.JRocker);
            padRadius = (int) (ta.getDimension(R.styleable.JRocker_pad_radius, windowWidth / 10));
            rockerRadius = (int) (ta.getDimension(R.styleable.JRocker_rocker_radius, (int) (padRadius / 1.3)));
        }else{
            padRadius = (int) (windowWidth / 10);
            rockerRadius = (int) (padRadius / 1.3);
        }
//        actionButtonsWidth=(int)(padRadius/1.5);
        padCircleCenter.set(padRadius+rockerRadius,padRadius+rockerRadius);
        rockerCircleCenter.set(padRadius+rockerRadius,padRadius+rockerRadius);

        paintForPad = new Paint();
        paintForPad.setColor(Color.WHITE);
        paintForPad.setStyle(Paint.Style.STROKE);
        paintForPad.setAntiAlias(true);
        paintForPad.setAlpha(100);
        paintForPad.setStrokeWidth(10);

        paintForRocker = new Paint();
        paintForRocker.setColor(Color.WHITE);
        paintForRocker.setStyle(Paint.Style.FILL);
        paintForRocker.setAlpha(100);
        paintForRocker.setAntiAlias(true);

//        normalPaint = new Paint();
//        normalPaint.setTextSize(actionButtonsWidth/2);
//        normalPaint.setColor(Color.WHITE);
//        normalPaint.setStyle(Paint.Style.FILL);
//        normalPaint.setAntiAlias(true);
//        Paint.FontMetricsInt fontMetrics = normalPaint.getFontMetricsInt();
//        baselineY = (actionButtonTop*2+actionButtonsWidth - fontMetrics.bottom - fontMetrics.top) / 2;

        FrameLayout.LayoutParams paramsForJRocker = ( FrameLayout.LayoutParams)getLayoutParams();
        int realWidth=2*padRadius+2*rockerRadius;
        if(paramsForJRocker==null)
            paramsForJRocker=new FrameLayout.LayoutParams(realWidth,realWidth);
        else {
            paramsForJRocker.height = realWidth;
            paramsForJRocker.width = realWidth;
        }
//        if(fireBitmap==null) {
//            fireBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
//            Matrix matrix = new Matrix();
//            matrix.postScale((float)actionButtonsWidth / fireBitmap.getWidth(), (float)actionButtonsWidth / fireBitmap.getHeight());
//            fireBitmap = Bitmap.createBitmap(fireBitmap, 0, 0, fireBitmap.getWidth(), fireBitmap.getHeight(), matrix, true);
//        }

        //在这里，新版系统必须重新setLayoutParams才能成功调整,19不用，还是要加。。。
        this.setLayoutParams(paramsForJRocker);
    }
    public JRocker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public JRocker(Context context) {
        super(context);
        init(context,null);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int realWidth=2*padRadius+2*rockerRadius;
        int width=realWidth;
        int height=realWidth;
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
        canvas.drawCircle( padCircleCenter.x, padCircleCenter.y,padRadius, paintForPad);
        canvas.drawCircle( rockerCircleCenter.x, rockerCircleCenter.y,rockerRadius, paintForRocker);
    }


}
