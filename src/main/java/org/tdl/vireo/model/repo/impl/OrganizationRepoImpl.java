package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.OrganizationRepoCustom;

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
    public Organization create(String name, Organization parent, OrganizationCategory category) {       
        Organization organization = create(name, category);
        parent.addChildOrganization(organization);
        organizationRepo.save(parent);
        return organizationRepo.findOne(organization.getId());
    }

    @Override
    public void delete(Organization organization) {
        OrganizationCategory category = organization.getCategory();
        category.removeOrganization(organization);
        organizationCategoryRepo.save(category);

        // need to recursively delete workflow step originating from organization being deleted
        List<WorkflowStep> workFlowStepsToRemove = new ArrayList<WorkflowStep>();

        organization.getWorkflowSteps().parallelStream().forEach(workflowStep -> {
            if(workflowStep.getOriginatingOrganization().getId().equals(organization.getId())) {
                workFlowStepsToRemove.add(workflowStep);
            }
        });
        
        for(WorkflowStep workflowStep : workFlowStepsToRemove) {
        	organization.removeWorkflowStep(workflowStep);
            workflowStepRepo.delete(workflowStep);
        }
        
        Set<Organization> parentOrganizations = organization.getParentOrganizations();
        
        // reconstructing tree when intermediate organization removed
        // i.e. grandparent becomes new parent of children
        organization.getChildrenOrganizations().parallelStream().forEach(childOrganization -> {
            childOrganization.removeParentOrganization(organization);
            
            parentOrganizations.parallelStream().forEach(parentOrganization -> {
                parentOrganization.addChildOrganization(childOrganization);
                organizationRepo.save(parentOrganization);
            });
            
            organizationRepo.save(childOrganization);
        });

        organization.getParentOrganizations().parallelStream().forEach(parentOrganization -> {
            parentOrganization.removeChildOrganization(organization);
            organizationRepo.save(parentOrganization);
        });
        
        organizationRepo.delete(organization.getId());
    }

}
