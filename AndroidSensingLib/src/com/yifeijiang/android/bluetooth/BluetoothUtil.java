package com.yifeijiang.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

public class BluetoothUtil {

	public static void Enable(Context context, BluetoothAdapter mBluetoothAdapter){
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    enableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    context.startActivity(enableIntent);
		}
		
	}
	
	public static void enableDiscoverable(Context context){
		
		//if (mBluetoothAdapter.getScanMode() !=
	    //BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(discoverableIntent);
		
	}
	
	
}
