package com.jedi.wolf_and_hunter.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.jedi.wolf_and_hunter.R;
import com.jedi.wolf_and_hunter.engine.GameMainEngine;
import com.jedi.wolf_and_hunter.myObj.gameObj.GameInfo;
import com.jedi.wolf_and_hunter.myObj.gameObj.MyVirtualWindow;
import com.jedi.wolf_and_hunter.myViews.mapBase.GameMap;
import com.jedi.wolf_and_hunter.myViews.mapBase.MapBaseFrame;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

public class GameBaseAreaActivity extends Activity {

    public static MapBaseFrame mapBaseFrame;
    public static BaseCharacterView myCharacter;
    public static FrameLayout baseFrame;
    public static GameInfo gameInfo;
    public static GameMap gameMap;
    public static MyVirtualWindow virtualWindow;
    public static GameMainEngine engine;
    BluetoothAdapter bluetoothAdapter;
    BluetoothServerSocket bluetoothServerSocket;
    BluetoothSocket bluetoothSocket;
boolean isFirstBoot=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_base_area);
//        initBluetoothConnection();

        engine =new GameMainEngine(this);
        gameInfo = engine.getGameInfo();
        gameMap = engine.getGameMap();
        baseFrame = engine.getBaseFrame();
        mapBaseFrame = engine.getMapBaseFrame();
        virtualWindow=engine.getVirtualWindow();
        engine.runEngine();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isFirstBoot==false) {
            if (engine == null) {
                engine = new GameMainEngine(this);
                gameInfo = engine.getGameInfo();
                gameMap = engine.getGameMap();
                baseFrame = engine.getBaseFrame();
                mapBaseFrame = engine.getMapBaseFrame();
                virtualWindow = engine.getVirtualWindow();
                engine.runEngine();
            }
            for (BaseCharacterView character : gameInfo.allCharacters) {
                character.initBitmapAndMedia();
                if (character.drawThread == null || character.drawThread.getState() == Thread.State.TERMINATED) {
                    character.runDrawThread();
                }
            }
        }
        isFirstBoot=false;



    }

    /**
     * Called when you are no longer visible to the user.  You will next
     * receive either {@link #onRestart}, {@link #onDestroy}, or nothing,
     * depending on later user activity.
     * <p>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestart
     * @see #onResume
     * @see #onSaveInstanceState
     * @see #onDestroy
     */
//   tim
    @Override
    protected void onDestroy() {
        engine.stopEngine();

        super.onDestroy();

    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        engine.stopEngine();
        super.onBackPressed();

    }
}
