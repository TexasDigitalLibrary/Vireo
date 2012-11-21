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
	DOCUMENT_LANGUAGE(11),
	
	PRIMARY_DOCUMENT(12),
	
	GRADUATION_DATE(13),
	SUBMISSION_DATE(14),
	LICENSE_AGREEMENT_DATE(15),
	APPROVAL_DATE(16),
	
	
	COMMITTEE_APPROVAL_DATE(17),
	COMMITTEE_EMBARGO_APPROVAL_DATE(18),
	COMMITTEE_MEMBERS(19),
	COMMITTEE_CONTACT_EMAIL(20),
	
	DEGREE(21),
	DEGREE_LEVEL(22),
	
	PROGRAM(23),
	COLLEGE(24),
	DEPARTMENT(25),
	MAJOR(26),
	
	EMBARGO_TYPE(27),
	DOCUMENT_TYPE(28),
	
	UMI_RELEASE(29),
	
	CUSTOM_ACTIONS(30),
	
	DEPOSIT_ID(31),
	
	LAST_EVENT_ENTRY(32),
	LAST_EVENT_TIME(33);
	
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
