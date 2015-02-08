package com.olathonxa.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.olathonxa.ClassEventProfile;
import com.olathonxa.FinalizeEventActivity;
import com.olathonxa.R;
import com.olathonxa.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MyEvents extends Fragment {
	
	Context context;
	
	ListView listMyEvents;
	String locationLatLng;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		context = getActivity();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	SharedPreferences sharedpreferences;
	List<ClassEventProfile> eventProfileList;
	String userId;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_myevents, container, false);
		eventProfileList = new ArrayList<ClassEventProfile>();
		listMyEvents = (ListView) view.findViewById(R.id.listViewMyEvents);
		sharedpreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
		userId = sharedpreferences.getString(Constants.SHARED_LOGINID, "0");
		
/*		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("EventDetails");
		query.whereEqualTo("eventId", memberList.get(position).eventId);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				int acceptedCount = 0;
				for (ParseObject parseObject : arg0) {
					if(parseObject.getBoolean("accepted"))
						acceptedCount++;
				}
				viewHolder.acceptedCount.setText("Invited: " + arg0.size() + " Accepted: " + acceptedCount );
				
			}
		});
*/
		
		
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("EventProfile");
		query.whereEqualTo("creatorId", userId);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objList, ParseException arg1) {
				// TODO Auto-generated method stub
				
				for(ParseObject object:objList){
				ClassEventProfile profile = new ClassEventProfile();
				profile.eventId = object.getString("eventId");
				profile.name = object.getString("eventName");
				profile.location = object.getString("location");
				profile.date = object.getString("date");
				profile.time = object.getString("time");
				eventProfileList.add(profile);
				}
				MyEventAdapter event = new MyEventAdapter(getActivity(),eventProfileList);
				listMyEvents.setAdapter(event);
				
				
			}
		});
		
		
		
		
		
		
		return view;
	}
	
	class MyEventAdapter  extends BaseAdapter {
		
		Context context;
		List<ClassEventProfile> memberList;
		
		public MyEventAdapter(Context context, List<ClassEventProfile> userIdList){
			this.context = context;
			this.memberList = userIdList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return memberList.size();
		}

		@Override
		public Object getItem(int pos) {
			return memberList.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				LayoutInflater inflator = ((Activity) context).getLayoutInflater();
				convertView = inflator.inflate(R.layout.view_myevent,
						null);
				viewHolder = new ViewHolder();
				viewHolder.eventName = (TextView) convertView
						.findViewById(R.id.textView1);
				viewHolder.date = (TextView) convertView
						.findViewById(R.id.textView2);
				viewHolder.acceptedCount = (TextView)convertView.findViewById(R.id.acceptedCount);
				viewHolder.confirmEvent = (Button) convertView.findViewById(R.id.confirmEvent);
				
				
				
				convertView.setTag(viewHolder);

			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.eventName.setText(memberList.get(position).name);
			viewHolder.date.setText(memberList.get(position).date+" "+memberList.get(position).time);
			viewHolder.confirmEvent.setTag(memberList.get(position));
			viewHolder.confirmEvent.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					final ProgressDialog diag = ProgressDialog.show(context, "Calculating....", "Please Wait while we otimize your trip");
					
					ClassEventProfile eventProfile = (ClassEventProfile)arg0.getTag();
					locationLatLng = eventProfile.location;
					ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("EventDetails");
					query.whereEqualTo("eventId", eventProfile.eventId);
					//query.whereEqualTo("accepted", true);
					query.findInBackground(new FindCallback<ParseObject>() {

						@Override
						public void done(List<ParseObject> arg0,
								ParseException arg1) {
							if(arg1 != null)
								return;
							GroupUsersintoCabs(arg0, locationLatLng);
							diag.dismiss();
						}
					});
				}
			});
			return convertView;
		}
		
		
		
	}
	class ViewHolder{
		TextView eventName,date, acceptedCount;
		Button confirmEvent;
	}
	
	private void GroupUsersintoCabs(List<ParseObject> allParticipants, String finalDestination)
	{
		int numCabsRequired = 0;
		ParseObject sourcePerson;
		String baseURL = "https://maps.googleapis.com/maps/api/directions/json?origin=";
		String sourceLatLong;
		if(allParticipants.size() <= 4)
		{
			numCabsRequired = 1;
			sourcePerson = getFarthestPerson(allParticipants, finalDestination);
			sourceLatLong = sourcePerson.getString("pickupLocation");
			baseURL += sourceLatLong + "&destination=" + finalDestination + "&waypoints=optimize:true";
			allParticipants.remove(sourcePerson);
			for (ParseObject parseObject : allParticipants) {
				baseURL += "|" + parseObject.getString("location");
			}
			baseURL += "&key=AIzaSyAKFnnMHpiUdw6Vr9YeUin9UPOj-DYGHuI";
			final String finalURL = baseURL;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					HttpClient client = new DefaultHttpClient();
					HttpGet request = new HttpGet(finalURL);
					try {
						HttpResponse response = client.execute(request);
						BufferedReader rd = new BufferedReader
								  (new InputStreamReader(response.getEntity().getContent()));
								    
								String line = "";
								String finalLine = "";
								while ((line = rd.readLine()) != null) {
								  finalLine+= line;
								} 
								Log.e("MyTag", finalLine);
								if(!TextUtils.isEmpty(finalLine)){
									Intent intent = new Intent(context, FinalizeEventActivity.class);
									intent.putExtra(Constants.BUN_GMAP_POINTS, finalLine);
									startActivity(intent);
								}
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
	
			
		}
		else if(allParticipants.size()<=10 && allParticipants.size() >4)
		{
			//Still Try and allocate a single Tempo Traveller

			numCabsRequired = 1;
			sourcePerson = getFarthestPerson(allParticipants, finalDestination);
			sourceLatLong = sourcePerson.getString("pickupLocation");
			baseURL += sourceLatLong + "&destination=" + finalDestination + "&waypoints=optimize:true";
			allParticipants.remove(sourcePerson);
			for (ParseObject parseObject : allParticipants) {
				baseURL += "|" + parseObject.getString("location");
			}
			baseURL += "&key=AIzaSyAKFnnMHpiUdw6Vr9YeUin9UPOj-DYGHuI";
			final String finalURL = baseURL;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					HttpClient client = new DefaultHttpClient();
					HttpGet request = new HttpGet(finalURL);
					try {
						HttpResponse response = client.execute(request);
						BufferedReader rd = new BufferedReader
								  (new InputStreamReader(response.getEntity().getContent()));
								    
								String line = "";
								String finalLine = "";
								while ((line = rd.readLine()) != null) {
								  finalLine+= line;
								} 
								Log.e("MyTag", finalLine);
								if(!TextUtils.isEmpty(finalLine)){
									Intent intent = new Intent(context, FinalizeEventActivity.class);
									intent.putExtra(Constants.BUN_GMAP_POINTS, finalLine);
									startActivity(intent);
								}
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
	
		}
		else
		{
			sourcePerson = getFarthestPerson(allParticipants, finalDestination);
			sourceLatLong = sourcePerson.getString("pickupLocation");
			
			ParseObject closestPerson = getClosestPerson(allParticipants, finalDestination);
			String  closestLatLong = closestPerson.getString("pickUpLocation");
			
			HashMap<String, ParseObject>UserGroupMap = new HashMap<String, ParseObject>();
			
			
			
		}
	}

	private ParseObject getFarthestPerson(List<ParseObject> allParticipants, String finalDestination)
	{	
		float currentDistance = 0.0f;
		ParseObject farthestPerson = null;
		for (ParseObject parseObject : allParticipants) {
			if( currentDistance <= getDistanceBetweenPoints(parseObject.getString("pickupLocation").trim(), finalDestination.trim()) )
			{
				farthestPerson = parseObject;
				currentDistance = getDistanceBetweenPoints(parseObject.getString("pickupLocation").trim(), finalDestination.trim());
			}
		}
		return farthestPerson;
	}
	
	private ParseObject getClosestPerson(List<ParseObject> allParticipants, String finalDestination)
	{	
		float currentDistance = Float.MAX_VALUE;
		ParseObject closestPerson = null;
		for (ParseObject parseObject : allParticipants) {
			if( currentDistance >= getDistanceBetweenPoints(parseObject.getString("pickupLocation").trim(), finalDestination.trim()) )
			{
				closestPerson = parseObject;
				currentDistance = getDistanceBetweenPoints(parseObject.getString("pickupLocation").trim(), finalDestination.trim());
			}
		}
		return closestPerson;
	}
	
	private float getDistanceBetweenPoints(String firstLocation, String secondLocation)
	{
		String[] sourceLatLong;
		String[] destLatLong;
		sourceLatLong = firstLocation.split(",");
		destLatLong = secondLocation.split(",");
		float[] results = new float[3];
		Location.distanceBetween(Float.parseFloat(sourceLatLong[0]), Float.parseFloat(sourceLatLong[1]), Float.parseFloat(destLatLong[0]), Float.parseFloat(destLatLong[1]),results);
		return results[0];
	}
	
	
}
