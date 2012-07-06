package com.yifeijiang.android.cloud;

public interface ServerUploaderListener {
	
	public void onUploadServerResponse(String pPath, String pFilename, String pUrl, String pKey, String Status, String ReturnInfo);
	public void onUploadNetworkError( String pPath, String pFilename, String pUrl, String pKey, String ErrorMsg,  String ErrorInfo);
	
}
