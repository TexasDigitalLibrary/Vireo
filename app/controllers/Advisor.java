package controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import play.Play;
import play.mvc.With;

@With(Authentication.class)
public class Advisor extends AbstractVireoController {
	
	public static final String AFFILIATION_CONFIG = "advisor.affiliation.restrict";

	
	/**
	 * Display the faculty advisor's review page where the user can approve or
	 * reject a submission. Theoretically we can identify the advisor based upon
	 * their use of the security token. If they have that token then they can
	 * access the submission. We still require that they authenticate first so
	 * we atleast have a user account associated with them.
	 * 
	 * @param token
	 *            The advisor's sort-of-security token.
	 */
	@Security(RoleType.NONE)
	public static void review(String token) {
		
		// Security check:
		// 1) Managers can access this page, with no other checks
		// 2) Non-managers need to have a specific affiliation before they can
		//    access this page. (Optional)
		//
		// If there is no affiliation check, then basically anyone who has
		// access to the security token can access this page.
		
		Person person = context.getPerson();
		java.lang.System.out.println("Person: "+person.getFormattedName(NameFormat.FIRST_LAST));
		
		if (context.isManager()) {
			String affiliationConfig = Play.configuration.getProperty(AFFILIATION_CONFIG, "");
			if ("".equals(affiliationConfig)) {
				// We are configured to require affiliations before accessing this page.
				for (String affiliation : affiliationConfig.split(";")) {
					if (person.getAffiliations().contains(affiliation.trim())) {
						unauthorized("Only those with an affiliation of "+affiliationConfig+" or managers are able to access this advisor review page.");
					}
				}
			} 
		}
		
		notFoundIfNull(token);
		Submission sub = subRepo.findSubmissionByEmailHash(token);
		notFoundIfNull(sub);

		boolean inputRecieved = false;
		try {
			context.turnOffAuthorization();
			Date agreementDate = new Date();
			if (params.get("advisorMessage") != null && params.get("advisorMessage").trim().length() > 0) {
				sub.logAction("Advisor comments : '" +	params.get("advisorMessage") + "'").save();
				inputRecieved = true;
			}

			if ("approve".equals(params.get("embargoApproval"))) {
				sub.setCommitteeEmbargoApprovalDate(agreementDate);
				inputRecieved = true;
			}

			if ("approve".equals(params.get("committeeApproval"))) {
				sub.setCommitteeApprovalDate(agreementDate);
				inputRecieved = true;
			}
			
			sub.save();
		} finally {
			context.restoreAuthorization();
		}

		String grantor = settingRepo.getConfig(Configuration.GRANTOR,"Unknown Institution");
		List<EmbargoType> allEmbargos = settingRepo.findAllEmbargoTypes();
		Person submitter = sub.getSubmitter();
		List<ActionLog> logs = subRepo.findActionLog(sub);
		Attachment primaryDocument = sub.getPrimaryDocument();
		List<Attachment> supplementaryDocuments = sub.getSupplementalDocuments();
		List<Attachment> feedbackDocuments = sub.getAttachmentsByType(AttachmentType.FEEDBACK);

		renderTemplate("Advisor/view.html", token, sub, submitter, logs, primaryDocument, supplementaryDocuments, feedbackDocuments, grantor,allEmbargos,inputRecieved);

	}
	
	
	/**
	 * Helper method to view an attachment specifically for advisors to handle their permissions.
	 * 
	 * @param token
	 *            The token identifying the submission
	 * @param attachmentId
	 *            The attachment id.
	 * @param name
	 * 	          The name of the attachment. (not used)
	 *            
	 */
	public static void viewAttachment(String token, Long attachmentId, String name) {

		notFoundIfNull(token);
		notFoundIfNull(attachmentId);

		Submission sub = subRepo.findSubmissionByEmailHash(token);    	
		Attachment attachment = subRepo.findAttachment(attachmentId);

		notFoundIfNull(sub);
		notFoundIfNull(attachment);

		response.setContentTypeIfNotSet(attachment.getMimeType());    	
		try {
			renderBinary( new FileInputStream(attachment.getFile()), attachment.getFile().length());
		} catch (FileNotFoundException ex) {
			error("File not found");
		}
	}
	
}
