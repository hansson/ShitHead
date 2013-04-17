package com.hansson.shithead.activities;

import static com.hansson.shithead.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hansson.sh_shared.rr.AuthResponse;
import com.hansson.sh_shared.rr.LoginRequest;
import com.hansson.sh_shared.rr.ResponseStatus;
import com.hansson.shithead.R;
import com.hansson.shithead.util.Constants;
import com.hansson.shithead.util.GsonOperator;
import com.hansson.shithead.util.Hash;
import com.hansson.shithead.util.HiddenConstants;

public class LoginActivity extends Activity {

	private Context mContext;
	private boolean mIsRegistered = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		setContentView(R.layout.login);
		mContext = this;
		handleGCMRegistration();
		SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
		String username = settings.getString(Constants.PREF_USERNAME, "");
		String password = settings.getString(Constants.PREF_PASSWORD, "");
		boolean autoLogin = settings.getBoolean(Constants.PREF_AUTO_LOGIN, false);
		CheckBox autoLoginBox = (CheckBox) findViewById(R.id.auto_login);
		autoLoginBox.setChecked(autoLogin);
		if (!username.equals("") && !password.equals("") && autoLogin) {
			LoginRequest params = new LoginRequest();
			params.setPassword(password);
			params.setUsername(username);
			findViewById(R.id.progress).setVisibility(View.VISIBLE);
			new AT_Login().execute(params);
		} else {
			((EditText) findViewById(R.id.username)).setText(username);
		}
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

	@Override
	protected void onDestroy() {
		if (mIsRegistered) {
			unregisterReceiver(mHandleMessageReceiver);
		}
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}

	public void loginListener(View v) {
		LoginRequest request = new LoginRequest();
		EditText findViewById = (EditText) findViewById(R.id.password);
		request.setPassword(Hash.getHash(findViewById.getText().toString()));
		request.setUsername(((EditText) findViewById(R.id.username)).getText().toString());
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
		//
		SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
		Editor editor = settings.edit();
		editor.putString(Constants.PREF_USERNAME, request.getUsername());
		CheckBox autoLogin = (CheckBox) findViewById(R.id.auto_login);
		if (autoLogin.isChecked()) {
			editor.putBoolean(Constants.PREF_AUTO_LOGIN, true);
			editor.putString(Constants.PREF_PASSWORD, request.getPassword());
		} else {
			editor.putBoolean(Constants.PREF_AUTO_LOGIN, false);
			editor.putString(Constants.PREF_PASSWORD, "");
		}
		editor.commit();
		new AT_Login().execute(request);
	}

	public void registerListener(View v) {
		Intent prefIntent = new Intent(this, RegisterActivity.class);
		this.startActivity(prefIntent);
	}

	private void handleGCMRegistration() {
		registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
		mIsRegistered = true;
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, HiddenConstants.SENDER_ID);
		} else {
			if (!GCMRegistrar.isRegisteredOnServer(this)) {
				SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(Constants.PREF_REGID, regId);
				editor.commit();
				Log.i("GCM", "Registered for GCM, RegId: " + regId);
			}
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new ShitHeadLoginBroadcastReciever();

	private class ShitHeadLoginBroadcastReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// For app id
		}
	}

	private class AT_Login extends AsyncTask<LoginRequest, Void, String> {

		@Override
		protected String doInBackground(LoginRequest... params) {
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			params[0].setRegId(settings.getString(Constants.PREF_REGID, ""));
			params[0].setVersion("0.15");
			String sendAndRecieveGson = GsonOperator.sendAndRecieveGson(params[0], "login");
			return sendAndRecieveGson;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				findViewById(R.id.progress).setVisibility(View.GONE);
				if (result.equals(Constants.CONNECTION_ERROR)) {
					Toast.makeText(mContext, R.string.connection_error, Toast.LENGTH_LONG).show();
				} else {
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
					AuthResponse fromJson = gson.fromJson(result, AuthResponse.class);
					SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
					Editor editor = settings.edit();
					if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
						editor.putString(Constants.PREF_USERNAME, "");
						editor.putString(Constants.PREF_PASSWORD, "");
						editor.commit();
						Toast.makeText(mContext, R.string.invalid_credentials, Toast.LENGTH_LONG).show();
					} else if (fromJson.getStatus() == ResponseStatus.BAD_VERSION) {
						Toast.makeText(mContext, R.string.invalid_version, Toast.LENGTH_LONG).show();
					} else {
						editor.putString(Constants.PREF_SESSION, fromJson.getSessionId());
						editor.commit();
						unregisterReceiver(mHandleMessageReceiver);
						mIsRegistered = false;
						Intent prefIntent = new Intent(LoginActivity.this, MenuActivity.class);
						LoginActivity.this.startActivity(prefIntent);
					}
				}
			} catch (Exception e) {
				Toast.makeText(mContext, R.string.terrible_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}
}
