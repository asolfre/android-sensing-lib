package com.yifeijiang.android.wifi;

import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

public class WifiScanner extends BroadcastReceiver{

	
	private boolean Continue = false;
	private long ContinueSleep = 2000;
    private IntentFilter filterW;
    private PowerManager pm;
    private WifiManager wm;
    private PowerManager.WakeLock wl;
    private WifiManager.WifiLock wifil;
 
    private int WiFi_SAMPLE_RATE = 30000;
    
    private Listener wifiListener;
    private RefreshHandler periodicHandler;
	private Context context;
	
    public static abstract class Listener {
    	
        public static final int ERR_OK = 0;
        public static final int ERR_WIFI_DISABLED = 1;
        public static final int ERR_READ_FAILED = 2;
        
        public abstract void onScanComplete(long epochTime, List<ScanResult> scanResults);        
        public abstract void onScanError(int error);
    }

    
	public WifiScanner( Context mcontext, Listener listener ){
		
		context = mcontext;
		wifiListener = listener;
		periodicHandler = new RefreshHandler();
        filterW = new IntentFilter();
        filterW.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(this, filterW);
	}
	
	
	public void scanStart(){
		
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
        //if wifi is disabled;
        //wm.setWifiEnabled(false);
        wm.setWifiEnabled(true);
        //while (!wm.isWifiEnabled()){
        //	try {
		//		Thread.sleep(1000);
		//	} catch (InterruptedException e) {
		//		// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}
        //}
        wm.startScan();
	}
	
	
	@Override
	public void onReceive(Context mcontext, Intent mintent) {
        
        if (mintent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
        	
            WifiManager w = (WifiManager) context.getSystemService (Context.WIFI_SERVICE); 
            List<ScanResult> scanResults = null;
            scanResults = w.getScanResults();
            long now = System.currentTimeMillis();
            wifiListener.onScanComplete(now, scanResults);   
            if ( Continue == true){
            	try {
					Thread.sleep(ContinueSleep);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	scanStart();
            }
        }
	}
	
	
	public void setPower(){
		
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PowerManagerTag");
        wl.acquire();
        if (wl.isHeld())
            Log.d("PowerManager","Lock Held");
        wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifil = wm.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "wifilock"); 
        wifil.acquire();
    }
    
	
    public void release(){
    	
    	context.unregisterReceiver(this);
    	periodicHandler.removeMessages(0);
        wl.release();
        wifil.release();
    }
    
    
    public void scanPeriodic(int sleepDuration){
    	WiFi_SAMPLE_RATE = sleepDuration;
    	periodicHandler.sleep( WiFi_SAMPLE_RATE );
    	scanStart();
    }
    
    public void scanContinuously(long interval){
    	Continue = true;
    	ContinueSleep = interval;
    	scanStart();
    }
    
	private class RefreshHandler extends Handler {
		
	    @Override
	    public void handleMessage(Message msg) {
	    	WifiScanner.this.scanPeriodic( WiFi_SAMPLE_RATE );
	    }
	
	    public void sleep(long delayMillis) {
	      this.removeMessages(0);
	      sendMessageDelayed(obtainMessage(0), delayMillis);
	    }
	};
}
