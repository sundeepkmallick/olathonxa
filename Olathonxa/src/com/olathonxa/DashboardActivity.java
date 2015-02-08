package com.olathonxa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;

import com.olathonxa.fragment.IAmInvited;
import com.olathonxa.fragment.MyEvents;
import com.olathonxa.util.Util;

public class DashboardActivity extends FragmentActivity {
	
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		context = DashboardActivity.this;
		
		FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(context, getSupportFragmentManager(), R.id.realtabcontent);

		mTabHost.addTab(
				mTabHost.newTabSpec(
						getResources().getString(R.string.my_events))
						.setIndicator(
								Util.getTabIndicator(mTabHost.getContext(),
										R.string.my_events)),
				MyEvents.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(
						getResources().getString(R.string.iam_invited))
						.setIndicator(
								Util.getTabIndicator(mTabHost.getContext(),
										R.string.iam_invited)),
				IAmInvited.class, null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		switch (id) {
		case R.id.action_add_event:
			Intent intent = new Intent(context, CreateEventActivity.class);
			startActivity(intent);
			
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
