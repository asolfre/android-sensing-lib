package com.yifeijiang.android.phone;

import android.content.Context;
import android.location.GpsStatus;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;


public class PhoneReader {
    
    TelephonyManager telManager;
    int phoneType;
    Listener listener;
    
    public static abstract class Listener {

        public static final int ERR_OK = 0;
        public static final int ERR_INIT_FAILED = 1;
        public static final int ERR_READ_FAILED = 2;

        public abstract void onReadCellComplete(double cID, double nID );
        public abstract void onReadCellSigComplete(double cID, double nID, double sig );
        public abstract void onReadError(int error);
    }
    
    
    public PhoneReader(Context context, Listener lis) {
    	
    	listener = lis;
        telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneType = telManager.getPhoneType();
        telManager.listen(phoneListner, android.telephony.PhoneStateListener.LISTEN_CELL_LOCATION | 
                android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
        
    }
    
    
    
    PhoneStateListener phoneListner = new PhoneStateListener() {
    	
    	public void onCellLocationChanged(CellLocation Loc){
    		int cID = -1;
    		int nID = -1;
    		//int sID = -1; 
            switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_GSM:
                GsmCellLocation gsmCellLoc = (GsmCellLocation) telManager.getCellLocation();
                if (gsmCellLoc != null) {
                	nID = gsmCellLoc.getLac();
                    cID = gsmCellLoc.getCid();
                }
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                CdmaCellLocation cdmaCellLoc = (CdmaCellLocation) telManager.getCellLocation();
                if (cdmaCellLoc != null) {
                	nID = cdmaCellLoc.getNetworkId();
                    cID = cdmaCellLoc.getBaseStationId();
                    //sID = cdmaCellLoc.getSystemId();
                }
                break;
            }
            listener.onReadCellComplete(cID, nID);
    	}
    	
        public void onSignalStrengthChanged (int asu) {
            //Log.i("PhoneListner", "signal strength: " + asu);
            int cID = -1;
            int nID =-1;
            switch (phoneType) {
                case TelephonyManager.PHONE_TYPE_GSM:
                    GsmCellLocation gsmCellLoc = (GsmCellLocation) telManager.getCellLocation();
                    if (gsmCellLoc != null) {
                        cID = gsmCellLoc.getCid();
                        nID = gsmCellLoc.getLac();
                    }
                    break;
                case TelephonyManager.PHONE_TYPE_CDMA:
                    CdmaCellLocation cdmaCellLoc = (CdmaCellLocation) telManager.getCellLocation();
                    if (cdmaCellLoc != null) {
                        cID = cdmaCellLoc.getBaseStationId();
                        nID = cdmaCellLoc.getNetworkId();
                    }
                    break;
                
            }
            
            //String s = "\"CELL_SIGNAL_STRENGTH\":"+ asu;
            //s = s + "," + "\"GSM_CELL_ID\":"+ cID;
            listener.onReadCellSigComplete(cID, nID, asu);
            
        }
        
        public void onServiceStateChanged (ServiceState serviceState) {
            String state = "";
            switch (serviceState.getState()) {
                case 0:  state = "STATE_IN_SERVICE"; break;
                case 1:  state = "STATE_OUT_OF_SERVICE"; break;
                case 2:  state = "STATE_EMERGENCY_ONLY"; break;
                case 3:  state = "STATE_POWER_OFF"; break;
            }
                    
            //Log.i("Servicestate", "Service State: " + state);
            String s = "\"CELL_SERVICE_STATE\":\""+ state + "\"";

        }

    };
    

    
 

}
