package org.tdl.vireo.model.repo.impl;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.custom.ActionLogRepoCustom;

import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;
import edu.tamu.weaver.response.ApiResponse;

public class ActionLogRepoImpl extends AbstractWeaverRepoImpl<ActionLog, ActionLogRepo> implements ActionLogRepoCustom {

    @Autowired
    private ActionLogRepo actionLogRepo;

    @Autowired
    private SubmissionRepo submissionRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public ActionLog create(Submission submission, User user, Calendar actionDate, String entry, boolean privateFlag) {
        ActionLog log = actionLogRepo.save(new ActionLog(submission.getSubmissionStatus(), user, actionDate, entry, privateFlag));
        submission.addActionLog(log);
        submissionRepo.save(submission);
        simpMessagingTemplate.convertAndSend("/channel/submission/" + submission.getId() + "/action-logs", new ApiResponse(SUCCESS, log));
        return log;
    }

    @Override
    public ActionLog create(Submission submission, Calendar actionDate, String entry, boolean privateFlag) {
        ActionLog log = actionLogRepo.save(new ActionLog(submission.getSubmissionStatus(), actionDate, entry, privateFlag));
        submission.addActionLog(log);
        submissionRepo.save(submission);
        simpMessagingTemplate.convertAndSend("/channel/submission/" + submission.getId() + "/action-logs", new ApiResponse(SUCCESS, log));
        return log;
    }

    @Override
    public ActionLog createPublicLog(Submission submission, User user, String entry) {
        return create(submission, user, Calendar.getInstance(), entry, false);
    }

    @Override
    public ActionLog createAdvisorPublicLog(Submission submission, String entry) {
        return create(submission, Calendar.getInstance(), entry, false);
    }

    @Override
    public ActionLog createPrivateLog(Submission submission, User user, String entry) {
        return create(submission, user, Calendar.getInstance(), entry, true);
    }

    @Override
    public void delete(ActionLog actionLog) {
        for (Submission submission : submissionRepo.findByActionLogsId(actionLog.getId())) {
            submission.removeActionLog(actionLog);
            submissionRepo.save(submission);
        }
    }

    @Override
    protected String getChannel() {
        return "/channel/action-log";
    }

}
