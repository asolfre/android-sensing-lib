package com.yifeijiang.android.wifiscanexample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.yifeijiang.android.storage.ExtFileLogger;
import com.yifeijiang.android.wifi.WifiScanner;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class WifiScanService extends Service{

	WifiScanner mwifi;
	WifiScanner.Listener wifiListener; 
	public static String uiWifiScan = "None"; 
	
	
	DateFormat logdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	ExtFileLogger mlogger;
	

	  
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void onCreate() {
    	mlogger = new ExtFileLogger("/WifiScanExample");
    	
    	setwifi();
    }
    
    @Override
    public void onDestroy() {
    	mwifi.release();
    	uiWifiScan = "None";
    }
    
    @Override
    public void onStart(Intent intent, int startid) {
    	
    }
    
    private void setwifi(){
        wifiListener = new WifiScanner.Listener() {
     		
     		@Override
     		public void onScanError(int error) {
     			// TODO Auto-generated method stub
     			
     		}
     		
     		@Override
     		public void onScanComplete(long epochTime, List<ScanResult> scanResults) {
                 String result = "\"WIFI\":{";
                 Iterator<ScanResult> resultIterator = scanResults.iterator();
                 if (resultIterator.hasNext()) {
                     while (resultIterator.hasNext()) {
                         ScanResult scanResult = resultIterator.next();
                         result = result + "\"" + scanResult.BSSID + "\":" 
                         			+ "\"" + scanResult.level + "\""
                         			+ ",";
                     }
                     result = result.substring(0, result.length()-1) + "}";
                 } else {
                     result = result + "}";
                 }
             
             Calendar calendar = Calendar.getInstance();
             calendar.setTimeInMillis(epochTime);
             
             result = "{\"CT\":\"" + logdateFormat.format(calendar.getTime()) + "\",\"T\":" 
                         + epochTime + "," + result + "}";
             uiWifiScan = result;
             mlogger.log(result);
     		}
     	};
         /////////////////////////////////////////////////////////
     	mwifi = new WifiScanner(this.getApplicationContext(), wifiListener);
     	mwifi.setPower();
     	mwifi.scanPeriodic(10000);
     }
  
}
