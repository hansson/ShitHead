package com.hansson.sh_shared.rr;

public class FaceDownRequest extends BasicRequest {

	private String mGameId;
	private int mIndex;

	public String getGameId() {
		return mGameId;
	}

	public void setGameId(String gameId) {
		mGameId = gameId;
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		mIndex = index;
	}
}
