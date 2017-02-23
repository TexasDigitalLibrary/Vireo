package org.tdl.vireo.model.repo.custom;

import java.util.List;

import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;
import org.tdl.vireo.inheritence.Heritable;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;

public interface WorkflowStepRepoCustom {

    public WorkflowStep create(String name, Organization originatingOrganization);

    public WorkflowStep swapFieldProfiles(Organization requestOrganization, WorkflowStep workflowStep, FieldProfile fp1, FieldProfile fp2) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException;

    public WorkflowStep reorderFieldProfiles(Organization requestOrganization, WorkflowStep workflowStep, int src, int dest) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException;

    public WorkflowStep swapNotes(Organization requestOrganization, WorkflowStep workflowStep, Note n1, Note n2) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException;

    public WorkflowStep reorderNotes(Organization requestOrganization, WorkflowStep workflowStep, int src, int dest) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException;

    public void removeFromOrganization(Organization requestingOrganization, WorkflowStep workflowStepToDisinherit);

    public WorkflowStep update(WorkflowStep workflowStep, Organization requestingOrganization) throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException;

    public void delete(WorkflowStep workflowStep);

    public List<WorkflowStep> getDescendantsOfStep(WorkflowStep workflowStep);

    public List<Organization> getContainingDescendantOrganization(Organization organization, WorkflowStep workflowStep);

    public List<WorkflowStep> getDescendantsOfStepUnderOrganization(WorkflowStep workflowStep, Organization organization);

    @SuppressWarnings("rawtypes")
    public List<WorkflowStep> findByAggregateHeritableModel(Heritable persistedHeritableModel);

}
