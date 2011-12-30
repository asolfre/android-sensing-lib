package com.yifeijiang.android.cloud;

import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

public class PeriodicUpload {
	UploadHandler upload_handler;
	Upload upload_listener;
	
	int interval;
	
    private WifiManager wm;
    private WifiManager.WifiLock wifil;
    Context context;
	Handler wake_wifi = new Handler(){
	       @Override
	        public void handleMessage(Message msg) {
	    	   upload_listener.startUpload();
	    	   wifil.release();
	    	   upload_handler.sleep(interval);
	        }		
	};
	
	
	

    
	public PeriodicUpload( Context ctx, Upload up ){
		upload_listener = up;
		context = ctx;
		upload_handler = new UploadHandler();
		
        wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifil = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "wifilock"); 
		
	}
	
	public void start(int pinterval ){
		interval = pinterval;
		upload();
	}
	
	public void release(){
		upload_handler.removeMessages(0);
		if (wifil.isHeld())
			wifil.release();
	}
	
	private void upload(){
		wifil.acquire();
		wm.reassociate();
		wm.reconnect();
		wake_wifi.sendMessageDelayed(wake_wifi.obtainMessage(0), 60*1000);
		
	}
	
    public static abstract class Upload {
    	
        public abstract void startUpload( );
        
    }
	
	class UploadHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	upload();
        	
        }

        public void sleep(long delayMillis) {

        	this.removeMessages(0);
        	sendMessageDelayed(obtainMessage(0), delayMillis);
          	
        }
        
	};

    
}
