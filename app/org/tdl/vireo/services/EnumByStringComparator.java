package org.tdl.vireo.services;

import java.util.Comparator;

/**
 * A Comparator helper class to help compare Enums' toString()s
 *  
 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
 */
public class EnumByStringComparator implements Comparator<Enum<?>> {

	public static final Comparator<Enum<?>> INSTANCE = new EnumByStringComparator();

	public int compare(Enum<?> enum1, Enum<?> enum2) {
		return enum1.toString().compareTo(enum2.toString());
	}
}