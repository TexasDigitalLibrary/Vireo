package org.tdl.vireo.enums;

public enum InputType {
	// NEVER CHANGE THE INT VALUES OR YOU'LL RUIN THE DB
	INPUT_TEXT(0),
	INPUT_EMAIL(1), 
	INPUT_PASSWORD(2), 
	INPUT_CHECKBOX(3), 
	INPUT_DATETIME(4), 
	INPUT_FILE(5), 
	INPUT_RADIO(6), 
	INPUT_TEL(7), 
	INPUT_URL(8), 
	TEXTAREA(9), 
	SELECT(10);

	private int value;
	
	InputType(int num) {
		value = num;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
}
