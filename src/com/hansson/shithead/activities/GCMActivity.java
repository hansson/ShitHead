package com.hansson.shithead.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.hansson.shithead.R;
import com.hansson.shithead.util.Constants;

import static com.hansson.shithead.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.hansson.shithead.CommonUtilities.EXTRA_MESSAGE;

public abstract class GCMActivity extends Activity {

	protected Context mContext;
	protected boolean mIsRegistered = false;
	private int mId = 0xbbbb; // Notification id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		mContext = this;
		handleGCMRegistration();
	}

	@Override
	protected void onDestroy() {
		if (mIsRegistered) {
			unregisterReceiver(mHandleMessageReceiver);
		}
		if (GCMRegistrar.isRegistered(mContext)) {
			GCMRegistrar.onDestroy(this);
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		if (!mIsRegistered) {
			registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
			mIsRegistered = true;
		}
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!mIsRegistered) {
			registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
			mIsRegistered = true;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void handleGCMRegistration() {
		registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
	}

	private void createNotification() {
		Log.d("Notification", "Showing notification");
		long[] pattern = { 100L, 100L, 100L, 100L, 500L };
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(
				getResources().getString(R.string.app_name)).setContentText(getResources().getString(R.string.game_new_move)).setAutoCancel(true).setVibrate(pattern);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MenuActivity.class);
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MenuActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		notificationManager.notify(mId, mBuilder.build());
	}

	protected abstract void handleGCMMessage(String message);

	protected final BroadcastReceiver mHandleMessageReceiver = new ShitHeadLoginBroadcastReceiver();

	private class ShitHeadLoginBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			if (newMessage.toLowerCase().contains(settings.getString(Constants.PREF_USERNAME, "").toLowerCase()) || newMessage.contains("GCM_START")) {
				createNotification();
			}
			handleGCMMessage(newMessage);
		}
	}
}
