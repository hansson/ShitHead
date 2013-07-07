package com.hansson.sh_shared.rr;

import java.util.List;

import com.hansson.sh_shared.entitys.Card;

public class MoveRequest extends BasicRequest {

	public enum MoveType {
		CHANCE, PILE, MOVE
	}

	private String mGameId;
	private List<Card> mCards;
	private MoveType mType;

	public MoveRequest(MoveType type) {
		setType(type);
	}

	public String getGameId() {
		return mGameId;
	}

	public void setGameId(String gameId) {
		mGameId = gameId;
	}

	public List<Card> getCards() {
		return mCards;
	}

	public void setCards(List<Card> cards) {
		mCards = cards;
	}

	public MoveType getType() {
		return mType;
	}

	public void setType(MoveType type) {
		mType = type;
	}
}
