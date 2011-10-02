package com.yifeijiang.android.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
	
	public static String getPreference(Context context, String PreferenceName, String Key){
		
    	SharedPreferences settings = context.getSharedPreferences(PreferenceName, 0);
    	String Value = settings.getString(Key, "");
    	if (!Value.equals("")){
    		return Value;
    	}
    	else{
    		return ""; 
    	}
    	
	}
	public static void putPreference(Context context, String PreferenceName, String Key, String Value){
		
    	SharedPreferences settings =   context.getSharedPreferences( PreferenceName,0 );
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putString( Key , Value);
    	editor.commit();
    	
	}
}
