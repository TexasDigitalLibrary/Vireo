package org.tdl.vireo.model.repo.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.DeprecatedAttachment;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.ActionLogRepoCustom;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

public class ActionLogRepoImpl implements ActionLogRepoCustom {

    @Autowired
    ActionLogRepo actionLogRepo;
    
    @Autowired
    UserRepo userRepo;
    
    @Autowired
    SubmissionRepo submissionRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Override
    public ActionLog create(Submission submission, SubmissionState submissionState, User user, Calendar actionDate, DeprecatedAttachment attachment, String entry, boolean privateFlag) {
    	submission.addActionLog(new ActionLog(submission, submissionState, user, actionDate, attachment, entry, privateFlag));
    	submission = submissionRepo.save(submission);
    	
    	ActionLog log = actionLogRepo.findBySubmissionAndActionDate(submission, actionDate);
    	simpMessagingTemplate.convertAndSend("/channel/submission/" + submission.getId() + "/action-logs", new ApiResponse(SUCCESS, log));
    	
        return log;
    }

	@Override
	public ActionLog createPublicLog(Submission submission, Credentials credentials, String entry) {
		User user = userRepo.findByEmail(credentials.getEmail());
		return create(submission, submission.getSubmissionState(), user, Calendar.getInstance(), null, entry, false);
	}
	
	@Override
	public ActionLog createPublicLog(Submission submission, Credentials credentials, String entry, DeprecatedAttachment attachment) {
		User user = userRepo.findByEmail(credentials.getEmail());
		return create(submission, submission.getSubmissionState(), user, Calendar.getInstance(), attachment, entry, false);
	}

	@Override
	public ActionLog createPrivateLog(Submission submission, Credentials credentials, String entry) {
		User user = userRepo.findByEmail(credentials.getEmail());
		return create(submission, submission.getSubmissionState(), user, Calendar.getInstance(), null, entry, true);
	}
	
	@Override
	public ActionLog createPrivateLog(Submission submission, Credentials credentials, String entry, DeprecatedAttachment attachment) {
		User user = userRepo.findByEmail(credentials.getEmail());
		return create(submission, submission.getSubmissionState(), user, Calendar.getInstance(), attachment, entry, true);
	}

}
