package org.tdl.vireo.view;

import java.util.Set;
import org.tdl.vireo.model.FieldValue;

public interface FieldValueSubmissionView extends SimpleSubmissionView {

    public Set<FieldValue> getFieldValues();
}
