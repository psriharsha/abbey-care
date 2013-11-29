package com.abbey.zephyr.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abbey.zephyr.R;

public class LoginActivity extends AccountAuthenticatorActivity implements OnClickListener{

	AbbeyAccountAuthenticator aaa;
	Account account;
	Bundle options;
	AccountAuthenticatorResponse response;
	EditText username, password;
	Button submit;
	
	// Account Details
	public static final String ACCOUNT_TYPE = "com.abbey.zephyr";
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.login);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		submit = (Button) findViewById(R.id.submitLogin);
		submit.setOnClickListener(this);
		aaa = new AbbeyAccountAuthenticator(getApplicationContext());
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view.getId() == R.id.submitLogin){
			Bundle resp = new Bundle();
			String user, pass;
			user = username.getText().toString();
			pass = password.getText().toString();
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
					this.finish();
				}
				else {
					username.setText("Incorrect Credentials");
				}
			} catch (NetworkErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
