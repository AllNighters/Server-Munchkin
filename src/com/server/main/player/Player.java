package com.server.main.player;

import java.util.ArrayList;

import com.server.communication.ClientService;
import com.server.database.Card;
import com.server.database.Parser;
import com.server.database.card.ClassCard;
import com.server.database.card.MonsterCard;
import com.server.database.card.RaceCard;
import com.server.database.card.TreasureCard;
import com.server.main.Client;

public class Player {

	public static int totalNum = -1;

	private int playerNum;

	// List of game properties for the player
	private String name;
	private String id;

	private ArrayList<Card> hand;
	private ArrayList<Card> equipment;

	private Client client;

	public ClientService service;

	public Thread t;

	// add hand, armor, level, ect...
	private CLASS playerClass;

	private RACE playerRace;

	private int level;

	private int mBonus = 0;

	private int mRunBonus = 0;

	private boolean isReady = false;

	private boolean isDead = false;

	public Player(String username, String id, Client theClient) {
		name = username;
		if (name == null)
			name = "default";

		this.id = id;

		client = theClient;

		totalNum++;
		playerNum = totalNum;
		service = new ClientService(client, totalNum);
		t = new Thread(service);
		t.start();

		hand = new ArrayList<Card>();
		equipment = new ArrayList<Card>();
		level = 1;
		playerClass = CLASS.None;
		playerRace = RACE.Human;
		isDead = false;

	}

	/**
	 * Adds a card to the players hand
	 * 
	 * @param card
	 *            The card to be added to the hand.
	 */
	public synchronized void addCard(Card card) {
		hand.add(card);
	}

	public ArrayList<Card> getHand() {
		return hand;
	}

	public ArrayList<Card> getEquipment() {
		return equipment;
	}

	public synchronized boolean getIsReady() {
		return isReady;
	}

	public synchronized void setIsReady(boolean ready) {
		isReady = ready;
	}

	/**
	 * This should only be called when a player is removed from the game. This
	 * will be call to shift all player numbers down, following the one that has
	 * been removed.
	 * 
	 * @param num
	 *            The new player number the client service will represent
	 */
	public synchronized void setPlayerNumber(int num) {
		playerNum = num;
		service.setPlayerNum(num);
	}

	public synchronized int getPlayerNumber() {
		return playerNum;
	}

	public synchronized boolean equipCard(String cardName) {
		System.out.println("Player equipCard()");
		Card cardToEquip;
		int indexInHand = 0;

		for (indexInHand = 0; indexInHand < getHand().size(); indexInHand++) {
			if (getHand().get(indexInHand).getName().equals(cardName)) {

				cardToEquip = getHand().get(indexInHand);

				System.out.println(cardToEquip.getClass().toString() + " | "
						+ TreasureCard.class);

				// Checks if card is a Treasure Card
				if (cardToEquip.getClass().toString()
						.equals(TreasureCard.class.toString())) { // If is
																	// treasure
					TreasureCard card = (TreasureCard) cardToEquip;

					System.out.println("Card is a treasure, check equipable.");
					if (Parser.isEquipable(this, card)) {
						getHand().remove(indexInHand);
						getEquipment().add(card);
						System.out.println("End of Player equipCard()");
						updateBonuses();
						return true;
					}

				} else if (cardToEquip.getClass().toString()
						.equals(RaceCard.class.toString())) {
					RaceCard card = (RaceCard) cardToEquip;
					if (Parser.isEquipable(this, card)) {
						getHand().remove(indexInHand);
						getEquipment().add(card);
						setPlayerRace(card.getType());
						System.out.println("Race type: " + card.getType());
						checkEquipment();
						updateBonuses();
						return true;
					}

				} else if (cardToEquip.getClass().toString()
						.equals(ClassCard.class.toString())) {
					ClassCard card = (ClassCard) cardToEquip;
					if (Parser.isEquipable(this, card)) {
						getHand().remove(indexInHand);
						getEquipment().add(card);
						setPlayerClass(card.getType());
						System.out.println("Class type: " + card.getType());
						checkEquipment();
						updateBonuses();
						return true;
					}
				}

			}
		}

		return false;
	}

	public synchronized boolean discardCard(String cardName) {
		Card card;
		for (int i = 0; i < getHand().size(); i++) {
			card = getHand().get(i);

			// if treasure, do normal
			if (card.getClass().equals(TreasureCard.class)) {
				if (card.getName().equals(cardName)) {
					getHand().remove(i);

					return true;
				}
			} else if (card.getClass().equals(RaceCard.class)) {
				System.out.println("GOT RACE!");
				if (card.getName().equals(cardName)) {
					getHand().remove(i);
					return true;
				}
			} else if (card.getClass().equals(ClassCard.class)) {
				System.out.println("GOT CLASS!");
				if (card.getName().equals(cardName)) {
					getHand().remove(i);
					return true;
				}
			} else if (card.getClass().equals(MonsterCard.class)) {
				if (card.getName().equals(cardName)) {
					getHand().remove(i);
					return true;
				}
			}
		}

		for (int i = 0; i < getEquipment().size(); i++) {
			card = getEquipment().get(i);

			// if treasure, do normal
			if (card.getClass().equals(TreasureCard.class)) {
				if (card.getName().equals(cardName)) {
					getEquipment().remove(i);
					updateBonuses();
					return true;
				}
			} else if (card.getClass().equals(RaceCard.class)) {
				if (card.getName().equals(cardName)) {
					setPlayerRace(RACE.Human);
					getEquipment().remove(i);
					checkEquipment();
					return true;
				}
			} else if (card.getClass().equals(ClassCard.class)) {
				if (card.getName().equals(cardName)) {
					setPlayerClass(CLASS.None);
					getEquipment().remove(i);
					checkEquipment();
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * This is called when a class or race card is discarded from the equipment.
	 * It checks all currently equiped cards if they are still able to be used.
	 */
	private void checkEquipment() {
		Card card;
		for (int i = 0; i < getEquipment().size(); i++) {
			card = getEquipment().get(i);

			if (card.getClass().equals(TreasureCard.class)) {
				if (!Parser.isStillEquipable(this, (TreasureCard) card)) {
					discardCard(card.getName());
				}
			}
		}
	}

	private void updateBonuses() {
		System.out.println("Begin of player updateBonuses()");
		int bonus = 0;
		int runBonus = 0;
		TreasureCard treas;

		for (int i = 0; i < getEquipment().size(); i++) {
			if (getEquipment().get(i).getClass().toString()
					.equals(TreasureCard.class.toString())) {
				treas = (TreasureCard) getEquipment().get(i);
				bonus += treas.getBonus();

				if (treas.getClassExtra().equals(getPlayerClass())) {
					bonus += treas.getExtraBonus();
				} else if (treas.getClassExtra().equals(getPlayerRace())) {
					bonus += treas.getExtraBonus();
				} else if (treas.getClassExtra().equals("Run")) {
					runBonus += treas.getExtraBonus();
				}
			}
		}

		setBonus(bonus);
		setRunBonus(runBonus);
		System.out.println("Bonus: " + mBonus + "\nRun Bonus: " + mRunBonus);
		System.out.println("End of updateBonuses()");
	}

	public synchronized int getBonus() {
		return mBonus;
	}

	public synchronized void setBonus(int bonus) {
		this.mBonus = bonus;
	}

	public synchronized int getRunBonus() {
		return mRunBonus;
	}

	public synchronized void setRunBonus(int bonus) {
		this.mRunBonus = bonus;
	}

	public synchronized RACE getPlayerRace() {
		return playerRace;
	}

	public synchronized void setPlayerRace(RACE playerRace) {
		this.playerRace = playerRace;
	}

	public synchronized CLASS getPlayerClass() {
		return playerClass;
	}

	public synchronized void setPlayerClass(CLASS playerClass) {
		this.playerClass = playerClass;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized String getId() {
		return id;
	}

	public synchronized Client getClient() {
		return client;
	}

	public void setHand(ArrayList<Card> playersHand) {
		this.hand = playersHand;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setDead(boolean b) {
		this.isDead = b;
	}

	public boolean getDead() {
		return isDead;
	}
}
