package org.tdl.vireo.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tdl.vireo.model.jpa.JpaAttachmentImpl;
import org.tdl.vireo.model.jpa.JpaCommitteeMemberImpl;
import org.tdl.vireo.model.jpa.JpaCustomActionValueImpl;
import org.tdl.vireo.model.jpa.JpaEmbargoTypeImpl;
import org.tdl.vireo.model.jpa.JpaGraduationMonthImpl;
import org.tdl.vireo.model.jpa.JpaPersonImpl;
import org.tdl.vireo.state.State;

/**
 * This is a simple mock submission class that may be useful for testing. Feel
 * free to extend this to add in extra parameters that you feel appropriate.
 * 
 * The basic concept is all properties are public so you can create the mock
 * object and then set whatever relevant properties are needed for your
 * particular test.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockSubmission extends AbstractMock implements Submission {

	/* Submission Properties */
	public Person submitter;
	public String studentFirstName;
	public String studentLastName;
	public String studentMiddleName;
	public Integer studentBirthYear;
	public String documentTitle;
	public String documentAbstract;
	public String documentKeywords;
	public MockEmbargoType embargoType;
	public List<Attachment> attachments = new ArrayList<Attachment>();
	public List<CommitteeMember> committeeMembers = new ArrayList<CommitteeMember>();
	public String committeeContactEmail;
	public String committeeEmailHash;
	public Date committeeApprovalDate;
	public Date committeeEmbargoApprovalDate;
	public String committeeDisposition;
	public Date submissionDate;
	public Date approvalDate;
	public Date licenseAgreementDate;
	public String degree;
	public DegreeLevel level;
	public String department;
	public String college;
	public String major;
	public String documentType;
	public Integer graduationYear;
	public Integer graduationMonth;
	public State state;
	public Person assignee;
	public Boolean UMIRelease;
	public List<CustomActionValue> customActions = new ArrayList<CustomActionValue>();
	public String depositId;
	public String lastLogEntry;
	public Date lastLogDate;

	@Override
	public MockSubmission save() {
		return this;
	}

	@Override
	public MockSubmission delete() {
		return this;
	}

	@Override
	public MockSubmission refresh() {
		return this;
	}

	@Override
	public MockSubmission merge() {
		return this;
	}
	
	@Override
	public MockSubmission detach() {
		return this;
	}

	@Override
	public Person getSubmitter() {
		return submitter;
	}
	
	@Override
	public String getStudentFirstName(){
		return studentFirstName;
	}

	@Override
	public void setStudentFirstName(String firstName) {
		this.studentFirstName = firstName;
	}

	@Override
	public String getStudentLastName() {
		return studentLastName;
	}

	@Override
	public void setStudentLastName(String lastName) {
		this.studentLastName = lastName;
	}

	@Override
	public String getStudentMiddleName() {
		return studentMiddleName;
	}

	public void setStudentMiddleName(String middleName) {
		this.studentMiddleName = middleName;
	}

	@Override
	public Integer getStudentBirthYear() {
		return studentBirthYear;
	}

	@Override
	public void setStudentBirthYear(Integer year) {
		this.studentBirthYear = year;
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
		this.embargoType = (MockEmbargoType) embargo;
	}

	@Override
	public Attachment getPrimaryDocument() {
		for (Attachment attachment : attachments) {
			if (attachment.getType() == AttachmentType.PRIMARY)
				return attachment;
		}
		return null;
	}

	@Override
	public List<Attachment> getSupplementalDocuments() {
		List<Attachment> supplemental = new ArrayList<Attachment>();

		for (Attachment attachment : attachments) {
			if (attachment.getType() == AttachmentType.PRIMARY)
				supplemental.add(attachment);
		}
		return supplemental;
	}

	@Override
	public List<Attachment> getAttachments() {
		return attachments;
	}

	@Override
	public Attachment addAttachment(File file, AttachmentType type)
			throws IOException {
		MockAttachment attachment = new MockAttachment();
		attachment.file = file;
		attachment.type = type;
		attachments.add(attachment);
		return attachment;
	}
	
	@Override
	public Attachment findAttachmentById(Long id) {
		for(Attachment attachment : attachments) {
			if(id == attachment.getId())
				return attachment;
		}
		return null;
	}

	@Override
	public List<CommitteeMember> getCommitteeMembers() {
		return committeeMembers;
	}

	@Override
	public CommitteeMember addCommitteeMember(String firstName,
			String lastName, String middleName, Boolean chair) {
		MockCommitteeMember member = new MockCommitteeMember();
		member.firstName = firstName;
		member.lastName = lastName;
		member.middleName = middleName;
		member.chair = chair;
		committeeMembers.add(member);
		return member;
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
	public DegreeLevel getDegreeLevel() {
		return level;
	}

	@Override
	public void setDegreeLevel(DegreeLevel level) {
		this.level = level;
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
		this.graduationMonth = month;
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public void setState(State state) {
		this.state = state;
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
	public List<CustomActionValue> getCustomActions() {
		return customActions;
	}

	@Override
	public CustomActionValue getCustomAction(CustomActionDefinition definition) {
		for (CustomActionValue value : customActions) {
			if (value.getDefinition() == definition)
				return value;
		}
		return null;
	}

	@Override
	public CustomActionValue addCustomAction(CustomActionDefinition definition,
			Boolean value) {
		MockCustomActionValue action = new MockCustomActionValue();
		action.submission = this;
		action.definition = definition;
		action.value = value;
		customActions.add(action);
		return action;
	}
	
	@Override
	public String getDepositId() {
		return depositId;
	}

	@Override
	public void setDepositId(String depositId) {
		this.depositId = depositId;
	}

	@Override
	public ActionLog logAction(String entry) {
		MockActionLog log = new MockActionLog();
		log.entry = entry;
		log.submission = this;
		log.actionDate = new Date();
		log.privateFlag = false;
		
		// store this as the last log entry as well.
		this.lastLogEntry = entry;
		this.lastLogDate = log.actionDate;
		
		return log;
	}

	@Override
	public String getLastLogEntry() {
		return lastLogEntry;
	}

	@Override
	public Date getLastLogDate() {
		return lastLogDate;
	}

}
