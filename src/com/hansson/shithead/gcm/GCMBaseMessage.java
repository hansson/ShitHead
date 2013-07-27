package com.hansson.shithead.gcm;

public class GCMBaseMessage {

	private GCMTypes mGCMType;

	public GCMBaseMessage(GCMTypes gcmType) {
		setGCMType(gcmType);
	}

	public GCMTypes getGCMType() {
		return mGCMType;
	}

	public void setGCMType(GCMTypes gCMType) {
		mGCMType = gCMType;
	}
}
