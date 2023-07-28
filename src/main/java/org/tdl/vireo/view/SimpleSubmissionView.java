package org.tdl.vireo.view;

import java.util.Calendar;
import org.tdl.vireo.model.SubmissionStatus;

public interface SimpleSubmissionView extends SimpleModelView {

    public SimpleUserView getSubmitter();

    public SettingsUserView getAssignee();

    public SubmissionStatus getSubmissionStatus();

    public SimpleNamedModelView getOrganization();

    public Calendar getSubmissionDate();
}
