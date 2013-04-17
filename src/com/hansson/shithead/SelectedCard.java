package com.hansson.shithead;

import com.hansson.sh_shared.entitys.Card;

import android.widget.ImageView;

public class SelectedCard {

	private Card mCard;
	private ImageView mResource;

	public SelectedCard(Card card, ImageView resource) {
		mCard = card;
		mResource = resource;
	}

	public Card getCard() {
		return mCard;
	}

	public void setCard(Card card) {
		mCard = card;
	}

	public ImageView getResource() {
		return mResource;
	}

	public void setResource(ImageView resource) {
		mResource = resource;
	}

	@Override
	public boolean equals(Object o) {
		try {
			SelectedCard that = (SelectedCard) o;
			return this.mCard.equals(that.mCard);
		} catch (Exception e) {
			return false;
		}
	}
}
