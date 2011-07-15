package com.yifeijiang.android.accelerometer;

import java.util.ArrayList;

public class AccelMotion {
	public  String readings = "";
	private String logString = "";
	ArrayList<ArrayList<Long>> allAcc = new ArrayList<ArrayList<Long>>();
	public  int MOVE_FLAG = 3;
	public  boolean MOVE_STATUS = false;
	public  int Variance = 0;
	
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
    	Variance = 0;
    	
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
