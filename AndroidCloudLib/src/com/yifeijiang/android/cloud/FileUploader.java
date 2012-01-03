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
	
	//static public HttpResponse response;
	
	public  static boolean upload(File dir, String filename, String url, String Key, boolean DELETE_UPLOADED) {
		HttpResponse response;
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
        File f = new File(dir, filename);

        HttpPost httpost = new HttpPost( url );
        MultipartEntity entity = new MultipartEntity();
        entity.addPart(Key, new FileBody(f));
        httpost.setEntity(entity);
        
		try {
            response = httpclient.execute(httpost);
        } catch (Exception ex) {
            Log.d("UPLOAD", "Upload failed: " + ex.getMessage() + " Stacktrace: " + ex.getStackTrace());
            String info = getErrorInfoFromException(ex);
            Log.d("UPLOAD", info);
            errorLog(dir, filename, "NO RESPONSE!");
            return false;
        }
        
        String status_line = response.getStatusLine().toString();
        String return_error_info = null; 
		try {
			return_error_info = EntityUtils.toString(response.getEntity());
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e){
			
		}
		
        
        if (entity != null) {
            try {
				entity.consumeContent();
			} catch (UnsupportedOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
        
        Log.d("UPLOAD", "status_line >>> " + status_line);
        Log.d("UPLOAD", "return_error_info >>> " + return_error_info);
        
        if (status_line.contains("HTTP/1.1 2") || return_error_info.equals("OK")) {
        	
            File oldName = new File(dir, filename);
            if ( DELETE_UPLOADED ){
            	oldName.delete();
            }
            else{
            	File newName = new File( dir, "uploaded." + filename );
            	oldName.renameTo(newName);
            }
            return true;
        }
        else if (status_line.contains( "HTTP/1.1 5" )){
            errorLog( dir, filename, return_error_info );
            return false;
        }
        else {
            errorLog( dir, filename, return_error_info );
            return false;
        }
	}
	
	private static void errorLog(File dir,String fn, String info){

		
   		try {
   			
			File extFile = new File(dir.getAbsolutePath()+"/errorlog" , fn + "_error_log_" + nowFileFormat()+ ".html");
			FileWriter fw = new FileWriter(extFile, false);
			fw.write( info );
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
