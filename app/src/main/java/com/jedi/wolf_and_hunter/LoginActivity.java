package com.jedi.wolf_and_hunter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jedi.wolf_and_hunter.activities.BlueToothTestActivity;
import com.jedi.wolf_and_hunter.activities.BluetoothOnlineActivity;
import com.jedi.wolf_and_hunter.activities.GameBaseAreaActivity;
import com.jedi.wolf_and_hunter.activities.MapBaseActivity;
import com.jedi.wolf_and_hunter.activities.WifiOnlineActivity;
import com.jedi.wolf_and_hunter.myObj.PlayerInfo;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;
import com.jedi.wolf_and_hunter.utils.BluetoothController;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    ArrayList<PlayerInfo> playerInfos;

    // UI references.
    private TableLayout tableLayout;
    private EditText mPasswordView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tableLayout=(TableLayout) findViewById(R.id.player_info_table);
        playerInfos=new ArrayList<PlayerInfo>();
        for(int i=0;i<4;i++){
            PlayerInfo playerInfo=new PlayerInfo(i+1);
            playerInfos.add(playerInfo);
            playerInfo.teamID=i+1;
        }



    }

    public void startTest(View view) {
        Intent i = new Intent(this, GameBaseAreaActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("playerInfos", playerInfos);
        bundle.putString("playMode", "single");
        i.putExtras(bundle);
        startActivity(i);
    }


    public void connectWithBluetooth(View view) {
        if(BluetoothController.getBluetoothStatus()==false) {
            BluetoothController.turnOnBluetooth(this, 0);
        }else {
            Intent i = new Intent(this, BluetoothOnlineActivity.class);
            startActivity(i);
        }
    }
    public void connectWithWifi(View view) {

            Intent i = new Intent(this, WifiOnlineActivity.class);
            startActivity(i);

    }


    public void onOffPlayer(View view) {
        TextView textView=(TextView)view;
        TableRow row=(TableRow) view.getParent();
        int playerID=Integer.parseInt((String) view.getTag());
        PlayerInfo playerInfo=playerInfos.get(playerID-1);
        if(playerInfo.isAvailable==true) {
            playerInfo.isAvailable=false;
            textView.setTextColor(Color.GRAY);
            row.getChildAt(1).setEnabled(false);
            row.getChildAt(2).setEnabled(false);
        }else{
            playerInfo.isAvailable=true;
            textView.setTextColor(Color.BLACK);
            row.getChildAt(1).setEnabled(true);
            row.getChildAt(2).setEnabled(true);
        }

    }

    public void changeCharacterType(View view) {
        Button button=(Button)view;
        TableRow row=(TableRow) view.getParent();
        int playerID=Integer.parseInt((String) view.getTag());
        PlayerInfo playerInfo=playerInfos.get(playerID-1);
        if(playerInfo.characterType== BaseCharacterView.CHARACTER_TYPE_HUNTER) {
            playerInfo.characterType=BaseCharacterView.CHARACTER_TYPE_WOLF;
            button.setText("狼");
        }else{
            playerInfo.characterType=BaseCharacterView.CHARACTER_TYPE_HUNTER;
            button.setText("猎人");
        }

    }
    public void changeTeam(View view) {
        Button button=(Button)view;
        TableRow row=(TableRow) view.getParent();
        int playerID=Integer.parseInt((String) view.getTag());
        PlayerInfo playerInfo=playerInfos.get(playerID-1);
        if(playerInfo.teamID<4) {
            playerInfo.teamID=playerInfo.teamID+1;
            button.setText(playerInfo.teamID+"队");
        }else{
            playerInfo.teamID=1;
            button.setText(playerInfo.teamID+"队");
        }

    }

}

