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

import android.content.Context;
import android.util.Log;

public class ServerUploader{

	ServerUploaderListener listener;
	String filepath;
	String filename;
	String url;
	String key;
	
	public ServerUploader(Context pContext, ServerUploaderListener pListener){
		
		listener = pListener;
		
	}
	
	public void upload(String pPath, String pFilename, String pUrl, String pKey){
		filepath = pPath;
		filename = pFilename;
		url = pUrl;
		key = pKey;
		new UploadThread().start();
	}
	

	private class UploadThread extends Thread{
		String tfilepath;
		String tfilename;
		String turl;
		String tkey;
		
		public synchronized void run(){
			tfilepath = filepath;
			tfilename = filename;
			turl = url;
			tkey= key;
			
			HttpResponse response;
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
	        File f = new File(tfilepath, tfilename);

	        HttpPost httpost = new HttpPost( turl );
	        MultipartEntity entity = new MultipartEntity();
	        entity.addPart(tkey, new FileBody(f));
	        httpost.setEntity(entity);
	        
	        
			try {
	            response = httpclient.execute(httpost);
	        } catch (Exception ex) {
	            //Log.d("UPLOAD", "Upload failed: " + ex.getMessage() + " Stacktrace: " + ex.getStackTrace());
	            String ErrorMsg = ex.getMessage();
	            //String ErrorStackTrace = ex.getStackTrace();
	            String ErrorInfo = getErrorInfoFromException(ex);
	            //Log.d("UPLOAD", info);
	           // errorLog(filepath, filename, "NO RESPONSE!");
	            listener.onUploadNetworkError(tfilepath, tfilename, turl, tkey,  ErrorMsg, ErrorInfo);
	            return;
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
	        listener.onUploadServerResponse(tfilepath, tfilename, turl, tkey, status_line, return_error_info);
	        
	        /*
	        if (status_line.contains("HTTP/1.1 2") || return_error_info.equals("OK")) {
	        	listener.onUploadSuccess(tfilepath, tfilename, turl, tkey);
	            //File oldName = new File(filepath, filename);
	            //if ( DELETE_UPLOADED ){
	            //	oldName.delete();
	            //}
	            //else{
	            //	File newName = new File( dir, "uploaded." + filename );
	            //	oldName.renameTo(newName);
	            //}
	            return;
	        }
	        else if (status_line.contains( "HTTP/1.1 5" )){
	        	listener.onUploadServerError(tfilepath, tfilename, turl, tkey, return_error_info);
	            //errorLog( tfilepath, tfilename, return_error_info );
	            return;
	        }
	        else {
	        	listener.onUploadServerError(tfilepath, tfilename, turl, tkey, return_error_info);
	            //errorLog( tfilepath, tfilename, return_error_info );
	            return;
	        }
*/
		}		
	}
	
	
	private static void errorLog(String dir,String fn, String info){

		
   		try {
   			File file_external_path = new File(dir+"/errorlog" );
   			file_external_path.mkdirs();
   			
			File extFile = new File(dir+"/errorlog" , fn + "_error_log_" + nowFileFormat()+ ".html");
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
