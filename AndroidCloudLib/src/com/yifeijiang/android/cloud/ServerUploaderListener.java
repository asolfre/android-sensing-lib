package com.yifeijiang.android.cloud;

public interface ServerUploaderListener {
	
	public void onUploadError(String ErrorMsg, String ErrorPage);
	public void onUploadSuccess();
	
}
