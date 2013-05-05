package com.server.database;

import java.util.ArrayList;

import com.server.database.card.ClassCard;
import com.server.database.card.RaceCard;
import com.server.database.card.TreasureCard;
import com.server.main.Game;
import com.server.main.GameRoom;
import com.server.main.player.BODY;
import com.server.main.player.CLASS;
import com.server.main.player.Player;
import com.server.main.player.RACE;
import com.server.main.player.SIZE;

public class Parser {

	/**
	 * Handles the game object, and the badstuff to the effected player it is
	 * being played on.
	 * 
	 * @param game
	 * @param badStuff
	 */
	public static void badStuffParser(GameRoom room, Game game, String badStuff) {
		// Check what the bad stuff is
		if (badStuff.equals("Headgear")) {
			// Destroys HeadGear!
			// Retrieve players hand
			ArrayList<Card> playersHand = game.getCurrentPlayer().getHand();
			// Remove the headgear
			for (int i = 0; i < playersHand.size(); i++) {
				// Determine if its a Treasure
				if (playersHand.get(i).getClass()
						.equals(TreasureCard.class.getClass())) {
					TreasureCard curCard = (TreasureCard) playersHand.get(i);
					// If Item is headgear remove
					// And is on the table
					if (curCard.getBody() == (BODY.Headgear)) {
						room.discardCard(game.getCurrentPlayerIndex(),
								curCard.getName());
					}
				}
			}
		} else if (badStuff.equals("BigItem")) {
			// Remove any Big Items!
			// Get players Hand
			// ArrayList<Card> playersHand = game.getCurrentPlayer().getHand();
			// for (Card card : playersHand) {
			// // If its a treasure card
			// if (card.getClass().equals(TreasureCard.class.getClass())) {
			// TreasureCard curCard = (TreasureCard) card;
			// // If item is BIG remove it
			// if (curCard.getSize().equals(SIZE.Big)) {
			// playersHand.remove(curCard);
			// }
			// }
			// }
			// // Set players new hand
			// game.getCurrentPlayer().setHand(playersHand);
		} else if (badStuff.equals("LowestPlayer")) {
			// Get list of all players
			ArrayList<Player> allPlayers = game.getPlayers();
			// Set current player as lowest known level
			Player curPlayer = game.getCurrentPlayer();
			int small = curPlayer.getLevel();
			for (Player player : allPlayers) {
				// If next players is lower, set curPlayer to it
				if (player.getLevel() < small) {
					small = player.getLevel();
				}
			}
			game.modifyLevel(curPlayer, small - curPlayer.getLevel());
		} else if (badStuff.equals("Escape")) {
			// Done in GameRoom
		} else if (badStuff.equals("Dead")) {
			// You die
			// Lose your whole hand, and equipment
			// Retrieve players hand
			ArrayList<Card> playersHand = game.getCurrentPlayer().getHand();
			ArrayList<Card> playersEquip = game.getCurrentPlayer()
					.getEquipment();
			// Remove everything that is not a race of class
			while (playersHand.size() > 0) {
				// Determine if its a Treasure
				room.discardCard(game.getCurrentPlayerIndex(),
						playersHand.get(0).getName());

			}
			// Remove Equipment too
			while (playersEquip.size() > 0) {
				room.discardCard(game.getCurrentPlayerIndex(), playersEquip
						.get(0).getName());
			}
			game.getCurrentPlayer().setDead(true);
		} else if (badStuff.equals("FullHand")) {
			// Deletes a whole hand
			// Get player hand
			ArrayList<Card> playersHand = game.getCurrentPlayer().getHand();
			// Remove each card
			while (playersHand.size() > 0) {
				// Determine if its a Treasure
				room.discardCard(game.getCurrentPlayerIndex(),
						playersHand.get(0).getName());

			}
		} else if (badStuff.contains("Race") && badStuff.contains("Class")) {
			// Get players hand
			ArrayList<Card> playersEquip = game.getCurrentPlayer()
					.getEquipment();

			for (Card card : playersEquip) {
				// If the card is of type class or race
				if (card.getClass().equals(ClassCard.class.getClass())
						|| card.getClass().equals(RaceCard.class.getClass())) {
					room.discardCard(game.getCurrentPlayerIndex(),
							card.getName());
				}
			}
		} else if (badStuff.equals("Race")) {
			// NOT USED
		} else if (badStuff.equals("Class")) {
			// NOT USED
		} else if (badStuff.equals("None")) {
			System.out.println("NOTHING HAPPENED!");
		}
	}

	/**
	 * Determines it the monster is a higher level then its base because of the
	 * current players class or race
	 * 
	 * @param game
	 * @param curLevel
	 * @param levelAdjust
	 * @param levelAdjustCount
	 * @return
	 */
	public static int levelParser(Game game, int curLevel, String levelAdjust,
			int levelAdjustCount) {
		int LEVEL = curLevel;
		if (levelAdjust.contains("Dwarf")) {
			if (game.getCurrentPlayer().getPlayerRace().equals(RACE.Dwarf)) {
				LEVEL = LEVEL + levelAdjustCount;
			}
			return LEVEL;
		} else if (levelAdjust.contains("Halfling")) {
			if (game.getCurrentPlayer().getPlayerRace().equals(RACE.Halfling)) {
				LEVEL = LEVEL + levelAdjustCount;
			}
			return LEVEL;
		} else if (levelAdjust.contains("Cleric")) {
			if (game.getCurrentPlayer().getPlayerClass().equals(CLASS.Cleric)) {
				LEVEL = LEVEL + levelAdjustCount;
			}
			return LEVEL;
		} else if (levelAdjust.contains("Warrior")) {
			if (game.getCurrentPlayer().getPlayerClass().equals(CLASS.Warrior)) {
				LEVEL = LEVEL + levelAdjustCount;
			}
			return LEVEL;
		} else if (levelAdjust.contains("Wizards")) {
			if (game.getCurrentPlayer().getPlayerClass().equals(CLASS.Wizard)) {
				LEVEL = LEVEL + levelAdjustCount;
			}
			return LEVEL;
		} else if (levelAdjust.contains("Level5")) {
			// Plutonium Dragon
			if (game.getCurrentPlayer().getLevel() < 5) {
				throw new Error(
						"Can't face ths monster, you don't have the courage");
			}
		}
		return LEVEL;
	}

	/**
	 * Determines if the current player gets more treasure then the monsters
	 * base, based off of his Class and Race.
	 * 
	 * @param game
	 * @param name
	 * @return
	 */
	public static int treasureParser(Game game, String name) {
		int TREASURE = 1;
		if (name.equals("PottedPlant")) {
			if (game.getCurrentPlayer().getPlayerRace().equals(RACE.Elf)) {
				TREASURE = TREASURE + 1;
			}
			return TREASURE;
		}
		return TREASURE;
	}

	/**
	 * Determines if the given treasure or item gives the current player a bonus
	 * based off of his Class or Race.
	 * 
	 * @param game
	 * @param name
	 * @param effectedBonus
	 * @return
	 */
	public static int effectParser(Game game, String name, int effectedBonus) {
		/* THINK ABOUT THIS ONE... */
		int BONUS = effectedBonus;
		if (name.equals(CLASS.Cleric)) {
			if (game.getCurrentPlayer().getPlayerClass().equals(CLASS.Cleric)) {
				game.getCurrentPlayer().setBonus(
						game.getCurrentPlayer().getBonus() + BONUS);
			}
		} else if (name.equals(CLASS.Thief)) {
			if (game.getCurrentPlayer().getClass().equals(CLASS.Thief)) {
				game.getCurrentPlayer().setBonus(
						game.getCurrentPlayer().getBonus() + BONUS);
			}
		} else if (name.equals(CLASS.Warrior)) {
			if (game.getCurrentPlayer().getPlayerClass().equals(CLASS.Warrior)) {
				game.getCurrentPlayer().setBonus(
						game.getCurrentPlayer().getBonus() + BONUS);
			}
		} else if (name.equals(CLASS.Wizard)) {
			if (game.getCurrentPlayer().getClass().equals(CLASS.Wizard)) {
				game.getCurrentPlayer().setBonus(
						game.getCurrentPlayer().getBonus() + BONUS);
			}
		} else if (name.equals(RACE.Dwarf)) {
			if (game.getCurrentPlayer().getPlayerRace().equals(RACE.Dwarf)) {
				game.getCurrentPlayer().setBonus(
						game.getCurrentPlayer().getBonus() + BONUS);
			}
			// TODO: FINISH
		} else if (name.equals(RACE.Elf)) {
			if (game.getCurrentPlayer().getPlayerRace().equals(RACE.Elf)) {
				game.getCurrentPlayer().setBonus(
						game.getCurrentPlayer().getBonus() + BONUS);
			}
		} else if (name.equals(RACE.Halfling)) {
			if (game.getCurrentPlayer().getPlayerRace().equals(RACE.Halfling)) {
				game.getCurrentPlayer().setBonus(
						game.getCurrentPlayer().getBonus() + BONUS);
			}
		} else if (name.equals(RACE.Human)) {
			if (game.getCurrentPlayer().getPlayerRace() == RACE.Human) {
				game.getCurrentPlayer().setBonus(
						game.getCurrentPlayer().getBonus() + BONUS);
			}
		} else if (name.equals("DoppleGanger")) {
			game.getCurrentPlayer().setBonus(
					game.getCurrentPlayer().getBonus() * 2);
		} else {

		}
		// CODE FOR INVISIBLITY POTION
		return BONUS;
	}

	/**
	 * Decides if a Treasure card is equipable by the given player.
	 * 
	 * @param player
	 *            player that is equipping the card.
	 * @param treasure
	 *            the card being equipped.
	 * @return true if can equip, false if can't
	 */
	public static boolean isEquipable(Player player, TreasureCard treasure) {
		return checkClass(player, treasure) && checkBody(player, treasure);
	}
	
	public static boolean isStillEquipable(Player player, TreasureCard treasure){
		return checkClass(player, treasure);
	}

	public static boolean isEquipable(Player player, ClassCard card) {
		if (player.getPlayerClass().equals(CLASS.None)) {
			return true;
		}
		return false;
	}

	public static boolean isEquipable(Player player, RaceCard card) {
		if (player.getPlayerRace().equals(RACE.Human)) {
			return true;
		}
		return false;
	}

	public static boolean checkBody(Player player, TreasureCard treasure) {
		// TODO Add Big item check

		BODY toAdd = treasure.getBody();
		System.out.println(toAdd);
		// headgear, armor, footgear, hand, hand
		boolean[] pb = new boolean[5];
		for (int i = 0; i < 5; i++) {
			pb[i] = false;
		}

		for (Card eq : player.getEquipment()) {
			if (eq.getClass().equals(TreasureCard.class)) { // Make sure current
															// card is a
															// equipment
				TreasureCard treas = (TreasureCard) eq;
				BODY cardBody = treas.getBody();
				System.out.println(cardBody.toString());

				if (cardBody.equals(BODY.Headgear)) {
					pb[0] = true;
				} else if (cardBody.equals(BODY.Armor)) {
					pb[1] = true;
				} else if (cardBody.equals(BODY.Footgear)) {
					pb[2] = true;
				} else if (cardBody.equals(BODY.OneHand)) {
					if (pb[3]) {
						pb[4] = true;
					} else {
						pb[3] = true;
					}
				} else if (cardBody.equals(BODY.TwoHand)) {
					pb[3] = true;
					pb[4] = true;
				}
			}
		}

		System.out.println("toAdd type: " + toAdd.toString());
		if (toAdd.equals(BODY.Limbless)) {
			return true;
		} else if (toAdd.equals(BODY.Headgear)) {
			if (!pb[0]) {
				return true;
			}
		} else if (toAdd.equals(BODY.Armor)) {
			if (!pb[1]) {
				return true;
			}
		} else if (toAdd.equals(BODY.Footgear)) {
			if (!pb[2]) {
				return true;
			}

		} else if (toAdd.equals(BODY.OneHand)) {
			if (!pb[3] || !pb[4]) {
				return true;
			}
		} else if (toAdd.equals(BODY.TwoHand)) {
			if (!(pb[3] || pb[4])) {
				return true;
			}
		}

		return false;
	}

	private static boolean checkClass(Player player, TreasureCard treasure) {
		String classEffected = treasure.getClassEffected();

		System.out.println(classEffected);
		if (classEffected.equals("None")) {
			return true;
		} else if (classEffected.equals(CLASS.Cleric.toString())) {
			if (player.getPlayerClass() == CLASS.Cleric) {
				return true;
			}
		} else if (classEffected.equals(CLASS.Thief.toString())) {
			if (player.getPlayerClass() == CLASS.Thief) {
				return true;
			}
		} else if (classEffected.equals(CLASS.Warrior.toString())) {
			if (player.getPlayerClass() == CLASS.Warrior) {
				return true;
			}
		} else if (classEffected.equals(CLASS.Wizard.toString())) {
			if (player.getPlayerClass() == CLASS.Wizard) {
				return true;
			}
		} else if (classEffected.equals(RACE.Dwarf.toString())) {
			if (player.getPlayerRace() == RACE.Dwarf) {
				return true;
			}
		} else if (classEffected.equals(RACE.Elf.toString())) {
			if (player.getPlayerRace() == RACE.Elf) {
				return true;
			}
		} else if (classEffected.equals(RACE.Halfling.toString())) {
			if (player.getPlayerRace() == RACE.Halfling) {
				return true;
			}
		} else if (classEffected.equals(RACE.Human.toString())) {
			if (player.getPlayerRace() == RACE.Human) {
				return true;
			}
		} else if (classEffected.equals("DoppleGanger")) {
			return true;
		} else if (classEffected.equals("Not Wizard")) {
			if (!(player.getPlayerClass() == CLASS.Wizard)) {
				return true;
			}
		} else if (classEffected.equals("Not Warrior")) {
			if (!(player.getPlayerClass() == CLASS.Warrior)) {
				return true;
			}
		} else if (classEffected.equals("Not Thief")) {
			if (!(player.getPlayerClass() == CLASS.Thief)) {
				return true;
			}
		}
		return false;
	}
}
