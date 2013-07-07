package com.hansson.sh_shared.rr;

import java.util.List;

import com.hansson.sh_shared.entitys.Friend;

public class FriendResponse extends BasicResponse {

	private List<Friend> mFriends;

	public List<Friend> getFriends() {
		return mFriends;
	}

	public void setFriends(List<Friend> friends) {
		mFriends = friends;
	}
}
