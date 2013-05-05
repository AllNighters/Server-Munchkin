package com.server.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.server.database.Card;
import com.server.database.DBaccess;
import com.server.database.card.MonsterCard;
import com.server.database.card.TreasureCard;
import com.server.main.player.BODY;
import com.server.main.player.SIZE;

public class TestDatabaseAccess {

	DBaccess db = null;

	@Before
	public void setUp() throws Exception {
		db = new DBaccess();
	}

	@Test
	public void testConnectionEstablished() {
		Card cardCheck = DBaccess.connect(1);
		assertTrue(cardCheck != null);
	}

	@Test
	public void testInvalidCardId() {
		Card cardCheck = DBaccess.connect(50);
		assertTrue(cardCheck == null);
	}

	@Test
	public void testMonsterCardValues() {
		/* Test against Bigfoot */
		Card cardCheck = DBaccess.connect(1);
		MonsterCard monster = (MonsterCard) cardCheck;
		assertTrue(monster.getID() == 1);
		assertTrue(monster.getName().equals("bigfoot"));
		assertTrue(monster.getLevel() == 12);
		assertTrue(monster.getGain() == 1);
		assertTrue(monster.getRun() == 0);
		assertTrue(monster.getLoss() == 0);
		assertTrue(monster.getTreasure() == 3);
	}

	@Test
	public void testTreasureCardValues() {
		/* Test against Flaming Armor */
		Card cardCheck = DBaccess.connect(15);
		TreasureCard treasure = (TreasureCard) cardCheck;
		assertTrue(treasure.getID() == 15);
		assertTrue(treasure.getName().equals("FlamingArmor"));
		assertTrue(treasure.getBonus() == 2);
		assertTrue(treasure.getBody().equals(BODY.Armor));
		assertTrue(treasure.getSize().equals(SIZE.Normal));
		assertTrue(treasure.getClassEffected().equals("None"));
		assertTrue(treasure.getClassExtra().equals("None"));
		assertTrue(treasure.getExtraBonus() == 0);

	}

	@After
	public void tearDown() throws Exception {
	}

}
