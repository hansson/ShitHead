package com.hansson.shithead.gcm;


import com.hansson.shithead.entitys.Card;
import com.hansson.shithead.rr.MoveResponse;

public class GCMPlayerMoveFaceDown extends GCMBaseMessage {

	private String mGameId;
	private String mNextPlayer;
	private Card mFaceDownCard;
	private MoveResponse.Event mGameEvent;

	public GCMPlayerMoveFaceDown() {
		super(GCMTypes.GCM_PLAYER_MOVE_FACE_DOWN);
	}

	public String getGameId() {
		return mGameId;
	}

	public void setGameId(String gameId) {
		mGameId = gameId;
	}

	public String getNextPlayer() {
		return mNextPlayer;
	}

	public void setNextPlayer(String nextPlayer) {
		mNextPlayer = nextPlayer;
	}

	public MoveResponse.Event getGameEvent() {
		return mGameEvent;
	}

	public void setGameEvent(MoveResponse.Event gameEvent) {
		mGameEvent = gameEvent;
	}

	public Card getFaceDownCard() {
		return mFaceDownCard;
	}

	public void setFaceDownCard(Card faceDownCard) {
		mFaceDownCard = faceDownCard;
	}
}
