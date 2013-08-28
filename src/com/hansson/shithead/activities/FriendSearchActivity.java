package com.hansson.shithead.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hansson.shithead.R;
import com.hansson.shithead.rr.BasicResponse;
import com.hansson.shithead.rr.FriendRequest;
import com.hansson.shithead.rr.ResponseStatus;
import com.hansson.shithead.util.Constants;
import com.hansson.shithead.util.GsonOperator;

public class FriendSearchActivity extends GCMActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.friends);
        findViewById(R.id.add_friend).setOnClickListener(new L_AddFriend());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    protected void handleGCMMessage(String message) {
        // TODO Auto-generated method stub
    }

    private class AT_AddFriend extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            FriendRequest request = new FriendRequest();
            SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
            request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
            request.setFriendUsername(params[0]);
            return GsonOperator.sendAndReceiveGson(request, "friends/add");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                findViewById(R.id.progress).setVisibility(View.GONE);
                if (result.equals(Constants.CONNECTION_ERROR)) {
                    Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_LONG).show();
                } else {
                    Gson gson = new Gson();
                    BasicResponse fromJson = gson.fromJson(result, BasicResponse.class);
                    if (fromJson.getStatus() == ResponseStatus.OK) {
                        Toast.makeText(mContext, R.string.menu_friend_added, Toast.LENGTH_LONG).show();
                    } else if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
                        Toast.makeText(mContext, R.string.error_invalid_session, Toast.LENGTH_LONG).show();
                    } else if (fromJson.getStatus() == ResponseStatus.NOT_OK) {
                        Toast.makeText(mContext, R.string.error_invalid_friend, Toast.LENGTH_LONG).show();
                    } else if (fromJson.getStatus() == ResponseStatus.FRIEND_EXISTS) {
                        Toast.makeText(mContext, R.string.error_friend_already_added, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mContext, R.string.error_terrible, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private class L_AddFriend implements OnClickListener {

        @Override
        public void onClick(View v) {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            EditText addFriend = (EditText) findViewById(R.id.search_friend);
            new AT_AddFriend().execute(addFriend.getText().toString());
        }
    }

}
