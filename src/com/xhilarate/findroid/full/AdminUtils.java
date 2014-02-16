package com.xhilarate.findroid.full;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

public class AdminUtils {
	
	private DevicePolicyManager mDPM;
	private ActivityManager mAM;
	private ComponentName mDeviceAdmin;
	
	private AdminUtils(Activity activity) {
		mDPM = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAM = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        mDeviceAdmin = new ComponentName(activity, AdminReceiver.class);
	}
	
	private void lockPhone() {
		
		boolean active = mDPM.isAdminActive(mDeviceAdmin);
		if(active) {
			mDPM.lockNow();
		}
		
	}
	
	private void wipePhone() {
		
		boolean active = mDPM.isAdminActive(mDeviceAdmin);
		if(active) {
			mDPM.wipeData(0);
		}
	}
	
}
