package com.yifeijiang.android.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class FormSubmission {

	private HttpClient httpClient;
	private HttpPost httpPost;
	private String responseString = null;
	
	
	public String Submit(String url, String[] keys, String[] values){
		
		httpClient= new DefaultHttpClient();
		httpPost = new HttpPost(url);
		
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		for (int i = 0; i< keys.length; i++){
			data.add(new BasicNameValuePair(keys[i],values[i]));
		}
		
		try{
			httpPost.setEntity( new UrlEncodedFormEntity( data ) );
			HttpResponse httpResponse = httpClient.execute( httpPost );
			responseString = EntityUtils.toString( httpResponse.getEntity() );
		}
		catch (ClientProtocolException e) { 
		  e.printStackTrace();
		  return null;
		} 
		catch (IOException e) { 
		   e.printStackTrace(); 
		   return null;
		 }
		
		 return responseString;
	}
}
