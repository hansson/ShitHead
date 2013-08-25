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
import com.google.gson.GsonBuilder;
import com.hansson.shithead.R;
import com.hansson.shithead.entitys.Friend;
import com.hansson.shithead.rr.BasicRequest;
import com.hansson.shithead.rr.BasicResponse;
import com.hansson.shithead.rr.FriendRequest;
import com.hansson.shithead.rr.FriendResponse;
import com.hansson.shithead.rr.InviteFriendRequest;
import com.hansson.shithead.rr.ResponseStatus;
import com.hansson.shithead.util.Constants;
import com.hansson.shithead.util.GsonOperator;

import java.util.LinkedList;
import java.util.List;

public class FriendsGameActivity extends GCMActivity {

    private List<String> mFriendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.friends_game);
        super.onCreate(savedInstanceState);
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        new AT_ListFriends().execute();
    }

    @Override
    protected void handleGCMMessage(String message) {

    }

    public void startGameListener(View v) {
//        CheckBox privateGame = (CheckBox) findViewById(R.id.private_game);
        //TODO ask user if  private game
        new AT_InviteFriends().execute(mFriendList, true);
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
            return GsonOperator.sendAndReceiveGson(request, "friends/list");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                findViewById(R.id.progress).setVisibility(View.GONE);
                if (result.equals(Constants.CONNECTION_ERROR)) {
                    Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_LONG).show();
                } else {
                    Gson gson = new Gson();
                    FriendResponse fromJson = gson.fromJson(result, FriendResponse.class);
                    if (fromJson.getStatus() == ResponseStatus.OK) {
                        mFriendList = new LinkedList<String>();
                        LinearLayout friendContainer = (LinearLayout) findViewById(R.id.friend_container);
                        friendContainer.removeAllViews();
                        for (Friend friend : fromJson.getFriends()) {
                                RelativeLayout inflate = (RelativeLayout) getLayoutInflater().inflate(R.layout.friend, null);
                                populateFriendView(friend, inflate);
                                friendContainer.addView(inflate);
                        }
                    } else if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
                        Toast.makeText(mContext, R.string.error_invalid_session, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mContext, R.string.error_terrible, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        private void populateFriendView(Friend friend, RelativeLayout inflate) {
            CheckBox checkFriend = (CheckBox) inflate.findViewById(R.id.check_friend);
            checkFriend.setOnCheckedChangeListener(new L_HandleCheckFriend(friend));
            TextView username = (TextView) inflate.findViewById(R.id.friend_name);
            username.setText(friend.getUsername());
        }
    }

    private class AT_InviteFriends extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            InviteFriendRequest request = new InviteFriendRequest();
            SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
            request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
            request.setFriends((List<String>)params[0]);
            request.setPrivateGame((Boolean)params[1]);
            return GsonOperator.sendAndReceiveGson(request, "find/invite");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result.equals(Constants.CONNECTION_ERROR)) {
                    Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_LONG).show();
                } else {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                    BasicResponse fromJson = gson.fromJson(result, BasicResponse.class);
                    if (fromJson.getStatus() == ResponseStatus.OK) {
                        Toast.makeText(mContext, R.string.menu_in_game_queue, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, R.string.error_invalid_session, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mContext, R.string.error_terrible, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

    }

    private class AT_RemoveFriend extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            FriendRequest request = new FriendRequest();
            SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
            request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
            request.setFriendUsername(params[0]);
            return GsonOperator.sendAndReceiveGson(request, "friends/remove");
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
                        Toast.makeText(mContext, R.string.error_friend_removed, Toast.LENGTH_LONG).show();
                        new AT_ListFriends().execute();
                    } else if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
                        Toast.makeText(mContext, R.string.error_invalid_session, Toast.LENGTH_LONG).show();
                    } else if (fromJson.getStatus() == ResponseStatus.NOT_OK) {
                        Toast.makeText(mContext, R.string.error_invalid_friend, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mContext, R.string.error_terrible, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
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
                mFriendList.add(mFriend.getUsername());
            } else if(!b) {
                mFriendList.remove(mFriend.getUsername());
            }
        }
    }

    private class L_RemoveFriend implements View.OnClickListener {

        private Friend mFriend;

        public L_RemoveFriend(Friend friend) {
            mFriend = friend;
        }

        @Override
        public void onClick(View v) {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            new AT_RemoveFriend().execute(mFriend.getUsername());
        }
    }
}
