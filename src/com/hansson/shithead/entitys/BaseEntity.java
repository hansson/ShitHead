package com.hansson.sh_shared.entitys;

public class BaseEntity {

	private String id;
	private boolean mLocked;
	public static final String ID = "id";
	public static final String LOCKED = "mLocked";

	public boolean isLocked() {
		return mLocked;
	}

	public void setLocked(boolean locked) {
		mLocked = locked;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
