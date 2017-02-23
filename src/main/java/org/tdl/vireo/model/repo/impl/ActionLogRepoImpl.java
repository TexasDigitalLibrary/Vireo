package org.tdl.vireo.model.repo.impl;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.custom.ActionLogRepoCustom;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

public class ActionLogRepoImpl implements ActionLogRepoCustom {

    @Autowired
    private ActionLogRepo actionLogRepo;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public ActionLog create(Submission submission, User user, Calendar actionDate, String entry, boolean privateFlag) {
        ActionLog log = actionLogRepo.save(new ActionLog(submission.getSubmissionState(), user, actionDate, entry, privateFlag));
        submission.addActionLog(log);
        submissionRepo.save(submission);
        simpMessagingTemplate.convertAndSend("/channel/submission/" + submission.getId() + "/action-logs", new ApiResponse(SUCCESS, log));
        return log;
    }

    @Override
    public ActionLog createPublicLog(Submission submission, Credentials credentials, String entry) {
        User user = userRepo.findByEmail(credentials.getEmail());
        return create(submission, user, Calendar.getInstance(), entry, false);
    }

    @Override
    public ActionLog createPrivateLog(Submission submission, Credentials credentials, String entry) {
        User user = userRepo.findByEmail(credentials.getEmail());
        return create(submission, user, Calendar.getInstance(), entry, true);
    }

    @Override
    public void delete(ActionLog actionLog) {
        for (Submission submission : submissionRepo.findByActionLogsId(actionLog.getId())) {
            submission.removeActionLog(actionLog);
            submissionRepo.save(submission);
        }
    }

}
