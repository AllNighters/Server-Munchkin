package com.server.database;

public abstract class Card {

	private int id;
	private String name;

	public boolean isActivated;
	public boolean isEquipped;

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}

	public void setName(String newname) {
		name = newname;
	}

	public void setID(int newid) {
		id = newid;
	}
	
	public String toString(){
		return name;
	}
}
