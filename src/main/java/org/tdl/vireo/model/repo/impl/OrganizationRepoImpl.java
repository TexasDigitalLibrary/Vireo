package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

import org.springframework.transaction.annotation.Transactional;

public class OrganizationRepoImpl implements OrganizationRepoCustom {
	
    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private OrganizationCategoryRepo organizationCategoryRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
     
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
        parent.addChildOrganization(organization);
        parent = organizationRepo.save(parent);
        parent.getWorkflow().forEach(ws -> {
            organization.addStepToWorkflow(ws);
        });
        return organizationRepo.save(organization);
    }

    @Override 
    public void delete(Organization organization) {
        OrganizationCategory category = organization.getCategory();
        category.removeOrganization(organization);
        organizationCategoryRepo.save(category);
        
        
        Set<Organization> parentOrganizations = new HashSet<Organization>();
        
        for(Organization childOrganization : organization.getParentOrganizations()) {
        	parentOrganizations.add(childOrganization);
        }
        
        for(Organization parentOrganization : parentOrganizations) {
            parentOrganization.removeChildOrganization(organization);
            organizationRepo.save(parentOrganization);
        }
        
        for(Organization childOrganization : organization.getChildrenOrganizations()) {
            childOrganization.removeParentOrganization(organization);
            organizationRepo.save(childOrganization);
            
            parentOrganizations.parallelStream().forEach(parentOrganization -> {
                parentOrganization.addChildOrganization(childOrganization);
                organizationRepo.save(parentOrganization);
            });
            
            organization.removeChildOrganization(childOrganization);
            
            organization = organizationRepo.save(organization);
        }
        
        
        
        List<WorkflowStep> workflowStepsToDelete = new ArrayList<WorkflowStep>();
        List<WorkflowStep> workflowStepsToRemove = new ArrayList<WorkflowStep>();
        
        for(WorkflowStep ws : organization.getWorkflowSteps()) {
        	workflowStepsToDelete.add(ws);
        	workflowStepsToRemove.add(ws);
        }
        
        for(WorkflowStep ws : workflowStepsToRemove) {
        	organization.removeWorkflowStep(ws);
        }
        
        
        List<WorkflowStep> workflow = new ArrayList<WorkflowStep>();
        
        for(WorkflowStep ws : organization.getWorkflow()) {
        	workflow.add(ws);
        }
        
        for(WorkflowStep ws : workflow) {
        	organization.removeStepFromWorkflow(ws);
        }
   	
    	
    	for(WorkflowStep ws : workflowStepsToDelete) {
        	workflowStepRepo.delete(ws);
        }
        
        organizationRepo.delete(organization.getId());
    }

}
