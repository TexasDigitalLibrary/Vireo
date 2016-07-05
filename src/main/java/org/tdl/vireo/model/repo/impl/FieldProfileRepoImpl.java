package org.tdl.vireo.model.repo.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.InputType;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.FieldProfileRepoCustom;

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
    
    public void removeFromWorkflowStep(Organization requestingOrganization, WorkflowStep pendingWorkflowStep, FieldProfile fieldProfileToRemove) throws WorkflowStepNonOverrideableException, FieldProfileNonOverrideableException {

        // if requesting organization originates the workflow step or the workflow step is overrideable,
        if (pendingWorkflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId()) || pendingWorkflowStep.getOverrideable()) {
            // ... and if also that workflow step originates the fieldProfile or the fieldProfile is overrideable,
            if (fieldProfileToRemove.getOriginatingWorkflowStep().getId().equals(fieldProfileToRemove.getId()) || fieldProfileToRemove.getOverrideable()) {
                // ...then the update is permissible.

                // if requesting organization is not the workflow step's orignating organization,
                if (!pendingWorkflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
                    // create a new workflow step
                    pendingWorkflowStep = workflowStepRepo.update(pendingWorkflowStep, requestingOrganization);

                    // recursive call
                    pendingWorkflowStep.removeAggregateFieldProfile(fieldProfileToRemove);

                    workflowStepRepo.save(pendingWorkflowStep);
                }
                // else, requesting organization originates the workflow step
                else {

                    List<WorkflowStep> workflowStepsContainingFieldProfile = getContainingDescendantWorkflowStep(requestingOrganization, fieldProfileToRemove);

                    if (workflowStepsContainingFieldProfile.size() > 0) {

                        boolean foundNewOriginalOwner = false;

                        for (WorkflowStep workflowStepContainingFieldProfile : workflowStepsContainingFieldProfile) {
                            // add fieldProfile as original to first workflow step
                            if (!foundNewOriginalOwner) {
                                workflowStepContainingFieldProfile.addOriginalFieldProfile(fieldProfileToRemove);
                                foundNewOriginalOwner = true;
                            } else {
                                workflowStepContainingFieldProfile.addAggregateFieldProfile(fieldProfileToRemove);
                            }
                            workflowStepRepo.save(workflowStepContainingFieldProfile);
                        }

                        pendingWorkflowStep.removeOriginalFieldProfile(fieldProfileToRemove);

                        workflowStepRepo.save(pendingWorkflowStep);

                    } else {
                        fieldProfileRepo.delete(fieldProfileToRemove);
                    }
                }
            } // workflow step doesn't originate the fieldProfile and it is non-overrideable
            else {
                throw new FieldProfileNonOverrideableException();
            }
        } // requesting org doesn't originate the fieldProfile's workflow step, and the workflow step is non-overrideable
        else {
            throw new WorkflowStepNonOverrideableException();
        }
    }
    
    public FieldProfile update(FieldProfile pendingFieldProfile, Organization requestingOrganization) throws FieldProfileNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        FieldProfile resultingFieldProfile = null;

        FieldProfile persistedFieldProfile = fieldProfileRepo.findOne(pendingFieldProfile.getId());

        boolean overridabilityOfPersistedFieldProfile = persistedFieldProfile.getOverrideable();

        boolean overridabilityOfOriginatingWorkflowStep = persistedFieldProfile.getOriginatingWorkflowStep().getOverrideable();

        // The ws that has the fieldProfile on the requesting org
        WorkflowStep workflowStepWithFieldProfileOnRequestingOrganization = null;

        // Did the requesting organization originate the workflow step that the fieldProfile is on?
        boolean requestingOrganizationOriginatedWorkflowStep = false;

        // Is the workflow step on which the fieldProfile is found on the requesting organization the workflow step that originates the fieldProfile?
        boolean workflowStepOriginatesFieldProfile = false;

        for (WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {
            if (workflowStep.getAggregateFieldProfiles().contains(persistedFieldProfile)) {
                workflowStepWithFieldProfileOnRequestingOrganization = workflowStep;
                requestingOrganizationOriginatedWorkflowStep = workflowStepWithFieldProfileOnRequestingOrganization.getOriginatingOrganization().getId().equals(requestingOrganization.getId());
                break;
            }
        }

        // A workflow step that has the fieldProfile on it was found on the requesting organization
        if (workflowStepWithFieldProfileOnRequestingOrganization != null) {
            workflowStepOriginatesFieldProfile = persistedFieldProfile.getOriginatingWorkflowStep().getId().equals(workflowStepWithFieldProfileOnRequestingOrganization.getId());
        } else {
            // The requesting org doesn't even have this fieldProfile anywhere!
            throw new ComponentNotPresentOnOrgException();
        }

        if (!overridabilityOfOriginatingWorkflowStep && !requestingOrganizationOriginatedWorkflowStep) {
            throw new WorkflowStepNonOverrideableException();
        }

        if (!overridabilityOfPersistedFieldProfile && !(workflowStepOriginatesFieldProfile && requestingOrganizationOriginatedWorkflowStep)) {
            throw new FieldProfileNonOverrideableException();
        }

        // If the requesting org originates the WS, then we don't need to make a new one
        if (requestingOrganizationOriginatedWorkflowStep) {
            // If the WS originates the FieldProfile, we don't need a new one
            if (workflowStepOriginatesFieldProfile) {

                // update fieldProfile directly
                resultingFieldProfile = fieldProfileRepo.save(pendingFieldProfile);

                // if change to non-overrideable, replace descendants of this fieldProfile in subordinate orgs and put it back on ones that deleted it
                if (overridabilityOfPersistedFieldProfile && !resultingFieldProfile.getOverrideable()) {
                    reInheritDescendantsOfFieldProfileWithAnotherFieldProfileUnderWS(persistedFieldProfile, resultingFieldProfile, workflowStepWithFieldProfileOnRequestingOrganization, requestingOrganization);
                }
            }
            // If the WS didn't originate the FieldProfile, we need a new FieldProfile to replace it
            else {

                // new fieldProfile
                em.detach(pendingFieldProfile);
                pendingFieldProfile.setOriginatingFieldProfile(persistedFieldProfile);
                pendingFieldProfile.setId(null);
                pendingFieldProfile.setOriginatingWorkflowStep(workflowStepWithFieldProfileOnRequestingOrganization);
                FieldProfile newFieldProfile = fieldProfileRepo.save(pendingFieldProfile);

                // replace descendants of the persisted (original) FieldProfile with our new FieldProfile at subordinate organizations
                // replace the fieldProfile on all descendant orgs aggregate workflows
                for (WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, persistedFieldProfile)) {
                    workflowStep.replaceAggregateFieldProfile(persistedFieldProfile, newFieldProfile);
                    workflowStepRepo.save(workflowStep);
                }

                // if change to non-overrideable, replace descendants of originating fieldProfile in subordinate orgs
                if (overridabilityOfPersistedFieldProfile && !newFieldProfile.getOverrideable()) {
                    reInheritDescendantsOfFieldProfileWithAnotherFieldProfileUnderWS(persistedFieldProfile, newFieldProfile, workflowStepWithFieldProfileOnRequestingOrganization, requestingOrganization);
                }
            }
        }
        // If the requesting org didn't originate the WS, we need a new WS to replace it and to originate a new FieldProfile
        // workflowStepWithFieldProfileOnRequestingOrganization does not originate on the requesting org
        else {

            // make the new step; the update call will propagate step replacement in subordinate orgs
            Long origWSId = workflowStepWithFieldProfileOnRequestingOrganization.getId();
            workflowStepWithFieldProfileOnRequestingOrganization.setOriginatingWorkflowStep(workflowStepRepo.findOne(origWSId));

            WorkflowStep newOriginatingWorkflowStep = workflowStepRepo.update(workflowStepWithFieldProfileOnRequestingOrganization, requestingOrganization);
            
            workflowStepWithFieldProfileOnRequestingOrganization = workflowStepRepo.findOne(origWSId);
            requestingOrganization = organizationRepo.findOne(requestingOrganization.getId());

            // new FieldProfile on the new WS
            em.detach(pendingFieldProfile);
            pendingFieldProfile.setId(null);
            pendingFieldProfile.setOriginatingFieldProfile(persistedFieldProfile);
            pendingFieldProfile.setOriginatingWorkflowStep(newOriginatingWorkflowStep);
            FieldProfile newFieldProfile = fieldProfileRepo.save(pendingFieldProfile);
            newOriginatingWorkflowStep.getOriginalFieldProfiles().add(newFieldProfile);
            newOriginatingWorkflowStep.replaceAggregateFieldProfile(persistedFieldProfile, newFieldProfile);
            newOriginatingWorkflowStep = workflowStepRepo.save(newOriginatingWorkflowStep);

            requestingOrganization.replaceAggregateWorkflowStep(workflowStepWithFieldProfileOnRequestingOrganization, newOriginatingWorkflowStep);
            requestingOrganization = organizationRepo.save(requestingOrganization);

            // replace the fieldProfile on all descendant orgs aggregate workflows
            for (WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, persistedFieldProfile)) {
                workflowStep.replaceAggregateFieldProfile(persistedFieldProfile, newFieldProfile);
                workflowStepRepo.save(workflowStep);
            }

            // if parent organization's workflow step updates a fieldProfile originating form a descendent, the original fieldProfile needs to be deleted
            if (workflowStepRepo.findByAggregateFieldProfilesId(persistedFieldProfile.getId()).size() == 0) {
                fieldProfileRepo.delete(persistedFieldProfile);
            } else {
                newFieldProfile.setOriginatingFieldProfile(persistedFieldProfile);
                newFieldProfile = fieldProfileRepo.save(newFieldProfile);
            }

            // if change to non-overrideable, replace descendants of originating fieldProfile in subordinate orgs
            if (overridabilityOfPersistedFieldProfile && !newFieldProfile.getOverrideable()) {
                reInheritDescendantsOfFieldProfileWithAnotherFieldProfileUnderWS(persistedFieldProfile, newFieldProfile, workflowStepWithFieldProfileOnRequestingOrganization, requestingOrganization);
            }

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
    
    /**
     * Gets a list of WorkflowSteps on the org and its descendants that contain a given fieldProfile
     * 
     * @param organization
     * @param fieldProfile
     * @return
     */
    private List<WorkflowStep> getContainingDescendantWorkflowStep(Organization organization, FieldProfile fieldProfile) {
        List<WorkflowStep> descendantWorkflowStepsContainingFieldProfile = new ArrayList<WorkflowStep>();
        organization.getAggregateWorkflowSteps().forEach(ws -> {
            if (ws.getAggregateFieldProfiles().contains(fieldProfile)) {
                descendantWorkflowStepsContainingFieldProfile.add(ws);
            }
        });
        organization.getChildrenOrganizations().forEach(descendantOrganization -> {
            descendantWorkflowStepsContainingFieldProfile.addAll(getContainingDescendantWorkflowStep(descendantOrganization, fieldProfile));
        });
        return descendantWorkflowStepsContainingFieldProfile;
    }

    // TODO: same logic here as in WorkflowStepRepoImpl.getDescendantsOfStep
    private List<FieldProfile> getDescendantsOfFieldProfile(FieldProfile fieldProfile) {
        List<FieldProfile> descendantFieldProfiles = new ArrayList<FieldProfile>();
        List<FieldProfile> currentDescendants = fieldProfileRepo.findByOriginatingFieldProfile(fieldProfile);
        descendantFieldProfiles.addAll(currentDescendants);
        currentDescendants.forEach(descendantFieldProfile -> {
            descendantFieldProfiles.addAll(getDescendantsOfFieldProfile(descendantFieldProfile));
        });
        return descendantFieldProfiles;
    }

    /**
     * Have all the fieldProfiles (in workflow steps descended from a given step) that are derived from a particular ancestor fieldProfile be replaced with a replacement fieldProfile (which could also be just that ancestor fieldProfile)
     * 
     */

    private void reInheritDescendantsOfFieldProfileWithAnotherFieldProfileUnderWS(FieldProfile ancestorFieldProfile, FieldProfile replacementFieldProfile, WorkflowStep workflowStepWithFieldProfileOnRequestingOrganization, Organization requestingOrganization) {
        // First off, fieldProfile the FieldProfiles that descend from the ancestor fieldProfile
        List<FieldProfile> descendantFieldProfiles = getDescendantsOfFieldProfile(ancestorFieldProfile);

        // For every workflow step derived off the step in question...
        // for(WorkflowStep ws : workflowStepRepo.getDescendantsOfStep(workflowStepWithFieldProfileOnRequestingOrganization)) {
        List<FieldProfile> fieldProfilesToDelete = new ArrayList<FieldProfile>();

        for (WorkflowStep ws : workflowStepRepo.getDescendantsOfStepUnderOrganization(workflowStepWithFieldProfileOnRequestingOrganization, requestingOrganization)) {
        
            boolean aggregatesFieldProfileOrDescendant = ws.getAggregateFieldProfiles().contains(replacementFieldProfile);
            // For every fieldProfile on that step (the aggregates will include the originals)
            List<FieldProfile> copyOfAggregatedFieldProfiles = new ArrayList<FieldProfile>();
            copyOfAggregatedFieldProfiles.addAll(ws.getAggregateFieldProfiles());

            for (FieldProfile n : copyOfAggregatedFieldProfiles) {
                // If that fieldProfile is a descendant of the fieldProfile in question, replace it with the fieldProfile in question and get rid of it
                if (descendantFieldProfiles.contains(n) && !replacementFieldProfile.equals(n)) {
                    if (ws.replaceAggregateFieldProfile(n, replacementFieldProfile)) {
                        ws.removeOriginalFieldProfile(n);
                        workflowStepRepo.save(ws);
                        fieldProfilesToDelete.add(n);
                        ws = workflowStepRepo.findOne(ws.getId());
                        aggregatesFieldProfileOrDescendant = true;
                    }
                }
            }

            // If the fieldProfile was not found on the aggregates at all, then add it back in
            if (!aggregatesFieldProfileOrDescendant) {
                ws.addAggregateFieldProfile(replacementFieldProfile);
                workflowStepRepo.save(ws);
            }
        }
        for (FieldProfile n : fieldProfilesToDelete) {
            delete(n);
        }
    }
    
}
