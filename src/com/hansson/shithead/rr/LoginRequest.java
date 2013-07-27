package com.hansson.shithead.rr;

public class LoginRequest {

	private String mUsername;
	private String mPassword;
	private String mRegId;
	private String mVersion;

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public String getRegId() {
		return mRegId;
	}

	public void setRegId(String regId) {
		mRegId = regId;
	}

	public String getVersion() {
		return mVersion;
	}

	public void setVersion(String version) {
		mVersion = version;
	}
}
