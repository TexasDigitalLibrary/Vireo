package org.tdl.vireo.model;

import java.util.List;
import java.util.Set;

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
	 * Create a new person model.
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
	 * @return All person objects
	 */
	public List<Person> findAllPersons();

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
