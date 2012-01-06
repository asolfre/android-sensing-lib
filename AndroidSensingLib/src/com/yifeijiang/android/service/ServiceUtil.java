package com.yifeijiang.android.service;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

public class ServiceUtil {
	
	
    public static boolean isServiceRunning(Context context, String serviceName){
	    //check if the service is running
    	ActivityManager actManager;
	    actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<ActivityManager.RunningServiceInfo> runningServs = actManager.getRunningServices(100);
	    Iterator <ActivityManager.RunningServiceInfo> iterator = runningServs.iterator();
	    
	    while (iterator.hasNext()) {
	    	String service_name = iterator.next().process;
	    	//Log.d("service", service_name);
	    	if (service_name.equals( serviceName )) {
	    		return true;
	    		} 
	    	}
	    return false;
    }
 
    
}
