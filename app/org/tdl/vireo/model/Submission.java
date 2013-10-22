package org.tdl.vireo.model;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.tdl.vireo.proquest.ProquestLanguage;
import org.tdl.vireo.state.State;

/**
 * The submission object is really the heart of the vireo workflow data model.
 * It represents the student's ETD application.  The only absolutely required
 * parameters are the system assigned identifier, and the original person
 * submitting. Instead of treating committee members as first class person
 * objects like we do for students we specialize their treatment as a separate
 * object type. The reason for this is because the institution may not be able
 * to authenticate the committee member through shibboleth because there are
 * cases where the committee member is outside of the university, perhaps
 * because this is a joint degree program or they have retired. To handle this
 * case, when vireo sends an email to the faculty member for approval an email
 * hash is generated the chair is expected to use the hash to authenticate their
 * return to vireo where they can approve the application.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public interface Submission extends AbstractModel {

	/**
	 * @return The original submitter who created this submission. This may not
	 *         necessarily the graduating student. In cases where the submission
	 *         is being made by a proxy like by a staff member then the
	 *         submitter would be the person object for the staff member who
	 *         original created the submission.
	 */
	public Person getSubmitter();

	/**
	 * @return The first name of the student who authored this submission.
	 */
	public String getStudentFirstName();

	/**
	 * @param firstName
	 *            The new first name of the student.
	 */
	public void setStudentFirstName(String firstName);

	/**
	 * @return The last name of the student who authored this submission.
	 */
	public String getStudentLastName();

	/**
	 * @param lastName
	 *            The new last name of the student.
	 */
	public void setStudentLastName(String lastName);

	/**
	 * @return The middle name of the student who authored this submission.
	 */
	public String getStudentMiddleName();

	/**
	 * @param middleName
	 *            The new middle name of the student.
	 */
	public void setStudentMiddleName(String middleName);

	/**
	 * 
	 * @return The birth year for the student woh authored this submission.
	 */
	public Integer getStudentBirthYear();

	/**
	 * @param year
	 *            The new birth year for the student.
	 */
	public void setStudentBirthYear(Integer year);
	
	/**
	 * @return The ORCID identifier of the student who authored this submission.
	 */
	public String getOrcid();

	/**
	 * @param orcidString
	 *            The ORCID identifier of the student who authored this submission.
	 */
	public void setOrcid(String orcidString);	
	
	/**
	 * @param format
	 *            The format specifying how the name should be constructed.
	 * 
	 * @return The student's name according to the format specified.
	 */
	public String getStudentFormattedName(NameFormat format);
	
	
	/**
	 * @return The document title.
	 */
	public String getDocumentTitle();

	/**
	 * 
	 * @param title The new document title.
	 */
	public void setDocumentTitle(String title);

	/**
	 * 
	 * @return The document abstract
	 */
	public String getDocumentAbstract();

	/**
	 * 
	 * @param docAbstract The new document abstract
	 */
	public void setDocumentAbstract(String docAbstract);

	/**
	 * 
	 * @return A list of document keywords
	 */
	public String getDocumentKeywords();

	/**
	 * 
	 * @param keywords A new list of document keywords
	 */
	public void setDocumentKeywords(String keywords);
	
	/**
	 * @return the mutable list of document subjects. So you may use the
	 *         add/remove/clear methods available on the list interface to
	 *         modify this data. The list in it's current form will be saved
	 *         when the submission object is receives a save() action.
	 */
	public List<String> getDocumentSubjects();

	/**
	 * Add a new subject to the end of the list of subjects for this document.
	 * 
	 * @param subject
	 *            The subject to add.
	 */
	public void addDocumentSubject(String subject);

	/**
	 * Remove the first occurrence of the subject from the list of subjects for
	 * this document..
	 * 
	 * @param subject
	 *            The subject to remove.
	 */
	public void removeDocumentSubject(String subject);

	/**
	 * @param language
	 *            The new language of the document. Must be a valid java locale.
	 */
	public void setDocumentLanguage(String language);

	/**
	 * @return Language of the document
	 */
	public String getDocumentLanguage();

	/**
	 * @return The locale of the document language, or null if not set or
	 *         invalid.
	 */
	public Locale getDocumentLanguageLocale();

	
	/**
	 * Set whether this submission contains any material that has been
	 * previously published. If the value is null, then no material has been
	 * previously published. If it is not null then some material may have been
	 * previously published and the contents of this field should indicate which
	 * material has been published.
	 * 
	 * @param material
	 *            English description identifying the material previously
	 *            published.
	 */
	public void setPublishedMaterial(String material);

	/**
	 * Whether this submission contains material which has been previously
	 * published. Null indicates that no material has been previously published,
	 * otherwise a human readable description of the published material is
	 * supplied.
	 * 
	 * @return Previously published descriptor
	 */
	public String getPublishedMaterial();
	
	/**
	 * 
	 * @return The type of embargo for this submission. Either requested or
	 *         approved.
	 */
	public EmbargoType getEmbargoType();

	/**
	 * 
	 * @param embargo The new embargo type.
	 */
	public void setEmbargoType(EmbargoType embargo);

	/**
	 * 
	 * @return The attachment who's type is PRIMARY.
	 */
	public Attachment getPrimaryDocument();

	/**
	 * 
	 * @return The unordered list of attachments who's type is SUPPLEMENTAL
	 */
	public List<Attachment> getSupplementalDocuments();

	/**
	 *
	 * @return The unordered list of all attachments.
	 */
	public List<Attachment> getAttachments();
	
	/**
	 * @param type
	 *            The type of attachment.
	 * @return Return a list of all attachments of the given type.
	 */
	public List<Attachment> getAttachmentsByType(AttachmentType...types);

	/**
	 * Add a new attachment from a file.
	 * 
	 * @param file
	 *            The new attachment file.
	 * @param type
	 *            The type of the attachment.
	 * @return The newly created attachment.
	 */
	public Attachment addAttachment(File file, AttachmentType type)
			throws IOException;

	/**
	 * Add a new attachment from a byte arra.
	 * 
	 * @param content
	 *            The contents of the new attachment.
	 * @param filename
	 *            The filename of the file.
	 * @param type
	 *            The type of attachment.
	 * @return The newly created attachment.
	 */
	public Attachment addAttachment(byte[] content, String filename, AttachmentType type) throws IOException;

	/**
	 * @return The specific attachment
	 */
	public Attachment findAttachmentById(Long id);
	
	/**
	 * @return the specific attachment
	 */
	public Attachment findAttachmentByName(String name);
	
	/**
	 * 
	 * @return The a list of all committee mebers associated with this submission.
	 */
	public List<CommitteeMember> getCommitteeMembers();
	
	/**
	 * Add a new committeeMember
	 * @param firstName The first name of the new member.
	 * @param lastName The last name of the new member.
	 * @param middleName The middle name of the new member.
	 * @return The newly created member.
	 */
	public CommitteeMember addCommitteeMember(String firstName, String lastName, String middleName);

	/**
	 * 
	 * @return Get the committee's contact email address.
	 */
	public String getCommitteeContactEmail();

	/**
	 * 
	 * @param email Set the committee's contact email address.
	 */
	public void setCommitteeContactEmail(String email);

	/**
	 * 
	 * @return The secrete committee email hash for approvals.
	 */
	public String getCommitteeEmailHash();

	/**
	 * 
	 * @param hash The new secrete committee email hash.
	 */
	public void setCommitteeEmailHash(String hash);

	/**
	 * 
	 * @return The date the committee approved this submission, or null if it has not been approved.
	 */
	public Date getCommitteeApprovalDate();

	/**
	 * 
	 * @param date Set the date the committee approved this submission.
	 */
	public void setCommitteeApprovalDate(Date date);

	/**
	 * 
	 * @return The date the committee approved the embargo type of this submission, or null if it has not been approved.
	 */
	public Date getCommitteeEmbargoApprovalDate();

	/**
	 * 
	 * @param date Set the date the committee approved the embargo type of this submission.
	 */
	public void setCommitteeEmbargoApprovalDate(Date date);

	/**
	 * 
	 * @return The date the submission was submitted for review.
	 */
	public Date getSubmissionDate();

	/**
	 * @param data The new date the submission was submitted for review.
	 */
	public void setSubmissionDate(Date date);

	/**
	 * 
	 * @return The date the submission was approved for release, or null if it has not been approved.
	 */
	public Date getApprovalDate();

	/**
	 * 
	 * @param date The new date this submission was approved.
	 */
	public void setApprovalDate(Date date);

	/**
	 * 
	 * @return The date the submitter agreed to the license for this submission, or null if the submitter has not agreed.
	 */
	public Date getLicenseAgreementDate();

	/**
	 * 
	 * @param date The new date the submitter agreed to the license.
	 */
	public void setLicenseAgreementDate(Date date);

	/**
	 * @return The date of the submitter's defense.
	 */
	public Date getDefenseDate();
	
	/**
	 * @param date The date of the submitter's defense.
	 */
	public void setDefenseDate(Date date);
	
	/**
	 * 
	 * @return The degree for this submission.
	 */
	public String getDegree();

	/**
	 * 
	 * @param degree The new degree for this submission.
	 */
	public void setDegree(String degree);

	/**
	 * @return The degree level for this submission
	 */
	public DegreeLevel getDegreeLevel();
	
	/**
	 * @param level The new degree level for this submission
	 */
	public void setDegreeLevel(DegreeLevel level);
	
	/**
	 * 
	 * @return The degree department that will award this degree.
	 */
	public String getDepartment();

	/**
	 * 
	 * @param department The new department.
	 */
	public void setDepartment(String department);

	/**
	 * 
	 * @return The college that will award this degree.
	 */
	public String getCollege();	
	
	/**
	 * 
	 * @param college The new college.
	 */
	public void setCollege(String college);
	
	/**
	 * 
	 * @return The program associated with the submission
	 */
	public String getProgram();
	
	/**
	 * 
	 * @param program The program associated with the submission
	 */
	public void setProgram(String program);

	/**
	 * 
	 * @return The major that will award this degree.
	 */
	public String getMajor();

	/**
	 * 
	 * @param major The new major.
	 */
	public void setMajor(String major);

	/**
	 * 
	 * @return The document type for this submission.
	 */
	public String getDocumentType();

	/**
	 * 
	 * @param documentType The new document type.
	 */
	public void setDocumentType(String documentType);

	/**
	 * 
	 * @return The graduation year
	 */
	public Integer getGraduationYear();

	/**
	 * 
	 * @param year The new graduation year
	 */
	public void setGraduationYear(Integer year);

	/**
	 * 
	 * @return The graduation month
	 */
	public Integer getGraduationMonth();

	/**
	 * 
	 * @param month The new graduation month.
	 */
	public void setGraduationMonth(Integer month);

	/**
	 * 
	 * @return The current application's state
	 */
	public State getState();

	/**
	 * 
	 * @param state The new state of this submission
	 */
	public void setState(State state);

	/**
	 * 
	 * @return The assigned reviewer for this submission.
	 */
	public Person getAssignee();

	/**
	 * 
	 * @param assignee The new assigned reviewer.
	 */
	public void setAssignee(Person assignee);

	/**
	 * 
	 * @return Weather the student has selected to release this submission to UMI.
	 */
	public Boolean getUMIRelease();

	/**
	 * 
	 * @param umiRelease The new UMI release status.
	 */
	public void setUMIRelease(Boolean umiRelease);

	/**
	 * 
	 * @return An unordered list of all custom action values.
	 */
	public List<CustomActionValue> getCustomActions();
	
	/**
	 * @param definition
	 *            The definition of the single action to return.
	 * @return The value for the provided custom action definition, otherwise
	 *         null if no value found for the definition.
	 */
	public CustomActionValue getCustomAction(CustomActionDefinition definition);

	/**
	 * Add a new custom action.
	 * 
	 * @param definition
	 *            The custom action definition.
	 * @param value
	 *            The value of the custom action.
	 * @return The newly created custom action value.
	 */
	public CustomActionValue addCustomAction(CustomActionDefinition definition,
			Boolean value);
		
	/**
	 * @return The deposit id associated with submission. It usually is a URL
	 *         but different repositories may use another identifier type.
	 */
	public String getDepositId();
	
	/**
	 * @param depositId
	 *            Set the deposit id for this submission.
	 */
	public void setDepositId(String depositId);
	
	
	/**
	 * @return The date the submission was deposited.
	 */
	public Date getDepositDate();
	
	/**
	 * @param depositDate
	 * 			The date the submission was deposited.
	 */
	public void setDepositDate(Date depositDate);
	
	/**
	 * @return Return the current reviewer notes. May only be accessed if a
	 *         reviewer or above.
	 */
	public String getReviewerNotes();

	/**
	 * Set new reviewer notes, may only be set by reviewers or above. Changes
	 * are logged in the action log, but will be marked as private.
	 * 
	 * @param notes
	 *            The new notes for this submission.
	 */
	public void setReviewerNotes(String notes);
	
	/**
	 * This is a short cut method to improve the performance of displaying
	 * search results. It is suggested that implementors cache the entry of the
	 * last action log item to improve performance.
	 * 
	 * @return The entry of the last log event to affect this submission.
	 */
	public String getLastLogEntry();

	/**
	 * This is a short cut method to improve the performance of displaying
	 * search results. It is suggested that implementors cache the date of the
	 * last action log to improve performance.
	 * 
	 * @return The date of the last log event to affect this submission.
	 */
	public Date getLastLogDate();
	
	/**
	 * Create an action log entry about this submission.
	 * 
	 * @param entry
	 *            The entry text to be saved, note " by User" will be appended
	 *            to the end of this entry text recording who made the action.
	 * @return The unsaved action log object.
	 */
	public ActionLog logAction(String entry);
}
