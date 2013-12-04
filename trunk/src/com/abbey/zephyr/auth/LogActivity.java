package com.abbey.zephyr.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.abbey.zephyr.HomeActivity;
import com.abbey.zephyr.R;

public class LogActivity extends AccountAuthenticatorActivity{

	AbbeyAccountAuthenticator aaa;
	Account account;
	Bundle options;
	AccountAuthenticatorResponse response;
	public static final String ACCOUNT_TYPE = "com.abbey.zephyr";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		startWebView();
		aaa = new AbbeyAccountAuthenticator(getApplicationContext());
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void startWebView() {
		// TODO Auto-generated method stub
		WebView myWebView = (WebView) findViewById(R.id.myView);
		myWebView.addJavascriptInterface(this, "Android");
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		myWebView.loadUrl("file:///android_asset/www/login.html");
	}
	
	@JavascriptInterface
	public String onSubmit(String username, String password){
		Toast.makeText(getApplicationContext(), "In Submit", Toast.LENGTH_SHORT).show();
		Bundle resp = new Bundle();
		String user, pass, result = "";
		user = username;
		pass = password;
		options = new Bundle();
		options.putString("username", user);
		options.putString("password", pass);
		account = new Account(user,ACCOUNT_TYPE);
		try {
			resp = aaa.confirmCredentials(response, account, options);
			if(resp.getString("response").equals("Success")){
				aaa.addAccount(response, ACCOUNT_TYPE, ACCOUNT_TYPE, null, options);
				final Intent intent = new Intent();
				intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, user);
				intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
				this.setAccountAuthenticatorResult(intent.getExtras());
				this.setResult(RESULT_OK, intent);
				Intent openVitals = new Intent(this,HomeActivity.class);
				startActivity(openVitals);
				this.finish();
			}
			else {
				result = "Invalid Credentials";
			}
		} catch (NetworkErrorException e) {
			// TODO Auto-generated catch block
			result = "Check your network connections!!";
			e.printStackTrace();
		}
		return result;
	}
	
}
