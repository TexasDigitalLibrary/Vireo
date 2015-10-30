package org.tdl.vireo.enums;

//TODO - why do we need Condition Type
//import org.tdl.vireo.model.ConditionType;
//import org.tdl.vireo.services.EnumByStringComparator;

public enum RecipientType {	
	// DO NOT CHANGE THIS ORDER!!! (or you'll corrupt the DB)
	STUDENT(1),
	ADVISOR(2),
	COLLEGE(3),
	DEPARTMENT(4),
	PROGRAM(5),
	ADMINGROUP(6),
	ASSIGNEE(7); 
	
	private int value;
	
	RecipientType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return this.name();
	}
	//TODO to be considered/deleted
	/*@Override
	public String toString() {
		switch (this) {
		case ADMINGROUP:
			return "Administrative Group";
		case ADVISOR:
			return "Advisor";
		case COLLEGE:
			return "College";
		case DEPARTMENT:
			return "Department";
		case PROGRAM:
			return "Program";
		case STUDENT:
			return "Student";
		case ASSIGNEE:
			return "Assignee";
		default:
			throw new InvalidParameterException();
		}
	}*/

	/**
	 * like {@link Enum}.values() but it returns them sorted by their toString() values
	 * 
	 * @return - {@link RecipientType}[] array sorted by toString() values
	 */
	/*public static RecipientType[] sortedValues() {
		RecipientType[] myVals = values();
		Arrays.sort(myVals, EnumByStringComparator.INSTANCE);
		return myVals;
	}*/
}
