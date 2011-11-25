package com.yifeijiang.android.accelerometer;

import java.util.ArrayList;

public class AccelMotion {
	public  String readings = "";
	private String logString = "";
	ArrayList<ArrayList<Long>> allAcc = new ArrayList<ArrayList<Long>>();
	ArrayList<ArrayList<Long>> AccSeq = new ArrayList<ArrayList<Long>>();
	final int AccSeqLength = 5;
	
	public  int MOVE_FLAG = 3;
	public  boolean MOVE_STATUS = false;

	
	public void addAccel(long x, long y, long z){
		ArrayList<Long> acc = new ArrayList<Long>();
		acc.add(x);
		acc.add(y);
		acc.add(z);
		
		if (AccSeq.size()< AccSeqLength){
			AccSeq.add(acc);
		}
		else{
			
			while ( AccSeq.size() >= AccSeqLength ){
				AccSeq.remove(0);
			}
			AccSeq.add(acc);	
		}
	}
	public boolean isStationary(){
		
		if (AccSeq.size()< AccSeqLength){
			return false;
		}
		else{
			
			if ( getVariance() > 50 ){
				return false;
			}
			else if ( getVariance() == -1 ){
				return false;
			}
			else{
				return true;
			}
			
		}
	}
	
	public int getVariance(){
	   	ArrayList<Long> LastAcc = null;
    	ArrayList<Long> curAcc = null;
    	int Variance = 0;
    	
    	for(int i =0;i<AccSeq.size();i++){
    		if (LastAcc == null) LastAcc = AccSeq.get(i);
    		else{
    			curAcc = AccSeq.get(i);
    			for (int j =0; j<3;j++)
    				Variance += Math.abs(curAcc.get(j)- LastAcc.get(j));
    		}
    	} 

    	/////////////
    	if (AccSeq.size()>0)
    		Variance = Variance/AccSeq.size()/3;
    	else
    		Variance = -1;
    	
    	return Variance;
	}
	
	
	public boolean getMotion(ArrayList<ArrayList<Long>> accelerationSet){
    	int m = Move();
    	if (m == 1){
    		/////HandlerAcc.obtainMessage(MAQSService.ACC_MOVE, 1, -1).sendToTarget();
    		MOVE_STATUS = true;
    		////MAQSService.logger.log("\"ACT\":"+"\"moving\"");

    	}
    	else if (m == 0){
    		//////HandlerAcc.obtainMessage(MAQSService.ACC_STATIONARY, 0, -1).sendToTarget();
    		MOVE_STATUS = false;
    		////MAQSService.logger.log("\"ACT\":"+"\"stationary\"");
    	}
    	return MOVE_STATUS;
	}
	
	
   public int Move(){
    	
    	ArrayList<Long> LastAcc = null;
    	ArrayList<Long> curAcc = null;
    	int Variance = 0;
    	
    	for(int i =0;i<allAcc.size();i++){
    		if (LastAcc == null) LastAcc = allAcc.get(i);
    		else{
    			curAcc = allAcc.get(i);
    			for (int j =0; j<3;j++)
    				Variance += Math.abs(curAcc.get(j)- LastAcc.get(j));
    		}
    	} 

    	/////////////
    	if (allAcc.size()>0)
    		Variance = Variance/allAcc.size()/3;
    	else
    		Variance = 0;
    	/////////////
    	
    	allAcc.clear();
    	if (Variance > 50){
    		if (MOVE_FLAG <6) 
    			MOVE_FLAG += 1;
    	}
    	else{
    		if (MOVE_FLAG > 0)
    			MOVE_FLAG -= 1;
    	}
    	
    	if (MOVE_FLAG >=6 && MOVE_STATUS == false) 
    		return 1;//move
    	else if (MOVE_FLAG <= 0 && MOVE_STATUS == true)
    		return 0;// stationary
    	else
    		return -1;// no change
    }

}
