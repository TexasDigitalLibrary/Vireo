package org.tdl.vireo.search.impl;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.SearchResult;
import org.tdl.vireo.search.Searcher;

/**
 * This iterator will efficiently loop through a lost list of search results.
 * Normally searches are paginated for individual display, however sometimes you
 * need to loop through more than what is feasible for a single page. This
 * iterator will use the standard paginated search methods in batches, allowing
 * the caller to iterate through them as if it were one long list of objects.
 * 
 * Another problem is efficiency. After the objects have been iterated past the
 * current window it will be detached from persistence storage.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 * @param <T>
 *            Either Submission or ActionLog
 */
public class SearchIteratorImpl<T extends AbstractModel> implements Iterator<T> {

	// How many objects to retrieved for each batch.
	public static int OBJECTS_PER_BATCH = 50;

	// Static state for the life of the iterator.
	public final Class type;
	public final Searcher searcher;
	public final SearchFilter filter;
	public final SearchOrder orderBy;
	public final SearchDirection direction;
	
	// Dynamic state of the iterator.
	public int offset;
	public int currentPointer;
	public List<T> retrieved;
	
	/**
	 * Construct a new search iterator.
	 * 
	 * @param type The type of object being iterated. I wish we could get this from java, but unfortunately it has to be passed in directly.
	 * @param searcher The searcher implementation to use.
	 * @param filter The filter
	 * @param orderBy The sort column.
	 * @param direction The sort direction.
	 */
	public SearchIteratorImpl(Class type, Searcher searcher, SearchFilter filter, SearchOrder orderBy, SearchDirection direction) {
		this.type = type;
		this.searcher = searcher;
		this.filter = filter;
		this.orderBy = orderBy;
		this.direction = direction;
		
		this.offset = 0;
		this.currentPointer = 0;
		this.retrieved = null;
		loadNextBatch();
	}
	
	/**
	 * Load the next batch. This will detach any objects from the current batch,
	 * and query the searcher for the next set of objects.
	 */
	private void loadNextBatch() {

		if (retrieved != null)
			for (T model : retrieved)
				model.detach();

		SearchResult<T> results = null;
		if (Submission.class.equals(type)) {
			results = (SearchResult<T>) searcher.submissionSearch(filter, orderBy, direction, offset, OBJECTS_PER_BATCH);
		} else if (ActionLog.class.equals(type)) {
			results = (SearchResult<T>) searcher.actionLogSearch(filter, orderBy, direction, offset, OBJECTS_PER_BATCH);
		}
		
		retrieved = results.getResults();
		offset = offset + retrieved.size();
		currentPointer = 0;
	}
	
	
	@Override
	public boolean hasNext() {
		
		if (currentPointer >= retrieved.size()) {
			loadNextBatch();
		}
		
		if (currentPointer < retrieved.size()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public T next() {
		if (currentPointer >= retrieved.size()) {
			loadNextBatch();
		}
		
		if (currentPointer < retrieved.size()) {
			// We have it loaded, so return it.
			return retrieved.get(currentPointer++); 
		} else {
			return null;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("The LuceneSearchIterator does not support removing objects.");
	}

}
