package org.tdl.vireo.email;

import java.security.InvalidParameterException;

public enum RecipientType {
	Student, Advisor, College, Department, Program, AdminGroup;

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
		default:
			throw new InvalidParameterException();
		}
	};
}
