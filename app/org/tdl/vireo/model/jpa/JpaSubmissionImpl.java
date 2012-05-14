package org.tdl.vireo.model.jpa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;

import play.db.jpa.Model;

/**
 * Jpa specefic implementation of Vireo's Submission interface.
 * 
 * TODO: Create actionLog items when the submission is changed.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "Submission")
public class JpaSubmissionImpl extends Model implements Submission {

	@OneToOne(optional = false, targetEntity = JpaPersonImpl.class)
	public Person submitter;

	public String documentTitle;
	public String documentAbstract;
	public String documentKeywords;

	@OneToOne(targetEntity = JpaEmbargoTypeImpl.class)
	public EmbargoType embargoType;

	@OneToMany(targetEntity = JpaAttachmentImpl.class, mappedBy = "submission", cascade = CascadeType.ALL)
	public Set<Attachment> attachments;

	@OneToMany(targetEntity = JpaCommitteeMemberImpl.class, mappedBy = "submission", cascade = CascadeType.ALL)
	public List<CommitteeMember> committeeMembers;
	public String committeeContactEmail;
	public String committeeEmailHash;

	@Temporal(TemporalType.TIMESTAMP)
	public Date committeeApporvalDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date committeeEmbargoApprovalDate;
	public String committeeDisposition;

	@Temporal(TemporalType.TIMESTAMP)
	public Date submissionDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date approvalDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date licenseAgreementDate;

	@OneToOne(targetEntity = JpaDegreeImpl.class)
	public Degree degree;

	@OneToOne(targetEntity = JpaDepartmentImpl.class)
	public Department department;

	@OneToOne(targetEntity = JpaCollegeImpl.class)
	public College college;

	@OneToOne(targetEntity = JpaMajorImpl.class)
	public Major major;

	@OneToOne(targetEntity = JpaDocumentTypeImpl.class)
	public DocumentType documentType;

	public Integer graduationYear;

	@OneToOne(targetEntity = JpaGraduationMonthImpl.class)
	public GraduationMonth graduationMonth;

	public String status;

	@OneToOne(targetEntity = JpaGraduationMonthImpl.class)
	public Person assignee;
	public Boolean UMIRelease;

	@OneToMany(targetEntity = JpaCustomActionValueImpl.class, mappedBy = "submission", cascade = CascadeType.ALL)
	public Set<CustomActionValue> customActions;

	/**
	 * Construct a new JpaSubmissionImpl
	 * 
	 * @param submitter
	 *            The student submitting this submission.
	 */
	protected JpaSubmissionImpl(Person submitter) {

		// TODO: check arguments

		this.submitter = submitter;
		this.attachments = new HashSet<Attachment>();
		this.committeeMembers = new ArrayList<CommitteeMember>();
		this.customActions = new HashSet<CustomActionValue>();
	}

	@Override
	public JpaSubmissionImpl save() {
		return super.save();
	}

	@Override
	public JpaSubmissionImpl delete() {
		return super.delete();
	}

	@Override
	public JpaSubmissionImpl refresh() {
		return super.refresh();
	}

	@Override
	public JpaSubmissionImpl merge() {
		return super.merge();
	}

	@Override
	public Person getSubmitter() {
		return submitter;
	}

	@Override
	public String getDocumentTitle() {
		return documentTitle;
	}

	@Override
	public void setDocumentTitle(String title) {
		this.documentTitle = title;
	}

	@Override
	public String getDocumentAbstract() {
		return documentAbstract;
	}

	@Override
	public void setDocumentAbstract(String docAbstract) {
		this.documentAbstract = docAbstract;
	}

	@Override
	public List<String> getDocumentKeywords() {

		// TODO: Split the string documentKeywords into a list and return it.

		return null;
	}

	@Override
	public void setDocumentKeywords(List<String> keywords) {

		// TODO: Combine the list of keywords into a single string value.

		this.documentKeywords = null;
	}

	@Override
	public EmbargoType getEmbargoType() {
		return embargoType;
	}

	@Override
	public void setEmbargoType(EmbargoType embargo) {
		this.embargoType = embargo;
	}

	@Override
	public Attachment getPrimaryDocument() {
		// TODO: search the attachments for the one with type = primary
		// document.
		return null;
	}

	@Override
	public Set<Attachment> getSupplementalDocuments() {
		// TODO: return all attacments with type= suplemental types.

		return attachments;
	}

	@Override
	public Set<Attachment> getAttachments() {
		return attachments;
	}

	@Override
	public Attachment addAttachment(File file, AttachmentType type)
			throws IOException {

		Attachment attachment = new JpaAttachmentImpl(this, type, file);
		attachments.add(attachment);
		return attachment;
	}

	@Override
	public List<CommitteeMember> getCommitteeMembers() {
		return committeeMembers;
	}

	@Override
	public CommitteeMember addCommitteeMember(String firstName,
			String lastName, String middleInitial, Boolean chair) {
		CommitteeMember member = new JpaCommitteeMemberImpl(this, firstName,
				lastName, middleInitial, chair);
		committeeMembers.add(member);
		return member;
	}

	@Override
	public String getCommitteeContactEmail() {
		return committeeContactEmail;
	}

	@Override
	public void setCommitteeContactEmail(String email) {
		
		// TODO: check that the email address is valid.
		
		this.committeeContactEmail = email;
	}

	@Override
	public String getCommitteeEmailHash() {
		return committeeEmailHash;
	}

	@Override
	public void setCommitteeEmailHash(String hash) {
		this.committeeEmailHash = hash;
	}

	@Override
	public Date getCommitteeApprovalDate() {
		return committeeApporvalDate;
	}

	@Override
	public void setCommitteeApprovalDate(Date date) {
		this.committeeApporvalDate = date;
	}

	@Override
	public Date getCommitteeEmbargoApprovalDate() {
		return committeeEmbargoApprovalDate;
	}

	@Override
	public void setCommitteeEmbargoApprovalDate(Date date) {
		this.committeeEmbargoApprovalDate = date;
	}

	@Override
	public String getCommitteeDisposition() {
		return committeeDisposition;
	}

	@Override
	public void setCommitteeDisposition(String disposition) {
		this.committeeDisposition = disposition;
	}

	@Override
	public Date getSubmissionDate() {
		return submissionDate;
	}

	@Override
	public void setSubmissionDate(Date date) {
		this.submissionDate = date;
	}

	@Override
	public Date getApprovalDate() {
		return approvalDate;
	}

	@Override
	public void setApprovalDate(Date date) {
		this.approvalDate = date;
	}

	@Override
	public Date getLicenseAgreementDate() {
		return licenseAgreementDate;
	}

	@Override
	public void setLicenseAgreementDate(Date date) {
		this.licenseAgreementDate = date;
	}

	@Override
	public Degree getDegree() {
		return degree;
	}

	@Override
	public void setDegree(Degree degree) {
		this.degree = degree;
	}

	@Override
	public Department getDepartment() {
		return department;
	}

	@Override
	public void setDepartment(Department department) {
		this.department = department;
	}

	@Override
	public College getCollege() {
		return college;
	}

	@Override
	public void setCollege(College college) {
		this.college = college;
	}

	@Override
	public Major getMajor() {
		return major;
	}

	@Override
	public void setMajor(Major major) {
		this.major = major;
	}

	@Override
	public DocumentType getDocumentType() {
		return documentType;
	}

	@Override
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	@Override
	public int getGraduationYear() {
		return graduationYear;
	}

	@Override
	public void setGraduationYear(int year) {
		this.graduationYear = year;
	}

	@Override
	public GraduationMonth getGraduationMonth() {
		return graduationMonth;
	}

	@Override
	public void setGraduationMonth(GraduationMonth month) {
		this.graduationMonth = month;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public Person getAssignee() {
		return assignee;
	}

	@Override
	public void setAssignee(Person assignee) {
		this.assignee = assignee;
	}

	@Override
	public Boolean getUMIRelease() {
		return UMIRelease;
	}

	@Override
	public void setUMIRelease(Boolean umiRelease) {
		this.UMIRelease = umiRelease;
	}

	@Override
	public Set<CustomActionValue> getCustomActions() {
		return customActions;
	}

	@Override
	public CustomActionValue addCustomAction(CustomActionDefinition definition,
			Boolean value) {
		CustomActionValue customAction = new JpaCustomActionValueImpl(this,
				definition, value);
		customActions.add(customAction);
		return customAction;
	}

}
