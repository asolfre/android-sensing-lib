package com.yifeijiang.android.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class LogFileManager {
	
	Context context;
	String TAG = "LogManager";
	public LogFileManager( Context ctx){
		context = ctx;
	}
	
	public void gzContext2ExternalStorage(String contexFileName, String extPath){
		
    	if (!isExtStorageWritable(extPath))
    		return;
    	
        try {
            // Create the GZIP output stream
            String gzFilename = contexFileName +  ".gz";

            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(new File(extPath, gzFilename)));

            // Open the input file
            FileInputStream in = new FileInputStream(new File(context.getFilesDir(),contexFileName));

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
            context.deleteFile(contexFileName);
        } catch (IOException e) {
            Log.d("CompressLog", "Failed to compress file");
            Log.e("Compresslog" , e.toString());
        } 
    	/*
        String filename = "";
        String filelist[] = context.fileList();
        int listLen = filelist.length;
        for (int i = 0; i < listLen; i++) {
            filename = filelist[i];
            if ( filename.equals(fileName)){
            	continue;
            }
            
            if ( isExtStorageWritable(filePath)) {
            //if (extWritable) {

                
            } else {
                Log.d(TAG, "No need to compress file or external Storage not available");
            }
        }*/
	}
	
	public String[] getContextFiles(){
		String filelist[] = context.fileList();
		return filelist;
	}
	
	public String[] getExtFiles(String fpath){
		File filePath;
		filePath = new File(fpath);
		String[] filesname = filePath.list();
		return filesname;
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
            Log.d(TAG, "FOLDER CREATED!!!" + path);
        } else {
            extWritable = false;
            Log.d(TAG, "External media not available");
        }
        
        return extWritable;
    }
}
