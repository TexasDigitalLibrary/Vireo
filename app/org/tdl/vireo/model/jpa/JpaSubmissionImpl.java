package org.tdl.vireo.model.jpa;

import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.LocaleUtils;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.proquest.ProquestLanguage;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.services.Utilities;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;
import org.tdl.vireo.services.Utilities;

import play.modules.spring.Spring;

/**
 * JPA specific implementation of Vireo's Submission interface.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@Entity
@Table(name = "submission")
public class JpaSubmissionImpl extends JpaAbstractModel<JpaSubmissionImpl> implements Submission {

	@ManyToOne(optional = false, targetEntity = JpaPersonImpl.class)
	public Person submitter;

	@Column(length=255)
	public String studentFirstName;
	@Column(length=255)
	public String studentLastName;
	@Column(length=255)
	public String studentMiddleName;
	public Integer studentBirthYear;
	
	// The new ORCID field
	@Column(length=255)
	public String orcid;
		
	@Column(length=326768) // 2^15
	public String documentTitle;
	@Column(length=326768) // 2^15
	public String documentAbstract;
	@Column(length=326768) // 2^15
	public String documentKeywords;
	@ElementCollection
	@OrderColumn
	@CollectionTable(name="submission_subjects")
	public List<String> documentSubjects;
	
	@Column(length=255)
	public String documentLanguage;
	
	@Column(length=326768) // 2^15
	public String publishedMaterial;

	@OneToOne(targetEntity = JpaEmbargoTypeImpl.class)
	public EmbargoType embargoType;

	@OneToMany(targetEntity = JpaAttachmentImpl.class, mappedBy = "submission", cascade = CascadeType.ALL)
	public List<Attachment> attachments;

	@OneToMany(targetEntity = JpaCommitteeMemberImpl.class, mappedBy = "submission", cascade = CascadeType.ALL)
	@OrderBy("displayOrder")
	public List<CommitteeMember> committeeMembers;
	@Column(length=255)
	public String committeeContactEmail;
	
	@Column(unique = true, length=255)
	public String committeeEmailHash;

	@Temporal(TemporalType.TIMESTAMP)
	public Date committeeApprovalDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date committeeEmbargoApprovalDate;

	@Temporal(TemporalType.TIMESTAMP)
	public Date submissionDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date approvalDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date licenseAgreementDate;
	@Temporal(TemporalType.TIMESTAMP)
	public Date defenseDate;
	
	@Column(length=255)
	public String degree;
	public DegreeLevel degreeLevel;
	@Column(length=255)
	public String department;
	@Column(length=255)
	public String college;
	@Column(length=255)
	public String program;
	@Column(length=255)
	public String major;
	@Column(length=255)
	public String documentType;

	public Integer graduationYear;
	public Integer graduationMonth;

	public String stateName;

	@OneToOne(targetEntity = JpaPersonImpl.class)
	public Person assignee;
	public Boolean UMIRelease;

	@OneToMany(targetEntity = JpaCustomActionValueImpl.class, mappedBy = "submission", cascade = CascadeType.ALL)
	public List<CustomActionValue> customActions;
	
	@Column(length=1024)
	public String depositId;
	@Temporal(TemporalType.TIMESTAMP)
	public Date depositDate;	
	
	@Column(length=326768) // 2^15
	public String reviewerNotes;
	
	@Column(length=326768) // 2^15
	public String lastActionLogEntry;
	@Temporal(TemporalType.TIMESTAMP)
	public Date lastActionLogDate;
	
	// This is not publicly available, only present for queries.
	@OneToMany(targetEntity = JpaActionLogImpl.class, mappedBy = "submission")
	public List<ActionLog> actionLogs;
	
	// List of log items pending a save.
	@Transient
	protected List<ActionLog> pendingLogs = new ArrayList<ActionLog>();
	
	// Flag whether the subjects list has changed.
	@Transient
	protected boolean documentSubjectsChanged = false;
	
	/**
	 * Insure that the pendingLogs array is initialized when loading the object
	 * from the database.
	 */
	@PostLoad
	private void onPostLoad() {
	     pendingLogs = new ArrayList<ActionLog>();
	}
	
	/**
	 * Construct a new JpaSubmissionImpl
	 * 
	 * @param submitter
	 *            The student submitting this submission.
	 */
	protected JpaSubmissionImpl(Person submitter) {

		if (submitter == null)
			throw new IllegalArgumentException("Submissions require a submitter");

		assertReviewerOrOwner(submitter);
		
		this.submitter = submitter;
		this.documentSubjects = new ArrayList<String>();
		this.attachments = new ArrayList<Attachment>();
		this.committeeMembers = new ArrayList<CommitteeMember>();
		this.customActions = new ArrayList<CustomActionValue>();
		this.actionLogs = new ArrayList<ActionLog>();
		this.stateName = (Spring.getBeanOfType(StateManager.class).getInitialState()).getBeanName();
		
		generateLog("Submission created", true);
	}

	@Override
	public JpaSubmissionImpl save() {		
		assertReviewerOrOwner(submitter);
		
		if (documentSubjectsChanged) {
			// Generate a note that the subjects have been updated.
			String entry = "";
			if (documentSubjects.size() == 0)
				entry = "Document subjects cleared";
			else {
				for (String subject : documentSubjects) {
					if (entry.length() != 0)
						entry += "; ";
					entry += "'"+subject+"'";
				}
				entry = "Document subjects changed to "+entry;
			}
			
			generateLog(entry,false);
			documentSubjectsChanged = false;
		}
		
		if (pendingLogs.size() > 0) {
			lastActionLogEntry = pendingLogs.get(pendingLogs.size()-1).getEntry();
			lastActionLogDate = pendingLogs.get(pendingLogs.size()-1).getActionDate();
		}
				
		// Scrub all user-exposed String fields of Unicode control stuff   
		this.documentTitle = Utilities.scrubControl(this.documentTitle, "");
		this.documentAbstract = Utilities.scrubControl(this.documentAbstract, " ");
		this.documentKeywords = Utilities.scrubControl(this.documentKeywords, " ");
		this.publishedMaterial = Utilities.scrubControl(this.publishedMaterial, " ");
		
		// Scrub all user-exposed String fields of Unicode control stuff   
		this.documentTitle = Utilities.scrubControl(this.documentTitle, "");
		this.documentAbstract = Utilities.scrubControl(this.documentAbstract, " ");
		this.documentKeywords = Utilities.scrubControl(this.documentKeywords, " ");
		this.publishedMaterial = Utilities.scrubControl(this.publishedMaterial, " ");
		
		super.save();
		
		// After saving save all pending actionlogs
		for(ActionLog log : pendingLogs) {			
			log.save();
		}
		pendingLogs.clear();
		
		return this;
	}
	
	@Override
	public JpaSubmissionImpl delete() {
		
		assertReviewerOrOwner(submitter);
		
		// Don't rely on the cascade for deleting attachments because the files
		// need to be deleted on disk.
		List<Attachment> attachmentsCopy = new ArrayList<Attachment>(attachments);
		for (Attachment attachment : attachmentsCopy) {
			attachment.delete();
		}
		
		// Explicitly delete the committee members because their roles will not
		// delete on cascade.
		List<CommitteeMember> membersCopy = new ArrayList<CommitteeMember>(committeeMembers);
		for (CommitteeMember member : membersCopy) {
			member.delete();
		}
		
		// Delete all action logs associated with this submission
		em().createQuery(
			"DELETE FROM JpaActionLogImpl " +
					"WHERE Submission_Id = ? " 
			).setParameter(1, this.getId())
			.executeUpdate();

		return super.delete();
	}
	
	
	@Override
	public JpaSubmissionImpl detach() {
		submitter.detach();
		if (assignee != null)
			assignee.detach();
		if (embargoType != null)
			embargoType.detach();
		return super.detach();
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
		
		assertReviewerOrOwner(submitter);

		if (firstName != null && firstName.trim().length() == 0)
			firstName = null;
		
		if (!equals(this.studentFirstName,firstName)) {
			this.studentFirstName = firstName;
			generateChangeLog("Student first name", firstName, false);
			
			updatePrimaryDocumentName();
		}
	}

	@Override
	public String getStudentLastName() {
		return studentLastName;
	}

	@Override
	public void setStudentLastName(String lastName) {
		
		assertReviewerOrOwner(submitter);

		if (lastName != null && lastName.trim().length() == 0)
			lastName = null;
		
		if (!equals(this.studentLastName,lastName)) {
			this.studentLastName = lastName;
			generateChangeLog("Student last name", lastName, false);
			
			updatePrimaryDocumentName();
		}
	}

	@Override
	public String getStudentMiddleName() {
		return studentMiddleName;
	}

	public void setStudentMiddleName(String middleName) {
		
		assertReviewerOrOwner(submitter);

		if (middleName != null && middleName.trim().length() == 0)
			middleName = null;
		
		if (!equals(this.studentMiddleName,middleName)) {
			this.studentMiddleName = middleName;
			generateChangeLog("Student middle name", middleName, false);
		}
	}

	@Override
	public Integer getStudentBirthYear() {
		return studentBirthYear;
	}

	@Override
	public void setStudentBirthYear(Integer year) {
		
		assertReviewerOrOwner(submitter);

		if (!equals(this.studentBirthYear,year)) {
			this.studentBirthYear = year;
	
			if (year == null)
				generateChangeLog("Student birth year", null,false);
			else
				generateChangeLog("Student birth year", String.valueOf(year),false);
		}
	}
	
	@Override
	public String getOrcid() {
		return orcid;
	}

	@Override
	public void setOrcid(String orcidString) {
		
		assertReviewerOrOwner(submitter);

		if (orcidString != null && orcidString.trim().length() == 0)
			orcidString = null;
		
		if (!equals(this.orcid,orcidString)) {
			this.orcid = orcidString;
			generateChangeLog("Student's ORCID identifier", orcidString, false);
		}
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
		
		assertReviewerOrOwner(submitter);
		if (!equals(this.documentTitle,title)) {
			this.documentTitle = title;
			generateChangeLog("Document title", title, false);
		}
	}

	@Override
	public String getDocumentAbstract() {
		return documentAbstract;
	}

	@Override
	public void setDocumentAbstract(String docAbstract) {
		
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.documentAbstract,docAbstract)) {
			this.documentAbstract = docAbstract;
			generateChangeLog("Document abstract", docAbstract, false);
		}
	}

	@Override
	public String getDocumentKeywords() {
		return documentKeywords;
	}

	@Override
	public void setDocumentKeywords(String keywords) {
		
		assertReviewerOrOwner(submitter);

		if (!equals(this.documentKeywords,keywords)) {
			this.documentKeywords = keywords;
			generateChangeLog("Document keywords", keywords, false);
		}
	}
	
	@Override
	public List<String> getDocumentSubjects() {
		return documentSubjects;
	}
	
	@Override
	public void addDocumentSubject(String subject) {
		documentSubjects.add(subject);
		documentSubjectsChanged = true;
	}
	
	@Override
	public void removeDocumentSubject(String subject) {
		documentSubjects.remove(subject);
		documentSubjectsChanged = true;
	}
	
	@Override
	public void setDocumentLanguage(String language) {
				
		if (language != null) {
			if(language.isEmpty())
				language = null;
			else if(JpaLanguageImpl._toLocale(language) == null)
				throw new IllegalArgumentException("Language is an invalid locale");
		}
		
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.documentLanguage,language)) {
			this.documentLanguage = language;
			if(language != null)
				generateChangeLog("Document language",JpaLanguageImpl._toLocale(language).getDisplayName(),false);
			else
				generateChangeLog("Document language",null,false);
		}
	}
	
	@Override
	public String getDocumentLanguage() {
		return documentLanguage;
	}
	
	@Override
	public Locale getDocumentLanguageLocale() {
		return JpaLanguageImpl._toLocale(documentLanguage);
	}

	@Override
	public String getPublishedMaterial() {
		return publishedMaterial;
	}
	
	@Override
	public void setPublishedMaterial(String material) {
		
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.publishedMaterial,material)) {
			this.publishedMaterial = material;
			if(material != null)
				generateChangeLog("Published material",material,false);
			else
				generateChangeLog("Published material",null,false);
		}
	}
	
	
	@Override
	public EmbargoType getEmbargoType() {
		return embargoType;
	}

	@Override
	public void setEmbargoType(EmbargoType embargo) {
		
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.embargoType,embargo)) {
			this.embargoType = embargo;
			generateChangeLog("Embargo type",embargo.getName(), false);
		}
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
	public Attachment addAttachment(byte[] content, String filename, AttachmentType type)
			throws IOException {

		Attachment attachment = new JpaAttachmentImpl(this, type, filename, content);
		attachments.add(attachment);
		return attachment;
	}
	
	@Override
	public Attachment findAttachmentById(Long id){
		for(Attachment attachment : attachments) {
			if(id.compareTo(attachment.getId())==0)
				return attachment;
		}
		return null;
	}
	
	@Override
	public Attachment findAttachmentByName(String name){
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
		CommitteeMember member = new JpaCommitteeMemberImpl(this, firstName,
				lastName, middleName);
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
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.committeeContactEmail,email)) {
			this.committeeContactEmail = email;
			generateChangeLog("Committee contact email address", email, false);
		}
	}

	@Override
	public String getCommitteeEmailHash() {
		return committeeEmailHash;
	}

	@Override
	public void setCommitteeEmailHash(String hash) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.committeeEmailHash,hash)) {
			this.committeeEmailHash = hash;		
			generateLog("New committee email hash generated", false);
		}
	}

	@Override
	public Date getCommitteeApprovalDate() {
		return committeeApprovalDate;
	}

	@Override
	public void setCommitteeApprovalDate(Date date) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.committeeApprovalDate,date)) {
			this.committeeApprovalDate = date;
			
			if (date == null)
				generateLog("Committee approval of submission cleared",false);
			else
				generateLog("Committee approval of submission set",false);
		}
	}

	@Override
	public Date getCommitteeEmbargoApprovalDate() {
		return committeeEmbargoApprovalDate;
	}

	@Override
	public void setCommitteeEmbargoApprovalDate(Date date) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.committeeEmbargoApprovalDate,date)) {
			this.committeeEmbargoApprovalDate = date;
			
			if (date == null)
				generateLog("Committee approval of embargo cleared",false);
			else
				generateLog("Committee approval of embargo set",false);
		}
	}

	@Override
	public Date getSubmissionDate() {
		return submissionDate;
	}

	@Override
	public void setSubmissionDate(Date date) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.submissionDate,date)) {
			this.submissionDate = date;
			
			if (date == null) {
				generateLog("Submission date cleared",true);
			} else {
				DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
				String formattedDate = format.format(date);
				generateLog("Submission date set to "+formattedDate,true);
			}
		}
	}

	@Override
	public Date getApprovalDate() {
		return approvalDate;
	}

	@Override
	public void setApprovalDate(Date date) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.approvalDate,date)) {
			this.approvalDate = date;
			
			if (date == null)
				generateLog("Submission approval cleared",true);
			else
				generateLog("Submission approval set",true);
		}
	}

	@Override
	public Date getLicenseAgreementDate() {
		return licenseAgreementDate;
	}

	@Override
	public void setLicenseAgreementDate(Date date) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.licenseAgreementDate,date)) {
			this.licenseAgreementDate = date;
			
			if (date == null)
				generateLog("Submission license agreement cleared",true);
			else
				generateLog("Submission license agreement set",true);
		}
	}
	
	@Override
	public Date getDefenseDate() {
		return defenseDate;
	}
	
	@Override
	public void setDefenseDate(Date date) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.defenseDate,date)) {
			this.defenseDate = date;
			
			if (date == null) {
				generateLog("Defense date cleared",true);
			} else {
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				generateChangeLog("Defense date",formatter.format(date),true);
			}
		}
	}

	@Override
	public String getDegree() {
		return degree;
	}

	@Override
	public void setDegree(String degree) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.degree,degree)) {
			this.degree = degree;
			generateChangeLog("Degree",degree,false);
		}
	}
	
	@Override
	public DegreeLevel getDegreeLevel() {
		return degreeLevel;
	}

	@Override
	public void setDegreeLevel(DegreeLevel level) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.degreeLevel,level)) {
			this.degreeLevel = level;
			generateChangeLog(
					"Degree level",         
					(degreeLevel == null) ? null : degreeLevel.name(),
					false);
		}
	}

	@Override
	public String getDepartment() {
		return department;
	}

	@Override
	public void setDepartment(String department) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.department,department)) {
			this.department = department;
			generateChangeLog("Department",department,false);
		}
	}

	@Override
	public String getCollege() {
		return college;
	}
	
	@Override
	public void setCollege(String college) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.college,college)) {
			this.college = college;
			generateChangeLog("College",college,false);
		}
	}
	
	@Override
	public String getProgram() {
		return program;
	}
	
	@Override
	public void setProgram(String program) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.program,program)) {
			this.program = program;
			generateChangeLog("Program",program,false);
		}
	}

	@Override
	public String getMajor() {
		return major;
	}

	@Override
	public void setMajor(String major) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.major,major)) {
			this.major = major;
			generateChangeLog("Major",major,false);
		}
	}

	@Override
	public String getDocumentType() {
		return documentType;
		
	}

	@Override
	public void setDocumentType(String documentType) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.documentType,documentType)) {
			this.documentType = documentType;
			generateChangeLog("Document type",documentType,false);
			
			updatePrimaryDocumentName();
		}
	}

	@Override
	public Integer getGraduationYear() {
		return graduationYear;
	}

	@Override
	public void setGraduationYear(Integer year) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.graduationYear,year)) {
			this.graduationYear = year;
			
			if (year == null)
				generateChangeLog("Graduation year", null,false);
			else
				generateChangeLog("Graduation year", String.valueOf(year),false);
			
			updatePrimaryDocumentName();
		}
	}

	@Override
	public Integer getGraduationMonth() {
		return graduationMonth;
	}

	@Override
	public void setGraduationMonth(Integer month) {
		
		if (month != null && (month < 0 || month > 11))
			throw new IllegalArgumentException("Month is out of bounds.");
		
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.graduationMonth,month)) {
			this.graduationMonth = month;
			
			if (month == null)
				generateChangeLog("Graduation month", null,false);
			else
				generateChangeLog("Graduation month", new DateFormatSymbols().getMonths()[month],false);
		}
	}

	@Override
	public State getState() {
		return Spring.getBeanOfType(StateManager.class).getState(stateName);
	}

	@Override
	public void setState(State state) {
		
		if (state == null)
			throw new IllegalArgumentException("State is required");
		
		assertReviewer();
		
		if (!equals(this.stateName,state.getBeanName())) {
			this.stateName = state.getBeanName();
			generateChangeLog("Submission status",state.getDisplayName(),true);
			
			// Check if this state is approved
			if (this.approvalDate == null && state.isApproved())
				this.setApprovalDate(new Date());
		}
	}

	@Override
	public Person getAssignee() {
		return assignee;
	}

	@Override
	public void setAssignee(Person assignee) {
		
		assertReviewer();
		
		this.assignee = assignee;
		
		if (assignee == null)
			generateChangeLog("Assignee", null,true);
		else
			generateChangeLog("Assignee",assignee.getFormattedName(NameFormat.FIRST_LAST),true);
	}

	@Override
	public Boolean getUMIRelease() {
		return UMIRelease;
	}

	@Override
	public void setUMIRelease(Boolean umiRelease) {
		
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.UMIRelease,umiRelease)) {
			this.UMIRelease = umiRelease;
			
			if (umiRelease == null)
				generateLog("UMI Release cleared",false);
			else if (umiRelease == true)
				generateChangeLog("UMI Release","Yes",false);
			else
				generateChangeLog("UMI Release","No",false);
		}
	}

	@Override
	public List<CustomActionValue> getCustomActions() {
		return customActions;
	}
	
	@Override
	public CustomActionValue getCustomAction(CustomActionDefinition definition) {
		
		Iterator<CustomActionValue> valueItr = customActions.iterator();
		while (valueItr.hasNext()) {
			CustomActionValue value = valueItr.next();
			if (value.getDefinition().equals(definition))
				return value;
		}
		
		return null;
	}
	
	/**
	 * Internal call back to notify the parent submission when a custom action
	 * value has been deleted.
	 * 
	 * @param value
	 *            The value being deleted.
	 */
	protected void removeCustomAction(CustomActionValue value) {
		customActions.remove(value);
	}

	@Override
	public CustomActionValue addCustomAction(CustomActionDefinition definition,
			Boolean value) {
		CustomActionValue customAction = new JpaCustomActionValueImpl(this,
				definition, value);
		customActions.add(customAction);
		return customAction;
	}
	
	@Override
	public String getDepositId() {
		return depositId;
	}
	
	@Override
	public void setDepositId(String depositId) {
		assertReviewerOrOwner(submitter);
		
		if (!equals(this.depositId,depositId)) {
			this.depositId = depositId;
			generateChangeLog("Repository deposit ID",depositId,false);
		}
	}
	
	@Override
	public Date getDepositDate() {
		return depositDate;
	}
	
	@Override
	public void setDepositDate(Date depositDate) {
		assertReviewerOrOwner(submitter);
		
		this.depositDate = depositDate;
	}
	
	
	@Override
	public String getReviewerNotes() {
		return this.reviewerNotes;
	}
	
	@Override
	public void setReviewerNotes(String notes) {
		
		assertReviewer();
		
		if (!equals(this.reviewerNotes,notes)) {
			
			this.reviewerNotes = notes;
			
			// Generate a private note
			String entry;
			if (notes == null)
				entry = "Reviewer notes cleared";
			else
				entry = String.format(
					"Reviewer notes changed to '%s'",
					notes);
			
			ActionLog log = logAction(entry);
			log.setPrivate(true);
		}
		
	}
	
	@Override
	public String getLastLogEntry() {
		return lastActionLogEntry;
	}

	@Override
	public Date getLastLogDate() {
		return lastActionLogDate;
	}
	
	
	@Override
	public ActionLog logAction(String entry) {
		return logAction(entry, null);
	}
	
	/**
	 * Create an action log entry about this submission. This is method is not
	 * included in the public interface at the present time. Maybe in the future
	 * we will move it up there. This method operates exactly the same as the
	 * "logAction" method, which is defined in the public interface, but accepts
	 * an attachment object. The action log object generated will be associated
	 * with the attachment object present.
	 * 
	 * @param entry
	 *            The entry text to be saved, note " by User" will be appended
	 *            to the end of this entry text recording who made the action.
	 * @param attachment
	 *            The attachment this action log item is associated with (may be
	 *            null).
	 * @return The unsaved action log object.
	 */
	public ActionLog logAction(String entry, Attachment attachment) {
		
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
		
		Person actor = context.getPerson();
		// The "By whom ever" is duplicating data and just uneeded.
		//if (actor != null)
		//	entry += " by " + actor.getFormattedName(NameFormat.FIRST_LAST);
		
		ActionLog log = new JpaActionLogImpl(this, this.getState(), actor, new Date(), attachment, entry, false);
		pendingLogs.add(log);
		return log;
	}

	/**
	 * Internal method for generating log events.
	 * 
	 * This will create an ActionLog entry and append it to a list of pending
	 * changes. Then when this object is saved() those logs will be persisted in
	 * the database.
	 * 
	 * @param entry
	 *            The log entry (note, "by User" will be appended to the end of
	 *            the log entry)
	 * @param always
	 * 			  This log message should be generated no matter what state the submission is currently in.
	 */
	protected void generateLog(String entry, boolean always) {
		if (!always) {
			// Ignore if the submission is in the initial state.
			StateManager manager = Spring.getBeanOfType(StateManager.class);
			if (manager.getInitialState() == this.getState())
				return;
		}
		
		pendingLogs.add(logAction(entry));
	}
	
	/**
	 * Internal method for generating log events.
	 * 
	 * This will create a preformatted action log entry for a field change. The
	 * log will then be appended to the list of pending changes which will be
	 * persisted when the submission object is saved.
	 * 
	 * @param fieldName
	 *            The name of the field being updated.
	 * @param newValue
	 *            The new value of the field. (may be null)
	 * @param always
	 * 			  This log message should be generated no matter what state the submission is currently in.
	 */
	protected void generateChangeLog(String fieldName, String newValue, boolean always) {
		String entry;
		if (newValue == null)
			entry = fieldName + " cleared";
		else
			entry = String.format(
				"%s changed to '%s'",
				fieldName,
				newValue);
		
		generateLog(entry,always);
	}
	
	/**
	 * This is called when information that could potentially effect the name of
	 * the primary document has been modified. This method is called to see if
	 * the attachment should be updated.
	 */
	protected void updatePrimaryDocumentName() {
	
		Attachment primary = this.getPrimaryDocument();
		if (primary != null) {
			((JpaAttachmentImpl) primary).renamePrimaryDocument();
			primary.save();
		}
	}
	
	
	/**
	 * Return true if the two objects are equals, accounting for nulls.
	 * 
	 * @param a An object
	 * @param b An object
	 * @return True if a is the same as b, otherwise false.
	 */
	private boolean equals(Object a, Object b) {
		
		if (a == null) {
			if (b == null) 
				return true;
			else
				return false;
		} else {
			return a.equals(b);
		}	
	}
	

}
