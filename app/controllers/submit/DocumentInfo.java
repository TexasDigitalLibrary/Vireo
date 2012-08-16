package controllers.submit;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.GraduationMonth;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import play.Logger;
import play.mvc.Scope.Params;
import controllers.Security;
import controllers.Student;

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

		// Get our configuration;
		boolean requestUMI = (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_UMI) != null) ? true : false;
		
		// Validate the submission
		Submission sub = getSubmission();
		
		// Get our form paramaters.
		String title = params.get("title");
        String degreeMonth = params.get("degreeMonth");
        String degreeYear = params.get("degreeYear");
        String docType = params.get("docType");
        String abstractText = params.get("abstractText");
        String keywords = params.get("keywords");
        String chairEmail = params.get("chairEmail");
        String embargo = params.get("embargo");
        String umi = params.get("umi");
        
        List<Map<String,String>> committee = parseCommitteeMembers();
        
        // If this is the first time loading, then get the current values off the submission.
        if (params.get("submit_next") == null) {
        	title = sub.getDocumentTitle();
        	
        	if (sub.getGraduationMonth() != null)
        		degreeMonth =  sub.getGraduationMonth().toString();
        	
            if (sub.getGraduationYear() != null)
            	degreeYear = sub.getGraduationYear().toString();
            
            docType = sub.getDocumentType();
            abstractText = sub.getDocumentAbstract();
            keywords = sub.getDocumentKeywords();
            chairEmail = sub.getCommitteeContactEmail();
            
            // Get the list of committee members
            committee = loadCommitteeMembers(sub);
            
            if (sub.getEmbargoType() != null)
            	embargo = sub.getEmbargoType().getId().toString();
            
            if (sub.getUMIRelease() != null && sub.getUMIRelease() == false)
            	umi = "true";
        }
        
		if (!"true".equals(flash.get("nextStep"))) 
			verify(title, degreeMonth, degreeYear, docType, abstractText, keywords, committee, chairEmail, embargo);
        
        // Validate the form and if all is ok - save in the model
        if (params.get("submit_next") != null && !validation.hasErrors() ) {
            sub.setDocumentTitle(title);
            sub.setGraduationMonth(Integer.parseInt(degreeMonth));
            sub.setGraduationYear(Integer.parseInt(degreeYear));
            sub.setDocumentType(docType);
            sub.setDocumentAbstract(abstractText);
            sub.setDocumentKeywords(keywords);
            sub.setCommitteeContactEmail(chairEmail);
            sub.setEmbargoType(settingRepo.findEmbargoType(Long.parseLong(embargo)));
            if (requestUMI && umi != null && umi.trim().length() > 0)
            	sub.setUMIRelease(false);
            else if (requestUMI)
            	sub.setUMIRelease(true);
            
            saveCommitteeMembers(sub, committee);
            
            sub.save();
            
            // Once the form has been saved - go to the fileUpload form
			flash.put("nextStep", "true");
            FileUpload.fileUpload(subId);
        }
        
        // List of valid degree years for drop-down population
        List<Integer> degreeYears = getDegreeYears();
        renderArgs.put("degreeYears", degreeYears);
        
        
        List<String> docTypes = getValidDocumentTypes(sub);
        renderArgs.put("docTypes", docTypes);
        
        // List of all *active* Embargo Types
        List<EmbargoType> embargoTypes = settingRepo.findAllActiveEmbargoTypes();
        renderArgs.put("embargoTypes", embargoTypes);
        
        // Figure out how mayn committee spots to show.
        int committeeSlots = 4;
        if (committee.size() > 3)
        	committeeSlots = committee.size();
        if (params.get("submit_add") != null)
        	committeeSlots += 4;
        
        renderTemplate("Submit/documentInfo.html", subId, requestUMI,
                
        		title, degreeMonth, degreeYear, docType, abstractText, keywords, 
                committeeSlots, committee, chairEmail, embargo, umi);
    }

	/**
	 * Verify that all the document information is correct. This will be
	 * accessed from two places, the document info page as well as the
	 * confirmation page.
	 * 
	 * @param title
	 *            The document title.
	 * @param degreeMonth
	 *            The graduation month.
	 * @param degreeYear
	 *            The graduation year.
	 * @param docType
	 *            The document type.
	 * @param abstractText
	 *            The abstract text
	 * @param keywords
	 *            The keywords
	 * @param committee
	 *            A list of maps for each committee member
	 * @param chairEmail
	 *            The committee chair's email.
	 * @param embargo
	 *            The embargo selection.
	 * @return True if there are no errors found, otherwise false.
	 */
	public static boolean verify(String title, String degreeMonth, String degreeYear, String docType, String abstractText, String keywords, List<Map<String,String>> committee, String chairEmail, String embargo) {
		
		int numberOfErrorsBefore = validation.errors().size();
		
		
		if(null == title || title.trim().length() == 0) {
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
         
         validateCommitteeMembers(committee);
         
         if(null == chairEmail || chairEmail.trim().length() == 0) {
             validation.addError("chairEmail", "Please enter an email address for the committee chair");
         } else {
				try {
					new InternetAddress(chairEmail).validate();
				} catch (AddressException ae) {
					validation.addError("chairEmail","The committee chair email address you provided is invalid.");
				}
         }
         
         if(null == embargo) {
             validation.addError("embargo", "Please choose an embargo option");
         }
         
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
    protected static boolean isValidDegreeMonth(int degreeMonth) {
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
    protected static boolean isValidDegreeYear(String degreeYear) {
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
     * @param docType The document type.
     * @return True if the docType is a valid document type.
     */
    protected static boolean isValidDocType(String docType) {
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
    protected static List<String> getValidDocumentTypes(Submission sub) {
        List<DocumentType> validTypes = settingRepo.findAllDocumentTypes(settingRepo.findDegreeByName(sub.getDegree()).getLevel());
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
    		if ((firstName != null && firstName.trim().length() > 0) ^ (lastName != null && lastName.trim().length() > 0)) {
    			validation.addError("member"+i,"Please provide both a first and last name for all committee members.");
    		}
    		
    		if (firstName != null && firstName.trim().length() > 0)
    			atLeastOneMember = true;
    		
    		String chairFlag = member.get("chairFlag");
    		if (chairFlag != null && chairFlag.trim().length() > 0)
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
    	while (params.get("committeeLastName"+i) != null) {
    		
    		String firstName = params.get("committeeFirstName"+i);
    		String middleName = params.get("committeeMiddleName"+i);
    		String lastName = params.get("committeeLastName"+i);
    		String chairFlag = params.get("committeeChairFlag"+i);
    		
    		Map<String,String> member = new HashMap<String,String>();
    		member.put("firstName",firstName);
    		member.put("middleName",middleName);
    		member.put("lastName",lastName);
    		member.put("chairFlag",chairFlag);
    		
    		committee.add(member);
    		i++;
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
    		
    		// Make sure that we have a first & last name.
    		if (firstName == null || firstName.trim().length() == 0)
    			continue;
    		if (lastName == null || lastName.trim().length() == 0)
    			continue;
    		
    		CommitteeMember newMember = sub.addCommitteeMember(firstName, lastName, middleName, chair);
    		newMember.setDisplayOrder(i);
    		newMember.save();
    	}
    	
    	return true;
    }
	
}
