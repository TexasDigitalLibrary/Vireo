package controllers.submit;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.PersistenceException;

import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import play.Logger;
import play.libs.MimeTypes;
import controllers.Security;
import controllers.Student;

/**
 * The fourth step of the submission process where students upload their files.
 * This step is pretty straight forward, the only tricky thing is that we make
 * sure the uploaded file is a pdf. Other than that pretty much anything goes.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author <a href="bill-ingram.com">Bill Ingram</a>
 * @author Dan Galewsky
 */
public class FileUpload extends AbstractSubmitStep {

	/**
	 * The fourth step of the submission process where students upload their
	 * files. We will always take the primary and supplementary file uploads no
	 * matter what button was clicked.
	 * 
	 * @param subId
	 *            The id of the submission.
	 */
	@Security(RoleType.STUDENT)
	public static void fileUpload(Long subId) {

		// Locate the submission that this upload will be attached to
		Submission sub = getSubmission();
		
		// If the replace manuscript button is pressed - then delete the manuscript 
		if (params.get("replacePrimary") != null) {
			Attachment primaryDoc = sub.getPrimaryDocument();   
			if (primaryDoc != null) {
				primaryDoc.delete();
				sub.save();
			}
		}
		
		// Handle the remove supplementary document button 
		if (params.get("removeSupplementary") != null) {
			removeSupplementary(sub);           	            	
		}
		
		if(params.get("primaryDocument",File.class) != null)
			uploadPrimaryDocument(sub);
		
		if(params.get("supplementaryDocument",File.class) != null)
			uploadSupplementary(sub);
		
		if (!"true".equals(flash.get("nextStep")))
			verify(sub.getPrimaryDocument());

		
		// 'Save And Continue' button was clicked
		if (params.get("submit-next") != null && !validation.hasErrors()) {
			Confirm.confirm(subId);
		}


		// Initialize variables and display form
		Attachment primaryAttachment = sub.getPrimaryDocument();
		List<Attachment> supplementalAttachments = sub.getSupplementalDocuments();

		renderTemplate("Submit/fileUpload.html",subId, primaryAttachment, supplementalAttachments);
	}

	/**
	 * Verify that the user has supplied a primary document. This will be used
	 * from both the fileUpload form and the confirmation page.
	 * 
	 * @param primaryDocument
	 *            The primary document to confirm it's existence.
	 * @return True if the primary document exists, otherwise false.
	 */
	public static boolean verify(Attachment primaryDocument) {
		
		if (primaryDocument == null || primaryDocument.getId() == null) {
			validation.addError("primaryDocument", "A manuscript file must be uploaded.");
			return false;
		} else {
			return true;
		}
		
	}
	
	
	/**
	 * Internal helper method to handle uploading a primary document.
	 * 
	 * @param sub
	 *            The submission to add the attachment too.
	 */
	protected static void uploadPrimaryDocument(Submission sub) {

		File primaryDocument = params.get("primaryDocument",File.class);
		if (primaryDocument == null)
			return;

		String mimetype = MimeTypes.getContentType(primaryDocument.getName());
		if (!"application/pdf".equals(mimetype)) {
			validation.addError("primaryDocument", "Primary document must be a PDF file.");
			return;
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
			
			if (iae.getMessage().contains("allready exists for this submission"))
				validation.addError("primaryDocument", "A file with that name allready exists; please use a different name or remove the other file.");
			else
				validation.addError("primaryDocument","Error uploading primary document.");
		
		}				
	}

	/**
	 * Internal helper method to handle uploading supplementary files.
	 * 
	 * @param sub
	 *            The submission to add the attachment too.
	 */
	protected static void uploadSupplementary(Submission sub) {

		// If the upload supplementary button is pressed - then add the manuscript as an attachment
		File supplementaryDocument = params.get("supplementaryDocument",File.class);
		if (supplementaryDocument == null)
			return;

		Attachment attachment = null;
		try {
			attachment = sub.addAttachment(supplementaryDocument, AttachmentType.SUPPLEMENTAL);                                         
			attachment.save();
			sub.save();
		} catch (IOException ioe) {
			Logger.error(ioe,"Unable to upload supplementary document");
			validation.addError("supplementaryDocument","Error uploading supplementary document.");
		
		} catch (IllegalArgumentException iae) {
			Logger.error(iae,"Unable to upload supplementary document");
			
			if (iae.getMessage().contains("allready exists for this submission"))
				validation.addError("supplementaryDocument", "A file with that name allready exists; please use a different name or remove the other file.");
			else
				validation.addError("supplementaryDocument","Error uploading primary document.");
		
		}				
	}


	/**
	 * Internal helper method to handle removing supplementary files from a
	 * submission. We check that the attachments are associated with the
	 * submission, and that they are SUPPLEMENTAL files to prevent deletion of
	 * other attachments.
	 * 
	 * @param sub
	 *            The submission to remove attachments from.
	 */
	private static void removeSupplementary(Submission sub) {

		// Get values from all check boxes

		String[] idsToRemove = params.getAll("attachmentToRemove");

		// Iterate over all checked check boxes - removing attachments as we go
		for (String idString : idsToRemove) {
			Long id = Long.valueOf(idString);
			
			Attachment attachment = subRepo.findAttachment(id);
			
			if (attachment.getSubmission() == sub && attachment.getType() == AttachmentType.SUPPLEMENTAL)
				attachment.delete();
		}		
	}

}
