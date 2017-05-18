package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/29.
 */

public class SFJRocker extends SurfaceView implements SurfaceHolder.Callback {
    BaseCharacterView bindingCharacter;
    public GameBaseAreaActivity.GameHandler gameHandler;
    Point padCircleCenter=new Point();
    Point rockerCircleCenter=new Point();

    int startCenterX;
    int startCenterY;

    int windowWidth;
    int windowHeight;
    public static int padRadius;
    public static int rockerRadius;
    double distance=0;
    Paint paintForPad;
    Paint paintForRocker;
    SurfaceHolder mHolder;
    public boolean isHoldingRocker=false;
    boolean isStop=false;
    public BaseCharacterView getBindingCharacter() {
        return bindingCharacter;
    }

    public void setBindingCharacter(BaseCharacterView bindingCharacter) {
        this.bindingCharacter = bindingCharacter;
    }
    public void init(Context context, AttributeSet attrs){
        mHolder=getHolder();
        mHolder.addCallback(this);
        ViewUtils.initWindowParams(getContext());
        DisplayMetrics dm=ViewUtils.windowsDisplayMetrics;

        if (attrs != null) {
            TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.JRocker);
            padRadius = (int)(ta.getDimension(R.styleable.JRocker_pad_radius,windowWidth/10));
            rockerRadius =(int)(ta.getDimension(R.styleable.JRocker_rocker_radius,(int)(padRadius/1.3))) ;
        }else{
            padRadius = (int)(windowWidth/10);
            rockerRadius =(int)(padRadius/1.3);
        }
        padCircleCenter.set(padRadius+rockerRadius,padRadius+rockerRadius);
        rockerCircleCenter.set(padRadius+rockerRadius,padRadius+rockerRadius);



//        setZOrderMediaOverlay(true);
        //以下这两个因为横屏关系，必须宽高调换，
        windowHeight =dm.heightPixels;
        windowWidth=dm.widthPixels;
        paintForPad = new Paint();
        paintForPad.setColor(Color.WHITE);
        paintForPad.setStyle(Paint.Style.STROKE);
        paintForPad.setAntiAlias(true);
        paintForPad.setStrokeWidth(10);

        paintForRocker = new Paint();
        paintForRocker.setColor(Color.WHITE);
        paintForRocker.setStyle(Paint.Style.FILL);
        paintForRocker.setAntiAlias(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }
    public SFJRocker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( context,  attrs);






    }

    public SFJRocker(Context context) {
        super(context);
        init(context,null);
    }

    /**
     * 等重构再说。。。
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode=MeasureSpec.getMode(widthMeasureSpec);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        isStop=false;

        setFocusable(true);
        setFocusableInTouchMode(true);

        FrameLayout.LayoutParams paramsForJRocker = ( FrameLayout.LayoutParams)getLayoutParams();
        paramsForJRocker.height=(int)(2*padRadius+2*rockerRadius);
        paramsForJRocker.width=(int)(2*padRadius+2*rockerRadius);
        //在这里，新版系统必须重新setLayoutParams才能成功调整,19不用，还是要加。。。
        this.setLayoutParams(paramsForJRocker);
        DrawRocker drawLeftRocker=new DrawRocker();
        Thread drawThread=new Thread(drawLeftRocker);
        drawThread.start();


    }




    class DrawRocker implements Runnable{

        @Override
        public void run() {
            Canvas canvas = null;


            while (!isStop) {
                try {

                    canvas = mHolder.lockCanvas();
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕

//                    canvas.drawColor(Color.GREEN);
                    canvas.drawCircle( padCircleCenter.x, padCircleCenter.y,padRadius, paintForPad);
                    canvas.drawCircle( rockerCircleCenter.x, rockerCircleCenter.y,rockerRadius, paintForRocker);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }








    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        isStop=true;
    }
}
