package com.xhilarate.findroid.full;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity
{
	public static final String FINDROID_PREFS = "findroid_preferences";
	public static final String PASSWORD_PREF_KEY = "passwd";
	public static final String NEW_PASSWORD_KEY="newPassword";
	public static final String LOST_PHONE_DATA="lostPhoneData";
	public static final String LOST_PHONE_NO="lostPhoneNo";
	public static final String LOST_PHONE_LAT="lostPhoneLat";
	public static final String LOST_PHONE_LON="lostPhoneLon";
	public static final String PASSWORD_PROTECT="passwordProtect";
	private String correctMD5;
	private boolean newPassword;
	private boolean getPassword;
	public static double lat=0,lon=0;
	public static String phone="";
	public static boolean smsSent=false;
	private Button btnStart;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //this.startService(new Intent(this, MainService.class));
        getPassword=PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PASSWORD_PROTECT, false);
        newPassword=this.getSharedPreferences(FINDROID_PREFS, 0).getBoolean(NEW_PASSWORD_KEY, true);
        if(newPassword)
        {
        	getNewPassword();
        }
        else if(getPassword)
        {
        	getPassword();
        }
        setContentView(R.layout.main_activity);
        //this.getSharedPreferences(FINDROID_PREFS,0).edit().putFloat(LOST_PHONE_LAT, (float) 18.0).putFloat(LOST_PHONE_LON, (float) 72.0).putString(LOST_PHONE_NO, phone).commit();
        btnStart=(Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new OnClickListener(){
    	public void onClick(View v){
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("Requirement")
                .setMessage("This feature requires FinDroid installed on the lost phone. Do you wish to continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton){
						getPhoneDetails();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	dialog.dismiss();
                    }
                })
                .show();
    		
    	}});
	}
	
	private void getNewPassword() 
	{
		LayoutInflater factory = LayoutInflater.from(this);
    	final View getNewPassword = factory.inflate(R.layout.set_password_dialog, null);
        final EditText txtPass1 = (EditText) getNewPassword.findViewById(R.id.password_1);
        final EditText txtPass2 = (EditText) getNewPassword.findViewById(R.id.password_2);
        new AlertDialog.Builder(this)
            .setTitle("Set a new password")
            .setView(getNewPassword)
            .setPositiveButton("Set", new DialogInterface.OnClickListener() 
            {
				public void onClick(DialogInterface dialog, int whichButton){
					String password1=txtPass1.getText().toString();
					String password2=txtPass2.getText().toString();
					if(password1.length()<4 || password2.length()<4 || password1.length()>10 || password2.length()>10)
					{
						dialog.dismiss();
						new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Password length")
                        .setMessage("Password length should be between 4-10 characters!")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton){
        						dialog.dismiss();
        						getNewPassword();
                            }
                        })
                        .show();
					}
					else if (password1.equals(password2))
                	{
						String md5hash = getMd5Hash(password1);
		        		Editor passwdfile = getSharedPreferences(FINDROID_PREFS, 0).edit();
		        		passwdfile.putString(PASSWORD_PREF_KEY, md5hash);
		        		passwdfile.commit();
		        		passwdfile=getSharedPreferences(FINDROID_PREFS,0).edit();
		        		passwdfile.putBoolean(NEW_PASSWORD_KEY, false);
		        		passwdfile.commit();
		        		Toast.makeText(getApplicationContext(),"Password set!",Toast.LENGTH_SHORT).show();
		        		
                	}
                	else
                	{
                		dialog.dismiss();
                		new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Mis-match")
                        .setMessage("Passwords do not match!")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                					public void onClick(DialogInterface dialog, int whichButton){
                						dialog.dismiss();
                						getNewPassword();                               
                            }
                        })
                        .show();
                	}
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
            {
                public void onClick(DialogInterface dialog, int whichButton) {
                	finish();
                }
            })
            .setCancelable(false)
            .show();		
	}
	
	private void getPassword() 
	{
		LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View textEntryView = factory.inflate(R.layout.get_password_dialog, null);
        final EditText txtPass = (EditText) textEntryView.findViewById(R.id.txtPassword);
        new AlertDialog.Builder(MainActivity.this)
        .setTitle("Password Required")
        .setView(textEntryView)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton){
				String md5hash=getMd5Hash(txtPass.getText().toString());
        		SharedPreferences passwdfile = getApplicationContext().getSharedPreferences(FINDROID_PREFS, 0);
        		correctMD5=passwdfile.getString(PASSWORD_PREF_KEY,"");
        		if(correctMD5.equals(md5hash))
        		{	
        			//Do nothing
        		}
        		else
        		{
        			dialog.dismiss();
        			new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Password incorrect")
                    .setMessage("The password you have enterred is incorrect.")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton){
    						dialog.dismiss();
    						getPassword();
                        }
                    })
                    .show();
        		}
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	finish();
            }
        })
        .setCancelable(false)
        .show();
   	}
	
	private void getPhoneDetails()	
	{
		LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View textEntryView = factory.inflate(R.layout.prompt_dialog, null);
        final EditText txtPhone = (EditText) textEntryView.findViewById(R.id.phone);
        final EditText txtPass = (EditText) textEntryView.findViewById(R.id.password);
        new AlertDialog.Builder(MainActivity.this)
            .setTitle("Search a new phone")
            .setView(textEntryView)
            .setPositiveButton("Search", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton){
					final String phone=txtPhone.getText().toString();
					final String password=txtPass.getText().toString();
					if (phone.length()==0 && password.length()==0)
                	{
						new AlertDialog.Builder(MainActivity.this)
               		 	.setTitle("Required")
               		 	.setMessage("Please enter all information correctly")
               		 	.show();
                	}
                	else
                	{
                		SmsManager sm= SmsManager.getDefault();
                		sm.sendTextMessage(phone, null, "LOCATE:"+getMd5Hash(password),null, null);
                		Toast.makeText(getApplicationContext(),"SMS sent to: "+phone,Toast.LENGTH_SHORT).show();
                	}
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	dialog.dismiss();
                }
            })
            .show();
	}
	
	public static String getMd5Hash(String input) 
	{
		try	
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1,messageDigest);
			String md5 = number.toString(16);
	    	while (md5.length() < 32)
				md5 = "0" + md5;
	    	
			return md5;
		} 
		catch(NoSuchAlgorithmException e) 
		{
			Log.e("MD5", e.getMessage());
			return input;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) 
	{
		switch(item.getItemId())
		{
			case R.id.resume_menu_item:
				Intent i=new Intent(MainActivity.this,SearchMap.class);
				startActivity(i);
				return true;
			case R.id.settings_menu_item:
				startActivity(new Intent(this,Settings.class));
				return true;
			case R.id.set_password_menu_item:
				changePassword();		
				return true;
			case R.id.help_menu_item:
				startActivity(new Intent(this,HelpActivity.class));
				return true;
			case R.id.register_menu_item:
				startActivity(new Intent(this,RegisterActivity.class));
				return true;
	}
		return true;
	}
	
	private void changePassword() {
		LayoutInflater factory = LayoutInflater.from(this);
    	final View getNewPassword = factory.inflate(R.layout.set_password_dialog, null);
        final EditText txtPass1 = (EditText) getNewPassword.findViewById(R.id.password_1);
        final EditText txtPass2 = (EditText) getNewPassword.findViewById(R.id.password_2);
        new AlertDialog.Builder(this)
            .setTitle("Set a new password")
            .setView(getNewPassword)
            .setPositiveButton("Set", new DialogInterface.OnClickListener() 
            {
				public void onClick(DialogInterface dialog, int whichButton){
					String password1=txtPass1.getText().toString();
					String password2=txtPass2.getText().toString();
					if(password1.length()<4 || password2.length()<4 || password1.length()>10 || password2.length()>10)
					{
						dialog.dismiss();
						new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Password length")
                        .setMessage("Password length should be between 4-10 characters!")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton){
        						dialog.dismiss();
        						changePassword();
                            }
                        })
                        .show();
					}
					else if (password1.equals(password2))
                	{
						String md5hash = getMd5Hash(password1);
		        		Editor passwdfile = getSharedPreferences(FINDROID_PREFS, 0).edit();
		        		passwdfile.putString(PASSWORD_PREF_KEY, md5hash);
		        		passwdfile.commit();
		        		passwdfile=getSharedPreferences(FINDROID_PREFS,0).edit();
		        		passwdfile.putBoolean(NEW_PASSWORD_KEY, false);
		        		passwdfile.commit();
		        		Toast.makeText(getApplicationContext(),"Password changed!",Toast.LENGTH_SHORT).show();
		        		
                	}
                	else
                	{
                		dialog.dismiss();
                		new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Mis-match")
                        .setMessage("Passwords do not match!")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton){
        						dialog.dismiss();
        						getNewPassword();
                            }
                        })
                        .show();
                	}
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
            {
                public void onClick(DialogInterface dialog, int whichButton) {
                	dialog.dismiss();
                }
            })
            .setCancelable(true)
            .show();	
	}
}
