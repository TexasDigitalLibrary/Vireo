package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;
import org.tdl.vireo.model.repo.impl.FieldProfileNonOverrideableException;
import org.tdl.vireo.model.repo.impl.WorkflowStepNonOverrideableException;

public class FieldProfileRepoImpl implements FieldProfileRepoCustom {
	
	@PersistenceContext
    private EntityManager em;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
    @Autowired
    private OrganizationRepo organizationRepo;
    
    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, repeatable, overrideable, enabled, optional));
        originatingWorkflowStep.addOriginalFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }

    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, repeatable, overrideable, enabled, optional));
        originatingWorkflowStep.addOriginalFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }
    
    @Override
    @Transactional // this is needed to lazy fetch fieldGlosses and controlledVocabularies
    public FieldProfile create(WorkflowStep originatingWorkflowStep, FieldPredicate fieldPredicate, InputType inputType, String usage, String help, Boolean repeatable, Boolean overrideable, Boolean enabled, Boolean optional) {
        FieldProfile fieldProfile = fieldProfileRepo.save(new FieldProfile(originatingWorkflowStep, fieldPredicate, inputType, usage, help, repeatable, overrideable, enabled, optional));
        originatingWorkflowStep.addOriginalFieldProfile(fieldProfile);
        workflowStepRepo.save(originatingWorkflowStep);
        return fieldProfileRepo.findOne(fieldProfile.getId());
    }
    
    public void disinheritFromWorkflowStep(Organization requestingOrganization, WorkflowStep workflowStep, FieldProfile fieldProfileToDisinherit) throws WorkflowStepNonOverrideableException {
        
    	// if requesting organization is not the workflow step's orignating organization    	    	    	
        if(!workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
        	// create a new workflow step
            workflowStep = workflowStepRepo.update(workflowStep, requestingOrganization);
            
            workflowStep.removeAggregateFieldProfile(fieldProfileToDisinherit);
        	
            workflowStepRepo.save(workflowStep);
        }
        else {
        	fieldProfileRepo.delete(fieldProfileToDisinherit);
        }
        
    }
    
    public FieldProfile update(FieldProfile fieldProfile, Organization requestingOrganization) throws FieldProfileNonOverrideableException, WorkflowStepNonOverrideableException {
    	
    	//if the requesting organization does not originate the step that originates the fieldProfile, and it is non-overrideable, then throw an exception.
        boolean requestorOriginatesProfile = false;
        
        for(WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {        	
            //if this step of the requesting organization happens to be the originator of the field profile, and the step also originates in the requesting organization, then this organization truly originates the field profile.
            if(fieldProfile.getOriginatingWorkflowStep().getId().equals(workflowStep.getId()) && requestingOrganization.getId().equals(workflowStep.getOriginatingOrganization().getId())) {
                requestorOriginatesProfile = true;
            }
        }
        
        //if the requestor is not the originator and it is not overrideable, we can't make the update
        if(!requestorOriginatesProfile && !fieldProfile.getOverrideable()) {
            
        	// provide feedback of attempt to override non overrideable
        	// exceptions may be of better use for unavoidable error handling
        	
            throw new FieldProfileNonOverrideableException();
        }
        //if the requestor is not originator, and the step the profile's on is not overrideable, we can't make the update
        else if(!requestorOriginatesProfile && !fieldProfile.getOriginatingWorkflowStep().getOverrideable())
        {
            throw new WorkflowStepNonOverrideableException();
        }
        //if the requestor originates, make the update at the requestor
        else if(requestorOriginatesProfile) {
            // do nothing, just save changes
            fieldProfile = fieldProfileRepo.save(fieldProfile);
        }
        //else, it's overrideable and we didn't oringinate it so we need to make a new one that overrides.
        else {
            
        	Long originalFieldProfileId = fieldProfile.getId();
        	
        	List<FieldGloss> fieldGlosses = new ArrayList<FieldGloss>();
        	List<ControlledVocabulary> controlledVocabularies = new ArrayList<ControlledVocabulary>();
        	
        	for(FieldGloss fg : fieldProfile.getFieldGlosses()) {
        		fieldGlosses.add(fg);            		
        	}
        	
			for(ControlledVocabulary cv : fieldProfile.getControlledVocabularies()) {
				controlledVocabularies.add(cv);
        	}
        	
			fieldProfile.setFieldGlosses(new ArrayList<FieldGloss>());
			fieldProfile.setControlledVocabularies(new ArrayList<ControlledVocabulary>());
        	
        	em.detach(fieldProfile);
        	fieldProfile.setId(null);
            
        	
        	FieldProfile originalFieldProfile = fieldProfileRepo.findOne(originalFieldProfileId);
             
        	
        	WorkflowStep originalOriginatingWorkflowStep = originalFieldProfile.getOriginatingWorkflowStep();
        	
        	// when a organization that did not originate the workflow step needs to update the field profile with the step,
        	// a new workflow step must be created with the requesting organization as the originator        	
        	if(!originalOriginatingWorkflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
        		
        		WorkflowStep newOriginatingWorkflowStep = workflowStepRepo.update(originalOriginatingWorkflowStep, requestingOrganization);
        		
        		fieldProfile.setOriginatingWorkflowStep(newOriginatingWorkflowStep);
        	}
        	
        	
        	fieldProfile.setOriginatingFieldProfile(null);
        	
             
        	fieldProfile.setFieldGlosses(fieldGlosses);
        	fieldProfile.setControlledVocabularies(controlledVocabularies);
        	
        	
        	fieldProfile = fieldProfileRepo.save(fieldProfile);
        	
        	
        	for(WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, originalFieldProfile)) {
        		workflowStep.replaceAggregateFieldProfile(originalFieldProfile, fieldProfile);
        		workflowStepRepo.save(workflowStep);
        	}
        	
        	
        	for(WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {
				if(workflowStep.getAggregateFieldProfiles().contains(originalFieldProfile)) {
					workflowStep.replaceAggregateFieldProfile(originalFieldProfile, fieldProfile);
					workflowStepRepo.save(workflowStep);
				}
    		}
			
        	
        	// if parent organization's workflow step updates a field profile originating form a descendent, the original field profile need to be deleted
            if(workflowStepRepo.findByAggregateFieldProfilesId(originalFieldProfile.getId()).size() == 0) {
                fieldProfileRepo.delete(originalFieldProfile);
            }
            else {
                fieldProfile.setOriginatingFieldProfile(originalFieldProfile);
                fieldProfile = fieldProfileRepo.save(fieldProfile);
            }
        	
        }
        
        return fieldProfile;

    }

    @Override
    public void delete(FieldProfile fieldProfile) {
    	
    	// allows for delete by iterating through findAll, while still deleting descendents
    	if(fieldProfileRepo.findOne(fieldProfile.getId()) != null) {
        
	    	WorkflowStep originatingWorkflowStep = fieldProfile.getOriginatingWorkflowStep();
	    	
	    	originatingWorkflowStep.removeOriginalFieldProfile(fieldProfile);
	    	
	    	if(fieldProfile.getOriginatingFieldProfile() != null) {
	    		fieldProfile.setOriginatingFieldProfile(null);
	        }
	    		    	
	    	fieldProfile.setOriginatingWorkflowStep(null);
	    	
	    	workflowStepRepo.findByAggregateFieldProfilesId(fieldProfile.getId()).forEach(workflowStep -> {
	    		workflowStep.removeAggregateFieldProfile(fieldProfile);
	    		workflowStepRepo.save(workflowStep);
	        });
	    	
	    	fieldProfileRepo.findByOriginatingFieldProfile(fieldProfile).forEach(fp -> {
	    		fp.setOriginatingFieldProfile(null);
	        });
	    	
	    	deleteDescendantsOfFieldProfile(fieldProfile);
	    	
	    	fieldProfileRepo.delete(fieldProfile.getId());
    	
    	}
    }
    
    private void deleteDescendantsOfFieldProfile(FieldProfile fieldProfile) {
        fieldProfileRepo.findByOriginatingFieldProfile(fieldProfile).forEach(desendantFieldProfile -> {
    		delete(desendantFieldProfile);
        });
    }
    
    private List<WorkflowStep> getContainingDescendantWorkflowStep(Organization organization, FieldProfile fieldProfile) {
        List<WorkflowStep> descendantWorkflowStepsContainingFieldProfile = new ArrayList<WorkflowStep>();
        organization.getAggregateWorkflowSteps().forEach(ws -> {
        	if(ws.getAggregateFieldProfiles().contains(fieldProfile)) {
            	descendantWorkflowStepsContainingFieldProfile.add(ws);
            }
        });
        organization.getChildrenOrganizations().forEach(descendantOrganization -> {
        	descendantWorkflowStepsContainingFieldProfile.addAll(getContainingDescendantWorkflowStep(descendantOrganization, fieldProfile));
        });
        return descendantWorkflowStepsContainingFieldProfile;
    }
    
}
