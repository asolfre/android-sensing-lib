package com.yifeijiang.android.wifiscanexample;

import com.yifeijiang.android.service.ServiceUtil;

import com.yifeijiang.android.wifiscanexample.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WifiScanActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	  Button buttonStart;
	  Button buttonEnd;
	  TextView textViewStatus;
	  TextView textViewWifi;
	  String versionName;
	  int versionCode;
	  RefreshHandler mRedrawHandler;
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        buttonStart = (Button) findViewById(R.id.start);
        buttonEnd = (Button) findViewById(R.id.end);
        buttonStart.setOnClickListener( this);
        buttonEnd.setOnClickListener(this);
        textViewStatus = (TextView) findViewById(R.id.status);
        textViewWifi =  (TextView) findViewById(R.id.wifi);
        mRedrawHandler = new RefreshHandler();
        
        try {
    		versionName = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
    		versionCode = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
    	} catch (NameNotFoundException e) {
    		
    	}
    	updateServiceStatus();
    	updateUI();
    }

	@Override
	public void onClick(View src) {
	    switch (src.getId()) {
	    case R.id.start:
	        startService(new Intent(this, WifiScanService.class));
	        Toast.makeText(this, "Starting Service", Toast.LENGTH_LONG).show();
	        //finish();
	        Log.d("yifei", "click start");
	        updateServiceStatus();
	        break;
	    case R.id.end:
	    	stopService(new Intent(this, WifiScanService.class));
	        //finish();
	    	updateServiceStatus();
	        break;
	    }

	}
	
	private void updateServiceStatus(){
		if (ServiceUtil.isServiceRunning(this,"com.yifeijiang.android.wifiscanexample")){
			textViewStatus.setText("Wifi Scan is Running: (V"+ versionName+" ." + Integer.toString(versionCode) + ")");
			buttonStart.setVisibility(android.view.View.GONE);
			buttonEnd.setVisibility(android.view.View.VISIBLE);
		}
		else{
			textViewStatus.setText("Wifi Scan is Stopped: (V"+ versionName+" ." + Integer.toString(versionCode) + ")");
			buttonStart.setVisibility(android.view.View.VISIBLE);
			buttonEnd.setVisibility(android.view.View.GONE);
            
		}
	}
	
    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	WifiScanActivity.this.updateUI();
        }

        public void sleep(long delayMillis) {
          this.removeMessages(0);
          sendMessageDelayed(obtainMessage(0), delayMillis);
        }

      };


      public void updateUI(){
    	  
          mRedrawHandler.sleep(1000);
          textViewWifi.setText(WifiScanService.uiWifiScan);
          
      }
}