package com.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.server.database.card.ClassCard;
import com.server.database.card.MonsterCard;
import com.server.database.card.RaceCard;
import com.server.database.card.TreasureCard;
import com.server.main.player.BODY;

public class DBaccess {

	final static String url = "jdbc:mysql://proj-309-14.cs.iastate.edu:3306/test";
	final static String user = "group14";
	final static String pass = "munchkin";

	public static Card connect(int idCard) {

		try {
			// Load the driver (registers itself)
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception E) {
			System.err.println("Unable to load driver.");
			E.printStackTrace();
		}

		Card result = null;
		MonsterCard monsterCard = null;
		boolean isMonster = false;
		TreasureCard treasureCard = null;
		boolean isTreasure = false;
		RaceCard raceCard = null;
		boolean isRace = false;
		ClassCard classCard = null;
		boolean isClass = false;
		try {
			Connection conn;
			conn = DriverManager.getConnection(url, user, pass);

			// variables used in the connection
			Statement stm = conn.createStatement();
			ResultSet rst = null;

			// query
			// Code for Monster cards based on id's
			if (idCard <= 14 && idCard > 0) {
				monsterCard = new MonsterCard();
				isMonster = true;
				rst = stm
						.executeQuery("select d.idDoor, d.name, d.level, d.levelAdjust, d.levelAdjustCount, d.treasure, d.gain, d.loss, d.run, d.badStuff"
								+ " "
								+ "from Door d"
								+ " "
								+ " where d.idDoor = '" + idCard + "';");
				// Process result
				while (rst.next()) {
					int idDoor = rst.getInt("idDoor");
					String name = rst.getString("name");
					int level = rst.getInt("level");
					String levelAdjust = rst.getString("levelAdjust");
					int levelAdjustCount = rst.getInt("levelAdjustCount");
					int treasure = rst.getInt("treasure");
					int gain = rst.getInt("gain");
					int loss = rst.getInt("loss");
					int run = rst.getInt("run");
					String badStuff = rst.getString("badStuff");
					monsterCard.setID(idDoor);
					monsterCard.setName(name);
					monsterCard.setLevel(level);
					monsterCard.setLevelAdjust(levelAdjust);
					monsterCard.setLevelAdjustCount(levelAdjustCount);
					monsterCard.setTreasure(treasure);
					monsterCard.setGain(gain);
					monsterCard.setLoss(loss);
					monsterCard.setRun(run);
					monsterCard.setBadStuff(badStuff);
				}

			} else if (idCard > 14 && idCard <= 17) {
				// Race Card
				raceCard = new RaceCard();
				isRace = true;
				rst = stm.executeQuery("select d.idDoor, d.name" + " "
						+ "from Door d" + " " + " where d.idDoor = '" + idCard
						+ "';");
				// Process result
				while (rst.next()) {
					int idDoor = rst.getInt("idDoor");
					String name = rst.getString("name");
					raceCard.setID(idDoor);
					raceCard.setName(name);
					raceCard.setType(name);
				}
			} else if (idCard > 17 && idCard <= 21) {
				// Class Card
				classCard = new ClassCard();
				isClass = true;
				rst = stm.executeQuery("select d.idDoor, d.name" + " "
						+ "from Door d" + " " + " where d.idDoor = '" + idCard
						+ "';");
				// Process result
				while (rst.next()) {
					int idDoor = rst.getInt("idDoor");
					String name = rst.getString("name");
					classCard.setID(idDoor);
					classCard.setName(name);
					classCard.setType(name);
				}
			} else if (idCard > 21 && idCard <= 55) {
				treasureCard = new TreasureCard();
				isTreasure = true;
				rst = stm
						.executeQuery("select t.idTreasure, t.name, t.bonus, t.body, t.size, t.classEffected, t.classExtra, t.extraBonus"
								+ " "
								+ "from Treasure t"
								+ " "
								+ " where t.idTreasure = '" + idCard + "';");
				// Process result
				while (rst.next()) {
					int idTreasure = rst.getInt("idTreasure");
					String name = rst.getString("name");
					int bonus = rst.getInt("bonus");
					String body = rst.getString("body");
					String size = rst.getString("size");
					String classEffected = rst.getString("classEffected");
					String classExtra = rst.getString("classExtra");
					int extraBonus = rst.getInt("extraBonus");
					treasureCard.setID(idTreasure);
					treasureCard.setName(name);
					treasureCard.setBonus(bonus);
					treasureCard.setBody(getBody(body));
					treasureCard.setSize(size);
					treasureCard.setClassEffected(classEffected);
					treasureCard.setClassExtra(classExtra);
					treasureCard.setExtraBonus(extraBonus);
				}

			}
			// Close connections
			stm.close();
			conn.close();

		} // End of try
		catch (SQLException E) {
			System.out.println("SQLException: " + E.getMessage());
			System.out.println("SQLState: " + E.getSQLState());
			System.out.println("VendorError: " + E.getErrorCode());

		} // End of catch
		if (isMonster) {
			return monsterCard;
		} else if (isTreasure) {
			return treasureCard;
		} else if (isClass) {
			return classCard;
		} else if (isRace) {
			return raceCard;
		} else {
			return result;
		}
	}
	
	private static BODY getBody(String a){
		
		if(a.equals("Armor")){
			return BODY.Armor;
		}else if(a.equals("Footgear")){
			return BODY.Footgear;
		}else if(a.equals("Headgear")){
			return BODY.Headgear;
		}else if(a.equals("Limbless")){
			return BODY.Limbless;
		}else if(a.equals("OneHand")){
			return BODY.OneHand;
		}else if(a.equals("TwoHand")){
			return BODY.TwoHand;
		}else if(a.equals("OneShot")){
			return BODY.OneShot;
		}
		
		return null;
	}
}
