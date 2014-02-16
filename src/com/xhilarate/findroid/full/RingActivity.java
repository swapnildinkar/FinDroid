package com.xhilarate.findroid.full;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RingActivity extends Activity
{	
	public static final String RING_TIME = "ringTime";
	public static final int DURATION_OF_VIBRATION = 2000;
	public static final int VIBRATION_DELAY = 2000;
	private long ringTime;
	private Button btnStop;
	private Uri alert;
	AudioManager mgr;
	MediaPlayer player;
	Handler playerHandler;
	Handler vibrationHandler = new Handler();
	Runnable vibrationRunnable = new Runnable() {
		@Override
		public void run() {
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(DURATION_OF_VIBRATION);
			// Provide loop for vibration
			vibrationHandler.postDelayed(vibrationRunnable, DURATION_OF_VIBRATION + VIBRATION_DELAY);
		}
	};
	public void onCreate(Bundle savedInstanceState) 
	{
	      	super.onCreate(savedInstanceState);
	      	setContentView(R.layout.ring_activity);
	      	ringTime=Long.parseLong(PreferenceManager.getDefaultSharedPreferences(this).getString(RING_TIME, "5000"));
	      	btnStop=(Button) findViewById(R.id.btnStopRing);
	        alert=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			mgr=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
			player=new MediaPlayer();
			playerHandler=new Handler();
			ringPhone();
	        btnStop.setOnClickListener(new OnClickListener(){
	        public void onClick(View v)
	        {
	        	if(player.isPlaying())
	        	{
	        		player.stop();
	        		finish();
	        	}
	        }
	        });
	        
	 }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(player.isPlaying()) {
			player.stop();
		}
	}
	
	private void ringPhone()
	{
		try 
        {
			player.setDataSource(this, alert);
        	mgr.setStreamVolume(AudioManager.STREAM_ALARM, mgr.getStreamMaxVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_VIBRATE | AudioManager.FLAG_PLAY_SOUND);
        	player.setAudioStreamType(AudioManager.STREAM_ALARM);
        	player.setLooping(true);
        	player.prepare();
        	player.start();
        	vibrationHandler.post(vibrationRunnable);
        }
        catch(Exception e)
        {
        	LOG.e("RingActivity: Exception occurred, "+e.getMessage());
        }	
        playerHandler.postDelayed(new Runnable() {
			public void run()
			{
				player.stop();
				vibrationHandler.removeCallbacks(vibrationRunnable);
				finish();
			}
		}, ringTime);
	}
}

