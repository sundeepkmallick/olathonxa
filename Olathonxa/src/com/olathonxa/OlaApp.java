package com.olathonxa;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class OlaApp extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Parse.initialize(this, "Y3QSGeYyQq3gAQX4H86zlxr6BlNkwifhU2ForaBL", "5vc1V1VCxjv9q9X6ixyTy9i8VGB56fm0hBo7NfWM");
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}
	
}
