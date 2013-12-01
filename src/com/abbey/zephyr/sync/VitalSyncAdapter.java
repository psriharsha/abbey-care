package com.abbey.zephyr.sync;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;

import com.abbey.zephyr.RestClient;
import com.abbey.zephyr.RestClient.RequestMethod;
import com.abbey.zephyr.Singleton;
import com.abbey.zephyr.provider.VitalsProvider;

public class VitalSyncAdapter extends AbstractThreadedSyncAdapter{
	
	Context mContext;
	static final String PROVIDER_NAME = "com.abbey.zephyr.provider";
	static final String URL = "content://" + PROVIDER_NAME + "/vitals";

	public VitalSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@SuppressLint("NewApi")
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		// TODO Auto-generated method stub
		Uri vitals = Uri.parse(URL);
	      String[] projection = {"_id","heartRate","respRate","skinTemp","posture","peakAcce","timeStamp"};
	      Cursor c = mContext.getContentResolver().query(vitals, projection, null, null, null);
	      c.moveToFirst();
	      do{
		try{
			RestClient client = new RestClient("http://"+Singleton.ip+"/zephyr/index.php/service/user/insertVitals");
			client.AddParam("hr", c.getString(c.getColumnIndex(VitalsProvider.HR)));
			client.AddParam("rr", c.getString(c.getColumnIndex(VitalsProvider.RR)));
			client.AddParam("st", c.getString(c.getColumnIndex(VitalsProvider.ST)));
			client.AddParam("po", c.getString(c.getColumnIndex(VitalsProvider.PO)));
			client.AddParam("pa", c.getString(c.getColumnIndex(VitalsProvider.PA)));
			client.AddParam("ts", c.getString(c.getColumnIndex(VitalsProvider.TS)));
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
    		try {
    		    client.Execute(RequestMethod.POST);
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    		String[] selectionArgs = {c.getString(c.getColumnIndex(VitalsProvider._ID))};
    		int result = mContext.getContentResolver().delete(vitals, VitalsProvider._ID + "=?", selectionArgs);
    		String response = client.getResponse();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	      }while(c.moveToNext());
	}

}
