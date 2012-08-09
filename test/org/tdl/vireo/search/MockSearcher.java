package org.tdl.vireo.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.MockActionLog;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.model.Submission;

/**
 * Mock implementation of the searcher interface.
 * 
 * You will need to fill the submissions, and actionlogs, list with mock
 * implementations of things you want returned for queries regardless of the
 * filter presented.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockSearcher implements Searcher {

	// List of submissions to feed frome
	public List<MockSubmission> submissions = new ArrayList<MockSubmission>();

	// List of action logs to feed from
	public List<MockActionLog> actionLogs = new ArrayList<MockActionLog>();

	@Override
	public SearchResult<Submission> submissionSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit) {

		MockSearchResults<Submission> results = new MockSearchResults<Submission>();
		results.filter = filter;
		results.orderBy = orderBy;
		results.direction = direction;
		results.offset = offset;
		results.limit = limit;

		results.results = new ArrayList<Submission>();
		for (int i = offset; i < submissions.size(); i++)
			results.results.add(submissions.get(i));

		return results;
	}

	@Override
	public SearchResult<ActionLog> actionLogSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit) {

		MockSearchResults<ActionLog> results = new MockSearchResults<ActionLog>();
		results.filter = filter;
		results.orderBy = orderBy;
		results.direction = direction;
		results.offset = offset;
		results.limit = limit;

		results.results = new ArrayList<ActionLog>();
		for (int i = offset; i < actionLogs.size(); i++)
			results.results.add(actionLogs.get(i));

		return results;
	}

	@Override
	public Iterator<Submission> submissionSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction) {
		return (Iterator) submissions.iterator();
	}

	@Override
	public Iterator<ActionLog> actionLogSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction) {
		return (Iterator) actionLogs.iterator();
	}

}
