package com.olathonxa;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SendCallback;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsPromptResult;
import android.widget.Button;
import android.widget.Toast;

public class CreateEventActivity1 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);
		
		Button b = (Button)findViewById(R.id.button1);
		
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ParsePush push = new ParsePush();
				push.setChannel("Phone-8867196850");
				JSONObject obj = new JSONObject();
				try {
					obj.put("mydata", "data");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				push.setData(obj);
				push.sendInBackground(new SendCallback() {
					
					@Override
					public void done(ParseException arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(CreateEventActivity1.this, "Sent", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

}
