package com.xhilarate.findroid.full;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver
{
	public static final String FINDROID_PREFS = "findroid_preferences";
	public static final String LOST_PHONE_DATA="lostPhoneData";
	public static final String LOST_PHONE_NO="lostPhoneNo";
	public static final String LOST_PHONE_LAT="lostPhoneLat";
	public static final String LOST_PHONE_LON="lostPhoneLon";
	public static final String ENABLE_SERVICE="enableService";
	public static final String GPS_KEYWORD="gpsKeyword";
	public static final String RING_KEYWORD="ringKeyword";
	public static final String WIPE_KEYWORD="wipeKeyword";
	public static final String LOCK_KEYWORD="lockKeyword";
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static String correctMd5;
	private static String gpsKeyword,ringKeyword, wipeKeyword, lockKeyword;
	private static boolean abortInbox;
	public DevicePolicyManager mDPM;
	public ComponentName mDeviceAdmin;
	
	@Override
	public void onReceive(Context context, Intent intent) {	
		final Context mContext=context;
		initializeDPM(context);

		abortInbox = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("abortInbox", false); 
		gpsKeyword = PreferenceManager.getDefaultSharedPreferences(context).getString(GPS_KEYWORD, "locate my phone");
		ringKeyword = PreferenceManager.getDefaultSharedPreferences(context).getString(RING_KEYWORD, "");
		lockKeyword = PreferenceManager.getDefaultSharedPreferences(context).getString(LOCK_KEYWORD, "");
		wipeKeyword = PreferenceManager.getDefaultSharedPreferences(context).getString(WIPE_KEYWORD, "");
		correctMd5 = context.getSharedPreferences(FINDROID_PREFS, 0).getString(MainActivity.PASSWORD_PREF_KEY,null);
		
		SmsMessage[] messages = null;
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ENABLE_SERVICE, true))
		{
			LOG.d("SMSReceiver: FinDroid service active!");
			if(intent!=null && intent.getAction()!=null && ACTION.compareToIgnoreCase(intent.getAction())==0)
				{
				Object[] pduArray= (Object[]) intent.getExtras().get("pdus");
				messages=new SmsMessage[pduArray.length];
				for (int i = 0; i<pduArray.length; i++) {
				messages[i] = SmsMessage.createFromPdu ((byte[])pduArray [i]);
				}
				}
		
		if (correctMd5 != null) {
			for (SmsMessage msg : messages) {
				if (msg.getMessageBody().contains("LOCATE:") || msg.getMessageBody().contains("REQUIRED: locate my phone") || msg.getMessageBody().toLowerCase().contains(gpsKeyword.toLowerCase())) 
				{
					stop();
					processMessage(context,msg);
					Log.d("FinDroid", "Message arrived: " + msg.getMessageBody() + ", " + gpsKeyword);
				}
				else if (msg.getMessageBody().contains("LOCATION:") && !msg.getMessageBody().contains("GPS"))
				{
					stop();
					String[] tokens = msg.getMessageBody().split(":");
					if(tokens.length>=3)
					{
						try
						{
							final String phone=msg.getOriginatingAddress();
							Log.d("FinDroid", "Message sender: " + phone);
							String dlgMsg;
							dlgMsg="Lost phone's location received.\nLocation: "+tokens[1]+","+tokens[2];
							dlgMsg+="\nView the location on the map?";							
							Editor callMap = mContext.getSharedPreferences(FINDROID_PREFS,0).edit();
					        callMap.putFloat(LOST_PHONE_LAT, Float.parseFloat(tokens[1]));
					        callMap.putFloat(LOST_PHONE_LON, Float.parseFloat(tokens[2]));
					        callMap.putString(LOST_PHONE_NO, phone);
					        callMap.commit();
							Intent i=new Intent(mContext, LocationDlgActivity.class);
							i.putExtra("message", dlgMsg);
							i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							mContext.startActivity(i);
						}
						catch(Exception e)
						{
							LOG.e("SMSReceiver: "+e.getMessage());
						}
					}
				}
				else if(msg.getMessageBody().contains("REQUIRED: ring my phone") || msg.getMessageBody().toLowerCase().contains(ringKeyword.toLowerCase()))
				{
					stop();
					LOG.d("SMSReceiver: Ring request");
					callRinger(context);
				}
				else if(msg.getMessageBody().contains("REQUIRED: wipe my phone") || msg.getMessageBody().toLowerCase().contains(wipeKeyword.toLowerCase())) {
					stop();
					Log.v("FinDroid", "WIPE PHONE");
					mDPM.wipeData(0);
				}
				else if(msg.getMessageBody().contains("REQUIRED: lock my phone") || msg.getMessageBody().toLowerCase().contains(lockKeyword.toLowerCase())) {
					stop();
					if(mDPM.isAdminActive(mDeviceAdmin))
					mDPM.lockNow();
				}
				else {
					//do nothing
				}
				
			}
		}	
	}
	else
	{
		LOG.e("SMSReceiver: FinDroid service inactive!");
	}
	}
	
	private void initializeDPM(Context context) {
		mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdmin = new ComponentName(context, AdminReceiver.class);			
	}

	private void stop() {
		if(abortInbox)
		abortBroadcast();
	}
	private void processMessage(final Context context,SmsMessage msg)
	{
		if(msg.getMessageBody().contains("LOCATE:"))
		{
			String[] tokens = msg.getMessageBody().split(":");
			if (tokens.length >= 2) 
			{
				String md5hash = tokens[1];
				if (md5hash.equals(correctMd5)) 
				{
					LOG.d("SMSReceiver: Location request");
					sendLocation(context,msg);
					
				}
			}
		}
		else if(msg.getMessageBody().contains(gpsKeyword))
		{
			LOG.d("SMSReceiver: Location request");
			sendLocation(context,msg);
		}
		
	}
	private void sendLocation(final Context context, final SmsMessage msg) 
	{
		final String to = msg.getOriginatingAddress();
		final SmsManager sm = SmsManager.getDefault();
		//sm.sendTextMessage(to, null, "FinDroid: SMS Received.\nSending GPS coordinates.", null, null);
		final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationlistener=new LocationListener() 
		{
			
			public void onLocationChanged(Location location) {
				if(location!=null)		
				{
				double lat=location.getLatitude();		
				double lon=location.getLongitude();
				if(msg.getMessageBody().contains("LOCATE:")){
					sm.sendTextMessage(to, null, "LOCATION:"+lat+":"+lon, null, null);
				}
				else {
					sm.sendTextMessage(to, null, "LOCATION:" + "http://maps.google.com/maps?q=" + lat + "%20" + lon, null, null);
				}
				LOG.d("SMSReceiver: Location sent");
				lm.removeUpdates(this);
				}
			}

			
			public void onProviderDisabled(String provider) {
				sm.sendTextMessage(to, null, "FinDroid: SMS Received. GPS disabled", null, null);
			}

			
			public void onProviderEnabled(String provider) {}

			
			public void onStatusChanged(String provider, int status,Bundle extras) {}	
		};
		lm.requestLocationUpdates("gps", 60000, 10, locationlistener);
		
	}
	private void callRinger(Context context)
	{
		context.startActivity(new Intent(context,RingActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
	
}

