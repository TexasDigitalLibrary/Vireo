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

import controllers.submit.PersonalInfo;

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
 * THIS CONTROLLER IS BEING REFACTORED. 
 * 
 * Please don't touch right now.
 */


@With(Authentication.class)
public class Student extends AbstractVireoController {
	
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

}