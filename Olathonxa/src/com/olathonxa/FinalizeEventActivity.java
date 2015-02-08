package com.olathonxa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.olathonxa.util.Constants;
import com.olathonxa.util.DirectionsJSONParser;

public class FinalizeEventActivity extends FragmentActivity {
	Context context;
	String response;
	
	TextView itenaryDetails;
	Button confirm;
	GoogleMap map;
	ArrayList<LatLng> markerPoints;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finalize_event);
		
		itenaryDetails = (TextView)findViewById(R.id.itenaryDetails);
		confirm = (Button)findViewById(R.id.ConfirmItenary);
		
		confirm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Thanks for your booking. Your cab details will be sent shortly", Toast.LENGTH_LONG).show();
				finish();
			}
		});
		
		context = FinalizeEventActivity.this;
		response = getIntent().getExtras().getString(Constants.BUN_GMAP_POINTS);
		
		// Initializing 
		markerPoints = new ArrayList<LatLng>();
		
		// Getting reference to SupportMapFragment of the activity_main
		SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
		
		// Getting Map for the SupportMapFragment
		map = fm.getMap();
		
		// Enable MyLocation Button in the Map
		map.setMyLocationEnabled(true);	
		
		ParserTask parserTask = new ParserTask();
		
		// Invokes the thread for parsing the JSON data
		parserTask.execute(response);
	}

	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	// Parsing the data in non-ui thread    	
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			
			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;			           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	DirectionsJSONParser parser = new DirectionsJSONParser();
            	
            	// Starts parsing data
            	routes = parser.parse(jObject);    
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}
		
		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			String distance = "";
			String duration = "";
			
			if(result.size()<1){
				Toast.makeText(context, "No Points", Toast.LENGTH_SHORT).show();
				return;
			}
				
			
			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);
				
				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);	
					
					if(j==0){	// Get distance from the list
						distance = (String)point.get("distance");						
						continue;
					}else if(j==1){ // Get duration from the list
						duration = (String)point.get("duration");
						continue;
					}
					
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);	
					
					points.add(position);						
				}
				
				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);	
				
			}
			
			//tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
			
			// Drawing polyline in the Google Map for the i-th route
			map.addPolyline(lineOptions).setWidth(6f);	
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 18));
			markerOptions = new MarkerOptions();
			markerOptions.position(points.get(0));
			map.addMarker(markerOptions);
			
			markerOptions = new MarkerOptions();
			markerOptions.position(points.get(points.size()-1));
			map.addMarker(markerOptions);
			itenaryDetails.setText("Total Distance: " + distance + "\nEstimate Time Of Travel: " + duration + "\nCab Type: Sedan\nEstimated Cost: \u20B9" + Float.parseFloat(distance.substring(0,distance.indexOf(" ")))*10);
		}			
    }   
	

}
