package com.jedi.wolf_and_hunter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.jedi.wolf_and_hunter.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/24.
 */

public class BitmapBox {
    //下面一行控制bitmap是否自适应分辨率，不强制设flase可能出现图片分辨率和draw分辨率不一致
    private static BitmapFactory.Options option = new BitmapFactory.Options();

    {
        option.inScaled = false;
    }
    public static Map<String,Bitmap> landformBitmaps;
    public static Map<String,Bitmap> characterBitmaps;
    public static Map<String,Bitmap> otherBitmaps;
    public static void init(Context context){
        if(landformBitmaps==null){
            landformBitmaps=new HashMap<String,Bitmap>();
            Bitmap tallGrass= BitmapFactory.decodeResource(context.getResources(), R.drawable.tall_grass);
            landformBitmaps.put("tallGrass",tallGrass);
        }



        if(characterBitmaps==null){
            characterBitmaps=new HashMap<String,Bitmap>();

            Bitmap oriNormalHunterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.normal_hunter, option);
            characterBitmaps.put("oriNormalHunter",oriNormalHunterBitmap);

            Bitmap oriNormalWolfBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.normal_wolf, option);
            characterBitmaps.put("oriNormalWolf",oriNormalWolfBitmap);
        }



        if(otherBitmaps==null){
            otherBitmaps=new HashMap<String,Bitmap>();

            Bitmap bloodOfMyCharacterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blood, option);
            otherBitmaps.put("oriBloodOfMyCharacter",bloodOfMyCharacterBitmap);

            Bitmap bloodOfOtherCharacterBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blood2, option);
            otherBitmaps.put("oriBloodOfOtherCharacter",bloodOfOtherCharacterBitmap);
        }
    }
    public static Bitmap getLandformBitmap(Context context,String bitmapName){
        if(landformBitmaps==null){
            init(context);
        }
        return landformBitmaps.get(bitmapName);
    }
    public static Bitmap getCharacterBitmap(Context context,String bitmapName){
        if(characterBitmaps==null){
            init(context);
        }
        return characterBitmaps.get(bitmapName);
    }
    public static Bitmap getOtherBitmap(Context context,String bitmapName){
        if(otherBitmaps==null){
            init(context);
        }
        return otherBitmaps.get(bitmapName);
    }
}
