package controllers;

import static org.tdl.vireo.constant.AppConfig.GRANTOR;
import static org.tdl.vireo.constant.FieldConfig.ADMINISTRATIVE_ATTACHMENT;
import static org.tdl.vireo.constant.FieldConfig.PRIMARY_ATTACHMENT;
import static org.tdl.vireo.constant.FieldConfig.SOURCE_ATTACHMENT;
import static org.tdl.vireo.constant.FieldConfig.SUPPLEMENTAL_ATTACHMENT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.constant.AppConfig;
import org.tdl.vireo.constant.FieldConfig;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;

import play.Logger;
import play.libs.MimeTypes;
import play.mvc.With;
import controllers.submit.PersonalInfo;

/**
 * THIS CONTROLLER IS BEING REFACTORED. 
 * 
 * Please don't touch right now.
 */
@With(Authentication.class)
public class Student extends AbstractVireoController {

	/**
	 * Retrieve the submission object and make sure it's in a proper state. This
	 * is slightly different that the version that is in the submission steps
	 * because it does not restrict the submission to be in an initial state.
	 * 
	 * @return The submission object.
	 */
	protected static Submission getSubmission() {
		// We require an sub id.
		Long subId = params.get("subId", Long.class);
		if (subId == null) {
		    error("Did not receive the expected submission id.");
		} 
		
		// And the submission must exist.
		Submission sub = subRepo.findSubmission(subId);
		if (sub == null) {
		    error("Unable to find the submission #"+subId);
		}
		
		// Check that we are the owner of the submission.
		Person submitter = context.getPerson();
		if (sub.getSubmitter() != submitter)
		    unauthorized();
		
		return sub;
	}



	/**
	 * This is a general swiss army of a controller method. This is where the
	 * student should go to start anything. If they come here and don't have
	 * anything then they will be redirected to start a new submission. However
	 * if they have other submissions then they will see a list. If the
	 * configuration is not to have multiple submissions then the student will
	 * be shutteled either to their inprogress submission, or view the status of
	 * the previous submission.
	 */
	@Security(RoleType.STUDENT)
	public static void submissionList() {	

		Person submitter = context.getPerson();
		List<Submission> submissions = subRepo.findSubmission(submitter);


		boolean submissionsOpen = settingRepo.getConfigBoolean(AppConfig.SUBMISSIONS_OPEN);
		boolean allowMultiple = settingRepo.getConfigBoolean(AppConfig.ALLOW_MULTIPLE_SUBMISSIONS);
		
		// Check to see there are no submissions, start a new one.
		if (submissions.size() == 0 && submissionsOpen) {
			// First time, here let's start a new sub.
			PersonalInfo.personalInfo(null);
		}
		
		
		// Check to see if we should skip the list page.
		if (submissions.size() == 1 && !allowMultiple && !submissions.get(0).getState().isArchived()) {
			// The only condition when a user should skip the list page is, when allow multiple 
			// submissions is turned off AND they have one *active* submission.
			Submission sub = submissions.get(0);
		
			if (submissionsOpen && sub.getState().isInProgress()) {
				// The one submission isn't complete yet.
				PersonalInfo.personalInfo(sub.getId());
			} else if (!sub.getState().isInProgress()){
				// Go straight to view the status page.
				submissionView(sub.getId());
			}
		}

		// Should we allow the student to start another submission.
		boolean showStartSubmissionButton = allowMultiple;
		
		// Check if we should allow the user to start another submission.
		boolean allArchived = true;
		for (Submission sub : submissions) {
			if (!sub.getState().isArchived())
				allArchived = false;
		}
		if (allArchived) {
			// If all the current submissions are archived, allow the user to submit another submission.
			showStartSubmissionButton = true;
		}
		
		if (!submissionsOpen)
			// If we're not open, then no one can start a new submission.
			showStartSubmissionButton = false;
		
		renderArgs.put("SUBMISSIONS_OPEN", settingRepo.findConfigurationByName(AppConfig.SUBMISSIONS_OPEN));
		renderArgs.put("CURRENT_SEMESTER", settingRepo.getConfigValue(AppConfig.CURRENT_SEMESTER, "current"));
		
		renderTemplate("Student/list.html",submissions, showStartSubmissionButton);
	}

	/**
	 * Student view of the submission. The form in all cases allows the student
	 * to leave comments for reviewers.
	 * 
	 * However, if the submission is in a state that allows editing by students
	 * then they may modify the documents associated with the submission, before
	 * confirming their corrections.
	 * 
	 * @param subId The submission id.
	 */
	@Security(RoleType.STUDENT)
	public static void submissionView(Long subId) {		

		// Locate the submission 
		Submission sub = subRepo.findSubmission(subId);
		
		// Check that we are the owner of the submission.
		Person submitter = context.getPerson();
		if (sub.getSubmitter() != submitter)
		    unauthorized();		
		
		Logger.info("%s (%d: %s) has viewed submission #%d.",
				submitter.getFormattedName(NameFormat.FIRST_LAST), 
				submitter.getId(), 
				submitter.getEmail(),
				sub.getId());
		
		boolean allowMultiple = settingRepo.getConfigBoolean(AppConfig.ALLOW_MULTIPLE_SUBMISSIONS);
		
		// Handle add message button. Just add the message to the submission
		if (params.get("submit_addMessage") != null) {   
			if (!params.get("studentMessage").equals(""))
				sub.logAction("Message added : '" +	params.get("studentMessage") + "'").save();
		}
		
		if (sub.getState().isEditableByStudent()) {
			// If the replace manuscript button is pressed - then delete the manuscript 
			if (params.get("replacePrimary") != null) {
				Attachment primaryDoc = sub.getPrimaryDocument();   
				if (primaryDoc != null) {
					primaryDoc.archive();
					primaryDoc.save();
					sub.save();
				}
			}
			
			// Handle the remove supplementary document button 
			if (params.get("removeAdditional") != null) {
				removeAdditional(sub);           	            	
			}
			
			if(params.get("primaryDocument",File.class) != null) 
				uploadPrimaryDocument(sub);
			
			if(params.get("additionalDocument",File.class) != null)
				uploadAdditional(sub);
			
			verify(sub);
			
			if (params.get("submit_corrections") != null && !validation.hasErrors()) {
				try {
					context.turnOffAuthorization();
					State nextState = sub.getState().getTransitions(sub).get(0);
					sub.setState(nextState);
					sub.save();

					correctionsComplete(sub.getId());
					
				} finally {
					context.restoreAuthorization();

				}
			}
		}

		String grantor = settingRepo.getConfigValue(AppConfig.GRANTOR,"Unknown Institution");
		List<Submission> allSubmissions = subRepo.findSubmission(submitter);
		List<ActionLog> logs = subRepo.findActionLog(sub);
		Attachment primaryDocument = sub.getPrimaryDocument();
		List<Attachment> additionalDocuments = sub.getAttachmentsByType(AttachmentType.SUPPLEMENTAL,AttachmentType.SOURCE,AttachmentType.ADMINISTRATIVE);
		List<Attachment> feedbackDocuments = sub.getAttachmentsByType(AttachmentType.FEEDBACK);
		
		for(FieldConfig field : FieldConfig.values()) {
			renderArgs.put(field.name(),field );
		}
		
		List<String> attachmentTypes = new ArrayList<String>();
		for(AttachmentType type : AttachmentType.values()){
			attachmentTypes.add(type.toString());
		}

		renderTemplate("Student/view.html",subId, sub, submitter, logs, primaryDocument, additionalDocuments, feedbackDocuments, allSubmissions, grantor, allowMultiple, attachmentTypes);		
	}

	/**
	 * Splash screen after a student has submitted corrections.
	 * 
	 * @param subId The submission
	 */
	@Security(RoleType.STUDENT)
	public static void correctionsComplete(Long subId) {
		
		
		// Get the post corrections instructions for display
		String instructions = settingRepo.getConfigValue(AppConfig.CORRECTION_INSTRUCTIONS);
		instructions = text2html(instructions);
		
		renderTemplate("Student/complete.html",instructions);
		
	}
	
	/**
	 * Delete a given submission
	 * 
	 * @param subId
	 *            The submission to delete.
	 */
	@Security(RoleType.STUDENT)
	public static void submissionDelete(Long subId) {
		Submission sub = getSubmission();
		
		// Check that we are the owner of the submission.
		Person submitter = context.getPerson();
		if (sub.getSubmitter() != submitter)
		    unauthorized();		
		
		sub.delete();
		
		Logger.info("%s (%d: %s) has deleted submission #%d.",
				submitter.getFormattedName(NameFormat.FIRST_LAST), 
				submitter.getId(), 
				submitter.getEmail(),
				sub.getId());
		
		List<Submission> submissions = subRepo.findSubmission(submitter);
		if (submissions.size() == 0)
			// No other submissions, send you back to the index.
			Application.index();
		else
			// Go back to the list of other submissions.
			submissionList();
	}



	
	
	
	
	/**
	 * Helper method to handle uploading a primary document.
	 * 
	 * @param sub
	 *            The submission to add the attachment too.
	 */
	public static boolean uploadPrimaryDocument(Submission sub) {

		File primaryDocument = params.get("primaryDocument",File.class);
		if (primaryDocument == null)
			return false;

		String mimetype = MimeTypes.getContentType(primaryDocument.getName());
		if (!"application/pdf".equals(mimetype)) {
			validation.addError("primaryDocument", "Primary document must be a PDF file.");
			return false;
		}       	
		
	
		try {
			Attachment attachment = sub.addAttachment(primaryDocument, AttachmentType.PRIMARY);
			attachment.save();
			sub.save();
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to upload primary document");
			validation.addError("primaryDocument","Error uploading primary document.");
		
		} catch (IllegalArgumentException iae) {
			Logger.error(iae,"Unable to upload primary document");
			
			if (iae.getMessage().contains("already exists for this submission"))
				validation.addError("primaryDocument", "A file with that name already exists; please use a different name or remove the other file.");
			else
				validation.addError("primaryDocument","Error uploading primary document.");
		
		}
		return true;
	}

	/**
	 * Helper method to handle uploading supplementary files.
	 * 
	 * @param sub
	 *            The submission to add the attachment too.
	 */
	public static boolean uploadAdditional(Submission sub) {

		// If the upload supplementary button is pressed - then add the manuscript as an attachment
		File additionalDocument = params.get("additionalDocument",File.class);
		if (additionalDocument == null)
			return false;
		
		AttachmentType type = null;
		if(!params.get("attachmentType").isEmpty())
			type = AttachmentType.valueOf(params.get("attachmentType"));
		else
			return false;
		

		Attachment attachment = null;
		try {
			attachment = sub.addAttachment(additionalDocument, type);                                         
			attachment.save();
			sub.save();
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to upload additional document");
			validation.addError("additionalDocument","Error uploading additional document.");
		
		} catch (IllegalArgumentException iae) {
			Logger.error(iae,"Unable to upload additional document");
			
			if (iae.getMessage().contains("already exists for this submission"))
				validation.addError("additionalDocument", "A file with that name already exists; please use a different name or remove the other file.");
			else
				validation.addError("additionalDocument","Error uploading additional document.");
		
		}
		return true;
	}


	/**
	 * Helper method to handle removing additional files from a
	 * submission. We check that the attachments are associated with the
	 * submission, and that they are SUPPLEMENTAL, ADMINISTRATIVE or SOURCE files 
	 * to prevent deletion of other attachments.
	 * 
	 * @param sub
	 *            The submission to remove attachments from.
	 */
	public static boolean removeAdditional(Submission sub) {

		// Get values from all check boxes
		String[] idsToRemove = params.getAll("attachmentToRemove");
		
		if (idsToRemove != null) {
		
			// Iterate over all checked check boxes - removing attachments as we go
			for (String idString : idsToRemove) {
				Long id = Long.valueOf(idString);
				
				Attachment attachment = subRepo.findAttachment(id);
				
				if (attachment.getSubmission() == sub && 
						(attachment.getType() == AttachmentType.SUPPLEMENTAL ||
						attachment.getType() == AttachmentType.ADMINISTRATIVE ||
						attachment.getType() == AttachmentType.SOURCE))
					attachment.delete();
			}
		}
		
		return true;
	}
	
	
	/**
	 * Helper method to view an attachment. In many places through the
	 * submission interface there are references to view attachments. All of
	 * them point here to download the file.
	 * 
	 * @param subId
	 *            The submission id.
	 * @param attachmentId
	 *            The attachment id.
	 * @param name
	 * 			  The name of the attachment (not used)
	 */
	public static void viewAttachment(Long subId, Long attachmentId, String name) {

		if (attachmentId == null)
			error();

		Submission sub = getSubmission();
		// Check that we are the owner of the submission.
		Person submitter = context.getPerson();
		if (sub.getSubmitter() != submitter)
		    unauthorized();		
				
		Attachment attachment = subRepo.findAttachment(attachmentId);

		if (attachment == null)
			error();
		if (attachment.getSubmission() != sub)
			unauthorized();

		response.setContentTypeIfNotSet(attachment.getMimeType());
		
		// Fix problem with no-cache headers and ie8
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control","public");
		
		try {
			renderBinary(new FileInputStream(attachment.getFile()), attachment.getName(), attachment.getFile().length(), true);
		} catch (FileNotFoundException ex) {
			error("File not found");
		}
	}
	
	/**
	 * Verify that the user has supplied a primary document. This will be used
	 * from both the fileUpload form and the confirmation page.
	 * 
	 * @return True if the primary document exists, otherwise false.
	 */
	public static boolean verify(Submission sub) {
		
		int numberOfErrorsBefore = validation.errors().size();

		if (isFieldRequired(PRIMARY_ATTACHMENT) && sub.getPrimaryDocument() == null )
			validation.addError("primaryDocument", "A manuscript file must be uploaded.");

		if (isFieldRequired(SUPPLEMENTAL_ATTACHMENT) && 
				sub.getAttachmentsByType(AttachmentType.SUPPLEMENTAL).size() == 0)
			validation.addError("supplementalDocument", "At least one supplemental file is required.");
		
		if (isFieldRequired(SOURCE_ATTACHMENT) && 
				sub.getAttachmentsByType(AttachmentType.SOURCE).size() == 0)
			validation.addError("sourceDocument", "At least one source file is required.");
		
		if (isFieldRequired(ADMINISTRATIVE_ATTACHMENT) && 
				sub.getAttachmentsByType(AttachmentType.ADMINISTRATIVE).size() == 0)
			validation.addError("administrativeDocument", "At least one administrative file is required.");
		
		if (numberOfErrorsBefore == validation.errors().size()) 
			return true;
		else
			return false;
	}

}