package com.hansson.shithead.activities;

import android.os.Bundle;

import com.hansson.shithead.R;

public class HowToPlayActivity extends GCMActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.how_to_play);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void handleGCMMessage(String message) {
		// TODO Auto-generated method stub
	}
}
