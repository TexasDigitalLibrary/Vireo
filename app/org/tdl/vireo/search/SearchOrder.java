package org.tdl.vireo.search;

/**
 * List of all possible ordering of Vireo submissions.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum SearchOrder {
	
	ID(1),
	SUBMITTER(2),
	
	STATE(3),
	
	ASSGINEE(4),
	
	DOCUMENT_TITLE(5),
	DOCUMENT_ABSTRACT(6),
	DOCUMENT_KEYWORDS(7),

	PRIMARY_DOCUMENT(8),
	
	GRADUATION_DATE(9),
	SUBMISSION_DATE(10),
	LICENSE_AGREEMENT_DATE(11),
	APPROVAL_DATE(12),
	
	
	COMMITTEE_APPROVAL_DATE(13),
	COMMITTEE_EMBARGO_APPROVAL_DATE(14),
	COMMITTEE_MEMBERS(15),
	COMMITTEE_CONTACT_EMAIL(16),
	COMMITTEE_DISPOSITION(17),

	DEGREE(18),
	COLLEGE(19),
	DEPARTMENT(20),
	MAJOR(21),
	
	EMBARGO_TYPE(22),
	DOCUMENT_TYPE(23),
	
	UMI_RELEASE(24),
	
	CUSTOM_ACTIONS(25),
	
	LAST_EVENT_ENTRY(26),
	LAST_EVENT_TIME(27);
	
	// The id for this search order.
	private int id;

	/**
	 * Private constructor for the defined search orders listed above.
	 * 
	 * @param id
	 *            The id of the search order.
	 */
	private SearchOrder(int id) {
		this.id = id;
	}

	/**
	 * @return The code of this search order.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Locate a search order based upon it's id.
	 * 
	 * @param id
	 *            The of the search order.
	 * @return The search order, or null if not found.
	 */
	public static SearchOrder find(int id) {

		for (SearchOrder searchOrder : SearchOrder.values()) {
			if (searchOrder.id == id)
				return searchOrder;
		}

		return null;
	}
}
