package com.server.database.card;

import com.server.database.Card;
import com.server.main.player.BODY;
import com.server.main.player.SIZE;

public class TreasureCard extends Card {

	private int bonus;
	private BODY body;
	private SIZE size;
	private String classEffected;
	private String classExtra;
	private int extraBonus;

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}

	public BODY getBody() {
		return body;
	}

	public void setBody(BODY b) {
		this.body = b;
	}

	public SIZE getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = SIZE.fromLetter(size);
	}

	public String getClassEffected() {
		return classEffected;
	}

	public void setClassEffected(String classEffected) {
		this.classEffected = classEffected;
	}

	public String getClassExtra() {
		return classExtra;
	}

	public void setClassExtra(String classExtra) {
		this.classExtra = classExtra;
	}

	public int getExtraBonus() {
		return extraBonus;
	}

	public void setExtraBonus(int extraBonus) {
		this.extraBonus = extraBonus;
	}

}
