package com.xhilarate.findroid.full;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AdminActivity extends Activity implements OnClickListener{

	private static final int RESULT_ENABLE = 1;
	private Button enableButton;
	private Button setPassword;
	
	private DevicePolicyManager mDPM;
	private ActivityManager mAM;
	private ComponentName mDeviceAdmin;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_activity);
		
		mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAM = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        
        mDeviceAdmin = new ComponentName(AdminActivity.this, AdminReceiver.class);
		enableButton = (Button) findViewById(R.id.btn_enable_admin);
		setPassword = (Button) findViewById(R.id.btn_set_admin_pass);
		enableButton.setOnClickListener(this);
		setPassword.setOnClickListener(this);
		updateButtons();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			
		case R.id.btn_enable_admin:
			if (!mDPM.isAdminActive(mDeviceAdmin)) {
				Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
						mDeviceAdmin);
				intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    	"This application should be added to device administrators to remotely lock and wipe data from this phone");
				startActivityForResult(intent, RESULT_ENABLE);
				updateButtons();
			}
			else {
				mDPM.removeActiveAdmin(mDeviceAdmin);
				updateButtons();
			}
			break;	
		
		case R.id.btn_set_admin_pass:
			Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
			startActivity(intent);
			break;

		}
		
		}
		
	 @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         switch (requestCode) {
             case RESULT_ENABLE:
                 if (resultCode == Activity.RESULT_OK) {
                     Log.i("FinDroid", "Admin enabled!");
                 } else {
                     Log.i("FinDroid", "Admin enable FAILED!");
                 }
                 updateButtons();
                 return;
         }

         super.onActivityResult(requestCode, resultCode, data);
     }
	 
	public void updateButtons() {
		boolean active = mDPM.isAdminActive(mDeviceAdmin);
		if(active) {
			enableButton.setText("Disable Device Admin");
			setPassword.setEnabled(true);
			
		}
		else {
			enableButton.setText("Enable Device Admin");
			setPassword.setEnabled(false);
			
		}
	}
	

}

