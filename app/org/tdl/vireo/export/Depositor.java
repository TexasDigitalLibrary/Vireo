package org.tdl.vireo.export;

import java.util.Map;

import org.tdl.vireo.model.DepositLocation;

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
	 * @param exportPackage
	 *            The package to be deposited.
	 * @return The depositID
	 * @throws DepositException
	 */
	public String deposit(DepositLocation location, ExportPackage exportPackage);

	/**
	 * Retrieve a map of collection names, and their technical identifier.
	 * 
	 * @param location
	 *            The repository location.
	 * @return A map of collection names & internal identifiers.
	 * @throws DepositException
	 */
	public Map<String, String> getCollections(DepositLocation location);

	/**
	 * Resolve the collection identifier into a displayable name for the
	 * collection.
	 * 
	 * @param location
	 *            The deposit location.
	 * @param collection
	 *            The collection identifier to resolve.
	 * 
	 * @return The displayable name of the collection.
	 * @throws DepositException
	 */
	public String getCollectionName(DepositLocation location, String collection);

}
