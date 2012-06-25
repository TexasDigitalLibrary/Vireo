package org.tdl.vireo.search;

import java.util.List;

import org.tdl.vireo.model.AbstractModel;

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
	
	
	/**
	 * Return a list of pagination calculations to aid in displaying the
	 * pagination links below a table listing.
	 * 
	 * The list returned will include an entry for each pagination option with
	 * the current result in the center of the window. Thus if the window is 10
	 * entries, then the current page would be #5 with four entry before, and 5
	 * entries following. However if the active page was such that there could
	 * not be five entries before then the scale would be increased so that the
	 * remaining were added to the end.
	 * 
	 * @param windowSize
	 *            How many pagination entries to include surrounding the
	 *            currently active entry.
	 * @return A list of pagination entries.
	 */
	public List<Pagination> getPagination(int windowSize);
	
	
	/**
	 * A simple class to contain pagination entries.
	 */
	public static class Pagination {
		
		// The displayable page number for this entry.
		public final int pageNumber;
		
		// The offset that will generate this page.
		public final int offset;
		
		// Weather this entry is the currently active entry for this search result.
		public final boolean current;
		
		
		/**
		 * Construct a new pagination entry.
		 * 
		 * @param pageNumber
		 *            The displayable page number of this entry.
		 * @param offset
		 *            The technical offset for this page.
		 * @param current
		 *            Weather this is the current entry or not.
		 */
		public Pagination(int pageNumber, int offset, boolean current) {
			this.pageNumber = pageNumber;
			this.offset = offset;
			this.current = current;
		}
	
	};
}
