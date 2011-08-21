package com.yifeijiang.android.phone;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneInfo {
	public static String getIMEI(Context context){
		String IMEI;
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telManager.getDeviceId();
        return IMEI;
	}
}
