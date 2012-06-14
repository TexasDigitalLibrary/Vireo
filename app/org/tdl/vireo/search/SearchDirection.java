package org.tdl.vireo.search;

/**
 * The possible directions an ordered set of submissions/actionlogs may be
 * ordered by.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum SearchDirection {
	ASCENDING(0), 
	DESCENDING(1);
	
	// The id for this search order.
	private int id;

	/**
	 * Private constructor for the defined search directions listed above.
	 * 
	 * @param id
	 *            The id of the search direction.
	 */
	private SearchDirection(int id) {
		this.id = id;
	}

	/**
	 * @return The id of this search direction.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Locate a search direction based upon it's id.
	 * 
	 * @param id
	 *            The of the search direction.
	 * @return The search direction, or null if not found.
	 */
	public static SearchDirection find(int id) {

		for (SearchDirection searchDirection : SearchDirection.values()) {
			if (searchDirection.id == id)
				return searchDirection;
		}

		return null;
	}
}

