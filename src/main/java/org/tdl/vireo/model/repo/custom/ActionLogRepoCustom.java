package org.tdl.vireo.model.repo.custom;

import java.util.Calendar;

import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;

public interface ActionLogRepoCustom {

    public ActionLog create(Action action, Submission submission, User user, Calendar actionDate, String entry, boolean privateFlag);

    public ActionLog create(Action action, Submission submission, Calendar actionDate, String entry, boolean privateFlag);

    public ActionLog createPublicLog(Action action, Submission submission, User user, String entry);

    public ActionLog createAdvisorPublicLog(Action action, Submission submission, String entry);

    public ActionLog createPrivateLog(Action action, Submission submission, User user, String entry);

}
