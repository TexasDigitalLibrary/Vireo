package org.tdl.vireo.model.repo.impl;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;

import edu.tamu.framework.model.ApiResponse;

public class OrganizationRepoImpl implements OrganizationRepoCustom {

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private SubmissionRepo submissionRepo;
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public Organization create(String name, OrganizationCategory category) {
        Organization organization = organizationRepo.save(new Organization(name, category));
        category.addOrganization(organization);
        organizationCategoryRepo.save(category);
        return organizationRepo.findOne(organization.getId());
    }

    @Override
    @Transactional // this transactional is required to persist parent child relationship within
    public Organization create(String name, Organization parent, OrganizationCategory category) {
        Organization organization = create(name, category);
        if (parent != null) {
            System.out.println("In Organization.create(): Creating organization " + name + " and adding it as a child of its parent " + (parent == null ? null : parent.getName()));
            parent.addChildOrganization(organization);
            parent = organizationRepo.save(parent);
            parent.getAggregateWorkflowSteps().forEach(ws -> {
                organization.addAggregateWorkflowStep(ws);
            });
        }
        return organizationRepo.save(organization);
    }

    public Organization reorderWorkflowSteps(Organization organization, WorkflowStep ws1, WorkflowStep ws2) {
        organization.swapAggregateWorkflowStep(ws1, ws2);
        return organizationRepo.save(organization);
    }

    @Override
    public void delete(Organization organization) {

        OrganizationCategory category = organization.getCategory();
        category.removeOrganization(organization);
        organizationCategoryRepo.save(category);

        Long orgId = organization.getId();

        organization.getEmails().clear();

        Organization parentOrganization = organization.getParentOrganization();

        // Have all the parent organizations not have this one as their child anymore
        if (parentOrganization != null) {
            parentOrganization.removeChildOrganization(organization);
            parentOrganization = organizationRepo.save(parentOrganization);
            organization = organizationRepo.findOne(orgId);
        }

        Set<Organization> childrenToRemove = new HashSet<Organization>();

        // Have all the child organizations get this one's parent as their parent
        for (Organization childOrganization : organization.getChildrenOrganizations()) {
            childrenToRemove.add(childOrganization);
        }

        for (Organization childOrganization : childrenToRemove) {
            organization.removeChildOrganization(childOrganization);
            organization = organizationRepo.save(organization);
            if (parentOrganization != null) {
                childOrganization = organizationRepo.save(childOrganization);
                parentOrganization.addChildOrganization(childOrganization);
                parentOrganization = organizationRepo.save(parentOrganization);
            } else {
                childOrganization.setParentOrganization(null);
                childOrganization = organizationRepo.save(childOrganization);
            }
        }

        // Have all the submissions on this organization get the parent as their new organization
        // TODO: for now, have to delete them if there is no parent org to attach them to.
        for (Submission submission : submissionRepo.findByOrganization(organization)) {
            if (parentOrganization != null) {
                submission.setOrganization(parentOrganization);
                submissionRepo.save(submission);
            } else {
                submissionRepo.delete(submission);
            }
        }

        List<WorkflowStep> workflowStepsToDelete = new ArrayList<WorkflowStep>();
        List<WorkflowStep> workflowStepsToRemove = new ArrayList<WorkflowStep>();

        for (WorkflowStep ws : organization.getOriginalWorkflowSteps()) {
            workflowStepsToDelete.add(ws);
            workflowStepsToRemove.add(ws);
        }

        for (WorkflowStep ws : workflowStepsToRemove) {
            organization.removeOriginalWorkflowStep(ws);
            organization.removeAggregateWorkflowStep(ws);
        }

        List<WorkflowStep> aggregateWorkflow = new ArrayList<WorkflowStep>();
        for (WorkflowStep ws : organization.getAggregateWorkflowSteps()) {
            aggregateWorkflow.add(ws);
        }

        for (WorkflowStep ws : aggregateWorkflow) {
            organization.removeAggregateWorkflowStep(ws);
        }

        for (WorkflowStep ws : workflowStepsToDelete) {
            workflowStepRepo.delete(ws);
        }

        organizationRepo.delete(orgId);
    }
    
    @Override
	public Organization restoreDefaults(Organization organization) {
    	 Organization persistedOrg = organizationRepo.findOne(organization.getId());
    	 Organization parentOrg = organizationRepo.findOne(organization.getParentOrganization().getId());
    	 
    	 persistedOrg.clearAggregatedWorkflowStepsFromHiarchy(); 
    	 
    	 parentOrg.getAggregateWorkflowSteps().forEach(ws -> {
    		 persistedOrg.addAggregateWorkflowStep(ws);
         });
    	
         return  organizationRepo.save(persistedOrg);
   	}

    @Override
    public Set<Organization> getDescendantOrganizations(Organization org) {

        Set<Organization> descendants = new HashSet<Organization>();

        for (Organization child : org.getChildrenOrganizations()) {
            descendants.add(child);
            descendants.addAll(getDescendantOrganizations(descendants, child));
        }

        return descendants;
    }

    private Set<Organization> getDescendantOrganizations(Set<Organization> descendants, Organization org) {

        for (Organization child : org.getChildrenOrganizations()) {
            descendants.add(child);
            descendants.addAll(getDescendantOrganizations(descendants, child));
        }

        return descendants;
    }

}