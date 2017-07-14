package com.jedi.wolf_and_hunter.myObj.onlineObj;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.jedi.wolf_and_hunter.activities.WifiOnlineActivity;
import com.jedi.wolf_and_hunter.myObj.gameObj.PlayerInfo;
import com.jedi.wolf_and_hunter.myViews.characters.BaseCharacterView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/5.
 */

public class ConnectThreadBox {
    public static final int CONNECT_ROLE_NONE = 0;
    public static final int CONNECT_ROLE_SERVER = 1;
    public static final int CONNECT_ROLE_CLIENT = 2;
    public static boolean isServerRunning=false;
    public static boolean isClientRunning=false;
    public static Thread serverConnectThread;
    public static Thread clientConnectThread;
    public static ArrayList<Thread> serverDealDataThreads=new ArrayList<Thread>();
    public static int nowRole=0;

    public static void startWifiConnectThread(Context context, WifiOnlineActivity.MyWifiHandler myWifiHandler,WifiManager wifiManager){
        clear();
        WifiClientThread clientThread = new WifiClientThread(myWifiHandler,wifiManager);
        clientThread.start();
        clientConnectThread = clientThread;
        Toast.makeText(context, "开始向服务器发送数据", Toast.LENGTH_SHORT).show();
       
    }

    public static void startWifiServerThread(Context context, WifiOnlineActivity.MyWifiHandler myWifiHandler,WifiManager wifiManager){
        clear();
        WifiServerThread acceptThread = new WifiServerThread(myWifiHandler,wifiManager);
        acceptThread.start();
        serverConnectThread = acceptThread;
        Toast.makeText(context, "服务器开始接收数据", Toast.LENGTH_SHORT).show();

    }

    
    public static class WifiServerThread extends DataExchangeThread {

        WifiOnlineActivity.MyWifiHandler myWifiHandler;
        WifiManager wifiManager;
        public WifiServerThread(WifiOnlineActivity.MyWifiHandler wifiHandler, WifiManager wifiManager){
            this.myWifiHandler=wifiHandler;
            this.wifiManager=wifiManager;
        }

        @Override
        public void run() {
            super.run();
            isServerRunning=true;
            //不断监听直到返回连接或者发生异常
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8086);
                while (isServerRunning) {
                    Socket socket = serverSocket.accept();

                    if (socket != null) {
                        state=STATE_WAITING_FOR_GAME;
                        WifiServerDealDataThread sddt = new WifiServerDealDataThread(socket);
                        sddt.start();
                        serverDealDataThreads.add(sddt);
                    }


//                    manageConnectedSocket(socket);
                }
            } catch (Exception e) {
                nowRole=CONNECT_ROLE_NONE;
                Log.e("AcceptThread", "哎呀，当个服务器不容易啊，不知干嘛又挂了。。。。。。。。。。。");
                myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.ACCEPT_FAIL);
            } finally {

                try {
                    if (serverSocket != null)
                        serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        /**
         * 取消正在监听的接口
         */


        class WifiServerDealDataThread extends Thread {
            Socket socket;

            public WifiServerDealDataThread(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void run() {


                InputStream is = null;
                OutputStream os = null;

                try {
                    while (isServerRunning) {
                        os = socket.getOutputStream();
                        is = socket.getInputStream();

                        if(state==STATE_WAITING_FOR_GAME){
                            is.read();
                        }
                        ObjectInputStream ois = new ObjectInputStream(is);
                        PlayerInfo pi = (PlayerInfo) ois.readObject();
                        if (pi != null)
                            myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.ACCEPT_SUCCESS);
                        ObjectOutputStream oos = new ObjectOutputStream(os);
                        PlayerInfo myPlayerInfo = new PlayerInfo(true, 1, BaseCharacterView.CHARACTER_TYPE_HUNTER, 1, "", true);
                        oos.writeObject(myPlayerInfo);
                        oos.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.ONE_ACCEPT_FAIL);
//                    Log.e("ServerDealDataThread", e.getMessage());
                } finally {

                }
            }
        }


    }


//    public static void startTempServerThread(Context context, WifiOnlineActivity.MyWifiHandler myWifiHandler){
//        clear();
//        ServerThread acceptThread = new ServerThread(myWifiHandler);
//        acceptThread.start();
//        serverConnectThread = acceptThread;
//        Toast.makeText(context, "服务器开始接收数据", Toast.LENGTH_SHORT).show();
//
//    }
//    public static void startTempConnectThread(Context context, WifiOnlineActivity.MyWifiHandler myWifiHandler){
//        clear();
//        ClientThread clientThread = new ClientThread(myWifiHandler);
//        clientThread.start();
//        clientConnectThread = clientThread;
//        Toast.makeText(context, "开始向服务器发送数据", Toast.LENGTH_SHORT).show();
//
//    }

//    public static class ServerThread extends DataExchangeThread {
//
//        WifiOnlineActivity.MyWifiHandler myWifiHandler;
//        public ServerThread(WifiOnlineActivity.MyWifiHandler wifiHandler){
//            this.myWifiHandler=wifiHandler;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            isServerRunning=true;
//            //不断监听直到返回连接或者发生异常
//            ServerSocket serverSocket = null;
//            try {
//                serverSocket = new ServerSocket(8086);
//                while (isServerRunning) {
//                    Socket socket = serverSocket.accept();
//
//                    if (socket != null) {
//                        state=STATE_WAITING_FOR_GAME;
//                        WifiServerDealDataThread sddt = new WifiServerDealDataThread(socket);
//                        sddt.start();
//                        serverDealDataThreads.add(sddt);
//                    }
//
//
////                    manageConnectedSocket(socket);
//                }
//            } catch (Exception e) {
//                nowRole=CONNECT_ROLE_NONE;
//                Log.e("AcceptThread", "哎呀，当个服务器不容易啊，不知干嘛又挂了。。。。。。。。。。。");
//                myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.ACCEPT_FAIL);
//            } finally {
//
//                try {
//                    if (serverSocket != null)
//                        serverSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//        /**
//         * 取消正在监听的接口
//         */
//
//
//        class WifiServerDealDataThread extends Thread {
//            Socket socket;
//
//            public WifiServerDealDataThread(Socket socket) {
//                this.socket = socket;
//            }
//
//            @Override
//            public void run() {
//
//
//                InputStream is = null;
//                OutputStream os = null;
//
//                try {
//                    while (isServerRunning) {
//                        os = socket.getOutputStream();
//                        is = socket.getInputStream();
//
//                        if(state==STATE_WAITING_FOR_GAME){
//                            is.read();
//                        }
//                        ObjectInputStream ois = new ObjectInputStream(is);
//                        PlayerInfo pi = (PlayerInfo) ois.readObject();
//                        if (pi != null)
//                            myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.ACCEPT_SUCCESS);
//                        ObjectOutputStream oos = new ObjectOutputStream(os);
//                        PlayerInfo myPlayerInfo = new PlayerInfo(true, 1, BaseCharacterView.CHARACTER_TYPE_HUNTER, 1, "", true);
//                        oos.writeObject(myPlayerInfo);
//                        oos.flush();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.ONE_ACCEPT_FAIL);
////                    Log.e("ServerDealDataThread", e.getMessage());
//                } finally {
//
//                }
//            }
//        }
//
//
//
//    }

//    public static class ClientThread extends Thread {
//
//
//        WifiOnlineActivity.MyWifiHandler myWifiHandler;
//        public ClientThread(WifiOnlineActivity.MyWifiHandler wifiHandler){
//            this.myWifiHandler=wifiHandler;
//        }
//        @Override
//        public void run() {
//            isClientRunning=true;
//            super.run();
//            //取消搜索因为搜索会让连接变慢
//            OutputStream os = null;
//            InputStream is = null;
//            Socket socket = null;
//            try {
//
////
//                InetAddress ia = InetAddress.getByName("192.168.0.104");
//
//                socket = new Socket(ia, 8086);
////                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(mUUID));
//                //通过socket连接设备，这是一个阻塞操作，知道连接成功或发生异常
//
//
//                os = socket.getOutputStream();
//                is = socket.getInputStream();
//                while (isClientRunning) {
//                    ObjectOutputStream oos = new ObjectOutputStream(os);
//                    PlayerInfo myPlayerInfo = new PlayerInfo(true, 1, BaseCharacterView.CHARACTER_TYPE_HUNTER, 1, "", false);
//                    oos.writeObject(myPlayerInfo);
//                    oos.flush();
//                    ObjectInputStream ois = new ObjectInputStream(is);
//                    PlayerInfo serverPlayerInfo = (PlayerInfo) ois.readObject();
//                    if (serverPlayerInfo != null)
//                        myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.CONNECT_SUCCESS);
//                }
//
//            } catch (Exception e) {
//                Log.e("ClientThread", "他妈的，当个客户端不容易啊，服务器又不理我了。。。。。。。。。。。");
//                myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.CONNECT_FAIL);
//
//            } finally {
//                try {
//                    if (is != null)
//                        is.close();
//                    if (os != null)
//                        os.close();
//                    if (socket != null && socket.isClosed() == false)
//                        socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//
//            //管理连接(在独立的线程)
//            // manageConnectedSocket(mmSocket);
//        }
//
//
//    }

   public static class WifiClientThread extends Thread {

        WifiOnlineActivity.MyWifiHandler myWifiHandler;
        WifiManager wifiManager;
        public WifiClientThread(WifiOnlineActivity.MyWifiHandler wifiHandler, WifiManager wifiManager){
            this.myWifiHandler=wifiHandler;
            this.wifiManager=wifiManager;
        }
        @Override
        public void run() {
            isClientRunning=true;
            super.run();
            //取消搜索因为搜索会让连接变慢
            OutputStream os = null;
            InputStream is = null;
            Socket socket = null;
            try {
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                String ssid= WifiHotspotController.getCleanSSID(wifiManager);
//                if (wifiInfo == null || ssid.equals(hotSpotSSID) == false) {
//                    myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.CONNECT_WRONG_HOTSPOT);
//                    nowRole = CONNECT_ROLE_NONE;
//                    return;
//                }
                int serverIP = dhcpInfo.gateway;
                //需要翻转数组，别问我为什么不用Formater那个方法，因为过期了
                byte[] ipAddressArray = BigInteger.valueOf(serverIP).toByteArray();
                int length = ipAddressArray.length;
                byte[] reverseIpAddressArray = new byte[length];
                for (int i = 0; i < length; i++) {
                    reverseIpAddressArray[length - 1 - i] = ipAddressArray[i];
                }
                InetAddress ia = InetAddress.getByAddress(reverseIpAddressArray);

                socket = new Socket(ia, 8086);
//                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(mUUID));
                //通过socket连接设备，这是一个阻塞操作，知道连接成功或发生异常


                os = socket.getOutputStream();
                is = socket.getInputStream();
                while (isClientRunning) {
                    ObjectOutputStream oos = new ObjectOutputStream(os);
                    PlayerInfo myPlayerInfo = new PlayerInfo(true, 1, BaseCharacterView.CHARACTER_TYPE_HUNTER, 1, "", false);
                    oos.writeObject(myPlayerInfo);
                    oos.flush();
                    ObjectInputStream ois = new ObjectInputStream(is);
                    PlayerInfo serverPlayerInfo = (PlayerInfo) ois.readObject();
                    if (serverPlayerInfo != null)
                        myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.CONNECT_SUCCESS);
                }

            } catch (Exception e) {
                Log.e("ClientThread", "他妈的，当个客户端不容易啊，服务器又不理我了。。。。。。。。。。。");
                myWifiHandler.sendEmptyMessage(WifiOnlineActivity.MyWifiHandler.CONNECT_FAIL);

            } finally {
                try {
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                    if (socket != null && socket.isClosed() == false)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            //管理连接(在独立的线程)
            // manageConnectedSocket(mmSocket);
        }


    }
    public static void clear(){
        isClientRunning=false;
        isServerRunning=false;
        if(serverConnectThread!=null&&serverConnectThread.getState()!= Thread.State.TERMINATED) {
            serverConnectThread.interrupt();
            serverConnectThread=null;
        }
        if(clientConnectThread!=null&&clientConnectThread.getState()!= Thread.State.TERMINATED) {
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
