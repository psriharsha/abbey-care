package com.abbey.zephyr;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.abbey.zephyr.auth.LogActivity;
import com.abbey.zephyr.vitals.GetVitals;

public class HomeActivity extends Activity {

	static final String PROVIDER_NAME = "com.abbey.zephyr.provider";
	static final String URL = "content://" + PROVIDER_NAME + "/vitals";
	static final String AUTHORITY = PROVIDER_NAME;
	static final String ACCOUNT_TYPE = "com.abbey.zephyr";
	Account account;
	AccountManager accMgr;
	Intent service = new Intent("com.abbey.zephyr.vitals.GetVitals");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		// Check if an Account Exist and bring in Login if it doesn't
		accMgr = AccountManager.get(this);
		String[] accs = getAccounts();
		if(accs == null)
		{
			Intent openLogin = new Intent(this, LogActivity.class);
			startActivity(openLogin);
			finish();
		}
		//End of Account Checks
		startWebView();
		if(!isMyServiceRunning())
			startService();
	}
	
	private boolean isMyServiceRunning() {
		// TODO Auto-generated method stub
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (GetVitals.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
		return false;
	}

	private void startService() {
		// TODO Auto-generated method stub
		startService(service);
		account = new Account("harsha@abbeytotalcare.co.uk", ACCOUNT_TYPE);
		Bundle extras = new Bundle();
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
		extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		ContentResolver.setIsSyncable(account, AUTHORITY, 1);
		ContentResolver.setMasterSyncAutomatically(true);
		ContentResolver.requestSync(account, AUTHORITY, extras);
		ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void startWebView() {
		// TODO Auto-generated method stub
		WebView myWebView = (WebView) findViewById(R.id.myView);
		myWebView.addJavascriptInterface(this, "Android");
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.loadUrl("file:///android_asset/www/home.html");
	}

	private String[] getAccounts() {
		// TODO Auto-generated method stub
		Account[] accounts = accMgr.getAccountsByType("com.abbey.zephyr");
		String[] names = null;
		if(accounts.length > 0){
	    names = new String[accounts.length];
	    for (int i = 0; i < names.length; i++) {
	        names[i] = accounts[i].name;
	    }
		}
	    return names;
	}
	
	@JavascriptInterface
    public void changeActivity(String url){
    	Intent change = new Intent(url);
    	startActivity(change);
    }

}