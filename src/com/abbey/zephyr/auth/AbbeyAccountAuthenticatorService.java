package com.abbey.zephyr.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AbbeyAccountAuthenticatorService extends Service{

	private static AbbeyAccountAuthenticator aaa = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		IBinder ret = null;
		if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
		{
		ret = new AbbeyAccountAuthenticator(this).getIBinder();
		}
		else
			ret = getAuthenticator().getIBinder();
		return ret;
	}
	
	private AbbeyAccountAuthenticator getAuthenticator(){
		if(aaa == null){
			aaa = new AbbeyAccountAuthenticator(this);
		}
		return aaa;
	}

}
