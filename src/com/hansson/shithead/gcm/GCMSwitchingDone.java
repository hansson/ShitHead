package com.hansson.shithead.gcm;

public class GCMSwitchingDone extends GCMBaseMessage {

	public GCMSwitchingDone() {
		super(GCMTypes.GCM_SWITCHING_DONE);
	}

	public String getGameId() {
		return mGameId;
	}

	public void setGameId(String gameId) {
		mGameId = gameId;
	}

	private String mGameId;
}
