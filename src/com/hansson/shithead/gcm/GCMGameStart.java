package com.hansson.shithead.gcm;

public class GCMGameStart extends GCMBaseMessage {

	private String mGameId;

	public GCMGameStart() {
		super(GCMTypes.GCM_START);
	}

	public String getGameId() {
		return mGameId;
	}

	public void setGameId(String gameId) {
		mGameId = gameId;
	}
}
