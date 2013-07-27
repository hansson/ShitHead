package com.hansson.shithead.rr;

public class AuthResponse extends BasicResponse {

	private String mSessionId;

	public AuthResponse() {
		super();
	}

	public AuthResponse(ResponseStatus status, String sessionId) {
		super(status);
	}

	public String getSessionId() {
		return mSessionId;
	}

	public void setSessionId(String sessionId) {
		this.mSessionId = sessionId;
	}
}
