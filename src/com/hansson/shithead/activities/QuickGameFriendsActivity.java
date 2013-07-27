package com.hansson.shithead.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hansson.shithead.R;
import com.hansson.shithead.entitys.Friend;
import com.hansson.shithead.rr.BasicRequest;
import com.hansson.shithead.rr.FriendResponse;
import com.hansson.shithead.rr.ResponseStatus;
import com.hansson.shithead.util.Constants;
import com.hansson.shithead.util.GsonOperator;

import java.util.LinkedList;
import java.util.List;

public class QuickGameFriendsActivity extends GCMActivity {

    private List<Friend> mFriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.friends_quick_game);
        super.onCreate(savedInstanceState);
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        new AT_ListFriends().execute();
    }

    @Override
    protected void handleGCMMessage(String message) {

    }

    public void startGameListener(View v) {
        unregisterReceiver(mHandleMessageReceiver);
        mIsRegistered = false;
        setResult(Constants.INVITE_FRIEND_RETURN_OK);
        finish();
    }

    private class AT_ListFriends extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            BasicRequest request = new BasicRequest();
            SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
            request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
            return GsonOperator.sendAndRecieveGson(request, "friends/list");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                findViewById(R.id.progress).setVisibility(View.GONE);
                if (result.equals(Constants.CONNECTION_ERROR)) {
                    Toast.makeText(mContext, R.string.connection_error, Toast.LENGTH_LONG).show();
                } else {
                    Gson gson = new Gson();
                    FriendResponse fromJson = gson.fromJson(result, FriendResponse.class);
                    if (fromJson.getStatus() == ResponseStatus.OK) {
                        mFriendList = new LinkedList<Friend>();
                        LinearLayout friendContainer = (LinearLayout) findViewById(R.id.friend_container);
                        friendContainer.removeAllViews();
                        for (Friend friend : fromJson.getFriends()) {
                            if (friend.isAccepted()) {
                                RelativeLayout inflate = (RelativeLayout) getLayoutInflater().inflate(R.layout.invite_friend, null);
                                populateFriendView(friend, inflate);
                                friendContainer.addView(inflate);
                            }
                        }
                    } else if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
                        Toast.makeText(mContext, R.string.invalid_session, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mContext, R.string.terrible_error, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        private void populateFriendView(Friend friend, RelativeLayout inflate) {
            CheckBox checkFriend = (CheckBox) inflate.findViewById(R.id.choose_friend);
            checkFriend.setOnCheckedChangeListener(new L_HandleCheckFriend(friend));
            TextView username = (TextView) inflate.findViewById(R.id.friend_name);
            username.setText(friend.getUsername());
        }
    }

    private class L_HandleCheckFriend implements CompoundButton.OnCheckedChangeListener {
        private Friend mFriend;
        public L_HandleCheckFriend(Friend friend) {
            mFriend = friend;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(b && mFriendList.size() < 3)  {
                mFriendList.add(mFriend);
            } else if(!b) {
                mFriendList.remove(mFriend);
            }
        }
    }
}
