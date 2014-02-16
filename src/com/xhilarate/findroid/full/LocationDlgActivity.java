package com.xhilarate.findroid.full;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LocationDlgActivity extends Activity
{
	Button btnYes,btnNo;
	String message;
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		message = getIntent().getStringExtra("message");
		new AlertDialog.Builder(this)
        .setTitle("Location received")
        .setMessage(message)
        .setPositiveButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton){
				startActivity(new Intent(LocationDlgActivity.this,SearchMap.class));
				dialog.dismiss();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton){
				dialog.dismiss();
            }
        })
        .show();
		}
}
