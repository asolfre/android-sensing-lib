package com.yifeijiang.android.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.GZIPOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


public class ExtFileLogger {
	String TAG = "RoomLoc:FileLogger";
	DateFormat logdateFormat = new SimpleDateFormat("EEE MM,dd,yyyy HH:mm:ss");
	private String str_external_dir;
	private File file_external_file;
	private File file_external_path;
	private String current_logfile_name;
	private Context context;
	//DateFormat filedateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	
	public ExtFileLogger(Context ctx, String path){
		str_external_dir = path;
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
		current_logfile_name = Name;
		//compressIntFiles2Ext();
	}
	
    public void logExt(String logText) {
    	String logStr = logText + "\n"; 
    	writeExtFile(logStr, current_logfile_name);
    }
    public void logInt(String s){
    	String logStr = s + "\n"; 
    	writeFlash(logStr);
    }
    public void logInt(JSONObject obj){
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        try {
	        obj.put("CT", logdateFormat.format(calendar.getTime()) );
	        obj.put("T", now );
		} catch (JSONException e) {
			e.printStackTrace();
		}
		logInt(obj.toString());    	
    }
    public void compressInt2Ext(){
    	compressIntFiles2Ext();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    private synchronized void compressIntFiles2Ext() {
    	if (!isExtStorageWritable(str_external_dir))
    		return;
        try {
			file_external_path = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/"+ str_external_dir);
		} catch (IOException e) {
			return;
		}
		
        String filename = "";
        String filelist[] = context.fileList();
        int listLen = filelist.length;
        for (int i = 0; i < listLen; i++) {
            filename = filelist[i];
            if ( filename.equals(current_logfile_name)){
            	continue;
            }
            
            if ( isExtStorageWritable(str_external_dir)) {
            //if (extWritable) {
                try {
                    // Create the GZIP output stream
                    String gzFilename = filename +  ".gz";

                    GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(new File(file_external_path, gzFilename)));

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
            fos = context.openFileOutput(current_logfile_name, Context.MODE_APPEND);
            //Log.d(TAG, "Opened file " + FILENAME);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Unable to open file " + current_logfile_name);
        }
        try {
            if (fos != null) {
                fos.write(s.getBytes());
                fos.close();
                //Log.d(TAG, "Wrote to file " + FILENAME);
            }
        } catch (IOException e) {
            Log.d(TAG, "Unable to write to file " + current_logfile_name);
        }
    }
    
    
    private synchronized void writeExtFile(String s, String FILENAME){
    	if (!isExtStorageWritable(str_external_dir))
    		return;
    	
		try {
			file_external_file = new File(Environment.getExternalStorageDirectory().getCanonicalPath() +"/"+ str_external_dir ,FILENAME);
			FileWriter fw = new FileWriter(file_external_file, true);
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
            try {
                file_external_path = new File(Environment.getExternalStorageDirectory().getCanonicalPath() +"/"+ path);
                file_external_path.mkdirs();
            } catch (IOException e) {
                Log.e(TAG, "Failed to create dirs");
            }
            extWritable = true;
        } else {
            extWritable = false;
            Log.d(TAG, "External media not available");
        }
        
        return extWritable;
    }
}
