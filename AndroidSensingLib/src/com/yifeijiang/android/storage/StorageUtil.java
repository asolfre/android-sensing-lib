package com.yifeijiang.android.storage;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class StorageUtil {
	
    public static boolean isExtStorageWritable() {
    	boolean extWritable;
    	boolean mExternalStorageAvailable;
    	boolean mExternalStorageWriteable;
    	File extPath;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        if ( mExternalStorageAvailable & mExternalStorageWriteable) {
            try {
                extPath = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/MAQS-Loc");
                extPath.mkdirs();
            } catch (IOException e) {
                //Log.e(TAG, "Failed to create dirs");
            }
            extWritable = true;
        } else {
            extWritable = false;
            //Log.d(TAG, "External media not available");
        }
        
        return extWritable;
    }
    
    
}
