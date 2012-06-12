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

public class WiFiSignalScanner extends BroadcastReceiver{

	
	private boolean Continue = false;
	private long ContinueSleep = 2000;
    private IntentFilter filterW;

 
    private int WiFi_SAMPLE_RATE = 30000;
    
    private WiFiSignalScannerListener wifiListener;
    private RefreshHandler periodicHandler;
	private Context context;
	
	private boolean periodic_scan_status = true;
	
  

    
	public WiFiSignalScanner( Context mcontext, WiFiSignalScannerListener listener ){
		
		context = mcontext;
		wifiListener = listener;
		periodicHandler = new RefreshHandler();
        filterW = new IntentFilter();
        filterW.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(this, filterW);
	}
	
	public void registerReceiver(){
		context.registerReceiver(this, filterW);
	}
	
	public void unregisterReceiver(){
		context.unregisterReceiver(this);
	}
	
    public void startPeriodicScan(int sleepDuration){
    	periodic_scan_status = true;
    	scanPeriodic(sleepDuration);
    }
    
    public void stopPeriodicScan(){
    	periodic_scan_status = false;
    	periodicHandler.removeMessages(0);
    }
    
    public void startScanContinuously(long interval){
    	Continue = true;
    	ContinueSleep = interval;
    	scanStart();
    }
    public void stopScanContinuously(){
    	Continue = false;
    }    
    
    public void release(){
    	try{
    	context.unregisterReceiver(this);}
    	catch (Exception e) {
			// TODO Auto-generated catch block
		}
    	periodicHandler.removeMessages(0);

    }
    
	public void scanStart(){
		
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
        if (wm.getWifiState() != WifiManager.WIFI_STATE_ENABLED)
        	wm.setWifiEnabled(true);
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
	
	

    
    
    private void scanPeriodic(int sleepDuration){
    	if (periodic_scan_status == false)
    		return;
    	
    	WiFi_SAMPLE_RATE = sleepDuration;
    	periodicHandler.sleep( WiFi_SAMPLE_RATE );
    	scanStart();
    }
    


	private class RefreshHandler extends Handler {
		
	    @Override
	    public void handleMessage(Message msg) {
	    	WiFiSignalScanner.this.scanPeriodic( WiFi_SAMPLE_RATE );
	    }
	
	    public void sleep(long delayMillis) {
	      this.removeMessages(0);
	      sendMessageDelayed(obtainMessage(0), delayMillis);
	    }
	};
}
