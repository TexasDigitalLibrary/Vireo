package org.tdl.vireo.search;

/**
 * List of filter paramater types. These values may be set as various datatypes
 * in on the SearchFilter object. The main purpose of this class is to provide a
 * listing of all possible search parameters and assigned ids for easier
 * retrieval.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum SearchFacet {
	
	TEXT(1),
	STATE(2),
	ASSIGNEE(3),
	GRADUATION_SEMESTER(4),
	DEPARTMENT(5),
	PROGRAM(6),
	COLLEGE(7),	
	MAJOR(8),
	EMBARGO(9),
	DEGREE(10),
	DOCUMENT_TYPE(11),
	UMI_RELEASE(12),
	DATE_CHOOSE(13),
	DATE_RANGE(14);
	
	// The id for this search facet.
	private int id;

	/**
	 * Private constructor for the defined search facet listed above.
	 * 
	 * @param id
	 *            The id of the search facet.
	 */
	private SearchFacet(int id) {
		this.id = id;
	}

	/**
	 * @return The code of this search facet.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Locate a search facet based upon it's id.
	 * 
	 * @param id
	 *            The of the search facet.
	 * @return The search facet, or null if not found.
	 */
	public static SearchFacet find(int id) {

		for (SearchFacet searchFacet : SearchFacet.values()) {
			if (searchFacet.id == id)
				return searchFacet;
		}

		return null;
	}
}
