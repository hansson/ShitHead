package com.hansson.shithead.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hansson.shithead.R;
import com.hansson.shithead.gcm.GCMBaseMessage;
import com.hansson.shithead.gcm.GCMGameStart;
import com.hansson.shithead.gcm.GCMTypes;
import com.hansson.shithead.rr.ActiveGame;
import com.hansson.shithead.rr.ActiveGamesResponse;
import com.hansson.shithead.rr.BasicRequest;
import com.hansson.shithead.rr.GameStateRequest;
import com.hansson.shithead.rr.GameStateResponse;
import com.hansson.shithead.rr.ResponseStatus;
import com.hansson.shithead.util.Constants;
import com.hansson.shithead.util.GsonOperator;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MenuActivity extends GCMActivity {

    private Date mLastUpdate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.menu);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        new AT_RequestGames().execute();
        super.onResume();
    }

    public void startGameListener(View v) {
        unregisterReceiver(mHandleMessageReceiver);
        mIsRegistered = false;
        Intent prefIntent = new Intent(this, StartGameActivity.class);
        this.startActivity(prefIntent);
    }

    @Override
    protected void handleGCMMessage(String message) {
        Log.d("GCM_MESSAGE", message);
        if (message.contains("{")) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
            GCMBaseMessage fromJson = gson.fromJson(message, GCMBaseMessage.class);
            if (fromJson.getGCMType().equals(GCMTypes.GCM_START)) {
                GCMGameStart gcmMessage = gson.fromJson(message, GCMGameStart.class);
                new AT_RequestGameState().execute(gcmMessage.getGameId());
            } else if (fromJson.getGCMType() == GCMTypes.GCM_PLAYER_MOVE || fromJson.getGCMType() == GCMTypes.GCM_PLAYER_MOVE_FACE_DOWN || fromJson.getGCMType() == GCMTypes.GCM_SWITCHING_DONE || fromJson.getGCMType() == GCMTypes.GCM_QUEUED) {
                new AT_RequestGames().execute();
            }
        }
    }

    private void createGameBoard(GameStateResponse fromJson) {
        Intent prefIntent = new Intent(this, GameActivity.class);
        prefIntent.putExtra("com.hansson.shithead.LoadGame", fromJson);
        this.startActivity(prefIntent);
    }

    private class AT_RequestGameState extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            GameStateRequest request = new GameStateRequest();
            request.setGameId(params[0]);
            SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
            request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
            return GsonOperator.sendAndReceiveGson(request, "game");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                findViewById(R.id.progress).setVisibility(View.GONE);
                if (result.equals(Constants.CONNECTION_ERROR)) {
                    Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_LONG).show();
                } else {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                    GameStateResponse fromJson = gson.fromJson(result, GameStateResponse.class);
                    if (fromJson.getStatus() == ResponseStatus.OK) {
                        createGameBoard(fromJson);
                    } else if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
                        Toast.makeText(mContext, R.string.error_invalid_session, Toast.LENGTH_LONG).show();
                    } else if (fromJson.getStatus() == ResponseStatus.INVALID_GAME) {
                        Toast.makeText(mContext, R.string.error_invalid_game, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mContext, R.string.error_terrible, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private class AT_RequestGames extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            if (mLastUpdate != null && new Date().getTime() - mLastUpdate.getTime() < 10000) {
                return "-";
            }
            mLastUpdate = new Date();
            BasicRequest request = new BasicRequest();
            SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
            request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
            return GsonOperator.sendAndReceiveGson(request, "games");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                findViewById(R.id.progress).setVisibility(View.GONE);
                if (result.equals(Constants.CONNECTION_ERROR)) {
                    Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_LONG).show();
                } else if (!result.equals("-")){
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                    ActiveGamesResponse fromJson = gson.fromJson(result, ActiveGamesResponse.class);
                    if (fromJson.getStatus() == ResponseStatus.OK) {
                        LinearLayout yourTurn = (LinearLayout) findViewById(R.id.your_turn);
                        LinearLayout opponentTurn = (LinearLayout) findViewById(R.id.opponent_turn);
                        yourTurn.removeAllViews();
                        opponentTurn.removeAllViews();
                        int queuedGamesCount = 0;
                        for (ActiveGame game : fromJson.getGames()) {
                            RelativeLayout inflate = (RelativeLayout) getLayoutInflater().inflate(R.layout.active_game, null);
                            assert inflate != null;
                            TextView startedAt = (TextView) inflate.findViewById(R.id.game_started);
                            if (game.getStartedAt() != null) {
                                String startedAtString = java.text.DateFormat.getDateTimeInstance().format(game.getStartedAt());
                                startedAt.setText(getString(R.string.menu_started) + " " + startedAtString);
                                String timeLeftString;
                                if (game.getLastMove() != null) {
                                    timeLeftString = displayGame(game);
                                } else {
                                    timeLeftString = "-";
                                }
                                TextView timeLeft = (TextView) inflate.findViewById(R.id.time_left);
                                timeLeft.setText(timeLeftString);
                                inflate.setOnClickListener(new L_ActiveGame(game.getGameId()));
                                //TODO get from saved username
                                if (game.getCurrentPlayerName().equals("linne")) {
                                    yourTurn.addView(inflate);
                                } else {
                                    opponentTurn.addView(inflate);
                                }
                            } else {
                                queuedGamesCount++;
                            }
                        }
                        TextView viewById = (TextView) findViewById(R.id.queue);
                        viewById.setText(String.format(getResources().getString(R.string.menu_queue_size), queuedGamesCount));
                    } else if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
                        Toast.makeText(mContext, R.string.error_invalid_session, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(mContext, R.string.error_terrible, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        private String displayGame(ActiveGame game) {
            //GSON doesn't handle the Zulu time very well
            //This part is for changing the time zone without changing the time
            Calendar userTime = Calendar.getInstance(TimeZone.getDefault());
            Calendar utcTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            userTime.setTime(game.getLastMove());
            for (int i = 0; i < Calendar.FIELD_COUNT; i++) {
                utcTime.set(i, userTime.get(i));
            }
            DateTime lastMove = new DateTime(utcTime.getTime());
            //UTC/Zulu conversion done

            lastMove = lastMove.plusMinutes(game.getRoundLength());
            DateTime now = DateTime.now();

            Period period = new Period(now, lastMove, PeriodType.dayTime());
            PeriodFormatter formatter = new PeriodFormatterBuilder().appendDays().appendSuffix(",", ",").appendHours().appendSuffix(",", ",").appendMinutes().toFormatter();

            String[] timeSplit = formatter.print(period).split(",");
            String timeLeftString;
            if (timeSplit != null && !timeSplit[0].trim().equals("")) {
                timeLeftString = timeSplit[0];
                if (timeSplit.length == 3) {
                    timeLeftString += "d";
                } else if (timeSplit.length == 2) {
                    timeLeftString += "h";
                } else {
                    timeLeftString += "m";
                }
            } else {
                timeLeftString = "< 1";
            }
            return timeLeftString;
        }
    }

    private class L_ActiveGame implements OnClickListener {

        private String mGameId;

        public L_ActiveGame(String gameId) {
            mGameId = gameId;
        }

        @Override
        public void onClick(View v) {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            new AT_RequestGameState().execute(mGameId);
        }
    }
}
