package com.jedi.wolf_and_hunter.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 */

public class WifiController {
    public static final String TAG = "WifiController";
    private static  WifiController wifiController = null;

    private List<WifiConfiguration> mWifiConfiguration; //无线网络配置信息类集合(网络连接列表)
    private List<ScanResult> mWifiList; //检测到接入点信息类 集合

    //描述任何Wifi连接状态
    private WifiInfo mWifiInfo;

    WifiManager.WifiLock mWifilock; //能够阻止wifi进入睡眠状态，使wifi一直处于活跃状态
    public WifiManager mWifiManager;

    /**
     * 获取该类的实例（懒汉）
     * @param context
     * @return
     */
    public static WifiController getInstance(Context context) {
        if(wifiController == null) {
            wifiController = new WifiController(context);
            return wifiController;
        }
        return null;
    }
    private WifiController(Context context) {
        //获取系统Wifi服务   WIFI_SERVICE
        this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //获取连接信息
        this.mWifiInfo = this.mWifiManager.getConnectionInfo();
    }

    /**
     * 是否存在网络信息
     * @param str  热点名称
     * @return
     */
    private WifiConfiguration isExsits(String str) {
        Iterator localIterator = this.mWifiManager.getConfiguredNetworks().iterator();
        WifiConfiguration localWifiConfiguration;
        do {
            if(!localIterator.hasNext()) return null;
            localWifiConfiguration = (WifiConfiguration) localIterator.next();
        }while(!localWifiConfiguration.SSID.equals("\"" + str + "\""));
        return localWifiConfiguration;
    }

    /**锁定WifiLock，当下载大文件时需要锁定 **/
    public void AcquireWifiLock() {
        this.mWifilock.acquire();
    }
    /**创建一个WifiLock**/
    public void CreateWifiLock() {
        this.mWifilock = this.mWifiManager.createWifiLock("Test");
    }
    /**解锁WifiLock**/
    public void ReleaseWifilock() {
        if(mWifilock.isHeld()) { //判断时候锁定
            mWifilock.acquire();
        }
    }


    /**打开Wifi**/
    public void OpenWifi() {
        if(!this.mWifiManager.isWifiEnabled()){ //当前wifi不可用
            this.mWifiManager.setWifiEnabled(true);
        }
    }
    /**关闭Wifi**/
    public void closeWifi() {
        if(mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }
    /**端口指定id的wifi**/
    public void disconnectWifi(int paramInt) {
        this.mWifiManager.disableNetwork(paramInt);
    }

    /**添加指定网络**/
    public void addNetwork(WifiConfiguration paramWifiConfiguration) {
        int i = mWifiManager.addNetwork(paramWifiConfiguration);
        mWifiManager.enableNetwork(i, true);
    }

    /**
     * 连接指定配置好的网络
     * @param index 配置好网络的ID
     */
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        //连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }

    /**
     * 根据wifi信息创建或关闭一个热点
     * @param config
     * @param paramBoolean 关闭标志
     */
    public void createWifiAP(WifiConfiguration config,boolean paramBoolean) {
        // 开启热点前，如果Wifi可用，先关闭Wifi
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
        Log.i(TAG, "into startWifiAp（） 启动一个Wifi 热点！");
        boolean ret = false;
        try {
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            ret = (Boolean) method.invoke(mWifiManager, config, paramBoolean);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.d(TAG, "stratWifiAp() IllegalArgumentException e");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "stratWifiAp() IllegalAccessException e");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.d(TAG, "stratWifiAp() InvocationTargetException e");
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, "stratWifiAp() SecurityException e");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.d(TAG, "stratWifiAp() NoSuchMethodException e");
        }
    }
    /**
     * 创建一个wifi信息
     * @param ssid 名称
     * @param password 密码
     * @param paramInt 有3个参数，1是无密码，2是简单密码，3是wap加密
     * @param type 是"ap"还是"wifi"
     * @return
     */
    public WifiConfiguration createWifiConfiguration(String ssid, String password,int paramInt, String type) {
        //配置网络信息类
        WifiConfiguration newWifiConfiguration = new WifiConfiguration();
        //设置配置网络属性
        newWifiConfiguration.allowedAuthAlgorithms.clear();
        newWifiConfiguration.allowedGroupCiphers.clear();
        newWifiConfiguration.allowedKeyManagement.clear();
        newWifiConfiguration.allowedPairwiseCiphers.clear();
        newWifiConfiguration.allowedProtocols.clear();

        if(type.equals("wifi")) { //wifi连接
            newWifiConfiguration.SSID = ("\"" + ssid + "\"");
            WifiConfiguration nowWifiConfiguration = isExsits(ssid);
            if(nowWifiConfiguration != null) {
                mWifiManager.removeNetwork(nowWifiConfiguration.networkId); //从列表中删除指定的网络配置网络
            }
            if(paramInt == 1) { //没有密码
                newWifiConfiguration.wepKeys[0] = "";
                newWifiConfiguration.allowedKeyManagement.set(0);
                newWifiConfiguration.wepTxKeyIndex = 0;
            } else if(paramInt == 2) { //简单密码
                newWifiConfiguration.hiddenSSID = true;
                newWifiConfiguration.wepKeys[0] = ("\"" + password + "\"");
                newWifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                newWifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            } else { //wap加密
                newWifiConfiguration.preSharedKey = ("\"" + password + "\"");
                newWifiConfiguration.hiddenSSID = true;
                newWifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                newWifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                newWifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                newWifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                newWifiConfiguration.status = WifiConfiguration.Status.ENABLED;

                newWifiConfiguration.allowedAuthAlgorithms.set(0);
                newWifiConfiguration.allowedGroupCiphers.set(2);
                newWifiConfiguration.allowedKeyManagement.set(1);
                newWifiConfiguration.allowedPairwiseCiphers.set(1);
                newWifiConfiguration.allowedGroupCiphers.set(3);
                newWifiConfiguration.allowedPairwiseCiphers.set(2);
            }
        }else {//"ap" wifi热点
            newWifiConfiguration.SSID = ssid;
            newWifiConfiguration.allowedAuthAlgorithms.set(1);
            newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            newWifiConfiguration.allowedKeyManagement.set(0);
            newWifiConfiguration.wepTxKeyIndex = 0;
            if (paramInt == 1) {  //没有密码
                newWifiConfiguration.wepKeys[0] = "";
                newWifiConfiguration.allowedKeyManagement.set(0);
                newWifiConfiguration.wepTxKeyIndex = 0;
            } else if (paramInt == 2) { //简单密码
                newWifiConfiguration.hiddenSSID = true;//网络上不广播ssid
                newWifiConfiguration.wepKeys[0] = password;
            } else if (paramInt == 3) {//wap加密
                newWifiConfiguration.hiddenSSID = true;
                newWifiConfiguration.preSharedKey = password;
                newWifiConfiguration.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.OPEN);
                newWifiConfiguration.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.TKIP);
                newWifiConfiguration.allowedKeyManagement
                        .set(WifiConfiguration.KeyMgmt.WPA_PSK);
                newWifiConfiguration.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
                // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                newWifiConfiguration.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.CCMP);
                newWifiConfiguration.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
                newWifiConfiguration.status = WifiConfiguration.Status.ENABLED;
            }
        }
        return newWifiConfiguration;
    }

    /**获取热点名**/
    public String getApSSID() {
        try {
            Method localMethod = this.mWifiManager.getClass().getDeclaredMethod("getWifiApConfiguration", new Class[0]);
            if (localMethod == null) return null;
            Object localObject1 = localMethod.invoke(this.mWifiManager,new Object[0]);
            if (localObject1 == null) return null;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
            if (localWifiConfiguration.SSID != null) return localWifiConfiguration.SSID;
            Field localField1 = WifiConfiguration.class .getDeclaredField("mWifiApProfile");
            if (localField1 == null) return null;
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localWifiConfiguration);
            localField1.setAccessible(false);
            if (localObject2 == null)  return null;
            Field localField2 = localObject2.getClass().getDeclaredField("SSID");
            localField2.setAccessible(true);
            Object localObject3 = localField2.get(localObject2);
            if (localObject3 == null) return null;
            localField2.setAccessible(false);
            String str = (String) localObject3;
            return str;
        } catch (Exception localException) {
        }
        return null;
    }

    /**获取wifi名**/
    public String getBSSID() {
        if (this.mWifiInfo == null)
            return "NULL";
        return this.mWifiInfo.getBSSID();
    }

    /**得到配置好的网络 **/
    public List<WifiConfiguration> getConfigurations() {
        return this.mWifiConfiguration;
    }

    /**获取ip地址**/
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }
    /**获取物理地址(Mac)**/
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    /**获取网络id**/
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }
    /**获取热点创建状态**/
    public int getWifiApState() {
        try {
            int i = ((Integer) this.mWifiManager.getClass()
                    .getMethod("getWifiApState", new Class[0])
                    .invoke(this.mWifiManager, new Object[0])).intValue();
            return i;
        } catch (Exception localException) {
        }
        return 4;   //未知wifi网卡状态
    }
    /**获取wifi连接信息**/
    public WifiInfo getWifiInfo() {
        return this.mWifiManager.getConnectionInfo();
    }
    /** 得到网络列表**/
    public List<ScanResult> getWifiList() {
        return this.mWifiList;
    }

    /**查看扫描结果**/
    public StringBuilder lookUpScan() {
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++)
        {
            localStringBuilder.append("Index_"+new Integer(i + 1).toString() + ":");
            //将ScanResult信息转换成一个字符串包
            //其中把包括：BSSID、SSID、capabilities、frequency、level
            localStringBuilder.append((mWifiList.get(i)).toString());
            localStringBuilder.append("\n");
        }
        return localStringBuilder;
    }

    /** 设置wifi搜索结果 **/
    public void setWifiList() {
        this.mWifiList = this.mWifiManager.getScanResults();
    }
    /**开始搜索wifi**/
    public void startScan() {
        this.mWifiManager.startScan();
    }
    /**得到接入点的BSSID**/
    public String GetBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }
}
