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
	DOCUMENT_SUBJECTS(10),

	PRIMARY_DOCUMENT(11),
	
	GRADUATION_DATE(12),
	SUBMISSION_DATE(13),
	LICENSE_AGREEMENT_DATE(14),
	APPROVAL_DATE(15),
	
	
	COMMITTEE_APPROVAL_DATE(16),
	COMMITTEE_EMBARGO_APPROVAL_DATE(17),
	COMMITTEE_MEMBERS(18),
	COMMITTEE_CONTACT_EMAIL(19),
	
	DEGREE(20),
	DEGREE_LEVEL(21),
	COLLEGE(22),
	DEPARTMENT(23),
	MAJOR(24),
	
	EMBARGO_TYPE(25),
	DOCUMENT_TYPE(26),
	
	UMI_RELEASE(27),
	
	CUSTOM_ACTIONS(28),
	
	DEPOSIT_ID(29),
	
	LAST_EVENT_ENTRY(30),
	LAST_EVENT_TIME(31);
	
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
