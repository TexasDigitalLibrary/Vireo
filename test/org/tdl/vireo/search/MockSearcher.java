package org.tdl.vireo.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.MockActionLog;
import org.tdl.vireo.model.MockSubmission;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;

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
	
	// Mock repository to pair with Mock Searcher
	public MockSubmissionRepository subRepo = new MockSubmissionRepository();

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

//	@Override
//	public Iterator<Submission> submissionSearch(SearchFilter filter,
//			SearchOrder orderBy, SearchDirection direction) {
//		return (Iterator) submissions.iterator();
//	}
//
//	@Override
//	public Iterator<ActionLog> actionLogSearch(SearchFilter filter,
//			SearchOrder orderBy, SearchDirection direction) {
//		return (Iterator) actionLogs.iterator();
//	}
	
	@Override
	public long[] submissionSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction) {
		
		long[] sortedIds = new long[submissions.size()];
		for (int i = 0; i < submissions.size(); i++) {
			sortedIds[i] = submissions.get(i).getId();
		}
		
		return sortedIds;
	}

	@Override
	public long[] actionLogSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction) {
			
		long[] sortedIds = new long[actionLogs.size()];
		for (int i = 0; i < actionLogs.size(); i++) {
			sortedIds[i] = actionLogs.get(i).getId();
		}
		
		return sortedIds;
	}
	
	/**
	 * Mock repository that can be paired with this searcher to retrieve the
	 * mock submissions.
	 * 
	 */
	public class MockSubmissionRepository implements SubmissionRepository {

		@Override
		public Submission createSubmission(Person submitter) {
			return null;
		}

		@Override
		public Submission findSubmission(Long id) {
			
			for (MockSubmission sub : submissions) {
				if (id.equals(sub.getId()))
				 return sub;
			}
			
			return null;
		}

		@Override
		public List<Submission> findSubmissions(List<Long> submissionIds) {
			return null;
		}

		@Override
		public Submission findSubmissionByEmailHash(String emailHash) {
			return null;
		}

		@Override
		public List<Submission> findSubmission(Person Submitter) {
			return null;
		}

		@Override
		public Iterator<Submission> findAllSubmissions() {
			return null;
		}

		@Override
		public long findSubmissionsTotal() {
			return 0;
		}

		@Override
		public List<Semester> findAllGraduationSemesters() {
			return null;
		}

		@Override
		public List<Integer> findAllSubmissionYears() {
			return null;
		}

		@Override
		public List<String> findAllColleges() {
			return null;
		}

		@Override
		public List<String> findAllDepartments() {
			return null;
		}

		@Override
		public List<String> findAllMajors() {
			return null;
		}

		@Override
		public Attachment findAttachment(Long id) {
			return null;
		}

		@Override
		public CommitteeMember findCommitteeMember(Long id) {
			return null;
		}

		@Override
		public CustomActionValue findCustomActionValue(Long id) {
			return null;
		}

		@Override
		public ActionLog findActionLog(Long id) {
			for (MockActionLog log : actionLogs) {
				if (id.equals(log.getId()))
				 return log;
			}
			
			return null;
		}

		@Override
		public List<ActionLog> findActionLogs(List<Long> logIds) {
			return null;
		}

		@Override
		public List<ActionLog> findActionLog(Submission submission) {
			return null;
		}

		@Override
		public Iterator<ActionLog> findAllActionLogs() {
			return null;
		}

		@Override
		public long findActionLogsTotal() {
			return 0;
		}

		@Override
		public NamedSearchFilter createSearchFilter(Person creator, String name) {
			return null;
		}

		@Override
		public NamedSearchFilter findSearchFilter(Long id) {
			return null;
		}

		@Override
		public List<NamedSearchFilter> findSearchFiltersByCreatorOrPublic(
				Person creator) {
			return null;
		}

		@Override
		public NamedSearchFilter findSearchFilterByCreatorAndName(
				Person creator, String name) {
			return null;
		}

		@Override
		public List<NamedSearchFilter> findAllSearchFilters() {
			return null;
		}
		
	}

}
