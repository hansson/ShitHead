/**
 * Copyright tobiashansson.nu <tobias@tobiashansson.nu>
 * This software may not be modified or reproduced without written
 * permission from the author. 
 */
package com.hansson.shithead.entitys;

public enum Suit {
	HEARTS(1), DIAMONDS(2), CLUBS(3), SPADES(4);

	private final int rank;

	Suit(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}
}
