package com.olathonxa.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.olathonxa.ClassEventProfile;
import com.olathonxa.R;
import com.olathonxa.SelectPickUpLocationActivity;
import com.olathonxa.util.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class IAmInvited  extends Fragment{
	
	Context context;
	
	ListView listImgoing;
	
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
	ArrayList<String> myEventIdList = new ArrayList<String>();
	String userId;
	HashMap<String, String> eventAcceptanceStatus = new HashMap<String, String>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_myevents, container, false);
		eventProfileList = new ArrayList<ClassEventProfile>();
		listImgoing = (ListView) view.findViewById(R.id.listViewMyEvents);
		sharedpreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
		userId = sharedpreferences.getString(Constants.SHARED_LOGINID, "0");
		updateList();
		
		return view;
	}
	
	private void updateList() {
		if(eventProfileList!=null && eventProfileList.size()>0){
			eventProfileList.clear();
		}else{
			eventProfileList = new ArrayList<ClassEventProfile>();
		}
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("EventDetails");
		query.whereEqualTo("parti_id", userId);
		
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objList, ParseException arg1) {
				// TODO Auto-generated method stub
				
				for(ParseObject object:objList){
					myEventIdList.add(object.getString("eventId"));
					if(object.get("accepted") != null)
						eventAcceptanceStatus.put(object.getString("eventId"), object.get("accepted").toString());
					else
						eventAcceptanceStatus.put(object.getString("eventId"), "");
				}
				
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("EventProfile");
				query.whereContainedIn("eventId", myEventIdList);
				query.findInBackground(new FindCallback<ParseObject>() {
					
					@Override
					public void done(List<ParseObject> objList, ParseException arg1) {
						// TODO Auto-generated method stub
						
						for(ParseObject object:objList){
							
							
						ClassEventProfile profile = new ClassEventProfile();
						profile.eventId = object.getString("eventId");
						profile.name = object.getString("eventName");
						profile.date = object.getString("date");
						profile.time = object.getString("time");
						profile.location = object.getString("location");
						eventProfileList.add(profile);
						MyEventAdapter event = new MyEventAdapter(getActivity(),eventProfileList);
						listImgoing.setAdapter(event);
						}
						
					}
				});
				  
			}
		});
		
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
				convertView = inflator.inflate(R.layout.view_imgoing,
						null);
				viewHolder = new ViewHolder();
				viewHolder.eventName = (TextView) convertView
						.findViewById(R.id.textView1);
				viewHolder.date = (TextView) convertView
						.findViewById(R.id.textView2);
				viewHolder.location = (TextView)convertView.findViewById(R.id.textView3);
				viewHolder.img_acc = (ImageView)convertView.findViewById(R.id.imageView1);
				viewHolder.acceptEvent = (Button)convertView.findViewById(R.id.acceptEvent);
				
				viewHolder.rejectEvent = (Button)convertView.findViewById(R.id.rejectEvent);
				
				convertView.setTag(viewHolder);

			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.eventName.setText(memberList.get(position).name);
			viewHolder.date.setText(memberList.get(position).date+" "+memberList.get(position).time);
			viewHolder.location.setText(memberList.get(position).location);
			if(eventAcceptanceStatus.get(memberList.get(position).eventId).equals("false"))
			{
				viewHolder.acceptEvent.setVisibility(View.INVISIBLE);
				viewHolder.rejectEvent.setVisibility(View.INVISIBLE);
				viewHolder.img_acc.setImageResource(R.drawable.rejected);
			}
			else if(eventAcceptanceStatus.get(memberList.get(position).eventId).equals("true"))
			{
				viewHolder.acceptEvent.setVisibility(View.INVISIBLE);
				viewHolder.rejectEvent.setVisibility(View.INVISIBLE);
				viewHolder.img_acc.setImageResource(R.drawable.confirm);
			}
			viewHolder.acceptEvent.setTag(position);
			viewHolder.rejectEvent.setTag(position);
			viewHolder.acceptEvent.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int position = Integer.parseInt(v.getTag().toString());
					String event_id = memberList.get(position).eventId;
					/*Bundle args = new Bundle();
					args.putString(Constants.BUN_EVENT_ID, event_id);
					DialogFragment newFragment = new MemberAddressInputDialog();  
					newFragment.setArguments(args);
					newFragment.show(getFragmentManager(), "Member Address Input Dialog");*/
					Intent intent = new Intent(context, SelectPickUpLocationActivity.class);
					intent.putExtra(Constants.BUN_EVENT_ID, event_id);
					startActivityForResult(intent, 500);
					/*
					int position = Integer.parseInt(v.getTag().toString());
					
				*/}
			});
			viewHolder.rejectEvent.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int position = Integer.parseInt(v.getTag().toString());
					ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("EventDetails");
					query.whereEqualTo("eventId", memberList.get(position).eventId);
					query.findInBackground(new FindCallback<ParseObject>() {

						@Override
						public void done(List<ParseObject> arg0,
								ParseException arg1) {
							for (ParseObject parseObject : arg0) {
								if(parseObject.get("parti_id").equals(userId))
								{
									parseObject.put("accepted", false);
									parseObject.saveInBackground(new SaveCallback() {
										
										@Override
										public void done(ParseException arg0) {
											if(arg0 == null)
												((MyEventAdapter)listImgoing.getAdapter()).notifyDataSetChanged();
										}
									});
								}
							}
						}
					});
				}
			});
			return convertView;
		}
		
		
		
	}
	class ViewHolder{
		TextView eventName,date,location;
		ImageView img_acc;
		Button acceptEvent, rejectEvent;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("resultCode","_"+resultCode);
		switch(requestCode){
		case 500:
			updateList();
			((MyEventAdapter)listImgoing.getAdapter()).notifyDataSetChanged();
			break;
		}
	}
}
