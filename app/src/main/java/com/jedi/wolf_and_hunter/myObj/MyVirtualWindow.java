package com.jedi.wolf_and_hunter.myObj;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.utils.ViewUtils;

import java.util.TimerTask;

/**
 * Created by Administrator on 2017/5/8.
        */

public class MyVirtualWindow {
    private static int windowWidth;
    private static int windowHeight;
    public int left;
    public int top;
    public int right;
    public int bottom;
    public int windowMoveSpeed=50;
    public int targetLeft;
    public int targetTop;
    public int targetRight;
    public int targetBottom;
    public FrameLayout movingLayout;
    public boolean hasUpdatedWindowPosition = false;

    public static int getWindowWidth(Context context) {
        if(windowWidth==0){
            ViewUtils.initWindowParams(context);
            DisplayMetrics dm=ViewUtils.getWindowsDisplayMetrics();
            windowWidth=dm.widthPixels;
            windowHeight=dm.heightPixels;
        }

        return windowWidth;
    }
    public static int getWindowHeight(Context context) {
        if(windowHeight==0){
            ViewUtils.initWindowParams(context);
            DisplayMetrics dm=ViewUtils.getWindowsDisplayMetrics();
            windowWidth=dm.widthPixels;
            windowHeight=dm.heightPixels;
        }

        return windowHeight;
    }

    public MyVirtualWindow(Context context, FrameLayout movingLayout){
        super();
        this.movingLayout=movingLayout;
        ViewUtils.initWindowParams(context);
        DisplayMetrics dm=ViewUtils.getWindowsDisplayMetrics();
        windowWidth=dm.widthPixels;
        windowHeight=dm.heightPixels;
    }
    public void updateNowWindowPosition(FrameLayout movingLayout) {
        synchronized (this) {
            if (hasUpdatedWindowPosition == true)
                return;
            this.movingLayout = movingLayout;
            left = -movingLayout.getLeft();
            top = -movingLayout.getTop();
            right = left + windowWidth;
            bottom = top + windowHeight;
            hasUpdatedWindowPosition = true;
        }
    }
    public void autoUpdateNowWindowPosition() {
        synchronized (this) {

            left = -movingLayout.getLeft();
            top = -movingLayout.getTop();
            right = left + windowWidth;
            bottom = top + windowHeight;
            hasUpdatedWindowPosition = true;
        }
    }
//    public void autoOffsetWindow() {
//        synchronized (this) {
//            gameHandler.sendEmptyMessage(GameBaseAreaActivity.GameHandler.REFRESH_WINDOW_POSITION);
//        }
//    }
    public void offsetWindow() {
        synchronized (this) {
            FrameLayout.LayoutParams movingLayoutParams = (FrameLayout.LayoutParams) movingLayout.getLayoutParams();
            int parentNewLeft = -left;
            int parentNewTop = -top;
            movingLayoutParams.leftMargin = parentNewLeft;
            movingLayoutParams.topMargin = parentNewTop;
            movingLayout.setLayoutParams(movingLayoutParams);
        }
    }
    public synchronized void reflashWindowPosition() {
        if(true)
        return;
        updateNowWindowPosition(movingLayout);
        FrameLayout.LayoutParams movingLayoutParams = (FrameLayout.LayoutParams) movingLayout.getLayoutParams();
        int relateX = targetLeft - left;
        int relateY = targetTop - top;
        if (Math.abs(relateX) > windowMoveSpeed)
            movingLayoutParams.leftMargin = -(left + windowMoveSpeed * Math.abs(relateX) / relateX);
        else
            movingLayoutParams.leftMargin = -(left + relateX);

        if (Math.abs(relateY) > windowMoveSpeed)
            movingLayoutParams.topMargin = -(top + windowMoveSpeed * Math.abs(relateY) / relateY);
        else
            movingLayoutParams.topMargin = -(top + relateY);
        movingLayout.setLayoutParams(movingLayoutParams);

    }

//    @Override
//    public void run() {
//        autoUpdateNowWindowPosition();
//        autoOffsetWindow();
//    }
}
