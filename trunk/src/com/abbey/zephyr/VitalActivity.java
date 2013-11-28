package com.abbey.zephyr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class VitalActivity extends Activity implements OnClickListener {

	Button strtService, stpService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control);
		strtService = (Button) findViewById(R.id.strtService);
		stpService = (Button) findViewById(R.id.stpService);
		strtService.setOnClickListener(this);
		stpService.setOnClickListener(this);
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
			break;
		}
		case R.id.stpService: {
			stopService(service);
			break;
		}
		}
	}

}
