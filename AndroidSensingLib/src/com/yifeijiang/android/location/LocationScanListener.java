package com.yifeijiang.android.location;

public interface LocationScanListener {
    public static final int ERR_OK = 0;
    public static final int ERR_INIT_FAILED = 1;
    public static final int ERR_READ_FAILED = 2;

    public abstract void onLocationReadComplete(double latitude, double longitude, double accuracy, long t);
}
