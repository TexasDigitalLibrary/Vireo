package org.tdl.vireo.search.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;
import org.tdl.vireo.error.ErrorLog;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Submission;

import play.Logger;
import play.jobs.Job;
import play.modules.spring.Spring;

/**
 * This is an abstract lucene job, from which all lucene jobs must extend. Jobs
 * are background threads to update the index. This abstract implementation
 * shares code between the various implementations.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public abstract class LuceneAbstractJobImpl extends Job {
	
	// The indexer who's background job this belongs too.
	public final LuceneIndexerImpl indexer;
	
	// The number of submissions which have been processed so far.
	public int progress = 0;

	// The total number of submissions to be indexed by this job. This
	// should be updated when the job is created, the job is merged, and
	// lastly just before a job is about to run.
	public int total = 0;
	
	// Flag to stop this job immediately abandoning any results.
	public boolean cancel = false;
	
	/**
	 * Construct a new index job. 
	 * 
	 * @param indexer The indexer
	 */
	public LuceneAbstractJobImpl(LuceneIndexerImpl indexer) {
		this.indexer = indexer;
	}
	
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
	public abstract LuceneAbstractJobImpl mergeJob(LuceneAbstractJobImpl job);
	
	/**
	 * @return the displayabel label of this index job.
	 */
	public abstract String getLabel();
	
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
	 * Single to the running job that it should be cancelled. If the job is
	 * currently in progress it will stop after the next submission, rollback
	 * any uncommitted changes to the index and finish processing.
	 */
	public void cancelJob() {
		this.cancel = true;
	}
	
	/**
	 * Start an index job.
	 * 
	 * This method handles the exceptions
	 */
	public void doJob() {
		try {
			if (cancel) {
				throw new InterruptedException("Lucene '"+this.getLabel()+"' job recieved a cancel request before begining processing.");
			}
			
			long start = System.currentTimeMillis();
							
			writeIndex();
			
			Logger.debug("Lucene '"+this.getLabel()+"' job processed "+total+" submissions completed succesfully in " + ((System.currentTimeMillis()-start)/1000F) +" seconds.");
			
			// If we successfully finished, then the index is no longer corrupted.
			indexer.corruptIndex = false;

		} catch(CorruptIndexException cie) {
			
			// Attempt to automaticall rebuild the index without intervention. To prevent infinate loops we will only rebuild a maximum of 5 times.
			if (!indexer.corruptIndex) {
				Logger.error(cie, "Lucene is unable to update index because it is corrupted, attempting to recover by rebuilding the index.");
				
				ErrorLog errorLog = Spring.getBeanOfType(ErrorLog.class);
				errorLog.logError(cie,"Updating search index");
				
				indexer.deleteAndRebuild(false);
				cancel = true;
				
			} else {
				Logger.fatal(cie, "Lucene's attempt to rebuild a corrupted index has failed. No further attempts will be made, and searching is disabled.");
				
				ErrorLog errorLog = Spring.getBeanOfType(ErrorLog.class);
				errorLog.logError(cie, "Updating search index");
			}
			throw new RuntimeException(cie);
			
		} catch(LockObtainFailedException lofe) {
			Logger.error(lofe, "Lucene is unable to update search index because it is being locked by another process.");
			
			ErrorLog errorLog = Spring.getBeanOfType(ErrorLog.class);
			errorLog.logError(lofe, "Updating search index");
			
			throw new RuntimeException(lofe);
			
		} catch (IOException ioe) {
			Logger.error(ioe, "Lucene is unable to update search index because of IO exception.");
			
			ErrorLog errorLog = Spring.getBeanOfType(ErrorLog.class);
			errorLog.logError(ioe, "Updating search index");
			
			throw new RuntimeException(ioe);
		} catch (InterruptedException ie) {
			// We were asked to stop.
			Logger.info(ie.getMessage());
		} finally {
			// If we were cancled don't start the next job.
			if (!cancel)
				indexer.runNextJob(null);
		}
	}
	
	/**
	 * Write the index.
	 * 
	 * This method may throw the following exceptions, and the caller is
	 * expected to handle them.
	 * 
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract void writeIndex() throws CorruptIndexException, LockObtainFailedException, IOException,  InterruptedException;
	
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
			sortAssigned = sub.getAssignee().getFormattedName(NameFormat.LAST_FIRST_MIDDLE_BIRTH);
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
		
		Date defenseDate = sub.getDefenseDate();
		
		String department = sub.getDepartment();
		String program = sub.getProgram();
		String college = sub.getCollege();
		String major = sub.getMajor();
		searchText.append(department).append(" ").append(program).append(" ").append(college).append(" ").append(major).append(" ");
		
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
			studentName += sub.getStudentLastName() + " ";
		if (sub.getStudentFirstName() != null)
			studentName += sub.getStudentFirstName() + " ";
		if (sub.getStudentMiddleName() != null)
			studentName += sub.getStudentMiddleName() + " ";
		searchText.append(studentName).append(" ");
		
		searchText.append(sub.getStudentFormattedName(NameFormat.LAST_FIRST_BIRTH)).append(" ");
		searchText.append(sub.getStudentFormattedName(NameFormat.FIRST_LAST_BIRTH)).append(" ");

		
		String studentEmail = sub.getSubmitter().getEmail();
		searchText.append(studentEmail).append(" ");
		
		String institutionalIdentifier = sub.getSubmitter().getInstitutionalIdentifier();
		searchText.append(institutionalIdentifier).append(" ");
		
		String documentTitle = sub.getDocumentTitle();
		String documentAbstract = sub.getDocumentAbstract();
		String documentKeywords = sub.getDocumentKeywords();
		searchText.append(documentTitle).append(" ").append(documentAbstract).append(" ").append(documentKeywords).append(" ");
		
		String documentSubjects = "";
		for (String subject : sub.getDocumentSubjects()) {
			documentSubjects += subject + " ";
		}
		searchText.append(documentSubjects).append(" ");
		
		String documentLanguage = null;
		if (sub.getDocumentLanguageLocale() != null) {
			Locale locale = sub.getDocumentLanguageLocale();
			searchText.append(locale.getDisplayName()).append(" ");
			searchText.append(locale.getDisplayLanguage()).append(" ");
			searchText.append(locale.getDisplayCountry()).append(" ");
			searchText.append(locale.getDisplayVariant()).append(" ");
			
			documentLanguage = locale.getDisplayName();
		}
		
		String publishedMaterial = sub.getPublishedMaterial();
		searchText.append(publishedMaterial).append(" ");
		
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
			committeeMembers += member.getFormattedName(NameFormat.LAST_FIRST) + " " + member.getFormattedRoles();
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
		
		String degreeLevel = null;
		if (sub.getDegreeLevel() != null)
			degreeLevel = sub.getDegreeLevel().name();
		searchText.append(degreeLevel).append(" ");
		
		String depositId = sub.getDepositId();
		searchText.append(depositId).append(" ");
		
		String reviewerNotes = sub.getReviewerNotes();
		searchText.append(reviewerNotes).append(" ");
		
		String lastEventEntry = null;
		Date lastEventTime = null;
		
		String orcid = sub.getOrcid();
		
		List<ActionLog> logs = indexer.subRepo.findActionLog(sub);
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
		
		if (defenseDate != null)
		doc.add(new NumericField("defenseDate",Field.Store.NO,true).setLongValue(defenseDate.getTime()));
		
		if (department != null)
		doc.add(new Field("department",department,Field.Store.NO,Index.NOT_ANALYZED));
		
		if (program != null)
		doc.add(new Field("program",program,Field.Store.NO,Index.NOT_ANALYZED));
			
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
		
		if (studentEmail != null)
		doc.add(new Field("studentEmail",studentEmail, Field.Store.NO,Index.NOT_ANALYZED));
		
		if (institutionalIdentifier != null)
		doc.add(new Field("institutionalIdentifier",institutionalIdentifier,Field.Store.NO,Index.NOT_ANALYZED));
		
		if (documentTitle != null)
		doc.add(new Field("documentTitle",documentTitle, Field.Store.NO,Index.NOT_ANALYZED));
		
		if (documentAbstract != null)
		doc.add(new Field("documentAbstract",documentAbstract, Field.Store.NO,Index.NOT_ANALYZED));
		
		if (documentKeywords != null)
		doc.add(new Field("documentKeywords",documentKeywords, Field.Store.NO,Index.NOT_ANALYZED));
		
		if (documentSubjects != null)
		doc.add(new Field("documentSubjects",documentSubjects, Field.Store.NO,Index.NOT_ANALYZED));
		
		if (documentLanguage != null)
		doc.add(new Field("documentLanguage",documentLanguage, Field.Store.NO,Index.NOT_ANALYZED));

		if (publishedMaterial != null)
		doc.add(new Field("publishedMaterial",publishedMaterial, Field.Store.NO,Index.NOT_ANALYZED));
		
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

		if (degreeLevel != null)
		doc.add(new Field("degreeLevel",degreeLevel,Field.Store.NO,Index.NOT_ANALYZED));
		
		if (depositId != null)
		doc.add(new Field("depositId",depositId,Field.Store.NO,Index.NOT_ANALYZED));
		
		if (reviewerNotes != null)
		doc.add(new Field("reviewerNotes",reviewerNotes,Field.Store.NO,Index.NOT_ANALYZED));
		
		if (lastEventEntry != null)
		doc.add(new Field("lastEventEntry",lastEventEntry,Field.Store.NO,Index.NOT_ANALYZED));
		
		if (lastEventTime != null)
		doc.add(new NumericField("lastEventTime",Field.Store.NO,true).setLongValue(lastEventTime.getTime()));
		
		if (orcid !=null)
		doc.add(new Field("orcid",orcid,Field.Store.NO,Index.NOT_ANALYZED));
		
		writer.addDocument(doc);
		
		for (ActionLog log : logs) {
			
			Long logId = log.getId();
			String logEntry = log.getEntry();
			String logState = log.getSubmissionState().getDisplayName();
			long logSearchAssigned = 0;
			String logSortAssigned = null;
			if (log.getPerson() != null) {
				logSearchAssigned = log.getPerson().getId();
				logSortAssigned = log.getPerson().getFormattedName(NameFormat.FIRST_LAST);
			}
			Date logTime = log.getActionDate();
			
			// The new special things for action logs.
			doc = new Document();
			doc.add(new NumericField("subId",Field.Store.YES,true).setLongValue(subId));
			doc.add(new NumericField("logId",Field.Store.YES,true).setLongValue(logId));
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
			
			if (defenseDate != null)
			doc.add(new NumericField("defenseDate",Field.Store.NO,true).setLongValue(defenseDate.getTime()));
			
			if (department != null)
			doc.add(new Field("department",department,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (program != null)
			doc.add(new Field("program",program,Field.Store.NO,Index.NOT_ANALYZED));
			
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
			
			if (studentEmail != null)
			doc.add(new Field("studentEmail",studentEmail, Field.Store.NO,Index.NOT_ANALYZED));
			
			if (institutionalIdentifier != null)
			doc.add(new Field("institutionalIdentifier",institutionalIdentifier,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (documentAbstract != null)
			doc.add(new Field("documentAbstract",documentAbstract, Field.Store.NO,Index.NOT_ANALYZED));
			
			if (documentKeywords != null)
			doc.add(new Field("documentKeywords",documentKeywords, Field.Store.NO,Index.NOT_ANALYZED));
			
			if (documentSubjects != null)
			doc.add(new Field("documentSubjects",documentSubjects, Field.Store.NO,Index.NOT_ANALYZED));
			
			if (documentLanguage != null)
			doc.add(new Field("documentLanguage",documentLanguage, Field.Store.NO,Index.NOT_ANALYZED));
			
			if (publishedMaterial != null)
			doc.add(new Field("publishedMaterial",publishedMaterial, Field.Store.NO,Index.NOT_ANALYZED));
			
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
			
			if (degreeLevel != null)
			doc.add(new Field("degreeLevel",degreeLevel,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (depositId != null)
			doc.add(new Field("depositId",depositId,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (reviewerNotes != null)
			doc.add(new Field("reviewerNotes",reviewerNotes,Field.Store.NO,Index.NOT_ANALYZED));
			
			if (orcid != null)
			doc.add(new Field("orcid",orcid,Field.Store.NO,Index.NOT_ANALYZED));
			
			writer.addDocument(doc);
			
			// Detach the log so it dosn't keep stacking up in memory.
			log.detach();
		} // for logs
	} // indexSubmission(writer,sub)
} // IndexJob
