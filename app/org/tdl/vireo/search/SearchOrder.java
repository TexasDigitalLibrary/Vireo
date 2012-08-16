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
	
	STATE(4),
	
	ASSIGNEE(5),
	
	DOCUMENT_TITLE(6),
	DOCUMENT_ABSTRACT(7),
	DOCUMENT_KEYWORDS(8),

	PRIMARY_DOCUMENT(9),
	
	GRADUATION_DATE(10),
	SUBMISSION_DATE(11),
	LICENSE_AGREEMENT_DATE(12),
	APPROVAL_DATE(13),
	
	
	COMMITTEE_APPROVAL_DATE(14),
	COMMITTEE_EMBARGO_APPROVAL_DATE(15),
	COMMITTEE_MEMBERS(16),
	COMMITTEE_CONTACT_EMAIL(17),
	
	DEGREE(17),
	DEGREE_LEVEL(18),
	COLLEGE(19),
	DEPARTMENT(20),
	MAJOR(21),
	
	EMBARGO_TYPE(22),
	DOCUMENT_TYPE(23),
	
	UMI_RELEASE(24),
	
	CUSTOM_ACTIONS(25),
	
	DEPOSIT_ID(26),
	
	LAST_EVENT_ENTRY(27),
	LAST_EVENT_TIME(28);
	
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
