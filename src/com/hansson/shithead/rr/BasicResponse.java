package com.hansson.sh_shared.rr;

public class BasicResponse {

	private ResponseStatus mStatus;

	public BasicResponse() {
	}

	public BasicResponse(ResponseStatus status) {
		setStatus(status);
	}

	public ResponseStatus getStatus() {
		return mStatus;
	}

	public void setStatus(ResponseStatus mStatus) {
		this.mStatus = mStatus;
	}

	
}
