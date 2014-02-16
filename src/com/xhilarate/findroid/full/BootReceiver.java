package com.xhilarate.findroid.full;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
	
	private String phoneNumber,serialNumber,IMSI,deviceID,alertNumber;
	private static Context context;
	public static final String FINDROID_PREFS = "findroid_preferences";
	public static final String REGISTERED_SIMS = "registeredSims";
	public static final String ALERT_NUMBER = "alertNumber";
	public static final String SIM_DETECT = "enableSimChangeDetect";
	private TelephonyManager mTelephonyMgr;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		context=arg0;
		Log.d("FinDroid", "BootReceiver: onReceive");
		//context.startService(new Intent(context, MainService.class));
		mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SIM_DETECT,true))
		{
			try {
			getCurrentSimDetails();
			if (checkSimChange())
			{
				getLocation();
				Log.d("FinDroid", "Sim card invalid!");
			}
			else
			{
				Log.d("FinDroid", "Sim card valid!");
			}
			}
			catch(Exception e) {
				Log.d("FinDroid", "Sim card detection failed! Possibly flight mode or no sim card inserted!!" + e.getMessage());
				Toast.makeText(context, "FinDroid: Flight Mode" + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}

	private void getCurrentSimDetails()
	{
		phoneNumber=mTelephonyMgr.getLine1Number();
		serialNumber=mTelephonyMgr.getSimSerialNumber();
		IMSI=mTelephonyMgr.getSubscriberId();
		deviceID=mTelephonyMgr.getDeviceId();
		
		if (phoneNumber == null || phoneNumber.equals(""))
			phoneNumber = "Unknown";
		if (serialNumber == null || serialNumber.equals(""))
			serialNumber = "Unknown";
		if (IMSI == null || IMSI.equals(""))
			IMSI = "Unknown";
		if (deviceID == null || deviceID.equals(""))
			deviceID = "Unknown";
				
	}

	private boolean checkSimChange() 
	{
		String prevList = context.getSharedPreferences(FINDROID_PREFS, 0)
		.getString(REGISTERED_SIMS, "");
		if (prevList == null)
			prevList = "";
		if (prevList.contains(serialNumber)) 
		{
			LOG.d("BootReceiver: Sim registered");
			Toast.makeText(context, "FinDroid: Sim is registered", Toast.LENGTH_SHORT).show();
			return false;
		}
		else
		{
			LOG.d("BootReceiver: Sim not registered");
			Toast.makeText(context, "FinDroid: Sim is not registered", Toast.LENGTH_SHORT).show();
			alertNumber=PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString(ALERT_NUMBER, "");
			if (alertNumber==null || alertNumber.equals(""))
				alertNumber = "9773113914";
				//LOG.d("BootReceiver: No alert number defined");
			//Toast.makeText(context, "FinDroid: No alert number defined", Toast.LENGTH_SHORT).show();
			return true;
		}
			
	}
	
	private String getSimInfo()
	{
		String simMsg;
		simMsg="Phone number: "+phoneNumber
				+"\nSerial Number: "+serialNumber
				+"\nSubscriber ID: "+IMSI
				+"\nDevice ID: "+deviceID
				+"\nCell location: "+mTelephonyMgr.getCellLocation().toString();
		return simMsg;
	}
	
	private void sendSMS(String msg)
	{
		final SmsManager sm = SmsManager.getDefault();
		try 
		{
			//sm.sendMultipartTextMessage(alertNumber, null,
					//sm.divideMessage(msg), null, null);
			LOG.d("BootReceiver: SMS sent");
		} 
		catch (Exception e) 
		{
			LOG.e("BootReceiver: SMS not sent: "+e.getMessage());
		}
		
	}
	
	private void getLocation()
	{
		final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationlistener = new LocationListener()
		{
			public void onLocationChanged(Location location) {
				if(location!=null)		
				{
				String locMsg = "\nGPS location: " + location.getLatitude()+ ", " + location.getLongitude();
				sendSMS(getSimInfo()+locMsg);
				lm.removeUpdates(this);
				}
			}

			public void onProviderDisabled(String provider) {
				String locMsg = "\nGPS provider disabled";
				sendSMS(getSimInfo()+locMsg);
				LOG.d("BootReceiver: Provider disabled");
			}

			public void onProviderEnabled(String provider) {}

			public void onStatusChanged(String provider, int status,Bundle extras) {}	
		};
		lm.requestLocationUpdates("gps", 60000, 10, locationlistener);
	}

}
