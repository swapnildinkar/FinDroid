package com.xhilarate.findroid.full;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class SearchMap extends MapActivity
{
	public static final String FINDROID_PREFS = "findroid_preferences";
	public static final String LOST_PHONE_DATA="lostPhoneData";
	public static final String LOST_PHONE_NO="lostPhoneNo";
	public static final String LOST_PHONE_LAT="lostPhoneLat";
	public static final String LOST_PHONE_LON="lostPhoneLon";
	MapView mapView = null;
	MapController mapController = null;
	MyLocationOverlay myLocation = null;
	Drawable lostPhoneMarker=null;
	double lat,lon;
	String phone;
	@Override
	protected void onCreate(Bundle icicle) 
	{
		super.onCreate(icicle);
		setContentView(R.layout.search_map_activity);
		try
		{
			SharedPreferences lostPhoneData = this.getSharedPreferences(FINDROID_PREFS,0);
			lat=new Double(lostPhoneData.getFloat(LOST_PHONE_LAT, 0));
			lon=new Double(lostPhoneData.getFloat(LOST_PHONE_LON, 0));
			phone=lostPhoneData.getString(LOST_PHONE_NO, "");
		}
		catch(Exception e)
		{
			Log.e("SearchMap",e.toString());
		}
		lostPhoneMarker=getResources().getDrawable(R.drawable.ic_lost_phone);
		lostPhoneMarker.setBounds(0, 0, lostPhoneMarker.getIntrinsicWidth(),lostPhoneMarker.getIntrinsicHeight());
		mapView = (MapView)findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(10);
		myLocation = new MyLocationOverlay(this, mapView);
		LostPhoneLocation lost=new LostPhoneLocation(lostPhoneMarker);
		mapView.getOverlays().add(myLocation);
		if(lat!=0 && lon!=0) {
			mapView.getOverlays().add(lost);
			Toast.makeText(this, "Phone is at "+lat+", "+lon, Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(this, "No lost phone saved yet!", Toast.LENGTH_SHORT).show();
		mapView.postInvalidate();
		
	}
	@Override
	protected boolean isLocationDisplayed() {
	return myLocation.isMyLocationEnabled();
	}
	@Override
	protected boolean isRouteDisplayed() {
	return false;
	}

	@Override
	public void onResume()
	{
	super.onResume();
	myLocation.enableMyLocation();
	myLocation.runOnFirstFix(new Runnable() {
	public void run() {
	mapController.setCenter(myLocation.getMyLocation());
	}
	});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.my_loc_menu_item:
			if(lat!=0 && lon!=0) {
				if(myLocation.getMyLocation()!=null)
				{
					mapController.animateTo(myLocation.getMyLocation());
				}
				else
				{
					Toast.makeText(this,"Waiting for your location...",Toast.LENGTH_SHORT).show();
				}
			}
			else {
				Toast.makeText(this, "No lost phone saved yet!", Toast.LENGTH_SHORT).show();
			}
		break;
		case R.id.navigate_menu_item:
			navigate();
		break;
		case R.id.lost_loc_menu_item:
			if(lat!=0 && lon!=0) 
				mapController.animateTo(new GeoPoint((int)(lat*1E6),(int)(lon*1E6)));
			else
				Toast.makeText(this, "No lost phone saved yet!", Toast.LENGTH_SHORT).show();
		break;
		case R.id.phone_options:
			if(lat!=0 && lon!=0)
				startActivity(new Intent(this, PhoneOptionsActivity.class));
			else
				Toast.makeText(this, "No lost phone saved yet!", Toast.LENGTH_SHORT).show();
		break;
		}
		return true;
	}
	private void navigate()
	{
		double myLatitude,myLongitude;
		if(myLocation.getMyLocation()!=null)
		{
			myLatitude=myLocation.getMyLocation().getLatitudeE6()/1E6;
			myLongitude=myLocation.getMyLocation().getLongitudeE6()/1E6;
			String link="http://maps.google.com/maps?f=d&saddr=" + myLatitude + ","+myLongitude + "&daddr=" + lat + "," + lon;
			startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(link)));
		}
		else
		{
			Toast.makeText(this,"Waiting for your location...",Toast.LENGTH_SHORT).show();
		}
	}

	class LostPhoneLocation extends ItemizedOverlay {
		private List<OverlayItem> locations = new ArrayList<OverlayItem>();
		private Drawable marker;
		GeoPoint lostPhonePoint;
		public LostPhoneLocation(Drawable marker)
		{
		super(marker);
		this.marker=marker;
		// create locations of the lost phone
		lostPhonePoint = new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
		locations.add(new OverlayItem(lostPhonePoint ,"Lost Phone", phone+":"+lat+","+lon));
		populate();
		}
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, false);
		boundCenterBottom(marker);
		}
		@Override
		protected OverlayItem createItem(int i) {
		return locations.get(i);
		}
		@Override
		public int size() {
		return locations.size();
		}		
		}
}