package org.tdl.vireo.email;

import java.security.InvalidParameterException;
import java.util.Arrays;

import org.tdl.vireo.model.ConditionType;
import org.tdl.vireo.services.EnumByStringComparator;

public enum RecipientType {
	Student, Advisor, College, Department, Program, AdminGroup, Assignee; // DO NOT CHANGE THIS ORDER!!! (or you'll corrupt the DB)

	@Override
	public String toString() {
		switch (this) {
		case AdminGroup:
			return "Administrative Group";
		case Advisor:
			return "Advisor";
		case College:
			return "College";
		case Department:
			return "Department";
		case Program:
			return "Program";
		case Student:
			return "Student";
		case Assignee:
			return "Assignee";
		default:
			throw new InvalidParameterException();
		}
	}

	/**
	 * like {@link Enum}.values() but it returns them sorted by their toString() values
	 * 
	 * @return - {@link RecipientType}[] array sorted by toString() values
	 */
	public static RecipientType[] sortedValues() {
		RecipientType[] myVals = values();
		Arrays.sort(myVals, EnumByStringComparator.INSTANCE);
		return myVals;
	}
}
