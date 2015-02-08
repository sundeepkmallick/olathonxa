package com.olathonxa.fragment;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.olathonxa.R;
import com.olathonxa.adapter.PlacesAutoCompleteAdapter;
import com.olathonxa.fragment.IAmInvited.MyEventAdapter;
import com.olathonxa.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class MemberAddressInputDialog extends DialogFragment {
	Context context;
	String eventId;
	String userId = "";
	
	AutoCompleteTextView txtEventLocation;
	GoogleMap googleMap;
	MarkerOptions markerOptions;
	LatLng latLng;
	Button btnSave;
	String latLongStr = "0,0";
	
	private SupportMapFragment fragment;

    public MemberAddressInputDialog() {
        fragment = new SupportMapFragment();
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_address_input, container, false);
        getDialog().setTitle("Save your pickup location");
        /*FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.mapView, fragment).commit();*/
        SupportMapFragment supportMapFragment = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.mapshow);
        googleMap = supportMapFragment.getMap();
        
        SharedPreferences sharedpreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
		userId = sharedpreferences.getString(Constants.SHARED_LOGINID, "0");
        Bundle mArgs = getArguments();
		if(mArgs.containsKey(Constants.BUN_EVENT_ID)){
			eventId = mArgs.getString(Constants.BUN_EVENT_ID);
		}
        
        btnSave = (Button) view.findViewById(R.id.buttonSave);
        txtEventLocation = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewEventLocation);
		txtEventLocation.setAdapter(new PlacesAutoCompleteAdapter(context, R.layout.list_item_autocomplete_location));
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
		
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("EventDetails");
				query.whereEqualTo("eventId", eventId);
				query.findInBackground(new FindCallback<ParseObject>() {

					@Override
					public void done(List<ParseObject> arg0,
							ParseException arg1) {
						for (ParseObject parseObject : arg0) {
							if(parseObject.get("parti_id").equals(userId))
							{
								parseObject.put("accepted", true);
								parseObject.put("pickupLocation", latLongStr);
								parseObject.saveInBackground(new SaveCallback() {
									
									@Override
									public void done(ParseException arg0) {
										if(arg0 == null){
													OnCloseListDialogListener act = (OnCloseListDialogListener) getParentFragment();
													act.onDialogListSelection();
											Log.d("INSIDE","Dialog done");		
										}
											
									}
								});
							}
						}
					}
				});
				Log.d("eventId", "_"+eventId);
				dismiss();
			}
		});

        return view;
    }

	private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(String... locationName) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(context);
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
				Toast.makeText(context, "No Location found",
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
	
	public interface OnCloseListDialogListener {
	    public void onDialogListSelection();
	}

}
