package org.tdl.vireo.search;

import java.util.Iterator;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Submission;

/**
 * Search the index.
 * 
 * The purpose of this interface is to allow the application to search through
 * the mountain of submissions and actionlogs within the system to quickly
 * display results for the user. This interface's implementation is typically
 * paired with the Indexer to provide a robust method of indexing and searching
 * vireo.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public interface Searcher {

	/**
	 * Search for all submissions which match the parameters and order specified
	 * below.
	 * 
	 * @param filter
	 *            The filter parameters describing which submissions should be
	 *            included.
	 * @param orderBy
	 *            How the submissions should be ordered.
	 * @param direction
	 *            The direction of the order.
	 * @param offset
	 *            The pagination offset.
	 * @param limit
	 *            The pagination limit of results per page.
	 * @return The submission results object.
	 */
	public SearchResult<Submission> submissionSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit);

	/**
	 * Search for all action logs which match the parameters and order specified
	 * below.
	 * 
	 * @param filter
	 *            The filter parameters describing which log items should be
	 *            included.
	 * @param orderBy
	 *            How the logs items should be ordered.
	 * @param direction
	 *            The direction of the order.
	 * @param offset
	 *            The pagination offset.
	 * @param limit
	 *            The pagination limit of results per page.
	 * @return The action log results object.
	 */
	public SearchResult<ActionLog> actionLogSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit);
	
	
	/**
	 * Search for all submissions which match the parameters and order specified
	 * below. The iterator returned will go through all submissions which match
	 * the search filter. Submissions from previous iterations may be detached
	 * from the persistence manager to increase memory efficiency, so it is best
	 * not to keep those references around in memory.
	 * 
	 * @param filter
	 *            The filter parameters describing which submissions should be
	 *            included.
	 * @param orderBy
	 *            How the submissions should be ordered.
	 * @param direction
	 *            The direction of the order.
	 * @return The submission results object.
	 */
	public Iterator<Submission> submissionSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction);

	/**
	 * Search for all action logs which match the parameters and order specified
	 * below. The iterator returned will go through all action logs which match
	 * the search filter. Action logs from previous iterations may be detached
	 * from the persistence manager to increase memory efficiency, so it is best
	 * not to keep those references around in memory.
	 * 
	 * @param filter
	 *            The filter parameters describing which log items should be
	 *            included.
	 * @param orderBy
	 *            How the logs items should be ordered.
	 * @param direction
	 *            The direction of the order.
	 * @return The action log results object.
	 */
	public Iterator<ActionLog> actionLogSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction);

}
