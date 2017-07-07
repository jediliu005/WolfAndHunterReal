package com.jedi.wolf_and_hunter.myObj;

/**
 * Created by Administrator on 2017/7/7.
 */

public class DataExchangeThread extends Thread {
    public static final int STATE_NONE=0;
    public static final int STATE_WAITING_FOR_GAME=1;
    public static final int STATE_GAME_RUNNING=0;
    public int state=0;
}
