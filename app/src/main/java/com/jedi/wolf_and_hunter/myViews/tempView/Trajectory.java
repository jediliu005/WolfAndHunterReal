package com.jedi.wolf_and_hunter.myViews.tempView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

import java.util.Date;


/**
 * Created by Administrator on 2017/5/15.
 */

public class Trajectory extends View {
    public Point fromPointRelateParent;
    public Point toPointRelateParent;
    public Point fromPointForDraw;
    public Point toPointForDraw;
    public BaseCharacterView createCharacter;
    public long addTime;
    int left=-1;
    int top=-1;
    private  Paint paint;
    public FrameLayout parent;



    public Trajectory(Context context,Point fromPoint, Point toPoint,BaseCharacterView createCharacter) {
        super(context);
        this.createCharacter=createCharacter;
        this.fromPointRelateParent=fromPoint;
        this.toPointRelateParent=toPoint;
        int relateX=toPoint.x-fromPoint.x;
        int relateY=toPoint.y-fromPoint.y;
        if(paint==null) {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(2);
            LinearGradient lg=null;
            int fromX;
            int fromY;
            int toX;
            int toY;
            if(relateX>0){
                fromX=0;
                toX=relateX;
            }else{
                fromX=-relateX;
                toX=0;
            }
            if(relateY>0){
                fromY=0;
                toY=relateY;
            }else{
                fromY=-relateY;
                toY=0;
            }
            fromPointForDraw=new Point(fromX,fromY);
            toPointForDraw=new Point(toX,toY);

            lg = new LinearGradient(fromX, fromY, toX, toY, Color.BLACK, Color.WHITE, Shader.TileMode.CLAMP);  //
            paint.setShader(lg);
        }
        if(relateX>0)
            left=fromPoint.x;
        else
            left=toPoint.x;
        if(relateY>0)
            top=fromPoint.y;
        else
            top=toPoint.y;



        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(Math.abs(relateX),Math.abs(relateY));
        params.leftMargin=left;
        params.topMargin=top;
        this.setLayoutParams(params);

    }

    public void addTrajectory(FrameLayout parent){
        this.parent=parent;
        parent.addView(this);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wSize=0;
        int hSize=0;

        if(fromPointForDraw!=null&&toPointForDraw!=null){
            wSize=Math.abs(fromPointForDraw.x-toPointForDraw.x);
            hSize=Math.abs(fromPointForDraw.y-toPointForDraw.y);
        }
        setMeasuredDimension(wSize, hSize);


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(fromPointForDraw!=null&&toPointForDraw!=null&&paint!=null)
            canvas.drawLine(fromPointForDraw.x,fromPointForDraw.y,toPointForDraw.x,toPointForDraw.y,paint);
//        long nowTime=new Date().getTime();
//        if(nowTime-addTime>1000) {
//            GameBaseAreaActivity.allTrajectory.remove(this);
//            FrameLayout parent=((FrameLayout) this.getParent());
//            parent.removeView(this);
//            parent.invalidate();
//        }
    }

}
