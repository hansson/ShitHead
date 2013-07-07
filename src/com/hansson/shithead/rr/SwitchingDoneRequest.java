package com.hansson.sh_shared.rr;

import java.util.List;

import com.hansson.sh_shared.entitys.Card;

public class SwitchingDoneRequest {

	private String mGameId;
	private String mSessionId;
	private List<Card> mHand;
	private List<Card> mFaceUp;
	public String getGameId() {
		return mGameId;
	}
	public void setGameId(String gameId) {
		mGameId = gameId;
	}
	public String getSessionId() {
		return mSessionId;
	}
	public void setSessionId(String sessionId) {
		mSessionId = sessionId;
	}
	public List<Card> getHand() {
		return mHand;
	}
	public void setHand(List<Card> hand) {
		mHand = hand;
	}
	public List<Card> getFaceUp() {
		return mFaceUp;
	}
	public void setFaceUp(List<Card> faceUp) {
		mFaceUp = faceUp;
	}
}
