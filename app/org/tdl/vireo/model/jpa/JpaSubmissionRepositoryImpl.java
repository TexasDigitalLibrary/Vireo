package org.tdl.vireo.model.jpa;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.tdl.vireo.model.AbstractModel;
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
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.Semester;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.SearchResult;
import org.tdl.vireo.state.StateManager;

import play.Logger;
import play.db.jpa.JPA;

/**
 * Jpa specific implementation of the Vireo Submission Repository interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JpaSubmissionRepositoryImpl implements SubmissionRepository {
	
	// How many objects to load for each iterator's batch operaton.
	public static final int ITERATOR_BATCH_SIZE = 10;

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
	public List<Submission> findSubmissions(List<Long> submissionIds) {
		
		if (submissionIds == null || submissionIds.size() == 0)
			return new ArrayList<Submission>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("FROM JpaSubmissionImpl WHERE id IN (");
		for (int i=0; i < submissionIds.size(); i++) {
			if (i > 0)
				sql.append(",");
			sql.append("?"+(i+1));
		}
		sql.append(")");
		
		
		TypedQuery query = JPA.em().createQuery(sql.toString(),JpaSubmissionImpl.class);
		
		for (int i=0; i < submissionIds.size(); i++) {
		query.setParameter(i+1, submissionIds.get(i));
		}
		
		return query.getResultList();
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
	public Iterator<Submission> findAllSubmissions() {
		return new JpaIterator() {
			@Override
			protected List loadNextBatch(int offset) {
				return JpaSubmissionImpl.find("order by id desc").from(offset).fetch(ITERATOR_BATCH_SIZE);
			}
		};
	}
	
	@Override
	public long findSubmissionsTotal() {
		return JpaSubmissionImpl.count();
	}

	// //////////////////////////////////////////////////////////////
	// Submission informational
	// //////////////////////////////////////////////////////////////
	
	@Override
	public List<Semester> findAllGraduationSemesters() {
		Query query = JPA.em().createQuery("SELECT DISTINCT new org.tdl.vireo.search.Semester(sub.graduationYear, sub.graduationMonth) FROM JpaSubmissionImpl AS sub WHERE sub.graduationYear IS NOT NULL AND sub.graduationMonth IS NOT NULL ORDER BY sub.graduationYear DESC, sub.graduationMonth DESC");
		
		List<Semester> results = query.getResultList();
		return results;
	}
	
	@Override
	public List<Integer> findAllSubmissionYears() {
		
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		
		// This is what we are selecting from.
		Root<JpaSubmissionImpl> sub = cq.from(JpaSubmissionImpl.class);
		
		// This gets a field name for use other expressions.
		Expression<Timestamp> subDate = sub.get("submissionDate");
		
		// Select District year(subDate)
		cq.select(cb.function("year", Integer.class, subDate)).distinct(true);
		
		// Where subDate is not null
		cq.where(cb.isNotNull(subDate));
		
		// Order by submission date.
		cq.orderBy(cb.desc(cb.function("year", Integer.class, subDate)));
		
		// Generate the query from the criteria query.
		TypedQuery<Integer> query = JPA.em().createQuery(cq);
		
		List<Integer> results = query.getResultList();
		return results;	
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
	public List<ActionLog> findActionLogs(List<Long> logIds) {
		
		if (logIds == null || logIds.size() == 0)
			return new ArrayList<ActionLog>();
		
		StringBuilder sql = new StringBuilder();
		sql.append("FROM JpaActionLogImpl WHERE id IN (");
		for (int i=0; i < logIds.size(); i++) {
			if (i > 0)
				sql.append(",");
			sql.append("?"+(i+1));
		}
		sql.append(")");
		
		
		TypedQuery query = JPA.em().createQuery(sql.toString(),JpaActionLogImpl.class);
		
		for (int i=0; i < logIds.size(); i++) {
		query.setParameter(i+1, logIds.get(i));
		}
		
		return query.getResultList();
	}
	
	@Override
	public List<ActionLog> findActionLog(Submission submission) {
		return JpaActionLogImpl.find("submission = ? order by actionDate desc, id desc", submission).fetch();
	}
	
	@Override
	public Iterator<ActionLog> findAllActionLogs() {
		return new JpaIterator() {
			@Override
			protected List loadNextBatch(int offset) {
				return JpaActionLogImpl.find("order by id desc").from(offset).fetch(ITERATOR_BATCH_SIZE);
			}
		};
	}
	
	@Override
	public long findActionLogsTotal() {
		return JpaActionLogImpl.count();
	}
	
	// //////////////////
	// Filter Search
	// //////////////////
	
	@Override
	public NamedSearchFilter createSearchFilter(Person creator, String name) {
		return new JpaNamedSearchFilterImpl(creator, name);
	}

	@Override
	public NamedSearchFilter findSearchFilter(Long id) {
		return (NamedSearchFilter) JpaNamedSearchFilterImpl.findById(id);
	}

	@Override
	public List<NamedSearchFilter> findSearchFiltersByCreatorOrPublic(Person creator) {
		return (List) JpaNamedSearchFilterImpl.find("creator = ? OR publicFlag = true order by id", creator).fetch();
	}
	
	@Override
	public NamedSearchFilter findSearchFilterByCreatorAndName(Person creator, String name) {
		return JpaNamedSearchFilterImpl.find("creator = ? AND name = ?", creator, name).first();
	}
	

	@Override
	public List<NamedSearchFilter> findAllSearchFilters() {
		return (List) JpaNamedSearchFilterImpl.find("order by id").fetch();
	}
	
	/**
	 * Inner class to handle iterating over submissions or action logs. This
	 * class solves the problem of needing to loop over all objects which may be
	 * too huge to load into memory at one time. The iterator addresses the
	 * problem by loading the objects in batches, so only a few objects are in
	 * memory at any single time.
	 * 
	 * @param <T>
	 *            The type of object to iterate over.
	 */
	public static abstract class JpaIterator<T extends AbstractModel> implements Iterator<T> {

		// This is the offset into the global list of objects.
		public int offset = 0;

		// This is the pointer to the current object in the list of retrieved
		// objects.
		public int currentPointer = 0;

		// This is the current batch of retrieved objects.
		public List<T> retrieved = new ArrayList<T>();

		/** Construct a new iterator **/
		protected JpaIterator() {
			loadNextBatch();
		}
		
		/**
		 * Load the next batch of objects from the database.
		 * 
		 * @param offset
		 *            How many objects have previously been loaded.
		 * @return A list of objects returned from the database, or an empty
		 *         list if no more objects exist.
		 */
		protected abstract List<T> loadNextBatch(int offset);
		
		
		/**
		 * Load the next batch, remove the current batch, obtain a new list and
		 * update the internal pointers.
		 */
		protected void loadNextBatch() {
			
			for (T model : retrieved)
				model.detach();
			
			retrieved = loadNextBatch(offset);
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
			retrieved.get(currentPointer).delete();
		}
		
	}

}
