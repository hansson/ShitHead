package com.hansson.sh_shared.entitys;

public class Friend {

	private String mUsername;
	private boolean mAccepted;
	private String mAvatar;

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public boolean isAccepted() {
		return mAccepted;
	}

	public void setAccepted(boolean accepted) {
		mAccepted = accepted;
	}

	public String getAvatar() {
		return mAvatar;
	}

	public void setAvatar(String avatar) {
		mAvatar = avatar;
	}
}
