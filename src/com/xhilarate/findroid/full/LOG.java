package com.xhilarate.findroid.full;

import android.util.Log;

//This class is used to monitor log catalog messages of the application
public class LOG 
{
	final static String TAG="FinDroid";
	
	public static void d(String msg)
	{
		Log.d(TAG, msg);
	}
	
	public static void e(String msg)
	{
		Log.e(TAG, msg);
	}

}
