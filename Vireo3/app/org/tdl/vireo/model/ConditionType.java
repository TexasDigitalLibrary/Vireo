package org.tdl.vireo.enums;

//import org.tdl.vireo.services.EnumByStringComparator;


public enum ConditionType {
	// DO NOT CHANGE THIS ORDER!!! (or you'll corrupt the DB)
	ALWAYS(1),
	COLLEGE(2),
	DEPARTMENT(3),
	PROGRAM(4); 
	
	private int value;
	
	ConditionType(int num) {
		value = num;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	
	/*@Override
	public String toString() {
		switch (this) {
		case Always:
			return "Always";
		case College:
			return "College";
		case Department:
			return "Department";
		case Program:
			return "Program";
		default:
			throw new InvalidParameterException();
		}
	}*/

	/**
	 * like {@link Enum}.values() but it returns them sorted by their toString() values
	 * 
	 * @return - {@link ConditionType}[] array sorted by toString() values
	 */
	/*public static ConditionType[] sortedValues() {
		ConditionType[] myVals = values();
		Arrays.sort(myVals, EnumByStringComparator.INSTANCE);
		return myVals;
	}*/
}