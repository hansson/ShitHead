package com.hansson.shithead.rr;

import com.hansson.shithead.entitys.Card;
import com.hansson.shithead.entitys.Opponent;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GameStateResponse extends BasicResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4005932846718608453L;
	private String id;
	private List<Card> mHand;
	private int mFaceDown;
	private List<Card> mFaceUp;
	private List<Opponent> mOpponents;
	private String mCurrentPlayer;
	private int mRoundLength;
	private Date mLastUpdate;
	private boolean mSwitching;
	private boolean mPlayerSwitching;
	private Date mStartedAt;
	private List<Card> mPile;
	private int mDeckSize;
	private int mPosition;
	private boolean mFinished;

	public GameStateResponse() {
	}

	public GameStateResponse(ResponseStatus status) {
		super(status);
	}

	public List<Card> getHand() {
		if (mHand == null) {
			mHand = new LinkedList<Card>();
		}
		return mHand;
	}

	public void setHand(List<Card> hand) {
		mHand = hand;
	}

	public List<Card> getFaceUp() {
		if (mFaceUp == null) {
			mFaceUp = new LinkedList<Card>();
		}
		return mFaceUp;
	}

	public void setFaceUp(List<Card> faceUp) {
		mFaceUp = faceUp;
	}

	public List<Opponent> getOpponents() {
		if (mOpponents == null) {
			mOpponents = new LinkedList<Opponent>();
		}
		return mOpponents;
	}

	public void setOpponents(List<Opponent> opponents) {
		mOpponents = opponents;
	}

	public String getCurrentPlayer() {
		return mCurrentPlayer;
	}

	public void setCurrentPlayer(String currentPlayer) {
		mCurrentPlayer = currentPlayer;
	}

	public int getRoundLength() {
		return mRoundLength;
	}

	public void setRoundLength(int roundLength) {
		mRoundLength = roundLength;
	}

	public Date getLastUpdate() {
		return mLastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		mLastUpdate = lastUpdate;
	}

	public int getFaceDown() {
		return mFaceDown;
	}

	public void setFaceDown(int faceDown) {
		mFaceDown = faceDown;
	}

	public boolean isSwitching() {
		return mSwitching;
	}

	public void setSwitching(boolean switching) {
		mSwitching = switching;
	}

	public Date getStartedAt() {
		return mStartedAt;
	}

	public void setStartedAt(Date startedAt) {
		mStartedAt = startedAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPlayerSwitching(boolean switching) {
		mPlayerSwitching = switching;
	}

	public boolean isPlayerSwitching() {
		return mPlayerSwitching;
	}

	public void setDeckSize(int size) {
		mDeckSize = size;
	}

	public int getDeckSize() {
		return mDeckSize;
	}

	public void setPile(List<Card> pile) {
		mPile = pile;
	}

	public List<Card> getPile() {
		if (mPile == null) {
			mPile = new LinkedList<Card>();
		}
		return mPile;
	}

	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	public boolean isFinished() {
		return mFinished;
	}

	public void setFinished(boolean finished) {
		mFinished = finished;
	}
}
