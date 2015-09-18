package org.tdl.vireo.enums;

public enum Language {
	// NEVER CHANGE THE INT VALUES OR YOU'LL RUIN THE DB
	ENGLISH(0);
	
	private int value;
	
	Language(int num) {
		value = num;
	}
	
	public int getValue() {
		return value;
	}
}
