package com.hansson.shithead.rr;

import com.hansson.shithead.entitys.Card;

import java.util.LinkedList;
import java.util.List;


public class MoveResponse extends BasicResponse {

	public enum Event {
		NONE, FOUR, EXPLODE, CHANCE, PILE
	}

	private String mNextPlayer;
	private Event mGameEvent;
	private List<Card> mNewCards;

	public MoveResponse(ResponseStatus status) {
		super(status);
	}

	public Event getGameEvent() {
		return mGameEvent;
	}

	public void setGameEvent(Event gameEvent) {
		mGameEvent = gameEvent;
	}

	public String getNextPlayer() {
		return mNextPlayer;
	}

	public void setNextPlayer(String nextPlayer) {
		mNextPlayer = nextPlayer;
	}

	public List<Card> getNewCards() {
		if (mNewCards == null) {
			mNewCards = new LinkedList<Card>();
		}
		return mNewCards;
	}

	public void setNewCards(List<Card> newCards) {
		mNewCards = newCards;
	}
}
