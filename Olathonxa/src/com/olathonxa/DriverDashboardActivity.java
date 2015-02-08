package com.olathonxa;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class DriverDashboardActivity extends Activity {
	
	ListView listViewPickUpEvents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_dashboard);
		
		listViewPickUpEvents = (ListView) findViewById(R.id.listViewPickUpEvents);
		
		
	}

}
