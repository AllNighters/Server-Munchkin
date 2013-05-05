package com.server.main.player;

import java.io.Serializable;

public class PlayerInfo implements Serializable {
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString(){
		return "Name: " + name;
	}
}
