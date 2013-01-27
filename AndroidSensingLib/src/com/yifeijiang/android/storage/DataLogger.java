package com.yifeijiang.android.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class DataLogger {
	String fileName;
	String filePath;
	Context context;
	private String TAG = "DataLogger";
	
	public DataLogger(String fpath, String fname, Context ctx){
		fileName = fname;
		filePath = fpath;
		context = ctx;
	}
	
	public String getFileName(){
		return fileName;
	}
	public String getFilePath(){
		return filePath;
	}
	
	public void setLogFile(String fname){
		fileName = fname;
	}
	public void setLogPath(String fpath){
		filePath = fpath;
	}
	
	
	public void write2ContextStorage(String data){
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            //Log.d(TAG, "Opened file " + FILENAME);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Unable to open file " + fileName);
        }
        try {
            if (fos != null) {
                fos.write(data.getBytes());
                fos.close();
                //Log.d(TAG, "Wrote to file " + FILENAME);
            }
        } catch (IOException e) {
            Log.d(TAG, "Unable to write to file " + fileName);
        }
	}
	
	
	public void write2ExternalStorage(String data){
    	if (!isExtStorageWritable(filePath))
    		return;
    	//Environment.getExternalStorageDirectory().getCanonicalPath() +"/"+ 
		try {
			File fileLogger = new File(filePath ,fileName);
			FileWriter fw = new FileWriter(fileLogger, true);
			fw.write(data);
			fw.close();
			
        } catch (IOException e) {
            Log.e("DataLogger", "Failed to create dirs");
        }	
	}
	
	

	
    private boolean isExtStorageWritable(String path) {
    	boolean extWritable;
    	boolean mExternalStorageAvailable;
    	boolean mExternalStorageWriteable;
    	File file_external_path;
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
            //Environment.getExternalStorageDirectory().getCanonicalPath() +"/"+ path
			file_external_path = new File(path);
			file_external_path.mkdirs();
            extWritable = true;
        } else {
            extWritable = false;
            Log.d(TAG, "External media not available");
        }
        
        return extWritable;
    }
    
    
}
