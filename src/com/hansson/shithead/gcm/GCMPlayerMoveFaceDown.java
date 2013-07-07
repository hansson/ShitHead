package com.hansson.sh_shared.gcm;

import com.hansson.sh_shared.entitys.Card;
import com.hansson.sh_shared.rr.MoveResponse.Event;

public class GCMPlayerMoveFaceDown extends GCMBaseMessage {

	private String mGameId;
	private String mNextPlayer;
	private Card mFaceDownCard;
	private Event mGameEvent;

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

	public Event getGameEvent() {
		return mGameEvent;
	}

	public void setGameEvent(Event gameEvent) {
		mGameEvent = gameEvent;
	}

	public Card getFaceDownCard() {
		return mFaceDownCard;
	}

	public void setFaceDownCard(Card faceDownCard) {
		mFaceDownCard = faceDownCard;
	}
}
