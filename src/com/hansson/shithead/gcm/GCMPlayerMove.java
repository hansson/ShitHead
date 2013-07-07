package com.hansson.sh_shared.gcm;

import java.util.List;

import com.hansson.sh_shared.entitys.Card;
import com.hansson.sh_shared.rr.MoveResponse.Event;

public class GCMPlayerMove extends GCMBaseMessage {

	private String mGameId;
	private String mNextPlayer;
	private List<Card> mPlayerMove;
	private Event mGameEvent;
	private List<Card> mFaceUpCards;
	private int mHandCards;

	public GCMPlayerMove() {
		super(GCMTypes.GCM_PLAYER_MOVE);
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

	public List<Card> getPlayerMove() {
		return mPlayerMove;
	}

	public void setPlayerMove(List<Card> playerMove) {
		mPlayerMove = playerMove;
	}

	public Event getGameEvent() {
		return mGameEvent;
	}

	public void setGameEvent(Event gameEvent) {
		mGameEvent = gameEvent;
	}

	public List<Card> getFaceUpCards() {
		return mFaceUpCards;
	}

	public void setFaceUpCards(List<Card> faceUpCards) {
		mFaceUpCards = faceUpCards;
	}

	public int getHandCards() {
		return mHandCards;
	}

	public void setHandCards(int handCards) {
		mHandCards = handCards;
	}
}
