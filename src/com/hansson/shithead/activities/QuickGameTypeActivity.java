package com.hansson.shithead.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hansson.shithead.R;
import com.hansson.shithead.rr.BasicRequest;
import com.hansson.shithead.rr.BasicResponse;
import com.hansson.shithead.rr.ResponseStatus;
import com.hansson.shithead.util.Constants;
import com.hansson.shithead.util.GsonOperator;

public class QuickGameTypeActivity extends GCMActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.quick_game_type);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void handleGCMMessage(String message) {
        // TODO Auto-generated method stub
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constants.INVITE_FRIEND_RETURN_OK) {
            finish();
        }
    }

    public void randomQuickGameListener(View v) {
        new RandomQuickGameTask().execute();
        unregisterReceiver(mHandleMessageReceiver);
        mIsRegistered = false;
        finish();
    }

    public void friendsQuickGameListener(View v) {
        unregisterReceiver(mHandleMessageReceiver);
        mIsRegistered = false;
        Intent prefIntent = new Intent(this, QuickGameFriendsActivity.class);
        startActivityForResult(prefIntent, 0);
    }

    private class RandomQuickGameTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            BasicRequest request = new BasicRequest();
            SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
            String sessionId = settings.getString(Constants.PREF_SESSION, "");
            request.setSessionId(sessionId);
            return GsonOperator.sendAndRecieveGson(request, "find");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result.equals(Constants.CONNECTION_ERROR)) {
                    Toast.makeText(mContext, R.string.connection_error, Toast.LENGTH_LONG).show();
                } else {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                    BasicResponse fromJson = gson.fromJson(result, BasicResponse.class);
                    if (fromJson.getStatus() == ResponseStatus.OK) {
                        Toast.makeText(mContext, R.string.in_game_queue, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, R.string.invalid_session, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mContext, R.string.terrible_error, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
