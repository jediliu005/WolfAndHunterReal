package com.jedi.wolf_and_hunter.myViews.landform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;


/**
 * Created by Administrator on 2017/4/24.
 */

public class DynamicLandform  extends View implements Landform {
    int width;
    int height;
    int Top;
    int Left;
    Landform[][] landformses;

    public DynamicLandform(Context context) {
        super(context);
        init();
    }

    public DynamicLandform(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
    }


    @Override
    public Bitmap getBitmap(Context context) {
        return null;
    }

    @Override
    public void effect(BaseCharacterView character) {

    }

    @Override
    public void removeEffect(BaseCharacterView character) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



    }
}
