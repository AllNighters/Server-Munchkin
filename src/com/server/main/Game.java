package com.server.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.common.Message;
import com.server.database.Card;
import com.server.database.DBaccess;
import com.server.database.card.MonsterCard;
import com.server.main.player.Player;

public class Game {

	/* List of players in game */
	private ArrayList<Player> players;
	/* Current players turn */
	private int curPlayer;
	/* Current phase of the game */
	private GameState state;

	private Card kickedCard;
	private MonsterCard troubleCard;

	// Received drawKick message
	private boolean drawDoor;

	private boolean trouble;

	private IntList doorList;
	private IntList treasList;
	private final int DOOR = 21;
	private final int TREASURE = 34;

	public Game() {
		players = new ArrayList<Player>();
		doorList = new IntList();
		treasList = new IntList();
		int curPlayer = 0;
		drawDoor = false;
		trouble = false;
		troubleCard = null;
		state = GameState.Lobby;
	}

	public synchronized boolean hasLookForTrouble() {
		return trouble;
	}

	public synchronized void setLookForTrouble(boolean a) {
		trouble = a;
	}

	public synchronized void setTroubleCard(MonsterCard card) {
		troubleCard = card;
	}

	public synchronized MonsterCard getTroubleCard() {
		return troubleCard;
	}

	public synchronized boolean hasDrawDoor() {
		return drawDoor;
	}

	public synchronized void setDrawDoor(boolean a) {
		drawDoor = a;
	}

	public synchronized Card getKickedCard() {
		return kickedCard;
	}

	public synchronized void setKickedCard(Card drawn) {
		kickedCard = drawn;
	}

	public synchronized GameState getState() {
		return state;
	}

	public synchronized void setState(GameState newState) {
		state = newState;
	}

	/**
	 * Generates a random int to retrieve a Door card from the database. Then
	 * keeps track of the ints generated to not redraw cards. The game will
	 * check if the door deck is empty at the end of a players turn.
	 * 
	 * If it is empty, all cards in the door discard will be removed, and all
	 * int values corresponding to those cards will be removed from the
	 * doorList.
	 * 
	 * @return The card that was drawn.
	 */
	public Card drawDoor() {
		Random rand = new Random();
		int a = -1;
		boolean good = false;
		while (!good) {
			a = rand.nextInt(DOOR) + 1;
			if (doorList.contains(a)) {
				good = false;
			} else {
				good = true;
			}
		}
		doorList.add(a);
		return DBaccess.connect(a);
	}

	public Card drawDoorFixed(int a) {
		return DBaccess.connect(a);
	}

	/**
	 * Generates a random int to retrieve a Door card from the database. Then
	 * keeps track of the ints generated to not redraw cards. The game will
	 * check if the door deck is empty at the end of a players turn.
	 * 
	 * If it is empty, all cards in the door discard will be removed, and all
	 * int values corresponding to those cards will be removed from the
	 * doorList.
	 * 
	 * @return The card that was drawn.
	 */
	public Card drawTreasure() {
		Random rand = new Random();
		int a = -1;
		boolean good = false;
		while (!good) {
			a = rand.nextInt(TREASURE) + 1 + DOOR;
			if (treasList.contains(a)) {
				good = false;
			} else {
				good = true;
			}
		}
		treasList.add(a);
		return DBaccess.connect(a);
	}

	/**
	 * Add a player to the current list of players. This should only be called
	 * before the game is started.
	 * 
	 * @param toAdd
	 *            Player being added
	 * @return true if the player was successfully added, false otherwise
	 */
	public synchronized boolean addPlayer(Player toAdd) {
		if (state.equals(GameState.Lobby)) {
			players.add(toAdd);
			return true;
		}
		return false;
	}

	/**
	 * Removes a player specified from the index value from the players list. it
	 * will then correctly adjust player numbers for each player
	 * 
	 * @param index
	 *            Player to be removed
	 * @return True if player was successfully removed
	 */
	public synchronized boolean removePlayer(int index) {
		Player.totalNum--;
		players.get(index).service.isActive = false;
		players.remove(index);
		// Adjusts the players that follow the removed player down one.
		for (int i = index; i < players.size(); i++) {
			players.get(i).setPlayerNumber(i);
		}

		return true;

	}

	/**
	 * Returns the specified player from the list of players in the game
	 * 
	 * @param index
	 *            Specified index in the array
	 * @return Returns null if the index is out of bounds. Returns the player
	 *         object otherwise
	 */
	public Player getPlayer(int index) {
		if (index < players.size()) {
			return players.get(index);
		} else {
			return null;
		}
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public int getNumPlayers() {
		return players.size();
	}

	public Player getCurrentPlayer() {
		return players.get(curPlayer);
	}

	public int getCurrentPlayerIndex() {
		return curPlayer;
	}

	public synchronized IntList getDoorList() {
		return doorList;
	}

	public void modifyLevel(Player player, int amount) {
		int curLevel = player.getLevel();
		int newLevel = curLevel + amount;

		if (newLevel < 1)
			newLevel = 1;

		player.setLevel(newLevel);
		String[] vals = new String[2];
		vals[0] = player.getName();
		vals[1] = player.getLevel() + "";
		sendMessageToAll(new Message("playerInfo", vals));

	}

	/**
	 * Increments the curPlayer value to properly keep track of which players
	 * turn it is. This should be called at the end of Charity phase.
	 */
	public void nextTurn() {
		curPlayer++;
		if (curPlayer >= players.size()) {
			curPlayer = 0;
		}
	}

	public synchronized void sendMessageToPlayer(int thePlayer, Message msg) {
		try {
			getPlayer(thePlayer).getClient().out.writeObject((msg));
			getPlayer(thePlayer).getClient().out.flush();

		} catch (IOException e) {
			System.out.println("player disconnected");
		}
	}

	public synchronized void sendMessageToAll(Message msg) {
		for (int i = 0; i < getNumPlayers(); i++) {
			sendMessageToPlayer(i, msg);
		}
	}

	public synchronized void sendToAllExcluding(Message msg, int index) {
		for (int i = 0; i < getNumPlayers(); i++) {
			if (index != i) {
				sendMessageToPlayer(i, msg);
			}
		}
	}

}
