package org.tdl.vireo.model;

/**
 * The possible ways to format a name.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum NameFormat {

	/**
	 * Example: Scott Phillips
	 */
	FIRST_LAST,

	/**
	 * Example: Scott Allen Phillips
	 */
	FIRST_MIDDLE_LAST,

	/**
	 * Example: Scott Allen Phillips 1978-
	 */
	FIRST_MIDDLE_LAST_BIRTH,

	/**
	 * Example: Scott Phillips 1978-
	 */
	FIRST_LAST_BIRTH,

	/**
	 * Example: Phillips, Scott
	 */
	LAST_FIRST,

	/**
	 * Example: Phillips, Scott Allen
	 */
	LAST_FIRST_MIDDLE,

	/**
	 * Example: Phillips, Scott Allen 1978-
	 */
	LAST_FIRST_MIDDLE_BIRTH,

	/**
	 * Example: Phillips, Scott 1978-
	 */
	LAST_FIRST_BIRTH;

	/**
	 * Format the name according to the provided name format using all the name
	 * parts provided. All nameparts are optional, however a name with no
	 * nameparts is just a blank string.
	 * 
	 * @param format
	 *            How to format the name.
	 * @param firstName
	 *            The first name, may be null.
	 * @param middleName
	 *            The middle name, may be null.
	 * @param lastName
	 *            The last name, may be null.
	 * @param birthYear
	 *            The birth year, may be null.
	 * @return A formatted name.
	 */
	public static String format(NameFormat format, String firstName,
			String middleName, String lastName, Integer birthYear) {

		if (format == null) 
			throw new IllegalArgumentException("A name format is required");
		
		NameFormatter name = new NameFormatter();

		switch (format) {
		case FIRST_LAST:
			// Ex: first last
			name.addPart(firstName);
			name.addPart(lastName);
			break;

		case FIRST_MIDDLE_LAST:
			// Ex: first middle last
			name.addPart(firstName);
			name.addPart(middleName);
			name.addPart(lastName);
			break;

		case FIRST_MIDDLE_LAST_BIRTH:
			// Ex: first middle last 1980-
			name.addPart(firstName);
			name.addPart(middleName);
			name.addPart(lastName);
			name.addBirthYear(birthYear);
			break;

		case FIRST_LAST_BIRTH:
			// Ex: first last 1980-
			name.addPart(firstName);
			name.addPart(lastName);
			name.addBirthYear(birthYear);
			break;

		case LAST_FIRST:
			// EX: last, first
			name.addPartWithComma(lastName);
			name.addPart(firstName);
			break;

		case LAST_FIRST_MIDDLE:
			// EX: last, first middle
			name.addPartWithComma(lastName);
			name.addPart(firstName);
			name.addPart(middleName);
			break;

		case LAST_FIRST_MIDDLE_BIRTH:
			// EX: last, first middle 1980-
			name.addPartWithComma(lastName);
			name.addPart(firstName);
			name.addPart(middleName);
			name.addBirthYear(birthYear);
			break;

		case LAST_FIRST_BIRTH:
			// EX: last, first 1980-
			name.addPartWithComma(lastName);
			name.addPart(firstName);
			name.addBirthYear(birthYear);
			break;
		}

		return name.toString();
	}

	/**
	 * This is a small helper class to generate name formats. It is a wrapper
	 * around a string builder to conveniently add name parts without checking
	 * if the part is present, or previous parts exist.
	 * 
	 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
	 * 
	 */
	public static class NameFormatter {

		// The internal name as parts are added.
		public StringBuilder name = new StringBuilder();

		public boolean addComma = false;

		/**
		 * Add a name part to the name. If the part exists then it is appended
		 * to the string separated by a space from previous parts.
		 * 
		 * @param part
		 *            The name part to append, may be null or blank.
		 */
		public void addPart(String part) {

			// The part is blank, so skip it.
			if (part == null || part.trim().length() == 0)
				return;

			if (name.length() == 0)
				// It's the first part, so just set it.
				name.append(part);
			else if (addComma)
				// The previous part should be separated by a comma.
				name.append(", " + part);
			else
				// Otherwise the name already includes at least one part and
				// there's no fancy comma rule going on.
				name.append(" " + part);

			addComma = false;
		}

		/**
		 * Add a name part to the name that will be followed by a comma if the
		 * part exists. This is basically for the last name first rule, where it
		 * should be "last, first"
		 * 
		 * @param part
		 *            The name part to append, may be null or blank.
		 */
		public void addPartWithComma(String part) {

			// The part is blank, so skip it.
			if (part == null || part.trim().length() == 0)
				return;

			addPart(part);

			// If another part gets added separate the part with a comma.
			addComma = true;
		}

		/**
		 * Add a birth year part. This part is added if the year is not null and
		 * not equal to zero. When added it is followed by a "dash" making the
		 * assumption that the person is still alive.
		 * 
		 * @param year
		 *            The birth year part to add, may be null.
		 */
		public void addBirthYear(Integer year) {

			if (year == null || year == 0)
				return;

			addComma = false;
			addPart(year + "-");
		}

		/**
		 * @return the formatted name as a string.
		 */
		public String toString() {
			return name.toString();
		}
	}

}
