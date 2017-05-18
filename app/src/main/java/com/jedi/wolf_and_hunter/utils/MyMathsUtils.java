package com.jedi.wolf_and_hunter.utils;

import android.graphics.Point;
import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2017/4/2.
 */

public class MyMathsUtils {
    public static int POSITION_IN=0;
    public static int POSITION_ON=1;
    public static int POSITION_OUT=2;

    public static boolean isInRECT(int rectLeft,int rectTop,int rectRight,int rectBottom, Point targetPoint){
        if(targetPoint.x>rectLeft&&targetPoint.x<rectRight&&targetPoint.y>rectTop&&targetPoint.y<rectBottom){
          return true;
        }
        return false;
    }

    public static boolean isInCircle(Point circleCenter, int radius, Point targetPoint){
        return positionRelativeToCircle(circleCenter,radius,targetPoint)==POSITION_IN?true:false;
    }

    public static int positionRelativeToCircle(Point circleCenter, int radius, Point targetPoint){
        int centreX = circleCenter.x;
        int centreY = circleCenter.y;
        int targetPointX=targetPoint.x;
        int targetPointY=targetPoint.y;
        int relateX=targetPointX-centreX;
        int relateY=targetPointY-centreY;
        if(relateX * relateX + relateY * relateY >radius*radius)
            return POSITION_OUT;
        if(relateX * relateX + relateY * relateY ==radius*radius)
            return POSITION_ON;
        if(relateX * relateX + relateY * relateY <radius*radius)
            return POSITION_IN;


        return 1;
    }

    public static double getDistance(Point a,Point b){
        if(a!=null&&b!=null ){
            int dx=a.x-b.x;
            int dy=a.y-b.y;
            return Math.sqrt(dx*dx+dy*dy);
        }
        return 0;
    }

    /**
     * 点（X1,Y1）到直线y=kx+b
     * 距离=|kX1-Y1+b|/√(k²+（-1）²)
     * @param point
     * @param k
     * @param b
     * @return
     */
    public static double getPointToLineDistance(Point point,double k,double b){
        double distance=0;
        distance=Math.abs(k*point.x-point.y)/Math.sqrt(k*k+1);
        return distance;
    }
    /**
     *
     * @param relateX 两点X轴坐标的相对距离
     * @param relateY 相对Y轴坐标的相对距离
     * @return 相对X轴的夹角[0-360)
     */
    public static float getAngleBetweenXAxus(int relateX,int relateY){

        double cos = relateX / Math.sqrt(relateX * relateX + relateY * relateY);
        double radian = Math.acos(cos);
        float angle = (float) (180 * radian / Math.PI);
        if (relateY < 0)
            angle = 360 - angle;
        return angle;
    }
}
