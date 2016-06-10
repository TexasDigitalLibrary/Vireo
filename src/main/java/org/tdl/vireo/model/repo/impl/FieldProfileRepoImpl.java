package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.enums.InputType;
import org.tdl.vireo.model.ControlledVocabulary;
import org.tdl.vireo.model.FieldGloss;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

public class FieldProfileRepoImpl implements FieldProfileRepoCustom {
	
	@PersistenceContext
    private EntityManager em;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;
    
    @Autowired
    private WorkflowStepRepo workflowStepRepo;
    
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
    
    public void disinheritFromWorkflowStep(Organization requestingOrganization, WorkflowStep workflowStep, FieldProfile fieldProfileToDisinherit) throws WorkflowStepNonOverrideableException, FieldProfileNonOverrideableException {
        
        if(workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId()) || workflowStep.getOverrideable()) {
            
            if(fieldProfileToDisinherit.getOriginatingWorkflowStep().getId().equals(fieldProfileToDisinherit.getId()) || fieldProfileToDisinherit.getOverrideable()) {
            
            	// if requesting organization is not the workflow step's orignating organization    	    	    	
                if(!workflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
                	// create a new workflow step
                    workflowStep = workflowStepRepo.update(workflowStep, requestingOrganization);
                    
                    workflowStep.removeAggregateFieldProfile(fieldProfileToDisinherit);
                	
                    workflowStepRepo.save(workflowStep);
                }
                else {
                    
                    List<WorkflowStep> workflowStepsContainingFieldProfile = getContainingDescendantWorkflowStep(requestingOrganization, fieldProfileToDisinherit);
                    
                    if(workflowStepsContainingFieldProfile.size() > 0) {
                        
                        boolean foundNewOriginalOwner = false;
                        
                        for(WorkflowStep workflowStepContainingFieldProfile : workflowStepsContainingFieldProfile) {
                            // add field profile as original to first workflow step
                            if(!foundNewOriginalOwner) {
                                workflowStepContainingFieldProfile.addOriginalFieldProfile(fieldProfileToDisinherit);
                                foundNewOriginalOwner = true;
                            }
                            else {
                                workflowStepContainingFieldProfile.addAggregateFieldProfile(fieldProfileToDisinherit);
                            }
                            workflowStepRepo.save(workflowStepContainingFieldProfile);
                        }
                        
                        workflowStep.removeOriginalFieldProfile(fieldProfileToDisinherit);
                        
                        workflowStepRepo.save(workflowStep);
                        
                    }
                    else {            
                        fieldProfileRepo.delete(fieldProfileToDisinherit);
                    }
                }
            }
            else {
                throw new FieldProfileNonOverrideableException();
            }
        }
        else {
            throw new WorkflowStepNonOverrideableException();
        }
        
    }
    
    public FieldProfile update(FieldProfile pendingFieldProfile, Organization requestingOrganization) throws FieldProfileNonOverrideableException, WorkflowStepNonOverrideableException {
    	
        FieldProfile resultingFieldProfile = null;
        
        FieldProfile persistedFieldProfile = fieldProfileRepo.findOne(pendingFieldProfile.getId());
        
        boolean overridabilityOfPersistedFieldProfile = persistedFieldProfile.getOverrideable();
        
        boolean overridabilityOfOriginatingWorkflowStep = persistedFieldProfile.getOriginatingWorkflowStep().getOverrideable();
        
        
        WorkflowStep workflowStepWithFieldProfileOnRequestingOrganization = null;
        
        boolean requestingOrganizationOriginatedWorkflowStep = false;
        
        boolean workflowStepWithFieldProfileOnRequestingOrganizationOriginatedFieldProfile = false;
        
        for(WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {
            if(workflowStep.getAggregateFieldProfiles().contains(persistedFieldProfile)) {
                workflowStepWithFieldProfileOnRequestingOrganization = workflowStep;
                requestingOrganizationOriginatedWorkflowStep = workflowStepWithFieldProfileOnRequestingOrganization.getOriginatingOrganization().getId().equals(requestingOrganization.getId());
            }
        }
        
        if(workflowStepWithFieldProfileOnRequestingOrganization != null) {
            workflowStepWithFieldProfileOnRequestingOrganizationOriginatedFieldProfile = persistedFieldProfile.getOriginatingWorkflowStep().getId().equals(workflowStepWithFieldProfileOnRequestingOrganization.getId());
        }
        
        if(!overridabilityOfOriginatingWorkflowStep && !requestingOrganizationOriginatedWorkflowStep) {
            throw new WorkflowStepNonOverrideableException();
        }
        
        if(!overridabilityOfPersistedFieldProfile && !(workflowStepWithFieldProfileOnRequestingOrganizationOriginatedFieldProfile && requestingOrganizationOriginatedWorkflowStep)) {
            throw new FieldProfileNonOverrideableException();
        }
        
        if(workflowStepWithFieldProfileOnRequestingOrganizationOriginatedFieldProfile && requestingOrganizationOriginatedWorkflowStep) {
            resultingFieldProfile = fieldProfileRepo.save(pendingFieldProfile);
        }
        else {
            
            List<FieldGloss> fieldGlosses = new ArrayList<FieldGloss>();
            List<ControlledVocabulary> controlledVocabularies = new ArrayList<ControlledVocabulary>();
              
            for(FieldGloss fg : pendingFieldProfile.getFieldGlosses()) {
                fieldGlosses.add(fg);                   
            }
              
            for(ControlledVocabulary cv : pendingFieldProfile.getControlledVocabularies()) {
                controlledVocabularies.add(cv);
            }
              
            pendingFieldProfile.setFieldGlosses(new ArrayList<FieldGloss>());
            pendingFieldProfile.setControlledVocabularies(new ArrayList<ControlledVocabulary>());
            
            
            em.detach(pendingFieldProfile);
            
            pendingFieldProfile.setId(null);

            WorkflowStep persistedOriginatingWorkflowStep = persistedFieldProfile.getOriginatingWorkflowStep();
            
            if(!requestingOrganizationOriginatedWorkflowStep) {
                
                WorkflowStep existingOriginatingWorkflowStep = workflowStepRepo.findByNameAndOriginatingOrganization(persistedOriginatingWorkflowStep.getName(), requestingOrganization);
                
                if(existingOriginatingWorkflowStep == null) {
                    WorkflowStep newOriginatingWorkflowStep = workflowStepRepo.update(persistedOriginatingWorkflowStep, requestingOrganization);
                  
                    pendingFieldProfile.setOriginatingWorkflowStep(newOriginatingWorkflowStep);
                }
                else {
                    pendingFieldProfile.setOriginatingWorkflowStep(existingOriginatingWorkflowStep);
                }
            }
          
          
            pendingFieldProfile.setOriginatingFieldProfile(null);
            
            pendingFieldProfile.setFieldGlosses(fieldGlosses);
            pendingFieldProfile.setControlledVocabularies(controlledVocabularies);
          

            FieldProfile newFieldProfile = fieldProfileRepo.save(pendingFieldProfile);
          
          
            for(WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, persistedFieldProfile)) {
                workflowStep.replaceAggregateFieldProfile(persistedFieldProfile, newFieldProfile);
                workflowStepRepo.save(workflowStep);
            }
          
          
            for(WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {
                if(workflowStep.getAggregateFieldProfiles().contains(persistedFieldProfile)) {
                    workflowStep.replaceAggregateFieldProfile(persistedFieldProfile, newFieldProfile);
                    workflowStepRepo.save(workflowStep);
                }
            }
            
          
            // if parent organization's workflow step updates a field profile originating form a descendent, the original field profile need to be deleted
            if(workflowStepRepo.findByAggregateFieldProfilesId(persistedFieldProfile.getId()).size() == 0) {
                fieldProfileRepo.delete(persistedFieldProfile);
            }
            else {
                newFieldProfile.setOriginatingFieldProfile(persistedFieldProfile);
                newFieldProfile = fieldProfileRepo.save(newFieldProfile);
            }
            
            
            
            // TODO: if changed from overrideable to non overrideable, re-inherit
            
            
            
            
            
            
            
            resultingFieldProfile = newFieldProfile;
            
        }
        
        return resultingFieldProfile;
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
