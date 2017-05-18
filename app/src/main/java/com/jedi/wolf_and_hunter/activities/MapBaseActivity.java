package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jedi.wolf_and_hunter.myViews.MapBaseFrame;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.R;

public class MapBaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_base);
//        TextView t = new TextView(this);
//        t.setText("测试");
//        t.setTextColor(20);
//        t.setTextSize(500);
        MapBaseFrame mapBaseFrame=(MapBaseFrame)findViewById(R.id.map_base_frame);
        LinearLayout.LayoutParams paramsForMapBase = (LinearLayout.LayoutParams)mapBaseFrame.getLayoutParams();


        paramsForMapBase.width=2000;
        paramsForMapBase.height=3500;
        BaseCharacterView character = new BaseCharacterView(this);
        mapBaseFrame.addView(character);
        ViewGroup.LayoutParams params = character.getLayoutParams();
        if(params!=null) {
            params.height = 100;
            params.width = 100;
        }
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,LinearLayout.LayoutParams.WRAP_CONTENT);
//        character.setLayoutParams(params);
//        Button b =new Button(this);
//
//        b.setText("测试");
//        b.setOnClickListener(new View.OnClickListener() {
//                                 int i=0;
//            @Override
//                                 public void onClick(View v) {
//                ((Button)v).setText(i++);
//                                 }
//                             }
//
//
//        );
//        mapMainLayout.addView(b);



        character.setBackgroundColor(Color.parseColor("#FF0000"));

    }
}
