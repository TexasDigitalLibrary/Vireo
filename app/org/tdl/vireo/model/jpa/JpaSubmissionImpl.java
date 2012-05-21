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
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.db.jpa.Model;
import play.modules.spring.Spring;

/**
 * JPA specific implementation of Vireo's Submission interface.
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
	@OrderBy("displayOrder")
	public List<CommitteeMember> committeeMembers;
	public String committeeContactEmail;
	public String committeeEmailHash;

	@Temporal(TemporalType.TIMESTAMP)
	public Date committeeApprovalDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date committeeEmbargoApprovalDate;
	public String committeeDisposition;

	@Temporal(TemporalType.TIMESTAMP)
	public Date submissionDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date approvalDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date licenseAgreementDate;

	public String degree;
	public String department;
	public String college;
	public String major;
	public String documentType;

	public Integer graduationYear;
	public Integer graduationMonth;

	public String state;

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

		if (submitter == null)
			throw new IllegalArgumentException("Submissions require a submitter");

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
	public String getDocumentKeywords() {
		return documentKeywords;
	}

	@Override
	public void setDocumentKeywords(String keywords) {
		this.documentKeywords = keywords;
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
		for (Attachment attachment : attachments) {
			if (AttachmentType.PRIMARY == attachment.getType())
				return attachment;
		}
		
		return null;
	}

	@Override
	public Set<Attachment> getSupplementalDocuments() {
		
		
		Set<Attachment> supplemental = new HashSet<Attachment>();
		for (Attachment attachment : attachments) {
			if (AttachmentType.SUPPLEMENTAL == attachment.getType())
				supplemental.add(attachment);
		}
		
		return supplemental;
	}

	@Override
	public Set<Attachment> getAttachments() {
		return attachments;
	}

	/**
	 * Internal call back method when an attachment has been deleted.
	 * 
	 * @param attachment
	 *            The attachment to remove.
	 */
	protected void removeAttachment(Attachment attachment) {
		attachments.remove(attachment);
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

	/**
	 * Internal call back for when a committee member has been deleted, so that
	 * it will be removed from the list.
	 * 
	 * @param member
	 *            The member to remove.
	 */
	protected void removeCommitteeMember(CommitteeMember member) {
		this.committeeMembers.remove(member);
	}

	@Override
	public String getCommitteeContactEmail() {
		return committeeContactEmail;
	}

	@Override
	public void setCommitteeContactEmail(String email) {		
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
		return committeeApprovalDate;
	}

	@Override
	public void setCommitteeApprovalDate(Date date) {
		this.committeeApprovalDate = date;
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
	public String getDegree() {
		return degree;
	}

	@Override
	public void setDegree(String degree) {
		this.degree = degree;
	}

	@Override
	public String getDepartment() {
		return department;
	}

	@Override
	public void setDepartment(String department) {
		this.department = department;
	}

	@Override
	public String getCollege() {
		return college;
	}

	@Override
	public void setCollege(String college) {
		this.college = college;
	}

	@Override
	public String getMajor() {
		return major;
	}

	@Override
	public void setMajor(String major) {
		this.major = major;
	}

	@Override
	public String getDocumentType() {
		return documentType;
	}

	@Override
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	@Override
	public Integer getGraduationYear() {
		return graduationYear;
	}

	@Override
	public void setGraduationYear(Integer year) {
		this.graduationYear = year;
	}

	@Override
	public Integer getGraduationMonth() {
		return graduationMonth;
	}

	@Override
	public void setGraduationMonth(Integer month) {
		
		if (month != null && (month < 0 || month > 11))
			throw new IllegalArgumentException("Month is out of bounds.");
		
		this.graduationMonth = month;
	}

	@Override
	public State getState() {
		return Spring.getBeanOfType(StateManager.class).getState(state);
	}

	@Override
	public void setState(State state) {
		
		if (state == null)
			this.state = null;
		else 
			this.state = state.getBeanName();
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
