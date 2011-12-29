package com.yifeijiang.android.audio;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class SoundWarning {
	static MediaPlayer mMediaPlayer;
	
    public static void RingWarning(Context ctx, int seconds){
    	//mRedrawHandler.sleep(1000*60*10);
    	Handler stop_handler = new Handler(){
    		@Override
            public void handleMessage(Message msg) {
    			mMediaPlayer.stop();
    		}
    	};
    	Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE); 
    	 mMediaPlayer = new MediaPlayer();
    	 try {
			mMediaPlayer.setDataSource(ctx, alert);
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (IllegalStateException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
    	 final AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
    	 if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
    		 mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
    		 mMediaPlayer.setLooping(false);
    		 try {
				mMediaPlayer.prepare();
			} catch (IllegalStateException e) {

				e.printStackTrace();
				
			} catch (IOException e) {

				e.printStackTrace();
			}
    	     mMediaPlayer.start();
    	     stop_handler.removeMessages(0);
    	     stop_handler.sendMessageDelayed( stop_handler.obtainMessage(0), seconds*1000);
    	  }

    	 
    	 
     	//AudioManager mAudioManager = (AudioManager) this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
     	//int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
     	//int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
         //mAudioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_PLAY_SOUND);
         
         //mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, AudioManager.FLAG_PLAY_SOUND);
         //mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxVolume, AudioManager.FLAG_PLAY_SOUND);

     }
    
    public static void BipWarning(Context ctx){
		AudioManager mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
    	//int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    	int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, currentVolume, AudioManager.FLAG_PLAY_SOUND);
    }
}
