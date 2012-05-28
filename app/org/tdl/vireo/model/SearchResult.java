package org.tdl.vireo.model;

import java.util.List;

/**
 * The result of a filter search. This records the parameters that produced the
 * search, along with the results. From this object you can obtain both the list
 * of objects, and the total number of objects that would have been returned if
 * there was no limits placed upon the query.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 * @param <T>
 *            The type of result.
 */
public interface SearchResult<T extends AbstractModel> {

	/**
	 * @return The filter that produced this search result.
	 */
	public SearchFilter getFilter();

	/**
	 * @return The order by direction that ordered the results.
	 */
	public SearchDirection getDirection();

	/**
	 * @return The order by column that ordered the result.
	 */
	public SearchOrder getOrderBy();

	/**
	 * @return The offset into the list.
	 */
	public int getOffset();

	/**
	 * @return The number of object returned.
	 */
	public int getLimit();

	/**
	 * @return An ordered list of results.
	 */
	public List<T> getResults();

	/**
	 * @return The total number of objects matched if the query did not have an
	 *         offset or limit in place.
	 */
	public int getTotal();

}
