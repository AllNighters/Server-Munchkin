package com.server.database.card;

import com.server.database.Card;

public class MonsterCard extends Card {

	private int level;
	private String levelAdjust;
	private int levelAdjustCount;
	private int treasure;
	private int gain;
	private int loss;
	private int run;
	private String badStuff;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getLevelAdjust() {
		return levelAdjust;
	}

	public void setLevelAdjust(String levelAdjust) {
		this.levelAdjust = levelAdjust;
	}

	public int getLevelAdjustCount() {
		return levelAdjustCount;
	}

	public void setLevelAdjustCount(int levelAdjustCount) {
		this.levelAdjustCount = levelAdjustCount;
	}

	public int getTreasure() {
		return treasure;
	}

	public void setTreasure(int treasure) {
		this.treasure = treasure;
	}

	public int getGain() {
		return gain;
	}

	public void setGain(int gain) {
		this.gain = gain;
	}

	public int getLoss() {
		return loss;
	}

	public void setLoss(int loss) {
		this.loss = loss;
	}

	public int getRun() {
		return run;
	}

	public void setRun(int run) {
		this.run = run;
	}

	public String getBadStuff() {
		return badStuff;
	}

	public void setBadStuff(String badStuff) {
		this.badStuff = badStuff;
	}

}
