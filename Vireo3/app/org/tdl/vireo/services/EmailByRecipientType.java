package org.tdl.vireo.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
			if(collegeId != null && settingRepo.findCollege(collegeId) != null) {
				HashMap<Integer, String> collegeRecipients = settingRepo.findCollege(collegeId).getEmails();
				if (collegeRecipients != null && collegeRecipients.size() > 0) {
					Collection<String> collegeRecipientsEmails = collegeRecipients.values();
					if(collegeRecipientsEmails != null && collegeRecipientsEmails.size() > 0) {
						recipients.addAll(collegeRecipientsEmails);
					}
				}
			}
			break;
		case Department:
			Long departmentId = submission.getDepartmentId();
			if(departmentId != null && settingRepo.findDepartment(departmentId) != null) {
				HashMap<Integer, String> departmentRecipients = settingRepo.findDepartment(departmentId).getEmails();
				if (departmentRecipients != null && departmentRecipients.size() > 0) {
					Collection<String> departmentRecipientsEmails = departmentRecipients.values();
					if(departmentRecipientsEmails != null && departmentRecipientsEmails.size() > 0) {
						recipients.addAll(departmentRecipientsEmails);
					}
				}
			}
			break;
		case Program:
			Long programId = submission.getProgramId();
			if(programId != null && settingRepo.findProgram(programId) != null) {
				HashMap<Integer, String> programRecipients = settingRepo.findProgram(programId).getEmails();
				if (programRecipients != null && programRecipients.size() > 0) {
					Collection<String> programRecipientsEmails = programRecipients.values();
					if(programRecipientsEmails != null && programRecipientsEmails.size() > 0) {
						recipients.addAll(programRecipientsEmails);
					}
				}
			}
			break;
		case AdminGroup:
			if(rule != null && rule.getAdminGroupRecipient() != null) {
				HashMap<Integer, String> adminRecipients = rule.getAdminGroupRecipient().getEmails();
				if(adminRecipients != null && adminRecipients.size() > 0) {
					Collection<String> adminRecipientsEmails = adminRecipients.values();
					if(adminRecipientsEmails != null && adminRecipientsEmails.size() > 0) {
						recipients.addAll(adminRecipientsEmails);
					}
				}
			}
			break;
		case Assignee:
			if(submission.getAssignee() != null && submission.getAssignee().getCurrentEmailAddress() != null) {
				recipients.add(submission.getAssignee().getCurrentEmailAddress());
			}
			break;
		default:
			throw new UnsupportedOperationException();
		}		
		return recipients;
	}
}