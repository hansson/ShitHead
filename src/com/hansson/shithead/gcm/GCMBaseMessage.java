package com.hansson.shithead.gcm;

import com.hansson.shithead.gcm.GCMTypes;

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
