package com.yifeijiang.android.cloud;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
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
