package org.tdl.vireo.services;

import java.util.ArrayList;
import java.util.List;

import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;

import play.modules.spring.Spring;

/**
 * A helper class to return an email by recipient type
 *  
 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 */
public class EmailByRecipientType {

	public static List<String> getRecipients(Submission submission, RecipientType recipientType, EmailWorkflowRule rule) {
		
		List<String> recipients = new ArrayList<String>();
		SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
		
		if(recipientType == null) return recipients;
		
		switch (recipientType) {
		case Student:
			if(submission.getSubmitter().getCurrentEmailAddress() != null && submission.getSubmitter().getCurrentEmailAddress().length() > 0) {
				recipients.add(submission.getSubmitter().getCurrentEmailAddress());
			}
			break;
		case Advisor:
			if(submission.getCommitteeContactEmail() != null && submission.getCommitteeContactEmail().length() > 0) {
				recipients.add(submission.getCommitteeContactEmail());
			}
			break;
		case College:
			Long collegeId = submission.getCollegeId();
			if(collegeId != null) {
				recipients.addAll(settingRepo.findCollege(collegeId).getEmails().values());
			}
			break;
		case Department:
			Long departmentId = submission.getDepartmentId();
			if(departmentId != null) {
				recipients.addAll(settingRepo.findDepartment(departmentId).getEmails().values());
			}
			break;
		case Program:
			Long programId = submission.getProgramId();
			if(programId != null) {
				recipients.addAll(settingRepo.findProgram(programId).getEmails().values());
			}
			break;
		case AdminGroup:
			if(rule != null && rule.getAdminGroupRecipient() != null) {
				recipients.addAll(rule.getAdminGroupRecipient().getEmails().values());
			}
			break;
		case Assignee:
			if(submission.getAssignee() != null) {
				recipients.add(submission.getAssignee().getCurrentEmailAddress());
			}
			break;
		default:
			throw new UnsupportedOperationException();
		}		
		return recipients;
	}
}