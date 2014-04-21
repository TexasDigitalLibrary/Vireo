

package controllers.submit;

import static org.tdl.vireo.constant.AppConfig.*;
import static org.tdl.vireo.constant.FieldConfig.*;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.LocaleUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.constant.FieldConfig;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CommitteeMemberRoleType;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.proquest.ProquestSubject;

import play.Logger;

import au.com.bytecode.opencsv.CSVReader;

import controllers.Security;

/**
* This is the third step of the submission process. This is where students
* provide metedata about their document, committee members, and select among
* publication options.
* 
* @author <a href="http://www.scottphillips.com">Scott Phillips</a>
* @author <a href="bill-ingram.com">Bill Ingram</a>
* @author Dan Galewsky 
*/
public class DocumentInfo extends AbstractSubmitStep {


	/**
	 * The third step of the submission form.
	 * 
	 * We handle committee members a bit differently. Basically we always keep a
	 * List of Maps for each committee member while we are working with it. So
	 * there are several methods to parse the committee members from the current
	 * form data, validate the committee members, then save the committee
	 * members, and if this is the first time visiting the form there is a
	 * method to load committee members. It's complex, but the problem is
	 * Difficult.
	 * 
	 * @param subId The id the submission.
	 */
	@Security(RoleType.STUDENT)
	public static void documentInfo(Long subId) {

		// Validate the submission
		Submission sub = getSubmission();

		// Get our form paramaters.
		String title = params.get("title");
		String degreeMonth = params.get("degreeMonth");
		String degreeYear = params.get("degreeYear");
		
		Date defenseDate = null;
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		try {
			if(params.get("defenseDate")!=null && !"".equals(params.get("defenseDate").trim()))
				defenseDate = (Date)formatter.parse(params.get("defenseDate"));
		} catch (ParseException e) {
			validation.addError("defenseDate","Please format your defense date as mm/dd/yyyy");
		}
		
		String docType = params.get("docType");
		String abstractText = params.get("abstractText");
		String keywords = params.get("keywords");
		String subjectPrimary = params.get("subject-primary");
		String subjectSecondary = params.get("subject-secondary");
		String subjectTertiary = params.get("subject-tertiary");
		
		String docLanguage = null;
		if(isFieldRequired(DOCUMENT_LANGUAGE) && settingRepo.findAllLanguages().size()==1) {
			docLanguage = settingRepo.findAllLanguages().get(0).getName();
		} else {		
			docLanguage = params.get("docLanguage");
			if (docLanguage != null && docLanguage.trim().length() == 0)
				docLanguage = null;
		}
		
		Boolean publishedMaterialFlag = params.get("publishedMaterialFlag",Boolean.class);
		if (publishedMaterialFlag == null)
			publishedMaterialFlag = false;
		String publishedMaterial = params.get("publishedMaterial");
		if (!publishedMaterialFlag)
			publishedMaterial = null;
		String chairEmail = params.get("chairEmail");
		String embargo = params.get("embargo");

		List<TransientMember> committee = parseCommitteeMembers();

		if ("documentInfo".equals(params.get("step"))) {
			
			// Save the data
			if (isFieldEnabled(DOCUMENT_TITLE))
				sub.setDocumentTitle(title);
			
			
			if (isFieldEnabled(GRADUATION_DATE)) {				
				if (!isEmpty(degreeMonth)) {
					try {
						sub.setGraduationMonth(Integer.parseInt(degreeMonth));
					} catch (RuntimeException re) {
						validation.addError("degreeMonth", "Please select a valid degree month");
					}
				} else {
					sub.setGraduationMonth(null);
				}
				
				if (!isEmpty(degreeYear)) {
					try {
						sub.setGraduationYear(Integer.parseInt(degreeYear));
					} catch (RuntimeException re) {
						validation.addError("degreeYear", "Please select a valid degree year");
					}
				} else {
					sub.setGraduationYear(null);
				}
			}
			
			if (isFieldEnabled(DEFENSE_DATE)) {
				sub.setDefenseDate(defenseDate);
			}
			
			if (isFieldEnabled(DOCUMENT_TYPE))
				sub.setDocumentType(docType);
			
			if (isFieldEnabled(DOCUMENT_ABSTRACT))
				sub.setDocumentAbstract(abstractText);
			
			if (isFieldEnabled(DOCUMENT_KEYWORDS))
				sub.setDocumentKeywords(keywords);
			
			if (isFieldEnabled(DOCUMENT_SUBJECTS)) {
				
				sub.getDocumentSubjects().clear();
				
				if (!isEmpty(subjectPrimary))
					sub.addDocumentSubject(subjectPrimary);
				
				if (!isEmpty(subjectSecondary))
					sub.addDocumentSubject(subjectSecondary);
				
				if (!isEmpty(subjectTertiary))
					sub.addDocumentSubject(subjectTertiary);
			}
			
			if (isFieldEnabled(DOCUMENT_LANGUAGE))
				sub.setDocumentLanguage(docLanguage);
			
			
			if (isFieldEnabled(COMMITTEE_CONTACT_EMAIL))
				sub.setCommitteeContactEmail(chairEmail);
			
			if (isFieldEnabled(PUBLISHED_MATERIAL)) {
				if (publishedMaterialFlag)
					sub.setPublishedMaterial(publishedMaterial);
				else
					sub.setPublishedMaterial(null);
			}
			
			if (isFieldEnabled(EMBARGO_TYPE)) {
				try {
					sub.setEmbargoType(settingRepo.findEmbargoType(Long.parseLong(embargo)));
				} catch (RuntimeException re){
					if (isFieldRequired(EMBARGO_TYPE))
						validation.addError("embargo", "Please select a valid embargo option");
				}
			}			
	
			if (isFieldEnabled(COMMITTEE)) {
				try {
					saveCommitteeMembers(sub, committee);
				} catch (RuntimeException re){ /*ignore*/ }
			}
			
			sub.save();
		} else {
			
			if (isFieldEnabled(DOCUMENT_TITLE))
				title = sub.getDocumentTitle();

			if (isFieldEnabled(GRADUATION_DATE) && sub.getGraduationMonth() != null)
				degreeMonth =  sub.getGraduationMonth().toString();
			
			if (isFieldEnabled(GRADUATION_DATE) && sub.getGraduationYear() != null)
				degreeYear = sub.getGraduationYear().toString();

			if (isFieldEnabled(DEFENSE_DATE) && sub.getDefenseDate() != null)
				defenseDate = sub.getDefenseDate();
			
			if (isFieldEnabled(DOCUMENT_TYPE))
				docType = sub.getDocumentType();
			
			if (isFieldEnabled(DOCUMENT_ABSTRACT))
				abstractText = sub.getDocumentAbstract();
			
			if (isFieldEnabled(DOCUMENT_KEYWORDS))
				keywords = sub.getDocumentKeywords();
			
			if (isFieldEnabled(DOCUMENT_SUBJECTS)) {
				
				List<String> subjects = sub.getDocumentSubjects();
				
				if (subjects.size() > 0)
					subjectPrimary = subjects.get(0);
				
				if (subjects.size() > 1)
					subjectSecondary = subjects.get(1);
				
				if (subjects.size() > 2)
					subjectTertiary = subjects.get(2);
			}
			
			if (isFieldEnabled(DOCUMENT_LANGUAGE)) {	
				docLanguage = sub.getDocumentLanguage();
			}
			
			// Get the list of committee members
			if (isFieldEnabled(COMMITTEE))
				committee = loadCommitteeMembers(sub);
			
			if (isFieldEnabled(COMMITTEE_CONTACT_EMAIL))
				chairEmail = sub.getCommitteeContactEmail();

			if (isFieldEnabled(PUBLISHED_MATERIAL))
				publishedMaterial = sub.getPublishedMaterial();
			
			if (isFieldEnabled(EMBARGO_TYPE) && sub.getEmbargoType() != null)
				embargo = sub.getEmbargoType().getId().toString();
		}
		
		// Verify the form if we are submitting or if jumping from the confirm step.
		if ("documentInfo".equals(params.get("step")) ||
			"confirm".equals(flash.get("from-step"))) {
			verify(sub);
		}

		// If there are no errors then go to the next step
		if (params.get("submit_next") != null && !validation.hasErrors() ) {
			FileUpload.fileUpload(subId);
		}

		// List of valid degree years for drop-down population
		List<Integer> degreeYears = getDegreeYears();
		renderArgs.put("degreeYears", degreeYears);

		// List of all document types
		List<String> docTypes = getValidDocumentTypes(sub);
		renderArgs.put("docTypes", docTypes);

		// List of all *active* Embargo Types
		List<EmbargoType> embargoTypes = settingRepo.findAllActiveEmbargoTypes();
		renderArgs.put("embargoTypes", embargoTypes);
		
		// List of all subjects
		List<ProquestSubject> subjects = proquestRepo.findAllSubjects();
		renderArgs.put("subjects", subjects);

		// List available committe roles
		List<CommitteeMemberRoleType> availableRoles = settingRepo.findAllCommitteeMemberRoleTypes(sub.getDegreeLevel());
		renderArgs.put("availableRoles", availableRoles);
		
		// List of all languages
		List<Language> languages = settingRepo.findAllLanguages();
		renderArgs.put("docLanguages", languages);
		
		// Figure out how mayn committee spots to show.
		int committeeSlots = 4;
		if (committee.size() > 3)
			committeeSlots = committee.size();
		if (params.get("submit_add") != null)
			committeeSlots += 4;
		
		List<String> stickies = new ArrayList<String>();
		String stickiesRaw = settingRepo.getConfigValue(SUBMIT_DOCUMENT_INFO_STICKIES);
		if (stickiesRaw != null && !"null".equals(stickiesRaw)) {
			try {
				CSVReader reader = new CSVReader(new StringReader(stickiesRaw));
				stickies = Arrays.asList(reader.readNext());
				reader.close();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
		
		if (publishedMaterial != null)
			publishedMaterialFlag = true;
		
		
		renderTemplate("Submit/documentInfo.html", subId, stickies,

				title, degreeMonth, degreeYear, defenseDate, docType, abstractText, keywords, 
				subjectPrimary, subjectSecondary, subjectTertiary, docLanguage, committeeSlots, 
				committee, chairEmail, publishedMaterialFlag, publishedMaterial, embargo);
	}

	/**
	 * Verify that all the document information is correct. This will be
	 * accessed from two places, the document info page as well as the
	 * confirmation page.
	 * 
	 * @return True if there are no errors found, otherwise false.
	 */
	public static boolean verify(Submission sub) {

		int numberOfErrorsBefore = validation.errors().size();
		
		// Document Title
		if(isFieldRequired(DOCUMENT_TITLE) && isEmpty(sub.getDocumentTitle()))
			validation.addError("title", "Please enter a thesis title");

		// Graduation Date (month & year)
		if (!isValidDegreeMonth(sub.getGraduationMonth()))
			validation.addError("degreeMonth", "Please select a degree month");

		if (!isValidDegreeYear(sub.getGraduationYear()))
			validation.addError("degreeYear", "Please select a degree year");
	
		// Defense Date
		Date now = new Date();
		Date min = new Date(-2208967200000L);
		Date max = new Date(now.getTime() + (365 * 24 * 60 * 60 * 1000L));
		
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		
		if (isFieldRequired(DEFENSE_DATE) && (sub.getDefenseDate()==null))
			validation.addError("defenseDate", "Please enter a defense date");
		else if(sub.getDefenseDate()!=null && (!sub.getDefenseDate().after(min) || !sub.getDefenseDate().before(max)))
			validation.addError("defenseDate", "Please enter a defense date between "+formatter.format(min)+" and "+formatter.format(max));
		
		// Document Type
		if (!isValidDocType(sub.getDocumentType()))
			validation.addError("docType", "Please select a Document Type");
		
		// Document Abstract
		if (isFieldRequired(DOCUMENT_ABSTRACT) && isEmpty(sub.getDocumentAbstract()))
			validation.addError("abstractText", "Please enter an abstract");

		// Document Keywords
		if (isFieldRequired(DOCUMENT_KEYWORDS) && isEmpty(sub.getDocumentKeywords()))
			validation.addError("keywords", "Please enter at least one keyword");

		// Document Subjects
		if (isFieldEnabled(DOCUMENT_SUBJECTS)) {
			for (String subject : sub.getDocumentSubjects()) {
				if (proquestRepo.findSubjectByDescription(subject) == null) {
					validation.addError("subjects", "One of the selected subjects is invalid");
				}
			}
			
			if (isFieldRequired(DOCUMENT_SUBJECTS) && sub.getDocumentSubjects().size() == 0) {
				validation.addError("subjects", "Please provide atleast a primary subject.");
			}
		}
		
		// Language
		if (isFieldRequired(DOCUMENT_LANGUAGE) && isEmpty(sub.getDocumentLanguage()))
			validation.addError("language", "Please select a language.");
		
		// Committee members (make sure they aren't any double entries)
		if (isFieldRequired(COMMITTEE) && !validation.hasError("committee")) {
			List<TransientMember> committee = loadCommitteeMembers(sub);
			validateCommitteeMembers(sub,committee);
		}
		
		// Committee Contact Email
		if (isFieldRequired(COMMITTEE_CONTACT_EMAIL) && isEmpty(sub.getCommitteeContactEmail())) {
			validation.addError("chairEmail", "Please enter an email address for the committee chair");
		} else if (isFieldEnabled(COMMITTEE_CONTACT_EMAIL) && !isEmpty(sub.getCommitteeContactEmail())) {
			try {
				new InternetAddress(sub.getCommitteeContactEmail()).validate();
			} catch (AddressException e) {
				validation.addError("chairEmail","The committee chair email address '"+sub.getCommitteeContactEmail()+"' you provided is invalid.");
			}
		}
		
		// Previously Published
		if (isFieldEnabled(PUBLISHED_MATERIAL)) {
			if (sub.getPublishedMaterial() != null && sub.getPublishedMaterial().trim().length() == 0) {
				validation.addError("publishedMaterial", "If the any material being submitted has been previously published then you must identify the material which was published. (i.e. the section or chapter).");
			}
		}
		
		// Embargo
		if (isFieldRequired(EMBARGO_TYPE) && sub.getEmbargoType() == null)
			validation.addError("embargo", "Please choose an embargo option");

		// Check if we've added any new errors. If so return false;
		if (numberOfErrorsBefore == validation.errors().size()) 
			return true;
		else
			return false;
	}
	
	
	
	/**
	 * @param degreeMonth
	 *            The month of the degree
	 * @return True if the name is a valid degree month.
	 */
	protected static boolean isValidDegreeMonth(Integer degreeMonth) {		
		if (degreeMonth == null) {
			if (isFieldRequired(GRADUATION_DATE))
				return false;
			return true;
		}
		
		for (GraduationMonth month : settingRepo.findAllGraduationMonths()) {
			if (degreeMonth == month.getMonth()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param degreeYear
	 *            The year of the degree
	 * @return True if the name is a valid degree year.
	 */
	protected static boolean isValidDegreeYear(Integer degreeYear) {
		if (degreeYear == null) {
			if (isFieldRequired(GRADUATION_DATE))
				return false;
			return true;
		}
		
		for (Integer y : getDegreeYears()) {
			if (degreeYear.equals(y)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * @param docType The document type.
	 * @return True if the docType is a valid document type.
	 */
	protected static boolean isValidDocType(String docType) {
		
		if (isEmpty(docType)) {
			if (isFieldRequired(DOCUMENT_TYPE))
				return false;
			return true;
		}
			
		List<DocumentType> docTypes = settingRepo.findAllDocumentTypes();

		for(DocumentType type : docTypes) {
			if(docType.equals(type.getName())) {
				return true;
			}
		}

		return false;
	}
	
	
	/**
	 * Determine if the provided roles are valid for this given degree level of the submission.
	 * 
	 * @param sub The submission
	 * @param roles The roles to validate.
	 * @return True if valid, otherwise false.
	 */
	protected static boolean isValidRoleType(Submission sub, List<String> roles) {
		
		List<CommitteeMemberRoleType> types = settingRepo.findAllCommitteeMemberRoleTypes(sub.getDegreeLevel());
		
		for (String role : roles) {
			
			boolean found = false;
			for (CommitteeMemberRoleType type : types) {
				if (type.getName().equals(role)) {
					found = true;
					break;
				}
			}
			
			if (!found)
				return false;
		}
		
		return true;
	}
	

	/**
	 * For now, this method returns the current valid years based on 
	 * the subsequent two years.  This will possible move into a 
	 * configuration setting eventually.
	 * 
	 * @return list of current valid degree years
	 */
	protected static List<Integer> getDegreeYears() {
		Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);

		List<Integer> validYears = new ArrayList<Integer>();
		validYears.add(currentYear - 3);
		validYears.add(currentYear - 2);
		validYears.add(currentYear - 1);
		validYears.add(currentYear);
		validYears.add(currentYear + 1);
		validYears.add(currentYear + 2);
		validYears.add(currentYear + 3);
		validYears.add(currentYear + 4);
		validYears.add(currentYear + 5);
		validYears.add(currentYear + 6);
		validYears.add(currentYear + 7);
		validYears.add(currentYear + 8);
		validYears.add(currentYear + 9);
		
		return validYears;
	}

	/**
	 * Returns a list of valid Document Types 
	 * 
	 * @return list of valid document types for this submission's degree
	 */
	protected static List<String> getValidDocumentTypes(Submission sub) {
		
		List<DocumentType> validTypes = settingRepo.findAllDocumentTypes();
		// If a valid degree is set, then limit the choices to those of that same level.
		if (sub.getDegree() != null && settingRepo.findDegreeByName(sub.getDegree()) != null) {
			validTypes = settingRepo.findAllDocumentTypes(settingRepo.findDegreeByName(sub.getDegree()).getLevel());
		}
		
		List<String> typeNames = new ArrayList<String>();

		// Gather Document Type names, since we are soft-linking these to Submission
		for(DocumentType type : validTypes) {
			typeNames.add(type.getName());
		}

		return typeNames;
	}

	/**
	 * Load committee members from the supplied submission
	 * object.
	 * 
	 * @param sub
	 *            The submission object
	 * @return List of transient committee members.
	 */
	public static List<TransientMember> loadCommitteeMembers(Submission sub) {
		List<TransientMember> committee = new ArrayList<TransientMember>();

		for (CommitteeMember member: sub.getCommitteeMembers()) {

			TransientMember transientMember = new TransientMember(
					member.getFirstName(), 
					member.getMiddleName(), 
					member.getLastName(), 
					member.getRoles());
			
			committee.add(transientMember);
		}

		return committee;
	}

	/**
	 * Validate the transient list for committee members. This method
	 * checks that all members have their first and last names
	 * 
	 * @param members
	 *            List of maps for each committee member.
	 */
	public static boolean validateCommitteeMembers(Submission sub, List<TransientMember> members ) {

		boolean atLeastOneMember = false;
		int i = 1;
		for (TransientMember member : members) {

			// Check that if we have a first name, then we have a last name.
			if (isEmpty(member.firstName) && isEmpty(member.lastName)) {
				validation.addError("member"+i,"Please provide a first or last name for all committee members.");
			} else {
				atLeastOneMember = true;
			}
			
			// roles
			if (!isValidRoleType(sub, member.roles))
				validation.addError("member"+i,"One of the roles selected is invalid");
		}

		if (!atLeastOneMember)
			validation.addError("committee", "You must specify who is on your committee.");
		
		return true;
	}

	/**
	 * Construct the transient list for committee members from the html
	 * form parameters.
	 * 
	 * @return List of committee member.
	 */
	public static List<TransientMember> parseCommitteeMembers() {

		List<TransientMember> committee = new ArrayList<TransientMember>();

		int i = 1;
		while (params.get("committeeFirstName"+i) != null || params.get("committeeLastName"+i) != null) {

			String firstName = params.get("committeeFirstName"+i);
			String middleName = params.get("committeeMiddleName"+i);
			String lastName = params.get("committeeLastName"+i);
			String[] roles = params.get("committeeRoles"+i,String[].class);
			if (roles == null || (roles.length == 1 && roles[0].trim().length() == 0))
				roles = new String[0];
			i++;

			if ((firstName == null  || firstName.trim().length() == 0) &&
					(lastName == null   || lastName.trim().length() == 0)) 
				// If the first or last name fields are blank then skip this one.
				continue;

			TransientMember member = new TransientMember(
					firstName, 
					middleName, 
					lastName, 
					roles);

			committee.add(member);
		}

		return committee;
	}
	
	
	/**
	 * Save the transient list of committee members into the submission
	 * object.
	 * 
	 * @param sub
	 *            The submission object to be modified.
	 * @param members
	 *            The new list of committee members.
	 */
	public static boolean saveCommitteeMembers(Submission sub, List<TransientMember> members) {


		for (CommitteeMember member : new ArrayList<CommitteeMember>(sub.getCommitteeMembers())) {
			member.delete();
		}


		int i = 1;
		for (TransientMember member : members) {

			String firstName = member.firstName;
			String middleName = member.middleName;
			String lastName = member.lastName;
			List<String> roles = member.roles;
			
			// Make sure that we have a first or last name
			if (firstName != null && firstName.trim().length() == 0)
				firstName = null;
			if (lastName != null && lastName.trim().length() == 0)
				lastName = null;
			if (firstName == null && lastName == null)
				continue;
			

			CommitteeMember newMember = sub.addCommitteeMember(firstName, lastName, middleName).save();
			newMember.setDisplayOrder(i);
			
			for (String role : roles) {
				newMember.addRole(role);
			}
			
			newMember.save();
			
			i++;
		}

		return true;
	}

	
	
	
	
	
	
	/**
	 * Simple data structure to keep committee members while being processed.
	 */
	public static class TransientMember {
		public String firstName;
		public String middleName;
		public String lastName;
		public List<String> roles;

		public TransientMember(String firstName, String middleName, String lastName, List<String> roles) {
			this.firstName = firstName;
			this.middleName = middleName;
			this.lastName = lastName;
			this.roles = new ArrayList<String>(roles);
		}
		
		public TransientMember(String firstName, String middleName, String lastName, String[] roles) {
			this.firstName = firstName;
			this.middleName = middleName;
			this.lastName = lastName;
			this.roles = Arrays.asList(roles);
		}
	}
	
}
