package com.xhilarate.findroid.full;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneOptionsActivity extends Activity implements OnClickListener {
	
	private Button updateLocation;
	private Button ringPhone;
	private Button getDirections;
	private Button lockPhone;
	private Button wipePhone;
	
	private SmsManager mgr;
	private String phone = "";
	private String updateKeyword = "";
	private String ringKeyword, gpsKeyword, lockKeyword, wipeKeyword;
	
	public static final String GPS_KEYWORD="gpsKeyword";
	public static final String RING_KEYWORD="ringKeyword";
	public static final String WIPE_KEYWORD="wipeKeyword";
	public static final String LOCK_KEYWORD="lockKeyword";
	public static final String FINDROID_PREFS = "findroid_preferences";
	public static final String LOST_PHONE_DATA="lostPhoneData";
	public static final String LOST_PHONE_NO="lostPhoneNo";
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_options_activity);
        mgr = SmsManager.getDefault();
        
        updateLocation = (Button) findViewById(R.id.btn_update_location);
        ringPhone = (Button) findViewById(R.id.btn_ring_phone);
        lockPhone = (Button) findViewById(R.id.btn_lock_phone);
        wipePhone = (Button) findViewById(R.id.btn_wipe_phone);
        updateLocation.setOnClickListener(this);
        ringPhone.setOnClickListener(this);
        lockPhone.setOnClickListener(this);
        wipePhone.setOnClickListener(this);
        
        gpsKeyword=PreferenceManager.getDefaultSharedPreferences(this).getString(GPS_KEYWORD, "");
		ringKeyword=PreferenceManager.getDefaultSharedPreferences(this).getString(RING_KEYWORD, "");
		lockKeyword=PreferenceManager.getDefaultSharedPreferences(this).getString(LOCK_KEYWORD, "");
		wipeKeyword=PreferenceManager.getDefaultSharedPreferences(this).getString(WIPE_KEYWORD, "");
		
		SharedPreferences lostPhoneData = this.getSharedPreferences(FINDROID_PREFS,0);
		phone=lostPhoneData.getString(LOST_PHONE_NO, "None");
		((TextView) findViewById(R.id.txtPhoneNo)).setText("Phone: " + phone);
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_update_location:
			updateLocation();
			break;
		case R.id.btn_ring_phone:
			ringPhone();
			break;
		case R.id.btn_lock_phone:
			lockPhone();
			break;
		case R.id.btn_wipe_phone:
			wipePhone();
			break;
		}
		
	}

	private void wipePhone() {
		mgr.sendTextMessage(phone, null, "REQUIRED: wipe my phone", null, null);	
		raiseText();
	}

	private void lockPhone() {
		mgr.sendTextMessage(phone, null, "REQUIRED: lock my phone", null, null);
		raiseText();
	}

	private void ringPhone() {
		mgr.sendTextMessage(phone, null, "REQUIRED: ring my phone", null, null);
		raiseText();
	}

	private void updateLocation() {
		mgr.sendTextMessage(phone, null, "REQUIRED: locate my phone", null, null);
		raiseText();
	}
	
	private void raiseText() {
		Toast.makeText(this, "SMS Sent!", Toast.LENGTH_SHORT).show();
	}
}



