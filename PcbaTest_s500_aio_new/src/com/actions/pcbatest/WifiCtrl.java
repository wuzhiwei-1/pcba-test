package com.actions.pcbatest;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.renderscript.Sampler.Value;
import android.text.format.Formatter;
import android.util.Log;

public class WifiCtrl {
    private final static String TAG = "WifiCtrl";
    private StringBuffer mStringBuffer = new StringBuffer();
    private List<ScanResult> listResult;
    private ScanResult mScanResult;
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo = null;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义�?��WifiLock
    WifiLock mWifiLock;
    
    private Context mContext;

    /**
     * 构�?方法
     */
    public WifiCtrl(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        
    }
    /**
     * 判断Wifi是否可打�?
     */
    public boolean wifiIsEnable(){
        return mWifiManager.setWifiEnabled(true);
    }
    /**
     * 打开Wifi网卡
     */
    public void openNetCard() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭Wifi网卡
     */
    public void closeNetCard() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * �?��当前Wifi网卡状�?
     */
    public int getNetCardWorkState() {
        int state = mWifiManager.getWifiState();
        if (state == WifiManager.WIFI_STATE_DISABLING) {
            Log.i(TAG, "网卡正在关闭");
        } else if (state == WifiManager.WIFI_STATE_DISABLED) {
           // Log.i(TAG, "����������������");
        } else if (state == WifiManager.WIFI_STATE_ENABLING) {
            Log.i(TAG, "网卡正在打开");
        } else if (state == WifiManager.WIFI_STATE_ENABLED) {
            Log.i(TAG, "网卡已经打开");
        } else {
            //Log.i(TAG, "---_---����......����л������������---_---");
        }
        return state;
    }
    public void checkNetCardState() {
        if (mWifiManager.getWifiState() == 0) {
            //Log.i(TAG, "网卡正在关闭");
        } else if (mWifiManager.getWifiState() == 1) {
            //Log.i(TAG, "网卡已经关闭");
        } else if (mWifiManager.getWifiState() == 2) {
            //Log.i(TAG, "网卡正在打开");
        } else if (mWifiManager.getWifiState() == 3) {
            //Log.i(TAG, "网卡已经打开");
        } else {
            //Log.i(TAG, "---_---�?.....没有获取到状�?--_---");
        }
    }

    /**
     * 扫描周边网络
     */
    public void scan() {
        mWifiManager.startScan();
        listResult = mWifiManager.getScanResults();
        if (listResult != null) {
            //Log.i(TAG, "当前区域存在无线网络，请查看扫描结果");
        } else {
           // Log.i(TAG, "当前区域没有无线网络");
        }
    }
    
    /**
     * 得到扫描结果
     */
    public ScanResult getScanResult(int index) {
        if (listResult == null || listResult.size() <= 0 || index >= listResult.size()) {
            return null;
        }
        return listResult.get(index);
    }
    public String getScanResult() {
        // 每次点击扫描之前清空上一次的扫描结果
        if (mStringBuffer != null) {
            mStringBuffer = new StringBuffer();
        }
        // �?��扫描网络
        scan();
        listResult = mWifiManager.getScanResults();
        if (listResult != null) {
            mStringBuffer.append("INDEX -> SSID : BSSID : CAPABILITIES : FREQUENCY : LEVEL : DESC\n\n");
            for (int i = 0; i < listResult.size(); i++) {
                mScanResult = listResult.get(i);
                /*待修正＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
                String tempstr = mContext.getString(R.string.test_wifi_signal);
                if(mScanResult.level >= -50) {
                    tempstr += mContext.getString(R.string.test_wifi_signal_very_good);
                } else if(mScanResult.level < -50 && mScanResult.level >= -60) {
                    tempstr += mContext.getString(R.string.test_wifi_signal_good);
                } else if(mScanResult.level < -60 && mScanResult.level >= -70) {
                    tempstr += mContext.getString(R.string.test_wifi_signal_ok);
                } else if(mScanResult.level < -70 && mScanResult.level >= -80) {
                    tempstr += mContext.getString(R.string.test_wifi_signal_poor);
                } else if(mScanResult.level < -80) {
                    tempstr += mContext.getString(R.string.test_wifi_signal_very_poor);
                } 
                
                mStringBuffer = mStringBuffer.append("NO.").append(i + 1)
                        .append("-> ").append(mScanResult.SSID).append(" : ")
                        .append(mScanResult.BSSID).append(" : ")
                        .append(mScanResult.capabilities).append(" : ")
                        .append(mScanResult.frequency).append(" : ")
                        .append(mScanResult.level).append(" : ")
                        .append(mScanResult.describeContents()).append("  ")
                        .append(tempstr).append("\n\n");
                
                */
            }
        }
        //Log.i(TAG, mStringBuffer.toString());
        return mStringBuffer.toString();
    }

    /**
     * 连接指定网络
     */
    public void connect() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        
    }
    public void updateConnectInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        
    }

    /**
     * 断开当前连接的网�?
     */
    public void disconnectWifi() {
        int netId = getNetworkId();
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
        mWifiInfo = null;
    }

    /**
     * �?��当前网络状�?
     * 
     * @return String
     */
    public boolean isNetWorkOK() {
        if (mWifiInfo != null) {
            //Log.i(TAG, "网络正常工作");
            return true;
        } else {
           // Log.i(TAG, "网络已断�?);
            return false;
        }
    }
    public void checkNetWorkState() {
        if (mWifiInfo != null) {
            //Log.i(TAG, "网络正常工作");
        } else {
            //Log.i(TAG, "网络已断�?);
        }
    }

    /**
     * 得到连接的ID
     */
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 得到IP地址
     */
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }
    /**
     * 得到IP地址
     */
    public int getGatewayAddress() {
        if(mWifiManager != null) {
            
            return mWifiManager.getDhcpInfo().gateway;
        }
        return 0;
    }


    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // ��ж������������
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建�?��WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index >= mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网�?
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    // ������MAC������
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加�?��网络并连�?
    public int addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(mWifiConfiguration.get(3));
        mWifiManager.enableNetwork(wcgID, true);
        return wcgID;
    }
    
    public String getConnectedSSID() {
        String retval = null;
         if(mWifiInfo == null || mWifiInfo.getSSID()== null || mWifiInfo.getSSID().equals("null")) {
            return null;
         }
         retval = mWifiInfo.getSSID();
         return retval;
    }
    
    public String getRssi() {
        if(mWifiInfo == null || mWifiInfo.getSSID()== null || mWifiInfo.getSSID().equals("null")) {
            return null;
         }
        return String.valueOf( mWifiInfo.getRssi()+"dBm");
        
    }
    public String getConnectedInfo() {
        String retval = null;//mContext.getString(R.string.test_wifi_unconnected);
        if(mWifiInfo == null)
            return retval;
        
        if(mWifiInfo.getSSID()== null || mWifiInfo.getSSID().equals("null")) {
            return retval;
        } else {
            int rssi = mWifiInfo.getRssi();
            /*待修正＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
            String signal = mContext.getString(R.string.test_wifi_signal);
            if(rssi >= -50) {
                signal += mContext.getString(R.string.test_wifi_signal_very_good);
            } else if(rssi < -50 && rssi >= -60) {
                signal += mContext.getString(R.string.test_wifi_signal_good);
            } else if(rssi < -60 && rssi >= -70) {
                signal += mContext.getString(R.string.test_wifi_signal_ok);
            } else if(rssi < -70 && rssi >= -80) {
                signal += mContext.getString(R.string.test_wifi_signal_poor);
            } else if(rssi < -80) {
                signal += mContext.getString(R.string.test_wifi_signal_very_poor);
            } 
            
            String speed = mContext.getString(R.string.test_wifi_speed) + mWifiInfo.getLinkSpeed() + "Mbps";
            retval = mWifiInfo.toString() + "\n"+ signal + "\n"+ speed;
            */
        }        
        return retval;
    }
}
