package com.yifeijiang.android.cloud;

import org.json.JSONObject;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

public class PeriodicUpload {
	PeriodicUploadHandler loop_upload_handler;
	Listener listener;
	
	int interval;
	
    private WifiManager wifi_manager;
    private WifiManager.WifiLock wifi_lock;
    Context context;
    
	Handler upload_ready;
	
	FileUploadThread upload_thread;
	FileUploadThread.Listener upload_thread_listener;
	
	String upload_path;
	String upload_fileregex; 
	String upload_url;
	boolean upload_deleteuploaded; 
	String upload_key;
	
    public static abstract class Listener {
    	
        public abstract void onComplete( );
        public abstract void onError( );
        public abstract void newLog();
        
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
		
		upload_path = mpath;
		upload_fileregex = mfileRegex; 
		upload_url = murl;
		upload_deleteuploaded = mdeleteUploaded; 
		upload_key = key;		
		
		loop_upload_handler = new PeriodicUploadHandler();
		
        wifi_manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifi_lock = wifi_manager.createWifiLock(WifiManager.WIFI_MODE_FULL, "wifilockfull"); 
        
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
				if (wifi_lock.isHeld()){
					Log.d("UPLOAD", "wifi DISABLED");
					wifi_lock.release();
				}
			}
    		
    	};
    	
    	upload_ready = new Handler(){
 	       @Override
 	        public void handleMessage(Message msg) {
 	    	   Log.d("UPLOAD", "wifi enabled");
 	    	   upload_thread = new FileUploadThread(
 	    			upload_path, 
 	    			upload_fileregex,
 	    			upload_url, 
 	    			upload_deleteuploaded , upload_key ,upload_thread_listener);
 	    	   upload_thread.start();
 	    	   loop_upload_handler.sleep(interval);
 	        }		
 	};
    	
		
	}
	
	public void start(int pinterval ){
		interval = pinterval;
		upload();
	}
	

	private void upload(){
		Log.d("UPLOAD", "enable wifi");
		wifi_lock.acquire();
		wifi_manager.reassociate();
		wifi_manager.reconnect();
		upload_ready.sendMessageDelayed(upload_ready.obtainMessage(0), 30*1000);
		
	}
	

	
	private class PeriodicUploadHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	listener.newLog();
        	upload();
        	
        }

        public void sleep(long delayMillis) {

        	this.removeMessages(0);
        	sendMessageDelayed(obtainMessage(0), delayMillis);
          	
        }
        
	};

	public void release(){
		loop_upload_handler.removeMessages(0);
		if (wifi_lock.isHeld())
			wifi_lock.release();
	}
	  
}
