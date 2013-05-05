package com.server.main;

import java.util.Random;

import com.common.Message;
import com.server.communication.MessageListener;
import com.server.database.Card;
import com.server.database.Parser;
import com.server.database.card.MonsterCard;
import com.server.database.card.TreasureCard;
import com.server.main.player.BODY;
import com.server.main.player.CLASS;
import com.server.main.player.Player;
import com.server.main.player.RACE;

public class GameRoom implements Runnable, MessageListener {

	final static int WIN_LEVEL = 5;
	/* Unique ID used in database */
	private int uID;

	private int oneshotPowerBonus = 0;

	/* Helps tell if a client is still connected */
	private boolean isConnected;
	/* This holds all values important to the game */
	Game game;

	public GameRoom(int ID) {
		uID = ID;
		// players = new ArrayList<Player>();
		// state = GameState.StandBy;
		game = new Game();
		isConnected = false;
	}

	/* Game loop */
	@Override
	public void run() {
		while (true) {
			// Keep Track of the Game Stages
			preGame();
			startGame();
			while (game.getState() != GameState.EndGame) {
				if (game.getState() == GameState.StandBy) {
					game.setState(GameState.BeginTurn);
				} else if (game.getState() == GameState.BeginTurn) {
					beginTurn();
				} else if (game.getState() == GameState.KickDoor) {
					doKick();
				} else if (game.getState() == GameState.LookForTrouble) {
					doLookForTrouble();
				} else if (game.getState() == GameState.LootRoom) {
					doLootRoom();
				} else if (game.getState() == GameState.Charity) {
					doCharity();
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Game is done!");
		}
	}

	/* ########## PRE GAME STAGE ########## */
	/**
	 * PreGame stage of the game.
	 */
	public void preGame() {
		game.setState(GameState.Lobby);
		while (game.getState() == GameState.Lobby) {
			// Check if players are still connected, if they are not, then
			// remove them from the players list
			checkClientConnection();
			sendLobbyDataToPlayers();
			checkReadyStatus();
			// Pause thread to allow main thread to add players to game room.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void checkClientConnection() {
		System.out.println("Checking Client Connections...");
		for (int i = 0; i < game.getNumPlayers(); i++) {
			String values[] = new String[1];
			values[0] = "N/A";
			game.sendMessageToPlayer(i, new Message("connection", values));

			// Gets client input from ClientService
			int numCheck = 3;
			boolean hasCommand = false;

			// Loops until server has received message from client, or after
			// 'numCheck' amount of attempts
			for (int p = 0; p < numCheck && hasCommand == false; p++) {
				if (!isConnected) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (!isConnected) {
				// Player is disconnected, so remove player
				System.out.println("Client is disconnected");
				removePlayer(i);
			} else {
				isConnected = false;
			}
		}

	}

	private synchronized void sendLobbyDataToPlayers() {
		String[] vals = new String[game.getNumPlayers() * 2];
		for (int i = 0; i < game.getNumPlayers(); i++) {
			vals[i * 2] = game.getPlayer(i).getName() + ":"
					+ game.getPlayer(i).getIsReady();
			vals[(i * 2) + 1] = game.getPlayer(i).getId();
		}
		Message msg = new Message("lobby", vals);
		game.sendMessageToAll(msg);
	}

	public void waitForAllReady() {
		for (int p = 0; p < game.getNumPlayers(); p++) {
			game.getPlayer(p).setIsReady(false);
			System.out.println(game.getPlayer(p).getIsReady());
		}

		boolean allReady = false;
		while (!allReady) {
			allReady = true;
			for (Player player : game.getPlayers()) {
				// System.out.println(player.getIsReady());
				allReady = allReady && player.getIsReady();
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void waitForReady(Player player) {
		player.setIsReady(false);

		boolean allReady = false;
		while (!allReady) {
			// System.out.println("waiting for ready...  " +
			// player.getIsReady());
			allReady = player.getIsReady();

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void checkReadyStatus() {
		boolean ready = true;
		for (int i = 0; i < game.getNumPlayers(); i++) {
			ready = ready && game.getPlayer(i).getIsReady();
		}
		if (ready && game.getNumPlayers() > 0) {
			game.setState(GameState.StandBy);
			game.sendMessageToAll(new Message("startgame", null));

			waitForAllReady(); // Wait for onCreate() to finish on each client
		}
	}

	/* ########## START GAME STAGE ########## */

	/**
	 * Actual game logic of starting the game, and the different phases.
	 */
	public void startGame() {

		// OnCreate() has been executed on each client.
		// Now send initial data to each client

		for (Player player : game.getPlayers()) {
			// for (int i = 0; i < 2; i++) {
			// Draw 4 treasures and 4 door cards for each player
			player.addCard(game.drawDoor());
			player.addCard(game.drawDoor());
			player.addCard(game.drawTreasure());
			player.addCard(game.drawTreasure());
			player.addCard(game.drawTreasure());
			// }
			System.out.println("Hand Size: " + player.getHand().size());
			System.out.println(player.getHand());
		}

		// Make Message from Player class. Send messages to each player
		for (Player player : game.getPlayers()) {
			String[] cardNames = new String[player.getHand().size()];
			for (int i = 0; i < player.getHand().size(); i++) {
				cardNames[i] = (player.getHand().get(i).getName());
			}
			Message hand = new Message("hand", cardNames);
			game.sendMessageToPlayer(player.getPlayerNumber(), hand);
			for (int p = 0; p < game.getNumPlayers(); p++) {
				if (p != player.getPlayerNumber()) {
					Message otherHand = new Message("otherPlayerHand", null);
					game.sendMessageToPlayer(player.getPlayerNumber(),
							otherHand);
				}
			}
		}

		// Send initial data to players
		sendAllPlayersMessage();

		System.out.println("Before loop");

		// Allow players to play any cards they wish to play.
		// Set isReady for all players to false.
		// Once every player is ready, move on to player 1's turn
		waitForAllReady();

		System.out.println("After loop");
		// Everyone is ready, tell players to move to kickdoor state
	}

	private void beginTurn() {
		// resetReadyStatus(game.getCurrentPlayer());}

		// Check if player was dead
		if (game.getCurrentPlayer().getDead()) {
			System.out.println("You are dead!");
			game.getCurrentPlayer().setDead(false);

			// for (int i = 0; i < 4; i++) {
			System.out.println("Drawing cards");
			game.getCurrentPlayer().addCard(game.drawDoor());
			game.getCurrentPlayer().addCard(game.drawDoor());
			game.getCurrentPlayer().addCard(game.drawTreasure());
			game.getCurrentPlayer().addCard(game.drawTreasure());
			game.getCurrentPlayer().addCard(game.drawTreasure());
			System.out.println("Cards added");
			// }

			game.sendMessageToPlayer(game.getCurrentPlayerIndex(),
					createPlayerHandMessage(game.getCurrentPlayerIndex()));
		}

		// Draw and send door card to player before he clicks.
		// This allows the phone to preload the card.
		game.setKickedCard(game.drawDoor());
		// game.setKickedCard(game.drawDoorFixed(11));

		String[] vals = new String[2];
		vals[0] = game.getCurrentPlayer().getName();
		vals[1] = game.getKickedCard().getName();
		System.out.println("Kicked Card: " + vals[1]);

		game.sendMessageToAll(new Message("beginTurn", vals));

		while (!game.hasDrawDoor()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // Once this loop is done, player has clicked to draw card, so move to
			// kickdoor state
		game.setDrawDoor(false);
		game.setState(GameState.KickDoor);
	}

	/* ########## KICK IN THE DOOR STAGE ########## */
	private void doKick() {
		System.out.println("doKick()");
		// Send message to tell players that we are in kickDoor phase

		// If card is a monster, go to battle phase
		if (game.getKickedCard().getClass() == MonsterCard.class) {
			doBattle((MonsterCard) game.getKickedCard());
			game.setKickedCard(null);
			if (game.getState().equals(GameState.EndGame)) {
				return;
			}
		} else {

			// Add kicked card to hand.
			game.getCurrentPlayer().addCard(game.getKickedCard());
			game.setKickedCard(null);
			game.setDrawDoor(false);
			game.sendMessageToPlayer(game.getCurrentPlayerIndex(),
					createPlayerHandMessage(game.getCurrentPlayerIndex()));

			game.setKickedCard(game.drawDoor());
			String[] val = new String[1];
			val[0] = game.getKickedCard().getName();
			game.sendMessageToAll(new Message("kickDoor", val));

			game.setLookForTrouble(false);
			while ((!game.hasDrawDoor() && !game.hasLookForTrouble())) {

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (game.hasDrawDoor()) { // Player chose to loot the room
				System.out.println("Player chose to loot the room.");
				game.getCurrentPlayer().addCard(game.getKickedCard());
				game.setKickedCard(null);
				game.setDrawDoor(false);
				game.sendMessageToPlayer(game.getCurrentPlayerIndex(),
						createPlayerHandMessage(game.getCurrentPlayerIndex()));

			} else if (game.hasLookForTrouble()) { // Player chose to look for
													// trouble
				System.out.println("Before lookfortrouble: "
						+ game.getDoorList());
				// Put pre-drawn card back into the deck
				for (int i = 0; i < game.getDoorList().size(); i++) {
					Integer id = game.getDoorList().get(i);
					if (id == game.getKickedCard().getID()) {
						game.getDoorList().remove(id);
					}
				}

				game.setKickedCard(null);

				System.out.println("After lookfortrouble: "
						+ game.getDoorList());

				System.out.println("Player is looking for trouble!");
				game.setLookForTrouble(false);
				discardCard(game.getCurrentPlayerIndex(), game.getTroubleCard()
						.getName());
				doBattle(game.getTroubleCard());
				if (game.getState().equals(GameState.EndGame)) {
					return;
				}
				game.setTroubleCard(null);
			}

		}
		game.setState(GameState.Charity);

		// Grab the kicked card and reset it in the game variable
		game.setKickedCard(null);

	}

	private void doBattle(MonsterCard monster) {
		game.setState(GameState.Battle);
		Player player = game.getCurrentPlayer();

		Boolean win = false;
		String[] vals = new String[2];
		vals[0] = monster.getName();
		vals[1] = monster.getLevel() + "";

		int playerPower = 0;
		int prevPlayerPower = -1;

		int monsterPower;

		game.sendMessageToAll(new Message("battle", vals));

		player.setIsReady(false);

		while (!player.getIsReady()) {
			// Waits for player to click "Run Away" Button
			System.out
					.println("Level: " + player.getLevel() + "Bonus: "
							+ player.getBonus() + " Oneshot: "
							+ getOneshotPowerBonus());
			playerPower = player.getLevel() + player.getBonus()
					+ getOneshotPowerBonus();
			monsterPower = monster.getLevel();

			// If oneshots have been played, update the UI on the player's phone
			if (playerPower != prevPlayerPower) {
				String[] val = new String[1];
				val[0] = playerPower + "";
				System.out.println("Player power: " + playerPower);
				game.sendMessageToAll(new Message("battlePower", val));
				System.out.println(new Message("battlePower", val));
			}

			// Check if monster gets any special bonuses against player
			if (monster.getLevelAdjust().contains(
					player.getPlayerClass().toString())
					|| monster.getLevelAdjust().contains(
							player.getPlayerRace().toString())) {
				monsterPower += monster.getLevelAdjustCount();
			}

			// Check if player beats monster
			if ((playerPower > monsterPower)
					|| (playerPower == monsterPower && player.getPlayerClass()
							.equals(CLASS.Warrior))) {
				// You win!
				win = true;
			}

			prevPlayerPower = playerPower;

		}

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean gameOver = false;
		if (win) {

			String[] val = new String[1];
			val[0] = "You win!";
			game.sendMessageToPlayer(player.getPlayerNumber(), new Message(
					"winBattle", val));
			System.out.println("Player has won battle!");
			game.modifyLevel(player, monster.getGain());
			if (player.getLevel() >= WIN_LEVEL) {
				game.setState(GameState.EndGame);
				String[] over = new String[1];
				over[0] = player.getName();
				game.sendMessageToAll(new Message("gameOver", over));
				gameOver = true;
				return;
			}

			String[] cards = new String[monster.getTreasure()];
			TreasureCard drawnCard;
			for (int i = 0; i < monster.getTreasure(); i++) {
				drawnCard = (TreasureCard) game.drawTreasure();
				player.addCard(game.drawTreasure());
				cards[i] = drawnCard.getName();
			}

			waitForReady(game.getCurrentPlayer()); // Wait for player to click
													// treasure deck
			game.sendMessageToPlayer(game.getCurrentPlayerIndex(), new Message(
					"treasure", cards));

			game.sendMessageToPlayer(player.getPlayerNumber(),
					createPlayerHandMessage(player.getPlayerNumber()));
		} else {
			Random rand = new Random();
			int roll = rand.nextInt(6) + 1;
			roll += player.getRunBonus();

			System.out.println("Dice roll: " + roll);
			String[] mes = new String[1];
			mes[0] = "You rolled a " + roll + ".";
			game.sendMessageToPlayer(game.getCurrentPlayerIndex(), new Message(
					"toast", mes));
			String[] send = new String[1];
			send[0] = String.valueOf(roll);
			game.sendMessageToAll(new Message("roll", send));
			waitForReady(game.getCurrentPlayer());
			if (roll > 4) {
				// You safely ran away

				// If Mr. Bones, you still lose a level
				if (monster.getName().equals("mr_bones")) {
					game.modifyLevel(player, -1);
				}
				String[] val = new String[1];
				val[0] = "Ran Away!";
				game.sendMessageToAll(new Message("winRun", val));
			} else {
				// Badstuff happens to you!

				if (monster.getBadStuff().equals("None")) {
					game.modifyLevel(player, -monster.getLoss());
				} else {
					Parser.badStuffParser(this, game, monster.getBadStuff());
				}

				String[] val = new String[1];
				val[0] = "Defeated!";
				game.sendMessageToAll(new Message("lossRun", val));
			}
		}

		if (!gameOver) {
			setOneshotPowerBonus(0); // Reset the oneshot power bonus
			game.setState(GameState.KickDoor);
		}
	}

	private void doLookForTrouble() {
		// Message to be sent to the client
		String msg;
		for (int i = 0; i < game.getNumPlayers(); i++) {
			msg = "Player " + (game.getCurrentPlayerIndex() + 1)
					+ " just looked for trouble!";
			// Send the current player a specific message
			if (i == game.getCurrentPlayerIndex()) {
				msg = "You just looked for trouble!";
			}
			// sendToPlayer(i, msg);
		}
		game.setState(GameState.LootRoom);
	}

	private void doLootRoom() {
		// Message to be sent to the client
		String msg;
		for (int i = 0; i < game.getNumPlayers(); i++) {
			msg = "Player " + (game.getCurrentPlayerIndex() + 1)
					+ " just looted the room!";
			// Send the current player a specific message
			if (i == game.getCurrentPlayerIndex()) {
				msg = "You just looted the room!";
			}
			// sendToPlayer(i, msg);
		}
		game.setState(GameState.Charity);
	}

	private void doCharity() {
		System.out.println("Now in charity phase.");

		int handSize = 5;
		if (game.getCurrentPlayer().getPlayerRace().equals(RACE.Dwarf)) {
			handSize = 6;
		}

		String[] val = new String[1];
		if (game.getCurrentPlayer().getHand().size() > handSize) {
			val[0] = "Get down to " + handSize + " in your hand.";
		} else {
			val[0] = "Hit 'Ready' to go to next players turn.";
		}
		game.sendMessageToAll(new Message("charity", val));

		waitForReady(game.getCurrentPlayer());
		System.out.println("Charity: Beore while loop");
		while (game.getCurrentPlayer().getHand().size() > handSize) {
			String[] vals = new String[1];
			vals[0] = "Discard or equip "
					+ (game.getCurrentPlayer().getHand().size() - handSize)
					+ " more cards.";
			game.sendMessageToPlayer(game.getCurrentPlayerIndex(), new Message(
					"toast", vals));
			waitForReady(game.getCurrentPlayer());

		}
		System.out.println("Charity: After while loop");
		game.nextTurn();
		game.setState(GameState.BeginTurn);
		System.out.println("Done with doCharity()");
	}

	private synchronized void setOneshotPowerBonus(int bonus) {
		oneshotPowerBonus = bonus;
	}

	private synchronized int getOneshotPowerBonus() {
		return oneshotPowerBonus;
	}

	// Returns true if player has successfully been added
	public synchronized void addPlayer(Player player) {
		player.service.setOnMessageReceived(this);
		game.addPlayer(player);
	}

	// public synchronized void addPlayer(String username, Client newPlayer) {
	// game.addPlayer(new Player(username, newPlayer));
	//
	// }

	/**
	 * Removes the player from the players array list. Each player has a
	 * ClientService thread running, so the thread should be stopped as well.
	 * 
	 * @param index
	 *            - The index to specifiy which player to remove in the array
	 *            list
	 */
	private synchronized void removePlayer(int index) {
		// somehow end the thread
		// players.get(index).t.

		// Now remove the player from the arraylist
		game.removePlayer(index);
	}

	public Game getGame() {
		return game;
	}

	private synchronized Message createPlayerHandMessage(int playerNum) {

		String[] vals = new String[game.getPlayer(playerNum).getHand().size()];

		for (int i = 0; i < vals.length; i++) {
			vals[i] = game.getPlayer(playerNum).getHand().get(i).getName();
		}

		Message msg = new Message("hand", vals);

		System.out.println("Hand Message: \n " + msg);
		return msg;
	}

	private synchronized Message createPlayerEquipmentMessage(int playerNum) {

		String[] vals = new String[game.getPlayer(playerNum).getEquipment()
				.size()];

		for (int i = 0; i < vals.length; i++) {
			vals[i] = game.getPlayer(playerNum).getEquipment().get(i).getName();
		}

		Message msg = new Message("equipment", vals);
		System.out.println("Equipment Message: \n " + msg);
		return msg;
	}

	private synchronized void sendAllPlayersMessage() {

		String[] vals = new String[game.getNumPlayers() * 2];
		for (int i = 0; i < game.getNumPlayers(); i++) {
			vals[i * 2] = game.getPlayer(i).getName();
			vals[(i * 2) + 1] = game.getPlayer(i).getId();
		}
		game.sendMessageToAll(new Message("allPlayers", vals));
	}

	// private synchronized void sendMessageToPlayer(int thePlayer, Message msg)
	// {
	// try {
	// game.getPlayer(thePlayer).getClient().out.writeObject((msg));
	// game.getPlayer(thePlayer).getClient().out.flush();
	//
	// } catch (IOException e) {
	// System.out.println("player disconnected");
	// }
	// }
	//
	// private synchronized void sendMessageToAll(Message msg) {
	// for (int i = 0; i < game.getNumPlayers(); i++) {
	// sendMessageToPlayer(i, msg);
	// }
	// }
	//
	// private synchronized void sendToAllExcluding(Message msg, int index) {
	// for (int i = 0; i < game.getNumPlayers(); i++) {
	// if (index != i) {
	// sendMessageToPlayer(i, msg);
	// }
	// }
	// }

	public synchronized boolean equipCard(int playerNum, String cardName) {
		System.out.println("Start of GameRoom equipCard()");
		if (game.getPlayer(playerNum).equipCard(cardName)) {

			game.sendMessageToPlayer(playerNum,
					createPlayerHandMessage(playerNum));

			game.sendMessageToPlayer(playerNum,
					createPlayerEquipmentMessage(playerNum));

			// Send updated powers to all players
			String[] vals = new String[2];
			vals[0] = game.getPlayer(playerNum).getName();
			vals[1] = (game.getPlayer(playerNum).getBonus()) + "";
			game.sendMessageToAll(new Message("playerPower", vals));

			return true;
		}

		return false;
		// cardToEquip.
	}

	public synchronized boolean discardCard(int playerNum, String cardName) {

		if (game.getPlayer(playerNum).discardCard(cardName)) {

			game.sendMessageToPlayer(playerNum,
					createPlayerHandMessage(playerNum));

			Message equipment = createPlayerEquipmentMessage(playerNum);
			System.out.println(equipment);
			game.sendMessageToPlayer(playerNum, equipment);

			// Send updated powers to all players
			String[] vals = new String[2];
			vals[0] = game.getPlayer(playerNum).getName();
			vals[1] = (game.getPlayer(playerNum).getBonus()) + "";
			game.sendMessageToAll(new Message("playerPower", vals));

			return true;
		}

		return false;
	}

	/**
	 * This method is called from the ClientService threads whenever a client
	 * has sent a message.
	 * 
	 * @param playerNumber
	 *            Specifies which player has sent the message according to index
	 *            in player array.
	 * @parm message The message that has been received from the client
	 */
	@Override
	public void onMessageReceived(int playerNumber, Message message) {
		// TODO Auto-generated method stub
		if (message.type.equals("connection")) {
			isConnected = true;
			System.out.println("connection: " + playerNumber + " | "
					+ game.getNumPlayers());
			System.out.println("Player: "
					+ game.getPlayer(playerNumber).getName()
					+ " has responded!");
		} else if (message.type.equals("chat")) {
			System.out.println("chat: " + playerNumber + " | "
					+ game.getNumPlayers());
			message.values[0] = game.getPlayer(playerNumber).getName();
			game.sendToAllExcluding(message, playerNumber);
		} else if (message.type.equals("ready")) {
			game.getPlayer(playerNumber).setIsReady(true);
		} else if (message.type.equals("equip")) {
			if (!(game.getState() == GameState.Battle)) {
				System.out.println(game.getState().toString());
				if (game.getState() == GameState.StandBy
						|| game.getCurrentPlayerIndex() == playerNumber) {
					if (!equipCard(playerNumber, message.values[0])) {
						String[] val = new String[1];
						val[0] = "Unable to equip card. "
								+ game.getPlayer(playerNumber).getPlayerRace()
								+ " | "
								+ game.getPlayer(playerNumber).getPlayerClass();
						game.sendMessageToPlayer(playerNumber, new Message(
								"toast", val));
					}
				}
				System.out.println("done equiping");
			} else {
				String[] val = new String[1];
				val[0] = "You cannot equip items at this time.";
				game.sendMessageToPlayer(playerNumber,
						new Message("toast", val));
			}
		} else if (message.type.equals("discard")) {
			discardCard(playerNumber, message.values[0]);
		} else if (message.type.equals("drawDoor")) {
			if (game.getCurrentPlayerIndex() == playerNumber) {
				if (game.getState().equals(GameState.KickDoor)) {
					// game.setKickedCard(game.drawDoor());
				}
				game.setDrawDoor(true);
			}

		} else if (message.type.equals("drawTreasure")) {
			if (game.getCurrentPlayerIndex() == playerNumber
					&& game.getState().equals(GameState.Battle)) {
				game.getPlayer(playerNumber).setIsReady(true);
			} else {
				String[] val = new String[1];
				val[0] = "You have no cards to be drawn right now!";
				game.sendMessageToPlayer(playerNumber,
						new Message("toast", val));
			}
		} else if (message.type.equals("use")) {
			// for (Card card : game.getPlayer(playerNumber).getHand()) {
			for (int i = 0; i < game.getPlayer(playerNumber).getHand().size(); i++) {
				Card card = game.getPlayer(playerNumber).getHand().get(i);
				if (card.getName().equals(message.values[0])) {

					if (game.getState().equals(GameState.KickDoor)) {
						if (card.getClass() == MonsterCard.class) {
							game.setLookForTrouble(true);
							game.setTroubleCard((MonsterCard) card);
							discardCard(playerNumber, message.values[0]);
						}
					} else if (game.getState().equals(GameState.Battle)) {
						if (card.getClass() == TreasureCard.class) {
							TreasureCard treas = (TreasureCard) card;
							if (treas.getBody().equals(BODY.OneShot)) {
								if (treas.getName().equals("doppleganger")) {
									setOneshotPowerBonus((getOneshotPowerBonus()
											+ game.getPlayer(playerNumber)
													.getLevel() + game
											.getPlayer(playerNumber).getBonus()) * 2);
								}// TODO add other oneshot here
							}
						}
					}
				}

			}
		}
	}
}
