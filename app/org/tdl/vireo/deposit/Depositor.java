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
	 * Deposit one submission and if successfully change to the published state.
	 * 
	 * The deposit operation will occur in a separate thread and could
	 * potentially take a substantial amount of time depending on how responsive
	 * the remote server is and the size of the submission.
	 * 
	 * As many errors as possible will be checked before separating into a
	 * separate thread as possible. I.e. the location will be checked for all
	 * the required attribute, the state of the submission object etc. However
	 * some errors may not occur until deposit time. When these errors occur the
	 * submission will remain in it's current state and an action log item will
	 * be generated detailing the error encountered.
	 * 
	 * @param location
	 *            The deposit location where this submission should be deposited
	 *            into. The location must be complete, containing a repository
	 *            URL, collection URL, and packager. It is also unwise if the
	 *            depositor is different than this implementation.
	 * 
	 * @param submission
	 *            The single submission to deposit.
	 * 
	 */
	public void deposit(DepositLocation location, Submission submission);

	/**
	 * Deposit a batch of submissions and for each successful deposit change to
	 * the published state.
	 * 
	 * The deposit operation will occur in a separate thread that will likely
	 * take a substantial amount of time.
	 * 
	 * As many errors as possible will be checked before separating into a
	 * separate thread as possible. I.e. the location will be checked for all
	 * the required attribute, the state of the submission object etc. However
	 * some errors may not occur until deposit time. When these errors occur the
	 * submission will remain in it's current state and an action log item will
	 * be generated detailing the error encountered.
	 * 
	 * @param location
	 *            The deposit location where this submission should be deposited
	 *            into. The location must be complete, containing a repository
	 *            URL, collection URL, and packager. It is also unwise if the
	 *            depositor is different than this implementation.
	 * @param filter
	 *            A search filter of all submissions which should be deposited.
	 */
	public void deposit(DepositLocation location, SearchFilter filter);

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
