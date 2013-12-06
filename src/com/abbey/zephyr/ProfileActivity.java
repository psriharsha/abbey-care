package com.abbey.zephyr;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
	SharedPreferences sharedPreference;
	Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		startWebView();
		startBluetooth();
		sharedPreference = getSharedPreferences(Singleton.sharedPrefName, 0);
		aaa = new AbbeyAccountAuthenticator(getApplicationContext());
	}

	private void startBluetooth() {
		// TODO Auto-generated method stub
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if ((mBluetoothAdapter != null) && !(mBluetoothAdapter.isEnabled())) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 0);
		}
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
		String username = sharedPreference.getString("username", null);
		account = new Account(username, ACCOUNT_TYPE);
		Bundle extras = new Bundle();
		if (ContentResolver.getSyncAutomatically(account, AUTHORITY)) {
			stopService();
			Toast.makeText(getApplicationContext(), "Sync is Active", Toast.LENGTH_SHORT).show();
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.setIsSyncable(account, AUTHORITY, 0);
			ContentResolver.setMasterSyncAutomatically(true);
			ContentResolver.removePeriodicSync(account, AUTHORITY, extras);
		} else if(sharedPreference.contains("bio")){
			startService();
			Toast.makeText(getApplicationContext(), "Sync is inactive", Toast.LENGTH_SHORT).show();
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.setIsSyncable(account, AUTHORITY, 1);
			ContentResolver.setMasterSyncAutomatically(true);
			ContentResolver.requestSync(account, AUTHORITY, extras);
			ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
		}
	}

	private void startService() {
		// TODO Auto-generated method stub
		Intent getVitalsService = new Intent("com.abbey.zephyr.vitals.GetVitals");
		stopService(getVitalsService);
	}

	@SuppressLint("InlinedApi")
	@JavascriptInterface
	public void logout() {
		Boolean res;
		Bundle removeAcc;
		stopSync();
		String username = sharedPreference.getString("username", null);
		account = new Account(username, ACCOUNT_TYPE);
		removeAcc = aaa.removeAccount(aar, account);
		res = removeAcc.getBoolean(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION);
		Intent change = new Intent(this, LogActivity.class);
		change.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(change);
		finish();
	}
	
	private void stopService() {
		// TODO Auto-generated method stub
		Intent loggingOut = new Intent("com.abbey.zephyr.vitals.GetVitals");
		stopService(loggingOut);
		Toast.makeText(getApplicationContext(), "Stop Service", Toast.LENGTH_SHORT).show();
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
		String username = sharedPreference.getString("username", null);
		account = new Account(username, ACCOUNT_TYPE);
		if(ContentResolver.getSyncAutomatically(account, AUTHORITY))
			syncState = "true";		
		return syncState;
	}
	
	@JavascriptInterface
    public void toast(String url){
    	Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
    }
	
	@JavascriptInterface
	public String selectBio(){
		String selected = null;
		AlertDialog.Builder bio = new AlertDialog.Builder(getApplicationContext());
		bio.setTitle("Pick your Bio-Sensor");
		bio.show();
		return selected;
	}
}
