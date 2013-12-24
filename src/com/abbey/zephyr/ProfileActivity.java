package com.abbey.zephyr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ZephyrProtocol;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.abbey.zephyr.RestClient.RequestMethod;
import com.abbey.zephyr.auth.AbbeyAccountAuthenticator;
import com.abbey.zephyr.auth.LogActivity;
import com.abbey.zephyr.vitals.GetVitals;

public class ProfileActivity extends Activity {

	static final String PROVIDER_NAME = "com.abbey.zephyr.provider";
	static final String URL = "content://" + PROVIDER_NAME + "/vitals";
	static final String AUTHORITY = PROVIDER_NAME;
	static final String ACCOUNT_TYPE = "com.abbey.zephyr";
	static String select;
	AbbeyAccountAuthenticator aaa;
	AccountAuthenticatorResponse aar;
	Account account;
	AccountManager accMgr;
	SharedPreferences sharedPreference;
	Editor editor;
	BluetoothAdapter adapter = null;
	BTClient _bt;
	ZephyrProtocol _protocol;
	String[] names = new String[100];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		startWebView();
		sharedPreference = getSharedPreferences(Singleton.sharedPrefName, 0);
		editor = sharedPreference.edit();
		if(sharedPreference.getInt("age", -1) == -1){
			getProfDetails();
		}
		startBluetooth();
		aaa = new AbbeyAccountAuthenticator(getApplicationContext());
	}

	private void getProfDetails() {
		// TODO Auto-generated method stub
		AsyncTask<Void,Void,Void> task = new AsyncTask<Void,Void,Void>(){
			ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
			RestClient client = new RestClient("http://"+Singleton.ip+"/zephyr/index.php/service/user/getDetails");
			@SuppressLint("NewApi")
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				client.AddParam("uId", sharedPreference.getInt("uId", -1)+"");
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		        StrictMode.setThreadPolicy(policy);
				try {
				    client.Execute(RequestMethod.POST);
				} catch (Exception e) {
				    e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				pd.dismiss();
				String response = client.getResponse();
				try {
					JSONObject object = new JSONObject(response);
					editor.putString("firstName", object.getString("firstName"));
					editor.putString("lastName", object.getString("lastName"));
					editor.putString("dob", object.getString("dob"));
					editor.putString("gender", object.getString("gender"));
					editor.putString("bloodGroup", object.getString("bloodGroup"));
					editor.putString("ethnicity", object.getString("ethnicity"));
					editor.commit();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd.setCancelable(false);
				pd.setIndeterminate(true);
				pd.show();
			}
			
		};
		task.execute((Void[])null);
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

	@SuppressLint("InlinedApi")
	@JavascriptInterface
	public void stopSync() {
		String username = sharedPreference.getString("username", null);
		account = new Account(username, ACCOUNT_TYPE);
		if (ContentResolver.getSyncAutomatically(account, AUTHORITY)) {
			stopService();
			/*extras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.setIsSyncable(account, AUTHORITY, 0);
			ContentResolver.removePeriodicSync(account, AUTHORITY, extras);*/
		} else if(sharedPreference.getString("bio",null)!=null){
			startService();
			Intent change = new Intent(this, HomeActivity.class);
			change.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(change);
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

	@SuppressLint("InlinedApi")
	@JavascriptInterface
	public void logout() {
		Boolean res;
		Bundle removeAcc;
		stopService();
		Intent log = new Intent(this,LogActivity.class);
		log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(log);
		String username = sharedPreference.getString("username", null);
		account = new Account(username, ACCOUNT_TYPE);
		removeAcc = aaa.removeAccount(aar, account);
		res = removeAcc.getBoolean(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION);
		editor.clear();
		editor.commit();
		finish();
	}
	
	private void stopService() {
		// TODO Auto-generated method stub
		Intent loggingOut = new Intent("com.abbey.zephyr.vitals.GetVitals");
		stopService(loggingOut);
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
		String[] BhMacID;
		IntentFilter filter = new IntentFilter(
				"android.bluetooth.device.action.PAIRING_REQUEST");
		this.getApplicationContext().registerReceiver(
				new BTBroadcastReceiver(), filter);
		IntentFilter filter2 = new IntentFilter(
				"android.bluetooth.device.action.BOND_STATE_CHANGED");
		this.getApplicationContext().registerReceiver(new BTBondReceiver(),
				filter2);
		adapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		BluetoothDevice[] btDevice = new BluetoothDevice[100];
		BhMacID = new String[100];
		int i = 0;
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().startsWith("BH")) {
					btDevice[i] = device;
					BhMacID[i] = btDevice[i].getAddress();
					i++;
				}
			}
		}
		BluetoothDevice[] devices = new BluetoothDevice[100];
		int j=0;
		for( j=0; j< i ; j++){
			devices[j] = adapter.getRemoteDevice(BhMacID[j]);
			names[j] = devices[j].getName();
			selected += "," + names[j];
		}		
		return selected;
	}
	
	class MyDialog implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			ProfileActivity.select = names[which];
		}
		
	}
	
	// For Bluetooth
	private class BTBondReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b = intent.getExtras();
			BluetoothDevice device = adapter.getRemoteDevice(b.get(
					"android.bluetooth.device.extra.DEVICE").toString());
			Log.d("Bond state", "BOND_STATED = " + device.getBondState());
		}
	}

	private class BTBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("BTIntent", intent.getAction());
			Bundle b = intent.getExtras();
			Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE")
					.toString());
			Log.d("BTIntent",
					b.get("android.bluetooth.device.extra.PAIRING_VARIANT")
							.toString());
			try {
				BluetoothDevice device = adapter.getRemoteDevice(b.get(
						"android.bluetooth.device.extra.DEVICE").toString());
				Method m = BluetoothDevice.class.getMethod("convertPinToBytes",
						new Class[] { String.class });
				byte[] pin = (byte[]) m.invoke(device, "1234");
				m = device.getClass().getMethod("setPin",
						new Class[] { pin.getClass() });
				Object result = m.invoke(device, pin);
				Log.d("BTTest", result.toString());
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// End of Bluetooth
	
	@SuppressLint("SimpleDateFormat")
	@JavascriptInterface
	public String[] getMyDetails(){
		Toast.makeText(getApplicationContext(), sharedPreference.getString("firstName", null) + " 2£", Toast.LENGTH_SHORT).show();
		String[] details = new String[7] ;
		details[0] = sharedPreference.getString("firstName", null) + sharedPreference.getString("lastName", null);
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		formatter.setLenient(false);
		String dateStr = sharedPreference.getString("dob", null);
		Date date = null;
		try {
			date = formatter.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = Integer.parseInt(sharedPreference.getString(calendar.get(Calendar.YEAR)+"", 1900+""));
		year = 2013 - year;
		if(year > 0)
		details[1] = year + "";
		else
			details[1] = "-- ";
		details[2] = sharedPreference.getString("gender", "--");
		//Toast.makeText(getApplicationContext(), sharedPreference.getString("firstName", null), Toast.LENGTH_SHORT).show();
		return details;
	}
	
	@JavascriptInterface
	public String getMyName(){
		return sharedPreference.getString("firstName", "-- ")+ " "+sharedPreference.getString("lastName", "--");
	}
	
	@JavascriptInterface
	public String getGender(){
		return sharedPreference.getString("gender", "-- ");
	}
	
	@JavascriptInterface
	public String getBloodGroup(){
		return sharedPreference.getString("bloodGroup", "-- ");
	}
	
	@JavascriptInterface
	public String getEthnicity(){
		return sharedPreference.getString("ethnicity", "-- ");
	}
	
	@JavascriptInterface
	public void changeBio(String bioName){
		editor.putString("bio", bioName);
		editor.commit();
	}
	
	@SuppressLint("SimpleDateFormat")
	@JavascriptInterface
	public int getMyAge(){
		int age = 0;
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		formatter.setLenient(false);
		String dateStr = sharedPreference.getString("dob", "01-01-1990");
		Date date = null;
		try {
			date = formatter.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		year = 2013 - year;
		if(year > 0)
		age = year;
		return age;
	}
	
	@JavascriptInterface
	public String getSharedBio(){
		String bio = "null";
		bio = sharedPreference.getString("bio", "null");
		return "dumpring";
	}
	
}
