package com.hansson.shithead.gcm;

import com.hansson.shithead.entitys.Card;
import com.hansson.shithead.rr.MoveResponse;

import java.util.List;

public class GCMPlayerMove extends GCMBaseMessage {

	private String mGameId;
	private String mNextPlayer;
	private List<Card> mPlayerMove;
	private MoveResponse.Event mGameEvent;
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

	public MoveResponse.Event getGameEvent() {
		return mGameEvent;
	}

	public void setGameEvent(MoveResponse.Event gameEvent) {
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
