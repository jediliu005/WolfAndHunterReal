package com.jedi.wolf_and_hunter.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Wifi热点工具类
 *
 * @author lizhipeng
 */
public class WifiHotspotController {

    public static final String TAG = "WifiHotspotController";
    //描述任何Wifi连接状态
    public static WifiManager mWifiManager;


    /**
     * 创建Wifi热点
     *
     * @param wifiManager Wifi管理器
     * @param config      Wifi配置信息
     * @param isEnable      true为开启Wifi热点，false为关闭
     * @return 返回开启成功状态，true为成功，false为失败
     */
    public static boolean createWifiAP(WifiManager wifiManager, WifiConfiguration config, boolean isEnable) {
        // 开启热点前，如果Wifi可用，先关闭Wifi
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 马鹿的HTC自己搞了个mWifiApProfile字段存热点数据，TM还要手动适配才能指定热点名称和密码！！
        boolean isHtc = false;
        try {
            isHtc = WifiConfiguration.class
                    .getDeclaredField("mWifiApProfile") != null;
        } catch (java.lang.NoSuchFieldException e) {
            isHtc = false;
        }
        if (isHtc) {
            setHTCSSID(config);
        }

        Log.i(TAG, "into startWifiAp（） 启动一个Wifi 热点！");
        boolean ret = false;
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, Boolean.TYPE);

            ret = (Boolean) method.invoke(wifiManager,config, isEnable);
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
        Log.i(TAG, "out startWifiAp（） 启动一个Wifi 热点！");
        return ret;
    }

    //给HTC大爷做的setting适配
    public static void setHTCSSID(WifiConfiguration config) {
        try {
            Field mWifiApProfileField = WifiConfiguration.class
                    .getDeclaredField("mWifiApProfile");
            mWifiApProfileField.setAccessible(true);
            Object hotSpotProfile = mWifiApProfileField.get(config);
            mWifiApProfileField.setAccessible(false);


            if (hotSpotProfile != null) {
                Field ssidField = hotSpotProfile.getClass().getDeclaredField(
                        "SSID");
                ssidField.setAccessible(true);
                ssidField.set(hotSpotProfile, config.SSID);
                ssidField.setAccessible(false);


                Field localField3 = hotSpotProfile.getClass().getDeclaredField(
                        "key");
                localField3.setAccessible(true);
                localField3.set(hotSpotProfile, config.preSharedKey);
                localField3.setAccessible(false);


                Field localField6 = hotSpotProfile.getClass().getDeclaredField(
                        "dhcpEnable");
                localField6.setAccessible(true);
                localField6.setInt(hotSpotProfile, 1);
                localField6.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 配置Wifi或者热点信息
     *
     * @param wifiManager Wifi管理器
     * @param ssid        Wifi名称
     * @param password    Wifi密码
     * @param paramInt    Wifi加密方式 1为不加密，2为WEP加密，3为wpa加密
     * @param wifiType    “wifi”为打开普通Wifi连接，“ap”为创建Wifi热点
     * @return
     */
    public static WifiConfiguration createWifiConfiguration(WifiManager wifiManager,
                                                   String ssid, String password, int paramInt, String wifiType) {
        WifiConfiguration newConfig = new WifiConfiguration();
        newConfig.allowedAuthAlgorithms.clear();
        newConfig.allowedGroupCiphers.clear();
        newConfig.allowedKeyManagement.clear();
        newConfig.allowedPairwiseCiphers.clear();
        newConfig.allowedProtocols.clear();

        if ("wifi".equals(wifiType)) {
            newConfig.SSID = ("\"" + ssid + "\"");
            WifiConfiguration nowConfig = isExsits(wifiManager, ssid);
            if (nowConfig != null) {
                if (wifiManager != null) {
                    wifiManager.removeNetwork(nowConfig.networkId);
                }
            }
            if (paramInt == 1) {
                newConfig.wepKeys[0] = "";
                newConfig.allowedKeyManagement.set(0);
                newConfig.wepTxKeyIndex = 0;
                return newConfig;
            } else if (paramInt == 2) {
                newConfig.hiddenSSID = false;
                newConfig.wepKeys[0] = ("\"" + password + "\"");
                return newConfig;
            } else {
                newConfig.preSharedKey = ( "\"" + password + "\"");
                newConfig.hiddenSSID = false;

                newConfig.allowedAuthAlgorithms.set(0);
                newConfig.allowedGroupCiphers.set(2);
                newConfig.allowedKeyManagement.set(1);
                newConfig.allowedPairwiseCiphers.set(1);
                newConfig.allowedGroupCiphers.set(3);
                newConfig.allowedPairwiseCiphers.set(2);
                return newConfig;
            }
        } else {

            newConfig.SSID = ssid;


            if (paramInt == 1) // WIFICIPHER_NOPASS 不加密
            {
                newConfig.wepKeys[0] = "";
                newConfig.allowedKeyManagement
                        .set(WifiConfiguration.KeyMgmt.NONE);
                newConfig.wepTxKeyIndex = 0;
                return newConfig;
            }
            if (paramInt == 2) // WIFICIPHER_WEP WEP加密
            {
                newConfig.hiddenSSID = false;
                newConfig.wepKeys[0] = password;
                newConfig.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.SHARED);
                newConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.CCMP);
                newConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.TKIP);
                newConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.WEP40);
                newConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.WEP104);
                newConfig.allowedKeyManagement
                        .set(WifiConfiguration.KeyMgmt.NONE);
                newConfig.wepTxKeyIndex = 0;
                return newConfig;
            }
            if (paramInt == 3) // WIFICIPHER_WPA wpa加密
            {
                newConfig.preSharedKey = password;
                newConfig.hiddenSSID = false;
                newConfig.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.OPEN);
                newConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.TKIP);
                newConfig.allowedKeyManagement
                        .set(WifiConfiguration.KeyMgmt.WPA_PSK);
                newConfig.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
                // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                newConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.CCMP);
                newConfig.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
                newConfig.status = WifiConfiguration.Status.ENABLED;
                return newConfig;
            }
        }
        return null;
    }

    /**
     * 连接Wifi热点
     *
     * @param wifiManager Wifi管理器
     * @param wifiConfig  需要连接的Wifi网络的配置对象
     * @return 返回Wifi热点是否连接成功
     */
    public static boolean connectHotspot(WifiManager wifiManager,
                                         WifiConfiguration wifiConfig) {
        Log.i(TAG, "into enableNetwork(WifiConfiguration wifiConfig)");

        // 新创建的网络配置的id
        int wcgID = wifiManager.addNetwork(wifiConfig);
        Log.i(TAG, "into enableNetwork(WifiConfiguration wifiConfig) wcID = "
                + wcgID);

        if (wcgID < 0) {
            return false;
        }
        Log.i(TAG, "out enableNetwork(WifiConfiguration wifiConfig)");
        return wifiManager.enableNetwork(wcgID, true);

    }

    /**
     * 利用反射，调用Wifi热点链接方法
     *
     * @param wifiManager
     * @param wifiName    热点名
     * @param password    热点密码
     * @return
     */
    @SuppressWarnings(value = "uncheck")
    public static boolean connectHotPointByNameAndPassword(WifiManager wifiManager,
                                                    String wifiName, String password) {
        Method getWifiConfig;
        WifiConfiguration myConfig;
        try {
            getWifiConfig = wifiManager.getClass().getMethod(
                    "getWifiApConfiguration", new Class[0]);

            myConfig = (WifiConfiguration) getWifiConfig.invoke(wifiManager, new Object[]{});

            myConfig.SSID = "\"" + wifiName + "\"";
            myConfig.preSharedKey = "\"" + password + "\"";

            Method setWifiConfig = wifiManager.getClass().getMethod(
                    "setWifiApConfiguration", WifiConfiguration.class);
            setWifiConfig.invoke(wifiManager, new Object[]{myConfig, true});

            Method enableWifi = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, boolean.class);
            enableWifi.invoke(wifiManager, null, false);
            WifiConfiguration newConfiguration = (WifiConfiguration) wifiManager
                    .getClass().getMethod("getWifiApConfiguration", new Class[]{})
                    .invoke(wifiManager, new Object[]{});
            return (Boolean) enableWifi.invoke(wifiManager, newConfiguration,
                    true);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    public static boolean connectHotPointByReflect(WifiManager wifiManager,
                                                     WifiConfiguration wifiConfig) {
        try {
            Method enableWifi = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, boolean.class);
            enableWifi.invoke(wifiManager, null, false);
            return (Boolean) enableWifi.invoke(wifiManager, wifiConfig, true);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 关闭Wifi热点
     *
     * @param wifiManager Wifi管理器
     * @return 返回关闭状态
     */
    public static boolean closeConnection(WifiManager wifiManager) {
        Log.i(TAG, "into closeWifiAp（） 关闭一个Wifi 热点！");
        boolean ret = false;
        if (isWifiApEnabled(wifiManager)) {
            try {
                Method method = wifiManager.getClass().getMethod(
                        "getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method
                        .invoke(wifiManager);
                Method method2 = wifiManager.getClass().getMethod(
                        "setWifiApEnabled", WifiConfiguration.class,
                        boolean.class);
                ret = (Boolean) method2.invoke(wifiManager, config, false);
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.i(TAG, "out closeWifiAp（） 关闭一个Wifi 热点！");
        return ret;
    }

    /**
     * 检测Wifi热点是否可用
     *
     * @param wifiManager Wifi管理器
     * @return 是否可用状态
     */
    public static boolean isWifiApEnabled(WifiManager wifiManager) {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 通过反射，获取Wifi热点名称 SSID
     *
     * @param wifiManager
     * @return
     */
    public static String getApSSID(WifiManager wifiManager) {
        try {
            Method localMethod = wifiManager.getClass().getDeclaredMethod(
                    "getWifiApConfiguration", new Class[0]);
            if (localMethod == null)
                return null;
            Object localObject1 = localMethod
                    .invoke(wifiManager, new Object[0]);
            if (localObject1 == null)
                return null;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
            if (localWifiConfiguration.SSID != null)
                return localWifiConfiguration.SSID;
            Field localField1 = WifiConfiguration.class
                    .getDeclaredField("mWifiApProfile");
            if (localField1 == null)
                return null;
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localWifiConfiguration);
            localField1.setAccessible(false);
            if (localObject2 == null)
                return null;
            Field localField2 = localObject2.getClass()
                    .getDeclaredField("SSID");
            localField2.setAccessible(true);
            Object localObject3 = localField2.get(localObject2);
            if (localObject3 == null)
                return null;
            localField2.setAccessible(false);
            String str = (String) localObject3;
            return str;
        } catch (Exception localException) {
        }
        return null;
    }

    /**
     * 获取Wifi热点的状态
     *
     * @param wifiManager Wifi管理器
     * @return
     */
    public int getWifiApState(WifiManager wifiManager) {
        try {
            int i = ((Integer) wifiManager.getClass()
                    .getMethod("getWifiApState", new Class[0])
                    .invoke(wifiManager, new Object[0])).intValue();
            return i;
        } catch (Exception localException) {
        }
        return 4; // 未知wifi网卡状态
    }

    /**
     * 判断选择的Wifi热点是否可以连接
     *
     * @param ssid     Wifi热点名 SSID
     * @param wifiList 附近的Wifi列表
     * @return true 可以连接 false 不可以连接（不在范围内）
     */
    public static boolean checkCoonectHotIsEnable(String ssid,
                                                  List<ScanResult> wifiList) {
        for (ScanResult result : wifiList) {
            if (result.SSID.equals(ssid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查wifi列表中是否有以输入参数为名的wifi热点，如果存在，则在开始配置wifi网络之前将其移除，以避免ssid的重复
     *
     * @param wifiManager
     * @param paramString
     * @return
     */
    private static WifiConfiguration isExsits(WifiManager wifiManager,
                                              String paramString) {
        Log.e("","");
        List<WifiConfiguration> configs=wifiManager.getConfiguredNetworks();
        if(configs==null)
            return null;
        Iterator<WifiConfiguration> localIterator = configs.iterator();
        WifiConfiguration localWifiConfiguration;
        do {
            if (!localIterator.hasNext())
                return null;
            localWifiConfiguration = (WifiConfiguration) localIterator.next();
        } while (!localWifiConfiguration.SSID.equals("\"" + paramString + "\""));
        return localWifiConfiguration;
    }

    public static List<WifiConfiguration> getWifiConfigurations(
            WifiManager wifiManager) {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        return existingConfigs;
    }

    /**
     * 创建一个wifi信息
     * @param ssid 名称
     * @param password 密码
     * @param paramInt 有3个参数，1是无密码，2是简单密码，3是wap加密
     * @param type 是"ap"还是"wifi"
     * @return
     */
    public WifiConfiguration createWifiConfigurationByParams(WifiManager wifiManager,String ssid,String password,int paramInt, String type) throws Exception {

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
            WifiConfiguration nowWifiConfiguration = isExsits(wifiManager,ssid);
            if(nowWifiConfiguration != null) {
                wifiManager.removeNetwork(nowWifiConfiguration.networkId); //从列表中删除指定的网络配置网络
            }
            if(paramInt == 1) { //没有密码
                newWifiConfiguration.wepKeys[0] = "";
                newWifiConfiguration.allowedKeyManagement.set(0);
                newWifiConfiguration.wepTxKeyIndex = 0;
            } else if(paramInt == 2) { //简单密码
                newWifiConfiguration.hiddenSSID = false;
                newWifiConfiguration.wepKeys[0] = ("\"" + password + "\"");
                newWifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                newWifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                newWifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            } else { //wap加密
                newWifiConfiguration.preSharedKey = ("\"" + password + "\"");
                newWifiConfiguration.hiddenSSID = false;
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
                newWifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                newWifiConfiguration.wepTxKeyIndex = 0;
            } else if (paramInt == 2) { //简单密码
                newWifiConfiguration.hiddenSSID = false;//网络上不广播ssid
                newWifiConfiguration.wepKeys[0] = password;
            } else if (paramInt == 3) {//wap加密
                newWifiConfiguration.hiddenSSID = false;
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



    /**
     * 打开Wifi
     **/
    public static boolean  OpenWifi() {
        if (mWifiManager!=null&&mWifiManager.isWifiEnabled() == false) { //当前wifi不可用
            mWifiManager.setWifiEnabled(true);
        }
        return false;
    }

    /**
     * 关闭Wifi
     **/
    public static boolean  closeWifi() {
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
        return false;
    }

    /**
     * 端口指定id的wifi
     **/
    public static boolean disconnectWifi(int paramInt) {
        if (mWifiManager != null){
           return mWifiManager.disableNetwork(paramInt);

        }
        return false;
    }

    public static WifiManager getWifiManager(Context context) {
        if (mWifiManager == null)
            initParams(context);

        return mWifiManager;

    }

    public static void initParams(Context context) {
        //获取系统Wifi服务   WIFI_SERVICE
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

    }

    public static String getCleanSSID(@NonNull WifiManager wifiManager){
        WifiInfo wifiInfo =wifiManager.getConnectionInfo();
        String ssid=wifiInfo.getSSID();
        if(ssid.startsWith("\"")&&ssid.endsWith("\"")){
            ssid=ssid.substring(1,ssid.length()-1);
        }
        return ssid;
    }
}