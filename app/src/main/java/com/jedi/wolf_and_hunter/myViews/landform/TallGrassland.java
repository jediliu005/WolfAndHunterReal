package com.jedi.wolf_and_hunter.myViews.landform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.utils.BitmapBox;

/**
 * Created by Administrator on 2017/4/20.
 */

public class TallGrassland extends DynamicLandform{
    private static Bitmap bitmap;

    public TallGrassland(Context context) {
        super(context);
        init();
    }

    public TallGrassland(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void init() {
        super.init();
        if(bitmap==null) {
            bitmap = BitmapBox.getLandformBitmaps(getContext()).get("tall_grass");
            Matrix matrix = new Matrix();
            matrix.postScale((float) 100 / bitmap.getWidth(), (float) 100 / bitmap.getHeight());
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
    }

    @Override
    public Bitmap getBitmap(Context context) {
        if(bitmap==null){
            BitmapBox.init(context);
            bitmap=BitmapBox.landformBitmaps.get("tallGrass");
        }
        return bitmap;
    }

    @Override
    public  void effect(BaseCharacterView character) {

            if (character.nowHiddenLevel < BaseCharacterView.HIDDEN_LEVEL_HIGHT_HIDDEN)
                character.nowHiddenLevel = BaseCharacterView.HIDDEN_LEVEL_HIGHT_HIDDEN;

    }

    @Override
    public void removeEffect(BaseCharacterView character) {

            if (character.nowHiddenLevel ==BaseCharacterView.HIDDEN_LEVEL_HIGHT_HIDDEN)
                character.nowHiddenLevel = character.defaultHiddenLevel;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap,0,0,null);
    }
}
