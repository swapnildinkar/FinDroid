package com.xhilarate.findroid.full;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class MainService extends Service{
	
	String thisphone;
	String pass;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "FinDroid Service Created!", Toast.LENGTH_SHORT).show();
		SharedPreferences file = this.getSharedPreferences(MainActivity.FINDROID_PREFS, 0);
		thisphone = file.getString("thisphone", "2");
		SharedPreferences passwdfile =this.getSharedPreferences(MainActivity.FINDROID_PREFS, 0);
		pass=passwdfile.getString(MainActivity.PASSWORD_PREF_KEY,"");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "FinDroid Service Stopped!", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		getLocation();
		Toast.makeText(this, "FinDroid Service Started", Toast.LENGTH_SHORT).show();
	}
	
	private void getLocation()
	{
		final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationlistener = new LocationListener()
		{
			public void onLocationChanged(Location location) {
				if(location!=null)		
				{
				String locMsg = "phn=" + thisphone + "&passwd=" + pass + "&lat=" + location.getLatitude()+ "&lng=" + location.getLongitude();
				Toast.makeText(MainService.this, locMsg, Toast.LENGTH_SHORT).show();
				ServerInterface.setLocation(locMsg);
				Toast.makeText(MainService.this, "Location updated!", Toast.LENGTH_SHORT).show();
				}
			}

			public void onProviderDisabled(String provider) {
				String locMsg = "\nGPS provider disabled";
				LOG.d("BootReceiver: Provider disabled");
			}

			public void onProviderEnabled(String provider) {}

			public void onStatusChanged(String provider, int status,Bundle extras) {}	
		};
		lm.requestLocationUpdates("gps", 60 * 60000, 10, locationlistener);
	}
}
