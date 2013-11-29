package com.abbey.zephyr.vitals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ZephyrProtocol;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.abbey.zephyr.provider.VitalsProvider;

public class GetVitals extends Service {

	BluetoothAdapter adapter = null;
	BTClient _bt;
	ZephyrProtocol _protocol;
	ConnectedListener _NConnListener;
	private final int HEART_RATE = 0x100;
	private final int RESPIRATION_RATE = 0x101;
	private final int SKIN_TEMPERATURE = 0x102;
	private final int POSTURE = 0x103;
	private final int PEAK_ACCLERATION = 0x104;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Handler handler = new Handler();

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

	Runnable collectVitals = new Runnable() {

		@SuppressLint("NewApi")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Toast.makeText(getBaseContext(), "Hi", Toast.LENGTH_SHORT).show();
			handler.postDelayed(collectVitals, 3000);
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// TODO Auto-generated method stub
		init();

		return START_STICKY;
	}

	private void init() {
		// TODO Auto-generated method stub
		// Initialization
		IntentFilter filter = new IntentFilter(
				"android.bluetooth.device.action.PAIRING_REQUEST");
		this.getApplicationContext().registerReceiver(
				new BTBroadcastReceiver(), filter);
		IntentFilter filter2 = new IntentFilter(
				"android.bluetooth.device.action.BOND_STATE_CHANGED");
		this.getApplicationContext().registerReceiver(new BTBondReceiver(),
				filter2);
		// End of initialization
		// Connecting to the device
		String BhMacID = "00:07:80:9D:8A:E8";
		// String BhMacID = "00:07:80:88:F6:BF";
		adapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().startsWith("BH")) {
					BluetoothDevice btDevice = device;
					BhMacID = btDevice.getAddress();
					break;

				}
			}
		}

		// BhMacID = btDevice.getAddress();
		BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
		String DeviceName = Device.getName();
		Toast.makeText(getBaseContext(), DeviceName, Toast.LENGTH_SHORT).show();
		_bt = new BTClient(adapter, BhMacID);
		_NConnListener = new ConnectedListener(btHandler, btHandler);
		_bt.addConnectedEventListener(_NConnListener);
		if (_bt.IsConnected()) {
			_bt.start();
		} else {
			init();
		}
		// End of the connection
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		_bt.removeConnectedEventListener(_NConnListener);
		_bt.Close();
	}

	final Handler btHandler = new Handler() {
		public void handleMessage(Message msg) {
			int count = 0;
			String col = null;
			String data = null;
			ContentValues values = new ContentValues();
			do{
				count++;
			switch (msg.what) {
			case HEART_RATE:
				data = msg.getData().getString("HeartRate");
				col = VitalsProvider.HR;
				// Insert values into SQLite
				break;

			case RESPIRATION_RATE:
				data = msg.getData().getString("RespirationRate");
				col = VitalsProvider.RR;
				break;

			case SKIN_TEMPERATURE:
				data = msg.getData().getString("SkinTemperature");
				col = VitalsProvider.ST;
				break;

			case POSTURE:
				data = msg.getData().getString("Posture");
				col = VitalsProvider.PO;
				break;

			case PEAK_ACCLERATION:
				data = msg.getData().getString("PeakAcceleration");
				col = VitalsProvider.PA;
				break;
			}
			values.put(col, data);
			}while(count<5);

			Uri uri = getContentResolver().insert(VitalsProvider.CONTENT_URI,
					values);
		}
	};
}
