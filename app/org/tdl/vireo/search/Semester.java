package org.tdl.vireo.search;

/**
 * A Simple data structure to hold the pair of Graduation Year and Graduation
 * Semester. This is primarily used for filter searching where users may select
 * a semester, this is a convenient object to house the pairing od data.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class Semester {
	public Integer year = null;
	public Integer month = null;

	/**
	 * Construct a new graduation semester with null values.
	 */
	public Semester() {
	}

	/**
	 * Construct a new graduation semester
	 * 
	 * @param year
	 *            The year
	 * @param month
	 *            The month
	 */
	public Semester(Integer year, Integer month) {
		this.year = year;
		this.month = month;
	}

	/**
	 * @return true if the two objects are equal, otherwise false.
	 */
	public boolean equals(Object otherObject) {

		if (!(otherObject instanceof Semester))
			return false;
		Semester other = (Semester) otherObject;

		if (this.year == null && other.year != null)
			return false;

		if (this.year != null && !this.year.equals(other.year))
			return false;

		if (this.month == null && other.month != null)
			return false;

		if (this.month != null && !this.month.equals(other.month))
			return false;

		return true;
	}
}
