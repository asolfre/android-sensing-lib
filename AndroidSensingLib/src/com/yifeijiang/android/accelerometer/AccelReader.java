package com.yifeijiang.android.accelerometer;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;


public class AccelReader implements SensorEventListener{
	
    SensorManager mSensorManager;
    
    int SensingDuration;
    Listener SensorListener;
    long SensingStart;
    ////////////////////////////////////
	//private static final String TAG = "AccelemeterResult";
	
	private ArrayList<ArrayList<Long>> allAcc = new ArrayList<ArrayList<Long>>();
	
	
    
    public static abstract class Listener {
    	
        public static final int ERR_OK = 0;
        public static final int ERR_INIT_FAILED = 1;
        public static final int ERR_READ_FAILED = 2;
        
        public abstract void onReadComplete(ArrayList<ArrayList<Long>> accReadings);
        public abstract void onReadError(int error);
    }
    
	
    public AccelReader(){
    	
    }
    
    public void startReader(Context context, Listener listener, int duration){ // duration - ms
    	SensingDuration = duration;
    	SensorListener = listener;
        // Obtain a reference to system-wide sensor event manager.
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // Register for events.
        //mAccelemeterResult = new AccelemeterResult(context.getApplicationContext(),HandlerAcc);
        mSensorManager.registerListener( this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        SensingStart = System.currentTimeMillis();
        
    }
    
    public void release(){
    	
    	mSensorManager.unregisterListener(this);
    	
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		ArrayList<Long> acc = new ArrayList<Long>();
		String results = "";
		
		//String t = String.valueOf(event.timestamp);
		
		long mNewValue = Math.round( event.values[0]*100.0 );
        acc.add(mNewValue);
        
		mNewValue = Math.round( event.values[1]*100.0 );
        acc.add(mNewValue);
        
		mNewValue = Math.round( event.values[2]*100.0 );
        acc.add(mNewValue);

        allAcc.add(acc);        
        if ( ( System.currentTimeMillis() - SensingStart) > SensingDuration) {
        	SensorListener.onReadComplete(allAcc);
        	release();
        }
		
	}
    
}

