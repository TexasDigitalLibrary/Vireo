package org.tdl.vireo.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.tdl.vireo.model.EmailWorkflowRuleByAction;
import org.tdl.vireo.model.EmailWorkflowRuleByStatus;
import org.tdl.vireo.model.OrganizationCategory;

public interface ShallowOrganizationView extends TreeOrganizationView {

    public OrganizationCategory getCategory();

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = SimpleWorkflowStepView.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    public List<SimpleWorkflowStepView> getOriginalWorkflowSteps();

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = SimpleWorkflowStepView.class, property = "id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public List<SimpleWorkflowStepView> getAggregateWorkflowSteps();

    public List<String> getEmails();

    public List<EmailWorkflowRuleByStatus> getEmailWorkflowRules();

    public List<EmailWorkflowRuleByAction> getEmailWorkflowRulesByAction();

}
