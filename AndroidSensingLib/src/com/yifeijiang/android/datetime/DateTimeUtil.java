package com.yifeijiang.android.datetime;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeUtil {
	public static String now(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ct = sdf.format(cal.getTime());
        return ct;
	}
	
	public static String nowFileFormat(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String ct = sdf.format(cal.getTime());
        return ct;
	}
}
