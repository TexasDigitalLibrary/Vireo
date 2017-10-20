package org.tdl.vireo.model.repo.custom;

import java.util.Calendar;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;

import edu.tamu.framework.model.Credentials;

public interface ActionLogRepoCustom {

    public ActionLog create(Submission submission, User user, Calendar actionDate, String entry, boolean privateFlag);

    public ActionLog create(Submission submission, Calendar actionDate, String entry, boolean privateFlag);

    public ActionLog createPublicLog(Submission submission, Credentials credentials, String entry);

    public ActionLog createAdvisorPublicLog(Submission submission, String entry);

    public ActionLog createPrivateLog(Submission submission, Credentials credentials, String entry);

}
