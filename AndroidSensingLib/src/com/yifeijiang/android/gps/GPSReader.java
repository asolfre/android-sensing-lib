package com.yifeijiang.android.gps;

import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

public class GPSReader implements LocationListener,  GpsStatus.Listener{

	public static String STATUS = "OFF";
	
	LocationManager lm;
	String gpsdata = "";
	Context ContextGPS;
	Listener listener;
	
	
    /**
     * Listener for audio reads.
     */
    public static abstract class Listener {

        public static final int ERR_OK = 0;
        public static final int ERR_INIT_FAILED = 1;
        public static final int ERR_READ_FAILED = 2;

        public abstract void onReadComplete(double latitude, double longitude, double accuracy);
        public abstract void onReadError(int error);
    }
    
    
	public GPSReader(Context context, Listener lis){
		
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		ContextGPS = context;
		listener = lis;
		
	}

	public void start(){
		
	    String provider = Settings.Secure.getString(ContextGPS.getContentResolver(), 
	            Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps") == false) {
        	enableGPS(); // the GPS is already in the requested state
        }
        
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1f, this);
		
		STATUS = "ON";
	}
	
	public void stop(){
		lm.removeUpdates(this);
		STATUS = "OFF";
	}
  
    public void release(){
    	
    	lm.removeUpdates(this);
    	STATUS = "OFF";
    	
    }
    
    private void enableGPS( ){
    	
        String provider = Settings.Secure.getString(ContextGPS.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            ContextGPS.sendBroadcast(poke);
        }    
    }

    private void disableGPS(){
    	
        String provider = Settings.Secure.getString(ContextGPS.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            ContextGPS.sendBroadcast(poke);
        }
    }

	@Override
	public void onLocationChanged(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		double accuracy = location.getAccuracy();
		listener.onReadComplete(latitude, longitude, accuracy);
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
        String state = "";
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:  
                state = "GPS_EVENT_STARTED"; 
                break;
            case GpsStatus.GPS_EVENT_STOPPED:  
                state = "GPS_EVENT_STOPPED";
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:  
                state = "GpsStatus.GPS_EVENT_FIRST_FIX";
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:  
                state = "GPS_EVENT_SATELLITE_STATUS"; 
                break;
        }
	}
}