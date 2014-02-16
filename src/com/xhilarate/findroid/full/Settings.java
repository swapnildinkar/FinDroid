package com.xhilarate.findroid.full;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		//set the about message to the about preference
		final DialogPreference aboutPref= (DialogPreference) findPreference("about");
		aboutPref.setDialogTitle("FinDroid");
		aboutPref.setDialogLayoutResource(R.layout.about_dialog);
		
	}

}
