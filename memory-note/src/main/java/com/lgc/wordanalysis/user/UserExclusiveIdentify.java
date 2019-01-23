package com.lgc.wordanalysis.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.lgc.baselibrary.baseComponent.BaseApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Administrator on 2016/11/16 0016.
 * 用户唯一标识符
 * 综合多种方法，取MD5值，目前没有单一可行的方法
 */
public class UserExclusiveIdentify {
    /**
     * The IMEI: 仅仅只对Android手机有效:
     * 需要通话权限，对用户体验以一定影响
     */
    private static String getIMEI() {
        TelephonyManager TelephonyMgr = (TelephonyManager) BaseApplication.appContext.getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        return szImei + "";
    }

    /**
     * Pseudo-Unique ID, 这个在任何Android手机中都有效
     * <p>没有通话功能，或者你不愿加入READ_PHONE_STATE许可
     * <p>可以通过取出ROM版本、制造商、CPU型号、以及其他硬件信息来实现这一点。
     * <p>这样计算出来的ID不是唯一的（因为如果两个手机应用了同样的硬件以及Rom 镜像）。但出现类似情况的可能性基本可以忽略。
     * <p> 要实现这一点，你可以使用Build类
     */
    private static String getPseudoUniqueId() throws Exception {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10;
        return m_szDevIDShort;
    }

    /**
     * The Android ID
     * 通常被认为不可信，因为它有时为null。
     * 开发文档中说明了：如果进行了出厂设置或者被Root过的话,这个ID会改变.
     */
    private static String getTheAndroidID() throws Exception {
        String m_szAndroidID = Settings.Secure.getString(BaseApplication.appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        return m_szAndroidID + "";
    }

    /**
     * The WLAN MAC Address string
     * 是另一个唯一ID。但是你需要为你的工程加入android.permission.ACCESS_WIFI_STATE 权限，否则这个地址会为null。
     */
    private static String getWLAN_MAC() throws Exception {
        WifiManager wm = (WifiManager) BaseApplication.appContext.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        return m_szWLANMAC + "";
    }

    /**
     * 综上所述，我们一共有五种方式取得设备的唯一标识。它们中的一些可能会返回null，或者由于硬件缺失、权限问题等获取失败。
     * 但你总能获得至少一个能用。所以，最好的方法就是通过拼接，或者拼接后的计算出的MD5值来产生一个结果。
     *
     * @return 可产生32位的16进制数据:
     * 比如 9DDDF85AFF0A87974CE4541BD94D5F55
     */
    public static String getExclusiveIndentify() {
        SharedPreferences sp = BaseApplication.appContext.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
        String deviceIdentify = sp.getString("device_identify", null);
        if (deviceIdentify != null)
            return deviceIdentify;

        String m_szLongID = "S";
        try {
            m_szLongID += getIMEI() + getPseudoUniqueId() + getTheAndroidID()
                    + getWLAN_MAC();
        } catch (Exception e) {
            e.printStackTrace();
        }
// compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
// get md5 bytes
        byte p_md5Data[] = m.digest();
// create a hex string
        String m_szUniqueID = "";
        for (byte ai : p_md5Data) {
            int b = (0xFF & ai);
// if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
// add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("device_identify", m_szUniqueID).apply();
        return m_szUniqueID;
    }
}
