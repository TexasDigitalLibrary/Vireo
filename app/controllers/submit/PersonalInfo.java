package controllers.submit;

import static org.tdl.vireo.constant.AppConfig.*;

import static org.tdl.vireo.constant.FieldConfig.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.constant.AppConfig;
import org.tdl.vireo.constant.FieldConfig;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Program;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import au.com.bytecode.opencsv.CSVReader;

import play.Logger;
import play.Play;
import controllers.Application;
import controllers.Security;

/**
 * The first step of the submission process. This is where the student confirms
 * their personal information such as name, the degree and department they are
 * seeking, and contact information.
 * 
 * This step is a bit different from other steps because it can accommodate the
 * possibility of a submission that has not already been created. The first time
 * a student goes to this form the submission will not be created until they
 * have at least passed all the validation checks on this page. After that the
 * submission is created and they can roam through the steps as they desire.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author <a href="bill-ingram.com">Bill Ingram</a>
 * @author Dan Galewsky
 */
public class PersonalInfo extends AbstractSubmitStep {
	
	
	/**
	 * The first screen of the submission process which allows the student to
	 * supply their identity, affiliation, and contact information. We default
	 * the data displayed to information from the submitter's person object if
	 * available. After successfully completing this step a new submission is
	 * persisted in the database and the user is sent on to the license step.
	 *
	 * @param subId
	 *            The id of the submission being edited. This may be null for
	 *            this step, all other steps this id is required.
	 */
	@Security(RoleType.STUDENT)
	public static void personalInfo(Long subId) {
		
		// Bail if they canceled
		if (params.get("submit_cancel") != null)
			Application.index();

		Person submitter = context.getPerson();	
		Submission sub;
		// Check if this is a new submission.
		if (subId == null) {
			// Do we allow multiple submissions?
			boolean allowMultiple = settingRepo.getConfigBoolean(ALLOW_MULTIPLE_SUBMISSIONS);
			
			if (!allowMultiple) {
				// Check if this user already has another submission open.
				List<Submission> otherSubmissions = subRepo.findSubmission(context.getPerson());
				
				for (Submission otherSubmission : otherSubmissions) {
					if (otherSubmission.getState().isActive() || otherSubmission.getState().isInProgress()) {
						error("Multiple submissions are not allowed, and the submitter already has another submission.");
					}
				}
			}
			// Create a submission with default data on it.
			sub = subRepo.createSubmission(submitter);
			if (isFieldEnabled(STUDENT_FIRST_NAME))
				sub.setStudentFirstName(submitter.getFirstName());
			if (isFieldEnabled(STUDENT_MIDDLE_NAME))
				sub.setStudentMiddleName(submitter.getMiddleName());
			if (isFieldEnabled(STUDENT_LAST_NAME))
				sub.setStudentLastName(submitter.getLastName());
			if (isFieldEnabled(STUDENT_BIRTH_YEAR))
				sub.setStudentBirthYear(submitter.getBirthYear());
			if (isFieldEnabled(PROGRAM))
				sub.setProgram(submitter.getCurrentProgram());
			if (isFieldEnabled(COLLEGE))
				sub.setCollege(submitter.getCurrentCollege());
			if (isFieldEnabled(DEPARTMENT))
				sub.setDepartment(submitter.getCurrentDepartment());
			if (isFieldEnabled(DEGREE))
				sub.setDegree(submitter.getCurrentDegree());
			if (isFieldEnabled(MAJOR))
				sub.setMajor(submitter.getCurrentMajor());
			if (isFieldEnabled(STUDENT_ORCID))
				sub.setOrcid(submitter.getOrcid());
			sub.save();
			subId = sub.getId();
			Logger.info("%s (%d: %s) has started submission #%d.",
					submitter.getFormattedName(NameFormat.FIRST_LAST), 
					submitter.getId(), 
					submitter.getEmail(),
					sub.getId());
			
			
		} else {
			// Retrieve the existing submission
			sub = getSubmission();
		}

		String firstName = params.get("firstName");
		String middleName = params.get("middleName");
		String lastName = params.get("lastName");
		String orcid = params.get("orcid");
		String birthYear = params.get("birthYear");
		String program = params.get("program");
		String college = params.get("college");
		String department = params.get("department");
		String degree = params.get("degree");
		String major = params.get("major");
		String permPhone = params.get("permPhone");
		String permAddress = params.get("permAddress");
		String permEmail = params.get("permEmail");
		String currentPhone = params.get("currentPhone");
		String currentAddress = params.get("currentAddress");

		// List of fields which are disabled on the form.
		List<String> disabledFields = new ArrayList<String>();

		// Prepare to display the form.

		// Should the personal group be locked.
		if (isFieldGroupLocked("personal")) {
			if (submitter.getFirstName() != null) {
				disabledFields.add("firstName");
				firstName = submitter.getFirstName();
			}
			if (submitter.getMiddleName() != null) {
				disabledFields.add("middleName");
				middleName = submitter.getMiddleName();
			}
			if (submitter.getLastName() != null) {
				disabledFields.add("lastName");
				lastName = submitter.getLastName();
			}
			if (sub.getOrcid() != null) {
				disabledFields.add("orcid");
				orcid = sub.getOrcid();
			}
			if (submitter.getBirthYear() != null) {
				if (submitter.getBirthYear() == null)
					birthYear = "";
				else
					birthYear = String.valueOf(submitter.getBirthYear());

				disabledFields.add("birthYear");
			}
		}

		// Should the affiliation group be locked.
		if (isFieldGroupLocked("affiliation")) {
			if (isValidProgram(submitter.getCurrentProgram())) {
				disabledFields.add("program");
				program = submitter.getCurrentProgram();
			}
			if (isValidCollege(submitter.getCurrentCollege())) {
				disabledFields.add("college");
				college = submitter.getCurrentCollege();
			}
			if (isValidDepartment(submitter.getCurrentDepartment())) {
				disabledFields.add("department");
				department = submitter.getCurrentDepartment();
			}
			if (isValidDegree(submitter.getCurrentDegree())) {
				disabledFields.add("degree");
				degree = submitter.getCurrentDegree();
			}
			if (isValidMajor(submitter.getCurrentMajor())) {
				disabledFields.add("major");
				major = submitter.getCurrentMajor();
			}
		}

		// Should the contact group be locked
		if (isFieldGroupLocked("contact")) {
			if (submitter.getPermanentPhoneNumber() != null) {
				disabledFields.add("permPhone");
				permPhone = submitter.getPermanentPhoneNumber();
			}
			if (submitter.getPermanentPostalAddress() != null) {
				disabledFields.add("permAddress");
				permAddress = submitter.getPermanentEmailAddress();
			}
			if (submitter.getPermanentEmailAddress() != null) {
				try {
					new InternetAddress(submitter.getPermanentEmailAddress()).validate();
					disabledFields.add("permEmail");
					permEmail = submitter.getPermanentEmailAddress();
				} catch (AddressException ae) {
					// Ignore
				}
			}
			if (submitter.getCurrentPhoneNumber() != null) {
				disabledFields.add("currentPhone");
				currentPhone = submitter.getCurrentPhoneNumber();
			}
			if (submitter.getCurrentPostalAddress() != null) {
				disabledFields.add("currentAddress");
				currentAddress = submitter.getCurrentPostalAddress();
			}
		}

		
		// Save the form data
		if ("personalInfo".equals(params.get("step"))) {
			if (isFieldEnabled(STUDENT_FIRST_NAME))
				sub.setStudentFirstName(firstName);
			if (isFieldEnabled(STUDENT_MIDDLE_NAME))
				sub.setStudentMiddleName(middleName);
			if (isFieldEnabled(STUDENT_LAST_NAME))
				sub.setStudentLastName(lastName);
			if (isFieldEnabled(STUDENT_ORCID))
				sub.setOrcid(orcid);
			if (isFieldEnabled(STUDENT_BIRTH_YEAR)) {
				// Don't fail if the year is invalid
				if (birthYear != null && birthYear.trim().length() > 0) {
					try {
						sub.setStudentBirthYear(Integer.valueOf(birthYear));
					} catch (NumberFormatException ex) {
						sub.setStudentBirthYear(null);
					}
				} else {
					sub.setStudentBirthYear(null);
				}
			}
			
			if (isFieldEnabled(PROGRAM)) {
				if(program != null && program.trim().length() == 0)
					sub.setProgram(null);
				else
					sub.setProgram(program);
			}
			if (isFieldEnabled(COLLEGE)) {
				if(college != null && college.trim().length() == 0)
					sub.setCollege(null);
				else
					sub.setCollege(college);
			}
			if (isFieldEnabled(DEPARTMENT)) {
				if(department != null && department.trim().length() == 0)
					sub.setDepartment(null);
				else
					sub.setDepartment(department);
			}
			if (isFieldEnabled(DEGREE)) {
				if(degree != null && degree.trim().length() == 0)
					sub.setDegree(null);
				else
					sub.setDegree(degree);
				
				// Put the degree level of the student's current degree into the submission
				if (settingRepo.findDegreeByName(degree) != null)
					sub.setDegreeLevel(settingRepo.findDegreeByName(degree).getLevel());
			}
			if (isFieldEnabled(MAJOR)) {
				if(major != null && major.trim().length() == 0)
					sub.setMajor(null);
				else
					sub.setMajor(major);
			}
	
			if (isFieldEnabled(PERMANENT_PHONE_NUMBER))
				submitter.setPermanentPhoneNumber(permPhone);
			if (isFieldEnabled(PERMANENT_POSTAL_ADDRESS))
				submitter.setPermanentPostalAddress(permAddress);
			if (isFieldEnabled(PERMANENT_EMAIL_ADDRESS))
				submitter.setPermanentEmailAddress(permEmail);
			if (isFieldEnabled(CURRENT_PHONE_NUMBER))
				submitter.setCurrentPhoneNumber(currentPhone);
			if (isFieldEnabled(CURRENT_POSTAL_ADDRESS))
				submitter.setCurrentPostalAddress(currentAddress);
			
			// Save the form
			try {
				sub.save();
				submitter.save();
			} catch(RuntimeException re) {
				validation.addError("general", re.getMessage());
			}	
		}
		
		// Verify the form if we are submitting or if jumping from the confirm step.
		if ("personalInfo".equals(params.get("step")) ||
			"confirm".equals(flash.get("from-step"))) {
			verify(sub);
		}
		
		// If we got here because the 'Save and Continue' button on the form was clicked - 
		// then the form was being submitted. Gather arguments, perform validation and 
		// if all is 'ok' then save the submission.

		if (params.get("submit_next") != null && !validation.hasErrors()) {
			// Display the license -- passing along the submission id
			License.license(sub.getId());
		} 
		
		// Has this form ever been displayed before, if not then load all the data.
		if (params.get("form_submit") == null) {
			firstName = sub.getStudentFirstName();
			middleName = sub.getStudentMiddleName();
			lastName = sub.getStudentLastName();
			orcid = sub.getOrcid();
			birthYear = sub.getStudentBirthYear() != null ? String.valueOf(sub.getStudentBirthYear()) : null;
			program = sub.getProgram();
			college = sub.getCollege();
			department = sub.getDepartment();
			degree = sub.getDegree();
			major = sub.getMajor();
			permPhone = submitter.getPermanentPhoneNumber();
			permAddress = submitter.getPermanentPostalAddress();
			permEmail = submitter.getPermanentEmailAddress();
			currentPhone = submitter.getCurrentPhoneNumber();
			currentAddress = submitter.getCurrentPostalAddress();
			orcid = submitter.getOrcid();
		}
	
	
	
		// Get display settings
		List<String> stickies = new ArrayList<String>();
		String stickiesRaw = settingRepo.getConfigValue(SUBMIT_PERSONAL_INFO_STICKIES);
		if (stickiesRaw != null && !"null".equals(stickiesRaw)) {
			try {
				CSVReader reader = new CSVReader(new StringReader(stickiesRaw));
				stickies = Arrays.asList(reader.readNext());
				reader.close();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
		
		String grantor = settingRepo.getConfigValue(AppConfig.GRANTOR,"Unknown Institution");

		renderTemplate("Submit/personalInfo.html",submitter, subId, disabledFields, stickies,

				// Form data
				firstName, middleName, lastName, orcid, birthYear, grantor, program, college, department, 
				degree, major, permPhone, permAddress, permEmail, currentPhone, currentAddress
				);
	}

 
	/**
	 * Verify this step has successfully been completed. This may be called from
	 * two places, here on the PersonalInfo form and from the confirmation step
	 * to ensure that everything has been completed.
	 * 
	 * @return true if there were no errors, otherwise false.
	 */
    public static boolean verify(Submission sub) {
    	
    	int numberOfErrorsBefore = validation.errors().size();
		
		// Student name
		if (isFieldRequired(STUDENT_FIRST_NAME) && isFieldRequired(STUDENT_LAST_NAME)) {
			// If both the first and last names are required they really mean than either are required because students might only have one name.
			if (isEmpty(sub.getStudentFirstName()) && 
				isEmpty(sub.getStudentLastName())) {
				validation.addError("firstName","Either the first or last name is required");
				validation.addError("lastName","Either the first or last name is required");
			}
		} else if (isFieldRequired(STUDENT_FIRST_NAME) && isEmpty(sub.getStudentFirstName())) {
			validation.addError("firstName","Your first name is required");
		} else if (isFieldRequired(STUDENT_LAST_NAME) && isEmpty(sub.getStudentFirstName())) {
			validation.addError("lastName","Your last name is required");
		}
		if (isFieldRequired(STUDENT_MIDDLE_NAME) && isEmpty(sub.getStudentMiddleName()))
			validation.addError("middleName","Your middle name is required");
		if (isFieldRequired(STUDENT_ORCID) && isEmpty(sub.getOrcid()))
			validation.addError("orcid","Your ORCID id is required");
	
		// Birth year
		if (sub.getStudentBirthYear() != null && sub.getStudentBirthYear() < 1900)
			validation.addError("birthYear","Your birth year is invalid, please use a four digit year");
		if (isFieldRequired(STUDENT_BIRTH_YEAR) && sub.getStudentBirthYear() == null)
			validation.addError("birthYear","Your birth year is required");

		// Program
		if (sub.getProgram() != null && !isValidProgram(sub.getProgram()))
			validation.addError("program", "The program selected is not valid");
		if (isFieldRequired(PROGRAM) && isEmpty(sub.getProgram())) 
			validation.addError("program", "Program is required");
		
		// College
		if (sub.getCollege() != null && !isValidCollege(sub.getCollege()))
			validation.addError("college","The college selected is not valid");
		if (isFieldRequired(COLLEGE) && isEmpty(sub.getCollege()))
			validation.addError("college","College is required");
		
		// Department
		if (sub.getDepartment() != null && !isValidDepartment(sub.getDepartment()))
			validation.addError("department","The department selected is not valid");
		if (isFieldRequired(DEPARTMENT) && isEmpty(sub.getDepartment()))
			validation.addError("department","Department is required");
		
		// Degree
		if (sub.getDegree() != null && !isValidDegree(sub.getDegree()))
			validation.addError("degree","The degree selected is not valid");
		if (isFieldRequired(DEGREE) && isEmpty(sub.getDegree())) 
			validation.addError("degree","Degree is required");
		
		// Major
		if (sub.getMajor() != null && !isValidMajor(sub.getMajor()))
			validation.addError("major","The major selected is not valid");
		if (isFieldRequired(COLLEGE) && isEmpty(sub.getMajor())) 
			validation.addError("major","Major is required");
		
		// Permanent Phone
		if (isFieldRequired(PERMANENT_PHONE_NUMBER) && isEmpty(sub.getSubmitter().getPermanentPhoneNumber())) 
			validation.addError("permPhone","Permanent phone number is required");
		
		// Permanent Address
		if (isFieldRequired(PERMANENT_POSTAL_ADDRESS) && isEmpty(sub.getSubmitter().getPermanentPostalAddress())) 
			validation.addError("permAddress","Permanent address is required");
		
		// Permanent Email
		if (isFieldRequired(PERMANENT_EMAIL_ADDRESS) && isEmpty(sub.getSubmitter().getPermanentEmailAddress()))
			validation.addError("permEmail","Permanent email is required");
		else if (isFieldEnabled(PERMANENT_EMAIL_ADDRESS) && sub.getSubmitter().getPermanentEmailAddress() != null) {
			try {
				new InternetAddress(sub.getSubmitter().getPermanentEmailAddress()).validate();
			} catch (AddressException e) {
				validation.addError("permEmail","The permanent email address '"+sub.getSubmitter().getPermanentEmailAddress()+"' is invalid.");
			}
		}

		// Current Phone
		if (isFieldRequired(CURRENT_PHONE_NUMBER) && isEmpty(sub.getSubmitter().getCurrentPhoneNumber()))
			validation.addError("currentPhone","Current phone number is required");
		
		// Current Address
		if (isFieldRequired(CURRENT_POSTAL_ADDRESS) && isEmpty(sub.getSubmitter().getCurrentPostalAddress()))
			validation.addError("currentAddress","Current address is required");
		
		// Check if we've added any new errors. If so return false;
		if (numberOfErrorsBefore == validation.errors().size()) 
			return true;
		else
			return false;
    }
	
	
	/**
	 * Internal method to determine if a group of information should be locked.
	 * 
	 * TODO: Right now this is pulling from the application.conf, it should come
	 * from system wide configuration.
	 * 
	 * @param group
	 *            The group to check.
	 * @return Weather the group provided should be locked.
	 */
	private static boolean isFieldGroupLocked(String group) {
		String config = Play.configuration.getProperty("submit.field.lock", "");
		if (config.contains(group))
			return true;
		return false;
	}

	/**
	 * @param programName
	 *            The name of the program
	 * @return True if the name is a valid program name.
	 */
	protected static boolean isValidProgram(String programName) {
		
		if (programName == null || programName.trim().length() == 0)
			return false;
		
		if (settingRepo.findAllPrograms().size() > 0) {
			// If there is a list of programs it must be in the list.
			for (Program program : settingRepo.findAllPrograms() ) {
				if (programName.equals(program.getName()))
					return true;
			}
			return false;
		}
		
		// Otherwise, it can be anything
		return true;
	}
	
	/**
	 * @param collegeName
	 *            The name of the college
	 * @return True if the name is a valid college name.
	 */
	protected static boolean isValidCollege(String collegeName) {
		
		if (collegeName == null || collegeName.trim().length() == 0)
			return false;
		
		if (settingRepo.findAllColleges().size() > 0) {
			// If there is a list of colleges it must be in the list.
			for (College college : settingRepo.findAllColleges() ) {
				if (collegeName.equals(college.getName()))
					return true;
			}
			return false;
		}
		
		// Otherwise, it can be anything
		return true;
	}

	/**
	 * @param departmentName
	 *            The name of the department
	 * @return True if the name is a valid department name.
	 */
	protected static boolean isValidDepartment(String departmentName) {
		if (departmentName == null || departmentName.trim().length() == 0)
			return false;
		
		if (settingRepo.findAllDepartments().size() > 0) {
			// If there is a list of departments it must be in the list.
			for (Department department : settingRepo.findAllDepartments() ) {
				if (departmentName.equals(department.getName()))
					return true;
			}
			return false;
		}
		
		// Otherwise, it can be anything.
		return true;
	}

	/**
	 * @param degreeName
	 *            The name of the degree
	 * @return True if the name is a valid degree name.
	 */
	protected static boolean isValidDegree(String degreeName) {
		if (degreeName == null || degreeName.trim().length() == 0)
			return false;
		
		// Unlike the other fields, the degree must be pre-defined. Otherwise
		// we would not know what level to assign this submission.
		for (Degree degree : settingRepo.findAllDegrees() ) {
			if (degreeName.equals(degree.getName()))
				return true;
		}
		return false;
	}

	/**
	 * @param majorName
	 *            The name of the major.
	 * @return True if the name is a valid major name.
	 */
	protected static boolean isValidMajor(String majorName) {
		if (majorName == null || majorName.trim().length() == 0)
			return false;
		
		if (settingRepo.findAllMajors().size() > 0) {
			// If there is a list of majors it must be in the list.
			for (Major major : settingRepo.findAllMajors() ) {
				if (majorName.equals(major.getName()))
					return true;
			}
			return false;
		}
		
		// Otherwise, it can be anything
		return true;
	}
	
	
	
	
	
}
