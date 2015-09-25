package org.tdl.vireo.enums;

public enum Language {
	
	/*
	 * TODO: 	This should be a model and not an enum, to allow for the addition of
	 * 			other languages after compile time.
	 */
	
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
