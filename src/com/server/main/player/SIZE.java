package com.server.main.player;

public enum SIZE {
	Normal("None"), Small("Small"), Big("Big");

	private String size;

	SIZE(String size) {
		this.size = size;
	}

	public static SIZE fromLetter(String letter) {
		for (SIZE s : values()) {
			if (s.size.equals(letter))
				return s;
		}
		return null;
	}
}
