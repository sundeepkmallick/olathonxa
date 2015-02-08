package com.olathonxa;

import java.io.IOException;
import java.util.List;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.olathonxa.adapter.PlacesAutoCompleteAdapter;
import com.olathonxa.fragment.DatePickerFragment;
import com.olathonxa.fragment.DatePickerFragment.DatePickerListener;
import com.olathonxa.fragment.TimePickerDialogFragment;
import com.olathonxa.util.Constants;
import com.olathonxa.util.Util;

public class CreateEventActivity extends FragmentActivity implements DatePickerListener {

	Context context;

	EditText txtEventName;
	EditText txtEventDate;
	EditText txtEventTime;
	AutoCompleteTextView txtEventLocation;
	Button btnAddEvent;

	GoogleMap googleMap;
	MarkerOptions markerOptions;
	LatLng latLng;
	
	int mHour = 15; 
	int mMinute = 15;
	
	ProgressDialog progressDialog;
	AddEventTask taskAddEvent;
	
	String eventName = "";
	String eventDate = "";
	String eventTime = "";
	String eventAddress = "";
	String userId = "";
	String latLongStr = "0,0";
	
	/** This handles the message send from TimePickerDialogFragment on setting Time */
	Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message m){   
        	/** Creating a bundle object to pass currently set Time to the fragment */
        	Bundle b = m.getData();
        	/** Getting the Hour of day from bundle */
    		mHour = b.getInt("set_hour");
    		/** Getting the Minute of the hour from bundle */
    		mMinute = b.getInt("set_minute");
    		/** Displaying a short time message containing time set by Time picker dialog fragment */
    		//Toast.makeText(getBaseContext(), b.getString("set_time"), Toast.LENGTH_SHORT).show();
    		txtEventTime.setText(b.getString("set_time"));
        }
	};

	SharedPreferences sharedpreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);

		context = CreateEventActivity.this;

		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		
	    googleMap = supportMapFragment.getMap();
		
		sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
		 userId = sharedpreferences.getString(Constants.SHARED_LOGINID, "0");
		
		
		progressDialog = new ProgressDialog(context);
		btnAddEvent = (Button) findViewById(R.id.buttonAddEvent);
		txtEventName = (EditText) findViewById(R.id.editTextEventName);
		txtEventDate = (EditText) findViewById(R.id.editTextEventDate);
		txtEventTime = (EditText) findViewById(R.id.editTextEventTime);
		txtEventLocation = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewEventLocation);
		txtEventLocation.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item_autocomplete_location));
		txtEventLocation.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String location = txtEventLocation.getText().toString();

				if (!TextUtils.isEmpty(location)) {
					new GeocoderTask().execute(location);
				}
			}
		});
		
		txtEventTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/** Creating a bundle object to pass currently set time to the fragment */
				Bundle b = new Bundle();
				/** Adding currently set hour to bundle object */
				b.putInt("set_hour", mHour);
				/** Adding currently set minute to bundle object */
				b.putInt("set_minute", mMinute);
				/** Instantiating TimePickerDialogFragment */
				TimePickerDialogFragment timePicker = new TimePickerDialogFragment(mHandler);
				/** Setting the bundle object on timepicker fragment */
				timePicker.setArguments(b);				
				/** Getting fragment manger for this activity */
				FragmentManager fm = getSupportFragmentManager();				
				/** Starting a fragment transaction */
				FragmentTransaction ft = fm.beginTransaction();
				/** Adding the fragment object to the fragment transaction */
				ft.add(timePicker, "time_picker");
				/** Opening the TimePicker fragment */
				ft.commit();				
			}
		});
		
		btnAddEvent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String eventName = txtEventName.getText().toString();
				String eventDate = txtEventDate.getText().toString();
				String eventTime = txtEventTime.getText().toString();
				String eventAddress = txtEventLocation.getText().toString();
				
				if(!TextUtils.isEmpty(eventName)){
					if(!TextUtils.isEmpty(eventDate)){
						if(!TextUtils.isEmpty(eventTime)){
							if(!TextUtils.isEmpty(eventAddress)){
								ClassEventProfile profile = new ClassEventProfile();
								profile.name = eventName;
								profile.date = eventDate;
								profile.time = eventTime;
								profile.location = latLongStr;//TODO Need to change 
								profile.creatorId = userId;
								Intent intent = new Intent(context, AddMemberActivity.class);
								intent.putExtra("EventProfile", profile);
								startActivity(intent);
								/*taskAddEvent = new AddEventTask();
								taskAddEvent.execute();*/
								
							}else{
								Toast.makeText(context, Constants.ERR_PROVIDE_EVENT_LOCATION, Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(context, Constants.ERR_PROVIDE_EVENT_TIME, Toast.LENGTH_LONG).show();
						}
					}else{
						Toast.makeText(context, Constants.ERR_PROVIDE_EVENT_DATE, Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(context, Constants.ERR_PROVIDE_EVENT_NAME, Toast.LENGTH_LONG).show();
				}
				
			}
		});
	}

	// An AsyncTask class for accessing the GeoCoding Web Service
	private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(String... locationName) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> addresses = null;

			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName(locationName[0], 3);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return addresses;
		}

		@Override
		protected void onPostExecute(List<Address> addresses) {

			if (addresses == null || addresses.size() == 0) {
				Toast.makeText(getBaseContext(), "No Location found",
						Toast.LENGTH_SHORT).show();
			}

			// Clears all the existing markers on the map
			googleMap.clear();

			// Adding Markers on Google Map for each matching address
			for (int i = 0; i < addresses.size(); i++) {

				Address address = (Address) addresses.get(i);

				// Creating an instance of GeoPoint, to display in Google Map
				latLng = new LatLng(address.getLatitude(),
						address.getLongitude());
				
				latLongStr = address.getLatitude()+","+address.getLongitude();

				String addressText = String.format(
						"%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address
								.getCountryName());

				markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.title(addressText);

				googleMap.addMarker(markerOptions);

				if (i == 0){
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
				}
				
				
			}
		}
	}
	
	public void showDatePickerDialog(View v) {
		OnDateSetListener  datePickerListener = new OnDateSetListener () {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				String formattedDate = Util.daymonthyearToDate(context, year, monthOfYear, dayOfMonth);
				txtEventDate.setText(formattedDate);
			}
		};
		DatePickerFragment newFragment = new DatePickerFragment();
		newFragment.setCallBack(datePickerListener);
		newFragment.show(getSupportFragmentManager(), "Event Date");
	}

	@Override
	public void onDateSelected(DialogFragment dialog, String selectedDate) {
		txtEventDate.setText(selectedDate);
	}

	private class AddEventTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			if(progressDialog!=null){
				progressDialog.setMessage("");
				progressDialog.setTitle("");
				progressDialog.show();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			// Call WS
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			if(progressDialog!=null && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			
			String eventId = "1"; //Will come from Web-service
			if(!TextUtils.isEmpty(eventId)){
				Intent intent = new Intent(context, AddMemberActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constants.BUN_EVENT_ID, eventId);
				startActivity(intent);
			}
		}
		
	}
}
