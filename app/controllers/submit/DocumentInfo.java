package controllers.submit;

import static org.tdl.vireo.constant.AppConfig.*;
import static org.tdl.vireo.constant.FieldConfig.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.LocaleUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.constant.FieldConfig;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.Language;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.proquest.ProquestSubject;

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
		String docType = params.get("docType");
		String abstractText = params.get("abstractText");
		String keywords = params.get("keywords");
		String subjectPrimary = params.get("subject-primary");
		String subjectSecondary = params.get("subject-secondary");
		String subjectTertiary = params.get("subject-tertiary");
		String docLanguage = params.get("language");
		String chairEmail = params.get("chairEmail");
		String embargo = params.get("embargo");
		String umi = params.get("umi");

		List<Map<String,String>> committee = parseCommitteeMembers();

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
			
			if (isFieldEnabled(EMBARGO_TYPE)) {
				try {
					sub.setEmbargoType(settingRepo.findEmbargoType(Long.parseLong(embargo)));
				} catch (RuntimeException re){
					if (isFieldRequired(EMBARGO_TYPE))
						validation.addError("embargo", "Please select a valid embargo option");
				}
			}
			
			if (isFieldEnabled(UMI_RELEASE)) {
				if (umi != null && umi.trim().length() > 0) 
					sub.setUMIRelease(true);
				else
					sub.setUMIRelease(false);
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
			
			if (isFieldEnabled(FieldConfig.COMMITTEE_CONTACT_EMAIL))
				chairEmail = sub.getCommitteeContactEmail();

			// Get the list of committee members
			if (isFieldRequired(COMMITTEE))
				committee = loadCommitteeMembers(sub);

			if (isFieldEnabled(EMBARGO_TYPE) && sub.getEmbargoType() != null)
				embargo = sub.getEmbargoType().getId().toString();

			if (isFieldEnabled(UMI_RELEASE) && sub.getUMIRelease() != null && sub.getUMIRelease() != false)
				umi = "true";
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
		
		renderTemplate("Submit/documentInfo.html", subId, stickies,

				title, degreeMonth, degreeYear, docType, abstractText, keywords, 
				subjectPrimary, subjectSecondary, subjectTertiary, docLanguage, committeeSlots, 
				committee, chairEmail, embargo, umi);
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

		// Graduation Date (month & yeaor)
		if (!isValidDegreeMonth(sub.getGraduationMonth()))
			validation.addError("degreeMonth", "Please select a degree month");

		if (!isValidDegreeYear(sub.getGraduationYear()))
			validation.addError("degreeYear", "Please select a degree year");
	
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
			List<Map<String,String>> committee = loadCommitteeMembers(sub);
			validateCommitteeMembers(committee);
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
		
		// Embargo
		if (isFieldRequired(EMBARGO_TYPE) && sub.getEmbargoType() == null)
			validation.addError("embargo", "Please choose an embargo option");

		// UMI
		if (isFieldRequired(UMI_RELEASE) && sub.getUMIRelease() == null)
			validation.addError("umi", "Please select whether to release to UMI.");
		
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
	 * Load committee members into a list of maps from the supplied submission
	 * object.
	 * 
	 * @param sub
	 *            The submission object
	 * @return List of transient committee members.
	 */
	public static List<Map<String,String>> loadCommitteeMembers(Submission sub) {
		List<Map<String,String>> committee = new ArrayList<Map<String,String>>();

		for (CommitteeMember member: sub.getCommitteeMembers()) {

			Map<String,String> hash = new HashMap<String,String>();
			hash.put("firstName",member.getFirstName());
			hash.put("middleName",member.getMiddleName());
			hash.put("lastName",member.getLastName());
			hash.put("chairFlag",member.isCommitteeChair() ? "true" : null);
			committee.add(hash);
		}

		return committee;
	}

	/**
	 * Validate the transient list of maps for committee members. This method
	 * checks that all members have their first and last names, and that there
	 * is atleast one member who is a committee chair.
	 * 
	 * @param members
	 *            List of maps for each committee member.
	 */
	public static boolean validateCommitteeMembers(List<Map<String,String>> members ) {

		boolean atLeastOneMember = false;
		boolean atLeastOneChair = false;	
		int i = 1;
		for (Map<String,String> member : members) {

			// Check that if we have a first name, then we have a last name.
			String firstName = member.get("firstName");
			String lastName = member.get("lastName");
			
			if (isEmpty(firstName) && isEmpty(lastName)) {
				validation.addError("member"+i,"Please provide a first or last name for all committee members.");
			} else {
				atLeastOneMember = true;
			}

			String chairFlag = member.get("chairFlag");
			if (!isEmpty(chairFlag))
				atLeastOneChair = true;
			i++;
		}

		if (!atLeastOneMember)
			validation.addError("committee", "You must specify who is on your committee.");
		else if (!atLeastOneChair)
			validation.addError("committee", "You must specify which members are chairs or co-chairs of your committee.");

		return true;
	}

	/**
	 * Construct the transient list of maps for committee members from the html
	 * form parameters.
	 * 
	 * @return List of maps for each committee member.
	 */
	public static List<Map<String,String>> parseCommitteeMembers() {

		List<Map<String,String>> committee = new ArrayList<Map<String,String>>();

		int i = 1;
		while (params.get("committeeFirstName"+i) != null || params.get("committeeLastName"+i) != null) {

			String firstName = params.get("committeeFirstName"+i);
			String middleName = params.get("committeeMiddleName"+i);
			String lastName = params.get("committeeLastName"+i);
			String chairFlag = params.get("committeeChairFlag"+i);
			i++;


			if ((firstName == null  || firstName.trim().length() == 0) &&
					(lastName == null   || lastName.trim().length() == 0)) 
				// If the first or last name fields are blank then skip this one.
				continue;

			Map<String,String> member = new HashMap<String,String>();
			member.put("firstName",firstName);
			member.put("middleName",middleName);
			member.put("lastName",lastName);
			member.put("chairFlag",chairFlag);

			committee.add(member);
		}

		return committee;
	}

	/**
	 * Save the transient list of maps for committee members into the submission
	 * object.
	 * 
	 * @param sub
	 *            The submission object to be modified.
	 * @param members
	 *            The new list of committee members.
	 */
	public static boolean saveCommitteeMembers(Submission sub, List<Map<String,String>> members) {


		for (CommitteeMember member : new ArrayList<CommitteeMember>(sub.getCommitteeMembers())) {
			member.delete();
		}


		int i = 1;
		for (Map<String,String> member : members) {

			String firstName = member.get("firstName");
			String middleName = member.get("middleName");
			String lastName = member.get("lastName");
			String chairFlag = member.get("chairFlag");

			boolean chair = false;
			if (chairFlag != null && chairFlag.trim().length() > 0)
				chair = true;
			
			// Make sure that we have a first or last name
			if (firstName != null && firstName.trim().length() == 0)
				firstName = null;
			if (lastName != null && lastName.trim().length() == 0)
				lastName = null;
			if (firstName == null && lastName == null)
				continue;
			

			CommitteeMember newMember = sub.addCommitteeMember(firstName, lastName, middleName, chair);
			newMember.setDisplayOrder(i);
			newMember.save();
			
			i++;
		}

		return true;
	}

}
