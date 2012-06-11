package com.yifeijiang.android.wifiscanexample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.yifeijiang.android.storage.ExtFileLogger;
import com.yifeijiang.android.wifi.WiFiSignalScanner;
import com.yifeijiang.android.wifi.WiFiSignalScannerListener;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.IBinder;

public class WifiScanService extends Service implements WiFiSignalScannerListener{

	WiFiSignalScanner mwifi;
	WiFiSignalScannerListener wifiListener; 
	
	
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
    	Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String ct = sdf.format(cal.getTime());
        String fileName =  ct + ".log";
        
    	mlogger = new ExtFileLogger(this.getApplicationContext(), "WifiScanExample");
    	mwifi = new WiFiSignalScanner(this, wifiListener);
    }
    
    @Override
    public void onDestroy() {
    	mwifi.release();
    	uiWifiScan = "None";
    }
    
    @Override
    public void onStart(Intent intent, int startid) {
    	
    }
    


	@Override
	public void onScanComplete(long epochTime, List<ScanResult> scanResults) {
		// TODO Auto-generated method stub
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
    mlogger.logExt(result);
	}

	@Override
	public void onScanError(int error) {
		// TODO Auto-generated method stub
		
	}
  
}
