package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.model.Degree;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.model.jpa.JpaSubmissionRepositoryImpl;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.security.impl.SecurityContextImpl;
import play.Logger;
import play.Play;
import play.modules.spring.Spring;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
/**
 * Submit controller
 * This controller manages the student submission forms for Vireo 
 * 
 * @author Dan Galewsky</a>
 * @author <a href="www.scottphillips.com">Scott Phillips</a>
 * @author <a href="bill-ingram.com">Bill Ingram</a>
 */

@With(Authentication.class)
public class Submit extends AbstractVireoController {
	
	
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
	public static void verifyPersonalInformation(Long subId) {
	
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
			
			// First name
			if (firstName == null || firstName.trim().length() == 0)
				validation.addError("firstName","First name is required.");
			
			// Last name
			if (lastName == null || lastName.trim().length() == 0)
				validation.addError("lastName","Last name is required.");
			
			// Year of birth
			Integer birthYearInt = null;
			if (birthYear != null && birthYear.trim().length() > 0) {
				try {
					birthYearInt = Integer.valueOf(birthYear);
					
					if (birthYearInt < 1900 || birthYearInt > Calendar.getInstance().get(Calendar.YEAR) + 1900)
						validation.addError("birthYear","Your birth year is invalid, please use a four digit year.");
				} catch (NumberFormatException nfe) {
					validation.addError("birthYear","Your birth year is invalid.");
				}
			}
			
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
			
			// If the form passed validation -- then save the submission (and updated submitter)
			// and then bring up the license page.
			
			if (!validation.hasErrors()) {
				
				// Save the submission.
				
				if (sub == null)
					sub = subRepo.createSubmission(submitter);
				
				sub.setStudentFirstName(firstName);
				sub.setStudentMiddleName(middleName);
				sub.setStudentLastName(lastName);
				sub.setStudentBirthYear(birthYearInt);
				sub.setDepartment(department);
				sub.setDegree(degree);
				sub.setMajor(major);
				submitter.setPermanentPhoneNumber(permPhone);
				submitter.setPermanentPostalAddress(permAddress);
				submitter.setPermanentEmailAddress(permEmail);
				submitter.setCurrentPhoneNumber(currentPhone);
				submitter.setCurrentPostalAddress(currentAddress);
				
				sub.save();
				submitter.save();

				// Display the license -- passing along the submission id
				
				license(sub.getId());
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
		
		render(submitter,subId,disabledFields,firstName,middleName,lastName,birthYear,department,degree, major, permPhone,permAddress,permEmail,currentPhone,currentAddress);
	}

	@Security(RoleType.STUDENT)
	public static void license(Long subId) {

        Submission sub = subRepo.findSubmission(subId);
        if (sub == null) {
            // something is wrong
            error("Did not receive the expected submission id.");
        } else {
            Person submitter = context.getPerson();

            // This is an existing submission so check that we're the student or administrator here.
            if (sub.getSubmitter() != submitter)
                unauthorized();

            // Not too keen on this; seems messy
            String licenseText = Play.configuration.getProperty("submit.license.text", "");

            // first time here?
            if (params.get("submit_next") != null) {

                if (params.get("licenseAgreement") == null) {
                    validation.addError("laLabel","You must agree to the license agreement before continuing.");
                } else {
                	
                    // TODO: add license text to the database
                	
                	
                    sub.setLicenseAgreementDate(new Date());
                    docInfo(subId);
                    
                }
            }

            render(subId,licenseText);
        }

    }

    @Security(RoleType.STUDENT)
    public static void docInfo(Long subId) {

        String title = params.get("title");
        String degreeMonth = params.get("degreeMonth");
        String degreeYear = params.get("degreeYear");
        String committeeFirstName = params.get("committeeFirstName");
        String committeeMiddleInitial = params.get("committeeMiddleInitial");
        String committeeLastName = params.get("committeeLastName");
        String chairFlag = params.get("chairFlag");
        String chairEmail = params.get("chairEmail");
        String embargo = params.get("embargo");

        if (params.get("submit_next") != null) {

            // Get currently logged in person 
            Person currentPerson = context.getPerson();
            Submission sub = null;

            if (null != subId) {
                sub = subRepo.findSubmission(subId);
            } else {
                // TODO: Are we going to handle this more gracefully in the future?
                error("Did not receive the expected submission id.");
            }
            
            if(null == title || title.equals("")) {
                validation.addError("title", "Please enter a thesis title.");
            }
            
            if(null == degreeMonth || degreeMonth.trim().length() == 0 || !isValidDegreeMonth(Integer.parseInt(degreeMonth))) {
                validation.addError("degreeMonth", "Please select a degree month.");
            }
            
            if(null == degreeYear || !isValidDegreeYear(degreeYear)) {
               validation.addError("degreeYear", "Please select a degree year");
            }
            
            if(!validation.hasErrors()) {
                sub.setDocumentTitle(title);
                sub.setGraduationMonth(Integer.parseInt(degreeMonth));
                sub.save();
                
                fileUpload(subId);
            }
        }
        
        // List of valid degree years for drop-down population
        List degreeYears = getDegreeYears();

        render(subId, title, degreeMonth, degreeYear, committeeFirstName, committeeMiddleInitial, committeeLastName, chairFlag, chairEmail, embargo, degreeYears);
    }


	@Security(RoleType.STUDENT)
	public static void fileUpload(Long subId) {
		render("Submit/FileUpload.html");
	}

	@Security(RoleType.STUDENT)
	public static void confirmAndSubmit(Long subId) {		
		render(subId);		
	}

	@Security(RoleType.STUDENT)
	public static void review() {
		render("Submit/Review.html");
	}

	public static void dump() {
		render("Submit/VerifyPersonalInformation.html");
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
	 * @param departmentName
	 *            The name of the department
	 * @return True if the name is a valid department name.
	 */
	private static boolean isValidDepartment(String departmentName) {
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
	private static boolean isValidDegree(String degreeName) {
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
	private static boolean isValidMajor(String majorName) {
		if (majorName == null || majorName.trim().length() == 0)
			return false;
		for (Major major : settingRepo.findAllMajors() ) {
			if (majorName.equals(major.getName()))
				return true;
		}
		return false;
	}

    /**
     * @param degreeMonth The month of the degree
     * @return True if the name is a valid degree month.
     */
    private static boolean isValidDegreeMonth(int degreeMonth) {
        for (GraduationMonth month : settingRepo.findAllGraduationMonths()) {
            if (degreeMonth == month.getMonth()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param degreeYear The year of the degree
     * @return True if the name is a valid degree year.
     */
    private static boolean isValidDegreeYear(String degreeYear) {
        int year = Integer.parseInt(degreeYear);

        for (Integer y : getDegreeYears()) {
            if (year == y) {
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
    private static List<Integer> getDegreeYears() {
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        
        List<Integer> validYears = new ArrayList<Integer>();
        validYears.add(currentYear - 2);
        validYears.add(currentYear - 1);
        validYears.add(currentYear);
        
        return validYears;
    }
}