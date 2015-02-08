package com.olathonxa;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.parse.ParsePushBroadcastReceiver;

public class MessageReceiver extends ParsePushBroadcastReceiver {

	Context ctx;

	@Override
	protected void onPushReceive(Context c, Intent intent) {
		// TODO Auto-generated method stub
		super.onPushReceive(c, intent);
		ctx = c;

		String data = intent.getExtras().getString("com.parse.Data");
		Toast.makeText(c, data, Toast.LENGTH_SHORT).show();
		
		showNotification();
	}

	void showNotification() {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Event Invitation").setContentText("click me");
		Intent resultIntent = new Intent(ctx, DashboardActivity.class);

		// Because clicking the notification opens a new ("special") activity,
		// there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx, 0,
				resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		NotificationManager mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	}

}
