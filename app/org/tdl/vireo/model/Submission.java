package org.tdl.vireo.model;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
	 * @return The submitter of this submission.
	 */
	public Person getSubmitter();

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
	public List<String> getDocumentKeywords();

	/**
	 * 
	 * @param keywords A new list of document keywords
	 */
	public void setDocumentKeywords(List<String> keywords);

	/**
	 * 
	 * @return The type of embargo for this submission. Either requested or approved.
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
	 * @return The list of attachments who's type is SUPPLEMENTAL
	 */
	public Set<Attachment> getSupplementalDocuments();

	/**
	 *
	 * @return The list of all attachments.
	 */
	public Set<Attachment> getAttachments();

	/**
	 * Add a new attachment.
	 * @param file The new attachment file.
	 * @param type The type of the attachment.
	 * @return The newly created attachment.
	 */
	public Attachment addAttachment(File file, AttachmentType type) throws IOException;

	/**
	 * 
	 * @return The a list of all committee mebers associated with this submission.
	 */
	public List<CommitteeMember> getCommitteeMembers();

	/**
	 * Add a new committeeMember
	 * @param firstName The first name of the new member.
	 * @param lastName The last name of the new member.
	 * @param middleInitial The middle initial of the new member.
	 * @param chair Weather this member is the chair or a co-chair.
	 * @return The newly created member.
	 */
	public CommitteeMember addCommitteeMember(String firstName, String lastName,
			String middleInitial, Boolean chair);

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
	 * @return The disposition of the committee when the application was approved or declined.
	 */
	public String getCommitteeDisposition();

	/**
	 * 
	 * @param disposition The new disposition of the committee.
	 */
	public void setCommitteeDisposition(String disposition);

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
	 * 
	 * @return The degree for this submission.
	 */
	public Degree getDegree();

	/**
	 * 
	 * @param degree The new degree for this submission.
	 */
	public void setDegree(Degree degree);

	/**
	 * 
	 * @return The degree department that will award this degree.
	 */
	public Department getDepartment();

	/**
	 * 
	 * @param department The new department.
	 */
	public void setDepartment(Department department);

	/**
	 * 
	 * @return The college that will award this degree.
	 */
	public College getCollege();

	/**
	 * 
	 * @param college The new college.
	 */
	public void setCollege(College college);

	/**
	 * 
	 * @return The major that will award this degree.
	 */
	public Major getMajor();

	/**
	 * 
	 * @param major The new major.
	 */
	public void setMajor(Major major);

	/**
	 * 
	 * @return The document type for this submission.
	 */
	public DocumentType getDocumentType();

	/**
	 * 
	 * @param documentType The new document type.
	 */
	public void setDocumentType(DocumentType documentType);

	/**
	 * 
	 * @return The graduation year
	 */
	public int getGraduationYear();

	/**
	 * 
	 * @param year The new graduation year
	 */
	public void setGraduationYear(int year);

	/**
	 * 
	 * @return The graduation month
	 */
	public GraduationMonth getGraduationMonth();

	/**
	 * 
	 * @param month The new graduation month.
	 */
	public void setGraduationMonth(GraduationMonth month);

	/**
	 * 
	 * @return The name of the spring bean representing the state of this application.
	 */
	public String getStatus();

	/**
	 * 
	 * @param status The new spring bean representing the state of this application.
	 */
	public void setStatus(String status);

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
	 * @return A set of all custom action values.
	 */
	public Set<CustomActionValue> getCustomActions();

	/**
	 * Add a new custom action.
	 * @param definition The custom action definition.
	 * @param value The vaule of the custom action.
	 * @return The newly created custom action value.
	 */
	public CustomActionValue addCustomAction(CustomActionDefinition definition,
			Boolean value);

}
