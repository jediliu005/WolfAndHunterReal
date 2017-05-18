package com.jedi.wolf_and_hunter.myViews.landform;

import android.content.Context;
import android.graphics.Bitmap;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

/**
 * Created by Administrator on 2017/4/20.
 */

public interface  Landform {

    public Bitmap getBitmap(Context context);
    public  void effect(BaseCharacterView character);
    public  void removeEffect(BaseCharacterView character);
}
