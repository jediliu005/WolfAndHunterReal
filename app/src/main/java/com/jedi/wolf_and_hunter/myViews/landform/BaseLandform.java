package com.jedi.wolf_and_hunter.myViews.landform;

import android.content.Context;
import android.graphics.Bitmap;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

/**
 * Created by Administrator on 2017/4/24.
 */

public class BaseLandform implements Landform {
    int width;
    int height;
    int Top;
    int Left;
    Landform[][] landformses;

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
}
