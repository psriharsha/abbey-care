package com.abbey.zephyr.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.SyncStateContract.Constants;
import android.widget.Toast;

import com.abbey.zephyr.RestClient;
import com.abbey.zephyr.RestClient.RequestMethod;
import com.abbey.zephyr.Singleton;

public class AbbeyAccountAuthenticator extends AbstractAccountAuthenticator{

	Context mContext;
	AccountManager accMgr;
	
	public AbbeyAccountAuthenticator(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response,
			String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		final Bundle result;
		String username = options.getString("username");
		String password = options.getString("password");
		final Account account = new Account(username, accountType);
		accMgr.addAccountExplicitly(account, password, null);
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
		intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType);
		intent.putExtra(AccountManager.KEY_PASSWORD, password);
		intent.putExtra(Constants.ACCOUNT_TYPE, authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

		result = new Bundle();
		result.putParcelable(AccountManager.KEY_INTENT, intent);

		return result;
	}
	
	public Bundle removeAccount(AccountAuthenticatorResponse response, Account account){
		Bundle resp = new Bundle();
		AccountManagerCallback<Boolean> acc = null;
		AccountManagerFuture<Boolean> bool = null;
		bool = accMgr.removeAccount(account, acc, null);
		resp.putParcelable(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION, (Parcelable) bool);
		return resp;
	}

	@SuppressLint("NewApi")
	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
			Account account, Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		String username = options.getString("username");
		String password = options.getString("password");
		String error;
		RestClient client = new RestClient("http://"+Singleton.ip+"/zephyr/index.php/service/user/authenticate");
		client.AddParam("user", username);
		client.AddParam("pass", password);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		try {
		    client.Execute(RequestMethod.POST);
		} catch (Exception e) {
		    e.printStackTrace();
		}

		String resp = client.getResponse();
		Toast.makeText(mContext.getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
		if(resp.equals("Success"))
		{
			error = "validated";
		}
		else {
			error = "invalid";
		}
		error = client.getErrorMessage();
		Bundle result = new Bundle();
		result.putString("response", resp);
		result.putString("error", error);
		return result;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response,
			String accountType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response,
			Account account, String[] features) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response,
			Account account, String authTokenType, Bundle options)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		AccountManagerCallback<Bundle> resp = null;
		accMgr.updateCredentials(account, authTokenType, options, null, resp, null);
		return null;
	}
	
	public String[] getAccounts(String uri) {
		// TODO Auto-generated method stub
		Account[] accounts = accMgr.getAccountsByType(uri);
		String[] names = null;
		if(accounts.length > 0){
	    names = new String[accounts.length];
	    for (int i = 0; i < names.length; i++) {
	        names[i] = accounts[i].name;
	    }
		}
	    return names;
	}

}
