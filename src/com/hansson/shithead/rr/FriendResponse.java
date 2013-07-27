package com.hansson.shithead.rr;

import com.hansson.shithead.entitys.Friend;

import java.util.List;


public class FriendResponse extends BasicResponse {

	private List<Friend> mFriends;

	public List<Friend> getFriends() {
		return mFriends;
	}

	public void setFriends(List<Friend> friends) {
		mFriends = friends;
	}
}
