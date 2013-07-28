package com.hansson.shithead.rr;

import java.util.List;

public class InviteFriendRequest extends BasicRequest{
    private List<String> mFriends;
    private boolean mPrivateGame;

    public List<String> getFriends() {
        return mFriends;
    }

    public void setFriends(List<String> friends) {
        mFriends = friends;
    }

    public boolean isPrivateGame() {
        return mPrivateGame;
    }

    public void setPrivateGame(boolean privateGame) {
        mPrivateGame = privateGame;
    }
}
