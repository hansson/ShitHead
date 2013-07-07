package com.hansson.shithead.entitys;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.hansson.shithead.entitys.BaseEntity;
import com.hansson.shithead.gcm.GCMPlayerMoveFaceDown;
import com.hansson.shithead.rr.FaceDownRequest;
import com.hansson.shithead.rr.GameStateResponse;
import com.hansson.shithead.rr.MoveRequest;
import com.hansson.shithead.rr.MoveResponse;
import com.hansson.shithead.rr.ResponseStatus;

/**
 * This class will handle the main game logic.
 * 
 * @author hansson
 * 
 */
public class GameBoard extends BaseEntity {

	private int mCurrentPlayer = 0;
	private String mCurrentPlayerName = "-";
	private boolean mStarted = false;
	private boolean mSwitching = false;
	private boolean mFinished = false;
	private List<Player> mPlayers;
	private List<Card> mDeck;
	private List<Card> mPile;
	private Date mStartedAt;
	private Date mLastUpdate;
	private boolean mChanceTaken = false;
	// Customizable
	private int mRoundLength = 60;
	private int mNumberOfPlayers = 4;
	// Fields
	public static final String CURRENT_PLAYER = "mCurrentPlayer";
	public static final String STARTED = "mStarted";
	public static final String FINISHED = "mFinished";
	public static final String PLAYERS = "mPlayers";
	public static final String DECK = "mDeck";
	public static final String PILE = "mPile";
	public static final String ROUND_LENGHT = "mRoundLength";
	public static final String LAST_UPDATE = "mLastUpdate";
	public static final String STARTED_AT = "mStartedAt";
	public static final String NUMBER_OF_PLAYERS = "mNumberOfPlayers";
	public static final String CHANCE_TAKEN = "mChanceTaken";

	/**
	 */
	public GameBoard() {
		for (int i = 0; i < 4; i++) {
			for (int j = 2; j < 15; j++) {
				Card card = new Card(j, i);
				getDeck().add(card);
			}
		}
		Collections.shuffle(getDeck());
	}

	/**
	 * When the game is full this method should be called to deal the cards to the players and do other initialization stuff. After this method is called a
	 * GameState should be sent to every player in the game.
	 */
	public void start() {
		mStarted = true;
		setSwitching(true);
		mStartedAt = new Date();
		deal();
	}

	/**
	 * Add a new player to the game. After this method a checkStart() should be called.
	 * 
	 * @param player
	 * @return if the player was added or not
	 */
	public boolean addPlayer(Player player) {
		boolean added = false;
		if (!mStarted && getPlayers().size() < mNumberOfPlayers && !getPlayers().contains(player)) {
			getPlayers().add(player);
			added = true;
		}
		return added;
	}

	/**
	 * This method is used to decide if it is time to start the game. If this method returns true then the start() method should be called.
	 * 
	 * @return if it is time to start the game
	 */
	public boolean checkStart() {
		if (!mStarted && getPlayers().size() == mNumberOfPlayers) {
			return true;
		}
		return false;
	}

	/**
	 * Check if time is out and then force a player to make a move and return the card that the player used. The used card should then be sent to the other
	 * players.
	 * 
	 * @return The used card
	 */
	public Card forcePlayerMove() {
		// TODO implement
		return null;
	}

	/**
	 * Returns the current game state from the point of view of a specific player.
	 * 
	 * @param forPlayer - The ID of the User
	 * @return The current game state
	 */
	public GameStateResponse getGameState(String forPlayer) {
		GameStateResponse response = new GameStateResponse();
		for (Player player : getPlayers()) {
			if (player.getPlayerId().equals(forPlayer)) {
				response.setFaceDown(player.getFaceDown().size());
				response.setFaceUp(player.getFaceUp());
				response.setHand(player.getHand());
				response.setPlayerSwitching(player.isSwitching());
				response.setPosition(player.getPosition());
			} else {
				Opponent opponent = new Opponent();
				opponent.setFaceDown(player.getFaceDown().size());
				opponent.setFaceUp(player.getFaceUp());
				opponent.setOnHand(player.getHand().size());
				opponent.setUsername(player.getUsername());
				opponent.setPosition(player.getPosition());
				response.getOpponents().add(opponent);
			}
		}
		response.setId(super.getId());
		response.setCurrentPlayer(mCurrentPlayerName);
		response.setLastUpdate(getLastUpdate());
		response.setRoundLength(mRoundLength);
		response.setStartedAt(mStartedAt);
		response.setSwitching(isSwitching());
		response.setStatus(ResponseStatus.OK);
		response.setPile(getPile());
		response.setDeckSize(getDeck().size());
		response.setFinished(mFinished);
		return response;
	}

	/**
	 * Card switching is done, decide who should start
	 */
	public void switchingDone() {
		setSwitching(false);
		decideStarter();
	}

	/**
	 * This method will check if the move the user wants to make is valid and respond with an appropriate response status after doing lots of magic
	 * 
	 * @param request - The player move request
	 * @param response - Response for the move made by the user
	 * @param user - What user tried to make the move
	 */
	public void checkMove(MoveRequest request, MoveResponse response, User user) {
		Player player = new Player();
		player.setPlayerId(user.getId());
		int index = mPlayers.indexOf(player);
		if (index != -1) {
			player = mPlayers.get(index);
			if (player.getUsername().equals(mCurrentPlayerName)) {
				if (request.getType() == MoveRequest.MoveType.MOVE) {
					boolean valid = false;
					int value = 0;
					// For every card
					for (Card card : request.getCards()) {
						// valid if player hand is larger than 0 and card exists in hand
						// or if player hand is empty and card exists is face up
						if ((player.getHand().size() > 0 && player.getHand().contains(card)) || (player.getHand().size() == 0 && player.getFaceUp().contains(card))) {
							if (value == 0) {
								value = card.getValue();
							}
							// If the same value on every card and card value is same or higher than pile top, or pile is empty. Or is 2 or 10
							if (value == card.getValue()
									&& (card.getValue() == 10 || card.getValue() == 2 || getPile().size() == 0 || card.getValue() >= getPile().get(
											getPile().size() - 1).getValue())) {
								valid = true;
							} else {
								valid = false;
								break;
							}
						} else {
							valid = false;
							break;
						}
					}
					// if valid move
					if (valid) {
						// if card(s) was 10's
						if (request.getCards().get(0).getValue() == 10) {
							getPile().clear();
							response.setGameEvent(MoveResponse.Event.EXPLODE);
							// or if there now are 4 of a kind
						} else if (checkFourOfAKind(request.getCards())) {
							getPile().clear();
							response.setGameEvent(MoveResponse.Event.FOUR);
							// or just a normal move
						} else {
							do {
								mCurrentPlayer++;
								if (mCurrentPlayer == mNumberOfPlayers) {
									mCurrentPlayer = 0;
								}
							} while (mPlayers.get(mCurrentPlayer).getPosition() != 0);
							mCurrentPlayerName = mPlayers.get(mCurrentPlayer).getUsername();
							response.setGameEvent(MoveResponse.Event.NONE);
							getPile().addAll(request.getCards());
						}
						response.setNextPlayer(mCurrentPlayerName);
						player.getHand().removeAll(request.getCards());
						player.getFaceUp().removeAll(request.getCards());
						response.setNewCards(getFromDeck(3 - player.getHand().size()));
						player.getHand().addAll(response.getNewCards());
						response.setStatus(ResponseStatus.OK);
						mChanceTaken = true;
					} else {
						response.setStatus(ResponseStatus.NOT_OK);
					}
				} else if (request.getType() == MoveRequest.MoveType.CHANCE) {
					// if valid take pile move
					if (mChanceTaken) {
						response.setStatus(ResponseStatus.CHANCE_TAKEN);
					} else if (validChanceTakePile(player) && getDeck().size() > 0) {
						response.setNewCards(getFromDeck(1));
						player.getHand().addAll(response.getNewCards());
						response.setNextPlayer(mCurrentPlayerName);
						response.setStatus(ResponseStatus.OK);
						mChanceTaken = true;
					} else {
						response.setStatus(ResponseStatus.NOT_OK);
					}
				} else if (request.getType() == MoveRequest.MoveType.PILE) {
					// if valid take chance card move
					if (validChanceTakePile(player)) {
						List<Card> newCards = new LinkedList<Card>();
						newCards.addAll(getPile());
						response.setNewCards(newCards);
						player.getHand().addAll(response.getNewCards());
						getPile().clear();
						do {
							mCurrentPlayer++;
							if (mCurrentPlayer == mNumberOfPlayers) {
								mCurrentPlayer = 0;
							}
						} while (mPlayers.get(mCurrentPlayer).getPosition() != 0);
						mCurrentPlayerName = mPlayers.get(mCurrentPlayer).getUsername();
						response.setNextPlayer(mCurrentPlayerName);
						response.setGameEvent(MoveResponse.Event.PILE);
						response.setStatus(ResponseStatus.OK);
						mChanceTaken = false;
					} else {
						response.setStatus(ResponseStatus.NOT_OK);
					}
				}
				if (player.getFaceDown().size() == 0 && player.getFaceUp().size() == 0 && player.getHand().size() == 0) {
					decideFinnishPosition(player);
				}
			} else {
				response.setStatus(ResponseStatus.OTHER_PLAYER);
			}
		} else {
			response.setStatus(ResponseStatus.INVALID_GAME);
		}
	}

	public void handleFaceDown(FaceDownRequest request, MoveResponse response, User user, GCMPlayerMoveFaceDown done) {
		Player player = new Player();
		player.setPlayerId(user.getId());
		int playerIndex = mPlayers.indexOf(player);
		if (playerIndex != -1) {
			player = mPlayers.get(playerIndex);
			if (player.getUsername().equals(mCurrentPlayerName)) {
				if (player.getHand().size() == 0 && player.getFaceUp().size() == 0 && player.getFaceDown().size() != 0) {
					int index = request.getIndex();
					if (index < 0) {
						index = 0;
					}
					if (index > player.getFaceDown().size()) {
						index = player.getFaceDown().size() - 1;
					}
					Card card = player.getFaceDown().remove(index);
					done.setFaceDownCard(card);
					getPile().add(card);
					if (card.getValue() == 10) {
						getPile().clear();
						response.setGameEvent(MoveResponse.Event.EXPLODE);
					} else if (checkFourOfAKind(getPile().subList(getPile().size() - 1, getPile().size()))) {
						getPile().clear();
						response.setGameEvent(MoveResponse.Event.FOUR);
					} else if (card.getValue() == 2 || getPile().size() == 0 || card.getValue() >= getPile().get(getPile().size() - 1).getValue()) {
						do {
							mCurrentPlayer++;
							if (mCurrentPlayer == mNumberOfPlayers) {
								mCurrentPlayer = 0;
							}
						} while (mPlayers.get(mCurrentPlayer).getPosition() != 0);
						mCurrentPlayerName = mPlayers.get(mCurrentPlayer).getUsername();
						response.setGameEvent(MoveResponse.Event.NONE);
					} else {
						response.getNewCards().addAll(getPile());
						player.getHand().addAll(getPile());
						getPile().clear();
						do {
							mCurrentPlayer++;
							if (mCurrentPlayer == mNumberOfPlayers) {
								mCurrentPlayer = 0;
							}
						} while (mPlayers.get(mCurrentPlayer).getPosition() != 0);
						mCurrentPlayerName = mPlayers.get(mCurrentPlayer).getUsername();
						response.setGameEvent(MoveResponse.Event.PILE);
					}
					response.setNextPlayer(mCurrentPlayerName);
					response.setStatus(ResponseStatus.OK);
				}
				if (player.getFaceDown().size() == 0 && player.getFaceUp().size() == 0 && player.getHand().size() == 0) {
					decideFinnishPosition(player);
				}
			} else {
				response.setStatus(ResponseStatus.OTHER_PLAYER);
			}
		} else {
			response.setStatus(ResponseStatus.INVALID_GAME);
		}
	}

	private void decideFinnishPosition(Player player) {
		int highestPos = 0;
		for (Player other : getPlayers()) {
			if (other.getPosition() > highestPos) {
				highestPos = other.getPosition();
			}
		}
		player.setPosition(highestPos + 1);
		if (player.getPosition() == 3) {
			setFinished(true);
		}
	}

	private boolean validChanceTakePile(Player player) {
		boolean valid = true;
		if (getPile().size() == 0) {
			valid = false;
		} else if (player.getHand().size() > 0) {
			for (Card card : player.getHand()) {
				if (card.getValue() == 2 || card.getValue() == 10 || card.getValue() >= getPile().get(getPile().size() - 1).getValue()) {
					valid = false;
					break;
				}
			}
		} else if (player.getFaceUp().size() > 0) {
			for (Card card : player.getFaceUp()) {
				if (card.getValue() == 2 || card.getValue() == 10 || card.getValue() >= getPile().get(getPile().size() - 1).getValue()) {
					valid = false;
					break;
				}
			}
		} else {
			valid = false;
		}
		return valid;
	}

	private boolean checkFourOfAKind(List<Card> cards) {
		int count = cards.size();
		for (Card card : getPile()) {
			if (card.getValue() == cards.get(0).getValue()) {
				count++;
			}
		}
		if (count == 4) {
			return true;
		}
		return false;
	}

	private List<Card> getFromDeck(int size) {
		List<Card> newCards = new LinkedList<Card>();
		if (getDeck().size() > 0 && size > 0) {
			for (int i = 0; i < size; i++) {
				Card remove = getDeck().remove(0);
				if (remove == null) {
					break;
				}
				newCards.add(remove);
			}
		}
		return newCards;
	}

	private void decideStarter() {
		int lowestPlayer = 0;
		Card lowestCard = new Card(100, 3);
		// For every player
		for (int i = 0; i < getPlayers().size(); i++) {
			Player player = getPlayers().get(i);
			// For every card in that players hand
			for (int j = 0; j < player.getHand().size(); j++) {
				// If it is lower than the current lowest and is not a 2 or a 10
				if (player.getHand().get(j).getValue() < lowestCard.getValue() && player.getHand().get(j).getValue() != 2
						&& player.getHand().get(j).getValue() != 10) {
					lowestPlayer = i;
					lowestCard = player.getHand().get(j);
				}
				// Or if it is the same value (still not 2 or 10)
				else if (player.getHand().get(j).getValue() == lowestCard.getValue() && player.getHand().get(j).getValue() != 2
						&& player.getHand().get(j).getValue() != 10) {
					// And a better ranked suit
					if (player.getHand().get(j).getSuit() < lowestCard.getSuit()) {
						lowestCard = player.getHand().get(j);
						lowestPlayer = i;
					}
				}
			}
		}
		mCurrentPlayer = lowestPlayer;
		mCurrentPlayerName = getPlayers().get(mCurrentPlayer).getUsername();
	}

	private void deal() {
		for (int i = 0; i < getPlayers().size(); i++) {
			Player player = getPlayers().get(i);
			for (int j = 0; j < 3; j++) {
				player.getHand().add(getDeck().remove(0));
				player.getFaceDown().add(getDeck().remove(0));
				player.getFaceUp().add(getDeck().remove(0));
			}
		}
	}

	public List<Player> getPlayers() {
		if (mPlayers == null) {
			mPlayers = new LinkedList<Player>();
		}
		return mPlayers;
	}

	public void setPlayers(List<Player> players) {
		mPlayers = players;
	}

	public List<Card> getDeck() {
		if (mDeck == null) {
			mDeck = new LinkedList<Card>();
		}
		return mDeck;
	}

	public void setDeck(List<Card> deck) {
		mDeck = deck;
	}

	public List<Card> getPile() {
		if (mPile == null) {
			mPile = new LinkedList<Card>();
		}
		return mPile;
	}

	public void setPile(List<Card> pile) {
		mPile = pile;
	}

	public Date getStartedAt() {
		return mStartedAt;
	}

	public String getCurrentPlayerName() {
		return mCurrentPlayerName;
	}

	public Date getLastUpdate() {
		return mLastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		mLastUpdate = lastUpdate;
	}

	public boolean isSwitching() {
		return mSwitching;
	}

	public void setSwitching(boolean switching) {
		mSwitching = switching;
	}

	public boolean isFinished() {
		return mFinished;
	}

	public void setFinished(boolean finished) {
		mFinished = finished;
	}
}
