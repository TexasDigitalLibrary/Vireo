package controllers;

import java.util.Date;
import java.util.List;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;

public class Advisor extends AbstractVireoController {


	public static void review(String token) {
		
		java.lang.System.out.println("token="+token);
		
		
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

		renderTemplate("Advisor/view.html", token, sub, submitter, logs, primaryDocument, supplementaryDocuments,grantor,allEmbargos,inputRecieved);

	}

	
	
	/**
	 * Helper method to view an attachment specifically for advisors to handle their permissions.
	 * 
	 * @param subId
	 *            The submission id.
	 * @param attachmentId
	 *            The attachment id.
	 */
	public static void viewAttachment(String token, Long attachmentId) {

		notFoundIfNull(token);
		notFoundIfNull(attachmentId);

		Submission sub = subRepo.findSubmissionByEmailHash(token);    	
		Attachment attachment = subRepo.findAttachment(attachmentId);

		notFoundIfNull(sub);
		notFoundIfNull(attachment);

		response.setContentTypeIfNotSet(attachment.getMimeType());    	
		renderBinary(attachment.getFile(),attachment.getName());
	}
	
	
	
}
