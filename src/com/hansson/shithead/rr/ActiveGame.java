package com.hansson.shithead.rr;

import java.util.Date;

public class ActiveGame {

	private Date mStartedAt;
	private String mGameId;
	private String mCurrentPlayerName;
	private Date mLastMove;

	public Date getStartedAt() {
		return mStartedAt;
	}

	public void setStartedAt(Date startedAt) {
		mStartedAt = startedAt;
	}

	public String getGameId() {
		return mGameId;
	}

	public void setGameId(String id) {
		mGameId = id;
	}

	public String getCurrentPlayerName() {
		return mCurrentPlayerName;
	}

	public void setCurrentPlayerName(String currentPlayerName) {
		mCurrentPlayerName = currentPlayerName;
	}

	public void setLastMove(Date lastUpdate) {
		mLastMove = lastUpdate;
	}

	public Date getLastMove() {
		return mLastMove;
	}
}
