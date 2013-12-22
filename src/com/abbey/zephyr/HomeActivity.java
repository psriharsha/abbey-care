package com.abbey.zephyr;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.abbey.zephyr.auth.AbbeyAccountAuthenticator;
import com.abbey.zephyr.auth.LogActivity;
import com.abbey.zephyr.provider.VitalsProvider;
import com.abbey.zephyr.vitals.GetVitals;

public class HomeActivity extends Activity {

	static final String PROVIDER_NAME = "com.abbey.zephyr.provider";
	static final String URL = "content://" + PROVIDER_NAME + "/vitals";
	static final String AUTHORITY = PROVIDER_NAME;
	static final String ACCOUNT_TYPE = "com.abbey.zephyr";
	AbbeyAccountAuthenticator aaa;
	AccountAuthenticatorResponse aar;
	Account account;
	AccountManager accMgr;
	Intent service = new Intent("com.abbey.zephyr.vitals.GetVitals");
	SharedPreferences sharedPreference;
	Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		// Check if an Account Exist and bring in Login if it doesn't
		afterStart();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startWebView();
	}

	private void afterStart() {
		// TODO Auto-generated method stub
		accMgr = AccountManager.get(this);
		startBluetooth();
		String[] accs = getAccounts();
		if(accs == null || accs.length == 0)
		{
			Intent openLogin = new Intent(this, LogActivity.class);
			startActivity(openLogin);
			finish();
		}
		//End of Account Checks
		else {
		startWebView();
		sharedPreference = getSharedPreferences(Singleton.sharedPrefName,0);
		if(sharedPreference.contains("bio")){
		if(!isMyServiceRunning())
			startVitalService();
		}
		else
		{
			Intent openLogin = new Intent(this, ProfileActivity.class);
			startActivity(openLogin);
			finish();
		}
		
		aaa = new AbbeyAccountAuthenticator(getApplicationContext());
		}
	}
	
	private void startBluetooth() {
		// TODO Auto-generated method stub
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if ((mBluetoothAdapter != null) && !(mBluetoothAdapter.isEnabled())) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 0);
		}
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
	
	@JavascriptInterface
	public String myServiceRun(){
		String res = "Start Sync";
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (GetVitals.class.getName().equals(service.service.getClassName())) {
	            res = "Stop Sync";
	        }
	    }
		return res;
	}

	private void startVitalService() {
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
		final String function = "alertMessage";
		final WebView myWebView = (WebView) findViewById(R.id.myView);
		myWebView.addJavascriptInterface(this, "Android");
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.loadUrl("file:///android_asset/www/home.html");
		myWebView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				
				view.loadUrl("javascript:alertMessage()");
			}
			
		});
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
    	startActivityForResult(change,0);
    }
	
	@JavascriptInterface
    public void toast(String url){
    	Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
    }
	
	@JavascriptInterface
    public void myProfile(){
		Intent change = new Intent("com.abbey.zephyr.ProfileActivity");
		startActivity(change);
    }
	
	@JavascriptInterface
	public void stopSync() {
		String username = sharedPreference.getString("username", null);
		account = new Account(username, ACCOUNT_TYPE);
		Bundle extras = new Bundle();
		if (isMyServiceRunning()) {
			stopService();
			/*extras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.setIsSyncable(account, AUTHORITY, 0);
			ContentResolver.removePeriodicSync(account, AUTHORITY, extras);*/
		} else{
			startService();
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.requestSync(account, AUTHORITY, extras);
			startActivity(new Intent(this,HomeActivity.class));
			finish();
			/*ContentResolver.setIsSyncable(account, AUTHORITY, 1);
			ContentResolver.setMasterSyncAutomatically(true);
			ContentResolver.setSyncAutomatically(account, AUTHORITY, true);*/
		}
	}
	
	private void startService() {
		// TODO Auto-generated method stub
		Intent getVitalsService = new Intent("com.abbey.zephyr.vitals.GetVitals");
		startService(getVitalsService);
	}
	
	private void stopService() {
		// TODO Auto-generated method stub
		Intent loggingOut = new Intent("com.abbey.zephyr.vitals.GetVitals");
		stopService(loggingOut);
	}
	
	@SuppressLint("InlinedApi")
	@JavascriptInterface
	public void logout() {
		Boolean res;
		Bundle removeAcc;
		stopService();
		String username = sharedPreference.getString("username", null);
		account = new Account(username, ACCOUNT_TYPE);
		removeAcc = aaa.removeAccount(aar, account);
		res = removeAcc.getBoolean(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION);
		editor.clear();
		editor.commit();
		Intent change = new Intent(this, LogActivity.class);
		change.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(change);
		editor.clear();
		editor.commit();
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
	public String getMyName(){
		return sharedPreference.getString("firstName", "-- ")+ " "+sharedPreference.getString("lastName", "--");
	}
	
	@JavascriptInterface
	public String refreshVitalContents(){
		String details = "";
		Uri vitals = Uri.parse(URL);
		String[] projection = {"avg(_id) as _id","avg(heartRate) as heartRate","avg(respRate) as respRate","avg(skinTemp) as skinTemp","avg(posture) as posture","avg(peakAcce) as peakAcce","timeStamp","notSync"};
	    //String selection = "MAX("+VitalsProvider.TS+")";
		String sortOrder = VitalsProvider.TS + " DESC";
	    Cursor c = getContentResolver().query(vitals, projection, null, null, sortOrder);
	    if(c.getCount()>0 && c.moveToPosition(2)){
	    	details = c.getString(c.getColumnIndex(VitalsProvider.RR)) + ",";
	    	details += c.getString(c.getColumnIndex(VitalsProvider.HR)) + ",";
	    	details += c.getString(c.getColumnIndex(VitalsProvider.ST)) + ",";
	    	details += c.getString(c.getColumnIndex(VitalsProvider.PO)) + ",";
	    	details += c.getString(c.getColumnIndex(VitalsProvider.PA)) + ",";
	    }
	    else details = "--,--,--,--,--";
	    c.close();
		return details;
	}

}