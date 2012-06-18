package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.model.*;
import play.Logger;
import play.Play;
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
                    sub.setLicenseAgreementDate(new Date());

                    sub.save();

                    docInfo(subId);
                    //fileUpload(subId);
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
        String docType = params.get("docType");
        String abstractText = params.get("abstractText");
        String keywords = params.get("keywords");
        String committeeFirstName = params.get("committeeFirstName");
        String committeeMiddleInitial = params.get("committeeMiddleInitial");
        String committeeLastName = params.get("committeeLastName");
        String chairFlag = params.get("chairFlag");
        String chairEmail = params.get("chairEmail");
        String embargo = params.get("embargo");
        
        // Get currently logged in person 
        Person currentPerson = context.getPerson();
        Submission sub = null;

        if (null != subId) {
            sub = subRepo.findSubmission(subId);
        }

        if (null == sub) {
            // TODO: Are we going to handle this more gracefully in the future?
            error("Did not receive the expected submission id.");
        }
        
        if (params.get("submit_next") != null) {
            
            if(null == title || title.equals("")) {
                validation.addError("title", "Please enter a thesis title.");
            }
            
            if(null == degreeMonth || degreeMonth.trim().length() == 0 || !isValidDegreeMonth(Integer.parseInt(degreeMonth))) {
                validation.addError("degreeMonth", "Please select a degree month.");
            }
            
            if(null == degreeYear || !isValidDegreeYear(degreeYear)) {
               validation.addError("degreeYear", "Please select a degree year");
            }
            
            if(null == docType || !isValidDocType(docType)) {
                validation.addError("docType", "Please select a Document Type");
            }
            
            if(null == abstractText || abstractText.trim().length() == 0) {
                validation.addError("abstractText", "Please enter an abstract");
            }
            
            if(null == keywords || keywords.trim().length() == 0) {
                validation.addError("keywords", "Please enter at least one keyword");
            }
            
            if(null == chairEmail || chairEmail.trim().length() == 0) {
                validation.addError("chairEmail", "Please enter an email address for the committee chair");
            }
            
            if(null == embargo) {
                validation.addError("embargo", "Please choose an embargo option");
            }
            
            if(!validation.hasErrors()) {
                sub.setDocumentTitle(title);
                sub.setGraduationMonth(Integer.parseInt(degreeMonth));
                sub.setGraduationYear(Integer.parseInt(degreeYear));
                sub.setDocumentType(docType);
                sub.setDocumentAbstract(abstractText);
                sub.setDocumentKeywords(keywords);
                sub.setCommitteeContactEmail(chairEmail);
                sub.setEmbargoType(settingRepo.findEmbargoType(Long.parseLong(embargo)));
                sub.save();
                
                fileUpload(subId);
            }
        }
        
        // List of valid degree years for drop-down population
        List<Integer> degreeYears = getDegreeYears();
        renderArgs.put("degreeYears", degreeYears);
        
        
        List<String> docTypes = getValidDocumentTypes(sub);
        renderArgs.put("docTypes", docTypes);
        
        // List of all *active* Embargo Types
        List<EmbargoType> embargoTypes = settingRepo.findAllActiveEmbargoTypes();
        renderArgs.put("embargoTypes", embargoTypes);
        
        render( subId, 
                title, 
                degreeMonth, 
                degreeYear, 
                abstractText, 
                keywords, 
                committeeFirstName, 
                committeeMiddleInitial, 
                committeeLastName, 
                chairFlag, 
                chairEmail, 
                embargo);
    }

    // Handle File Upload
    
	@Security(RoleType.STUDENT)
	public static void fileUpload(Long subId) {

        Submission sub = subRepo.findSubmission(subId);
        if (sub == null) {
            // something is wrong
            error("Did not receive the expected submission id.");
        } else {
            Person submitter = context.getPerson();

            // This is an existing submission so check that we're the student or administrator here.
            if (sub.getSubmitter() != submitter)
                unauthorized();

            // If the upload manuscript button is pressed - then add the manuscript as an attachment
            if (params.get("uploadPrimary") != null) {

                File primaryDocument = params.get("primaryDocument",File.class);

                if (primaryDocument == null)
                    Logger.info("Doc is null");
                else
                    Logger.info("Doc: " + primaryDocument.getClass().getName());

                try {
                    sub.addAttachment(primaryDocument, AttachmentType.PRIMARY);
                    sub.save();
                } catch (IOException e) {
                    validation.addError("primaryDocument", "Error uploading primary document.");
                } catch (IllegalArgumentException e) {
                    validation.addError("primaryDocument","Error uploading primary document.");
                }
            }

            // If the upload supplementary button is pressed - then add the manuscript as an attachment
            if (params.get("uploadSupplementary") != null) {

                File supplementaryDocument = params.get("supplementaryDocument",File.class);

                if (supplementaryDocument == null)
                    Logger.info("Doc is null");
                else
                    Logger.info("Doc: " + supplementaryDocument.getClass().getName());

                try {
                    sub.addAttachment(supplementaryDocument, AttachmentType.SUPPLEMENTAL);
                    sub.save();
                } catch (IOException e) {
                    validation.addError("supplementaryDocument","Error uploading supplementary document.");
                } catch (IllegalArgumentException e) {
                    validation.addError("supplementaryDocument","Error uploading supplementary document.");
                }
            }

            // Submit was clicked
            if (params.get("submit_next") != null) {

                // no files was uploaded
                if (sub.getPrimaryDocument() == null)
                    validation.addError("primaryDocument", "A manuscript file must be uploaded.");

                // Finally, if all is well, we can move on
                if (!validation.hasErrors())
                    confirmAndSubmit(subId);
            }

            // Initialize variables
            Attachment primaryAttachment = sub.getPrimaryDocument();
            List<Attachment> supplementalAttachments = sub.getSupplementalDocuments();

            render(subId, primaryAttachment, supplementalAttachments);
        }
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
     * Helper for assigning <em>class="current"</em> to the nav item
     * @param name1
     * @param name2
     * @return
     */
    public static String giveCurrentClassIfEqual(String name1, String name2) {
        return name1 == name2 ? "class=current" : "";
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
     * 
     * @param docType
     * @return 
     */
    private static boolean isValidDocType(String docType) {
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
    private static List<Integer> getDegreeYears() {
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        
        List<Integer> validYears = new ArrayList<Integer>();
        validYears.add(currentYear - 2);
        validYears.add(currentYear - 1);
        validYears.add(currentYear);
        
        return validYears;
    }
    
    /**
     * Returns a list of valid Document Types 
     * 
     * @return list of valid document types for this submission's degree
     * 
     * TODO: Since this will be applicable on the admin side of things, move this to 
     *       a SubmissionService along with related concerns.
     */
    private static List<String> getValidDocumentTypes(Submission sub) {
        List<DocumentType> validTypes = settingRepo.findAllDocumentTypes(settingRepo.findDegreeByName(sub.getDegree()).getLevel());
        List<String> typeNames = new ArrayList<String>();
        
        // Gather Document Type names, since we are soft-linking these to Submission
        for(DocumentType type : validTypes) {
            typeNames.add(type.getName());
        }
        
        return typeNames;
    }
}