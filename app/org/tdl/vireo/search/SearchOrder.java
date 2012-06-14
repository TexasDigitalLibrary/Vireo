package org.tdl.vireo.search;

/**
 * List of all possible ordering of Vireo submissions.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum SearchOrder {
	
	ID(1),
	SUBMITTER(2),
	
	DOCUMENT_TITLE(3),
	DOCUMENT_ABSTRACT(4),
	DOCUMENT_KEYWORDS(5),
	
	EMBARGO_TYPE(6),
	
	PRIMARY_DOCUMENT(7),
	
	COMMITTEE_MEMBERS(8),
	COMMITTEE_CONTACT_EMAIL(9),
	COMMITTEE_APPROVAL_DATE(10),
	COMMITTEE_EMBARGO_APPROVAL_DATE(11),
	COMMITTEE_DISPOSITION(12),
	
	SUBMISSION_DATE(13),
	APPROVAL_DATE(14),
	LICENSE_AGREEMENT_DATE(15),
	
	DEGREE(16),
	DEPARTMENT(17),
	COLLEGE(18),
	MAJOR(19),
	
	DOCUMENT_TYPE(20),
	GRADUATION_YEAR(21),
	GRADUATION_MONTH(22),
	GRADUATION_DATE(23),
	
	STATE(24),
	
	ASSGINEE(25),
	
	UMI_RELEASE(26),
	
	CUSTOM_ACTIONS(27),
	
	LAST_EVENT_ENTRY(28),
	LAST_EVENT_TIME(29);
	
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
