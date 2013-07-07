package com.hansson.sh_shared.entitys;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class User extends BaseEntity {

	private String mEmail;
	private String mSessionId;
	private String mUsername;
	private String mPassword;
	private boolean mActive;
	private Date mCreated;
	private String mRegId;
	private List<String> mFriends = new LinkedList<String>();
	private List<String> mCurrentGames = new LinkedList<String>();
	private List<ArchivedGame> mFinishedGames = new LinkedList<ArchivedGame>();
	public static final String SESSIONID = "mSessionId";
	public static final String EMAIL = "mEmail";
	public static final String USERNAME = "mUsername";
	public static final String PASSWORD = "mPassword";
	public static final String ACTIVE = "mActive";
	public static final String CREATED = "mCreated";
	public static final String REG_ID = "mRegId";
	public static final String FRIENDS = "mFriends";
	public static final String CURRENT_GAMES = "mCurrentGames";

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String email) {
		mEmail = email;
	}

	public String getSesionId() {
		return mSessionId;
	}

	public void setSessionId(String sesionId) {
		mSessionId = sesionId;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	public boolean isActive() {
		return mActive;
	}

	public void setActive(boolean active) {
		mActive = active;
	}

	public Date getCreated() {
		return mCreated;
	}

	public void setCreated(Date created) {
		mCreated = created;
	}

	public String getRegId() {
		return mRegId;
	}

	public void setRegId(String regId) {
		this.mRegId = regId;
	}

	public List<String> getFriends() {
		return mFriends;
	}

	public void setFriends(List<String> friends) {
		mFriends = friends;
	}

	public List<String> getCurrentGames() {
		return mCurrentGames;
	}

	public void setCurrentGames(List<String> currentGames) {
		mCurrentGames = currentGames;
	}

	public List<ArchivedGame> getFinishedGames() {
		return mFinishedGames;
	}

	public void setFinishedGames(List<ArchivedGame> finishedGames) {
		mFinishedGames = finishedGames;
	}
}
