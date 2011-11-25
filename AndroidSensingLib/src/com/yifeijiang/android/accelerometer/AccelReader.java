package com.yifeijiang.android.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class AccelReader implements SensorEventListener{
	
    SensorManager mSensorManager;    
    Listener AccListener;
    
    public static abstract class Listener {
    	
        public static final int ERR_OK = 0;
        public static final int ERR_INIT_FAILED = 1;
        public static final int ERR_READ_FAILED = 2;
        
        public abstract void onReadComplete(long x, long y, long z);
        public abstract void onReadError(int error);
    }
    
	
    public AccelReader(Context context, Listener listener){
    	AccListener = listener;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener( this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        
    }
        
    public void release(){
    	mSensorManager.unregisterListener(this);
    	
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		//String t = String.valueOf(event.timestamp);
		
		long x = Math.round( event.values[0]*100.0 );
        
		long y = Math.round( event.values[1]*100.0 );
        
		long z = Math.round( event.values[2]*100.0 );
        

		AccListener.onReadComplete(x, y, z);
		
	}
    
}

