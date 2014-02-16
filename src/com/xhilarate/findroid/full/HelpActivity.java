package com.xhilarate.findroid.full;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class HelpActivity extends Activity
{
	private WebView help;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);
		help = (WebView) findViewById(R.id.help);
		help.loadUrl("file:///android_asset/help.htm");
	}

}
