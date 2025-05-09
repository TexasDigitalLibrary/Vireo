package org.tdl.vireo.model.listener;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.service.SubmissionEmailService;

/**
 * Listener to check new action logs created on submission change.
 *
 * See ActionLogRepoImpl.
 *
 * All action logs created require a submission and when any action log is saved
 * it saves the submission. Which we can reliable process all actions through a
 * submission create or update.
 *
 * ActionLog now must have a flag indicating it has been processed.
 */
@Component
public class SubmissionActionLogListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmissionActionLogListener.class);

    private SubmissionEmailService submissionEmailService;

    SubmissionActionLogListener(@Lazy SubmissionEmailService submissionEmailService) {
        this.submissionEmailService = submissionEmailService;
    }

    @PrePersist
    private void afterCreate(Submission submission) {
        processSubmission(submission);
    }

    @PreUpdate
    private void afterUpdate(Submission submission) {
        processSubmission(submission);
    }

    private synchronized void processSubmission(Submission submission) {
        submission.getActionLogs()
            .stream()
            .filter(this::isNotProcessed)
            .forEach(actionLog -> processSubmissionActionLog(submission, actionLog));
    }

    private boolean isNotProcessed(ActionLog actionLog) {
        return !actionLog.getProcessed();
    }

    private void processSubmissionActionLog(Submission submission, ActionLog actionLog) {
        Action action = actionLog.getAction();
        
        switch (action) {
        case STUDENT_MESSAGE:
        case ADVISOR_MESSAGE:
        case ADVISOR_APPROVE_SUBMISSION:
        case ADVISOR_CLEAR_APPROVE_SUBMISSION:
        case ADVISOR_APPROVE_EMBARGO:
        case ADVISOR_CLEAR_APPROVE_EMBARGO:
            submissionEmailService.sendActionEmails(submission, actionLog);
            LOGGER.info("Submission {}: {} action logged processed", submission.getId(), action);
            break;
        case UNDETERMINED:
        default:
            break;
        }
        // set processed after processing action email workflow rules
        actionLog.setProcessed(true);
    }

}
