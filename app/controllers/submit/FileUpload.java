package controllers.submit;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tdl.vireo.constant.FieldConfig;

import static org.tdl.vireo.constant.AppConfig.SUBMIT_UPLOAD_FILES_STICKIES;
import static org.tdl.vireo.constant.FieldConfig.*;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import au.com.bytecode.opencsv.CSVReader;

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
		
		// Upload or replace the primary document.
		if (isFieldEnabled(PRIMARY_ATTACHMENT)) {
			if (params.get("replacePrimary") != null) {
				Attachment primaryDoc = sub.getPrimaryDocument();   
				if (primaryDoc != null) {
					primaryDoc.delete();
					sub.save();
				}
			}
			
			if(params.get("primaryDocument",File.class) != null)
				Student.uploadPrimaryDocument(sub);
		}
		
		// Manage supplemental files
		if (isFieldEnabled(SUPPLEMENTARY_ATTACHMENT)) {
			if (params.get("removeSupplementary") != null)
				Student.removeSupplementary(sub);           	            	
			if(params.get("supplementaryDocument",File.class) != null)
				Student.uploadSupplementary(sub);
		}
		
		// Verify the form if we are submitting or if jumping from the confirm step.
		if ("fileUpload".equals(params.get("step")) ||
			"confirm".equals(flash.get("from-step"))) {
			verify(sub);
		}

		
		// 'Save And Continue' button was clicked
		if (params.get("submit_next") != null && !validation.hasErrors()) {
			Confirm.confirm(subId);
		}


		// Initialize variables and display form
		Attachment primaryAttachment = sub.getPrimaryDocument();
		List<Attachment> supplementalAttachments = sub.getSupplementalDocuments();

		List<String> stickies = new ArrayList<String>();
		String stickiesRaw = settingRepo.getConfigValue(SUBMIT_UPLOAD_FILES_STICKIES);
		if (stickiesRaw != null && !"null".equals(stickiesRaw)) {
			try {
				CSVReader reader = new CSVReader(new StringReader(stickiesRaw));
				stickies = Arrays.asList(reader.readNext());
				reader.close();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}
		
		renderTemplate("Submit/fileUpload.html",subId, primaryAttachment, supplementalAttachments, stickies);
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

		if (isFieldRequired(SUPPLEMENTARY_ATTACHMENT) && sub.getAttachmentsByType(AttachmentType.SUPPLEMENTAL).size() == 0)
			validation.addError("supplementaryDocument", "At least one supplementary file is required.");

		if (numberOfErrorsBefore == validation.errors().size()) 
			return true;
		else
			return false;
	}


}
