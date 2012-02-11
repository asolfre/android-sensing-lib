package com.yifeijiang.android.location;

import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

public class LocationScan implements LocationListener,  GpsStatus.Listener{

	public String STATUS = "OFF";
	
	LocationManager lm;
	String gpsdata = "";
	Context context;
	LocationScanListener listener;
	
	

    
    
	public LocationScan(Context ctx, LocationScanListener lis){
		
		context = ctx;
		listener = lis;
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	public void start(){
		
	    String provider = Settings.Secure.getString(context.getContentResolver(), 
	            Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps") == false) {
        	enableGPS(); // the GPS is already in the requested state
        }
        if (STATUS == "OFF"){
        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1f, this);
        	STATUS = "ON";
        }
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
    	
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            context.sendBroadcast(poke);
        }    
    }

    private void disableGPS(){
    	
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            context.sendBroadcast(poke);
        }
    }

	@Override
	public void onLocationChanged(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		double accuracy = location.getAccuracy();
		long t = location.getTime();
		listener.onLocationReadComplete(latitude, longitude, accuracy, t);
		
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