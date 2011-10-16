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
        		
        		
                if (result.equals("HTTP/1.1 200 OK")) {
                	
                    File oldName = new File(filePath, fn);
                    if ( DELETE_UPLOADED ){
                    	oldName.delete();
                    }
                    else{
                    	File newName = new File( filePath, "uploaded." + fn );
                    	oldName.renameTo(newName);
                    }
                }
                else if (result.equalsIgnoreCase( "HTTP/1.1 500 INTERNAL SERVER ERROR" )){
                	
                    //File oldName = new File(filePath, fn );
                    //File newName = new File(filePath, "error."+fn );
                    //oldName.renameTo(newName);    
                    errorLog( fn );
                    
                }
                else{
                	errorLog( fn );
                }
                
        	}
        	
        }
	}
	
	private void errorLog(String fn){
		String returnErrInfo = "";
		if (FileUploader.response == null){
			returnErrInfo = "NO Response";
		}
		else{
			try {
					returnErrInfo = EntityUtils.toString(FileUploader.response.getEntity());
				} catch (ParseException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		
   		try {
   			
			File extFile = new File(filePath , fn + "_error_log_" + nowFileFormat()+ ".html");
			FileWriter fw = new FileWriter(extFile, false);
			fw.write( returnErrInfo );
			fw.close();
			
        } catch (IOException e) {
            //Log.e(TAG, "Failed to create dirs");
        }
	}
	
	private String nowFileFormat(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String ct = sdf.format(cal.getTime());
        return ct;
	}
}
