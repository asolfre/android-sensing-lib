package com.yifeijiang.android.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Environment;
import android.util.Log;

public class ExtFileLogger {
	String TAG = "RoomLoc:FileLogger";
	
	private String Path;
	private File extFile;
	String CURRENT_LOGFILE_NAME;
	//DateFormat filedateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	
	public ExtFileLogger(String path){
		newFileLogName();
		Path = path;
	}
	
	public void newFileLogName(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String ct = sdf.format(cal.getTime());
        CURRENT_LOGFILE_NAME = ct + ".log";
	}
	
    public void log(String logText) {
    	String logStr = logText + "\n"; 
    	writeExtFile(logStr, CURRENT_LOGFILE_NAME);
    }
    
    
    public void writeExtFile(String s, String FILENAME){
    	if (!isExtStorageWritable(Path))
    		return;
    	
		try {
			extFile = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + Path ,FILENAME);
			FileWriter fw = new FileWriter(extFile, true);
			fw.write(s);
			fw.close();
			
        } catch (IOException e) {
            Log.e(TAG, "Failed to create dirs");
        }
    }
    
    private boolean isExtStorageWritable(String path) {
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
                extPath = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + path);
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
