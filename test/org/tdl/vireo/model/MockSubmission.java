package org.tdl.vireo.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.tdl.vireo.proquest.ProquestLanguage;
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
	public String orcid;
	public Integer studentBirthYear;
	public String documentTitle;
	public String documentAbstract;
	public String documentKeywords;
	public List<String> documentSubjects = new ArrayList<String>();
	public String documentLanguage;
	public String publishedMaterial;
	public MockEmbargoType embargoType;
	public List<Attachment> attachments = new ArrayList<Attachment>();
	public List<CommitteeMember> committeeMembers = new ArrayList<CommitteeMember>();
	public String committeeContactEmail;
	public String committeeEmailHash;
	public Date committeeApprovalDate;
	public Date committeeEmbargoApprovalDate;
	public Date submissionDate;
	public Date approvalDate;
	public Date licenseAgreementDate;
	public Date defenseDate;
	public String degree;
	public DegreeLevel level;
	public String department;
	public String college;
	public String program;
	public String major;
	public String documentType;
	public Integer graduationYear;
	public Integer graduationMonth;
	public State state;
	public Person assignee;
	public Boolean UMIRelease;
	public List<CustomActionValue> customActions = new ArrayList<CustomActionValue>();
	public String depositId;
	public Date depositDate;
	public String reviewerNotes;
	public String lastLogEntry;
	public Date lastLogDate;
	public List<MockActionLog> logs = new ArrayList<MockActionLog>();

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
	public String getOrcid() {
		return orcid;
	}

	public void setOrcid(String orcidString) {
		this.orcid = orcidString;
	}

	@Override
	public String getStudentFormattedName(NameFormat format) {
		return NameFormat.format(format, studentFirstName, studentMiddleName, studentLastName, studentBirthYear);
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
	public List<String> getDocumentSubjects() {
		return documentSubjects;
	}
	
	@Override
	public void addDocumentSubject(String subject) {
		documentSubjects.add(subject);
	}
	
	@Override
	public void removeDocumentSubject(String subject) {
		documentSubjects.remove(subject);
	}
	
	@Override
	public String getDocumentLanguage() {
		return documentLanguage;
	}

	@Override
	public void setDocumentLanguage(String language) {
		this.documentLanguage = language;
	}
	
	@Override
	public Locale getDocumentLanguageLocale() {
		return MockLanguage.toLocale(this.documentLanguage);
	}
	
	@Override
	public String getPublishedMaterial() {
		return publishedMaterial;
	}
	
	@Override
	public void setPublishedMaterial(String material) {
		this.publishedMaterial = material;
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
		return getAttachmentsByType(AttachmentType.SUPPLEMENTAL);
	}
	
	@Override
	public List<Attachment> getAttachmentsByType(AttachmentType...types) {
		List<Attachment> filteredAttachments = new ArrayList<Attachment>();

		for (AttachmentType type : types) {
			for (Attachment attachment : attachments) {
				if (type == attachment.getType())
					filteredAttachments.add(attachment);
			}
		}
		return filteredAttachments;
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
	public Attachment addAttachment(byte[] content, String filename, AttachmentType type) {
		MockAttachment attachment = new MockAttachment();
		attachment.name = filename;
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
	public Attachment findAttachmentByName(String name) {
		if (name == null)
			return null;
		
		for(Attachment attachment : attachments) {
			if(name.equals(attachment.getName()))
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
			String lastName, String middleName) {
		MockCommitteeMember member = new MockCommitteeMember();
		member.firstName = firstName;
		member.lastName = lastName;
		member.middleName = middleName;
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
	public Date getDefenseDate() {
		return defenseDate;
	}
	
	@Override
	public void setDefenseDate(Date date) {
		this.defenseDate = date;
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
	public String getProgram() {
		return program;
	}
	
	@Override
	public void setProgram(String program) {
		this.program = program;
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
	public Date getDepositDate() {
		return depositDate;
	}

	@Override
	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;		
	}

	@Override
	public String getReviewerNotes() {
		return reviewerNotes;
	}

	@Override
	public void setReviewerNotes(String notes) {
		this.reviewerNotes = notes;
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
		logs.add(log);
		
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
