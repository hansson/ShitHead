package com.hansson.shithead.rr;

import java.util.LinkedList;
import java.util.List;

public class ActiveGamesResponse extends BasicResponse {

	private List<ActiveGame> mGames;

	public ActiveGamesResponse(ResponseStatus status) {
		super(status);
	}

	public List<ActiveGame> getGames() {
		if (mGames == null) {
			mGames = new LinkedList<ActiveGame>();
		}
		return mGames;
	}

	public void setGames(List<ActiveGame> games) {
		mGames = games;
	}
}
