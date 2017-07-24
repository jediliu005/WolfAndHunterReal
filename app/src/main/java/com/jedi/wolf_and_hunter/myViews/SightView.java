package com.jedi.wolf_and_hunter.myViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myObj.gameObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.myViews.rocker.JRocker;
import com.jedi.wolf_and_hunter.utils.MyMathsUtils;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

/**
 * Created by Administrator on 2017/3/13.
 */

public class SightView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String TAG = "SurfaceView";
    //以下为移动相关
    public MyVirtualWindow virtualWindow;
    int windowWidth;
    int windowHeight;
    //这四个是可视界面看做相对this.parent的View而的虚拟出来的LRTB
    public boolean hasUpdatedWindowPosition = false;

//    public int virtualWindow.left;
//    public int virtualWindow.right;
//    public int virtualWindow.top;
//    public int virtualWindow.bottom;


    public int offX;
    public int offY;
    public boolean needMove = false;
    //以下为视点基本共有属性
    public boolean hasUpdatedPosition = false;
    public int centerX=-1, centerY=-1;
    public int nowLeft=-1;
    public int nowTop=-1;
    public int nowRight=-1;
    public int nowBottom=-1;
    public int speed = 15;
    public int sightSize;
    //以下为绘图杂项

    public static boolean isStop=true;
    public Bitmap sightBitmap;
    public Matrix matrix;
    public SurfaceHolder mHolder;
    public int sightBitmapWidth;
    public int sightBitmapHeight;
    public FrameLayout.LayoutParams mLayoutParams;
    public BaseCharacterView bindingCharacter;
    Paint transparentPaint;
    public boolean isHidden;

    public SightView(Context context) {
        super(context);
        init();
    }

    public SightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SightView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        ViewUtils.initWindowParams(getContext());
        DisplayMetrics dm = ViewUtils.windowsDisplayMetrics;
        windowHeight = dm.heightPixels;
        windowWidth = dm.widthPixels;
        if (sightSize == 0)
            sightSize = 50;
        getHolder().addCallback(this);
        mHolder = getHolder();
        mHolder.addCallback(this);
        //以下两句必须在构造方法里做，否则各种奇妙poorguy
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);

        if(nowLeft<0||nowTop<0){
            nowLeft=0;
            nowTop=0;
            nowRight=sightSize;
            nowBottom=sightSize;
        }
        mLayoutParams=(FrameLayout.LayoutParams)getLayoutParams();
        if(mLayoutParams==null)
            mLayoutParams=new FrameLayout.LayoutParams(sightSize,sightSize);
        mLayoutParams.leftMargin=nowLeft;
        mLayoutParams.topMargin=nowTop;
        this.setLayoutParams(mLayoutParams);
        centerX = nowLeft + (sightSize) / 2;
        centerY = nowTop + (sightSize) / 2;
    }



    public void goWatchingCharacter() {
        updateNowPosition();

        int newWindowLeft = virtualWindow.left;
        int newWindowTop = virtualWindow.top;
        if (bindingCharacter.nowLeft < virtualWindow.left)
            newWindowLeft = bindingCharacter.nowLeft;
        if (bindingCharacter.nowRight > virtualWindow.right)
            newWindowLeft = bindingCharacter.nowRight - windowWidth;
        if (bindingCharacter.nowBottom > virtualWindow.bottom)
            newWindowTop = bindingCharacter.nowBottom - windowHeight;
        if (bindingCharacter.nowTop < virtualWindow.top)
            newWindowTop = bindingCharacter.nowTop;
        virtualWindow.targetLeft = newWindowLeft;
        virtualWindow.targetTop = newWindowTop;
        virtualWindow.targetRight = newWindowLeft + windowWidth;
        virtualWindow.targetBottom = newWindowTop + windowHeight;


    }

    public void keepDirectionAndMove(int limitLeft, int limitTop, int limitRight, int limitBottom) {
        updateNowPosition();
//        updateNowWindowPosition();
        int nowCharacterCenterX = (bindingCharacter.nowLeft + bindingCharacter.nowRight) / 2;
        int nowCharacterCenterY = (bindingCharacter.nowTop + bindingCharacter.nowBottom) / 2;
        int nowSightCenterX = (nowLeft + nowRight) / 2;
        int nowSightCenterY = (nowTop + nowBottom) / 2;
        //注意添加sight本身宽度修正
        int realRelateLimitLeft = limitLeft + getWidth() / 2 - nowCharacterCenterX;
        int realRelateLimitTop = limitTop + getHeight() / 2 - nowCharacterCenterY;
        int realRelateLimitRight = limitRight - getWidth() / 2 - nowCharacterCenterX;
        int realRelateLimitBottom = limitBottom - getHeight() / 2 - nowCharacterCenterY;

        int resultRelateX = 0;
        int resultRelateY = 0;
        int relateX = nowSightCenterX - nowCharacterCenterX;
        int relateY = nowSightCenterY - nowCharacterCenterY;
        if (relateX == 0 && relateY == 0)
            return;

        if (relateX == 0) {
            if (relateY > 0)
                resultRelateY = realRelateLimitBottom;
            else {
                resultRelateY = realRelateLimitTop;
            }
        } else if (relateY == 0) {
            if (relateX > 0)
                resultRelateX = realRelateLimitRight;
            else {
                resultRelateX = realRelateLimitLeft;
            }
        } else {

            double tanAlpha = 0;
            try {

                tanAlpha = (double) relateY / relateX;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (tanAlpha > 0) {
                if (relateX > 0) {
                    //BR
                    resultRelateY = (int) (tanAlpha * realRelateLimitRight);

                    if (resultRelateY > realRelateLimitBottom) {
                        resultRelateY = realRelateLimitBottom;
                        resultRelateX = (int) (realRelateLimitBottom / tanAlpha);
                    } else {
                        resultRelateX = realRelateLimitRight;
                    }

                } else if (relateX < 0) {
                    //TL
                    resultRelateY = (int) (tanAlpha * realRelateLimitLeft);

                    if (resultRelateY < realRelateLimitTop) {
                        resultRelateY = realRelateLimitTop;
                        resultRelateX = (int) (realRelateLimitTop / tanAlpha);
                    } else {
                        resultRelateX = realRelateLimitLeft;
                    }
                }

            } else if (tanAlpha < 0) {
                if (relateX > 0) {
                    //TR
                    resultRelateY = (int) (tanAlpha * realRelateLimitRight);

                    if (resultRelateY < realRelateLimitTop) {
                        resultRelateY = realRelateLimitTop;
                        resultRelateX = (int) (realRelateLimitTop / tanAlpha);
                    } else {
                        resultRelateX = realRelateLimitRight;
                    }
                } else if (relateX < 0) {
                    //BL
                    resultRelateY = (int) (tanAlpha * realRelateLimitLeft);

                    if (resultRelateY > realRelateLimitBottom) {
                        resultRelateY = realRelateLimitBottom;
                        resultRelateX = (int) (realRelateLimitBottom / tanAlpha);
                    } else {
                        resultRelateX = realRelateLimitLeft;
                    }
                }

            }
//            else {
//                Log.i("", "");
//            }
//            if (resultRelateX == 0 || resultRelateY == 0) {
//                Log.i("", "");
//            }
        }
        int newCenterX = nowCharacterCenterX + resultRelateX;
        int newCenterY = nowCharacterCenterY + resultRelateY;
//        mLayoutParams.leftMargin=bindingCharacter.centerX+resultRelateX-this.getWidth()/2;
//        mLayoutParams.topMargin=bindingCharacter.centerY+resultRelateY-this.getHeight()/2;
        if(isHidden==false) {
            nowLeft = newCenterX - getWidth() / 2;
            nowTop = newCenterY - getHeight() / 2;
            nowRight = nowLeft + getWidth();
            nowBottom = nowTop + getHeight();
        }

    }

    public void updateNowPosition() {
        if (hasUpdatedPosition == true)
            return;

        nowLeft = getLeft();
        nowTop = getTop();
        nowRight = getRight();
        nowBottom = getBottom();
        hasUpdatedPosition = true;
    }

//    vir

    public boolean isCharacterInWindow() {
        if (bindingCharacter.nowLeft < virtualWindow.left
                || bindingCharacter.nowRight > virtualWindow.right
                || bindingCharacter.nowBottom > virtualWindow.bottom
                || bindingCharacter.nowTop < virtualWindow.top) {
            return false;
        }
        return true;
    }

    public boolean isSightInWindow() {
        updateNowPosition();
//        updateNowWindowPosition();
        if (this.nowLeft < virtualWindow.left
                || this.nowRight > virtualWindow.right
                || this.nowBottom > virtualWindow.bottom
                || this.nowTop < virtualWindow.top) {
            return false;
        }
        return true;
    }

    public void followCharacter(int CharacterOffX, int CharacterOffY) {
        updateNowPosition();
//        updateNowWindowPosition();
        int followX = CharacterOffX;
        int followY = CharacterOffY;

        //控制位置不超出父View
        try {
            followX = ViewUtils.reviseOffX(this, (View) this.getParent(), followX);
            followY = ViewUtils.reviseOffY(this, (View) this.getParent(), followY);
            nowLeft = nowLeft + followX;
            nowRight = nowLeft + getWidth();
            nowTop = nowTop + followY;
            nowBottom = nowTop + getHeight();
            if (nowLeft < virtualWindow.left) {
                virtualWindow.targetLeft = nowLeft;
                virtualWindow.targetRight = nowLeft + windowWidth;
            } else if (nowRight > virtualWindow.right) {
                virtualWindow.targetRight = nowRight;
                virtualWindow.targetLeft = nowRight - windowWidth;
            }
            if (nowTop < virtualWindow.top) {
                virtualWindow.targetTop = nowTop;
                virtualWindow.targetBottom = nowTop + windowHeight;
            } else if (nowBottom > virtualWindow.bottom) {
                virtualWindow.targetBottom = nowBottom;
                virtualWindow.targetTop = nowBottom - windowHeight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void masterModeOffsetLRTBParams(boolean isMyCharacterMoving) {
        int nowOffX = offX;
        int nowOffY = offY;


        //根据设定速度修正位移量
        double offDistance = Math.sqrt(nowOffX * nowOffX + nowOffY * nowOffY);
        int nowSpeed = speed;
        if (offDistance < JRocker.padRadius * 4 / 5)
            nowSpeed = speed / 3;

        updateNowPosition();
//        updateNowWindowPosition();
        int oldWindowLeft = virtualWindow.left;
        int oldWindowTop = virtualWindow.top;


        nowOffX = (int) (nowSpeed * nowOffX / offDistance);
        nowOffY = (int) (nowSpeed * nowOffY / offDistance);

        try {

            //控制位置不超出父View
            nowOffX = ViewUtils.reviseOffX(this, (View) this.getParent(), nowOffX);
            nowOffY = ViewUtils.reviseOffY(this, (View) this.getParent(), nowOffY);

//            centerX = getLeft() + getWidth() / 2 + nowOffX;
//            centerY = getTop() + getHeight() / 2 + nowOffX;
            //控制位置不超出可视范围

            nowLeft = nowLeft + nowOffX;
            nowTop = nowTop + nowOffY;
            nowRight = nowLeft + getWidth();
            nowBottom = nowTop + getHeight();
            if (isMyCharacterMoving) {
                movingNearCharacter();
            } else {

                if (this.nowLeft < virtualWindow.left)
                    virtualWindow.targetLeft = this.nowLeft;
                if (this.nowRight > virtualWindow.right)
                    virtualWindow.targetLeft = this.nowRight - windowWidth;
                if (this.nowTop < virtualWindow.top)
                    virtualWindow.targetTop = this.nowTop;
                if (this.nowBottom > virtualWindow.bottom)
                    virtualWindow.targetTop = this.nowBottom - windowHeight;
                virtualWindow.targetRight = virtualWindow.left + windowWidth;
                virtualWindow.targetBottom = virtualWindow.top + windowHeight;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (nowBottom > ((FrameLayout) getParent()).getHeight()) {
            Log.i("", "");
        }
    }


    public void normalModeOffsetLRTBParams() {
        int nowOffX = offX;
        int nowOffY = offY;
        float targetFacingAngle=0;
        if(nowOffX==0&&nowOffY==0)
            return;
        try {
            targetFacingAngle=MyMathsUtils.getAngleBetweenXAxus(nowOffX,nowOffY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        float relateAngle = targetFacingAngle - bindingCharacter.nowFacingAngle;
        if (Math.abs(relateAngle) > 180) {//处理旋转最佳方向
            if (relateAngle > 0)
                relateAngle = relateAngle - 360;

            else
                relateAngle = 360 - relateAngle;
        }
        if (Math.abs(relateAngle) > bindingCharacter.nowAngleChangSpeed)
            relateAngle = Math.abs(relateAngle) / relateAngle * bindingCharacter.nowAngleChangSpeed;

        targetFacingAngle = bindingCharacter.nowFacingAngle + relateAngle;
        bindingCharacter.nowFacingAngle=targetFacingAngle;

        if (bindingCharacter.nowFacingAngle < 0)
            bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle + 360;
        else if (bindingCharacter.nowFacingAngle > 360)
            bindingCharacter.nowFacingAngle = bindingCharacter.nowFacingAngle - 360;



        if(isHidden==false) {
            //这模式下sight隐藏，但跟随character
            this.nowLeft = (bindingCharacter.nowLeft + bindingCharacter.nowRight + sightSize) / 2;
            this.nowRight = this.nowLeft + getWidth();
            this.nowTop = (bindingCharacter.nowTop + bindingCharacter.nowBottom + sightSize) / 2;
            this.nowBottom = this.nowTop + getHeight();
        }

    }

    public void movingNearCharacter() {
        int newLRRelateX = Math.abs((nowLeft + nowRight) / 2 - (bindingCharacter.nowLeft + bindingCharacter.nowRight) / 2) + (getWidth() + bindingCharacter.getWidth()) / 2;
        int newTBRelateY = Math.abs((nowTop + nowBottom) / 2 - (bindingCharacter.nowTop + bindingCharacter.nowBottom) / 2) + (getHeight() + bindingCharacter.getHeight()) / 2;
        if (newLRRelateX > windowWidth) {
            if (nowRight > bindingCharacter.nowLeft) {
                nowRight = bindingCharacter.nowLeft + windowWidth;
                nowLeft = nowRight - getWidth();
                virtualWindow.targetRight = nowRight;
                virtualWindow.targetLeft = nowRight - windowWidth;
            } else if (nowLeft < bindingCharacter.nowRight) {
                nowLeft = bindingCharacter.nowRight - windowWidth;
                nowRight = nowLeft + getWidth();
                virtualWindow.targetLeft = nowLeft;
                virtualWindow.targetRight = nowLeft + windowWidth;
            }
        } else {

            if (nowLeft < virtualWindow.left) {
                virtualWindow.targetLeft = nowLeft;
                virtualWindow.targetRight = nowLeft + windowWidth;
            } else if (nowRight > virtualWindow.right) {
                virtualWindow.targetRight = nowRight;
                virtualWindow.targetLeft = nowRight - windowWidth;
            }
        }


        if (newTBRelateY > windowHeight) {
            if (nowBottom > bindingCharacter.nowTop) {
                nowBottom = bindingCharacter.nowTop + windowHeight;
                nowTop = nowBottom - getHeight();
                virtualWindow.targetBottom = nowBottom;
                virtualWindow.targetTop = nowBottom - windowHeight;
            } else if (nowTop < bindingCharacter.nowBottom) {
                nowTop = bindingCharacter.nowBottom - windowHeight;
                nowBottom = nowTop + getHeight();
                virtualWindow.targetTop = nowTop;
                virtualWindow.targetBottom = nowTop + windowHeight;
            }
        } else {

            if (nowTop < virtualWindow.top) {
                virtualWindow.targetTop = nowTop;
                virtualWindow.targetBottom = nowTop + windowHeight;
            } else if (nowBottom > virtualWindow.bottom) {
                virtualWindow.targetBottom = nowBottom;
                virtualWindow.targetTop = nowBottom - windowHeight;
            }
        }


    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(wSize, hSize);


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


//    public boolean onTouchEvent(MotionEvent event) {
//        //获取到手指处的横坐标和纵坐标
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//
//        switch(event.getAction())
//        {
//            case MotionEvent.ACTION_DOWN:
//
//                lastX = x;
//                lastY = y;
//
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                int offX ;
//                int offY ;
//                int[] movementArr=new int[4];
//                //计算移动的距离
//                offX = x - lastX;
//                offY = y - lastY;
//                movementArr= new ViewUtils().reviseTwoRectViewMovement(this,(View)this.getParent(),offX,offY);
//                nowLeft=movementArr[0];
//                nowTop=movementArr[1];
//                nowRight=movementArr[2];
//                nowBottom=movementArr[3];
//
//                mLayoutParams = (FrameLayout.LayoutParams)this.getLayoutParams();
//                mLayoutParams.setMargins(movementArr[0],movementArr[1], movementArr[2], movementArr[3]);
//                layout(nowLeft,nowTop, nowRight, nowBottom);
//        }
//
//        return true;
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {



        transparentPaint = new Paint();
        transparentPaint.setAlpha(0);

        sightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aim64);
        matrix = new Matrix();
//         缩放原图
        matrix.postScale((float) sightSize / sightBitmap.getWidth(), (float) sightSize / sightBitmap.getHeight());
        sightBitmap = Bitmap.createBitmap(sightBitmap, 0, 0, sightBitmap.getWidth(), sightBitmap.getHeight(),
                matrix, true);
        sightBitmapHeight = sightBitmap.getHeight();
        sightBitmapWidth = sightBitmap.getHeight();
        mLayoutParams = (FrameLayout.LayoutParams) this.getLayoutParams();


        mLayoutParams.height = sightBitmapHeight;
        mLayoutParams.width = sightBitmapWidth;

        this.setLayoutParams(mLayoutParams);

//        FrameLayout parent = (FrameLayout) this.getParent();
//        virtualWindow.left = -parent.getLeft();
//        virtualWindow.top = -parent.getTop();
//        virtualWindow.right =  windowWidth- parent.getLeft();
//        virtualWindow.bottom =  windowHeight- parent.getTop();
        if(isHidden==false) {
            centerX = sightBitmapHeight / 2;
            centerY = sightBitmapWidth / 2;
            Thread drawThread = new Thread(new SightDraw());
            drawThread.start();
        }
    }

    class SightDraw implements Runnable {


        @Override
        public void run() {

            while (GameBaseAreaActivity.gameInfo.isStop==false&&isStop==false) {

                Canvas canvas = getHolder().lockCanvas();
                try {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清除屏幕
                    if(isHidden)
                        canvas.drawBitmap(sightBitmap, 0, 0, transparentPaint);
                    else
                        canvas.drawBitmap(sightBitmap, 0, 0, null);

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


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

