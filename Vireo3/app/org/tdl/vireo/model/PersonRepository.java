package org.tdl.vireo.model;

import java.util.List;

/**
 * The Vireo persistent repository for persons (aka users of various types).
 * This object follows the spring repository pattern, where this is the source
 * for creating and locating all persistent model objects. It is intended that
 * this object will be injected into all other spring beans that need access,
 * authenticate, or manage user information.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface PersonRepository {

	// //////////////
	// Person Model
	// //////////////

	/**
	 * Create a new person model. Either the first or last name must be present.
	 * 
	 * @param netId
	 *            The unique netid of the person.
	 * @param email
	 *            The unique email address of the person.
	 * @param firstName
	 *            The new person's first name.
	 * @param lastName
	 *            The new person's last name.
	 * @param role
	 *            The new person's role.
	 * @return A new person model.
	 */
	public Person createPerson(String netId, String email, String firstName,
			String lastName, RoleType role);

	/**
	 * Find a person based upon their unique id.
	 * 
	 * @param id
	 *            Person's id.
	 * @return The person object or null if not found.
	 */
	public Person findPerson(Long id);

	/**
	 * Find a person based upon their unique email address.
	 * 
	 * @param email
	 *            The person's email adress.
	 * @return The person object or null if not found.
	 */
	public Person findPersonByEmail(String email);

	/**
	 * Find a person based upon their unique netid.
	 * 
	 * @param netId
	 *            The person's netid
	 * @return The person object or null if not found.
	 */
	public Person findPersonByNetId(String netId);
	
	/**
	 * Find all persons with at least the provided role type. For example if
	 * there were three users A) an administrator, B) a reviewer, and C) a
	 * student. If queried for the role type of a reviewer then A and B would be
	 * returned. If queried for just the type administrator then only A would be
	 * returned.
	 * 
	 * @param type
	 *            The role type to querie for.
	 * @return A list of all persons which this role or higher.
	 */
	public List<Person> findPersonsByRole(RoleType type);
	
	
	/**
	 * Search for all people regardless of their role or state which match the
	 * provided query in either their name, or email address. Results may be
	 * paginated based upon the offset and limit provided.
	 * 
	 * @param query
	 *            The query to search for
	 * @param offset
	 *            How far into the search results
	 * @param limit
	 *            Limit the the maximum number of results.
	 * @return A list of persons that will be no larger than the maximum number
	 *         of results.
	 */
	public List<Person> searchPersons(String query,int offset, int limit);

	/**
	 * @return All person objects
	 */
	public List<Person> findAllPersons();
	
	/**
	 * Find how many persons are stored in the persistent repository.
	 * 
	 * @return the number total number of persons stored in the persistent
	 *         repository.
	 */
	public long findPersonsTotal();

	// ///////////////////////////
	// Personal Preference Model
	// ///////////////////////////

	/**
	 * Find a preference object
	 * 
	 * @param id
	 *            The unique id
	 * @return The preference object or null if not found.
	 */
	public Preference findPreference(Long id);

}
