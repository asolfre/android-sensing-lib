package com.yifeijiang.android.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.GZIPOutputStream;
import android.content.Context;
import android.os.Environment;
import android.util.Log;


public class ExtFileLogger {
	String TAG = "RoomLoc:FileLogger";
	
	private String strPath;
	private File extFile;
	private File extPath;
	private String CURRENT_LOGFILE_NAME;
	private Context context;
	//DateFormat filedateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	
	public ExtFileLogger(Context ctx, String path){
		strPath = path;
		context = ctx;
		newFileLogName();
	}
	
	public void newFileLogName(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String ct = sdf.format(cal.getTime());
        setLogFileName(ct +'.'+ "log");
	}
	
	public void setLogFileName(String Name){
		CURRENT_LOGFILE_NAME = Name;
		compressIntFiles2Ext();
	}
	
    public void logExt(String logText) {
    	String logStr = logText + "\n"; 
    	writeExtFile(logStr, CURRENT_LOGFILE_NAME);
    }
    public void logInt(String s){
    	String logStr = s + "\n"; 
    	writeFlash(logStr);
    }
    public void compressInt2Ext(){
    	compressIntFiles2Ext();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    private synchronized void compressIntFiles2Ext() {
    	if (!isExtStorageWritable(strPath))
    		return;
        try {
			extPath = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/"+ strPath);
		} catch (IOException e) {
			return;
		}
		
        String filename = "";
        String filelist[] = context.fileList();
        int listLen = filelist.length;
        for (int i = 0; i < listLen; i++) {
            filename = filelist[i];
            if ( filename.equals(CURRENT_LOGFILE_NAME)){
            	continue;
            }
            
            if ( isExtStorageWritable(strPath)) {
            //if (extWritable) {
                try {
                    // Create the GZIP output stream
                    String gzFilename = filename +  ".gz";

                    GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(new File(extPath, gzFilename)));

                    // Open the input file
                    FileInputStream in = new FileInputStream(new File(context.getFilesDir(),filename));

                    // Transfer bytes from the input file to the GZIP output stream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();

                    // Complete the GZIP file
                    out.finish();
                    out.close();
                    context.deleteFile(filename);
                } catch (IOException e) {
                    Log.d("CompressLog", "Failed to compress file");
                    Log.e("Compresslog" , e.toString());
                } 
                
            } else {
                Log.d(TAG, "No need to compress file or external Storage not available");
            }
        }
         
    }
    
    
    private synchronized void writeFlash(String s){
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(CURRENT_LOGFILE_NAME, Context.MODE_APPEND);
            //Log.d(TAG, "Opened file " + FILENAME);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Unable to open file " + CURRENT_LOGFILE_NAME);
        }
        try {
            if (fos != null) {
                fos.write(s.getBytes());
                fos.close();
                //Log.d(TAG, "Wrote to file " + FILENAME);
            }
        } catch (IOException e) {
            Log.d(TAG, "Unable to write to file " + CURRENT_LOGFILE_NAME);
        }
    }
    
    
    private synchronized void writeExtFile(String s, String FILENAME){
    	if (!isExtStorageWritable(strPath))
    		return;
    	
		try {
			extFile = new File(Environment.getExternalStorageDirectory().getCanonicalPath() +"/"+ strPath ,FILENAME);
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
