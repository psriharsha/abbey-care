package com.abbey.zephyr;

//can be deleted

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.abbey.zephyr.auth.LoginActivity;

public class VitalActivity extends Activity implements OnClickListener {

	Button strtService, stpService;
	static final String PROVIDER_NAME = "com.abbey.zephyr.provider";
	static final String URL = "content://" + PROVIDER_NAME + "/vitals";
	static final String AUTHORITY = PROVIDER_NAME;
	static final String ACCOUNT_TYPE = "com.abbey.zephyr";
	Account account;
	AccountManager accMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accMgr = AccountManager.get(this);
		String[] accs = getAccounts();
		if(accs == null)
		{
			Intent openLogin = new Intent(this, LoginActivity.class);
			startActivity(openLogin);
			finish();
		}
		setContentView(R.layout.control);
		strtService = (Button) findViewById(R.id.strtService);
		stpService = (Button) findViewById(R.id.stpService);
		strtService.setOnClickListener(this);
		stpService.setOnClickListener(this);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vital, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent service = new Intent("com.abbey.zephyr.vitals.GetVitals");
		switch (view.getId()) {
		case R.id.strtService: {
			startService(service);
			account = new Account("harsha@abbeytotalcare.co.uk", ACCOUNT_TYPE);
			Bundle extras = new Bundle();
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_INITIALIZE, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			ContentResolver.setIsSyncable(account, AUTHORITY, 1);
			ContentResolver.setMasterSyncAutomatically(true);
			ContentResolver.requestSync(account, AUTHORITY, extras);
			ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
			break;
		}
		case R.id.stpService: {
			stopService(service);
			break;
		}
		}
	}

}
