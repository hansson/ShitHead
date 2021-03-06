package com.hansson.shithead.entitys;

public class Friend {

	private String mUsername;
	private String mAvatar;

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public String getAvatar() {
		return mAvatar;
	}

	public void setAvatar(String avatar) {
		mAvatar = avatar;
	}

    @Override
    public boolean equals(Object o) {
        if(o instanceof  Friend) {
            Friend that = (Friend) o;
            if(that.mUsername.equals(this.mUsername)) {
                return true;
            }
        }
        return false;
    }
}
