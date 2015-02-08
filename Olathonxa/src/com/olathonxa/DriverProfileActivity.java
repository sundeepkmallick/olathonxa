package com.olathonxa;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class DriverProfileActivity extends Activity {
	
	Spinner spinnerVehicleType;
	TextView txtCapacity;
	Button btnSubmit;
	
	String[] vehicleTypes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_profile);
		
		spinnerVehicleType = (Spinner) findViewById(R.id.spinnerVehicleType);
		txtCapacity = (TextView) findViewById(R.id.textViewCapacity);
		btnSubmit = (Button) findViewById(R.id.buttonSubmit);
		
		vehicleTypes = getResources().getStringArray(R.array.vehicle_type_arrays);
		
		spinnerVehicleType.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	switch(position){
		    		case 0:
		    			txtCapacity.setText("3");
		    			break;
		    		case 1: 
		    			txtCapacity.setText("4");
		    			break;
		    		case 2:
		    			txtCapacity.setText("10");
		    	}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	
		    }
		});
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String vehicleType = vehicleTypes[spinnerVehicleType.getSelectedItemPosition()];
				String capacity = txtCapacity.getText().toString();
				Log.v("TAG","vehicleType:"+vehicleType+", capacity:"+capacity);
			}
		});
	}

}
