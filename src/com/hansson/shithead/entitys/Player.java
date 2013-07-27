package com.hansson.shithead.entitys;

import java.util.LinkedList;
import java.util.List;

public class Player {

	private String mPlayerId;
	private String mUsername;
	private List<Card> mHand;
	private List<Card> mFaceDown;
	private List<Card> mFaceUp;
	private int mPosition = 0;
	private boolean mSwitching;

	public Player() {
		mSwitching = true;
	}

	public String getPlayerId() {
		return mPlayerId;
	}

	public void setPlayerId(String playerId) {
		mPlayerId = playerId;
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

	public List<Card> getFaceDown() {
		if (mFaceDown == null) {
			mFaceDown = new LinkedList<Card>();
		}
		return mFaceDown;
	}

	public void setFaceDown(List<Card> faceDown) {
		mFaceDown = faceDown;
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

	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			Player that = (Player) obj;
			if (that.mPlayerId.equals(this.mPlayerId)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public boolean isSwitching() {
		return mSwitching;
	}

	public void setSwitching(boolean switching) {
		mSwitching = switching;
	}
}
