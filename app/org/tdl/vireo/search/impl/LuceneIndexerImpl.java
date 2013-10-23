package org.tdl.vireo.search.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.sf.oval.internal.util.LinkedSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.Indexer;

import play.Logger;
import play.Play;

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
 * defenseDate: The date of the student's defense.
 * 
 * program: the program, a single token not analyzed (search and sort)
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
 * studentEmail: The student's email for sorting.
 * 
 * institutionalIdentifier: The student's institutional identifier
 * 
 * documentTitle: The document's title for sorting.
 * 
 * documentAbstract: The document's abstract for sorting.
 * 
 * documentKeywords: The document's keywords for sorting.
 * 
 * documentSubjects: The document's subjects for sorting.
 * 
 * documentLanguage: The document's language both code and name.
 * 
 * publishedMaterial: The description of previously published material
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
 * degreeLevel: the degree level, single token not analyzed just for sorting
 * 
 * depositId: The deposit id for sorting
 * 
 * reviewerNotes: The notes from reviewers for sorting
 * 
 * lastEventEntry: The textual entry of the last log event for sorting.
 * 
 * lastEventTime: The date of the last entry for sorting.
 * 
 * orcid: The ORCID of the student.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */

public class LuceneIndexerImpl implements Indexer {

	// The current updated submissions for each thread.
	public ThreadLocal<Set<Long>> transactionLocal = new ThreadLocal<Set<Long>>();	
	
	// Thread state (Please don't mess with directly!) Use the method
	// runNextJob() so all access is synchronized
	private LuceneAbstractJobImpl currentJob = null;
	private LuceneAbstractJobImpl nextJob = null;
	
	// Static Lucene configuration
	public final File indexFile;
	public final Directory index;
	public final Version version = Version.LUCENE_36;
	public final Analyzer standardAnalyzer = new StandardAnalyzer(version);
	
	// Spring dependencies
	public SubmissionRepository subRepo = null;
	
	// A flag to designate weather the current index is corrupted. This does two
	// things, prevents searches across the corrupted index and serves as a flag
	// to prevent infinate rebuilding of a corrupted index.
	public boolean corruptIndex = false;
	
	/**
	 * Construct a new LuceneIndexer
	 */
	public LuceneIndexerImpl() throws IOException {
		indexFile = new File(Play.configuration.getProperty("index.path","data/indexes"));
		index = FSDirectory.open(indexFile);
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
			updated(sub.getId());
		}
	}
	
	@Override
	public void updated(Long submissionId) {
		
		if (submissionId == null)
			throw new IllegalArgumentException("An submission id is required");

		// Create a new transaction if needed.
		if (transactionLocal.get() == null)
			transactionLocal.set(new LinkedSet<Long>());
		
		// Add the submission to the transaction
		transactionLocal.get().add(submissionId);
		
	}
	
	@Override
	public void updated(List<Long> submissionIds) {
		
		if (submissionIds == null)
			throw new IllegalArgumentException("A list of submission ids are required");
		
		for (Long submissionId : submissionIds) {
			updated(submissionId);
		}
	}
	
	@Override
	public boolean isUpdated(Long submissionId) {
		
		if (transactionLocal.get() != null) {
			return transactionLocal.get().contains(submissionId);
		}
		return false;
	}

	@Override
	public boolean isUpdated(Submission submission) {
		return isUpdated(submission.getId());
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
	public void commit(boolean wait) {
	
		Set<Long> txn = transactionLocal.get();
		if (txn != null && txn.size() > 0) {
			LuceneAbstractJobImpl newJob = new LuceneUpdateJob(this,txn);
			
			if (wait) {
				// We will run this job in the current thread and wait for it to
				// finish. However we have to wait around for all other background
				// jobs to complete. We must also be thread safe about it.
				while (true) {
					synchronized (this) {
						if (currentJob == null && nextJob == null) {
							// Start the job now. Anyone attempting to schedule will
							// block until the end of this synchronized block.
							newJob.doJob();
							return;
						}
					}
					// Give all other thread a chance to do their thing now that we are out of the synchronized block. 
					Thread.yield();
				}
			} else {
				// Schedule the job as a background task.
				runNextJob(newJob);
			}
		}
	}
	
	/**
	 * Rebuild the entire search index from scratch.
	 * 
	 * Create a new job and add it to the queue.
	 */
	@Override
	public void rebuild(boolean wait) {
		LuceneAbstractJobImpl newJob = new LuceneRebuildJobImpl(this);
		
		if (wait) {
			// We will run this job in the current thread and wait for it to
			// finish. However we have to wait around for all other background
			// jobs to complete. We must also be thread safe about it.
			while (true) {
				synchronized (this) {
					if (currentJob == null && nextJob == null) {
						// Start the job now. Anyone attempting to schedule will
						// block until the end of this synchronized block.
						newJob.doJob();
						return;
					}
				}
				// Give all other thread a chance to do their thing now that we are out of the synchronized block. 
				Thread.yield();
			}
		} else {
			// Schedule the job as a background task.
			runNextJob(newJob);
		}
	}
	
	/**
	 * Delete the index and rebuild.
	 * 
	 * This method allows for recovery of corrupted indexes because it blows
	 * away whatever is in the index directory.
	 * 
	 * This method will (even with the wait flag turned off) will block until
	 * all the current jobs have been canceled and the index files have been
	 * deleted.
	 */
	@Override
	public void deleteAndRebuild(boolean wait) {

		Logger.info("Removing the entire lucene index at '"+indexFile.getPath()+"' and rebuilding.'");
		
		// First, cancel any currently running jobs.
		nextJob = null;
		if (currentJob != null)
			currentJob.cancelJob();
		synchronized(this) {
			
			// Wait for the jobs to stop
			while(isJobRunning()) {
				Thread.yield();
			}
			
			// Delete everything in the index directory.
			if (indexFile != null && indexFile.exists() ) {
				File[] files = indexFile.listFiles();
				for (File file : files) {
					file.delete();
				}
			}
			
			// Create our job, and put it on the queue. This will prevent any other jobs from jumping before this one while we are in the synchronized queue.
			LuceneAbstractJobImpl newJob = new LuceneRebuildJobImpl(this);
			currentJob = newJob;
		}
		
		if (wait) {
			// Start the rebuild job in the current thread.
			currentJob.doJob();
		} else {
			// Start it as a background job.
			currentJob.now();
		}
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
	public synchronized void runNextJob(LuceneAbstractJobImpl newJob) {
		
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
			// We already have the next job in the queue, so merge the two jobs together.
			nextJob = nextJob.mergeJob(newJob);
		}
	}
}
