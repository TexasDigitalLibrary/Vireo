package controllers.submit;

import static org.tdl.vireo.constant.AppConfig.GRANTOR;
import static org.tdl.vireo.constant.AppConfig.SUBMIT_INSTRUCTIONS;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;

import play.Logger;
import controllers.Security;

/**
 * This is the fifth, and last step, of the submission process. We allow
 * students to review all their information and make one final confirmation
 * before completing the submission.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author <a href="bill-ingram.com">Bill Ingram</a>
 * @author Dan Galewsky
 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
 */
public class Confirm extends AbstractSubmitStep {
	
	/**
	 * Confirm the submission has passed the verification from all the previous
	 * steps. Then allow the user to review their information before confirming
	 * the submission.
	 * 
	 * @param subId
	 *            The submission's id.
	 */
	@Security(RoleType.STUDENT)
	public static void confirm(Long subId) {		

		// Locate the submission 
		Submission sub = getSubmission();
		Person submitter = context.getPerson();

		if (!PersonalInfo.verify(sub))
			validation.addError("personalInfo", "There are errors on this page to correct.");

		if (!License.verify(sub))
			validation.addError("license","There are errors on this page to correct.");

		if (!DocumentInfo.verify(sub))
			validation.addError("documentInfo", "There are errors on this page to correct.");

		if (!FileUpload.verify(sub))
			validation.addError("fileUpload", "There are errors on this page to correct.");

		flash.put("from-step","confirm");


		if (params.get("submit_confirm") != null && !validation.hasErrors()) {

			try {
				context.turnOffAuthorization();
				
				if (sub.getState() == stateManager.getInitialState()) {
					// Only do these things if this is the first submission.
					
					// Generate an committee hash
					generateCommitteEmailHash(sub);
					
					// Clear the approval dates
					sub.setCommitteeApprovalDate(null);
					sub.setCommitteeEmbargoApprovalDate(null);
					
					// Set the submission date
					sub.setSubmissionDate(new Date());
				}
				
				// Transition to the next state
				State prevState = sub.getState();
				State nextState = prevState.getTransitions(sub).get(0);
				// This will trigger emails being sent out if they are configured in email workflow rules
				sub.setState(nextState);
				
				sub.save();

				Logger.info("%s (%d: %s) has completed %s submission #%d.",
						submitter.getFormattedName(NameFormat.FIRST_LAST), 
						submitter.getId(), 
						submitter.getEmail(),
						prevState.getDisplayName(),
						sub.getId());
			} finally {
				context.restoreAuthorization();

			}

			complete(subId);
		}


		List<ActionLog> logs = subRepo.findActionLog(sub);
		Attachment primaryDocument = sub.getPrimaryDocument();
		List<Attachment> additionalDocuments = sub.getAttachmentsByType(AttachmentType.SUPPLEMENTAL,AttachmentType.SOURCE,AttachmentType.ADMINISTRATIVE);
		String grantor = settingRepo.getConfigValue(GRANTOR,"Unknown Institution");

		boolean showEditLinks = true;

		renderTemplate("Submit/confirm.html",subId, sub, grantor, showEditLinks, submitter, logs, 
				primaryDocument,
				additionalDocuments);		
	}
	
	/**
	 * After completing a submission, show a set of instructions on what the student should do next.
	 * 
	 * @param subid The id of the completed submission.
	 */
	public static void complete(Long subId) {
		// Get the post submission instructions for display
		String instructions = settingRepo.getConfigValue(SUBMIT_INSTRUCTIONS);
		instructions = text2html(instructions);

		renderTemplate("Submit/complete.html", instructions);
	}

	/**
	 * Generate security hash for this submission. This method will make sure
	 * that it is not being used by another submission before assigning a hash.
	 * 
	 * @param sub
	 *            The submission to assign the hash too.
	 * @return The new hash.
	 */
	protected static String generateCommitteEmailHash(Submission sub) {

		String hash = null;

		do {		
			byte[] randomBytes = new byte[8];
			new Random().nextBytes(randomBytes);
			String proposed = Base64.encodeBase64URLSafeString(randomBytes);
			proposed = proposed.replaceAll("[^A-Za-z0-9]","");
						
			// Check if the hash already exists
			if (subRepo.findSubmissionByEmailHash(proposed) == null) {
				// We're done, otherwise keep looping.
				hash = proposed;
			}
		} while (hash == null);

		sub.setCommitteeEmailHash(hash);
		return hash;
	}
}
