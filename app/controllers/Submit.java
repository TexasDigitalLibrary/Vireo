package controllers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.tdl.vireo.model.*;
import org.tdl.vireo.state.State;

import com.google.gson.Gson;

import controllers.submitSteps.PersonalInfo;

import play.Logger;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import sun.util.logging.resources.logging;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import play.mvc.Scope.Params;
import static org.tdl.vireo.model.Configuration.CURRENT_SEMESTER;
import static org.tdl.vireo.model.Configuration.SUBMISSIONS_OPEN;

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
	 * Set up values needed to display submission status at the top of each page.
	 * If submissions are closed - always redirect to the submissionStatus page. 
	 */
	
	@Before(unless="submissionStatus")
	static void beforeSubmit() {
		
		if (settingRepo.findConfigurationByName(SUBMISSIONS_OPEN) == null)
			submissionStatus();	
		
		renderArgs.put("SUBMISSIONS_OPEN", settingRepo.findConfigurationByName(SUBMISSIONS_OPEN));
		
		Configuration curSemConfig = settingRepo.findConfigurationByName(CURRENT_SEMESTER);
		String currentSemester = (curSemConfig == null ? "undefined" : curSemConfig.getValue());
			
		renderArgs.put("CURRENT_SEMESTER", currentSemester);


	}
	
	protected static Submission getSubmission() {
		Long subId = params.get("subId", Long.class);
		Submission sub = subRepo.findSubmission(subId);
		
		if (sub == null) {
		    error("Did not receive the expected submission id.");
		} 
				
		// This is an existing submission so check that we're the student here.
		Person submitter = context.getPerson();
		if (sub.getSubmitter() != submitter)
		    unauthorized();
		
		return sub;
	}


//	@Security(RoleType.STUDENT)
//	public static void license(Long subId) {
//
//        Submission sub = subRepo.findSubmission(subId);
//        if (sub == null) {
//            // something is wrong
//            error("Did not receive the expected submission id.");
//        } else {
//            Person submitter = context.getPerson();
//
//            // This is an existing submission so check that we're the student here.
//            if (sub.getSubmitter() != submitter)
//                unauthorized();
//
//            String checked = sub.getLicenseAgreementDate() != null ? "checked=checked" : "";
//
//            // Not too keen on this; seems messy
//            String licenseText = settingRepo.getConfig(Configuration.SUBMIT_LICENSE,DEFAULT_LICENSE);
//
//            // first time here?
//            if (params.get("submit_next") != null) {
//
//                if (params.get("licenseAgreement") == null) {
//                    validation.addError("laLabel","You must agree to the license agreement before continuing.");
//                } else {
//                	
//                	Date agreementDate = new Date();
//                    sub.setLicenseAgreementDate(agreementDate);
//
//                    
//                    // Check if another license has been selected been saved.
//                    for (Attachment attachment : sub.getAttachments()){
//                    	if (attachment.getType() == AttachmentType.LICENSE && attachment.getName() == "LICENSE.txt") {
//                    		// Remove the old license, and save the new one.
//                    		attachment.delete();
//                    		break;
//                    	}
//                    }
//                    
//                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm a");
//                    
//                    licenseText += "\n\n--------------------------------------------------------------------------\n";
//                    licenseText += "The license above was accepted by "+submitter.getFullName()+" on "+formatter.format(agreementDate)+"\n";
//                                        
//                    // Save the text that the student aggreed too.
//                    try {
//                    	sub.addAttachment(licenseText.getBytes(),"LICENSE.txt",AttachmentType.LICENSE).save();
//                    } catch (IOException ioe) {
//                    	throw new RuntimeException("Unable to save license aggreement.",ioe);
//                    }
//                    
//                    sub.save();
//
//                    docInfo(subId);
//                    //fileUpload(subId);
//                }
//            }
//            
//            // Format the license text for display
//            licenseText = licenseText.replaceAll("  ", "&nbsp;&nbsp;");
//            String[] paragraphs = licenseText.split("\n\\s*\n");
//            licenseText = "";
//            for (String paragraph : paragraphs) {
//            	licenseText += "<p>"+paragraph+"</p>";
//            }
//            
//            licenseText = licenseText.replaceAll("\n", "<br/>");
//            
//            render(subId,licenseText,checked);
//        }
//
//    }

//    @Security(RoleType.STUDENT)
//    public static void docInfo(Long subId) {
//
//        String title = params.get("title");
//        
//        
//        Logger.info("Title: " + title);
//        
//        
//        String degreeMonth = params.get("degreeMonth");
//        String degreeYear = params.get("degreeYear");
//        String docType = params.get("docType");
//        String abstractText = params.get("abstractText");
//        String keywords = params.get("keywords");
//        String committeeFirstName = params.get("committeeFirstName");
//        String committeeMiddleInitial = params.get("committeeMiddleInitial");
//        String committeeLastName = params.get("committeeLastName");
//        String chairFlag = params.get("committeeChairFlag");
//        String chairEmail = params.get("chairEmail");
//        String embargo = params.get("embargo");
//        
//        List<CommitteeMember> committeeMembers = parseCommitteeMembers(params);
//        
//        Submission sub = null;
//
//        if (null != subId) {
//            sub = subRepo.findSubmission(subId);
//        }
//
//        if (null == sub) {
//            // TODO: Are we going to handle this more gracefully in the future?
//            error("Did not receive the expected submission id.");
//        }
//        
//        // Validate the form and if all is ok - save in the model
//        
//        if (params.get("submit_next") != null) {
//            
//            if(null == title || title.equals("")) {
//                validation.addError("title", "Please enter a thesis title.");
//            }
//            
//            if(null == degreeMonth || degreeMonth.trim().length() == 0 || !isValidDegreeMonth(Integer.parseInt(degreeMonth))) {
//                validation.addError("degreeMonth", "Please select a degree month.");
//            }
//            
//            if(null == degreeYear || !isValidDegreeYear(degreeYear)) {
//               validation.addError("degreeYear", "Please select a degree year");
//            }
//            
//            if(null == docType || !isValidDocType(docType)) {
//                validation.addError("docType", "Please select a Document Type");
//            }
//            
//            if(null == abstractText || abstractText.trim().length() == 0) {
//                validation.addError("abstractText", "Please enter an abstract");
//            }
//            
//            if(null == keywords || keywords.trim().length() == 0) {
//                validation.addError("keywords", "Please enter at least one keyword");
//            }
//            
//            if(null == chairEmail || chairEmail.trim().length() == 0) {
//                validation.addError("chairEmail", "Please enter an email address for the committee chair");
//            }
//            
//            if(null == embargo) {
//                validation.addError("embargo", "Please choose an embargo option");
//            }
//            
//            if(!validation.hasErrors()) {
//                sub.setDocumentTitle(title);
//                sub.setGraduationMonth(Integer.parseInt(degreeMonth));
//                sub.setGraduationYear(Integer.parseInt(degreeYear));
//                sub.setDocumentType(docType);
//                sub.setDocumentAbstract(abstractText);
//                sub.setDocumentKeywords(keywords);
//                sub.setCommitteeContactEmail(chairEmail);
//                sub.setEmbargoType(settingRepo.findEmbargoType(Long.parseLong(embargo)));
//                
//                for(CommitteeMember c : committeeMembers) {
//                    sub.addCommitteeMember(c.getFirstName(), c.getLastName(), c.getMiddleName(), c.isCommitteeChair());
//                }
//                
//                sub.save();
//                
//                // Once the form has been saved - go to the fileUpload form 
//                
//                fileUpload(subId);
//            }
//        }
//        
//        // List of valid degree years for drop-down population
//        List<Integer> degreeYears = getDegreeYears();
//        renderArgs.put("degreeYears", degreeYears);
//        
//        
//        List<String> docTypes = getValidDocumentTypes(sub);
//        renderArgs.put("docTypes", docTypes);
//        
//        // List of all *active* Embargo Types
//        List<EmbargoType> embargoTypes = settingRepo.findAllActiveEmbargoTypes();
//        renderArgs.put("embargoTypes", embargoTypes);
//        
//        // List of Committee Member objects
//        renderArgs.put("committeeMembers", committeeMembers);
//        
//        // Fill in values in the form - from the database
//        
//        if (sub != null) {
//        	title = sub.getDocumentTitle();
//        	
//        	if (sub.getGraduationMonth() != null)
//        		degreeMonth =  sub.getGraduationMonth().toString();
//        	
//            if (sub.getGraduationYear() != null)
//            	degreeYear = sub.getGraduationYear().toString();
//            
//            docType = sub.getDocumentType();
//            abstractText = sub.getDocumentAbstract();
//            keywords = sub.getDocumentKeywords();
//            chairEmail = sub.getCommitteeContactEmail();
//            
//            // Get the list of committee members
//            
//            committeeMembers = sub.getCommitteeMembers();
//            
//            // List of Committee Member objects
//            renderArgs.put("committeeMembers", committeeMembers);
//            
//            Logger.info("Committee Member List: " + committeeMembers.size());
//            
//            if (sub.getEmbargoType() != null)
//            	embargo = sub.getEmbargoType().getId().toString();
//            
//            Logger.info("Embargo: <" + embargo + ">");
//        }
//        render( subId, 
//                title, 
//                degreeMonth, 
//                degreeYear, 
//                docType,
//                abstractText, 
//                keywords, 
//                committeeFirstName, 
//                committeeMiddleInitial, 
//                committeeLastName, 
//                chairFlag, 
//                chairEmail, 
//                embargo);
//    }


//	/**
//	 * File upload form    
//	 * @param subId
//	 */
//    
//	@Security(RoleType.STUDENT)
//	public static void fileUpload(Long subId) {
//
//		// Locate the submission that this upload will be attached to
//		
//        Submission sub = subRepo.findSubmission(subId);
//        
//        if (sub == null) {
//            // something is wrong
//            error("Did not receive the expected submission id.");
//        } else {
//            Person submitter = context.getPerson();
//
//            // This is an existing submission so check that we're the student or administrator here.
//            
//            if (sub.getSubmitter() != submitter)
//                unauthorized();
//
//            // If the upload manuscript button is pressed - then add the manuscript as an attachment
//            
//            if (params.get("uploadPrimary") != null) {
//            	uploadPrimaryDocument(sub);
//            }
//
//            // If the replace manuscript button is pressed - then delete the manuscript 
//            
//            if (params.get("replacePrimary") != null) {
//            	Logger.info("Replace/Delete Manuscript");            	
//            	Attachment primaryDoc = sub.getPrimaryDocument();   
//            	if (primaryDoc != null) {
//            		primaryDoc.delete();
//            		sub.save();
//            	}
//            }
//
//            // If the upload supplementary button is pressed - then add the manuscript as an attachment
//            
//            if (params.get("uploadSupplementary") != null) {
//            	uploadSupplementary(sub);
//            }
//
//            // 'Save And Continue' button was clicked
//            
//            if (params.get("submit-next") != null) {
//
//            	// Go see if we have a document we can upload
//            	
//                if(params.get("primaryDocument",File.class) != null)
//                	uploadPrimaryDocument(sub);
//            	
//                if (sub.getPrimaryDocument() == null)
//                    validation.addError("primaryDocument", "A manuscript file must be uploaded.");
//               
//                // For now - print the names of the attachments in the submission
//                
//                // TODO - do we need to upload supplemental attachments here?
//                
//        		for (Attachment attachment : sub.getAttachments()) {
//        			Logger.info("Attachment for Submission: " + attachment.getName());
//        		}
//        		 		       		
//                // Finally, if all is well, we can move on
//                
//                if (!validation.hasErrors())
//                    confirmAndSubmit(subId);
//            }
//            
//            // Handle the remove supplementary document button 
//            
//            if (params.get("removeSupplementary") != null) {
//            	removeSupplementary(sub);           	            	
//            }
//
//            // Initialize variables and display form
//            
//            Attachment primaryAttachment = sub.getPrimaryDocument();
//            List<Attachment> supplementalAttachments = sub.getSupplementalDocuments();
//
//            render(subId, primaryAttachment, supplementalAttachments);
//        }
//	}
	
//	// Private method to upload a primary document.
//	
	private static void uploadPrimaryDocument(Submission sub) {
		
        File primaryDocument = params.get("primaryDocument",File.class);

        if (primaryDocument == null)
            Logger.info("Doc is null");
        else
            Logger.info("Doc: " + primaryDocument.getName());

        if (primaryDocument != null) {
        	if (!primaryDocument.getName().toLowerCase().endsWith(".pdf")) {
        		validation.addError("primaryDocument", "Primary document must be a PDF file.");
        		return;
        	}       	
        }
        try {
            sub.addAttachment(primaryDocument, AttachmentType.PRIMARY);
            sub.save();
        } catch (IOException e) {
            validation.addError("primaryDocument", "Error uploading primary document.");
        } catch (IllegalArgumentException e) {
            validation.addError("primaryDocument","Error uploading primary document.");
        }		
	}

	// Private method to upload a supplementary document.

	private static void uploadSupplementary(Submission sub) {
		
        // If the upload supplementary button is pressed - then add the manuscript as an attachment
        
        if (params.get("uploadSupplementary") != null) {

            File supplementaryDocument = params.get("supplementaryDocument",File.class);

            if (supplementaryDocument == null)
                Logger.info("Doc is null");
            else
                Logger.info("Doc: " + supplementaryDocument.getClass().getName());

            try {
            	Attachment thisAttachment = sub.addAttachment(supplementaryDocument, AttachmentType.SUPPLEMENTAL);                                         
                thisAttachment.save();
            	sub.save();
            } catch (IOException e) {
                validation.addError("supplementaryDocument","Error uploading supplementary document.");
            } catch (IllegalArgumentException e) {
                validation.addError("supplementaryDocument","Error uploading supplementary document.");
            }
        }				
	}
	
	
	/**
	 * Common method to remove supplementary files from a submission (based on checkboxes in the form)
	 * @param sub
	 */
	private static void removeSupplementary(Submission sub) {
		
    	// Get values from all check boxes
    	
    	String[] idsToRemove = params.getAll("attachmentToRemove");
    	
    	// Iterate over all checked check boxes - removing attachments as we go
    	
    	if (idsToRemove != null)
        	for (String id : idsToRemove ) {
        		
        		// Iterate over the list of supplemental documents and delete the indicated docs
        		List<Attachment> supList = sub.getSupplementalDocuments();
        		
        		for (Attachment a : supList) {
        			
        			Logger.info("Comparing " + a.getId() + " " + new Long(id));
        			
        			if (a.getId().equals(new Long(id))) {
        				
        				Logger.info("Deleteing attachment " + a.getId());
        				
        				a.delete();
        			}
        		}
        		
        	}            		
	}
	
	
	
//	// Handle the Confirm and Submit form
//	
//	@Security(RoleType.STUDENT)
//	public static void confirmAndSubmit(Long subId) {		
//		
//		// Locate the submission 
//			
//        Submission sub = subRepo.findSubmission(subId);
//        Person submitter = context.getPerson();
//        List<ActionLog> actionLogList = subRepo.findActionLog(sub);
//        
//        if (sub == null) {
//            // something is wrong
//            error("Did not receive the expected submission id.");
//        } else {
//
//            // This is an existing submission so check that we're the student or administrator here.
//            
//            if (sub.getSubmitter() != submitter)
//                unauthorized();		
//        }
//        
//        List<Attachment> supplementalAttachments = sub.getSupplementalDocuments();
//
//		render(subId, sub, submitter, actionLogList, supplementalAttachments);		
//	}
	
//	
//	/**
//	 * Submit the ETD
//	 * @param subId
//	 */
//	@Security(RoleType.STUDENT)
//	public static void submitETD(Long subId) {		
//        Submission sub = subRepo.findSubmission(subId);
//    
//        State currentState = sub.getState();
//        List<State> stateList = currentState.getTransitions(sub);
//        
//        Logger.info("Next State: " + stateList.get(0).getDisplayName());
//        
//        sub.setState(stateList.get(0));
//        sub.save();
//        review(subId);             
//	}	
	
	/**
	 * Submission Status
	 */
	
	@Security(RoleType.STUDENT)
	public static void submissionStatus() {	
		
        // Check to see if they have any active submissions
		
        Person submitter = context.getPerson();
        List<Submission> submissionList = subRepo.findSubmission(submitter);

        
        Configuration so = settingRepo.findConfigurationByName(SUBMISSIONS_OPEN);
        
        if (so != null) 
        	Logger.info("SubmissionStatus " + settingRepo.findConfigurationByName(SUBMISSIONS_OPEN).getValue());
        else
        	Logger.info("SubmissionStatus Null");                      
        
        if(submissionList.size() > 0 || settingRepo.findConfigurationByName(SUBMISSIONS_OPEN) == null) {
        	
        	//TODO -- This could be refactored - this same code is in the @Before method above
        	
    		Configuration curSemConfig = settingRepo.findConfigurationByName(CURRENT_SEMESTER);
    		String currentSemester = (curSemConfig == null ? "undefined" : curSemConfig.getValue());
    			
    		renderArgs.put("CURRENT_SEMESTER", currentSemester);
    		renderArgs.put("SUBMISSIONS_OPEN", settingRepo.findConfigurationByName(SUBMISSIONS_OPEN));
            render(submissionList);
        } else{
        	PersonalInfo.personalInfo(null);
        }
	}
	
	// Handle the Student View form 
	
	@Security(RoleType.STUDENT)
	public static void studentView(Long subId) {		
		
		// Locate the submission 
			
        Submission sub = subRepo.findSubmission(subId);
        Person submitter = context.getPerson();
        
        if (sub == null) {
            // something is wrong
            error("Did not receive the expected submission id.");
        } else {        	      	

            // This is an existing submission so check that we're the student or administrator here.
            
            if (sub.getSubmitter() != submitter)
                unauthorized();		

            // If the upload manuscript button is pressed - then add the manuscript as an attachment
            
            if (params.get("uploadPrimary") != null) {
            	Logger.info("Student View Upload " + sub.toString());
            	uploadPrimaryDocument(sub);
            }
            
            // If the upload supplementary file button is pressed - then add the supplementary doc as an attachment

            if (params.get("uploadSupplementary") != null) {
            	Logger.info("Student View Upload " + sub.toString());
            	uploadSupplementary(sub);
            }            
            
            // If the replace manuscript button is pressed - then delete the manuscript 
            
            if (params.get("replacePrimary") != null) {
            	Logger.info("Replace/Delete Manuscript");            	
            	Attachment primaryDoc = sub.getPrimaryDocument();   
            	if (primaryDoc != null) {
            		primaryDoc.delete();
            		sub.save();
            	}
            }
            
            // Remove indicated supplementary file
            
            if (params.get("removeSupplementary") != null) {
            	removeSupplementary(sub);
            }
            
            // Handle add message button. Just add the message to the submission
            
            if (params.get("addmsg") != null) {   
            	if (!params.get("studentMessage").equals(""))
            			sub.logAction("Message added : '" +	params.get("studentMessage") + "'").save();
            }
        }
        
        List<ActionLog> actionLogList = subRepo.findActionLog(sub);

        Attachment primaryAttachment = sub.getPrimaryDocument();
        List<Attachment> supplementalAttachments = sub.getSupplementalDocuments();
        
		render(subId, sub, submitter, actionLogList, primaryAttachment, supplementalAttachments);		
	}
	
	/**
	 * Final page of the submission process
	 * @param subId
	 */
	@Security(RoleType.STUDENT)
	public static void review(Long subId) {
		render(subId);
	}

	/**
	 * Delete a given submission
	 * @param subId
	 */
	@Security(RoleType.STUDENT)
	public static void deleteSubmission(Long subId) {
        Submission sub = subRepo.findSubmission(subId);
        sub.delete();
        submissionStatus();
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


//    /**
//     * @param degreeMonth The month of the degree
//     * @return True if the name is a valid degree month.
//     */
//    private static boolean isValidDegreeMonth(int degreeMonth) {
//        for (GraduationMonth month : settingRepo.findAllGraduationMonths()) {
//            if (degreeMonth == month.getMonth()) {
//                return true;
//            }
//        }
//        return false;
//    }
//    
//    /**
//     * @param degreeYear The year of the degree
//     * @return True if the name is a valid degree year.
//     */
//    private static boolean isValidDegreeYear(String degreeYear) {
//        int year = Integer.parseInt(degreeYear);
//
//        for (Integer y : getDegreeYears()) {
//            if (year == y) {
//                return true;
//            }
//        }
//        
//        return false;
//    }
//    
//    /**
//     * 
//     * @param docType
//     * @return 
//     */
//    private static boolean isValidDocType(String docType) {
//        List<DocumentType> docTypes = settingRepo.findAllDocumentTypes();
//        
//        for(DocumentType type : docTypes) {
//            if(docType.equals(type.getName())) {
//                return true;
//            }
//        }
//        
//        return false;
//    }
//    
//    /**
//     * For now, this method returns the current valid years based on 
//     * the subsequent two years.  This will possible move into a 
//     * configuration setting eventually.
//     * 
//     * @return list of current valid degree years
//     */
//    private static List<Integer> getDegreeYears() {
//        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
//        
//        List<Integer> validYears = new ArrayList<Integer>();
//        validYears.add(currentYear - 2);
//        validYears.add(currentYear - 1);
//        validYears.add(currentYear);
//        
//        return validYears;
//    }
//    
//    /**
//     * Returns a list of valid Document Types 
//     * 
//     * @return list of valid document types for this submission's degree
//     * 
//     * TODO: Since this will be applicable on the admin side of things, move this to 
//     *       a SubmissionService along with related concerns.
//     */
//    private static List<String> getValidDocumentTypes(Submission sub) {
//        List<DocumentType> validTypes = settingRepo.findAllDocumentTypes(settingRepo.findDegreeByName(sub.getDegree()).getLevel());
//        List<String> typeNames = new ArrayList<String>();
//        
//        // Gather Document Type names, since we are soft-linking these to Submission
//        for(DocumentType type : validTypes) {
//            typeNames.add(type.getName());
//        }
//        
//        return typeNames;
//    }
//    
//    // Return string representation of a month
//    
//    public static String getMonth(int month) {
//        return new DateFormatSymbols().getMonths()[month];
//    }
//
//    
//    /**
//     * Returns a list of all committee member metadata sent in request
//     * 
//     * @param params the full parameter set of the request
//     * @return a list of CommitteeMember skeleton records for use in the view
//     *         or conversion to database-backed CommitteeMembers in the database
//     */
//    private static List<CommitteeMember> parseCommitteeMembers(Params params) {
//        List<CommitteeMember> committeeMembers = new ArrayList<CommitteeMember>();
//        Map<String, String[]> allParams = params.all();
//        int added = 0;
//
//        String first;
//        String last;
//        String middle;
//        
//        for (String paramName : allParams.keySet()) {
//            if (paramName.length() > 9 && paramName.substring(0, 9).equals("committee")) {
//
//                // Grab index of found committee member
//                String index = paramName.substring(paramName.length() - 1);
//
//                first = params.get("committeeFirsttName" + index);
//                last = params.get("committeeLastName" + index);
//                middle = params.get("committeeMiddleName" + index);
//                
//                // Don't add a member more than once
//                if(null != index && Integer.parseInt(index) <= added) {
//                    continue;
//                }
//                
//                // Skip empty entries
//                if((null == last || last.equals("")) &&
//                   (null == first || first.equals("")) &&
//                   (null == middle || middle.equals("")) &&
//                   (null == params.get("committeeChairFlag" + index))) {
//                    added++;
//                    continue;
//                }
//
//                // Populate a Committee Member skeleton object and add to list
//                CommitteeMember member = new CommitteeMemberImpl();
//                member.setLastName(params.get("committeeLastName" + index));
//                member.setFirstName(params.get("committeeFirstName" + index));
//                member.setMiddleName(params.get("committeeMiddleName" + index));
//                member.setCommitteeChair(null == params.get("committeeChairFlag" + index) ? false : true);
//                member.setDisplayOrder(added + 2);
//                
//                committeeMembers.add(member);
//                added++;
//                Logger.info(String.valueOf(added));
//            }
//        }
//        return committeeMembers;
//    }
//    
    /**
     * Get document name from a submission
     * @param sub
     * @return
     */
    public static String getDocumentName(Submission sub){
		Attachment primaryDocument = sub.getPrimaryDocument();            
		String primaryDocumentName = "";	 

		if (primaryDocument != null) {
			primaryDocumentName = primaryDocument.getName();
		}   
		
		Logger.info("Get Document Name: " + primaryDocumentName );
		return primaryDocumentName;    	
    }    
    
    /**
     * View the document of an attachment
     * @param attachment
     */
    
    public static void viewPrimaryDocument(Long subId) {
    	
    	Submission sub = subRepo.findSubmission(subId);
    	
    	if (sub.getPrimaryDocument() == null)
    		submissionStatus();
    	
    	Logger.info("In View Primary Document " + sub.getPrimaryDocument().getName() );
    	Attachment attachment = sub.getPrimaryDocument();
    	response.setContentTypeIfNotSet(attachment.getMimeType());  	
    	
    	try {
    		renderBinary( new FileInputStream(attachment.getFile()), attachment.getFile().length());
    	} catch (FileNotFoundException ex) {
    		error("File not found");
    	}
    }
    
    public static void viewAttachment(Long subId, Long attachmentId) {
    	
    	if (attachmentId == null)
    		error();
    	
    	Submission sub = getSubmission();    	
    	Attachment attachment = subRepo.findAttachment(attachmentId);
    	
    	if (attachment == null)
    		error();
    	if (attachment.getSubmission() != sub)
    		unauthorized();
    	
    	response.setContentTypeIfNotSet(attachment.getMimeType());    	
    	renderBinary(attachment.getFile(),attachment.getName());
    	
    	
    }
    
    
//    protected final static String DEFAULT_LICENSE = 
//    		"I grant the Texas Digital Library (hereafter called \"TDL\"), my home institution (hereafter called \"Institution\"), and my academic department (hereafter called \"Department\") the non-exclusive rights to copy, display, perform, distribute and publish the content I submit to this repository (hereafter called \"Work\") and to make the Work available in any format in perpetuity as part of a TDL, Institution or Department repository communication or distribution effort.\n" +
//			"\n" +
//			"I understand that once the Work is submitted, a bibliographic citation to the Work can remain visible in perpetuity, even if the Work is updated or removed.\n" +
//			"\n" +
//			"I understand that the Work's copyright owner(s) will continue to own copyright outside these non-exclusive granted rights.\n" +
//			"\n" +
//			"I warrant that:\n" +
//			"\n" +
//			"    1) I am the copyright owner of the Work, or\n" +
//			"    2) I am one of the copyright owners and have permission from the other owners to submit the Work, or\n" +
//			"    3) My Institution or Department is the copyright owner and I have permission to submit the Work, or\n" +
//			"    4) Another party is the copyright owner and I have permission to submit the Work.\n" +
//			"\n" +
//			"Based on this, I further warrant to my knowledge:\n" +
//			"\n" +
//			"    1) The Work does not infringe any copyright, patent, or trade secrets of any third party,\n" +
//			"    2) The Work does not contain any libelous matter, nor invade the privacy of any person or third party, and\n" +
//			"    3) That no right in the Work has been sold, mortgaged, or otherwise disposed of, and is free from all claims.\n" +
//			"\n" +
//			"I agree to hold TDL, Institution, Department, and their agents harmless for any liability arising from any breach of the above warranties or any claim of intellectual property infringement arising from the exercise of these non-exclusive granted rights.\n"+
//			"\n";

}