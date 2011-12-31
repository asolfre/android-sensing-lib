package com.yifeijiang.android.cloud;

import org.json.JSONObject;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

public class PeriodicUpload {
	UploadHandler loop_upload_handler;
	Listener listener;
	
	int interval;
	
    private WifiManager wm;
    private WifiManager.WifiLock wifil;
    Context context;
    
	Handler wake_wifi = new Handler(){
	       @Override
	        public void handleMessage(Message msg) {
	    	   
	    	   upload_thread.start();
	    	   loop_upload_handler.sleep(interval);
	        }		
	};
	
	FileUploadThread upload_thread;
	FileUploadThread.Listener upload_thread_listener;
	
    public static abstract class Listener {
    	
        public abstract void onComplete( );
        public abstract void onError( );
        
    }
    
	public PeriodicUpload( Context ctx, 
			Listener lsn, 
			String mpath, 
			String mfileRegex, 
			String murl, 
			boolean mdeleteUploaded, 
			String key ){
		listener = lsn;
		context = ctx;
		
		
		loop_upload_handler = new UploadHandler();
		
        wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifil = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "wifilock"); 
        
       	upload_thread_listener = new FileUploadThread.Listener(){

			@Override
			public void onComplete() {
				listener.onComplete();
				
			}

			@Override
			public void onError() {
				listener.onError();
				
			}

			@Override
			public void allDone() {
				wifil.release();
				
			}
    		
    	};
    	upload_thread = new FileUploadThread(
   	 			"MAQS-Loc", 
   	 			"^(orignal.).+.+(\\.gz)$",
   			    "http://car.colorado.edu:443/uploadfile", 
   			    false , "ALLDATA",upload_thread_listener);
    	
		
	}
	
	public void start(int pinterval ){
		interval = pinterval;
		upload();
	}
	
	public void release(){
		loop_upload_handler.removeMessages(0);
		if (wifil.isHeld())
			wifil.release();
	}
	
	private void upload(){
		wifil.acquire();
		//wm.reassociate();
		wm.reconnect();
		wake_wifi.sendMessageDelayed(wake_wifi.obtainMessage(0), 30*1000);
		
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
