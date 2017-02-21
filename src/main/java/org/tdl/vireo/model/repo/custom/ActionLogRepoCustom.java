package org.tdl.vireo.model.repo.custom;

import java.util.Calendar;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.DeprecatedAttachment;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;

import edu.tamu.framework.model.Credentials;

public interface ActionLogRepoCustom {

    public ActionLog create(Submission submission, SubmissionState submissionState, User user, Calendar actionDate, DeprecatedAttachment attachment, String entry, boolean privateFlag);
    
    public ActionLog createPublicLog(Submission submission, Credentials credentials, String entry);
    
    public ActionLog createPrivateLog(Submission submission, Credentials credentials, String entry);

	ActionLog createPrivateLog(Submission submission, Credentials credentials, String entry, DeprecatedAttachment attachment);

	ActionLog createPublicLog(Submission submission, Credentials credentials, String entry, DeprecatedAttachment attachment);

}
