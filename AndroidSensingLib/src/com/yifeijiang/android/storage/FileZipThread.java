package com.yifeijiang.android.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import android.os.Environment;
import android.util.Log;


public class FileZipThread extends Thread{
	private String path;
	private String fileRegex;
	private boolean DELETE_ZIPPED;
	
	private File filePath;
	
	public FileZipThread(String mpath, String mfileRegex, boolean mdeleteZipped){
		path = mpath;
		fileRegex = mfileRegex;
		DELETE_ZIPPED = mdeleteZipped;
	}
	
	public  void run(){
		
    	if (!StorageUtil.isExtStorageWritable())
    		return;

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
        		try {
	        		String gzFilename = fn + ".gz";
	        		GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(new File(filePath, gzFilename)));
	        		File filein = new File(filePath,fn );
	        		FileInputStream in = new FileInputStream( filein );
	        		
	        		if (DELETE_ZIPPED){
	        			filein.delete();
	        		}
	                
	        		// Transfer bytes from the input file to the GZIP output stream
	                byte[] buf = new byte[1024];
	                int len;
	                while ((len = in.read(buf)) > 0) {
	                    out.write(buf, 0, len);
	                }
	                in.close();
	
	                // Complete the GZIP file
	                out.finish();
	                out.close();
                } catch (IOException e) {
                    Log.d("CompressLog", "Failed to compress file");
                    Log.e("Compresslog" , e.toString());
                } 
        	}
        }
	}
	
	
	
	
}
