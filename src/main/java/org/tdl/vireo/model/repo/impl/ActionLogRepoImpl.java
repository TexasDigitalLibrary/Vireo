package org.tdl.vireo.model.repo.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ActionLogRepo;
import org.tdl.vireo.model.repo.custom.ActionLogRepoCustom;

public class ActionLogRepoImpl implements ActionLogRepoCustom{
	@Autowired ActionLogRepo actionLogRepo;

	@Override
	public ActionLog create(Submission submission, SubmissionState submissionState, User user, Calendar actionDate, String entry, boolean privateFlag) {
		return actionLogRepo.save(new ActionLog(submission, submissionState, user, actionDate, entry, privateFlag));
	}
}


