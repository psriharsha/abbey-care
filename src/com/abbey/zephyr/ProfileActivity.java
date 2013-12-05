package com.abbey.zephyr;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.abbey.zephyr.auth.AbbeyAccountAuthenticator;
import com.abbey.zephyr.auth.LogActivity;

public class ProfileActivity extends Activity {

	static final String PROVIDER_NAME = "com.abbey.zephyr.provider";
	static final String URL = "content://" + PROVIDER_NAME + "/vitals";
	static final String AUTHORITY = PROVIDER_NAME;
	static final String ACCOUNT_TYPE = "com.abbey.zephyr";
	AbbeyAccountAuthenticator aaa;
	AccountAuthenticatorResponse aar;
	Account account;
	AccountManager accMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		startWebView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void startWebView() {
		// TODO Auto-generated method stub
		WebView myWebView = (WebView) findViewById(R.id.myView);
		myWebView.addJavascriptInterface(this, "Android");
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.loadUrl("file:///android_asset/www/profile.html");
	}

	@SuppressLint("NewApi")
	@JavascriptInterface
	public String createDialog() {
		String result = null;
		// String[] items = getBioModules();
		return result;
	}

	private String[] getBioModules() {
		// TODO Auto-generated method stub
		return null;
	}

	@JavascriptInterface
	public void stopSync() {
		account = new Account("harsha@abbeytotalcare.co.uk", ACCOUNT_TYPE);
		Bundle extras = new Bundle();
		if (ContentResolver.isSyncActive(account, AUTHORITY)) {
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.removePeriodicSync(account, AUTHORITY, extras);
		} else {
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.setIsSyncable(account, AUTHORITY, 1);
			ContentResolver.setMasterSyncAutomatically(true);
			ContentResolver.requestSync(account, AUTHORITY, extras);
			ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
		}
	}

	@SuppressLint("InlinedApi")
	@JavascriptInterface
	public void logout() {
		Intent change = new Intent(this, LogActivity.class);
		change.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(change);
		finish();
		account = new Account("harsha@abbeytotalcare.co.uk", ACCOUNT_TYPE);
		aaa.removeAccount(aar, account);
	}
	
	@SuppressLint("InlinedApi")
	@JavascriptInterface
	public void goHome(){
		Intent change = new Intent(this, HomeActivity.class);
		change.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(change);
		finish();
	}
	
	@JavascriptInterface
	public String isSyncActive(){
		String syncState = "false";
		account = new Account("harsha@abbeytotalcare.co.uk", ACCOUNT_TYPE);
		if(ContentResolver.isSyncActive(account, AUTHORITY))
			syncState = "true";
		Toast.makeText(getApplicationContext(), syncState, Toast.LENGTH_SHORT).show();
		return syncState;
	}
	
	@JavascriptInterface
    public void toast(String url){
    	Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
    }
}
