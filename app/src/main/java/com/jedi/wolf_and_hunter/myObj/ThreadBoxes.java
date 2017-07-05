package com.jedi.wolf_and_hunter.myObj;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/5.
 */

public class ThreadBoxes {
    public static Thread serverConnectThread;
    public static Thread clientConnectThread;
    public static ArrayList<Thread> serverDealDataThreads=new ArrayList<Thread>();

    public static void clear(){
        if(serverConnectThread!=null) {
            serverConnectThread.interrupt();
            serverConnectThread=null;
        }
        if(clientConnectThread!=null) {
            clientConnectThread.interrupt();
            clientConnectThread=null;
        }
        for(Thread thread:serverDealDataThreads){
            if(thread.getState()!= Thread.State.TERMINATED) {
                thread.interrupt();
            }
        }
        serverDealDataThreads.clear();
    }

}
