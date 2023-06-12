package org.tdl.vireo.view;

import java.util.Calendar;
import org.tdl.vireo.model.SubmissionStatus;

public interface SimpleSubmissionView {

    public Long getId();

    public SimpleUserView getSubmitter();

    public SettingsUserView getAssignee();

    public SubmissionStatus getSubmissionStatus();

    public SimpleOrganizationView getOrganization();

    public Calendar getSubmissionDate();
}
