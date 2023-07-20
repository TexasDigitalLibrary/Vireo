package org.tdl.vireo.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;

public class WorkflowStepRepoTest extends AbstractRepoTest {

    @BeforeEach
    public void setup() {
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();
        inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);
    }

    @Override
    @Test
    public void testCreate() {
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, Boolean.valueOf(false));
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        assertEquals(1, workflowStepRepo.count(), "The repository did not save the entity!");
        assertEquals(TEST_WORKFLOW_STEP_NAME, workflowStep.getName(), "Saved entity did not contain the name!");
        assertEquals(inputType, fieldProfile.getInputType(), "The field profile did not contain the correct value!");
        assertEquals(TEST_FIELD_PROFILE_USAGE, fieldProfile.getUsage(), "The field profile did not contain the correct value!");
        assertEquals(TEST_GLOSS, fieldProfile.getGloss(), "The field profile did not contain the correct value!");
        assertEquals(TEST_FIELD_PROFILE_REPEATABLE, fieldProfile.getRepeatable(), "The field profile did not contain the correct value!");
        assertEquals(TEST_FIELD_PROFILE_OVERRIDEABLE, fieldProfile.getOverrideable(), "The field profile did not contain the correct value!");
        assertEquals(TEST_FIELD_PROFILE_ENABLED, fieldProfile.getEnabled(), "The field profile did not contain the correct value!");
        assertEquals(TEST_FIELD_PROFILE_OPTIONAL, fieldProfile.getOptional(), "The field profile did not contain the correct value!");
        assertEquals(fieldPredicate, workflowStep.getFieldProfileByPredicate(fieldPredicate).getFieldPredicate(), "Saved entity did not contain the field profile field predicate value!");
    }

    @Override
    @Test
    public void testDelete() {
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        workflowStepRepo.delete(workflowStep);
        assertEquals(0, workflowStepRepo.count(), "Entity did not delete!");
    }

    @Override
    @Test
    public void testDuplication() {
        workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        try {
            workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
            assertTrue(false);
        } catch (Exception e) {
            // good
        }
    }

    @Override
    @Test
    public void testCascade() {
        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.findById(organization.getId()).get();

        Note note = noteRepo.create(workflowStep, TEST_NOTE_NAME, TEST_NOTE_TEXT);
        workflowStep = workflowStepRepo.findById(workflowStep.getId()).get();
        Note noteToDisassociate = noteRepo.create(workflowStep, TEST_SEVERABLE_NOTE_NAME, TEST_SEVERABLE_NOTE_TEXT);
        workflowStep = workflowStepRepo.findById(workflowStep.getId()).get();

        FieldPredicate fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, Boolean.valueOf(false));
        FieldPredicate fieldPredicateToDisassociate = fieldPredicateRepo.create(TEST_SEVERABLE_FIELD_PREDICATE_VALUE, Boolean.valueOf(false));

        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findById(workflowStep.getId()).get();

        FieldProfile fieldProfileToDisassociate = fieldProfileRepo.create(workflowStep, fieldPredicateToDisassociate, inputType, TEST_SEVERABLE_FIELD_PROFILE_USAGE, TEST_SEVERABLE_GLOSS, TEST_SEVERABLE_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_SEVERABLE_FIELD_PROFILE_ENABLED, TEST_SEVERABLE_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findById(workflowStep.getId()).get();

        workflowStep.addOriginalNote(note);
        workflowStep.addOriginalNote(noteToDisassociate);

        try {
            workflowStep = workflowStepRepo.update(workflowStep, organization);
        } catch (WorkflowStepNonOverrideableException | ComponentNotPresentOnOrgException e) {
            e.printStackTrace();

            assertTrue(false, "Could not update workflow step");
        }

        organization = organizationRepo.findById(organization.getId()).get();

        // check number of field profiles
        assertEquals(2, workflowStep.getOriginalFieldProfiles().size(), "Saved entity did not contain the correct number of field profiles!");
        assertEquals(2, fieldProfileRepo.count(), "WorkflowStep repo does not have the correct number of field profiles");

        // check number of notes
        assertEquals(2, workflowStep.getOriginalNotes().size(), "WorkflowStep repo does not have the correct number of notes");

        // check number of field predicates
        assertEquals(2, workflowStep.getOriginalFieldProfiles().size(), "WorkflowStep repo does not have the correct number of field profiles");

        // verify field profiles
        assertEquals(TEST_FIELD_PROFILE_REPEATABLE, workflowStep.getFieldProfileByPredicate(fieldPredicate).getRepeatable(), "Saved entity did not contain the field profile repeatable value!");
        assertEquals(TEST_FIELD_PROFILE_ENABLED, fieldProfile.getEnabled(), "Saved entity did not contain the field profile enabled value!");
        assertEquals(TEST_FIELD_PROFILE_OPTIONAL, fieldProfile.getOptional(), "Saved entity did not contain the field profile optional value!");
        assertEquals(inputType, workflowStep.getFieldProfileByPredicate(fieldPredicate).getInputType(), "Saved entity did not contain the field profile input type!");
        assertEquals(TEST_SEVERABLE_FIELD_PROFILE_USAGE, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getUsage(), "Saved entity did not contain the field profile repeatable value!");
        assertEquals(TEST_SEVERABLE_GLOSS, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getGloss(), "Saved entity did not contain the field profile repeatable value!");
        assertEquals(TEST_SEVERABLE_FIELD_PROFILE_REPEATABLE, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getRepeatable(), "Saved entity did not contain the field profile repeatable value!");
        assertEquals(TEST_SEVERABLE_FIELD_PROFILE_ENABLED, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getEnabled(), "Saved entity did not contain the field profile required value!");
        assertEquals(TEST_SEVERABLE_FIELD_PROFILE_OPTIONAL, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getOptional(), "Saved entity did not contain the field profile required value!");
        assertEquals(inputType, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getInputType(), "Saved entity did not contain the field profile input type!");

        // verify field predicates
        assertEquals(fieldPredicate, workflowStep.getFieldProfileByPredicate(fieldPredicate).getFieldPredicate(), "Saved entity did not contain the field profile field predicate value!");
        assertEquals(fieldPredicateToDisassociate, workflowStep.getFieldProfileByPredicate(fieldPredicateToDisassociate).getFieldPredicate(), "Saved entity did not contain the field profile field predicate value!");

        // test remove field profile from workflowStep
        workflowStep.removeOriginalFieldProfile(fieldProfileToDisassociate);

        try {
            workflowStep = workflowStepRepo.update(workflowStep, organization);
        } catch (WorkflowStepNonOverrideableException | ComponentNotPresentOnOrgException e) {
            e.printStackTrace();

            assertTrue(false, "Could not update workflow step");
        }

        organization = organizationRepo.findById(organization.getId()).get();

        // the field profile should no longer be on the workflow step, and it
        // should be deleted since it was orphaned
        assertEquals(false, workflowStep.getOriginalFieldProfiles().contains(fieldProfileToDisassociate), "The field profile was not removed!");
        assertEquals(2, fieldProfileRepo.count(), "The field profile was deleted!");

        // test remove note from workflow step
        workflowStep.removeOriginalNote(noteToDisassociate);

        long noteCount = noteRepo.count();

        try {
            workflowStep = workflowStepRepo.update(workflowStep, organization);
        } catch (WorkflowStepNonOverrideableException | ComponentNotPresentOnOrgException e) {
            e.printStackTrace();

            assertTrue(false, "Could not update workflow step");
        }

        // the note should no longer be on the workflow step, but it should not
        // be deleted
        assertEquals(1, workflowStep.getOriginalNotes().size(), "The note was not removed!");

        assertEquals(noteCount, noteRepo.count(), "The note was deleted!");

        // test delete workflow step
        workflowStepRepo.delete(workflowStep);

        // assert workflow step was deleted
        assertEquals(0, workflowStepRepo.count(), "The workflow step was not deleted!");

        assertEquals(0, fieldProfileRepo.count(), "The field profiles originating in this workflow step were orphaned!");

        assertEquals(0, noteRepo.count(), "The notes originating in this workflow step were orphaned!");

        assertEquals(2, fieldPredicateRepo.count(), "The field predicates were deleted!");
    }

    @Test
    public void testWorkFlowStepDefaultEmptyInit() {
        Organization org = organizationRepo.create("testOrg", parentCategory);
        assertEquals(0, org.getOriginalWorkflowSteps().size(), "A newly created organization should have no workflow steps");
        assertEquals(0, org.getAggregateWorkflowSteps().size(), "A newly created organization should have empty workflow");
    }

    @Test
    public void testWorkFlowStepAppend() {
        Organization organization = organizationRepo.create("testOrg", parentCategory);
        workflowStepRepo.create("first step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(1, organization.getOriginalWorkflowSteps().size(), "The organization should have one step");
        assertEquals(1, organization.getAggregateWorkflowSteps().size(), "The organization should have one step in workflow");

        workflowStepRepo.create("second step", organization);
        assertEquals(2, organization.getOriginalWorkflowSteps().size(), "The organization should have one step");
        assertEquals(2, organization.getAggregateWorkflowSteps().size(), "The organization should have one step in workflow");
    }

    @Test
    public void testWorkFlowStepAppendAtIndexSuccess() {
        workflowStepRepo.create("first step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(1, organization.getOriginalWorkflowSteps().size(), "The org should have 1 workflow steps.");

        workflowStepRepo.create("second step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(2, organization.getOriginalWorkflowSteps().size(), "The org should have 2 workflow steps.");

        workflowStepRepo.create("third step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(3, organization.getOriginalWorkflowSteps().size(), "The org should have 3 workflow steps.");

        workflowStepRepo.create("fourth step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(4, organization.getOriginalWorkflowSteps().size(), "The org should have 4 workflow steps.");

        workflowStepRepo.create("fifth step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(5, organization.getOriginalWorkflowSteps().size(), "The org should have 5 workflow steps.");
    }

    @Test
    public void testWorkFlowOrderRecordsCorrectly() {
        WorkflowStep ws1 = workflowStepRepo.create("first step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(1, organization.getOriginalWorkflowSteps().size(), "The org should have 1 workflow steps.");

        WorkflowStep ws2 = workflowStepRepo.create("second step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(2, organization.getOriginalWorkflowSteps().size(), "The org should have 2 workflow steps.");

        WorkflowStep ws3 = workflowStepRepo.create("third step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(3, organization.getOriginalWorkflowSteps().size(), "The org should have 3 workflow steps.");

        WorkflowStep ws4 = workflowStepRepo.create("fourth step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(4, organization.getOriginalWorkflowSteps().size(), "The org should have 4 workflow steps.");

        WorkflowStep ws5 = workflowStepRepo.create("fifth step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(5, organization.getOriginalWorkflowSteps().size(), "The org should have 5 workflow steps.");

        assertEquals(ws1.getId(), organization.getAggregateWorkflowSteps().get(0).getId(), "Step 1 did not appear in position 1!");
        assertEquals(ws2.getId(), organization.getAggregateWorkflowSteps().get(1).getId(), "Step 2 did not appear in position 2!");
        assertEquals(ws3.getId(), organization.getAggregateWorkflowSteps().get(2).getId(), "Step 3 did not appear in position 3!");
        assertEquals(ws4.getId(), organization.getAggregateWorkflowSteps().get(3).getId(), "Step 4 did not appear in position 4!");
        assertEquals(ws5.getId(), organization.getAggregateWorkflowSteps().get(4).getId(), "Step 5 did not appear in position 5!");
    }

    @Test
    public void testInheritWorkflowInCorrectOrder() {

        WorkflowStep ws1 = workflowStepRepo.create("first step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(1, organization.getOriginalWorkflowSteps().size(), "The organization should have 1 workflow steps.");

        WorkflowStep ws2 = workflowStepRepo.create("second step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(2, organization.getOriginalWorkflowSteps().size(), "The organization should have 2 workflow steps.");

        WorkflowStep ws3 = workflowStepRepo.create("third step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(3, organization.getOriginalWorkflowSteps().size(), "The organization should have 3 workflow steps.");

        WorkflowStep ws4 = workflowStepRepo.create("fourth step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(4, organization.getOriginalWorkflowSteps().size(), "The organization should have 4 workflow steps.");

        WorkflowStep ws5 = workflowStepRepo.create("fifth step", organization);
        organization = organizationRepo.findById(organization.getId()).get();
        assertEquals(5, organization.getOriginalWorkflowSteps().size(), "The organization should have 5 workflow steps.");

        assertEquals(5, organization.getAggregateWorkflowSteps().size(), "Organization workflow was the wrong length!");
        assertEquals(ws1.getId(), organization.getAggregateWorkflowSteps().get(0).getId(), "Step 1 did not appear in position 1!");
        assertEquals(ws2.getId(), organization.getAggregateWorkflowSteps().get(1).getId(), "Step 2 did not appear in position 2!");
        assertEquals(ws3.getId(), organization.getAggregateWorkflowSteps().get(2).getId(), "Step 3 did not appear in position 3!");
        assertEquals(ws4.getId(), organization.getAggregateWorkflowSteps().get(3).getId(), "Step 4 did not appear in position 4!");
        assertEquals(ws5.getId(), organization.getAggregateWorkflowSteps().get(4).getId(), "Step 5 did not appear in position 5!");

        Organization childOrg = organizationRepo.create("Child Organization", organization, parentCategory);
        assertEquals(5, childOrg.getAggregateWorkflowSteps().size(), "Child organization workflow was the wrong length!");
        assertEquals(ws1.getId(), childOrg.getAggregateWorkflowSteps().get(0).getId(), "Step 1 did not appear in position 1!");
        assertEquals(ws2.getId(), childOrg.getAggregateWorkflowSteps().get(1).getId(), "Step 2 did not appear in position 2!");
        assertEquals(ws3.getId(), childOrg.getAggregateWorkflowSteps().get(2).getId(), "Step 3 did not appear in position 3!");
        assertEquals(ws4.getId(), childOrg.getAggregateWorkflowSteps().get(3).getId(), "Step 4 did not appear in position 4!");
        assertEquals(ws5.getId(), childOrg.getAggregateWorkflowSteps().get(4).getId(), "Step 5 did not appear in position 5!");

    }

    @Test
    public void testInheritWorkflowStepViaPointer() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findById(organization.getId()).get();

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        assertEquals(0, parentOrganization.getOriginalWorkflowSteps().size(), "The Parent Organization has workflow steps");
        assertEquals(0, organization.getOriginalWorkflowSteps().size(), "The Organization has workflow steps");
        assertEquals(0, grandChildOrganization.getOriginalWorkflowSteps().size(), "The Grand Child Organization has workflow steps");

        assertEquals(0, parentOrganization.getAggregateWorkflowSteps().size(), "The Parent Organization has a step in its workflow");
        assertEquals(0, organization.getAggregateWorkflowSteps().size(), "The Organization has a step in its workflow");
        assertEquals(0, grandChildOrganization.getAggregateWorkflowSteps().size(), "The Grand Child Organization has a step in its workflow");

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        assertEquals(1, parentOrganization.getOriginalWorkflowSteps().size(), "The Parent Organization did not add workflow steps");
        assertEquals(0, organization.getOriginalWorkflowSteps().size(), "The Organization acquired workflow steps");
        assertEquals(0, grandChildOrganization.getOriginalWorkflowSteps().size(), "The Grand Child Organization acquired workflow steps");

        assertEquals(1, parentOrganization.getAggregateWorkflowSteps().size(), "The Parent Organization did not add step to workflow");
        assertEquals(1, organization.getAggregateWorkflowSteps().size(), "The Organization did not inherit workflow");
        assertEquals(1, grandChildOrganization.getAggregateWorkflowSteps().size(), "The Grand Child Organization did not inherit workflow");

        Long workflowStepId = workflowStep.getId();

        String newName = "A Changed Name";
        workflowStep.setName(newName);

        WorkflowStep newWorkflowStep = workflowStepRepo.update(workflowStep, parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        // get old workflow step back. pointer changed!
        workflowStep = workflowStepRepo.findById(workflowStepId).get();

        // is indeed a new row!
        assertEquals(workflowStep.getId(), newWorkflowStep.getId(), "The workflow step didn't get a new id! Needs a new row in the table!");

        assertEquals(newName, newWorkflowStep.getName(), "The workflow step didn't get the updated name!");
        assertEquals(newName, parentOrganization.getAggregateWorkflowSteps().get(0).getName(), "The parentOrganization organization's workflowStep's name was not updated");
        assertEquals(newName, organization.getAggregateWorkflowSteps().get(0).getName(), "The organization workflowStep's name was not updated");
        assertEquals(newName, grandChildOrganization.getAggregateWorkflowSteps().get(0).getName(), "The grandChildOrganization workflowStep's name was not updated");
    }

    @Test
    public void testMaintainHierarchyOnDeletionOfInteriorOrg() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findById(organization.getId()).get();

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        assertEquals(0, parentOrganization.getAggregateWorkflowSteps().size(), "The Parent Organization has workflow steps");
        assertEquals(0, organization.getAggregateWorkflowSteps().size(), "The Organization has workflow steps");
        assertEquals(0, grandChildOrganization.getAggregateWorkflowSteps().size(), "The Grand Child Organization has workflow steps");

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        Long workflowStepId = workflowStep.getId();

        organization = organizationRepo.findById(organization.getId()).get();

        // Delete the interior organization
        organizationRepo.delete(organization);

        workflowStep = workflowStepRepo.findById(workflowStepId).get();

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        // Check that hierarchy is maintained and grandchild is moved to be
        // child of the top
        assertTrue(parentOrganization.getChildrenOrganizations().contains(grandChildOrganization), "The hierarchy was not maintained!");
        assertTrue(grandChildOrganization.getParentOrganization().equals(parentOrganization), "The hierarchy was not maintained!");

        // Check that removal of middle organization does not disturb the
        // grandchild's and Parent's workflow.
        assertEquals(1, workflowStepRepo.count(), "The workflowstep repo didn't contain the single workflow step!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().contains(workflowStep), "The Parent Organization doesn't contain workflowStep");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(workflowStep), "The Grand Child Organization doesn't contain workflowStep");

        // Check that inheritance still works
        String newName = "A Changed Name";
        workflowStep.setName(newName);

        WorkflowStep newWorkflowStep = workflowStepRepo.update(workflowStep, workflowStep.getOriginatingOrganization());

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        // get old workflow step back. pointer changed!
        workflowStep = workflowStepRepo.findById(workflowStepId).get();

        // is indeed a new row!
        assertEquals(workflowStep.getId(), newWorkflowStep.getId(), "The workflow step didn't get a new id! Needs a new row in the table!");

        assertEquals(newName, newWorkflowStep.getName(), "The workflow step didn't get the updated name!");
        assertEquals(newName, parentOrganization.getAggregateWorkflowSteps().get(0).getName(), "The parents organization's workflowStep's name was not updated");
        assertEquals(newName, grandChildOrganization.getAggregateWorkflowSteps().get(0).getName(), "The grandChildOrganization workflowStep's name was not updated");
    }

    @Test
    public void testWorkflowStepChangeAtChildOrg() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        organization = organizationRepo.findById(organization.getId()).get();

        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        // refresh everybody after the create
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        Long workflowStepId = workflowStep.getId();

        // Update the workflow step at the great grandchild (which didn't
        // originate it, obviously.) (We'll need this later to see that the
        // newly generated step inherits properly from a new step that overrides
        // what it previously inherited)
        String updatedName = "This step Will get the step it derives from changed.";
        workflowStep.setName(updatedName);
        WorkflowStep newWorkflowStepAtGreatGrandChild = workflowStepRepo.update(workflowStep, greatGrandChildOrganization);
        Long workflowStepAtGreatGrandchildId = newWorkflowStepAtGreatGrandChild.getId();

        // Update the workflow step at a the org (which doesn't originate it)
        updatedName = "Updated Name";
        workflowStep.setName(updatedName);
        WorkflowStep newWorkflowStep = workflowStepRepo.update(workflowStep, organization);

        // refresh everybody after the updates
        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        // get persisted workflow steps back. They're stale.
        workflowStep = workflowStepRepo.findById(workflowStepId).get();
        newWorkflowStepAtGreatGrandChild = workflowStepRepo.findById(workflowStepAtGreatGrandchildId).get();

        // when updating the workflow step at organization, test that
        // a new workflow step is made at the organization
        assertFalse(newWorkflowStep.getId().equals(workflowStep.getId()), "The child organization did not recieve a new workflowStep; steps has same IDs of " + newWorkflowStep.getId());
        assertEquals(updatedName, newWorkflowStep.getName(), "The updated workflowStep's name did not change.");
        assertEquals(TEST_WORKFLOW_STEP_NAME, workflowStep.getName(), "The parent workflowStep's name did change.");

        // the new workflow step remembers from whence it was derived (the
        // parent's workflow step)
        assertEquals(workflowStep.getId(), newWorkflowStep.getOriginatingWorkflowStep().getId(), "The child's new workflow step knew not from whence it came");

        // and furthermore, the organization's descendants point to the new
        // WorkflowStep (except the one that got its own new one)
        Long grandchildWorkflowStepId = grandChildOrganization.getAggregateWorkflowSteps().get(0).getId();
        assertEquals(grandchildWorkflowStepId, newWorkflowStep.getId(), "The grandchild organization didn't start pointing at the new workflow step it was supposed to inherit!");

        Long anotherGreatGrandChildWorkflowStepId = anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getId();
        assertEquals(anotherGreatGrandChildWorkflowStepId, newWorkflowStep.getId(), "Another great grandchild organization didn't start pointing at the new workflow step it was supposed to inherit!");

        // and furthermore yet, the workflow steps in descendant organizations
        // that used to inherit from the old step now inherit from the new step
        System.out.println("Question is, is first override's orignator of " + newWorkflowStepAtGreatGrandChild.getOriginatingWorkflowStep().getName() + " now the same as the new override " + newWorkflowStep.getName() + "?");
        assertEquals(newWorkflowStepAtGreatGrandChild.getOriginatingWorkflowStep().getId(), newWorkflowStep.getId(), "The great grandchild org's workflow step didn't start originating from the new workflow step!");
    }

    @Test
    public void testCantOverrideNonOverrideable() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        Assertions.assertThrows(WorkflowStepNonOverrideableException.class, () -> {
            Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
            parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

            parentOrganization.addChildOrganization(organization);
            parentOrganization = organizationRepo.save(parentOrganization);

            parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

            // make the update at the non-originating organization. We'll find it's
            // non-overrideable, so throw and exception.

            // test that we can't override a non-overrideable workflow step at the
            // child of its originating organization
            WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
            workflowStep.setOverrideable(false);
            workflowStep = workflowStepRepo.save(workflowStep);

            organization = organizationRepo.findById(organization.getId()).get();
            workflowStepRepo.update(workflowStep, organization);
        });
    }

    @Test
    public void testPermissionWorkflowChangeNonOverrideableAtOriginatingOrg() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        assertEquals(true, workflowStep.getOverrideable(), "the workflow step didn't start out overrideable as expected!");
        workflowStep.setOverrideable(false);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        // test that we can override a non-overrideable workflow step (which
        // will remain the same database row) if we're the originating
        // organization
        Long originalWorkflowStepId = workflowStep.getId();
        WorkflowStep updatedWorkflowStep = workflowStepRepo.update(workflowStep, parentOrganization);
        assertEquals(updatedWorkflowStep.getId(), originalWorkflowStepId, "The originating Organization of the WorkflowStep couldn't update it!");
        assertEquals(false, updatedWorkflowStep.getOverrideable(), "The originating Organization of the WorkflowStep couldn't make it non-overrideable!");
        assertEquals(false, workflowStep.getOverrideable(), "The originating Organization of the WorkflowStep couldn't make it non-overrideable!");
    }

    @Test
    public void testMakeWorkflwoStepWithDescendantsNonOverrideable() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // Step S1 has derivative step S2 which has derivative step S3
        // Test that making S1 non-overrideable will blow away S2 and S3 and
        // replace pointer to them with pointers to S1

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        organization = organizationRepo.findById(organization.getId()).get();

        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);

        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep s1 = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep t1 = workflowStepRepo.create("Step T", parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep u1 = workflowStepRepo.create("Step U", parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(3, parentOrganization.getOriginalWorkflowSteps().size(), "Parent organization has the incorrect number of workflow steps!");
        assertEquals(3, parentOrganization.getAggregateWorkflowSteps().size(), "Parent organization has wrong size of workflow!");

        assertEquals(0, organization.getOriginalWorkflowSteps().size(), "organization has the incorrect number of workflow steps!");
        assertEquals(3, organization.getAggregateWorkflowSteps().size(), "organization has wrong size of workflow!");

        assertEquals(0, grandChildOrganization.getOriginalWorkflowSteps().size(), "Grand child organization has the incorrect number of workflow steps!");
        assertEquals(3, grandChildOrganization.getAggregateWorkflowSteps().size(), "Grand child organization has wrong size of workflow!");

        assertEquals(0, greatGrandChildOrganization.getOriginalWorkflowSteps().size(), "Great grand child organization has the incorrect number of workflow steps!");
        assertEquals(3, greatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Great grand child organization has wrong size of workflow!");

        assertEquals(0, anotherGreatGrandChildOrganization.getOriginalWorkflowSteps().size(), "Another great grand child organization has the incorrect number of workflow steps!");
        assertEquals(3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Another great grand child organization has wrong size of workflow!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Long s1Id = s1.getId();

        String updatedName = "Updated Name";

        s1.setName(updatedName);

        // should change originating organization
        WorkflowStep s2 = workflowStepRepo.update(s1, organization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        // pointer for s1 became s2, have to get from the repo again
        s1 = workflowStepRepo.findById(s1Id).get();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(s1.getId(), s2.getOriginatingWorkflowStep().getId(), "New workflow step does not have the correct originating workflow step!");

        assertEquals(3, parentOrganization.getOriginalWorkflowSteps().size(), "Parent organization has the incorrect number of workflow steps!");
        assertEquals(3, parentOrganization.getAggregateWorkflowSteps().size(), "Parent organization has wrong size of workflow!");

        // this is important!
        assertEquals(1, organization.getOriginalWorkflowSteps().size(), "Organization has the incorrect number of workflow steps!");
        assertEquals(3, organization.getAggregateWorkflowSteps().size(), "Organization has wrong size of workflow!");

        assertEquals(TEST_WORKFLOW_STEP_NAME, s1.getName(), "s1 has the wrong name!");
        assertEquals(updatedName, s2.getName(), "s2 has the wrong name!");
        assertEquals(organization.getId(), s2.getOriginatingOrganization().getId(), "s2 has the wrong originating Organization!");
        assertEquals(s1.getId(), s2.getOriginatingWorkflowStep().getId(), "s2 has the wrong originating WorkflowStep!");
        assertEquals(1, workflowStepRepo.findByOriginatingWorkflowStep(s1).size(), "No workflow steps found originating from s1!");

        assertFalse(parentOrganization.getAggregateWorkflowSteps().contains(s2), "Parent organization somehow contains updated workflow step through inheritence!");

        assertTrue(organization.getAggregateWorkflowSteps().contains(s2), "Organization does not contain updated workflow step through inheritence!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(s2), "Grandchild Organization does not contain updated workflow step through inheritence!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(s2), "Great Grandchild Organization does not contain updated workflow step through inheritence!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(s2), "Another Great Grandchild Organization does not contain updated workflow step through inheritence!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Long s2Id = s2.getId();

        String anotherUpdatedName = "Yet another updated name";

        s2.setName(anotherUpdatedName);

        // should change originating organization
        WorkflowStep s3 = workflowStepRepo.update(s2, grandChildOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        // pointer for s2 became s3, have to get from the repo again
        s2 = workflowStepRepo.findById(s2Id).get();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(s2.getId(), s3.getOriginatingWorkflowStep().getId(), "New workflow step does not have the correct originating workflow step!");

        assertEquals(3, parentOrganization.getOriginalWorkflowSteps().size(), "Parent organization has the incorrect number of workflow steps!");
        assertEquals(3, parentOrganization.getAggregateWorkflowSteps().size(), "Parent organization has wrong size of workflow!");

        // this is important!
        assertEquals(1, grandChildOrganization.getOriginalWorkflowSteps().size(), "Grand child organization has the incorrect number of workflow steps!");
        assertEquals(3, grandChildOrganization.getAggregateWorkflowSteps().size(), "Grand child organization has wrong size of workflow!");

        assertEquals(anotherUpdatedName, s3.getName(), "s3 has the wrong name!");
        assertEquals(grandChildOrganization.getId(), s3.getOriginatingOrganization().getId(), "s3 has the wrong originating Organization!");
        assertEquals(s2.getId(), s3.getOriginatingWorkflowStep().getId(), "s2 has the wrong originating WorkflowStep!");
        assertEquals(1, workflowStepRepo.findByOriginatingWorkflowStep(s2).size(), "No workflow steps found originating from s2!");

        assertEquals(s2, s3.getOriginatingWorkflowStep(), "s2 has the wrong originating WorkflowStep!");
        assertEquals(1, workflowStepRepo.findByOriginatingWorkflowStep(s2).size(), "No workflow steps found originating from s2!");

        assertFalse(parentOrganization.getAggregateWorkflowSteps().contains(s3), "Parent organization somehow contains updated workflow step through inheritence!");
        assertFalse(organization.getAggregateWorkflowSteps().contains(s3), "Organization somehow contains updated workflow step through inheritence!");

        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(s3), "Grandchild Organization does not contain updated workflow step through inheritence!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(s3), "Great Grandchild Organization does not contain updated workflow step through inheritence!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(s3), "Another Great Grandchild Organization does not contain updated workflow step through inheritence!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(TEST_WORKFLOW_STEP_NAME, s1.getName(), "s1 has the wrong name!");
        assertEquals(updatedName, s2.getName(), "s2 has the wrong name!");
        assertEquals(organization.getId(), s2.getOriginatingOrganization().getId(), "s2 has the wrong originating Organization!");

        assertEquals(anotherUpdatedName, s3.getName(), "s3 has the wrong name!");
        assertEquals(grandChildOrganization.getId(), s3.getOriginatingOrganization().getId(), "s3 has the wrong originating Organization!");

        long numWorkflowSteps = workflowStepRepo.count();

        // now we are ready to make step 1 non-overrideable and ensure that step
        // 2 and 3 go away

        s1.setOverrideable(false);

        s1 = workflowStepRepo.update(s1, parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        assertEquals(numWorkflowSteps - 2, workflowStepRepo.count(), "Workflow Step Repo didn't get the disallowed (no longer overrideable) steps deleted!");

        assertTrue(organization.getAggregateWorkflowSteps().contains(s1), "Child org didn't get its workflow step replaced by the non-overrideable s1!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(s1), "Grandchild org didn't get its workflow step replaced by the non-overrideable s1!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(s1), "Great grandchild org didn't get its workflow step replaced by the non-overrideable s1!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(s1), "Another Great grandchild org didn't get its workflow step replaced by the non-overrideable s1!");

        assertEquals(s1.getId(), greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getId(), "Great grandchild org didn't have s1 as the first step");
        assertEquals(t1.getId(), greatGrandChildOrganization.getAggregateWorkflowSteps().get(1).getId(), "Great grandchild org didn't have t1 as the second step");
        assertEquals(u1.getId(), greatGrandChildOrganization.getAggregateWorkflowSteps().get(2).getId(), "Great grandchild org didn't have u1 as the third step");
    }

    @Test
    public void testReorderAggregateWorkflowStepsWithInheritance() {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findById(organization.getId()).get();

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep s1 = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep s2 = workflowStepRepo.create("Step 2", parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep s3 = workflowStepRepo.create("Step 3", parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        assertTrue(parentOrganization.getOriginalWorkflowSteps().contains(s1), "The parentOrganization's did not contain an expected original workflow step!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().contains(s2), "The parentOrganization's did not contain an expected original workflow step!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().contains(s3), "The parentOrganization's did not contain an expected original workflow step!");

        assertEquals(s1, parentOrganization.getAggregateWorkflowSteps().get(0), "The parentOrganization's first aggregate workflow step was not as expected!");
        assertEquals(s2, parentOrganization.getAggregateWorkflowSteps().get(1), "The parentOrganization's second aggregate workflow step was not as expected!");
        assertEquals(s3, parentOrganization.getAggregateWorkflowSteps().get(2), "The parentOrganization's third aggregate workflow step was not as expected!");

        assertEquals(s1, organization.getAggregateWorkflowSteps().get(0), "The organization's first aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(1), "The organization's second aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(2), "The organization's third aggregate workflow step was not as expected!");

        assertEquals(s1, organization.getAggregateWorkflowSteps().get(0), "The grandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(1), "The grandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(2), "The grandChildOrganization third aggregate workflow step was not as expected!");

        assertEquals(s1, organization.getAggregateWorkflowSteps().get(0), "The greatGrandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(1), "The greatGrandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(2), "The greatGrandChildOrganization third aggregate workflow step was not as expected!");

        assertEquals(s1, organization.getAggregateWorkflowSteps().get(0), "The anotherGreatGrandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(1), "The anotherGreatGrandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(2), "The anotherGreatGrandChildOrganization third aggregate workflow step was not as expected!");

        parentOrganization = organizationRepo.reorderWorkflowSteps(parentOrganization, s1, s2);

        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        assertTrue(parentOrganization.getOriginalWorkflowSteps().contains(s1), "The parentOrganization's did not contain an expected original workflow step!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().contains(s2), "The parentOrganization's did not contain an expected original workflow step!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().contains(s3), "The parentOrganization's did not contain an expected original workflow step!");

        assertEquals(s2, parentOrganization.getAggregateWorkflowSteps().get(0), "The parentOrganization's first aggregate workflow step was not as expected!");
        assertEquals(s1, parentOrganization.getAggregateWorkflowSteps().get(1), "The parentOrganization's second aggregate workflow step was not as expected!");
        assertEquals(s3, parentOrganization.getAggregateWorkflowSteps().get(2), "The parentOrganization's third aggregate workflow step was not as expected!");

        assertEquals(s2, organization.getAggregateWorkflowSteps().get(0), "The organization's first aggregate workflow step was not as expected!");
        assertEquals(s1, organization.getAggregateWorkflowSteps().get(1), "The organization's second aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(2), "The organization's third aggregate workflow step was not as expected!");

        assertEquals(s2, organization.getAggregateWorkflowSteps().get(0), "The grandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s1, organization.getAggregateWorkflowSteps().get(1), "The grandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(2), "The grandChildOrganization third aggregate workflow step was not as expected!");

        assertEquals(s2, organization.getAggregateWorkflowSteps().get(0), "The greatGrandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s1, organization.getAggregateWorkflowSteps().get(1), "The greatGrandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(2), "The greatGrandChildOrganization third aggregate workflow step was not as expected!");

        assertEquals(s2, organization.getAggregateWorkflowSteps().get(0), "The anotherGreatGrandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s1, organization.getAggregateWorkflowSteps().get(1), "The anotherGreatGrandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(2), "The anotherGreatGrandChildOrganization third aggregate workflow step was not as expected!");

        parentOrganization = organizationRepo.reorderWorkflowSteps(parentOrganization, s2, s3);

        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        assertEquals(s1, parentOrganization.getOriginalWorkflowSteps().get(0), "The parentOrganization's first original workflow step was not as expected!");
        assertEquals(s2, parentOrganization.getOriginalWorkflowSteps().get(1), "The parentOrganization's second original workflow step was not as expected!");
        assertEquals(s3, parentOrganization.getOriginalWorkflowSteps().get(2), "The parentOrganization's third original workflow step was not as expected!");

        assertEquals(s3, parentOrganization.getAggregateWorkflowSteps().get(0), "The parentOrganization's first aggregate workflow step was not as expected!");
        assertEquals(s1, parentOrganization.getAggregateWorkflowSteps().get(1), "The parentOrganization's second aggregate workflow step was not as expected!");
        assertEquals(s2, parentOrganization.getAggregateWorkflowSteps().get(2), "The parentOrganization's third aggregate workflow step was not as expected!");

        assertEquals(s3, organization.getAggregateWorkflowSteps().get(0), "The organization's first aggregate workflow step was not as expected!");
        assertEquals(s1, organization.getAggregateWorkflowSteps().get(1), "The organization's second aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(2), "The organization's third aggregate workflow step was not as expected!");

        assertEquals(s3, organization.getAggregateWorkflowSteps().get(0), "The grandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s1, organization.getAggregateWorkflowSteps().get(1), "The grandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(2), "The grandChildOrganization third aggregate workflow step was not as expected!");

        assertEquals(s3, organization.getAggregateWorkflowSteps().get(0), "The greatGrandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s1, organization.getAggregateWorkflowSteps().get(1), "The greatGrandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(2), "The greatGrandChildOrganization third aggregate workflow step was not as expected!");

        assertEquals(s3, organization.getAggregateWorkflowSteps().get(0), "The anotherGreatGrandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s1, organization.getAggregateWorkflowSteps().get(1), "The anotherGreatGrandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(2), "The anotherGreatGrandChildOrganization third aggregate workflow step was not as expected!");

        organization = organizationRepo.reorderWorkflowSteps(organization, s1, s3);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        assertEquals(s1, parentOrganization.getOriginalWorkflowSteps().get(0), "The parentOrganization's first original workflow step was not as expected!");
        assertEquals(s2, parentOrganization.getOriginalWorkflowSteps().get(1), "The parentOrganization's second original workflow step was not as expected!");
        assertEquals(s3, parentOrganization.getOriginalWorkflowSteps().get(2), "The parentOrganization's third original workflow step was not as expected!");

        assertEquals(s3, parentOrganization.getAggregateWorkflowSteps().get(0), "The parentOrganization's first aggregate workflow step was not as expected!");
        assertEquals(s1, parentOrganization.getAggregateWorkflowSteps().get(1), "The parentOrganization's second aggregate workflow step was not as expected!");
        assertEquals(s2, parentOrganization.getAggregateWorkflowSteps().get(2), "The parentOrganization's third aggregate workflow step was not as expected!");

        assertEquals(s1, organization.getAggregateWorkflowSteps().get(0), "The organization's first aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(1), "The organization's second aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(2), "The organization's third aggregate workflow step was not as expected!");

        assertEquals(s1, organization.getAggregateWorkflowSteps().get(0), "The grandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(1), "The grandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(2), "The grandChildOrganization third aggregate workflow step was not as expected!");

        assertEquals(s1, organization.getAggregateWorkflowSteps().get(0), "The greatGrandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(1), "The greatGrandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(2), "The greatGrandChildOrganization third aggregate workflow step was not as expected!");

        assertEquals(s1, organization.getAggregateWorkflowSteps().get(0), "The anotherGreatGrandChildOrganization first aggregate workflow step was not as expected!");
        assertEquals(s3, organization.getAggregateWorkflowSteps().get(1), "The anotherGreatGrandChildOrganization second aggregate workflow step was not as expected!");
        assertEquals(s2, organization.getAggregateWorkflowSteps().get(2), "The anotherGreatGrandChildOrganization third aggregate workflow step was not as expected!");

    }

    @Test
    public void testDeleteParentWorkflow() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        organization = organizationRepo.findById(organization.getId()).get();

        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);

        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep s1 = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(1, parentOrganization.getOriginalWorkflowSteps().size(), "Parent organization has the incorrect number of workflow steps!");
        assertEquals(1, parentOrganization.getAggregateWorkflowSteps().size(), "Parent organization has wrong size of aggregate workflow!");

        assertEquals(0, organization.getOriginalWorkflowSteps().size(), "organization has the incorrect number of workflow steps!");
        assertEquals(1, organization.getAggregateWorkflowSteps().size(), "organization has wrong size of aggregate workflow!");

        assertEquals(0, grandChildOrganization.getOriginalWorkflowSteps().size(), "Grand child organization has the incorrect number of workflow steps!");
        assertEquals(1, grandChildOrganization.getAggregateWorkflowSteps().size(), "Grand child organization has wrong size of aggregate workflow!");

        assertEquals(0, greatGrandChildOrganization.getOriginalWorkflowSteps().size(), "Great grand child organization has the incorrect number of workflow steps!");
        assertEquals(1, greatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Great grand child organization has wrong size of aggregate workflow!");

        assertEquals(0, anotherGreatGrandChildOrganization.getOriginalWorkflowSteps().size(), "Another great grand child organization has the incorrect number of workflow steps!");
        assertEquals(1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Another great grand child organization has wrong size of aggregate workflow!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Long s1Id = s1.getId();

        String updatedName = "Updated Name";

        s1.setName(updatedName);

        // should change originating organization
        WorkflowStep s2 = workflowStepRepo.update(s1, organization);

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        String anotherUpdatedName = "Yet another updated name";

        s2.setName(anotherUpdatedName);

        // should change originating organization
        workflowStepRepo.update(s2, grandChildOrganization);

        // pointer for s1 became s2, have to get from the repo again
        s1 = workflowStepRepo.findById(s1Id).get();

        parentOrganization = organizationRepo.save(parentOrganization);

        parentOrganization.removeOriginalWorkflowStep(s1);

        parentOrganization = organizationRepo.save(parentOrganization);

        // would like to have orphanRemoval handle this, but need to trigger it
        // with some cascade
        workflowStepRepo.delete(s1);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        assertEquals(0, workflowStepRepo.findAll().size(), "All workflow steps have not been removed!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(0, parentOrganization.getOriginalWorkflowSteps().size(), "Parent organization has the incorrect number of workflow steps!");
        assertEquals(0, parentOrganization.getAggregateWorkflowSteps().size(), "Parent organization has wrong size of aggregate workflow!");

        assertEquals(0, organization.getOriginalWorkflowSteps().size(), "organization has the incorrect number of workflow steps!");
        assertEquals(0, organization.getAggregateWorkflowSteps().size(), "organization has wrong size of aggregate workflow!");

        assertEquals(0, grandChildOrganization.getOriginalWorkflowSteps().size(), "Grand child organization has the incorrect number of workflow steps!");
        assertEquals(0, grandChildOrganization.getAggregateWorkflowSteps().size(), "Grand child organization has wrong size of aggregate workflow!");

        assertEquals(0, greatGrandChildOrganization.getOriginalWorkflowSteps().size(), "Great grand child organization has the incorrect number of workflow steps!");
        assertEquals(0, greatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Great grand child organization has wrong size of aggregate workflow!");

        assertEquals(0, anotherGreatGrandChildOrganization.getOriginalWorkflowSteps().size(), "Another great grand child organization has the incorrect number of workflow steps!");
        assertEquals(0, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Another great grand child organization has wrong size of aggregate workflow!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    @Test
    public void testParentUpdatesWorkflowStepOriginatingAtDescendent() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Assertions.assertThrows(ComponentNotPresentOnOrgException.class, () -> {
            Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
            parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

            parentOrganization.addChildOrganization(organization);
            parentOrganization = organizationRepo.save(parentOrganization);

            organization = organizationRepo.findById(organization.getId()).get();

            WorkflowStep s1 = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);

            assertEquals(1, workflowStepRepo.count(), "Incorrect number of workflow steps!");

            parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
            organization = organizationRepo.findById(organization.getId()).get();

            String updatedName = "Updated Name";

            s1.setName(updatedName);

            // should throw ComponentNotPresentOnOrgException
            workflowStepRepo.update(s1, parentOrganization);
        });
    }

    @Test
    public void testUpdateWorkflowStepAndRevertUpdate() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findById(organization.getId()).get();

        // Create s1, originating at top-level org
        WorkflowStep s1 = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        assertEquals(1, workflowStepRepo.count(), "Incorrect number of workflow steps!");

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();

        String updatedName = "Updated Name";

        s1.setName(updatedName);

        // modify s1 at middle org, which will create a new s2
        // s2 should have originating organization at the middle org
        WorkflowStep s2 = workflowStepRepo.update(s1, organization);
        organization = organizationRepo.findById(organization.getId()).get();

        assertEquals(2, workflowStepRepo.count(), "Incorrect number of workflow steps!");

        String anotherUpdatedName = "Yet another updated name";

        s2.setName(anotherUpdatedName);

        // should change originating organization
        WorkflowStep s3 = workflowStepRepo.update(s2, organization);

        assertEquals(2, workflowStepRepo.count(), "Incorrect number of workflow steps!");

        s3.setName(updatedName);

        // should change originating organization
        workflowStepRepo.update(s3, organization);

        assertEquals(2, workflowStepRepo.count(), "Incorrect number of workflow steps!");
    }

    @Test
    public void testMakeWSNonOverrideableAndAddBackToOrgsThatDeletedItFromAggregate() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // Step S1 will be inherited by the (child) organization, the
        // grandchildren, and the great grandchildren.
        // Test that after deleting S1 from some of these's aggregate steps, it
        // gets added back when made non-overrideable.

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        organization = organizationRepo.findById(organization.getId()).get();

        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);

        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep s1 = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        /* WorkflowStep t1 = */ workflowStepRepo.create("Step T", parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        /* WorkflowStep u1 = */ workflowStepRepo.create("Step U", parentOrganization);

        // organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        // now let's delete S1 off the grandchild
        workflowStepRepo.removeFromOrganization(grandChildOrganization, s1);

        // but let's also override S1 at the (child) org so that it is a new one
        // derived from S1
        Long s1Id = s1.getId();
        s1 = workflowStepRepo.findById(s1Id).get();
        s1.setName("Overridden S1 at the child org");
        organization = organizationRepo.findById(organization.getId()).get();
        WorkflowStep s1override = workflowStepRepo.update(s1, organization);
        s1 = workflowStepRepo.findById(s1Id).get();
        organization = organizationRepo.findById(organization.getId()).get();
        assertTrue(organization.getAggregateWorkflowSteps().contains(s1override), "The child org didn't contained the overriding workflow step it originated!");
        assertFalse(organization.getAggregateWorkflowSteps().contains(s1), "The child org contained a workflow step it was supposed to have overridden!");

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        // should be on the aggregate of parent, but nobody else
        assertEquals(3, parentOrganization.getAggregateWorkflowSteps().size(), "Parent lost it's original workflow step from its aggregates when a child removed it from its aggregates!");
        assertEquals(2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().size(), "A great grandchild org kept an aggregate workflow step that an ancestor removed!");

        // make s1 non overrideable and see that's its added back down below
        // where it was removed or overridden, and is unaffected where it
        // originates
        s1 = workflowStepRepo.findById(s1Id).get();
        s1.setOverrideable(false);
        s1 = workflowStepRepo.update(s1, parentOrganization);

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        assertEquals(3, parentOrganization.getAggregateWorkflowSteps().size(), "The parent org somehow lost it's originating step!");
        assertEquals(3, parentOrganization.getOriginalWorkflowSteps().size(), "The parent org somehow lost it's originating step!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().contains(s1), "The parent org somehow lost it's originating step!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().contains(s1), "The parent org somehow lost it's originating step!");

        assertFalse(organization.getAggregateWorkflowSteps().contains(s1override), "The org with the overriding step didn't get the override removed when it was made non-overrideable!");
        assertFalse(organization.getOriginalWorkflowSteps().contains(s1override), "The org with the overriding step didn't get the override removed when it was made non-overrideable!");
        assertEquals(0, organization.getOriginalWorkflowSteps().size(), "The org with the overriding step didn't get the override removed when it was made non-overrideable!");
        assertTrue(organization.getAggregateWorkflowSteps().contains(s1), "The org with the overriding step didn't get it replaced when it was made non-overrideable!");

        assertEquals(3, greatGrandChildOrganization.getAggregateWorkflowSteps().size(), "The grandchild org didn't get back an aggregate workflow step that an ancestor made non-overrideable!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(s1), "The grandchild org didn't get back an aggregate workflow step that an ancestor made non-overrideable!");

        assertEquals(3, grandChildOrganization.getAggregateWorkflowSteps().size(), "A great grandchild org didn't get back an aggregate workflow step that an ancestor made non-overrideable!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(s1), "A great grandchild org didn't get back an aggregate workflow step that an ancestor made non-overrideable!");

        assertEquals(3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Another great grandchild org didn't get back an aggregate workflow step that an ancestor made non-overrideable!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(s1), "Another great grandchild org didn't get back an aggregate workflow step that an ancestor made non-overrideable!");
    }

    @Test
    public void testChildWorkflowStepNonOverrideableReplacedAfterParentWorkflowStepBecomesNonOverrideable() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findById(organization.getId()).get();

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();

        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        Long pwsId = parentWorkflowStep.getId();

        assertEquals(1, workflowStepRepo.count(), "Wrong number of workflow steps!");

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertTrue(parentOrganization.getOriginalWorkflowSteps().contains(parentWorkflowStep), "Parent does not have original workflow step!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "Parent does not have workflow step!");
        assertTrue(organization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "Child does not have workflow step!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "Grandchild does not have workflow step!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "Great grandchild does not have workflow step!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "Another great grandchild does not have workflow step!");

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        parentWorkflowStep.setOverrideable(false);

        WorkflowStep grandChildNonOverrideableWorkflowStep = workflowStepRepo.update(parentWorkflowStep, grandChildOrganization);

        assertEquals(2, workflowStepRepo.count(), "Wrong number of workflow steps!");

        parentWorkflowStep = workflowStepRepo.findById(pwsId).get();

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertTrue(parentOrganization.getOriginalWorkflowSteps().contains(parentWorkflowStep), "Parent does not have workflow step!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "Parent does not have workflow step!");
        assertTrue(organization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "Child does not have workflow step!");

        assertTrue(grandChildOrganization.getOriginalWorkflowSteps().contains(grandChildNonOverrideableWorkflowStep), "Grandchild does not have orginal workflow step!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(grandChildNonOverrideableWorkflowStep), "Grandchild does not have workflow step!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(grandChildNonOverrideableWorkflowStep), "Great grandchild does not have workflow step!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(grandChildNonOverrideableWorkflowStep), "Another great grandchild does not have workflow step!");

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        parentWorkflowStep.setOverrideable(false);

        WorkflowStep childNonOverrideableWorkflowStep = workflowStepRepo.update(parentWorkflowStep, organization);

        parentWorkflowStep = workflowStepRepo.findById(pwsId).get();

        parentOrganization = organizationRepo.findById(parentOrganization.getId()).get();
        organization = organizationRepo.findById(organization.getId()).get();
        grandChildOrganization = organizationRepo.findById(grandChildOrganization.getId()).get();
        greatGrandChildOrganization = organizationRepo.findById(greatGrandChildOrganization.getId()).get();
        anotherGreatGrandChildOrganization = organizationRepo.findById(anotherGreatGrandChildOrganization.getId()).get();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertTrue(parentOrganization.getOriginalWorkflowSteps().contains(parentWorkflowStep), "Parent does not have workflow step!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "Parent does not have workflow step!");

        assertTrue(organization.getOriginalWorkflowSteps().contains(childNonOverrideableWorkflowStep), "Child does not have original workflow step!");
        assertTrue(organization.getAggregateWorkflowSteps().contains(childNonOverrideableWorkflowStep), "Child does not have workflow step!");

        assertFalse(grandChildOrganization.getOriginalWorkflowSteps().contains(childNonOverrideableWorkflowStep), "Grandchild still has orginal workflow step!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(childNonOverrideableWorkflowStep), "Grandchild does not have workflow step!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(childNonOverrideableWorkflowStep), "Great grandchild does not have workflow step!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(childNonOverrideableWorkflowStep), "Another great grandchild does not have workflow step!");

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // should of deleted the workflow step the grandchild created when
        // making it non overrideable
        assertEquals(2, workflowStepRepo.count(), "Wrong number of workflow steps!");

        // parent would be the institution
        assertTrue(parentOrganization.getAggregateWorkflowSteps().contains(parentWorkflowStep), "Parent does not have workflow step!");
        assertEquals(1, parentOrganization.getAggregateWorkflowSteps().size(), "Parent has more than one workflow step!");
        assertEquals(true, parentOrganization.getAggregateWorkflowSteps().get(0).getOverrideable(), "Parent workflow step is overrideable!");

        // child is the first organization created. set workflow step to non
        // overrideable after the granchild did
        assertTrue(organization.getAggregateWorkflowSteps().contains(childNonOverrideableWorkflowStep), "Child does not have workflow step!");
        assertEquals(1, organization.getAggregateWorkflowSteps().size(), "Child has more than one workflow step!");
        assertEquals(false, organization.getAggregateWorkflowSteps().get(0).getOverrideable(), "Child workflow step is overrideable!");

        // grandchild is the organization to first set workflow step to non
        // overrideable
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(childNonOverrideableWorkflowStep), "Grandchild does not have child workflow step!");
        assertFalse(grandChildOrganization.getAggregateWorkflowSteps().contains(grandChildNonOverrideableWorkflowStep), "Grandchild still has grandchildchild workflow step!");
        assertEquals(1, grandChildOrganization.getAggregateWorkflowSteps().size(), "Grandchild has more than one workflow step!");
        assertEquals(false, grandChildOrganization.getAggregateWorkflowSteps().get(0).getOverrideable(), "Grandchild workflow step is overrideable!");

        // some additional children to check
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(childNonOverrideableWorkflowStep), "Great grandchild does not have child workflow step!");
        assertFalse(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(grandChildNonOverrideableWorkflowStep), "Great grandchild still has grandchildchild workflow step!");
        assertEquals(1, greatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Great grandchild has more than oneworkflow step!");
        assertEquals(false, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getOverrideable(), "Great grandchild workflow step is overrideable!");

        // some additional children to check
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(childNonOverrideableWorkflowStep), "Another great grandchild does not have child workflow step!");
        assertFalse(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(grandChildNonOverrideableWorkflowStep), "Another great grandchild still has grandchildchild workflow step!");
        assertEquals(1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Another great grandchild has more than one workflow step!");
        assertEquals(false, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getOverrideable(), "Another great grandchild workflow step is overrideable!");

    }

    @AfterEach
    public void cleanUp() {

        noteRepo.findAll().forEach(note -> {
            noteRepo.delete(note);
        });
        assertEquals(0, noteRepo.count(), "Couldn't delete all notes!");

        fieldProfileRepo.findAll().forEach(fieldProfile -> {
            fieldProfileRepo.delete(fieldProfile);
        });
        assertEquals(0, fieldProfileRepo.count(), "Couldn't delete all field profiles!");

        submissionListColumnRepo.deleteAll();

        inputTypeRepo.deleteAll();
        assertEquals(0, inputTypeRepo.count(), "Couldn't delete all input types!");

        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });
        assertEquals(0, workflowStepRepo.count(), "Couldn't delete all workflow steps!");

        organizationRepo.deleteAll();
        assertEquals(0, organizationRepo.count(), "Couldn't delete all organizations");

        organizationCategoryRepo.deleteAll();
        assertEquals(0, organizationCategoryRepo.count(), "Couldn't delete all organization categories!");

        fieldPredicateRepo.deleteAll();
        assertEquals(0, fieldPredicateRepo.count(), "Couldn't delete all predicates!");

        namedSearchFilterGroupRepo.findAll().forEach(nsf -> {
            namedSearchFilterGroupRepo.delete(nsf);
        });

        userRepo.deleteAll();
    }

}
