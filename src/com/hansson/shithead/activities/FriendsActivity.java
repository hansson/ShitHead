package com.hansson.shithead.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hansson.shithead.R;
import com.hansson.shithead.entitys.Friend;
import com.hansson.shithead.rr.BasicRequest;
import com.hansson.shithead.rr.BasicResponse;
import com.hansson.shithead.rr.FriendRequest;
import com.hansson.shithead.rr.FriendResponse;
import com.hansson.shithead.rr.ResponseStatus;
import com.hansson.shithead.util.Constants;
import com.hansson.shithead.util.GsonOperator;

public class FriendsActivity extends GCMActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.friends);
        findViewById(R.id.add_friend).setOnClickListener(new L_AddFriend());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        new AT_ListFriends().execute();
        super.onResume();
    }

    @Override
    protected void handleGCMMessage(String message) {
        // TODO Auto-generated method stub
    }

    private void populateFriendView(Friend friend, RelativeLayout view) {
        TextView friendName = (TextView) view.findViewById(R.id.friend_name);
        friendName.setText(friend.getUsername());
        ImageView removeFriend = (ImageView) view.findViewById(R.id.remove_friend);
        removeFriend.setOnClickListener(new L_RemoveFriend(friend));
    }

    private void populateAcceptFriendView(Friend friend, RelativeLayout view) {
        TextView friendName = (TextView) view.findViewById(R.id.friend_name);
        friendName.setText(friend.getUsername());
        ImageView acceptFriend = (ImageView) view.findViewById(R.id.accept_friend);
        acceptFriend.setOnClickListener(new L_AcceptFriend(friend));
        ImageView denyFriend = (ImageView) view.findViewById(R.id.deny_friend);
        denyFriend.setOnClickListener(new L_RemoveFriend(friend));
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
                        LinearLayout friendContainer = (LinearLayout) findViewById(R.id.friend_container);
                        LinearLayout pendingFriendContainer = (LinearLayout) findViewById(R.id.pending_friend_container);
                        friendContainer.removeAllViews();
                        pendingFriendContainer.removeAllViews();
                        for (Friend friend : fromJson.getFriends()) {
                            if (friend.isAccepted()) {
                                RelativeLayout inflate = (RelativeLayout) getLayoutInflater().inflate(R.layout.friend, null);
                                populateFriendView(friend, inflate);
                                friendContainer.addView(inflate);
                            } else {
                                RelativeLayout inflate = (RelativeLayout) getLayoutInflater().inflate(R.layout.accept_friend, null);
                                populateAcceptFriendView(friend, inflate);
                                pendingFriendContainer.addView(inflate);
                            }
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
    }


    private class AT_AcceptFriend extends AsyncTask<Object, Void, Object[]> {

        @Override
        protected Object[] doInBackground(Object... params) {
            FriendRequest request = new FriendRequest();
            SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
            request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
            request.setFriendUsername(((Friend) params[0]).getUsername());
            String sendAndReceiveGson = GsonOperator.sendAndReceiveGson(request, "friends/accept");
            Object[] returnArray = new Object[3];
            returnArray[0] = sendAndReceiveGson;
            returnArray[1] = params[1];
            returnArray[2] = params[0];
            return returnArray;
        }

        @Override
        protected void onPostExecute(Object[] result) {
            try {
                findViewById(R.id.progress).setVisibility(View.GONE);
                if (result[0].equals(Constants.CONNECTION_ERROR)) {
                    Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_LONG).show();
                } else {
                    Gson gson = new Gson();
                    BasicResponse fromJson = gson.fromJson((String) result[0], BasicResponse.class);
                    if (fromJson.getStatus() == ResponseStatus.OK) {
                        LinearLayout friendContainer = (LinearLayout) findViewById(R.id.friend_container);
                        LinearLayout pendingFriendContainer = (LinearLayout) findViewById(R.id.pending_friend_container);
                        pendingFriendContainer.removeView((View) result[1]);
                        RelativeLayout inflate = (RelativeLayout) getLayoutInflater().inflate(R.layout.friend, null);
                        populateFriendView((Friend) result[2], inflate);
                        friendContainer.addView(inflate);
                    } else if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
                        Toast.makeText(mContext, R.string.error_invalid_session, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mContext, R.string.error_terrible, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
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
                        new AT_ListFriends().execute();
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

    private class L_AddFriend implements OnClickListener {

        @Override
        public void onClick(View v) {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            EditText addFriend = (EditText) findViewById(R.id.search_friend);
            new AT_AddFriend().execute(addFriend.getText().toString());
        }
    }

    private class L_RemoveFriend implements OnClickListener {

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

    private class L_AcceptFriend implements OnClickListener {

        private Friend mFriend;

        public L_AcceptFriend(Friend friend) {
            mFriend = friend;
        }

        @Override
        public void onClick(View v) {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            new AT_AcceptFriend().execute(mFriend, v.getParent());
        }
    }

}
