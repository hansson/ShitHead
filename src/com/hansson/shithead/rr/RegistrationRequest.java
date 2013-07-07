package com.hansson.shithead.rr;

public class RegistrationRequest {

	private String mUsername;
	private String mEmail;
	private String mPassword;
	private String mRegId;

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String email) {
		mEmail = email;
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
}
