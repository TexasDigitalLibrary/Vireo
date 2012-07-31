package org.tdl.vireo.deposit;

import java.net.URL;
import java.util.Map;

import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.SearchFilter;

/**
 * Deposit service.
 * 
 * This class handles all interaction with a remote repository for depositing
 * submissions into.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface Depositor {

	/**
	 * @return The technical spring bean name of this depositor implementation.
	 */
	public String getBeanName();

	/**
	 * @return The displayable name of this bean.
	 */
	public String getDisplayName();
	
	
	/**
	 * Deposit the package into the remote location. The resulting depositID
	 * assigned by the location should be returned.
	 * 
	 * @param location
	 *            The deposit location, a repository and collection. If all
	 *            values are not assigned then an IllegalArgument exception will
	 *            be thrown.
	 * @param depositPackage
	 *            The package to be deposited.
	 * @return The depositID
	 */
	public String deposit(DepositLocation location, DepositPackage depositPackage);

	/**
	 * Retrieve a map of collection names, and their technical URLs.
	 * 
	 * @param location
	 *            The repository location.
	 * @return A map of colleciton names & urls.
	 */
	public Map<String, URL> getCollections(DepositLocation location);

	/**
	 * Resolve the collection URL into a displayable name for the collection.
	 * 
	 * @param location
	 *            The deposit location.
	 * @param collectionURL
	 *            The collection URL to resolve. If the location has a
	 *            collection url specified it is ignored.
	 * 
	 * @return The displayable name of the collection.
	 */
	public String getCollectionName(DepositLocation location, URL collectionURL);

}
