package com.xhilarate.findroid.full;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener{
static ServerInterface si;
	static EditText txtPhone, txtFname, txtLname, txtEmail;
	static ProgressDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		txtPhone = (EditText) findViewById(R.id.txtRegPhone);
		txtFname = (EditText) findViewById(R.id.txtFname);
		txtLname = (EditText) findViewById(R.id.txtLname);
		txtEmail = (EditText) findViewById(R.id.txtRegEmail);
		((Button) findViewById(R.id.btn_reg)).setOnClickListener(this);
		((Button) findViewById(R.id.btn_cancel)).setOnClickListener(this);
		dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait");
        dialog.setMessage("Registering...");
        si=new ServerInterface(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_reg:
			if(txtPhone.getText().toString()!="" && txtFname.getText().toString()!="" && txtLname.getText().toString()!="" && txtEmail.getText().toString()!="" ) {
				(new RegisterTask()).execute((Object)null);
				dialog.show();
				Editor file = getSharedPreferences(MainActivity.FINDROID_PREFS, 0).edit();
        		file.putString("thisphone", txtPhone.getText().toString());
        		file.commit();
			}
			else {
				Toast.makeText(this, "Please enter all the details!", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_cancel:
			finish();
			break;
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
    private class RegisterTask extends AsyncTask {

            /**
             * Let's make the http request and return the result as a String.
             */
            protected String doInBackground(Object... args) {    
            	
                    return si.getRegister(txtPhone.getText().toString(), txtFname.getText().toString(), txtLname.getText().toString(), txtEmail.getText().toString());
            }

            /**
             * Parse the String result, and create a new array adapter for the list
             * view.
             */
            protected void onPostExecute(Object objResult) {
                    // check to make sure we're dealing with a string
                    if(objResult != null && objResult instanceof String) {                          
                            String result = (String) objResult;
                            Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
                            if(result.contains("success")) {
                            	//startActivity(new Intent(RegisterActivity.this, RegisterActivity.class));
                            	finish();
                            	Toast.makeText(RegisterActivity.this, "Register successful!", Toast.LENGTH_SHORT).show();
                            }
                            else if(result.contains("pe")){
                            	Toast.makeText(RegisterActivity.this, "Phone number exists!", Toast.LENGTH_SHORT).show();
                            }
                            else if(result.contains("ee")){
                            	Toast.makeText(RegisterActivity.this, "E-mail exists!", Toast.LENGTH_SHORT).show();
                            }
                    }
                    dialog.dismiss();
            }

    }

	

}
