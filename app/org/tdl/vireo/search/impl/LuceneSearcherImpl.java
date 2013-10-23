package org.tdl.vireo.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.NumericUtils;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.SearchResult;
import org.tdl.vireo.search.Searcher;
import org.tdl.vireo.search.Semester;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.Logger;

/**
 * Lucene implementation of the Searcher interface.
 * 
 * This implementation is directly tied to the LuceneIndexerImpl, in fact it
 * requires it as a spring-based dependency.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class LuceneSearcherImpl implements Searcher {

	// Static constants
	public static String[] SORT_SUB_FIELDS = new String[SearchOrder.values().length];
	public static String[] SORT_LOG_FIELDS = new String[SearchOrder.values().length];
	public static int[] SORT_TYPES = new int[SearchOrder.values().length];
	{
		// Sort fields for submissions
		SORT_SUB_FIELDS[SearchOrder.ID.ordinal()] = "subId";
		SORT_SUB_FIELDS[SearchOrder.STUDENT_EMAIL.ordinal()] = "studentEmail";
		SORT_SUB_FIELDS[SearchOrder.STUDENT_NAME.ordinal()] = "studentName";
		SORT_SUB_FIELDS[SearchOrder.STUDENT_ID.ordinal()] = "institutionalIdentifier";
		SORT_SUB_FIELDS[SearchOrder.STATE.ordinal()] = "state";
		SORT_SUB_FIELDS[SearchOrder.ASSIGNEE.ordinal()] = "sortAssigned";
		SORT_SUB_FIELDS[SearchOrder.DOCUMENT_TITLE.ordinal()] = "documentTitle";
		SORT_SUB_FIELDS[SearchOrder.DOCUMENT_ABSTRACT.ordinal()] = "documentAbstract";
		SORT_SUB_FIELDS[SearchOrder.DOCUMENT_KEYWORDS.ordinal()] = "documentKeywords";
		SORT_SUB_FIELDS[SearchOrder.DOCUMENT_SUBJECTS.ordinal()] = "documentSubjects";
		SORT_SUB_FIELDS[SearchOrder.DOCUMENT_LANGUAGE.ordinal()] = "documentLanguage";
		SORT_SUB_FIELDS[SearchOrder.PUBLISHED_MATERIAL.ordinal()] = "publishedMaterial";
		SORT_SUB_FIELDS[SearchOrder.PRIMARY_DOCUMENT.ordinal()] = "primaryDocument";
		SORT_SUB_FIELDS[SearchOrder.GRADUATION_DATE.ordinal()] = "graduationSemester";
		SORT_SUB_FIELDS[SearchOrder.DEFENSE_DATE.ordinal()] = "defenseDate";
		SORT_SUB_FIELDS[SearchOrder.SUBMISSION_DATE.ordinal()] = "submissionDate";
		SORT_SUB_FIELDS[SearchOrder.LICENSE_AGREEMENT_DATE.ordinal()] = "licenseAgreementDate";
		SORT_SUB_FIELDS[SearchOrder.APPROVAL_DATE.ordinal()] = "approvalDate";
		SORT_SUB_FIELDS[SearchOrder.COMMITTEE_APPROVAL_DATE.ordinal()] = "committeeApprovalDate";
		SORT_SUB_FIELDS[SearchOrder.COMMITTEE_EMBARGO_APPROVAL_DATE.ordinal()] = "committeeEmbargoApprovalDate";
		SORT_SUB_FIELDS[SearchOrder.COMMITTEE_MEMBERS.ordinal()] = "committeeMembers";
		SORT_SUB_FIELDS[SearchOrder.COMMITTEE_CONTACT_EMAIL.ordinal()] = "committeeContactEmail";
		SORT_SUB_FIELDS[SearchOrder.DEGREE.ordinal()] = "degree";
		SORT_SUB_FIELDS[SearchOrder.DEGREE_LEVEL.ordinal()] = "degreeLevel";
		SORT_SUB_FIELDS[SearchOrder.PROGRAM.ordinal()] = "program";
		SORT_SUB_FIELDS[SearchOrder.COLLEGE.ordinal()] = "college";
		SORT_SUB_FIELDS[SearchOrder.DEPARTMENT.ordinal()] = "department";
		SORT_SUB_FIELDS[SearchOrder.MAJOR.ordinal()] = "major";
		SORT_SUB_FIELDS[SearchOrder.EMBARGO_TYPE.ordinal()] = "embargo";
		SORT_SUB_FIELDS[SearchOrder.DOCUMENT_TYPE.ordinal()] = "documentType";
		SORT_SUB_FIELDS[SearchOrder.UMI_RELEASE.ordinal()] = "umiRelease";
		SORT_SUB_FIELDS[SearchOrder.CUSTOM_ACTIONS.ordinal()] = "customActions";
		SORT_SUB_FIELDS[SearchOrder.DEPOSIT_ID.ordinal()] = "depositId";
		SORT_SUB_FIELDS[SearchOrder.REVIEWER_NOTES.ordinal()] = "reviewerNotes";
		SORT_SUB_FIELDS[SearchOrder.LAST_EVENT_ENTRY.ordinal()] = "lastEventEntry";
		SORT_SUB_FIELDS[SearchOrder.LAST_EVENT_TIME.ordinal()] = "lastEventTime";
		SORT_SUB_FIELDS[SearchOrder.ORCID.ordinal()] = "orcid";
		
		// Sort fields for action logs
		for (int i=0; i < SearchOrder.values().length; i++) 
			SORT_LOG_FIELDS[i] = SORT_SUB_FIELDS[i];
		SORT_LOG_FIELDS[SearchOrder.ID.ordinal()] = "logId";

		// Sort types for both submissions and action logs
		for (int i=0; i < SearchOrder.values().length; i++) 
			SORT_TYPES[i] = SortField.STRING;
		SORT_TYPES[SearchOrder.ID.ordinal()] = SortField.LONG;
		SORT_TYPES[SearchOrder.GRADUATION_DATE.ordinal()] = SortField.LONG;
		SORT_TYPES[SearchOrder.DEFENSE_DATE.ordinal()] = SortField.LONG;
		SORT_TYPES[SearchOrder.SUBMISSION_DATE.ordinal()] = SortField.LONG;
		SORT_TYPES[SearchOrder.LICENSE_AGREEMENT_DATE.ordinal()] = SortField.LONG;
		SORT_TYPES[SearchOrder.APPROVAL_DATE.ordinal()] = SortField.LONG;
		SORT_TYPES[SearchOrder.COMMITTEE_APPROVAL_DATE.ordinal()] = SortField.LONG;
		SORT_TYPES[SearchOrder.COMMITTEE_EMBARGO_APPROVAL_DATE.ordinal()] = SortField.LONG;
		SORT_TYPES[SearchOrder.CUSTOM_ACTIONS.ordinal()] = SortField.INT;
		SORT_TYPES[SearchOrder.LAST_EVENT_TIME.ordinal()] = SortField.LONG;
	}
	
	// Spring dependencies
	public LuceneIndexerImpl indexer = null;
	public SubmissionRepository subRepo = null;
	public StateManager stateManager = null;
	
	/**
	 * Spring injection for the LuceneIndexerImpl. Note that this implementation
	 * is tied directly to the indexer implementation, that is why the datatype
	 * is what it is.
	 * 
	 * @param indexer
	 *            The lucene indexer.
	 */
	public void setLuceneIndexerImpl(LuceneIndexerImpl indexer) {
		this.indexer = indexer;
	}

	/**
	 * @param subRepo
	 *            The submission repository
	 */
	public void setSubmissionRepository(SubmissionRepository subRepo) {
		this.subRepo = subRepo;
	}

	/**
	 * @param stateManager
	 *            The state manager (used to resolve state names into display
	 *            names)
	 */
	public void setStateManager(StateManager stateManager) {
		this.stateManager = stateManager;
	}
	
	@Override
	public SearchResult<Submission> submissionSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit) {
		try {
			IndexReader reader = IndexReader.open(indexer.index);
			try {
				IndexSearcher searcher = new IndexSearcher(reader);
				
				
				BooleanQuery andQuery = new BooleanQuery();
				andQuery.add(new TermQuery(new Term("type","submission")),Occur.MUST);
				buildQuery(andQuery,filter,true); // <-- This does most of the work.
				
				boolean reverse = (direction == SearchDirection.ASCENDING) ? false : true;
				
				SortField dynamicSortField = new SortField(SORT_SUB_FIELDS[orderBy.ordinal()], SORT_TYPES[orderBy.ordinal()], reverse);
				SortField idSortField = new SortField("subId",SortField.LONG,reverse);
				Sort sort = new Sort(dynamicSortField, idSortField);
		
				Logger.debug("Submission Query: "+andQuery.toString());
				
				// Run the search
				TopDocs topDocs = searcher.search(andQuery, offset + limit, sort);
								
				List<Long> sortedIds = new ArrayList<Long>();
				for(int i=offset; i < offset + limit ; i++) {
					if (i >= topDocs.scoreDocs.length )
						break;
					Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
					
					sortedIds.add(Long.valueOf(doc.get("subId")));
				}
				
				List<Submission> results = subRepo.findSubmissions(sortedIds);
				Collections.sort(results,new ModelComparator(sortedIds));
				
				return new LuceneSearchResults<Submission>(filter, direction, orderBy, offset, limit, results, topDocs.totalHits);
			} finally {
				reader.close();
			}
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to search");
		}
		return null;
	}

	@Override
	public SearchResult<ActionLog> actionLogSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit) {
		
		try {
			IndexReader reader = IndexReader.open(indexer.index);
			try {
				IndexSearcher searcher = new IndexSearcher(reader);
				
				
				BooleanQuery andQuery = new BooleanQuery();
				andQuery.add(new TermQuery(new Term("type","actionlog")),Occur.MUST);
				buildQuery(andQuery,filter,false); // <-- This does most of the work.
				
				boolean reverse = (direction == SearchDirection.ASCENDING) ? false : true;
				
				SortField dynamicSortField = new SortField(SORT_LOG_FIELDS[orderBy.ordinal()], SORT_TYPES[orderBy.ordinal()], reverse);
				SortField idSortField = new SortField("logId",SortField.LONG,reverse);
				Sort sort = new Sort(dynamicSortField, idSortField);
				
				Logger.debug("Log Query: "+andQuery.toString());
				
				// Run the search
				TopDocs topDocs = searcher.search(andQuery, offset + limit, sort);
				
				List<Long> sortedIds = new ArrayList<Long>();
				for(int i=offset; i < offset + limit ; i++) {
					if (i >= topDocs.scoreDocs.length )
						break;
					Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
					
					sortedIds.add(Long.valueOf(doc.get("logId")));
				}
				
				List<ActionLog> results = subRepo.findActionLogs(sortedIds);
				Collections.sort(results,new ModelComparator(sortedIds));

				return new LuceneSearchResults<ActionLog>(filter, direction, orderBy, offset, limit, results, topDocs.totalHits);
			} finally {
				reader.close();
			}
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to search");
		}
		return null;
	}
	
	@Override
	public long[] submissionSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction) {
		
		try {
			IndexReader reader = IndexReader.open(indexer.index);
			try {
				IndexSearcher searcher = new IndexSearcher(reader);
				
				BooleanQuery andQuery = new BooleanQuery();
				andQuery.add(new TermQuery(new Term("type","submission")),Occur.MUST);
				buildQuery(andQuery,filter,true); // <-- This does most of the work.
				
				boolean reverse = (direction == SearchDirection.ASCENDING) ? false : true;
				
				SortField dynamicSortField = new SortField(SORT_SUB_FIELDS[orderBy.ordinal()], SORT_TYPES[orderBy.ordinal()], reverse);
				SortField idSortField = new SortField("subId",SortField.LONG,reverse);
				Sort sort = new Sort(dynamicSortField, idSortField);
		
				Logger.debug("Submission ID Query: "+andQuery.toString());
				
				TopDocs topDocs = searcher.search(andQuery, Integer.MAX_VALUE, sort);
								
				long[] sortedIds = new long[topDocs.scoreDocs.length];
				for (int i = 0; i < topDocs.scoreDocs.length; i++) {
					Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
					sortedIds[i] = Long.valueOf(doc.get("subId")).longValue();
				}

				return sortedIds;
				
			} finally {
				reader.close();
				
			}
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to search");
		}
		return null;
	}

	@Override
	public long[] actionLogSearch(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction) {
		
		try {
			IndexReader reader = IndexReader.open(indexer.index);
			try {
				IndexSearcher searcher = new IndexSearcher(reader);
				
				BooleanQuery andQuery = new BooleanQuery();
				andQuery.add(new TermQuery(new Term("type","actionlog")),Occur.MUST);
				buildQuery(andQuery,filter,true); // <-- This does most of the work.
				
				boolean reverse = (direction == SearchDirection.ASCENDING) ? false : true;
				
				SortField dynamicSortField = new SortField(SORT_SUB_FIELDS[orderBy.ordinal()], SORT_TYPES[orderBy.ordinal()], reverse);
				SortField idSortField = new SortField("logId",SortField.LONG,reverse);
				Sort sort = new Sort(dynamicSortField, idSortField);
		
				Logger.debug("Log ID Query: "+andQuery.toString());
				
				TopDocs topDocs = searcher.search(andQuery, Integer.MAX_VALUE, sort);
								
				long[] sortedIds = new long[topDocs.scoreDocs.length];
				for (int i = 0; i < topDocs.scoreDocs.length; i++) {
					Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
					sortedIds[i] = Long.valueOf(doc.get("logId")).longValue();
				}

				return sortedIds;
				
			} finally {
				reader.close();
				
			}
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to search");
		}
		return null;
	}

	
	
	
	
	
	/**
	 * This method produces the common part of the query handle the filter search clauses. 
	 * 
	 * @param andQuery The existing and-based query
	 * @param filter The filter search paramaters.
	 * @param submissions Whether this is for submissions or action logs
	 */
	public void buildQuery(BooleanQuery andQuery, SearchFilter filter, boolean submissions) {
		QueryParser parser = new QueryParser(indexer.version,"searchText",indexer.standardAnalyzer);
		
		// Include Submission filter
		if (filter.getIncludedSubmissions().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(Submission sub : filter.getIncludedSubmissions()) {
				orQuery.add(new TermQuery(new Term("subId", NumericUtils.longToPrefixCoded(sub.getId()))), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}
		
		// Include Log filter
		if (filter.getIncludedActionLogs().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(ActionLog log : filter.getIncludedActionLogs()) {
				orQuery.add(new TermQuery(new Term("logId", NumericUtils.longToPrefixCoded(log.getId()))), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}
		
		// Exclude Submission filter
		for(Submission sub : filter.getExcludedSubmissions()) {
			andQuery.add(new TermQuery(new Term("subId", NumericUtils.longToPrefixCoded(sub.getId()))), Occur.MUST_NOT);
		}
		
		// Exclude Log filter
		for(ActionLog log : filter.getExcludedActionLogs()) {
			andQuery.add(new TermQuery(new Term("logId", NumericUtils.longToPrefixCoded(log.getId()))), Occur.MUST_NOT);
		}
		
		// Search Text Filter
		if (filter.getSearchText().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(String searchText : filter.getSearchText()) {
				try {
					// First try to interpret it as a complex lucene search string.
					orQuery.add(parser.parse(searchText),Occur.SHOULD);
				} catch (ParseException e) {
					// If that fails just fall back to a term query.
					orQuery.add(new TermQuery(new Term("searchText", searchText)), Occur.SHOULD);
				}
			}
			andQuery.add(orQuery,Occur.MUST);
		}
	
		// State Filter
		if (filter.getStates().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(String stateName : filter.getStates()) {
				State state = stateManager.getState(stateName);
				orQuery.add(new TermQuery(new Term("state", state.getDisplayName())), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}
		
		// Assignee Filter
		if (filter.getAssignees().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(Person assignee : filter.getAssignees()) {
				
				long assigneeId = 0;
				if (assignee != null)
					assigneeId = assignee.getId();
				
				orQuery.add(new TermQuery(new Term("searchAssigned", NumericUtils.longToPrefixCoded(assigneeId))), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}
		
		// Embargo Filter
		if (filter.getEmbargoTypes().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(EmbargoType embargo : filter.getEmbargoTypes()) {
				orQuery.add(new TermQuery(new Term("embargo", embargo.getName())), Occur.SHOULD);
			}			
			andQuery.add(orQuery,Occur.MUST);
		}
		
		// Graduation Semester Filter
		if (filter.getGraduationSemesters().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(Semester semester : filter.getGraduationSemesters()) {
				
				// We can't index it if it dosn't have a date.
				if (semester.year == null)
					continue;
				
				Calendar cal = Calendar.getInstance();
				cal.clear();
				cal.set(Calendar.YEAR, semester.year);
				if (semester.month != null) {
					cal.set(Calendar.MONTH,semester.month);
				}
				orQuery.add(new TermQuery(new Term("graduationSemester", NumericUtils.longToPrefixCoded(cal.getTimeInMillis()))), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}
				
		// Degree Filter
		if (filter.getDegrees().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(String degree : filter.getDegrees()) {
				orQuery.add(new TermQuery(new Term("degree", degree)), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}
		
		// Department Filter
		if (filter.getDepartments().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(String dept : filter.getDepartments()) {
				orQuery.add(new TermQuery(new Term("department", dept)), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}
		
		// Program Filter
		if (filter.getPrograms().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(String program : filter.getPrograms()) {
				orQuery.add(new TermQuery(new Term("program", program)), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}
				
		// College Filter
		if (filter.getColleges().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(String college : filter.getColleges()) {
				orQuery.add(new TermQuery(new Term("college", college)), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}		
		
		// Major Filter
		if (filter.getMajors().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(String major : filter.getMajors()) {
				orQuery.add(new TermQuery(new Term("major", major)), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}
		
		// Document Type Filter
		if (filter.getDocumentTypes().size() > 0) {
			BooleanQuery orQuery = new BooleanQuery();
			for(String docType : filter.getDocumentTypes()) {
				orQuery.add(new TermQuery(new Term("documentType", docType)), Occur.SHOULD);
			}
			andQuery.add(orQuery,Occur.MUST);
		}
		
		// UMI Release Filter
		if (filter.getUMIRelease() != null) {
			if (filter.getUMIRelease()) {
				andQuery.add(new TermQuery(new Term("umiRelease","yes")),Occur.MUST);
			} else {
				andQuery.add(new TermQuery(new Term("umiRelease","no")),Occur.MUST);
			}
		}
		
		// Date Range Filter
		if (filter.getDateRangeStart() != null || filter.getDateRangeEnd() != null) {
			
			long startTime = 0;
			if (filter.getDateRangeStart() != null)
				startTime = filter.getDateRangeStart().getTime();
			
			long endTime = Long.MAX_VALUE;
			if (filter.getDateRangeEnd() != null)
				endTime = filter.getDateRangeEnd().getTime();
			
			if (submissions)
				andQuery.add(NumericRangeQuery.newLongRange("submissionDate", startTime, endTime, true,true),Occur.MUST);
			else
				andQuery.add(NumericRangeQuery.newLongRange("lastEventTime", startTime, endTime, true,true),Occur.MUST);

		}
	}
	
	/**
	 * Search results object.
	 * 
	 * This method encapsulates the result of a search query for a particular
	 * type of object. It records what the parameters where that generated the
	 * query, and access to the results. In addition there are some helpful
	 * pagination options to aid the front-end in generating pagination results.
	 * 
	 * @param <T>
	 *            Either Submission or ActionLog
	 */
	public static class LuceneSearchResults<T extends AbstractModel> implements SearchResult<T> {

		// The search parameters
		public final SearchFilter filter;
		public final SearchDirection direction;
		public final SearchOrder orderBy;
		public final int offset;
		public final int limit;
		public final List<T> results;
		public final int total;
		
		/**
		 * Construct a new search results object.
		 * 
		 * @param filter
		 *            The filter which produced these results.
		 * @param direction
		 *            The direction of the results.
		 * @param orderBy
		 *            How the results are ordered.
		 * @param offset
		 *            The pagination offset of the results.
		 * @param limit
		 *            The number of items per page.
		 * @param results
		 *            The actual results.
		 * @param total
		 *            How many objects matched this query regardless of
		 *            pagination limits.
		 */
		public LuceneSearchResults(SearchFilter filter, SearchDirection direction, SearchOrder orderBy, int offset, int limit, List<T> results, int total) {
			this.filter = filter;
			this.direction = direction;
			this.orderBy = orderBy;
			this.offset = offset;
			this.limit = limit;
			this.results = results;
			this.total = total;
		}
		
		
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
		public List<Pagination> getPagination(int windowSize) {
			
			List<Pagination> pagination = new ArrayList<Pagination>();
			
			// Create the backwards entries
			for( int i = -((windowSize-1)/2); i < 0; i++) {
				
				int offset = getOffset() + (i * getLimit());
				int page = (getOffset() / getLimit()) + i + 1;
				
				if (offset >= 0) 
					pagination.add(new Pagination(page, offset, false));
			}
			
		    // Add the current entry
			pagination.add(new Pagination( (getOffset() / limit) +1, getOffset(), true));
				
		    // Create the forward entries
			for( int i = 1; i <= windowSize; i++) {
				
				int offset = getOffset() + (i * getLimit());
				int page = (getOffset() / getLimit()) + i + 1;
				
				if (offset < getTotal() && pagination.size() < windowSize )
					pagination.add(new Pagination(page, offset, false));
			}
			
			return pagination;
		}
	} // LuceneSearchResults
	
	
	
	/**
	 * Sort vireo model objects based upon a provided list of ids.
	 * 
	 */
	public static class ModelComparator implements Comparator<AbstractModel> {
		
		// The predefined list of sorted ids.
		public final List<Long> sortedIds;
		
		/**
		 * Construct a new comparator.
		 * @param sortedIds A predefined list of sorted ids.
		 */
		public ModelComparator(List<Long> sortedIds) {
			this.sortedIds = sortedIds;
		}

		@Override
		public int compare(AbstractModel a, AbstractModel b) {
			
			Long aId = a.getId();
			Long bId = b.getId();
			
			if (aId == bId)
				return 0;
			if (sortedIds.indexOf(aId) < sortedIds.indexOf(bId))
				return -1;
			return 1;
		}
		
	}
	
	
	
}



























