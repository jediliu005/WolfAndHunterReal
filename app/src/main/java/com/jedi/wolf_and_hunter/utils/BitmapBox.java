package com.jedi.wolf_and_hunter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jedi.wolf_and_hunter.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/24.
 */

public class BitmapBox {

    public static Map<String,Bitmap> landformBitmaps;
    public static void init(Context context){
        if(landformBitmaps==null){
            landformBitmaps=new HashMap<String,Bitmap>();
            Bitmap tallGrass= BitmapFactory.decodeResource(context.getResources(), R.drawable.tall_grass);
            landformBitmaps.put("tall_grass",tallGrass);
        }
    }
    public static Map<String,Bitmap> getLandformBitmaps(Context context){
        if(landformBitmaps==null){
            init(context);
        }
        return landformBitmaps;
    }
}
