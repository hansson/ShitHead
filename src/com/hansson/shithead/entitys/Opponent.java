package com.hansson.sh_shared.entitys;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Opponent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5135099185206548666L;
	private String mUsername;
	private List<Card> mFaceUp;
	private int mOnHand;
	private int mFaceDown;
	private int mPosition;

	public int getFaceDown() {
		return mFaceDown;
	}

	public void setFaceDown(int faceDown) {
		mFaceDown = faceDown;
	}

	public int getOnHand() {
		return mOnHand;
	}

	public void setOnHand(int onHand) {
		mOnHand = onHand;
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

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public int getPosition() {
		return mPosition;
	}

	public void setPosition(int position) {
		mPosition = position;
	}
}
