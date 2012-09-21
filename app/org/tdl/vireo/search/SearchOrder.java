package org.tdl.vireo.search;

/**
 * List of all possible ordering of Vireo submissions.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum SearchOrder {
	
	ID(1),
	STUDENT_EMAIL(2),
	STUDENT_NAME(3),
	STUDENT_ID(4),
	
	STATE(5),
	
	ASSIGNEE(6),
	
	DOCUMENT_TITLE(7),
	DOCUMENT_ABSTRACT(8),
	DOCUMENT_KEYWORDS(9),

	PRIMARY_DOCUMENT(10),
	
	GRADUATION_DATE(11),
	SUBMISSION_DATE(12),
	LICENSE_AGREEMENT_DATE(13),
	APPROVAL_DATE(14),
	
	
	COMMITTEE_APPROVAL_DATE(15),
	COMMITTEE_EMBARGO_APPROVAL_DATE(16),
	COMMITTEE_MEMBERS(17),
	COMMITTEE_CONTACT_EMAIL(18),
	
	DEGREE(19),
	DEGREE_LEVEL(20),
	COLLEGE(21),
	DEPARTMENT(21),
	MAJOR(22),
	
	EMBARGO_TYPE(23),
	DOCUMENT_TYPE(24),
	
	UMI_RELEASE(25),
	
	CUSTOM_ACTIONS(26),
	
	DEPOSIT_ID(27),
	
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
