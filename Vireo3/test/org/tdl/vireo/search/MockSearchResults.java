package org.tdl.vireo.search;

import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.model.AbstractModel;

/**
 * Mock implementation of the search results interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockSearchResults<T extends AbstractModel> implements
		SearchResult<T> {

	public SearchFilter filter;
	public SearchDirection direction;
	public SearchOrder orderBy;
	public int offset;
	public int limit;
	public List<T> results = new ArrayList<T>();
	public int total;

	@Override
	public SearchFilter getFilter() {
		return filter;
	}

	@Override
	public SearchDirection getDirection() {
		return direction;
	}

	@Override
	public SearchOrder getOrderBy() {
		return orderBy;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public List<T> getResults() {
		return results;
	}

	@Override
	public int getTotal() {
		return total;
	}

	@Override
	public List<org.tdl.vireo.search.SearchResult.Pagination> getPagination(
			int windowSize) {

		List<SearchResult.Pagination> pagination = new ArrayList<SearchResult.Pagination>();
		pagination
				.add(new SearchResult.Pagination(total / limit, offset, true));
		return pagination;
	}

}
