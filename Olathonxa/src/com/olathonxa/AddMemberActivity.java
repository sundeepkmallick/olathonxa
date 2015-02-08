package com.olathonxa;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.olathonxa.adapter.AddMemberAdapter;
import com.olathonxa.model.AddMember;
import com.olathonxa.util.Constants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class AddMemberActivity extends Activity {
	
	Context context;
	String eventId;
	
	EditText txtAddMember;
	ListView listMember;
	Button btnAdd;
	
	ArrayList<AddMember> memberList;
	AddMemberAdapter adapterAddMember;

	SharedPreferences sharedpreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_member);
		
		context = AddMemberActivity.this;
		
		final ClassEventProfile eventProfile = (ClassEventProfile)getIntent().getSerializableExtra("EventProfile");
		Button btn_submit = (Button)findViewById(R.id.submit_button);
		memberList = new ArrayList<AddMember>();
		
		listMember = (ListView) findViewById(R.id.listViewMember);
		txtAddMember = (EditText) findViewById(R.id.editTextAddMember);
		btnAdd = (Button) findViewById(R.id.buttonAdd);
		
		adapterAddMember = new AddMemberAdapter(context, memberList);
		listMember.setAdapter(adapterAddMember);
		
		sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
		final String userId = sharedpreferences.getString(Constants.SHARED_LOGINID, "0");
		
		btnAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String userId = txtAddMember.getText().toString();
				
				if(!TextUtils.isEmpty(userId)){
					AddMember member = new AddMember();
					member.setUserId(userId);
					
					memberList.add(member);
					adapterAddMember.notifyDataSetChanged();
				}
				
			}
		});
		
		btn_submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(memberList.size()==0)
				return;
				
				final String eventId = userId+System.currentTimeMillis();
				ParseObject eventProfileObject = new ParseObject("EventProfile");
				eventProfileObject.put("eventName", eventProfile.name);
				eventProfileObject.put("location", eventProfile.location);
				eventProfileObject.put("date", eventProfile.date);
				eventProfileObject.put("time", eventProfile.time);
				eventProfileObject.put("eventId",eventId );
				eventProfileObject.put("creatorId", userId);
				
				eventProfileObject.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException arg0) {
						// TODO Auto-generated method stub
						if(arg0 == null)
						{
							for(final AddMember member:memberList)
							{
								ParseObject eventDetailsObject = new ParseObject("EventDetails");
								eventDetailsObject.put("parti_id", member.getUserId());
								//eventDetailsObject.put("accepted", false);
								eventDetailsObject.put("eventId", eventId);
								
								eventDetailsObject.saveInBackground(new SaveCallback() {
									
									@Override
									public void done(ParseException arg0) {
										// TODO Auto-generated method stub
										ParsePush push = new ParsePush();
										push.setChannel("Phone-"+member.getUserId());
										JSONObject data = new JSONObject();
										try {
											data.put("eventId", eventId);
											data.put("eventName", eventProfile.name);
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											return;
										}
										
										push.setData(data);
										push.sendInBackground();
										
										try {
											Thread.sleep(700);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
									}
								});
							}
						}
					}
				});
				
				
				finish();
			}
		});
	}

}
