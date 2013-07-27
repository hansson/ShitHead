package com.hansson.shithead.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hansson.shithead.R;
import com.hansson.shithead.SelectedCard;
import com.hansson.shithead.entitys.Card;
import com.hansson.shithead.entitys.Opponent;
import com.hansson.shithead.gcm.GCMBaseMessage;
import com.hansson.shithead.gcm.GCMPlayerMove;
import com.hansson.shithead.gcm.GCMPlayerMoveFaceDown;
import com.hansson.shithead.gcm.GCMSwitchingDone;
import com.hansson.shithead.gcm.GCMTypes;
import com.hansson.shithead.rr.BasicResponse;
import com.hansson.shithead.rr.FaceDownRequest;
import com.hansson.shithead.rr.GameStateRequest;
import com.hansson.shithead.rr.GameStateResponse;
import com.hansson.shithead.rr.MoveRequest;
import com.hansson.shithead.rr.MoveResponse;
import com.hansson.shithead.rr.ResponseStatus;
import com.hansson.shithead.rr.SwitchingDoneRequest;
import com.hansson.shithead.util.Constants;
import com.hansson.shithead.util.GsonOperator;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameActivity extends GCMActivity {

	private GameStateResponse mGame;
	private SelectedCard mSwitchHand;
	private SelectedCard mSwitchFaceUp;
	private List<Card> mPlay = new LinkedList<Card>();
	private ImageView mTurnChip = null;
	private ImageView[] mRibbons = null;
	private int mRibbonDrawables[] = { R.drawable.ribbon_1, R.drawable.ribbon_2, R.drawable.ribbon_3 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		mGame = (GameStateResponse) getIntent().getSerializableExtra("com.hansson.shithead.LoadGame");
		initializeGameBoard();
	}

	private void initializeGameBoard() {
		// Initialize player cards
		HorizontalScrollView container = (HorizontalScrollView) findViewById(R.id.hand_container);
		LinearLayout hand = (LinearLayout) container.getChildAt(0);
		hand.removeAllViews();
		Collections.sort(mGame.getHand());
		for (Card card : mGame.getHand()) {
			ImageView image = new ImageView(mContext);
			image.setImageResource(Constants.CARD_MAP.get(card.toString()));
			image.setOnClickListener(new L_HandCardClick(card));
			image.setPadding(0, 0, 1, 0);
			hand.addView(image);
		}
		LinearLayout faceUpFaceDown = (LinearLayout) findViewById(R.id.face_up_down_container);
		faceUpFaceDown.removeAllViews();
		for (Card card : mGame.getFaceUp()) {
			ImageView image = new ImageView(mContext);
			image.setImageResource(Constants.CARD_MAP.get(card.toString()));
			image.setOnClickListener(new L_FaceUpCardClick(card));
			image.setPadding(0, 0, 1, 0);
			faceUpFaceDown.addView(image);
		}
		for (int i = mGame.getFaceUp().size(); i < mGame.getFaceDown(); i++) {
			ImageView image = new ImageView(mContext);
			image.setImageResource(R.drawable.back_card);
			image.setOnClickListener(new L_FaceDownCardClick(i));
			image.setPadding(0, 0, 1, 0);
			faceUpFaceDown.addView(image);
		}
		// Create 1st, 2nd, 3rd place ribbons if they does not already exist
		if (mRibbons == null) {
			mRibbons = new ImageView[3];
			for (int i = 0; i < 3; i++) {
				mRibbons[i] = new ImageView(mContext);
				mRibbons[i].setImageResource(mRibbonDrawables[i]);
				mRibbons[i].setVisibility(View.INVISIBLE);
			}
		}
		// If player should have a ribbon, show it!
		if (mGame.getPosition() != 0) {
			faceUpFaceDown.addView(mRibbons[mGame.getPosition() - 1]);
			mRibbons[mGame.getPosition() - 1].setVisibility(View.VISIBLE);
		}
		// Initiate opponents
		List<Opponent> opponents = mGame.getOpponents();
		for (int j = 0; j < opponents.size(); j++) {
			// Init
			Map<String, Integer> cardMap = getCardMap(j);
			int[] handPadding = getHandPadding(j);
			int[] cardPadding = getCardPadding(j);
			int backCardId = getBackCardId(j);
			// Processing
			// Get current opponent
			Opponent opponent = opponents.get(j);
			LinearLayout opponentContainer = (LinearLayout) findViewById(getContainerId(j));
			// Clear views
			opponentContainer.removeAllViews();
			// Get hand container and add text with number of cards on hand
			RelativeLayout opponentHand = (RelativeLayout) getLayoutInflater().inflate(R.layout.opponent_hand, null);
			opponentHand.setPadding(handPadding[0], handPadding[1], handPadding[2], handPadding[3]);
			ImageView opponentFaceDownCards = (ImageView) opponentHand.getChildAt(0);
			opponentFaceDownCards.setImageResource(backCardId);
			TextView text = (TextView) opponentHand.getChildAt(1);
			text.setText("" + opponent.getOnHand());
			opponentContainer.addView(opponentHand);
			// Show opponent face up cards
			for (Card card : opponent.getFaceUp()) {
				ImageView opponentFaceDownCard = new ImageView(mContext);
				opponentFaceDownCard.setImageResource(cardMap.get(card.toString()));
				opponentFaceDownCard.setPadding(cardPadding[0], cardPadding[1], cardPadding[2], cardPadding[3]);
				opponentContainer.addView(opponentFaceDownCard);
			}
			// If face up cards is less than the three you started with show some face down cards
			for (int i = opponent.getFaceUp().size(); i < opponent.getFaceDown(); i++) {
				ImageView opponentFaceUpCard = new ImageView(mContext);
				opponentFaceUpCard.setImageResource(backCardId);
				opponentFaceUpCard.setPadding(cardPadding[0], cardPadding[1], cardPadding[2], cardPadding[3]);
				opponentContainer.addView(opponentFaceUpCard);
			}
			// Draw opponent name
			int size = 40; // TODO calculate size
			int[] nameImageValues = getNameImageValues(j, opponents.get(j).getUsername().length(), size);
			ImageView opponentName = (ImageView) findViewById(getNameId(j));
			Bitmap bitmap = Bitmap.createBitmap(nameImageValues[0], nameImageValues[1], Bitmap.Config.ARGB_8888);
			Paint paint = new Paint();
			paint.setTextSize(size);
			Canvas canvas = new Canvas(bitmap);
			canvas.rotate(nameImageValues[2]);
			canvas.drawText(opponent.getUsername(), nameImageValues[3], nameImageValues[4], paint);
			opponentName.setImageBitmap(bitmap);
			// If opponent should have a ribbon, show it!
			if (opponent.getPosition() != 0) {
				opponentContainer.addView(mRibbons[opponent.getPosition() - 1]);
				mRibbons[opponent.getPosition() - 1].setVisibility(View.VISIBLE);
			}
		}
		// Initiate Pile and deck
		ImageView pile = (ImageView) findViewById(R.id.pile);
		if (mGame.getPile().size() == 0) {
			pile.setImageResource(R.drawable.placeholder_1_card);
		} else {
			Card card = mGame.getPile().get(mGame.getPile().size() - 1);
			pile.setImageResource(Constants.CARD_MAP.get(card.toString()));
		}
		ImageView deck = (ImageView) findViewById(R.id.deck);
		if (mGame.getDeckSize() == 0) {
			deck.setImageResource(R.drawable.placeholder_1_card);
		} else {
			deck.setImageResource(R.drawable.back_card);
		}
		// Place turn chip
		placeTurnChip(opponents);
		// Initiate button mode Playing/Switching
		if (mGame.isSwitching()) {
			Button done = (Button) findViewById(R.id.button1);
			done.setText(R.string.done);
			done.setOnClickListener(new L_DoneSwitching());
			Button none = (Button) findViewById(R.id.button2);
			none.setVisibility(View.INVISIBLE);
			Button switchCards = (Button) findViewById(R.id.button3);
			switchCards.setText(R.string.switch_card);
			switchCards.setOnClickListener(new L_SwitchCards());
			if (!mGame.isPlayerSwitching()) {
				done.setEnabled(false);
				switchCards.setEnabled(false);
			}
		} else {
			Button play = (Button) findViewById(R.id.button1);
			play.setEnabled(true);
			play.setText(R.string.play);
			play.setOnClickListener(new L_Play());
			Button chance = (Button) findViewById(R.id.button2);
			chance.setVisibility(View.VISIBLE);
			chance.setOnClickListener(new L_Chance());
			chance.setText(R.string.chance);
			Button pileButton = (Button) findViewById(R.id.button3);
			pileButton.setEnabled(true);
			pileButton.setText(R.string.pile);
			pileButton.setOnClickListener(new L_Pile());
		}
	}

	private void placeTurnChip(List<Opponent> opponents) {
		SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
		String username = settings.getString(Constants.PREF_USERNAME, "");
		RelativeLayout gameboard = (RelativeLayout) findViewById(R.id.gameboard);
		if (mTurnChip == null) {
			mTurnChip = new ImageView(mContext);
			mTurnChip.setImageResource(R.drawable.turn_chip);
		} else {
			gameboard.removeView(mTurnChip);
		}
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		if (mGame.getCurrentPlayer().equals(username)) {
			p.addRule(RelativeLayout.RIGHT_OF, R.id.face_up_down_container);
			p.addRule(RelativeLayout.ABOVE, R.id.hand_container);
			mTurnChip.setLayoutParams(p);
			mTurnChip.setPadding(15, 0, 0, 15);
			mTurnChip.setVisibility(View.VISIBLE);
		} else if (mGame.getCurrentPlayer().equals(opponents.get(0).getUsername())) {
			p.addRule(RelativeLayout.BELOW, R.id.op_left_container_holder);
			mTurnChip.setLayoutParams(p);
			mTurnChip.setPadding(10, 10, 0, 0);
			mTurnChip.setVisibility(View.VISIBLE);
		} else if (mGame.getCurrentPlayer().equals(opponents.get(1).getUsername())) {
			p.addRule(RelativeLayout.RIGHT_OF, R.id.op_top_container);
			mTurnChip.setLayoutParams(p);
			mTurnChip.setPadding(10, 10, 0, 0);
			mTurnChip.setVisibility(View.VISIBLE);
		} else if (mGame.getCurrentPlayer().equals(opponents.get(2).getUsername())) {
			p.addRule(RelativeLayout.BELOW, R.id.op_right_container_holder);
			p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			mTurnChip.setLayoutParams(p);
			mTurnChip.setPadding(0, 10, 10, 0);
			mTurnChip.setVisibility(View.VISIBLE);
		} else {
			mTurnChip.setVisibility(View.INVISIBLE);
		}
		gameboard.addView(mTurnChip);
	}

	@Override
	protected void handleGCMMessage(String message) {
		System.out.println(message);
		if (message.contains("{")) {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
			GCMBaseMessage fromJson = gson.fromJson(message, GCMBaseMessage.class);
			if (fromJson.getGCMType().equals(GCMTypes.GCM_SWITCHING_DONE)) {
				GCMSwitchingDone gcmMessage = gson.fromJson(message, GCMSwitchingDone.class);
				if (gcmMessage.getGameId().equals(mGame.getId())) {
					new AT_UpdateGameState().execute(mGame.getId());
				}
			} else if (fromJson.getGCMType() == GCMTypes.GCM_PLAYER_MOVE) {
				GCMPlayerMove gcmMessage = gson.fromJson(message, GCMPlayerMove.class);
				if (gcmMessage.getGameId().equals(mGame.getId())) {
					for (Opponent opponent : mGame.getOpponents()) {
						if (opponent.getUsername().equals(mGame.getCurrentPlayer())) {
							if (gcmMessage.getGameEvent() == MoveResponse.Event.EXPLODE || gcmMessage.getGameEvent() == MoveResponse.Event.FOUR) {
								mGame.getPile().clear();
							} else if (gcmMessage.getGameEvent() == MoveResponse.Event.NONE) {
								mGame.getPile().addAll(gcmMessage.getPlayerMove());
							} else if (gcmMessage.getGameEvent() == MoveResponse.Event.PILE) {
								opponent.setOnHand(mGame.getPile().size() + 1);
								mGame.getPile().clear();
							}
							opponent.setFaceUp(gcmMessage.getFaceUpCards());
							opponent.setOnHand(gcmMessage.getHandCards());
							mGame.setCurrentPlayer(gcmMessage.getNextPlayer());
							break;
						}
					}
					initializeGameBoard();
				}
			} else if (fromJson.getGCMType() == GCMTypes.GCM_PLAYER_MOVE_FACE_DOWN) {
				GCMPlayerMoveFaceDown gcmMessage = gson.fromJson(message, GCMPlayerMoveFaceDown.class);
				if (gcmMessage.getGameId().equals(mGame.getId())) {
					for (Opponent opponent : mGame.getOpponents()) {
						if (opponent.getUsername().equals(mGame.getCurrentPlayer())) {
							opponent.setFaceDown(opponent.getFaceDown() - 1);
							if (gcmMessage.getGameEvent() == MoveResponse.Event.EXPLODE || gcmMessage.getGameEvent() == MoveResponse.Event.FOUR) {
								mGame.getPile().clear();
							} else if (gcmMessage.getGameEvent() == MoveResponse.Event.NONE) {
								mGame.getPile().add(gcmMessage.getFaceDownCard());
							} else if (gcmMessage.getGameEvent() == MoveResponse.Event.PILE) {
								opponent.setOnHand(mGame.getPile().size() + 1);
								mGame.getPile().clear();
							} else if (gcmMessage.getGameEvent() == MoveResponse.Event.CHANCE) {
								mGame.setDeckSize(mGame.getDeckSize() - 1);
							}
							mGame.setCurrentPlayer(gcmMessage.getNextPlayer());
							break;
						}
					}
					initializeGameBoard();
				}
			}
		}
	}

	private int[] getNameImageValues(int playerIndex, int usernameLength, int textSize) {
		// width, height, rotate, x, y
		switch (playerIndex) {
			case 0:
				int[] leftNameValues = { 30, (int) ((textSize / 2) * usernameLength), -90, -(int) ((textSize / 2) * usernameLength), 30 };
				return leftNameValues;
			case 1:
				int[] topNameValues = { (int) ((textSize / 2) * usernameLength), 30, 0, 0, 30 };
				return topNameValues;
			case 2:
				int[] rightNameValues = { 30, (int) ((textSize / 2) * usernameLength), 90, 0, 0 };
				return rightNameValues;
			default:
				int[] defaultNameValues = { 0, 0, 0, 0, 0 };
				return defaultNameValues;
		}
	}

	private int[] getCardPadding(int playerIndex) {
		switch (playerIndex) {
			case 0:
				int[] leftPadding = { 0, 0, 0, 1 };
				return leftPadding;
			case 1:
				int[] topPadding = { 0, 0, 1, 0 };
				return topPadding;
			case 2:
				int[] rightPadding = { 0, 0, 0, 1 };
				return rightPadding;
			default:
				int[] defaultPadding = { 0, 0, 0, 0 };
				return defaultPadding;
		}
	}

	private int[] getHandPadding(int playerIndex) {
		switch (playerIndex) {
			case 0:
				int[] leftPadding = { 0, 0, 0, 10 };
				return leftPadding;
			case 1:
				int[] topPadding = { 0, 0, 10, 0 };
				return topPadding;
			case 2:
				int[] rightPadding = { 0, 0, 0, 10 };
				return rightPadding;
			default:
				int[] defaultPadding = { 0, 0, 0, 0 };
				return defaultPadding;
		}
	}

	private int getNameId(int playerIndex) {
		switch (playerIndex) {
			case 0:
				return R.id.op_left_name;
			case 1:
				return R.id.op_top_name;
			case 2:
				return R.id.op_right_name;
			default:
				return -1;
		}
	}

	private Map<String, Integer> getCardMap(int playerIndex) {
		switch (playerIndex) {
			case 0:
				return Constants.HORIZONTAL_OPPONENT_CARD_MAP;
			case 1:
				return Constants.VERTICAL_OPPONENT_CARD_MAP;
			case 2:
				return Constants.HORIZONTAL_OPPONENT_CARD_MAP;
			default:
				return new HashMap<String, Integer>();
		}
	}

	private int getBackCardId(int playerIndex) {
		switch (playerIndex) {
			case 0:
				return R.drawable.hor_op_back_card;
			case 1:
				return R.drawable.op_back_card;
			case 2:
				return R.drawable.hor_op_back_card;
			default:
				return -1;
		}
	}

	private int getContainerId(int playerIndex) {
		switch (playerIndex) {
			case 0:
				return R.id.op_left_container;
			case 1:
				return R.id.op_top_container;
			case 2:
				return R.id.op_right_container;
			default:
				return -1;
		}
	}

	private class L_Pile implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!mGame.isFinished() && mGame.getPile().size() != 0) {
				new AT_TakePile().execute();
			} else {
				Toast.makeText(mContext, R.string.not_pile, Toast.LENGTH_LONG).show();
			}
		}
	}

	private class L_Chance implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!mGame.isFinished() && checkTakeChance()) {
				new AT_Chance().execute();
			} else {
				Toast.makeText(mContext, R.string.not_chance, Toast.LENGTH_LONG).show();
			}
		}
	}

	private class L_Play implements OnClickListener {

		@Override
		public void onClick(View v) {
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			String username = settings.getString(Constants.PREF_USERNAME, "");
			if (!mGame.isFinished() && !mGame.isSwitching() && mGame.getCurrentPlayer().equalsIgnoreCase(username)) {
				if (moveIsValid()) {
					new AT_SendMove().execute();
				} else {
					Toast.makeText(mContext, R.string.invalid_move, Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(mContext, R.string.not_your_turn, Toast.LENGTH_LONG).show();
			}
		}

		private boolean moveIsValid() {
			boolean valid = false;
			int value = 0;
			for (Card card : mPlay) {
				if (value == 0) {
					value = card.getValue();
				}
				// If the same value on every card and card value is same or higher than pile top, or pile is empty. Or is 2 or 10
				if (value == card.getValue()
						&& (card.getValue() == 10 || card.getValue() == 2 || mGame.getPile().size() == 0 || card.getValue() >= mGame.getPile().get(
								mGame.getPile().size() - 1).getValue())) {
					valid = true;
				} else {
					valid = false;
					break;
				}
			}
			return valid;
		}
	}

	private class L_SwitchCards implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!mGame.isFinished() && mGame.isSwitching() && mSwitchFaceUp != null && mSwitchHand != null) {
				// GUI Switching
				mSwitchHand.getResource().setImageResource(Constants.CARD_MAP.get(mSwitchFaceUp.getCard().toString()));
				mSwitchHand.getResource().setOnClickListener(new L_HandCardClick(mSwitchFaceUp.getCard()));
				mSwitchHand.getResource().setColorFilter(Color.BLUE, PorterDuff.Mode.DST_ATOP);
				mSwitchFaceUp.getResource().setImageResource(Constants.CARD_MAP.get(mSwitchHand.getCard().toString()));
				mSwitchFaceUp.getResource().setOnClickListener(new L_FaceUpCardClick(mSwitchHand.getCard()));
				mSwitchFaceUp.getResource().setColorFilter(Color.BLUE, PorterDuff.Mode.DST_ATOP);
				// GameState Switching
				int indexOfHand = mGame.getHand().indexOf(mSwitchHand.getCard());
				int indexOfFaceUp = mGame.getFaceUp().indexOf(mSwitchFaceUp.getCard());
				mGame.getFaceUp().add(indexOfFaceUp, mGame.getHand().get(indexOfHand));
				mGame.getHand().add(indexOfHand, mGame.getFaceUp().get(indexOfFaceUp + 1));
				mGame.getHand().remove(mSwitchHand.getCard());
				mGame.getFaceUp().remove(mSwitchFaceUp.getCard());
				mSwitchFaceUp = null;
				mSwitchFaceUp = null;
			}
		}
	}

	private class L_DoneSwitching implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (mGame.isSwitching()) {
				findViewById(R.id.progress).setVisibility(View.VISIBLE);
				new AT_SendSwitchDone().execute();
			}
		}
	}

	private class L_FaceDownCardClick implements OnClickListener {

		private int mIndex;

		public L_FaceDownCardClick(int i) {
			mIndex = i;
		}

		@Override
		public void onClick(View v) {
			if (mGame.getHand().size() == 0 && mGame.getFaceUp().size() == 0) {
				new AT_SendFaceDown().execute(mIndex);
			}
		}
	}

	private class L_FaceUpCardClick implements OnClickListener {

		private Card mCard;

		public L_FaceUpCardClick(Card card) {
			mCard = card;
		}

		@Override
		public void onClick(View v) {
			// Cast view to image view
			ImageView thisImage = (ImageView) v;
			// If game is switching only one card can be chosen from hand and one from head up
			if (mGame.isSwitching()) {
				LinearLayout findViewById = (LinearLayout) findViewById(R.id.face_up_down_container);
				// Iterate all cards and set them to not selected
				for (int i = 0; i < findViewById.getChildCount(); i++) {
					ImageView childAt = (ImageView) findViewById.getChildAt(i);
					childAt.setColorFilter(Color.BLUE, PorterDuff.Mode.DST_ATOP);
				}
				thisImage.setColorFilter(Color.BLUE, PorterDuff.Mode.SCREEN);
				mSwitchFaceUp = new SelectedCard(mCard, thisImage);
			} else if (mGame.getHand().size() == 0) {
				// Toggle selected/not selected
				if (mPlay.remove(mCard)) {
					thisImage.setColorFilter(Color.BLUE, PorterDuff.Mode.DST_ATOP);
				} else {
					thisImage.setColorFilter(Color.BLUE, PorterDuff.Mode.SCREEN);
					mPlay.add(mCard);
				}
			}
		}
	}

	private class L_HandCardClick implements OnClickListener {

		private Card mCard;

		public L_HandCardClick(Card card) {
			mCard = card;
		}

		@Override
		public void onClick(View v) {
			// Cast view to image view
			ImageView thisImage = (ImageView) v;
			// If game is switching only one card can be chosen from hand and one from head up
			if (mGame.isSwitching()) {
				HorizontalScrollView findViewById = (HorizontalScrollView) findViewById(R.id.hand_container);
				LinearLayout hand = (LinearLayout) findViewById.getChildAt(0);
				// Iterate all cards and set them to not selected
				for (int i = 0; i < hand.getChildCount(); i++) {
					ImageView childAt = (ImageView) hand.getChildAt(i);
					childAt.setColorFilter(Color.BLUE, PorterDuff.Mode.DST_ATOP);
				}
				// Set the current card to selected
				thisImage.setColorFilter(Color.BLUE, PorterDuff.Mode.SCREEN);
				mSwitchHand = new SelectedCard(mCard, thisImage);
			} else {
				// Toggle selected/not selected
				if (mPlay.remove(mCard)) {
					thisImage.setColorFilter(Color.BLUE, PorterDuff.Mode.DST_ATOP);
				} else {
					thisImage.setColorFilter(Color.BLUE, PorterDuff.Mode.SCREEN);
					mPlay.add(mCard);
				}
			}
		}
	}

	private class AT_SendFaceDown extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			FaceDownRequest request = new FaceDownRequest();
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
			request.setGameId(mGame.getId());
			request.setIndex(params[0]);
			String sendAndRecieveGson = GsonOperator.sendAndRecieveGson(request, "move/face-down");
			return sendAndRecieveGson;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				findViewById(R.id.progress).setVisibility(View.GONE);
				if (result.equals(Constants.CONNECTION_ERROR)) {
					Toast.makeText(mContext, R.string.connection_error, Toast.LENGTH_LONG).show();
				} else {
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
					MoveResponse response = gson.fromJson(result, MoveResponse.class);
					if (response.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
						Toast.makeText(mContext, R.string.invalid_session, Toast.LENGTH_LONG).show();
					} else if (response.getStatus() == ResponseStatus.INVALID_GAME) {
						Toast.makeText(mContext, R.string.invalid_game, Toast.LENGTH_LONG).show();
					} else if (response.getStatus() == ResponseStatus.OK) {
						mGame.setFaceDown(mGame.getFaceDown() - 1);
						mPlay.clear();
						mGame.setCurrentPlayer(response.getNextPlayer());
						if (response.getGameEvent() == MoveResponse.Event.NONE) {
							mGame.getPile().addAll(response.getNewCards());
						} else if (response.getGameEvent() == MoveResponse.Event.PILE) {
							mGame.getPile().clear();
							mGame.getHand().addAll(response.getNewCards());
							// getNewCards[0] should be the face down card
						} else {
							mGame.getPile().clear();
						}
						initializeGameBoard();
						if (mGame.getHand().size() == 0 && mGame.getFaceUp().size() == 0 && mGame.getFaceDown() == 0) {
							new AT_UpdateGameState().execute(mGame.getId());
						}
					} else if (response.getStatus() == ResponseStatus.NOT_OK) {
						Toast.makeText(mContext, R.string.invalid_move, Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
				Toast.makeText(mContext, R.string.terrible_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	private class AT_Chance extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			MoveRequest request = new MoveRequest(MoveRequest.MoveType.CHANCE);
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
			request.setGameId(mGame.getId());
			String sendAndRecieveGson = GsonOperator.sendAndRecieveGson(request, "move");
			return sendAndRecieveGson;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				findViewById(R.id.progress).setVisibility(View.GONE);
				if (result.equals(Constants.CONNECTION_ERROR)) {
					Toast.makeText(mContext, R.string.connection_error, Toast.LENGTH_LONG).show();
				} else {
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
					MoveResponse response = gson.fromJson(result, MoveResponse.class);
					if (response.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
						Toast.makeText(mContext, R.string.invalid_session, Toast.LENGTH_LONG).show();
					} else if (response.getStatus() == ResponseStatus.INVALID_GAME) {
						Toast.makeText(mContext, R.string.invalid_game, Toast.LENGTH_LONG).show();
					} else if (response.getStatus() == ResponseStatus.OK) {
						mGame.getPile().addAll(mPlay);
						mPlay.clear();
						if (response.getNewCards() != null) {
							mGame.getHand().addAll(response.getNewCards());
						}
						initializeGameBoard();
					} else if (response.getStatus() == ResponseStatus.CHANCE_TAKEN) {
						Toast.makeText(mContext, R.string.invalid_chance, Toast.LENGTH_LONG).show();
					} else if (response.getStatus() == ResponseStatus.NOT_OK) {
						Toast.makeText(mContext, R.string.invalid_move, Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
				Toast.makeText(mContext, R.string.terrible_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	private class AT_TakePile extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			MoveRequest request = new MoveRequest(MoveRequest.MoveType.PILE);
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
			request.setGameId(mGame.getId());
			String sendAndRecieveGson = GsonOperator.sendAndRecieveGson(request, "move");
			return sendAndRecieveGson;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				findViewById(R.id.progress).setVisibility(View.GONE);
				if (result.equals(Constants.CONNECTION_ERROR)) {
					Toast.makeText(mContext, R.string.connection_error, Toast.LENGTH_LONG).show();
				} else {
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
					MoveResponse response = gson.fromJson(result, MoveResponse.class);
					if (response.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
						Toast.makeText(mContext, R.string.invalid_session, Toast.LENGTH_LONG).show();
					} else if (response.getStatus() == ResponseStatus.INVALID_GAME) {
						Toast.makeText(mContext, R.string.invalid_game, Toast.LENGTH_LONG).show();
					} else if (response.getStatus() == ResponseStatus.OK) {
						mGame.getPile().clear();
						mPlay.clear();
						mGame.setCurrentPlayer(response.getNextPlayer());
						mGame.getHand().addAll(response.getNewCards());
						initializeGameBoard();
					} else if (response.getStatus() == ResponseStatus.NOT_OK) {
						Toast.makeText(mContext, R.string.invalid_move, Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
				Toast.makeText(mContext, R.string.terrible_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	private class AT_SendMove extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			MoveRequest request = new MoveRequest(MoveRequest.MoveType.MOVE);
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
			request.setGameId(mGame.getId());
			request.setCards(mPlay);
			String sendAndRecieveGson = GsonOperator.sendAndRecieveGson(request, "move");
			return sendAndRecieveGson;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				findViewById(R.id.progress).setVisibility(View.GONE);
				if (result.equals(Constants.CONNECTION_ERROR)) {
					Toast.makeText(mContext, R.string.connection_error, Toast.LENGTH_LONG).show();
				} else {
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
					BasicResponse fromJson = gson.fromJson(result, BasicResponse.class);
					if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
						Toast.makeText(mContext, R.string.invalid_session, Toast.LENGTH_LONG).show();
					} else if (fromJson.getStatus() == ResponseStatus.INVALID_GAME) {
						Toast.makeText(mContext, R.string.invalid_game, Toast.LENGTH_LONG).show();
					} else if (fromJson.getStatus() == ResponseStatus.OK) {
						MoveResponse response = gson.fromJson(result, MoveResponse.class);
						if (response.getGameEvent() == MoveResponse.Event.NONE) {
							handleMoveNoEvent(response);
						} else if (response.getGameEvent() == MoveResponse.Event.FOUR || response.getGameEvent() == MoveResponse.Event.EXPLODE) {
							handleMoveFourOrExplode(response);
						}
						if (mGame.getHand().size() == 0 && mGame.getFaceUp().size() == 0 && mGame.getFaceDown() == 0) {
							new AT_UpdateGameState().execute(mGame.getId());
						}
					} else if (fromJson.getStatus() == ResponseStatus.NOT_OK) {
						Toast.makeText(mContext, R.string.invalid_move, Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
				Toast.makeText(mContext, R.string.terrible_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}

		private void handleMoveFourOrExplode(MoveResponse response) {
			mGame.getPile().clear();
			mGame.getHand().removeAll(mPlay);
			mGame.getFaceUp().removeAll(mPlay);
			mPlay.clear();
			if (response.getNewCards() != null) {
				mGame.getHand().addAll(response.getNewCards());
			}
			initializeGameBoard();
		}

		private void handleMoveNoEvent(MoveResponse response) {
			mGame.getPile().addAll(mPlay);
			mGame.getHand().removeAll(mPlay);
			mGame.getFaceUp().removeAll(mPlay);
			mPlay.clear();
			mGame.setCurrentPlayer(response.getNextPlayer());
			if (response.getNewCards() != null) {
				mGame.getHand().addAll(response.getNewCards());
			}
			initializeGameBoard();
		}
	}

	private class AT_SendSwitchDone extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			SwitchingDoneRequest request = new SwitchingDoneRequest();
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
			request.setGameId(mGame.getId());
			request.setFaceUp(mGame.getFaceUp());
			request.setHand(mGame.getHand());
			String sendAndRecieveGson = GsonOperator.sendAndRecieveGson(request, "switch");
			return sendAndRecieveGson;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				findViewById(R.id.progress).setVisibility(View.GONE);
				if (result.equals(Constants.CONNECTION_ERROR)) {
					Toast.makeText(mContext, R.string.connection_error, Toast.LENGTH_LONG).show();
				} else {
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
					BasicResponse fromJson = gson.fromJson(result, BasicResponse.class);
					if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
						Toast.makeText(mContext, R.string.invalid_session, Toast.LENGTH_LONG).show();
					} else if (fromJson.getStatus() == ResponseStatus.INVALID_GAME) {
						Toast.makeText(mContext, R.string.invalid_game, Toast.LENGTH_LONG).show();
					} else if (fromJson.getStatus() == ResponseStatus.OK) {
						findViewById(R.id.button1).setEnabled(false);
						findViewById(R.id.button3).setEnabled(false);
					} else if (fromJson.getStatus() == ResponseStatus.NOT_OK) {
						Toast.makeText(mContext, R.string.error_switching, Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
				Toast.makeText(mContext, R.string.terrible_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	private class AT_UpdateGameState extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			GameStateRequest request = new GameStateRequest();
			request.setGameId(params[0]);
			SharedPreferences settings = getSharedPreferences(Constants.PREF_TAG, 0);
			request.setSessionId(settings.getString(Constants.PREF_SESSION, ""));
			String sendAndRecieveGson = GsonOperator.sendAndRecieveGson(request, "game");
			return sendAndRecieveGson;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.equals(Constants.CONNECTION_ERROR)) {
					Toast.makeText(mContext, R.string.connection_error, Toast.LENGTH_LONG).show();
				} else {
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
					GameStateResponse fromJson = gson.fromJson(result, GameStateResponse.class);
					if (fromJson.getStatus() == ResponseStatus.OK) {
						mGame = fromJson;
						initializeGameBoard();
					} else if (fromJson.getStatus() == ResponseStatus.INVALID_CREDENTIALS) {
						Toast.makeText(mContext, R.string.invalid_session, Toast.LENGTH_LONG).show();
					} else if (fromJson.getStatus() == ResponseStatus.INVALID_GAME) {
						Toast.makeText(mContext, R.string.invalid_game, Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
				Toast.makeText(mContext, R.string.terrible_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}

	private boolean checkTakeChance() {
		boolean valid = true;
		if (mGame.getPile().size() == 0) {
			valid = false;
		} else if (mGame.getHand().size() > 0) {
			for (Card card : mGame.getHand()) {
				if (card.getValue() == 2 || card.getValue() == 10 || card.getValue() >= mGame.getPile().get(mGame.getPile().size() - 1).getValue()) {
					valid = false;
					break;
				}
			}
		} else if (mGame.getFaceUp().size() > 0) {
			for (Card card : mGame.getFaceUp()) {
				if (card.getValue() == 2 || card.getValue() == 10 || card.getValue() >= mGame.getPile().get(mGame.getPile().size() - 1).getValue()) {
					valid = false;
					break;
				}
			}
		} else {
			valid = false;
		}
		return valid;
	}
}
