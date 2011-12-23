package com.yifeijiang.android.cloud;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import android.os.Environment;
import android.util.Log;

public class FileUploadThread extends Thread{
	
	private String path;
	private String fileRegex;
	private String url;
	private boolean DELETE_UPLOADED;
	private String Key;
	
	private File filePath;
	
	public FileUploadThread(String mpath, String mfileRegex, String murl, boolean mdeleteUploaded, String key){
		path = mpath;
		fileRegex = mfileRegex;
		url = murl;
		DELETE_UPLOADED = mdeleteUploaded;
		Key = key;
	}
	
	public synchronized void run(){
    	
    	
    	try {
    		filePath = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/"+ path);
		} catch (IOException e) {
			return;
		}
		
        String filesname[] = filePath.list();
        int fileNum = filesname.length;
        
        for (int i = 0; i < fileNum; i++) {
        	String fn = filesname[i];
        	
        	if ( Pattern.matches(fileRegex, fn) ) {
        		
        		String result = FileUploader.upload( filePath, fn, url ,Key );
        		FileUploader.processResult(result, filePath, fn, DELETE_UPLOADED);
                
        	}
        	
        }
	}
	

}
