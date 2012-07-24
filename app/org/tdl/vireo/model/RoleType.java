package org.tdl.vireo.model;

/**
 * The possible roles a person may be within vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum RoleType {
	NONE(0),
	STUDENT(1),
	REVIEWER(2),
	MANAGER(3),
	ADMINISTRATOR(4);
	
	
	
	// The id for this Role Type.
	private int id;

	/**
	 * Private constructor for the defined role types listed above.
	 * 
	 * @param id
	 *            The id of the degree level.
	 */
	private RoleType(int id) {
		this.id = id;
	}

	/**
	 * @return The id of this role.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Locate a role type based upon it's id.
	 * 
	 * @param id
	 *            The id of the desired role.
	 * @return The role, or null if not found.
	 */
	public static RoleType find(int id) {

		for (RoleType role : RoleType.values()) {
			if (role.id == id)
				return role;
		}

		return null;
	}
}
