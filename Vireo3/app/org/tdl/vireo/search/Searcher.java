package org.tdl.vireo.search;

import java.util.Iterator;
import java.util.List;

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
	 * Search for the ids of all submissions which match the parameters and
	 * order specified below. This method is useful for batch operations where
	 * you want to operate on the entire set but don't want to have all the
	 * objects in memory for the entire time. This way you can get the list and
	 * iterate through the list one at a time clearing out all the previous
	 * objects each time.
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
	public long[] submissionSearch(SearchFilter filter, SearchOrder orderBy,
			SearchDirection direction);

	/**
	 * Search for the ids of all action logs which match the parameters and
	 * order specified below. This method is useful for batch operations where
	 * you want to operate on the entire set but don't want to have all the
	 * objects in memory for the entire time. This way you can get the list and
	 * iterate through the list one at a time clearing out all the previous
	 * objects each time.
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
	public long[] actionLogSearch(SearchFilter filter, SearchOrder orderBy,
			SearchDirection direction);

}
