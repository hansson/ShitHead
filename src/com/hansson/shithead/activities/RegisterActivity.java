package com.hansson.shithead.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.hansson.shithead.R;
import com.hansson.shithead.rr.BasicResponse;
import com.hansson.shithead.rr.RegistrationRequest;
import com.hansson.shithead.rr.ResponseStatus;
import com.hansson.shithead.util.Constants;
import com.hansson.shithead.util.GsonOperator;
import com.hansson.shithead.util.Hash;

public class RegisterActivity extends Activity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mContext = getApplicationContext();
    }

    public void registerListener(View v) {
        TextView password = (TextView) findViewById(R.id.password);
        TextView password2 = (TextView) findViewById(R.id.password2);
        TextView email = (TextView) findViewById(R.id.email);
        TextView username = (TextView) findViewById(R.id.username);
        if (!password.getText().toString().equals(password2.getText().toString())) {
            Toast.makeText(mContext, R.string.error_passwords_not_equal, Toast.LENGTH_LONG).show();
        } else if (password.getText().length() < 5) {
            Toast.makeText(mContext, R.string.error_password_to_short, Toast.LENGTH_LONG).show();
        } else if (email.getText().equals("") || username.getText().equals("")) {
            Toast.makeText(mContext, R.string.error_all_fields_mandatory, Toast.LENGTH_LONG).show();
        } else if (username.getText().length() > 20) {
            Toast.makeText(mContext, R.string.error_username_to_long, Toast.LENGTH_LONG).show();
        } else if (password.getText().length() > 20) {
            Toast.makeText(mContext, R.string.error_password_to_long, Toast.LENGTH_LONG).show();
        } else {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            new RegisterTask().execute(username.getText().toString(), email.getText().toString(), password.getText().toString());
        }
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            RegistrationRequest request = new RegistrationRequest();
            request.setUsername(params[0]);
            request.setEmail(params[1]);
            request.setPassword(Hash.getHash(params[2]));
            SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
            request.setRegId(settings.getString(Constants.PREF_REGID, ""));
            return GsonOperator.sendAndReceiveGson(request, "register");
        }

        @Override
        protected void onPostExecute(String result) {
            findViewById(R.id.progress).setVisibility(View.GONE);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
            if (result == null) {
                Toast.makeText(mContext, R.string.error_terrible, Toast.LENGTH_LONG).show();
            } else if (result.equals(Constants.CONNECTION_ERROR)) {
                Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_LONG).show();
            } else {
                try {
                    BasicResponse fromJson = gson.fromJson(result, BasicResponse.class);
                    if (fromJson.getStatus().equals(ResponseStatus.EMAL_OR_USERNAME_EXISTS)) {
                        Toast.makeText(mContext, R.string.error_username_or_email_error, Toast.LENGTH_LONG).show();
                    } else if (fromJson.getStatus().equals(ResponseStatus.NOT_OK)) {
                        Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, R.string.login_user_created, Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JsonSyntaxException e) {
                    Toast.makeText(mContext, R.string.error_terrible, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
