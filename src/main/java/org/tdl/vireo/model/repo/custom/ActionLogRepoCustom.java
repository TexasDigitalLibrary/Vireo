package org.tdl.vireo.model.repo.custom;

import java.util.Calendar;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.DeprecatedAttachment;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;

public interface ActionLogRepoCustom {

    public ActionLog create(Submission submission, SubmissionState submissionState, User user, Calendar actionDate, DeprecatedAttachment attachment, String entry, boolean privateFlag);

}
