package com.yifeijiang.android.cloud;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class FileUploader {
	
	static public HttpResponse response;
	
	public static String upload(File dir, String filename, String url, String Key) {
		
		String result = "";
	        
		try {
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
            File f = new File(dir, filename);

            HttpPost httpost = new HttpPost( url );
            MultipartEntity entity = new MultipartEntity();
            entity.addPart(Key, new FileBody(f));
            httpost.setEntity(entity);
            
            response = httpclient.execute(httpost);
	        result = response.getStatusLine().toString();
	        
	        Log.d("Uploader", "Response: " + result);

            if (entity != null) {
                entity.consumeContent();
            }
            
        } catch (Exception ex) {
            Log.d("Uploader", "Upload failed: " + ex.getMessage() + " Stacktrace: " + ex.getStackTrace());
            String info = getErrorInfoFromException(ex);
            Log.d("Uploader", info);
        }
        
        return result;
        
	}
	
	public static boolean processResult(String result,File dir, String fn, boolean DELETE_UPLOADED){
		Log.d("UPLOAD", result);
        if (result.contains("HTTP/1.1 2")) {
        	
            File oldName = new File(dir, fn);
            if ( DELETE_UPLOADED ){
            	oldName.delete();
            }
            else{
            	File newName = new File( dir, "uploaded." + fn );
            	oldName.renameTo(newName);
            }
            return true;
        }
        else if (result.contains( "HTTP/1.1 5" )){
        	
            //File oldName = new File(filePath, fn );
            //File newName = new File(filePath, "error."+fn );
            //oldName.renameTo(newName);    
            errorLog( dir, fn );
            return false;
        }
        else{
        	errorLog( dir,fn );
        	return false;
        }
        
		
	}
	
	private static void errorLog(File dir,String fn){
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
				} catch (Exception e){
					
				}
		}
		
   		try {
   			
			File extFile = new File(dir , fn + "_error_log_" + nowFileFormat()+ ".html");
			FileWriter fw = new FileWriter(extFile, false);
			fw.write( returnErrInfo );
			fw.close();
			
        } catch (IOException e) {
            //Log.e(TAG, "Failed to create dirs");
        }
	}
	
	private static String nowFileFormat(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String ct = sdf.format(cal.getTime());
        return ct;
	}
	
	
	
	public static String getErrorInfoFromException(Exception e) {  
        try {  
            StringWriter sw = new StringWriter();  
            PrintWriter pw = new PrintWriter(sw);  
            e.printStackTrace(pw);  
            return "\r\n" + sw.toString() + "\r\n";  
        } catch (Exception e2) {  
            return "bad getErrorInfoFromException";  
        }  
    }  
}
