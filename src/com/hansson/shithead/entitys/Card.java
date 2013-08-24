package com.hansson.shithead.entitys;

import java.io.Serializable;

public class Card implements Comparable<Card>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4362768273313554273L;
	private int mValue;
	private int mSuit;

	public Card() {
	}

	public Card(int value, int suit) {
		mValue = value;
		mSuit = suit;
	}

	public int getValue() {
		return mValue;
	}

	public void setValue(int value) {
		mValue = value;
	}

	@Override
	public boolean equals(Object obj) {
        if(obj instanceof Card) {
			Card that = (Card) obj;
			if (that.mValue == this.mValue && that.mSuit == this.mSuit) {
				return true;
			}
        }
		return false;


	}

	@Override
	public String toString() {
		return mValue + "" + mSuit;
	}

	@Override
	public int compareTo(Card o) {
		if (o.mValue == mValue && o.mSuit == mSuit) {
			return 0;
		} else if (o.mValue > mValue || (o.mValue == mValue && mSuit > o.mSuit)) {
			return -1;
		} else {
			return 1;
		}
	}
}
