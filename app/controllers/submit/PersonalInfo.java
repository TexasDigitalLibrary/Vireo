package controllers.submit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.model.College;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import play.Play;
import controllers.Application;
import controllers.Security;
import controllers.Student;

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

		// Get Configuration
		boolean requestCollege = (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_COLLEGE) != null) ? true : false;
		boolean requestBirth = (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_BIRTH) != null) ? true : false;

		// Bail if they canceled
		if (params.get("submit_cancel") != null)
			Application.index();


		Person submitter = context.getPerson();

		// Note: since this is the first step submission may be null.

		Submission sub = null;

		// If we already have a subid - then fetch the corresponding submission

		if (subId != null) {
			sub = subRepo.findSubmission(subId);

			// This is an existing submission so check that we're the student or administrator here.
			if (sub.getSubmitter() != submitter) 
				unauthorized();
		}

		String firstName = params.get("firstName");
		String middleName = params.get("middleName");
		String lastName = params.get("lastName");
		String birthYear = params.get("birthYear");
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

		// If we got here because the 'Save and Continue' button on the form was clicked - 
		// then the form was being submitted. Gather arguments, perform validation and 
		// if all is 'ok' then save the submission.

		if (params.get("submit_next") != null) {

			verify(firstName, lastName, birthYear, college, department, degree, major, permPhone, permAddress, permEmail);
			
			if (!validation.hasErrors()) {

				// Data conversions, they shouldn't fail because they passed verify
				// Year of birth
				Integer birthYearInt = null;
				if (requestBirth && birthYear != null && birthYear.trim().length() > 0) {
						birthYearInt = Integer.valueOf(birthYear);
				}
				
				// If there are no erros, save the submission.
				if (sub == null)
					sub = subRepo.createSubmission(submitter);

				sub.setStudentFirstName(firstName);
				sub.setStudentMiddleName(middleName);
				sub.setStudentLastName(lastName);
				if (requestBirth)
					sub.setStudentBirthYear(birthYearInt);
				if (requestCollege)
					sub.setCollege(college);
				sub.setDepartment(department);
				sub.setDegree(degree);

				// Put the degree level of the student's current degree into the submission

				sub.setDegreeLevel(settingRepo.findDegreeByName(degree).getLevel());

				sub.setMajor(major);
				sub.setSubmissionDate(new Date());
				submitter.setPermanentPhoneNumber(permPhone);
				submitter.setPermanentPostalAddress(permAddress);
				submitter.setPermanentEmailAddress(permEmail);
				submitter.setCurrentPhoneNumber(currentPhone);
				submitter.setCurrentPostalAddress(currentAddress);

				try {
					sub.save();
					submitter.save();
				} catch(RuntimeException re) {
					validation.addError("general", re.getMessage());
				}

				// Display the license -- passing along the submission id
				flash.put("nextStep", "true");
				License.license(sub.getId());
			}

		} else if (sub != null) {

			// Initial form display, for an existing submission. Fill in all data from the submission record.

			firstName = sub.getStudentFirstName();
			middleName = sub.getStudentMiddleName();
			lastName = sub.getStudentLastName();
			if (submitter.getBirthYear() == null)
				birthYear = "";
			else
				birthYear = String.valueOf(submitter.getBirthYear());

			college = sub.getCollege();
			if (!isValidCollege(college))
				college = null;

			department = sub.getDepartment();
			if (!isValidDepartment(department))
				department = null;

			degree = sub.getDegree();
			if (!isValidDegree(degree))
				degree = null;

			major = sub.getMajor();
			if (!isValidMajor(major))
				major = null;

			permPhone = submitter.getPermanentPhoneNumber();
			permAddress = submitter.getPermanentPostalAddress();
			permEmail = submitter.getPermanentEmailAddress();
			currentPhone = submitter.getCurrentPhoneNumber();
			currentAddress = submitter.getCurrentPostalAddress();
		} else {

			// Initial form display, with no submission created. Fill in as much of the form as we know.

			firstName = submitter.getFirstName();
			middleName = submitter.getMiddleName();
			lastName = submitter.getLastName();
			if (submitter.getBirthYear() == null)
				birthYear = "";
			else
				birthYear = String.valueOf(submitter.getBirthYear());

			college = submitter.getCurrentCollege();
			if (!isValidCollege(college))
				college = null;

			department = submitter.getCurrentDepartment();
			if (!isValidDepartment(department))
				department = null;

			degree = submitter.getCurrentDegree();
			if (!isValidDegree(degree))
				degree = null;

			major = submitter.getCurrentMajor();
			if (!isValidMajor(major))
				major = null;

			permPhone = submitter.getPermanentPhoneNumber();
			permAddress = submitter.getPermanentPostalAddress();
			permEmail = submitter.getPermanentEmailAddress();
			currentPhone = submitter.getCurrentPhoneNumber();
			currentAddress = submitter.getCurrentPostalAddress();
		}

		// Display the for with appropriate values filled in

		String grantor = settingRepo.getConfig(Configuration.GRANTOR,"Unknown Institution");

		renderTemplate("Submit/personalInfo.html",submitter, subId, disabledFields, requestCollege, requestBirth,

				// Form data
				firstName, middleName, lastName, birthYear, grantor, college, department, 
				degree, major, permPhone, permAddress, permEmail, currentPhone, currentAddress


				);
	}

	
	/**
	 * Verify this step has successfully been completed. This may be called from
	 * two places, here on the PersonalInfo form and from the confirmation step
	 * to ensure that everything has been completed.
	 * 
	 * @param firstName
	 *            The Student's firstname
	 * @param lastName
	 *            The Student's lastname
	 * @param birthYear
	 *            The student's birthYear
	 * @param college
	 *            The selected college
	 * @param department
	 *            The selected department
	 * @param degree
	 *            The selected degree
	 * @param major
	 *            The selected major.
	 * @return Return true if everything passed, otherwise false.
	 */
	public static boolean verify(String firstName,String lastName, String birthYear, String college, String department, String degree, String major, String permPhone, String permAddress, String permEmail) {
		
		// Get submission configuration.
		boolean requestCollege = (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_COLLEGE) != null) ? true : false;
		boolean requestBirth = (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_BIRTH) != null) ? true : false;
		
		int numberOfErrorsBefore = validation.errors().size();
		
		// First name
		if (firstName == null || firstName.trim().length() == 0)
			validation.addError("firstName","First name is required.");

		// Last name
		if (lastName == null || lastName.trim().length() == 0)
			validation.addError("lastName","Last name is required.");

		// Year of birth
		Integer birthYearInt = null;
		if (requestBirth && birthYear != null && birthYear.trim().length() > 0) {
			try {
				birthYearInt = Integer.valueOf(birthYear);

				if (birthYearInt < 1900 || birthYearInt > Calendar.getInstance().get(Calendar.YEAR) + 1900)
					validation.addError("birthYear","Your birth year is invalid, please use a four digit year.");
			} catch (NumberFormatException nfe) {
				validation.addError("birthYear","Your birth year is invalid.");
			}
		}

		// College
		if (requestCollege && !isValidCollege(college))
			validation.addError("college", "College is required.");

		// Department
		if (!isValidDepartment(department))
			validation.addError("department", "Department is required.");

		// Degree
		if (!isValidDegree(degree))
			validation.addError("degree", "Degree is required.");

		// Major
		if (!isValidMajor(major))
			validation.addError("major", "Major is required.");
		
		// Permanent Phone
		if (permPhone == null || permPhone.trim().length() == 0)
			validation.addError("permPhone","Permanent phone number is required");

		// Permanent Address
		if (permAddress == null || permAddress.trim().length() == 0)
			validation.addError("permAddress","Permanent address is required");

		// Permanent Email
		if (permEmail != null && permEmail.trim().length() > 0) {
			try {
				new InternetAddress(permEmail).validate();
			} catch (AddressException ae) {
				validation.addError("permEmail","The permanent email address you provided is invalid.");
			}
		}
				
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
	 * @param collegeName
	 *            The name of the college
	 * @return True if the name is a valid college name.
	 */
	protected static boolean isValidCollege(String collegeName) {
		if (collegeName == null || collegeName.trim().length() == 0)
			return false;
		for (College college : settingRepo.findAllColleges() ) {
			if (collegeName.equals(college.getName()))
				return true;
		}
		return false;
	}

	/**
	 * @param departmentName
	 *            The name of the department
	 * @return True if the name is a valid department name.
	 */
	protected static boolean isValidDepartment(String departmentName) {
		if (departmentName == null || departmentName.trim().length() == 0)
			return false;
		for (Department department : settingRepo.findAllDepartments() ) {
			if (departmentName.equals(department.getName()))
				return true;
		}
		return false;
	}

	/**
	 * @param degreeName
	 *            The name of the degree
	 * @return True if the name is a valid degree name.
	 */
	protected static boolean isValidDegree(String degreeName) {
		if (degreeName == null || degreeName.trim().length() == 0)
			return false;
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
		for (Major major : settingRepo.findAllMajors() ) {
			if (majorName.equals(major.getName()))
				return true;
		}
		return false;
	}
	
	
	
	
	
}
