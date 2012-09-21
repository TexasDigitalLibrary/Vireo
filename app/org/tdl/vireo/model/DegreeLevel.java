package org.tdl.vireo.model;


/**
 * The possible degree levels supported by vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum DegreeLevel {
	NONE(1),
	UNDERGRADUATE(2),
	MASTERS(3),
	DOCTORAL(4);
	
	// The id for this degree level.
	private int id;

	/**
	 * Private constructor for the defined degree levels listed above.
	 * 
	 * @param id
	 *            The id of the degree level.
	 */
	private DegreeLevel(int id) {
		this.id = id;
	}

	/**
	 * @return The id of this level.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Locate a degree level based upon it's id.
	 * 
	 * @param id
	 *            The id of the desired level.
	 * @return The level, or null if not found.
	 */
	public static DegreeLevel find(int id) {

		for (DegreeLevel level : DegreeLevel.values()) {
			if (level.id == id)
				return level;
		}

		return null;
	}
}
