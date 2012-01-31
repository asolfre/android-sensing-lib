package com.yifeijiang.android.wifi;

import java.util.List;

import android.net.wifi.ScanResult;

public interface WiFiSignalScannerListener {
	  public  final int ERR_OK = 0;
      public  final int ERR_WIFI_DISABLED = 1;
      public  final int ERR_READ_FAILED = 2;
      
      public  void onScanComplete(long epochTime, List<ScanResult> scanResults);        
      public  void onScanError(int error);
}
