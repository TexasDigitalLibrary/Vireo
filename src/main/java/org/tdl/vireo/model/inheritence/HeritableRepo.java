package org.tdl.vireo.model.inheritence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.HeritableModelNonOverrideableException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

@SuppressWarnings("rawtypes")
public class HeritableRepo<M extends Heritable, R extends HeritableJpaRepo<M>> {

    @Autowired
    private R heritableRepo;

    @Autowired
    private WorkflowStepRepo workflowStepRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    public void removeFromWorkflowStep(Organization requestingOrganization, WorkflowStep pendingWorkflowStep, M heritableModelToRemove) throws WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {

        // if requesting organization originates the workflow step or the workflow step is overrideable,
        if (pendingWorkflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId()) || pendingWorkflowStep.getOverrideable()) {
            // ... and if also that workflow step originates the heritableModel or the heritableModel is overrideable,
            if (heritableModelToRemove.getOriginatingWorkflowStep().getId().equals(heritableModelToRemove.getId()) || heritableModelToRemove.getOverrideable()) {
                // ...then the update is permissible.

                // if requesting organization is not the workflow step's orignating organization,
                if (!pendingWorkflowStep.getOriginatingOrganization().getId().equals(requestingOrganization.getId())) {
                    // create a new workflow step
                    pendingWorkflowStep = workflowStepRepo.update(pendingWorkflowStep, requestingOrganization);

                    // recursive call
                    pendingWorkflowStep.removeAggregateHeritableModel(heritableModelToRemove);

                    workflowStepRepo.save(pendingWorkflowStep);
                }
                // else, requesting organization originates the workflow step
                else {

                    List<WorkflowStep> workflowStepsContainingHeritableModel = getContainingDescendantWorkflowStep(requestingOrganization, heritableModelToRemove);

                    if (workflowStepsContainingHeritableModel.size() > 0) {

                        boolean foundNewOriginalOwner = false;

                        for (WorkflowStep workflowStepContainingHeritableModel : workflowStepsContainingHeritableModel) {
                            // add heritableModel as original to first workflow step
                            if (!foundNewOriginalOwner) {
                                workflowStepContainingHeritableModel.addOriginalHeritableModel(heritableModelToRemove);
                                foundNewOriginalOwner = true;
                            } else {
                                workflowStepContainingHeritableModel.addAggregateHeritableModel(heritableModelToRemove);
                            }
                            workflowStepRepo.save(workflowStepContainingHeritableModel);
                        }

                        pendingWorkflowStep.removeOriginalHeritableModel(heritableModelToRemove);

                        workflowStepRepo.save(pendingWorkflowStep);

                    } else {
                        heritableRepo.delete(heritableModelToRemove);
                    }
                }
            } // workflow step doesn't originate the heritableModel and it is non-overrideable
            else {
                throw new HeritableModelNonOverrideableException();
            }
        } // requesting org doesn't originate the heritableModel's workflow step, and the workflow step is non-overrideable
        else {
            throw new WorkflowStepNonOverrideableException();
        }
    }

    @SuppressWarnings("unchecked")
    public M update(M pendingHeritableModel, Organization requestingOrganization) throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        M resultingHeritableModel = null;

        Long phmId = pendingHeritableModel.getId();

        M persistedHeritableModel = heritableRepo.findOne(phmId);

        boolean overridabilityOfPersistedHeritableModel = persistedHeritableModel.getOverrideable();

        boolean overridabilityOfOriginatingWorkflowStep = persistedHeritableModel.getOriginatingWorkflowStep().getOverrideable();

        // The ws that has the heritableModel on the requesting org
        WorkflowStep workflowStepWithHeritableModelOnRequestingOrganization = null;

        // Did the requesting organization originate the workflow step that the heritableModel is on?
        boolean requestingOrganizationOriginatedWorkflowStep = false;

        // Is the workflow step on which the heritableModel is found on the requesting organization the workflow step that originates the heritableModel?
        boolean workflowStepOriginatesHeritableModel = false;

        for (WorkflowStep workflowStep : requestingOrganization.getAggregateWorkflowSteps()) {
            if (workflowStep.getAggregateHeritableModels(persistedHeritableModel).contains(persistedHeritableModel)) {
                workflowStepWithHeritableModelOnRequestingOrganization = workflowStep;
                requestingOrganizationOriginatedWorkflowStep = workflowStepWithHeritableModelOnRequestingOrganization.getOriginatingOrganization().getId().equals(requestingOrganization.getId());
            }
        }

        // A workflow step that has the heritableModel on it was found on the requesting organization
        if (workflowStepWithHeritableModelOnRequestingOrganization != null) {
            workflowStepOriginatesHeritableModel = persistedHeritableModel.getOriginatingWorkflowStep().getId().equals(workflowStepWithHeritableModelOnRequestingOrganization.getId());
        } else {
            // The requesting org doesn't even have this heritableModel anywhere!
            throw new ComponentNotPresentOnOrgException();
        }

        if (!overridabilityOfOriginatingWorkflowStep && !requestingOrganizationOriginatedWorkflowStep) {
            throw new WorkflowStepNonOverrideableException();
        }

        if (!overridabilityOfPersistedHeritableModel && !(workflowStepOriginatesHeritableModel && requestingOrganizationOriginatedWorkflowStep)) {
            throw new HeritableModelNonOverrideableException();
        }

        // If the requesting org originates the WS, then we don't need to make a new one
        if (requestingOrganizationOriginatedWorkflowStep) {

            // If the WS originates the M, we don't need a new one
            if (workflowStepOriginatesHeritableModel) {

                // update heritableModel directly
                resultingHeritableModel = heritableRepo.save(pendingHeritableModel);

                // if change to non-overrideable, replace descendants of this heritableModel in subordinate orgs and put it back on ones that deleted it
                if (overridabilityOfPersistedHeritableModel && !resultingHeritableModel.getOverrideable()) {
                    reInheritDescendantsOfHeritableModelWithAnotherHeritableModelUnderWS(persistedHeritableModel, resultingHeritableModel, workflowStepWithHeritableModelOnRequestingOrganization, requestingOrganization);
                }
            }
            // If the WS didn't originate the M, we need a new M to replace it
            else {

                M cloneHeritableModel = (M) pendingHeritableModel.clone();

                cloneHeritableModel.setOriginating(persistedHeritableModel);
                cloneHeritableModel.setOriginatingWorkflowStep(workflowStepWithHeritableModelOnRequestingOrganization);

                M newHeritableModel = heritableRepo.save(cloneHeritableModel);

                requestingOrganization = organizationRepo.findOne(requestingOrganization.getId());

                // replace descendants of the persisted (original) M with our new M at subordinate organizations
                // replace the heritableModel on all descendant orgs aggregate workflows
                for (WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, persistedHeritableModel)) {
                    workflowStep.replaceAggregateHeritableModel(persistedHeritableModel, newHeritableModel);
                    workflowStepRepo.save(workflowStep);
                }

                // if change to non-overrideable, replace descendants of originating heritableModel in subordinate orgs
                if (overridabilityOfPersistedHeritableModel && !newHeritableModel.getOverrideable()) {
                    reInheritDescendantsOfHeritableModelWithAnotherHeritableModelUnderWS(persistedHeritableModel, newHeritableModel, workflowStepWithHeritableModelOnRequestingOrganization, requestingOrganization);
                }

                resultingHeritableModel = newHeritableModel;
            }
        }
        // If the requesting org didn't originate the WS, we need a new WS to replace it and to originate a new M
        // workflowStepWithHeritableModelOnRequestingOrganization does not originate on the requesting org
        else {

            WorkflowStep newOriginatingWorkflowStep = workflowStepRepo.update(workflowStepWithHeritableModelOnRequestingOrganization, requestingOrganization);

            M cloneHeritableModel = (M) pendingHeritableModel.clone();

            cloneHeritableModel.setOriginating(persistedHeritableModel);
            cloneHeritableModel.setOriginatingWorkflowStep(newOriginatingWorkflowStep);

            M newHeritableModel = heritableRepo.save(cloneHeritableModel);

            newOriginatingWorkflowStep = workflowStepRepo.findOne(newOriginatingWorkflowStep.getId());

            newOriginatingWorkflowStep.getOriginalHeritableModels(newHeritableModel).add(newHeritableModel);

            newOriginatingWorkflowStep.replaceAggregateHeritableModel(persistedHeritableModel, newHeritableModel);

            newOriginatingWorkflowStep = workflowStepRepo.save(newOriginatingWorkflowStep);

            workflowStepWithHeritableModelOnRequestingOrganization = workflowStepRepo.findOne(workflowStepWithHeritableModelOnRequestingOrganization.getId());

            requestingOrganization.replaceAggregateWorkflowStep(workflowStepWithHeritableModelOnRequestingOrganization, newOriginatingWorkflowStep);

            requestingOrganization = organizationRepo.save(requestingOrganization);

            // replace the heritableModel on all descendant orgs aggregate workflows
            for (WorkflowStep workflowStep : getContainingDescendantWorkflowStep(requestingOrganization, persistedHeritableModel)) {
                workflowStep.replaceAggregateHeritableModel(persistedHeritableModel, newHeritableModel);
                workflowStepRepo.save(workflowStep);
            }

            // if change to non-overrideable, replace descendants of originating heritableModel in subordinate orgs
            if (overridabilityOfPersistedHeritableModel && !newHeritableModel.getOverrideable()) {
                reInheritDescendantsOfHeritableModelWithAnotherHeritableModelUnderWS(persistedHeritableModel, newHeritableModel, workflowStepWithHeritableModelOnRequestingOrganization, requestingOrganization);
            }

            resultingHeritableModel = newHeritableModel;
        }

        return resultingHeritableModel;

    }

    @SuppressWarnings("unchecked")
    public void delete(M heritableModel) {

        // allows for delete by iterating through findAll, while still deleting descendents
        if (heritableRepo.findOne(heritableModel.getId()) != null) {

            WorkflowStep originatingWorkflowStep = heritableModel.getOriginatingWorkflowStep();

            originatingWorkflowStep.removeOriginalHeritableModel(heritableModel);

            if (heritableModel.getOriginating() != null) {
                heritableModel.setOriginating(null);
            }

            workflowStepRepo.findByAggregateHeritableModel(heritableModel).forEach(workflowStep -> {
                workflowStep.removeAggregateHeritableModel(heritableModel);
                workflowStepRepo.save(workflowStep);
            });

            heritableRepo.findByOriginating(heritableModel).forEach(fp -> {
                fp.setOriginating(null);
            });

            deleteDescendantsOfHeritableModel(heritableModel);

            heritableRepo.delete(heritableModel.getId());

        }
    }

    private void deleteDescendantsOfHeritableModel(M heritableModel) {
        heritableRepo.findByOriginating(heritableModel).forEach(desendantHeritableModel -> {
            delete(desendantHeritableModel);
        });
    }

    /**
     * Gets a list of WorkflowSteps on the org and its descendants that contain a given heritableModel
     *
     * @param organization
     * @param heritableModel
     * @return
     */
    private List<WorkflowStep> getContainingDescendantWorkflowStep(Organization organization, M heritableModel) {
        List<WorkflowStep> descendantWorkflowStepsContainingHeritableModel = new ArrayList<WorkflowStep>();
        organization.getAggregateWorkflowSteps().forEach(ws -> {
            if (ws.getAggregateHeritableModels(heritableModel).contains(heritableModel)) {
                descendantWorkflowStepsContainingHeritableModel.add(ws);
            }
        });
        organization.getChildrenOrganizations().forEach(descendantOrganization -> {
            descendantWorkflowStepsContainingHeritableModel.addAll(getContainingDescendantWorkflowStep(descendantOrganization, heritableModel));
        });
        return descendantWorkflowStepsContainingHeritableModel;
    }

    // TODO: same logic here as in WorkflowStepRepoImpl.getDescendantsOfStep
    private List<M> getDescendantsOfHeritableModel(M heritableModel) {
        List<M> descendantHeritableModels = new ArrayList<M>();
        List<M> currentDescendants = heritableRepo.findByOriginating(heritableModel);
        descendantHeritableModels.addAll(currentDescendants);
        currentDescendants.forEach(descendantHeritableModel -> {
            descendantHeritableModels.addAll(getDescendantsOfHeritableModel(descendantHeritableModel));
        });
        return descendantHeritableModels;
    }

    /**
     * Have all the heritableModels (in workflow steps descended from a given step) that are derived from a particular ancestor heritableModel be replaced with a replacement heritableModel (which could also be just that ancestor heritableModel)
     *
     */

    @SuppressWarnings("unchecked")
    private void reInheritDescendantsOfHeritableModelWithAnotherHeritableModelUnderWS(M ancestorHeritableModel, M replacementHeritableModel, WorkflowStep workflowStepWithHeritableModelOnRequestingOrganization, Organization requestingOrganization) {

        // First off, heritableModel the HeritableModels that descend from the ancestor heritableModel
        List<M> descendantHeritableModels = getDescendantsOfHeritableModel(ancestorHeritableModel);

        // For every workflow step derived off the step in question...

        // for(WorkflowStep ws : workflowStepRepo.getDescendantsOfStep(workflowStepWithHeritableModelOnRequestingOrganization)) {
        List<M> heritableModelsToDelete = new ArrayList<M>();

        for (WorkflowStep ws : workflowStepRepo.getDescendantsOfStepUnderOrganization(workflowStepWithHeritableModelOnRequestingOrganization, requestingOrganization)) {

            boolean aggregatesHeritableModelOrDescendant = ws.getAggregateHeritableModels(replacementHeritableModel).contains(replacementHeritableModel);

            // For every heritableModel on that step (the aggregates will include the originals)
            List<M> copyOfAggregatedHeritableModels = new ArrayList<M>();

            copyOfAggregatedHeritableModels.addAll(ws.getAggregateHeritableModels(replacementHeritableModel));

            for (M n : copyOfAggregatedHeritableModels) {

                // If that heritableModel is a descendant of the heritableModel in question, replace it with the heritableModel in question and get rid of it
                if (descendantHeritableModels.contains(n) && !replacementHeritableModel.equals(n)) {

                    if (ws.replaceAggregateHeritableModel(n, replacementHeritableModel)) {
                        ws.removeOriginalHeritableModel(n);
                        heritableModelsToDelete.add(n);
                        aggregatesHeritableModelOrDescendant = true;
                    }
                }
            }

            // If the heritableModel was not found on the aggregates at all, then add it back in
            if (!aggregatesHeritableModelOrDescendant) {
                ws.addAggregateHeritableModel(replacementHeritableModel);
            }

            workflowStepRepo.save(ws);
        }

        for (M n : heritableModelsToDelete) {
            delete(n);
        }
    }

}
