package controllers.submit;

import java.io.File;
import java.util.List;

import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

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
			Student.removeSupplementary(sub);           	            	
		}
		
		if(params.get("primaryDocument",File.class) != null)
			Student.uploadPrimaryDocument(sub);
		
		if(params.get("supplementaryDocument",File.class) != null)
			Student.uploadSupplementary(sub);
		
		if (!"true".equals(flash.get("nextStep")))
			verify(sub.getPrimaryDocument());

		
		// 'Save And Continue' button was clicked
		if (params.get("submit_next") != null && !validation.hasErrors()) {
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
	

}
