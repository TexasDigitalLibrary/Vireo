package org.tdl.vireo.model.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SearchDirection;
import org.tdl.vireo.model.SearchFilter;
import org.tdl.vireo.model.SearchOrder;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.state.StateManager;

import play.Logger;
import play.db.jpa.JPA;

/**
 * Jpa specific implementation of the Vireo Submission Repository interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaSubmissionRepositoryImpl implements SubmissionRepository {
	
	// Define sorting syntax for individual fields.
	public static final String[] SUBMISSION_ORDER_BY_COLUMNS = new String[SearchOrder.values().length];
	public static final String[] ACTION_LOG_ORDER_BY_COLUMNS = new String[SearchOrder.values().length];

	{
		// Static block to define sorting columns.
		// TODO: Pull in the following tables to support sorting by others types:
		// 1) Attachments
		// 2) Committee Members
		// 3) Custom Actions
		// 4) Action Log Table for last event time.
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.ID.ordinal()] = "sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.SUBMITTER.ordinal()] = "submitter.lastName %, submitter.firstName %, sub.id %";
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.DOCUMENT_TITLE.ordinal()] = "sub.documentTitle %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.DOCUMENT_ABSTRACT.ordinal()] = "sub.documentAbstract %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.DOCUMENT_KEYWORDS.ordinal()] = "sub.documentKeywords %, sub.id %";
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.EMBARGO_TYPE.ordinal()] = "embargo.displayOrder %, sub.id %";
		
		// Note: HQL does not support nulls first/last it depends upon the database implementation.
		// https://hibernate.onjira.com/browse/HHH-465
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.PRIMARY_DOCUMENT.ordinal()] = "primary.name %, sub.id %";
		
		// Meh, sorts by the number of committee members? Probably not what people expect.
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.COMMITTEE_MEMBERS.ordinal()] = "COUNT(committees.id) %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.COMMITTEE_CONTACT_EMAIL.ordinal()] = "sub.committeeContactEmail %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.COMMITTEE_APPROVAL_DATE.ordinal()] = "sub.committeeApprovalDate %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.COMMITTEE_EMBARGO_APPROVAL_DATE.ordinal()] = "sub.committeeEmbargoApprovalDate %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.COMMITTEE_DISPOSITION.ordinal()] = "sub.committeeDisposition %, sub.id %";
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.SUBMISSION_DATE.ordinal()] = "sub.submissionDate %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.APPROVAL_DATE.ordinal()] = "sub.approvalDate %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.LICENSE_AGREEMENT_DATE.ordinal()] = "sub.licenseAgreementDate %, sub.id %";
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.DEGREE.ordinal()] = "sub.degree %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.DEPARTMENT.ordinal()] = "sub.department %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.COLLEGE.ordinal()] = "sub.college %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.MAJOR.ordinal()] = "sub.major %, sub.id %";
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.DOCUMENT_TYPE.ordinal()] = "sub.documentType %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.GRADUATION_YEAR.ordinal()] = "sub.graduationYear %, sub.graduationMonth %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.GRADUATION_MONTH.ordinal()] = "sub.graduationMonth %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.GRADUATION_DATE.ordinal()] = "sub.graduationYear %, sub.graduationMonth %, sub.id %";
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.STATE.ordinal()] = "sub.stateName %, sub.id %";
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.ASSGINEE.ordinal()] = "assignee.lastName %, assignee.firstName %, sub.id %";
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.UMI_RELEASE.ordinal()] = "sub.UMIRelease %, sub.id %";
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.CUSTOM_ACTIONS.ordinal()] = "COUNT(actions.id) %, sub.id %";
		
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.LAST_EVENT_ENTRY.ordinal()] = "MAX(logs.actionDate) %, sub.id %";
		SUBMISSION_ORDER_BY_COLUMNS[SearchOrder.LAST_EVENT_TIME.ordinal()] = "MAX(logs.actionDate) %, sub.id %";
		
		// Copy everything over to the action log order.
		for( int i = 0 ; i <SUBMISSION_ORDER_BY_COLUMNS.length; i++) {
			ACTION_LOG_ORDER_BY_COLUMNS[i] = SUBMISSION_ORDER_BY_COLUMNS[i].replaceAll(" sub.id ", " log.id ");
		}
		// And then make a few exceptions.
		ACTION_LOG_ORDER_BY_COLUMNS[SearchOrder.ID.ordinal()] = "log.id %";
		ACTION_LOG_ORDER_BY_COLUMNS[SearchOrder.LAST_EVENT_ENTRY.ordinal()] = "log.actionDate %, sub.id %";
		ACTION_LOG_ORDER_BY_COLUMNS[SearchOrder.LAST_EVENT_TIME.ordinal()] = "log.actionDate %, sub.id %";
	}
	
	
	// //////////////////
	// Submission Model
	// //////////////////
	
	@Override
	public Submission createSubmission(Person submitter) {
		return new JpaSubmissionImpl(submitter);
	}

	@Override
	public Submission findSubmission(Long id) {
		return (Submission) JpaSubmissionImpl.findById(id);
	}

	@Override
	public Submission findSubmissionByEmailHash(String emailHash) {
		return JpaSubmissionImpl.find("committeeEmailHash = ?", emailHash).first();
	}

	@Override
	public List<Submission> findSubmission(Person submitter) {
		return JpaSubmissionImpl.find("submitter = ?", submitter).fetch();

	}
	
	@Override
	public List<Submission> filterSearchSubmissions(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit) {
		
		// We will store all the supplied parameters in this map, which will be
		// added to the query near the end. This prevents SQL injection attacks
		// and makes the query execute quicker in some circumstances. Each
		// paramater will be labeled based upon how it is being used, i.e.
		// searchText, degree, college, etc. But then will have a unique id
		// appended to the in to the end to make sure that all names are
		// globally unique across the entire query.
		int paramIndex = 1;
		Map<String, Object> params = new HashMap<String, Object>();
		
		// The query consists of a set of AND clauses, containing possibly a set
		// of OR clauses. Each of these are stored in these ANDList / ORList
		// data structure defined at static inner classes here. They just make
		// it easy to manipulate the query string so we don't have to manually
		// place AND or ORs everywhere with proper spacing and scoping.
		
		// Search Text Filter
		ANDList andList = new ANDList();
		for(String searchText : filter.getSearchText()) {
			ORList orList = new ORList();
			
			orList.add(new Statement("sub.documentTitle LIKE :searchText"+paramIndex));
			orList.add(new Statement("sub.documentAbstract LIKE :searchText"+paramIndex));
			orList.add(new Statement("sub.documentKeywords LIKE :searchText"+paramIndex));
			orList.add(new Statement("sub.committeeContactEmail LIKE :searchText"+paramIndex));
			orList.add(new Statement("sub.committeeDisposition LIKE :searchText"+paramIndex));
			
			// TODO: Add more search locations such as attachment names, and committee member names.
			
			params.put("searchText"+(paramIndex++), "%"+searchText+"%");
			andList.add(orList);
		}
		
		// Status Filter
		ORList orList = new ORList();
		for(String stateName : filter.getStates()) {
			orList.add(new Statement("sub.stateName = :stateName"+paramIndex));
			params.put("stateName"+(paramIndex++), stateName);
		}
		andList.add(orList);
		
		// Assignee Filter
		orList = new ORList();
		for(Person assignee : filter.getAssignees()) {
			orList.add(new Statement("sub.assignee = :assignee"+paramIndex));
			params.put("assignee"+(paramIndex++), assignee);
		}
		andList.add(orList);
		
		// Graduation Years Filter
		orList = new ORList();
		for(Integer year : filter.getGraduationYears()) {
			orList.add(new Statement("sub.graduationYear = :graduationYear"+paramIndex));
			params.put("graduationYear"+(paramIndex++), year);
		}
		andList.add(orList);
		
		// Graduation Months Filter
		orList = new ORList();
		for(Integer month : filter.getGraduationMonths()) {
			orList.add(new Statement("sub.graduationMonth = :graduationMonth"+paramIndex));
			params.put("graduationMonth"+(paramIndex++), month);
		}
		andList.add(orList);
		
		// Degree Filter
		orList = new ORList();
		for(String degree : filter.getDegrees()) {
			orList.add(new Statement("sub.degree = :degree"+paramIndex));
			params.put("degree"+(paramIndex++), degree);
		}
		andList.add(orList);
		
		// Department Filter
		orList = new ORList();
		for(String department : filter.getDepartments()) {
			orList.add(new Statement("sub.department = :department"+paramIndex));
			params.put("department"+(paramIndex++), department);
		}
		andList.add(orList);
		
		// College Filter
		orList = new ORList();
		for(String college : filter.getColleges()) {
			orList.add(new Statement("sub.college = :college"+paramIndex));
			params.put("college"+(paramIndex++), college);
		}
		andList.add(orList);
		
		// Major Filter
		orList = new ORList();
		for(String major : filter.getMajors()) {
			orList.add(new Statement("sub.major = :major"+paramIndex));
			params.put("major"+(paramIndex++), major);
		}
		andList.add(orList);
		
		// Document Type Filter
		orList = new ORList();
		for(String docType : filter.getDocumentTypes()) {
			orList.add(new Statement("sub.documentType = :docType"+paramIndex));
			params.put("docType"+(paramIndex++), docType);
		}
		andList.add(orList);
		
		// UMI release
		if (filter.getUMIRelease() != null) {
			
			if (filter.getUMIRelease()) {
				andList.add(new Statement("umiRelease = true"));
			} else {
				andList.add(new Statement("umiRelease = false"));
			}
		}
		
		// Date range Filter
		if (filter.getDateRangeStart() != null && filter.getDateRangeEnd() != null) {
			Date start = filter.getDateRangeStart();
			Date end = filter.getDateRangeEnd();
			
			andList.add(new Statement("sub.submissionDate > :startDate"+paramIndex));
			andList.add(new Statement("sub.submissionDate < :endDate"+(paramIndex+1)));
			
			params.put("startDate"+(paramIndex++), start);
			params.put("endDate"+(paramIndex++), end);			
		}
		
		StringBuilder queryText = new StringBuilder();
		queryText.append("SELECT sub ");
		queryText.append("FROM JpaSubmissionImpl AS sub ");
		queryText.append("LEFT OUTER JOIN sub.submitter AS submitter ");
		queryText.append("LEFT OUTER JOIN sub.assignee AS assignee ");
		queryText.append("LEFT OUTER JOIN sub.embargoType AS embargo ");
		queryText.append("LEFT OUTER JOIN sub.attachments AS primary WITH primary.type = :primaryDocument ");
		queryText.append("LEFT OUTER JOIN sub.committeeMembers AS committees ");
		queryText.append("LEFT OUTER JOIN sub.customActions AS actions ");
		queryText.append("LEFT OUTER JOIN sub.actionLogs AS logs ");


		params.put("primaryDocument", AttachmentType.PRIMARY);
		queryText.append("WHERE ");
		andList.buildClause(queryText);
		queryText.append(" ");
		queryText.append("GROUP BY sub ");
		String orderByClause = SUBMISSION_ORDER_BY_COLUMNS[orderBy.ordinal()];
		if (direction == SearchDirection.DESCENDING)
			orderByClause = orderByClause.replaceAll("%", "DESC");
		else
			orderByClause = orderByClause.replaceAll("%", "ASC");
		queryText.append("ORDER BY "+orderByClause);
		
		if (Logger.isDebugEnabled()) {
			String message = "Filter '"+filter.getName()+"' query = '"+queryText.toString()+"'\n";
			for(String key : params.keySet()) {
				message += "   ':"+key+"' = '"+params.get(key)+"'\n";
			}
			Logger.debug(message);
		}
		
		TypedQuery<JpaSubmissionImpl> query = JPA.em().createQuery(queryText.toString(), JpaSubmissionImpl.class);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		for(String key : params.keySet()) 
			query.setParameter(key, params.get(key));
		
		return (List) query.getResultList();
	}

	// //////////////////////////////////////////////////////////////
	// Attachment, Committee Member, and Custom Action Value Models
	// //////////////////////////////////////////////////////////////
	
	@Override
	public Attachment findAttachment(Long id) {
		return (Attachment) JpaAttachmentImpl.findById(id);
	}

	@Override
	public CommitteeMember findCommitteeMember(Long id) {
		return (CommitteeMember) JpaCommitteeMemberImpl.findById(id);

	}

	@Override
	public CustomActionValue findCustomActionValue(Long id) {
		return (CustomActionValue) JpaCustomActionValueImpl.findById(id);
	}

	// //////////////////
	// Action Log Model
	// //////////////////
	
	@Override
	public ActionLog findActionLog(Long id) {
		return (ActionLog) JpaActionLogImpl.findById(id);
	}

	@Override
	public List<ActionLog> findActionLog(Submission submission) {
		return JpaActionLogImpl.find("submission = ? order by actionDate", submission).fetch();
	}

	@Override
	public List<ActionLog> filterSearchActionLogs(SearchFilter filter,
			SearchOrder orderBy, SearchDirection direction, int offset,
			int limit) {
		// We will store all the supplied parameters in this map, which will be
		// added to the query near the end. This prevents SQL injection attacks
		// and makes the query execute quicker in some circumstances. Each
		// paramater will be labeled based upon how it is being used, i.e.
		// searchText, degree, college, etc. But then will have a unique id
		// appended to the in to the end to make sure that all names are
		// globally unique across the entire query.
		int paramIndex = 1;
		Map<String, Object> params = new HashMap<String, Object>();
		
		// The query consists of a set of AND clauses, containing possibly a set
		// of OR clauses. Each of these are stored in these ANDList / ORList
		// data structure defined at static inner classes here. They just make
		// it easy to manipulate the query string so we don't have to manually
		// place AND or ORs everywhere with proper spacing and scoping.
		
		// Search Text Filter
		ANDList andList = new ANDList();
		ORList orList = new ORList();
		for(String searchText : filter.getSearchText()) {	
			orList.add(new Statement("log.entry LIKE :searchText"+paramIndex));
			params.put("searchText"+(paramIndex++), "%"+searchText+"%");
		}
		andList.add(orList);
		
		// Status Filter
		orList = new ORList();
		for(String stateName : filter.getStates()) {
			orList.add(new Statement("log.submissionState = :stateName"+paramIndex));
			params.put("stateName"+(paramIndex++), stateName);
		}
		andList.add(orList);
		
		// Assignee Filter
		orList = new ORList();
		for(Person assignee : filter.getAssignees()) {
			orList.add(new Statement("log.person = :assignee"+paramIndex));
			params.put("assignee"+(paramIndex++), assignee);
		}
		andList.add(orList);
		
		// Graduation Years Filter
		orList = new ORList();
		for(Integer year : filter.getGraduationYears()) {
			orList.add(new Statement("sub.graduationYear = :graduationYear"+paramIndex));
			params.put("graduationYear"+(paramIndex++), year);
		}
		andList.add(orList);
		
		// Graduation Months Filter
		orList = new ORList();
		for(Integer month : filter.getGraduationMonths()) {
			orList.add(new Statement("sub.graduationMonth = :graduationMonth"+paramIndex));
			params.put("graduationMonth"+(paramIndex++), month);
		}
		andList.add(orList);
		
		// Degree Filter
		orList = new ORList();
		for(String degree : filter.getDegrees()) {
			orList.add(new Statement("sub.degree = :degree"+paramIndex));
			params.put("degree"+(paramIndex++), degree);
		}
		andList.add(orList);
		
		// Department Filter
		orList = new ORList();
		for(String department : filter.getDepartments()) {
			orList.add(new Statement("sub.department = :department"+paramIndex));
			params.put("department"+(paramIndex++), department);
		}
		andList.add(orList);
		
		// College Filter
		orList = new ORList();
		for(String college : filter.getColleges()) {
			orList.add(new Statement("sub.college = :college"+paramIndex));
			params.put("college"+(paramIndex++), college);
		}
		andList.add(orList);
		
		// Major Filter
		orList = new ORList();
		for(String major : filter.getMajors()) {
			orList.add(new Statement("sub.major = :major"+paramIndex));
			params.put("major"+(paramIndex++), major);
		}
		andList.add(orList);
		
		// Document Type Filter
		orList = new ORList();
		for(String docType : filter.getDocumentTypes()) {
			orList.add(new Statement("sub.documentType = :docType"+paramIndex));
			params.put("docType"+(paramIndex++), docType);
		}
		andList.add(orList);
		
		// UMI release
		if (filter.getUMIRelease() != null) {
			
			if (filter.getUMIRelease()) {
				andList.add(new Statement("umiRelease = true"));
			} else {
				andList.add(new Statement("umiRelease = false"));
			}
		}
		
		// Date range Filter
		if (filter.getDateRangeStart() != null && filter.getDateRangeEnd() != null) {
			Date start = filter.getDateRangeStart();
			Date end = filter.getDateRangeEnd();
			
			andList.add(new Statement("sub.submissionDate > :startDate"+paramIndex));
			andList.add(new Statement("sub.submissionDate < :endDate"+(paramIndex+1)));
			
			params.put("startDate"+(paramIndex++), start);
			params.put("endDate"+(paramIndex++), end);			
		}
		
		StringBuilder queryText = new StringBuilder();
		queryText.append("SELECT log ");
		queryText.append("FROM JpaActionLogImpl AS log ");
		queryText.append("LEFT OUTER JOIN log.submission AS sub ");
		queryText.append("LEFT OUTER JOIN sub.submitter AS submitter ");
		queryText.append("LEFT OUTER JOIN sub.assignee AS assignee ");
		queryText.append("LEFT OUTER JOIN sub.embargoType AS embargo ");
		queryText.append("LEFT OUTER JOIN sub.attachments AS primary WITH primary.type = :primaryDocument ");
		queryText.append("LEFT OUTER JOIN sub.committeeMembers AS committees ");
		queryText.append("LEFT OUTER JOIN sub.customActions AS actions ");
		params.put("primaryDocument", AttachmentType.PRIMARY);
		queryText.append("WHERE ");
		andList.buildClause(queryText);
		queryText.append(" ");
		queryText.append("GROUP BY log ");
		String orderByClause = ACTION_LOG_ORDER_BY_COLUMNS[orderBy.ordinal()];
		if (direction == SearchDirection.DESCENDING)
			orderByClause = orderByClause.replaceAll("%", "DESC");
		else
			orderByClause = orderByClause.replaceAll("%", "ASC");
		queryText.append("ORDER BY "+orderByClause);
		
		if (Logger.isDebugEnabled()) {
			String message = "Filter '"+filter.getName()+"' query = '"+queryText.toString()+"'\n";
			for(String key : params.keySet()) {
				message += "   ':"+key+"' = '"+params.get(key)+"'\n";
			}
			Logger.debug(message);
		}
		
		TypedQuery<JpaActionLogImpl> query = JPA.em().createQuery(queryText.toString(), JpaActionLogImpl.class);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		for(String key : params.keySet()) 
			query.setParameter(key, params.get(key));
		
		return (List) query.getResultList();

	}
	
	// //////////////////
	// Filter Search
	// //////////////////
	
	@Override
	public SearchFilter createSearchFilter(Person creator, String name) {
		return new JpaSearchFilterImpl(creator, name);
	}

	@Override
	public SearchFilter findSearchFilter(Long id) {
		return (SearchFilter) JpaSearchFilterImpl.findById(id);
	}

	@Override
	public List<SearchFilter> findSearchFiltersByCreatorOrPublic(Person creator) {
		return (List) JpaSearchFilterImpl.find("creator = ? OR publicFlag = true order by id", creator).fetch();
	}

	@Override
	public List<SearchFilter> findAllSearchFilters() {
		return (List) JpaSearchFilterImpl.find("order by id").fetch();
	}
	
	
	
	
	/**
	 * Internal class to represent a WHERE clause of an HQL query. The clause
	 * may either be a single clause, or a list of multiple clauses combined
	 * together. The combined clause will be constructed using a passed
	 * StringBuilder so that we can efficiently build large queries.
	 */
	public static interface Clause {
		public StringBuilder buildClause(StringBuilder query);
	}
	
	/**
	 * The implementation of the Clause interface for just a single statement. A
	 * statement would be something like "property = :value".
	 */
	public static class Statement implements Clause {
		final private String statement;
		public Statement(final String statement) {
			this.statement = statement;
		}
		
		public StringBuilder buildClause(StringBuilder query) {
			query.append(statement);
			return query;
		}
	}
	
	/**
	 * This class defines an abstract list of clauses. Each clauses will be
	 * isolated into a single clause using parenthesis (), then inside each
	 * individual clause will be separated by a logical operator to be defined
	 * by the implementing concrete class.
	 * 
	 */
	public abstract static class ClauseList extends ArrayList<Clause> implements Clause {
		
		// The logical operator will glue together the individual clauses in the list.
		public final String logicalOperator;
		
		/**
		 * Construct a new ClauseList using the given logical operator.
		 * @param logicalOperator AND or OR that will seperate the individual clauses in the list.
		 */
		protected ClauseList(final String logicalOperator) {
			this.logicalOperator = logicalOperator;
		}
		
		/**
		 * Don't add empty lists. This just makes creating queries easier, you
		 * can for loop of properties and if at the end nothing was produced
		 * skip adding it to the list.
		 */
		public boolean add(Clause clause) {
			
			// Don't add any empty sub lists.
			if (clause instanceof ClauseList) {
				ClauseList list = (ClauseList) clause;
				if (list.size() == 0)
					return false;
			}
	
			return super.add(clause);
		}
		
		/**
		 * Append to the query string all the clauses combined into a single
		 * statement isolated by parenthesis ().
		 */
		public StringBuilder buildClause(StringBuilder query) {

			if (this.isEmpty())
				throw new IllegalStateException("Unable to render an empty clause list.");
			
			// Skip if we're just a list of one.
			if (this.size() == 1) {
				this.get(0).buildClause(query);
				return query;
			}
			
			// Loop through all the clauses.
			boolean first = true;
			for (Clause clause : this) {
				
				if (first) {
					query.append("( ");
					first = false;
				} else {
					query.append(" " + logicalOperator + " ");
				}
				
				clause.buildClause(query);
			}
			
			query.append(" )");
			return query;
		}
	}
	
	/**
	 * Concrete implementation of the ClauseList for clauses that should be "OR"ed together.
	 */
	public static class ORList extends ClauseList {
		
		public ORList() {
			super("OR");
		}
		
		
	}
	
	/**
	 * Concrete implementation of the ClauseList for clauses that should be "AND"ed together.
	 */
	public static class ANDList extends ClauseList {
		
		public ANDList() {
			super("AND");
		}
	}

}
