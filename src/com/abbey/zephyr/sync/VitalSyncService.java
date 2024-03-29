package com.abbey.zephyr.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VitalSyncService extends Service{

	private static final Object sSyncAdapterLock = new Object();
    private static VitalSyncAdapter sSyncAdapter = null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return sSyncAdapter.getSyncAdapterBinder();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new VitalSyncAdapter(getApplicationContext(), true);
        }
	}
	
}
