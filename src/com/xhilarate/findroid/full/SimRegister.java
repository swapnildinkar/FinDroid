package com.xhilarate.findroid.full;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SimRegister extends Activity
{
	public static final String FINDROID_PREFS = "findroid_preferences";
	public static final String REGISTERED_SIMS = "registeredSims";
	private String serialNumber="";
	private String phoneNumber="";
	private String imsiNumber="";
	TextView phone,serial,imsi,alert,regSims;
	Button btnReg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sim_register_activity);
		phone = (TextView) findViewById(R.id.simInfo1);
		serial = (TextView) findViewById(R.id.simInfo2);
		imsi = (TextView) findViewById(R.id.simInfo3);
		alert = (TextView) findViewById(R.id.simAlert);
		regSims = (TextView) findViewById(R.id.registeredSims);
		setCurrentSimDetails();
		setRegisteredSimSerials();
		btnReg=(Button) findViewById(R.id.btnRegister);
		btnReg.setOnClickListener (new OnClickListener(){
		    	public void onClick(View v){
		    		registerCurrentSim();		    		
		    	}});
	}

	private void setRegisteredSimSerials() {
		String prevList = getSharedPreferences(FINDROID_PREFS, 0)
		.getString(REGISTERED_SIMS, "");
		if(prevList==null || prevList.equals(""))
		{
			prevList="No sim cards registered yet.";
		}
		regSims.setText(prevList);
		
	}

	private void registerCurrentSim() {
		String prevList = getSharedPreferences(FINDROID_PREFS, 0)
		.getString(REGISTERED_SIMS, "");
		if (prevList == null)
		prevList = "";
		if (!prevList.contains(serialNumber)) {
			String newList = prevList + serialNumber + "\n";
			getSharedPreferences(FINDROID_PREFS, 0).edit()
			.putString(REGISTERED_SIMS, newList).commit();
			Toast.makeText(getApplicationContext(),"Sim card registered !", Toast.LENGTH_SHORT).show();
			}
		else {
			Toast.makeText(getApplicationContext(),"This sim card is already registered !", Toast.LENGTH_SHORT).show();
		}
		setCurrentSimDetails();
		setRegisteredSimSerials();
	}

	private void setCurrentSimDetails() 
	{
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phoneNumber=mTelephonyMgr.getLine1Number();
		serialNumber=mTelephonyMgr.getSimSerialNumber();
		imsiNumber=mTelephonyMgr.getSubscriberId();
		if (phoneNumber == null || phoneNumber.equals(""))
			phoneNumber = "Unknown";
		if (serialNumber == null || serialNumber.equals(""))
			serialNumber = "Unknown";
		if (imsiNumber == null || imsiNumber.equals(""))
			imsiNumber = "Unknown";
		phone.setText("Phone Number: "+phoneNumber);
		serial.setText("Serial Number: "+serialNumber);
		imsi.setText("IMSI Number: "+imsiNumber);
		String prevList = getSharedPreferences(FINDROID_PREFS, 0)
		.getString(REGISTERED_SIMS, "");
		if (prevList == null)
		prevList = "";
		if(mTelephonyMgr.getSimSerialNumber()==null) {
			//No sim card inserted
			alert.setTextColor(Color.RED);
			alert.setText("No sim card inserted!");
		}
		else {
			if (prevList.contains(serialNumber)) 
			{
				alert.setTextColor(Color.BLUE);
				alert.setText("This sim card is registered.");
			}
			else
			{
				alert.setTextColor(Color.RED);
				alert.setText("This sim card is not registered.");
			}
		}
	}

}