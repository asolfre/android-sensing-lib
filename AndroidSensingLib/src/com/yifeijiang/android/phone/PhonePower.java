package com.yifeijiang.android.phone;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

public class PhonePower {
    private static PowerManager pm;
    private static WifiManager wm;
    private static PowerManager.WakeLock wl;
    private static WifiManager.WifiLock wifil;
    
    public static void setPowerWeakLock(Context context){
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PowerManagerTag");
    }
    
    public static void setWiFiScanOnlyLock(Context context){
        wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifil = wm.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "wifilock"); 
    }
    
    
    public static void wifiScanOnlyLockOn(){
        wifil.acquire();
	}
	
    public static void wifiScanOnlyLockOff(){
		if (wifil.isHeld()){
			wifil.release();
		}
	}
    
    public static void powerLockOn(){
    	wl.acquire();
	}
	
    public static void powerLockOff(){
    	 if (wl.isHeld())
             wl.release();
	}
    
}
