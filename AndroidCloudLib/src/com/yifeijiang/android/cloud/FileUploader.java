package com.yifeijiang.android.cloud;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Environment;
import android.util.Log;

public class FileUploader {
	
	static public HttpResponse response;
	
	public static String upload(File dir, String filename, String url) {
		
		String result = "";
	        
		try {
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
            File f = new File(dir, filename);

            HttpPost httpost = new HttpPost(url);
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("ALLDATA", new FileBody(f));
            httpost.setEntity(entity);

            response = httpclient.execute(httpost);
	        result = response.getStatusLine().toString();
	        
	        Log.d("httpPost", "Response: " + result);

            if (entity != null) {
                entity.consumeContent();
            }
            
        } catch (Exception ex) {
            Log.d("Uploader", "Upload failed: " + ex.getMessage() + " Stacktrace: " + ex.getStackTrace());
        }
        
        return result;
        
	}
}
