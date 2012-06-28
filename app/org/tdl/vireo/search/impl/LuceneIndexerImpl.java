package org.tdl.vireo.search.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.oval.internal.util.LinkedSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.Indexer;

import com.mysql.jdbc.log.Log;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.modules.spring.Spring;

/**
 * The Lucene Search Indexer.
 * 
 * This class manages writing to the Lucene search index all updates. For each
 * thread modifying data in the model a separate transaction of those changes
 * will be maintained. So when a model is updated, it is expected that the model
 * object will tell the indexer that it's contents have been modified. The
 * indexer will receive that notification and keep the related submission's id
 * in a set. Then after the database transaction has been committed the index
 * transaction can be committed. Updating the index is handled by background
 * threads, in a matter completely seemless to the outside observer.
 * 
 * 
 * 
 * 
 * Fields:
 * 
 * subId: The id of the submission, this is available for both submissions and
 * action logs.
 * 
 * logId: The id of the action log, this is only available for action logs.
 * 
 * type: Either "submission" or "actionlog" depending on what type of record.
 * 
 * SEARCH INDEXES (SOME USED FOR SORTING)
 * 
 * searchText: search index for textual searches
 * 
 * state: The bean name of the current state. single token not analyzed (search
 * and sort)
 * 
 * searchAssigned: search index for those who are assigned. The field is
 * composed of either the id of the assigned person, or 0 for unassigned
 * submissions.
 * 
 * sortAssigned: The full name of the person assigned
 * 
 * graduationSemester: The grad year & month converted into a date object and
 * stored as milliseconds.
 * 
 * department: The department, single token not analyzed (search and sort)
 * 
 * college: The college, single token not analyzed (search and sort)
 * 
 * major: The major, single token not analyzed (search and sort)
 * 
 * embargo: The embargo name, single token not analyzed (search and sort)
 * 
 * degree: The degree, single token not analyzed (search and sort)
 * 
 * documentType: The document type's name, single token not analyzed (search and
 * sort)
 * 
 * umiRelease: Either "yes", "no", or "" for the three possible states (search
 * and sort)
 * 
 * submissionDate: The submission date for the submission as a numeric date
 * stored as milliseconds.
 * 
 * 
 * 
 * ONLY USED FOR SORTING:
 * 
 * studentName: The student name for sorting.
 * 
 * documentTitle: The document's title for sorting.
 * 
 * documentAbstract: The document's abstract for sorting.
 * 
 * documentKeywords: The document's keywords for sorting.
 * 
 * primaryDocument: The name of the primary document for sorting.
 * 
 * licenseAgreementDate: The date the license was agreed too for sorting.
 * 
 * approvalDate: The date the submission was approved for sorting.
 * 
 * committeeApprovalDate: The date the committee approved the submission for
 * sorting.
 * 
 * committeeEmbargoApprovalDate: The date the committee approved the embargo for
 * sorting.
 * 
 * committeeMembers: A single string strung together of all committee member's
 * name for sorting.
 * 
 * committeeContactEmail: The committee email for sorting
 * 
 * customActions: The count of custom actions that have been checked off on the
 * submission for sorting.
 * 
 * lastEventEntry: The textual entry of the last log event for sorting.
 * 
 * lastEventTime: The date of the last entry for sorting.
 * 
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */

public class LuceneIndexerImpl implements Indexer {

	// The current updated submissions for each thread.
	public ThreadLocal<Set<Long>> transactionLocal = new ThreadLocal<Set<Long>>();	
	
	// Thread state (Please don't mess with directly!) Use the method
	// runNextJob() so all access is synchronized
	private IndexJob currentJob = null;
	private IndexJob nextJob = null;
	
	// Static Lucene configuration
	public final Directory index;
	public final IndexReader reader;
	public final Version version = Version.LUCENE_36;
	public final Analyzer standardAnalyzer = new StandardAnalyzer(version);
	
	// Spring dependencies
	public SubmissionRepository subRepo = null;
	
	/**
	 * Construct a new LuceneIndexer
	 */
	public LuceneIndexerImpl() throws IOException {
		index = FSDirectory.open(new File(Play.configuration.getProperty("index.path","data/indexes")));
		reader = IndexReader.open(index);
	}
	
	/**
	 * Inject the submission repository.
	 * 
	 * @param subRepo
	 *            The submission repository
	 */
	public void setSubmissionRepository(SubmissionRepository subRepo) {
		this.subRepo = subRepo;
	}
	
	
	/**
	 * Receive the model and update the transactions with any submissions that
	 * need to be updated in the index. We may receive models of lots of
	 * different types, however we only care about a few specific ones, those
	 * that are connected with a particular submission. When one of those models
	 * comes through we exact the submission it is related to and update the
	 * transaction accordingly.
	 * 
	 * @param model
	 *            The model which was saved, created, or deleted.
	 */
	@Override
	public <T extends AbstractModel> void updated(T model) {
	
		// If we get a null then there probably is a problem somewhere else than needs to be fixed.
		if (model == null)
			throw new IllegalArgumentException("Updated object must not be null");
		
		// If we haven't been persisted in the database yet then forget about it.
		if (model.getId() == null)
			return;
		
		// Check the type of object it is.
		Submission sub = null;
		if (model instanceof Submission) {
			sub = (Submission) model;
		} else if (model instanceof Attachment) {
			Attachment attachment = (Attachment) model;
			sub = attachment.getSubmission();
		} else if (model instanceof CommitteeMember) {
			CommitteeMember member = (CommitteeMember) model;
			sub = member.getSubmission();
		} else if (model instanceof CustomActionValue) {
			CustomActionValue action = (CustomActionValue) model;
			sub = action.getSubmission();
		} else if (model instanceof ActionLog) {
			ActionLog log = (ActionLog) model;
			sub = log.getSubmission();
		}
		
		// Add the submission to the transaction
		if (sub != null) {
			// Create a new transaction if needed.
			if (transactionLocal.get() == null)
				transactionLocal.set(new LinkedSet<Long>());
			
			// Add the submission to the transaction
			transactionLocal.get().add(sub.getId());
		}
	}
	
	/**
	 * Rollback the current transaction.
	 * 
	 * Just remove the threadLocal entry.
	 */
	@Override
	public void rollback() {
		transactionLocal.remove();
	}

	/**
	 * Commit the current transaction.
	 * 
	 * Create a new job and add it to the queue.
	 */
	@Override
	public void commit() {
	
		Set<Long> txn = transactionLocal.get();
		if (txn.size() > 0) {
			IndexJob newJob = new UpdateIndexJob(txn);
			runNextJob(newJob);
		}
	}

	/**
	 * Rebuild the entire search index from scratch.
	 * 
	 * Create a new job and add it to the queue.
	 */
	@Override
	public void rebuild() {
		
		IndexJob newJob = new RebuildIndexJob();
		runNextJob(newJob);
	}
	
	
	@Override
	public boolean isJobRunning() {
		return !(currentJob == null);
	}
	
	@Override
	public String getCurrentJobLabel() {
		try {
			return currentJob.getLabel();
		} catch (Throwable t) {
			// We need this method to be thread safe, so use a try catch block
			// instead of an if block to check for the case where currentJob ==
			// null.
			return "None";
		}
	}
	
	@Override
	public long getCurrentJobProgress() {
		try {
			return currentJob.getProgress();
		} catch (Throwable t) {
			// We need this method to be thread safe, so use a try catch block
			// instead of an if block to check for the case where currentJob ==
			// null.
			return -1;
		}
	}
	
	@Override
	public long getCurrentJobTotal() {
		try {
			return currentJob.getTotal();
		} catch (Throwable t) {
			// We need this method to be thread safe, so use a try catch block
			// instead of an if block to check for the case where currentJob ==
			// null.
			return -1;
		}
	}
	
	
	/**
	 * Control processing of the next job. Any management of the current & next
	 * job pointers is handled by this method to ensure thread safety.
	 * 
	 * This method works under two scenarios:
	 * 
	 * 1) A transaction has just been committed, generating a new index job.
	 * This method is called with the new index job. It will either be started
	 * right away and assigned as the current job. However if another job is
	 * already running then this job will be held until that finishes. We only
	 * keep track of one job in the future, so potentially if multiple jobs are
	 * waiting they will be merged into a single job covering all the
	 * submissions across them all.
	 * 
	 * 2) The current job has finished processing and the next job in the queue
	 * needs to be run. For this case the parameter newJob is null. The nextJob
	 * pointer will move up to the currentJob freeing it's old position, and
	 * then it will be executed right away.
	 * 
	 * @param newJob
	 *            A new job to add to the queue, or null to run the next job
	 *            (this should only be called by the current index job).
	 */
	public synchronized void runNextJob(IndexJob newJob) {
		
		if (newJob == null) {
			// This means the current job has finished, so we should move on to the next job.
			currentJob = nextJob;
			nextJob = null;
			if (currentJob != null)
				currentJob.now();
			return;
		}
		
		
		if (currentJob == null) {
			// Nothing is running, so launch the job.
			currentJob = newJob;
			newJob.now();
		} else if (nextJob == null) {
			// The next job is free, so just put it there until ready.
			nextJob = newJob;
		} else {
			// We allready have the next job in the queue, so merge the two jobs together.
			nextJob = nextJob.mergeJob(newJob);
		}
	}
	
	/**
	 * This is an abstract IndexJob, see either the UpdateIndexJob or
	 * RebuildIndexJob for a specific implementation. This class just shares the
	 * common code between these two jobs.
	 * 
	 */
	public abstract class IndexJob extends Job {

		// The number of submissions which have been processed so far.
		public int progress = 0;

		// The total number of submissions to be indexed by this job. This
		// should be updated when the job is created, the job is merged, and
		// lastly just before a job is about to run.
		public int total = 0;
		
		/**
		 * Merge the two jobs together forming one job. The particular
		 * implementation of the job needs to figure out the details so that no
		 * submission update is left without being updated. It may be assumed
		 * that this method will only be called prior to a job actual being
		 * started.
		 * 
		 * @param job
		 *            The job to merge with.
		 * @return The merged job (it may be the same instance, or a new
		 *         instance)
		 */
		public abstract IndexJob mergeJob(IndexJob job);
		
		/**
		 * @return the displayabel label of this index job.
		 */
		public abstract String getLabel();
		
		/**
		 * Write the index.
		 * 
		 * This method may throw the following exceptions, and the caller is
		 * expected to handle them.
		 * 
		 * @throws CorruptIndexException
		 * @throws LockObtainFailedException
		 * @throws IOException
		 */
		public abstract void writeIndex() throws CorruptIndexException, LockObtainFailedException, IOException;
		
		/**
		 * @return The number of processed submissions.
		 */
		public int getProgress() {
			return progress;
		}
		
		/**
		 * @return The total number of submissions to be processed.
		 */
		public int getTotal() {
			return total;
		}
		
		/**
		 * Start an index job.
		 * 
		 * This method handles the exceptions
		 */
		public void doJob() {
			try {
				writeIndex();
				
			} catch(CorruptIndexException cie) {
				// TODO: handle this gracefully.
				Logger.fatal(cie, "Unable to update index because it is corrupted.");
				throw new RuntimeException(cie);
				
			} catch(LockObtainFailedException lofe) {
				// TODO: handle? I think lucene allready waits some amount of time before throwing.
				Logger.error(lofe, "Unable to update search index because it is being locked by another process.");
				throw new RuntimeException(lofe);
				
			} catch (IOException ioe) {
				Logger.error(ioe, "Unable to update search index because of IOException.");
				throw new RuntimeException(ioe);
				
			} finally {
				runNextJob(null);
			}
		}
		
		/**
		 * Write a the provided submission and all associated action logs to the
		 * index writer. This method expects that the submission and action logs
		 * have been removed from the index, either through a specific delete,
		 * or a delete all in the case of rebuilding the index.
		 * 
		 * This method is used to share code between the various index job
		 * implementations so that submissions are written the same no matter
		 * who indexes them first.
		 * 
		 * @param writer
		 *            The index writer.
		 * @param sub
		 *            The submission to index.
		 */
		public void indexSubmission(IndexWriter writer, Submission sub) throws CorruptIndexException, IOException {
			
			StringBuilder searchText = new StringBuilder();
			
			long subId = sub.getId();
			
			String state = sub.getState().getDisplayName();
			searchText.append(state).append(" ");
			
			long searchAssigned = 0;
			String sortAssigned = "";
			if (sub.getAssignee() != null) {
				searchAssigned = sub.getAssignee().getId();
				sortAssigned = sub.getAssignee().getFullName();
				searchText.append(sortAssigned).append(" ");
			}
			
			Date graduationSemester = null;
			if (sub.getGraduationYear() != null) {
				Calendar cal = Calendar.getInstance();
				cal.clear();
				cal.set(Calendar.YEAR, sub.getGraduationYear());
				if (sub.getGraduationMonth() != null)
					cal.set(Calendar.MONTH,sub.getGraduationMonth());
				
				graduationSemester = cal.getTime();
			}
			
			String department = sub.getDepartment();
			String college = sub.getCollege();
			String major = sub.getMajor();
			searchText.append(department).append(" ").append(college).append(" ").append(major).append(" ");
			
			String embargo = null;
			if (sub.getEmbargoType() != null) {
				embargo = sub.getEmbargoType().getName();
				searchText.append(embargo).append(" ");
			}
			
			String degree = sub.getDegree();
			String documentType = sub.getDocumentType();
			searchText.append(degree).append(" ").append(documentType).append(" ");
			
			Date submissionDate = sub.getSubmissionDate();
			
			String studentName = "";
			if (sub.getStudentLastName() != null)
				studentName += sub.getStudentLastName();
			if (sub.getStudentFirstName() != null)
				studentName += sub.getStudentFirstName();
			if (sub.getStudentMiddleName() != null)
				studentName += sub.getStudentMiddleName();
			searchText.append(studentName).append(" ");
			
			String documentTitle = sub.getDocumentTitle();
			String documentAbstract = sub.getDocumentAbstract();
			String documentKeywords = sub.getDocumentKeywords();
			searchText.append(documentTitle).append(" ").append(documentAbstract).append(" ").append(documentKeywords).append(" ");
			
			String primaryDocument = null;
			if (sub.getPrimaryDocument() != null) {
				primaryDocument = sub.getPrimaryDocument().getName();
				searchText.append(primaryDocument).append(" ");
			}
			
			Date licenseAgreementDate = sub.getLicenseAgreementDate();
			Date approvalDate = sub.getApprovalDate();
			Date committeeApprovalDate = sub.getCommitteeApprovalDate();
			Date committeeEmbargoApprovalDate = sub.getCommitteeEmbargoApprovalDate();
			
			String committeeMembers = "";
			for (CommitteeMember member : sub.getCommitteeMembers()) {
				// TODO: sort by display order?
				committeeMembers += member.getLastName() + " " + member.getFirstName() + " " + member.getLastName();
			}
			searchText.append(committeeMembers).append(" ");

			String committeeContactEmail = sub.getCommitteeContactEmail();
			searchText.append(committeeContactEmail).append(" ");
			
			String umiRelease;
			if (sub.getUMIRelease() == null) {
				umiRelease = "";
			} else if (sub.getUMIRelease()) {
				umiRelease = "yes";
			} else {
				umiRelease = "no";
			}
			
			int customActions = 0;
			for (CustomActionValue action : sub.getCustomActions()) {
				if (action.getValue())
					customActions++;
			}
			
			String lastEventEntry = null;
			Date lastEventTime = null;
			
			List<ActionLog> logs = subRepo.findActionLog(sub);
			if (logs.size() > 0) {
				lastEventEntry = logs.get(0).getEntry();
				lastEventTime = logs.get(0).getActionDate();
				searchText.append(lastEventEntry);
			}
			
			Document doc = new Document();
			
			doc.add(new NumericField("subId",Field.Store.YES,true).setLongValue(subId));
			doc.add(new Field("type","submission",Field.Store.YES,Index.NOT_ANALYZED));
			doc.add(new Field("searchText",searchText.toString(),Field.Store.NO,Index.ANALYZED_NO_NORMS));
			if (state != null)
			doc.add(new Field("state",state,Field.Store.NO,Index.NOT_ANALYZED));
			
			doc.add(new NumericField("searchAssigned",Field.Store.NO,true).setLongValue(searchAssigned));
			
			if (sortAssigned != null)
			doc.add(new Field("sortAssigned",sortAssigned,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (graduationSemester != null)
			doc.add(new NumericField("graduationSemester",Field.Store.NO,true).setLongValue(graduationSemester.getTime()));
			
			if (department != null)
			doc.add(new Field("department",department,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (college != null)
			doc.add(new Field("college",college,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (major != null)
			doc.add(new Field("major",major,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (embargo != null)
			doc.add(new Field("embargo",embargo,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (degree != null)
			doc.add(new Field("degree",degree,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (documentType != null)
			doc.add(new Field("documentType",documentType,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (submissionDate != null)
			doc.add(new NumericField("submissionDate",Field.Store.NO,true).setLongValue(submissionDate.getTime()));
			
			if (studentName != null)
			doc.add(new Field("studentName",studentName, Field.Store.NO,Index.NOT_ANALYZED));
			
			if (documentTitle != null)
			doc.add(new Field("documentTitle",documentTitle, Field.Store.NO,Index.NOT_ANALYZED));
			
			if (documentAbstract != null)
			doc.add(new Field("documentAbstract",documentAbstract, Field.Store.NO,Index.NOT_ANALYZED));
			
			if (documentKeywords != null)
			doc.add(new Field("documentKeywords",documentKeywords, Field.Store.NO,Index.NOT_ANALYZED));
			
			if (primaryDocument != null)
			doc.add(new Field("primaryDocument",primaryDocument, Field.Store.NO,Index.NOT_ANALYZED));
			
			if (licenseAgreementDate != null)
			doc.add(new NumericField("licenseAgreementDate",Field.Store.NO,true).setLongValue(licenseAgreementDate.getTime()));
			
			if (approvalDate != null)
			doc.add(new NumericField("approvalDate",Field.Store.NO,true).setLongValue(approvalDate.getTime()));
			
			if (committeeApprovalDate != null)
			doc.add(new NumericField("committeeApprovalDate",Field.Store.NO,true).setLongValue(committeeApprovalDate.getTime()));
			
			if (committeeEmbargoApprovalDate != null)
			doc.add(new NumericField("committeeEmbargoApprovalDate",Field.Store.NO,true).setLongValue(committeeEmbargoApprovalDate.getTime()));
			
			if (committeeMembers != null)
			doc.add(new Field("committeeMembers",committeeMembers,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (committeeContactEmail != null)
			doc.add(new Field("committeeContactEmail",committeeContactEmail,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (umiRelease != null)
			doc.add(new Field("umiRelease",umiRelease,Field.Store.NO,Index.NOT_ANALYZED));
			
			doc.add(new NumericField("customActions",Field.Store.NO,true).setIntValue(customActions));
			
			if (lastEventEntry != null)
			doc.add(new Field("lastEventEntry",lastEventEntry,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (lastEventTime != null)
			doc.add(new NumericField("lastEventTime",Field.Store.NO,true).setLongValue(lastEventTime.getTime()));
			
			writer.addDocument(doc);
			
			for (ActionLog log : logs) {
				
				String logEntry = log.getEntry();
				String logState = log.getSubmissionState().getDisplayName();
				long logSearchAssigned = 0;
				String logSortAssigned = null;
				if (log.getPerson() != null) {
					logSearchAssigned = log.getPerson().getId();
					logSortAssigned = log.getPerson().getFullName();
				}
				Date logTime = log.getActionDate();
				
				// The new special things for action logs.
				doc = new Document();
				doc.add(new NumericField("subId",Field.Store.YES,true).setLongValue(sub.getId()));
				doc.add(new NumericField("logId",Field.Store.YES,true).setLongValue(log.getId()));
				doc.add(new Field("type","actionlog",Field.Store.YES,Index.NOT_ANALYZED));
				
				if (logEntry != null)
				doc.add(new Field("searchText",logEntry,Field.Store.NO,Index.ANALYZED_NO_NORMS));
				
				if (logState != null)
				doc.add(new Field("state",logState,Field.Store.NO,Index.NOT_ANALYZED));
				
				doc.add(new NumericField("searchAssigned",Field.Store.NO,true).setLongValue(logSearchAssigned));
				
				if (logSortAssigned != null)
				doc.add(new Field("sortAssigned",logSortAssigned,Field.Store.NO,Index.NOT_ANALYZED));
				
				if (logEntry != null)
				doc.add(new Field("lastEventEntry",logEntry,Field.Store.NO,Index.NOT_ANALYZED));
				
				if (logTime != null)
				doc.add(new NumericField("lastEventTime",Field.Store.NO,true).setLongValue(logTime.getTime()));
				
				
				// Stuff that is the same as the submission.
				if (graduationSemester != null)
				doc.add(new NumericField("graduationSemester",Field.Store.NO,true).setLongValue(graduationSemester.getTime()));
				
				if (department != null)
				doc.add(new Field("department",department,Field.Store.NO,Index.NOT_ANALYZED));
				
				if (college != null)
				doc.add(new Field("college",college,Field.Store.NO,Index.NOT_ANALYZED));
				
				if (major != null)
				doc.add(new Field("major",major,Field.Store.NO,Index.NOT_ANALYZED));
				
				if (embargo != null)
				doc.add(new Field("embargo",embargo,Field.Store.NO,Index.NOT_ANALYZED));
				
				if (degree != null)
				doc.add(new Field("degree",degree,Field.Store.NO,Index.NOT_ANALYZED));
				
				if (documentType != null)
				doc.add(new Field("documentType",documentType,Field.Store.NO,Index.NOT_ANALYZED));
				
				if (submissionDate != null)
				doc.add(new NumericField("submissionDate",Field.Store.NO,true).setLongValue(submissionDate.getTime()));
				
				if (studentName != null)
				doc.add(new Field("studentName",studentName, Field.Store.NO,Index.NOT_ANALYZED));
				
				if (documentAbstract != null)
				doc.add(new Field("documentAbstract",documentAbstract, Field.Store.NO,Index.NOT_ANALYZED));
				
				if (documentKeywords != null)
				doc.add(new Field("documentKeywords",documentKeywords, Field.Store.NO,Index.NOT_ANALYZED));
				
				if (primaryDocument != null)
				doc.add(new Field("primaryDocument",primaryDocument, Field.Store.NO,Index.NOT_ANALYZED));
				
				if (licenseAgreementDate != null)
				doc.add(new NumericField("licenseAgreementDate",Field.Store.NO,true).setLongValue(licenseAgreementDate.getTime()));
				
				if (approvalDate != null)
				doc.add(new NumericField("approvalDate",Field.Store.NO,true).setLongValue(approvalDate.getTime()));
				
				if (committeeApprovalDate != null)
				doc.add(new NumericField("committeeApprovalDate",Field.Store.NO,true).setLongValue(committeeApprovalDate.getTime()));
				
				if (committeeEmbargoApprovalDate != null)
				doc.add(new NumericField("committeeEmbargoApprovalDate",Field.Store.NO,true).setLongValue(committeeEmbargoApprovalDate.getTime()));
				
				if (committeeMembers != null)
				doc.add(new Field("committeeMembers",committeeMembers,Field.Store.NO,Index.NOT_ANALYZED));
				
				if (committeeContactEmail != null)
				doc.add(new Field("committeeContactEmail",committeeContactEmail,Field.Store.NO,Index.NOT_ANALYZED));
				
				if (umiRelease != null)
				doc.add(new Field("umiRelease",umiRelease,Field.Store.NO,Index.NOT_ANALYZED));
				
				doc.add(new NumericField("customActions",Field.Store.NO,true).setIntValue(customActions));
				
				writer.addDocument(doc);
			} // for logs
		} // indexSubmission(writer,sub)
	} // IndexJob
	
	
	/**
	 * Update the index for a given list of modified submissions. The
	 * submissions may either be new (never been in the index), updated, or
	 * removed.
	 */
	public class UpdateIndexJob extends IndexJob {
		
		// The list of submission ids to update in the index.
		public Set<Long> subIds;
		
		/**
		 * Construct a new update index job.
		 * 
		 * @param submissionIds The list of submissions to index.
		 */
		public UpdateIndexJob(Set<Long> submissionIds) {
			this.subIds = new LinkedSet<Long>();
			this.subIds.addAll(submissionIds);
			progress = 0;
			total = submissionIds.size();
		}
		
		@Override
		public String getLabel() {
			return "Update Index";
		}
		
		@Override
		public IndexJob mergeJob(IndexJob job) {
			
			if (job instanceof RebuildIndexJob) {
				return job;
			} else if (job instanceof UpdateIndexJob) {
				UpdateIndexJob updateJob = (UpdateIndexJob) job;
				this.subIds.addAll(updateJob.subIds);
				progress = 0;
				total = subIds.size();
				return this;
				
			} else {
				throw new IllegalArgumentException("Unable to merge index jobs because the other job is an unsupported: "+job.getClass().getName());
			}
		}
		
		@Override
		public void writeIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
			
	
			// Update the progress and total one last time before starting.
			progress = 0;
			total = subIds.size();

			IndexWriterConfig writerConfig = new IndexWriterConfig(version,standardAnalyzer);
			IndexWriter writer = new IndexWriter(index, writerConfig);
			try {
				for (Long id : subIds) {

					// First, delete everything from with this submission id
					// (submission and actionlogs!)
					writer.deleteDocuments(new TermQuery(new Term("subId",
							NumericUtils.longToPrefixCoded(id))));

					Submission sub = subRepo.findSubmission(id);
					if (sub != null)
						indexSubmission(writer, sub);

					progress++;
				}
			} finally {
				writer.close();
			}
		}
	} // UpdateIndexJob
	
	/**
	 * Rebuild the entire search index.
	 */
	public class RebuildIndexJob extends IndexJob {
		
		/**
		 * Construct a new rebuild index job.
		 */
		public RebuildIndexJob() {
			progress = 0;
			total = (int) subRepo.findSubmissionsTotal();
		}
		
		@Override
		public String getLabel() {
			return "Rebuild Index";
		}
		
		@Override
		public IndexJob mergeJob(IndexJob job) {
			// we're rebuilding the entire index and haven't started, so we can
			// merge with any job.
			return this;
		}
	
		@Override
		public void writeIndex() throws CorruptIndexException,
				LockObtainFailedException, IOException {

			progress = 0;
			total = (int) subRepo.findSubmissionsTotal();

			IndexWriterConfig writerConfig = new IndexWriterConfig(version,
					standardAnalyzer);
			IndexWriter writer = new IndexWriter(index, writerConfig);

			try {
				writer.deleteAll();

				Iterator<Submission> itr = subRepo.findAllSubmissions();

				while (itr.hasNext()) {
					indexSubmission(writer, itr.next());
					progress++;
				}
			} finally {
				writer.close();
			}
		}
		
	} // RebuildIndexJob
}
