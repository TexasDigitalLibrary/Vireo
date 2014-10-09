/**
 * 
 */
package org.tdl.vireo.model;

import java.security.InvalidParameterException;
import java.util.Arrays;

import org.tdl.vireo.services.EnumByStringComparator;

/**
 * 
 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
 *
 */
public enum ConditionType {
	Always, College, Department, Program; // DO NOT CHANGE THIS ORDER!!! (or you'll corrupt the DB)
	
	@Override
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
	}

	/**
	 * like {@link Enum}.values() but it returns them sorted by their toString() values
	 * 
	 * @return - {@link ConditionType}[] array sorted by toString() values
	 */
	public static ConditionType[] sortedValues() {
		ConditionType[] myVals = values();
		Arrays.sort(myVals, EnumByStringComparator.INSTANCE);
		return myVals;
	}
}