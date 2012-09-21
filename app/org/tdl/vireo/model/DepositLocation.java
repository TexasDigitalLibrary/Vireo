package org.tdl.vireo.model;

import org.tdl.vireo.export.Depositor;
import org.tdl.vireo.export.Packager;

/**
 * A pre-configured deposit location. While technically this may mean deposit
 * into anything, the design use-cases are for sword deposit into a repository
 * such as DSpace or Fedora.
 * 
 * This object contains all the connection information required to deposit,
 * along with the specific implementations that should be used to make that
 * happen.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface DepositLocation extends AbstractOrderedModel {

	/**
	 * @return The name of this deposit location.
	 */
	public String getName();

	/**
	 * @param name
	 *            The new name of this deposit location. May not be null.
	 */
	public void setName(String name);

	/**
	 * @return The location of the repository, this will likely be a URI.
	 */
	public String getRepository();

	/**
	 * @param location
	 *            The new location of the repository. This will likely be a URI.
	 */
	public void setRepository(String location);

	/**
	 * @return The collection id or location within the designated repository. This will likely be a URI.
	 */
	public String getCollection();

	/**
	 * @param location
	 *            The new collection id or location within the designated
	 *            repository. This will likely be a URI.
	 */
	public void setCollection(String location);

	/**
	 * @return The authentication username, null if no authentication is
	 *         required.
	 */
	public String getUsername();

	/**
	 * @param username
	 *            The new username.
	 */
	public void setUsername(String username);

	/**
	 * @return The authentication password, only used if the username is not
	 *         null.
	 */
	public String getPassword();

	/**
	 * @param password
	 *            The new password.
	 */
	public void setPassword(String password);

	/**
	 * @return The onBehalfOf user, a string identifying a user within the
	 *         repository that the deposit should occur on behalf of. Null if no
	 *         onBehalfOf user is specified.
	 */
	public String getOnBehalfOf();

	/**
	 * @param onBehalfOf
	 *            The new onBehalfOf User
	 */
	public void setOnBehalfOf(String onBehalfOf);

	/**
	 * @return The packager implementation who is responsible for generating
	 *         depositable packages.
	 */
	public Packager getPackager();

	/**
	 * @param packager
	 *            The new packager implementation.
	 */
	public void setPackager(Packager packager);

	/**
	 * @return The depositor implementation who is responsible for depositing
	 *         the generated package.
	 */
	public Depositor getDepositor();

	/**
	 * @param depositor
	 *            The new depositor implementation
	 */
	public void setDepositor(Depositor depositor);
}
