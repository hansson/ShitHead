package com.hansson.shithead.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

public class MenuActivity extends GCMActivity {

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

	public void quickGameListener(View v) {
		unregisterReceiver(mHandleMessageReceiver);
		mIsRegistered = false;
		Intent prefIntent = new Intent(this, QuickGameTypeActivity.class);
		this.startActivity(prefIntent);
	}

	public void customGameListener(View v) {
	}

	public void howToPlayListener(View v) {
		unregisterReceiver(mHandleMessageReceiver);
		mIsRegistered = false;
		Intent prefIntent = new Intent(this, HowToPlayActivity.class);
		this.startActivity(prefIntent);
	}

	public void friendsListener(View v) {
		unregisterReceiver(mHandleMessageReceiver);
		mIsRegistered = false;
		Intent prefIntent = new Intent(this, FriendsActivity.class);
		this.startActivity(prefIntent);
	}

	public void logoutListener(View v) {
		new AT_Logout().execute();
		unregisterReceiver(mHandleMessageReceiver);
		mIsRegistered = false;
		finish();
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
			} else if (fromJson.getGCMType() == GCMTypes.GCM_PLAYER_MOVE || fromJson.getGCMType() == GCMTypes.GCM_PLAYER_MOVE_FACE_DOWN
					|| fromJson.getGCMType() == GCMTypes.GCM_SWITCHING_DONE) {
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
			String sendAndRecieveGson = GsonOperator.sendAndRecieveGson(request, "game");
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
					GameStateResponse fromJson = gson.fromJson(result, GameStateResponse.class);
					if (fromJson.getStatus() == ResponseStatus.OK) {
						createGameBoard(fromJson);
					} else if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
						Toast.makeText(mContext, R.string.invalid_session, Toast.LENGTH_LONG).show();
					} else if (fromJson.getStatus() == ResponseStatus.INVALID_GAME) {
						Toast.makeText(mContext, R.string.invalid_game, Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
				Toast.makeText(mContext, R.string.terrible_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	private class AT_Logout extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			Editor editor = settings.edit();
			editor.putBoolean(Constants.PREF_AUTO_LOGIN, false);
			editor.commit();
			String session = settings.getString(Constants.PREF_SESSION, "");
			BasicRequest request = new BasicRequest();
			request.setSessionId(session);
			GsonOperator.sendAndRecieveGson(request, "auth/logout");
			return null;
		}
	}

	private class AT_RequestGames extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			BasicRequest request = new BasicRequest();
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
			String sendAndRecieveGson = GsonOperator.sendAndRecieveGson(request, "games");
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
					ActiveGamesResponse fromJson = gson.fromJson(result, ActiveGamesResponse.class);
					if (fromJson.getStatus() == ResponseStatus.OK) {
						LinearLayout container = (LinearLayout) findViewById(R.id.active_games);
						container.removeAllViews();
						for (ActiveGame game : fromJson.getGames()) {
							RelativeLayout inflate = (RelativeLayout) getLayoutInflater().inflate(R.layout.active_game, null);
							TextView startedAt = (TextView) inflate.findViewById(R.id.game_started);
							TextView lastMove = (TextView) inflate.findViewById(R.id.last_move);
							TextView currentPlayer = (TextView) inflate.findViewById(R.id.current_player);
							String startedAtString = java.text.DateFormat.getDateTimeInstance().format(game.getStartedAt());
							startedAt.setText(getString(R.string.started) + " " + startedAtString);
							String lastMoveString;
							if (game.getLastMove() != null) {
								lastMoveString = java.text.DateFormat.getDateTimeInstance().format(game.getLastMove());
							} else {
								lastMoveString = "-";
							}
							lastMove.setText(getString(R.string.last_move) + " " + lastMoveString);
							currentPlayer.setText(getString(R.string.current_player) + " " + game.getCurrentPlayerName());
							inflate.setOnClickListener(new L_ActiveGame(game.getGameId()));
							container.addView(inflate);
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
