package com.olathonxa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.olathonxa.util.Constants;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class LoginActivity extends Activity {

	SharedPreferences sharedpreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btn_ok = (Button)findViewById(R.id.button1);
		final EditText et_number = (EditText)findViewById(R.id.editText1);
		sharedpreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);	
		
		if(sharedpreferences.contains(Constants.SHARED_LOGINID))
		{
			Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
			startActivity(i);
			finish();
		}
		
		btn_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String number = et_number.getText().toString();
				
				ParsePush.subscribeInBackground("Phone-"+number, new SaveCallback() {
					
					@Override
					public void done(ParseException arg0) {
						// TODO Auto-generated method stub
						if(arg0==null){
							
						Toast.makeText(LoginActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();
						Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
						startActivity(i);
						finish();
						
						Editor editor = sharedpreferences.edit();
					      editor.putString(Constants.SHARED_LOGINID, number);
					      editor.commit(); 
						
						}
						else
						Toast.makeText(LoginActivity.this, arg0.getMessage().toString(), Toast.LENGTH_SHORT).show();
							
					}
				});
				
			}
		});
		
	}

}
