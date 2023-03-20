package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.HeritableModelNonOverrideableException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;

public class FieldProfileTest extends AbstractEntityTest {

    @BeforeEach
    public void setUp() {
        assertEquals(0, languageRepo.count(), "The repository was not empty!");
        assertEquals(0, fieldProfileRepo.count(), "The repository was not empty!");
        assertEquals(0, fieldPredicateRepo.count(), "The repository was not empty!");
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, new Boolean(false));
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());
        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.getById(organization.getId());
        inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);
    }

    @Override
    @Test
    public void testCreate() {
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        assertEquals(1, fieldProfileRepo.count(), "The repository did not save the entity!");
        assertEquals(fieldPredicate, fieldProfile.getFieldPredicate(), "The field profile did not contain the correct perdicate value!");
        assertEquals(inputType, fieldProfile.getInputType(), "The field predicate did not contain the correct value!");
        assertEquals(TEST_FIELD_PROFILE_USAGE, fieldProfile.getUsage(), "The field predicate did not contain the correct value!");
        assertEquals(TEST_GLOSS, fieldProfile.getGloss(), "The field predicate did not contain the correct value!");
        assertEquals(TEST_FIELD_PROFILE_REPEATABLE, fieldProfile.getRepeatable(), "The field predicate did not contain the correct value!");
        assertEquals(TEST_FIELD_PROFILE_ENABLED, fieldProfile.getEnabled(), "The field predicate did not contain the correct value!");
        assertEquals(TEST_FIELD_PROFILE_OPTIONAL, fieldProfile.getOptional(), "The field predicate did not contain the correct value!");
    }

    @Override
    @Test
    public void testDuplication() {
        fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        try {
            fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */
        }
        assertEquals(1, fieldProfileRepo.count(), "The repository duplicated entity!");
    }

    @Override
    @Test
    public void testDelete() {
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        fieldProfileRepo.delete(fieldProfile);
        assertEquals(0, fieldProfileRepo.count(), "Entity did not delete!");
    }

    @Override
    @Test
    public void testCascade() {
        // create field profile
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        // add glosses and controlled vocabularies
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME);
        fieldProfile.setGloss(TEST_GLOSS);
        fieldProfile.setControlledVocabulary(controlledVocabulary);
        fieldProfile = fieldProfileRepo.save(fieldProfile);

        // verify field glosses and controlled vocabularies
        assertEquals(TEST_GLOSS, fieldProfile.getGloss(), "The field profile did not contain the correct field gloss!");
        assertEquals(controlledVocabulary.getName(), fieldProfile.getControlledVocabulary().getName(), "The field profile did not contain the correct controlled vocabulary!");

        // test delete profile
        fieldProfileRepo.delete(fieldProfile);
        assertEquals(0, fieldProfileRepo.count(), "An field profile was not deleted!");
        assertEquals(1, fieldPredicateRepo.count(), "The field predicate was deleted!");
        assertEquals(1, controlledVocabularyRepo.count(), "The controlled vocabulary was deleted!");
    }

    @Test
    @Transactional
    public void testInheritFieldProfileViaPointer() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        parentOrganization.addChildOrganization(childOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        childOrganization = organizationRepo.getById(childOrganization.getId());

        Organization grandchildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandchildOrganization = organizationRepo.getById(grandchildOrganization.getId());

        childOrganization.addChildOrganization(grandchildOrganization);
        childOrganization = organizationRepo.save(childOrganization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_STEP_NAME, parentOrganization);

        FieldProfile fieldProfile = fieldProfileRepo.create(parentWorkflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        childOrganization = organizationRepo.getById(childOrganization.getId());
        grandchildOrganization = organizationRepo.getById(grandchildOrganization.getId());

        FieldProfile parentFieldProfile = parentOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().get(0);
        FieldProfile childFieldProfile = childOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().get(0);
        FieldProfile grandchildFieldProfile = grandchildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().get(0);

        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fieldProfile), "The parent organization's workflow did not contain the original fieldProfile");
        assertTrue(childOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fieldProfile), "The child organization's workflow did not contain the original fieldProfile");
        assertTrue(grandchildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fieldProfile), "The grandchild organization's workflow did not contain the original fieldProfile");

        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fieldProfile), "The parent organization's workflow did not contain the aggregate fieldProfile");
        assertTrue(childOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fieldProfile), "The child organization's workflow did not contain the aggregate fieldProfile");
        assertTrue(grandchildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fieldProfile), "The grandchild organization's workflow did not contain the aggregate fieldProfile");

        assertEquals(fieldProfile.getFieldPredicate().getId(), parentFieldProfile.getFieldPredicate().getId(), "The parent organization's workflow did not contain the fieldProfile's predicate");
        assertEquals(fieldProfile.getFieldPredicate().getId(), childFieldProfile.getFieldPredicate().getId(), "The child organization's workflow did not contain the fieldProfile's predicate");
        assertEquals(fieldProfile.getFieldPredicate().getId(), childFieldProfile.getFieldPredicate().getId(), "The grandchild organization's workflow did not contain the fieldProfile's predicate");

        String updatedFieldPredicateValue = "updated.value";
        parentFieldProfile.getFieldPredicate().setValue(updatedFieldPredicateValue);

        fieldProfileRepo.update(parentFieldProfile, parentOrganization);

        childFieldProfile = fieldProfileRepo.getById(childFieldProfile.getId());
        grandchildFieldProfile = fieldProfileRepo.getById(grandchildFieldProfile.getId());

        // *********************************************************************************************************************************************************//
        // *********************************************************************************************************************************************************//
        // *********************************************************************************************************************************************************//
        assertEquals(updatedFieldPredicateValue, childFieldProfile.getFieldPredicate().getValue(), "The child fieldProfile's value did not recieve updated value");
        assertEquals(updatedFieldPredicateValue, grandchildFieldProfile.getFieldPredicate().getValue(), "The grand child fieldProfile's value did not recieve updated value");
        // *********************************************************************************************************************************************************//
        // *********************************************************************************************************************************************************//
        // *********************************************************************************************************************************************************//

    }

    @Test
    public void testCantOverrideNonOverrideable() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Assertions.assertThrows(HeritableModelNonOverrideableException.class, () -> {
            Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
            parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

            workflowStep = workflowStepRepo.getById(workflowStep.getId());

            FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

            fieldProfile.setOverrideable(false);

            organization = organizationRepo.getById(organization.getId());

            // actually set the note to non overrideable on parent first
            fieldProfile = fieldProfileRepo.update(fieldProfile, organization);

            assertFalse(fieldProfile.getOverrideable(), "The field profile was not made non-overrideable!");

            childOrganization = organizationRepo.getById(childOrganization.getId());

            fieldProfile.getFieldPredicate().setValue("Updated Value");

            fieldProfileRepo.update(fieldProfile, childOrganization);
        });
    }

    @Test
    public void testCantOverrideNonOverrideableWorkflowStep() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Assertions.assertThrows(WorkflowStepNonOverrideableException.class, () -> {

            Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
            parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

            workflowStep = workflowStepRepo.getById(workflowStep.getId());

            FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

            organization = organizationRepo.getById(organization.getId());
            workflowStep = workflowStepRepo.getById(workflowStep.getId());

            workflowStep.setOverrideable(false);

            workflowStep = workflowStepRepo.update(workflowStep, organization);

            fieldProfile = fieldProfileRepo.getById(fieldProfile.getId());

            childOrganization = organizationRepo.getById(childOrganization.getId());

            workflowStep = workflowStepRepo.getById(workflowStep.getId());

            assertEquals(workflowStep, fieldProfile.getOriginatingWorkflowStep(), "The field profile's originating workflow step is not the intended workflow step!");

            assertFalse(fieldProfile.getOriginatingWorkflowStep().getOverrideable(), "The field profile's originating workflow step was not made non-overrideable!");

            assertFalse(workflowStep.getOverrideable(), "The workflowstep was not made non-overrideable!");

            fieldProfile.getFieldPredicate().setValue("Updated Value");

            fieldProfileRepo.update(fieldProfile, childOrganization);
        });
    }

    @Test
    public void testCanOverrideNonOverrideableAtOriginatingOrg() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        workflowStep = workflowStepRepo.getById(workflowStep.getId());

        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        organization = organizationRepo.getById(organization.getId());

        fieldProfile.setOverrideable(false);

        // actually set the note to non overrideable on parent first
        fieldProfile = fieldProfileRepo.update(fieldProfile, organization);

        String helpTest = "Help!";

        fieldProfile.setHelp(helpTest);

        assertTrue(fieldProfile.getHelp().equals(helpTest), "The setter didn't work for help string on the FieldProfile!");

        assertFalse(fieldProfile.getOverrideable(), "The field profile didn't record that it was made non-overrideable!");

        fieldProfile = fieldProfileRepo.update(fieldProfile, organization);

        assertTrue(fieldProfile.getHelp().equals(helpTest), "The field profile wasn't updated to include the changed help!");

    }

    @Test
    public void testInheritAndRemoveFieldProfiles() {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(0, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().size(), "parentOrganization's workflow step has the incorrect number of field profiles!");
        assertEquals(0, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "parentOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!");

        assertEquals(0, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "organization's aggregate workflow step has the incorrect number of aggregate field profiles!");

        assertEquals(0, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "grandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        FieldPredicate fieldPredicate2 = fieldPredicateRepo.create("foo.bar", new Boolean(false));
        FieldPredicate fieldPredicate3 = fieldPredicateRepo.create("bar.foo", new Boolean(false));

        FieldProfile fp = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        FieldProfile fp2 = fieldProfileRepo.create(workflowStep, fieldPredicate2, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        FieldProfile fp3 = fieldProfileRepo.create(workflowStep, fieldPredicate3, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(3, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().size(), "parentOrganization's workflow step has the incorrect number of field profiles!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp), "parentOrganization's workflow step's did not contain field profile 1!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp2), "parentOrganization's workflow step's did not contain field profile 2!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp3), "parentOrganization's workflow step's did not contain field profile 3!");

        assertEquals(3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "parentOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp), "parentOrganization's aggregate workflow step's did not contain field profile 1!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "parentOrganization's aggregate workflow step's did not contain field profile 2!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "parentOrganization's aggregate workflow step's did not contain field profile 3!");

        assertEquals(3, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "organization's aggregate workflow step has the incorrect number of aggregate field profiles!");
        assertTrue(organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp), "organization's aggregate workflow step's did not contain field profile 1!");
        assertTrue(organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "organization's aggregate workflow step's did not contain field profile 2!");
        assertTrue(organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "organization's aggregate workflow step's did not contain field profile 3!");

        assertEquals(3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "grandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp), "grandChildOrganization's aggregate workflow step's did not contain field profile 1!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "grandChildOrganization's aggregate workflow step's did not contain field profile 2!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "grandChildOrganization's aggregate workflow step's did not contain field profile 3!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "greatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp), "greatGrandChildOrganization's aggregate workflow step's did not contain field profile 1!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "greatGrandChildOrganization's aggregate workflow step's did not contain field profile 2!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "greatGrandChildOrganization's aggregate workflow step's did not contain field profile 3!");

        assertEquals(3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "anotherGreatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp), "anotherGreatGrandChildOrganization's aggregate workflow step's did not contain field profile 1!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "anotherGreatGrandChildOrganization's aggregate workflow step's did not contain field profile 2!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "anotherGreatGrandChildOrganization's aggregate workflow step's did not contain field profile 3!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        parentOrganization.getOriginalWorkflowSteps().get(0).removeOriginalFieldProfile(fp);
        parentOrganization = organizationRepo.save(parentOrganization);

        fieldProfileRepo.delete(fp);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(2, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().size(), "parentOrganization's workflow step has the incorrect number of field profiles!");
        assertFalse(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp), "parentOrganization's workflow step's still contains field profile 1!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp2), "parentOrganization's workflow step's did not contain field profile 2!");
        assertTrue(parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp3), "parentOrganization's workflow step's did not contain field profile 3!");

        assertEquals(2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "parentOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!");
        assertFalse(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp), "parentOrganization's aggregate workflow step's still contains field profile 1!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "parentOrganization's aggregate workflow step's did not contain field profile 2!");
        assertTrue(parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "parentOrganization's aggregate workflow step's did not contain field profile 3!");

        assertEquals(2, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "organization's aggregate workflow step has the incorrect number of aggregate field profiles!");
        assertFalse(organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp), "organization's aggregate workflow step's still contains field profile 1!");
        assertTrue(organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "organization's aggregate workflow step's did not contain field profile 2!");
        assertTrue(organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "organization's aggregate workflow step's did not contain field profile 3!");

        assertEquals(2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "grandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!");
        assertFalse(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp), "grandChildOrganization's aggregate workflow step's still contains field profile 1!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "grandChildOrganization's aggregate workflow step's did not contain field profile 2!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "grandChildOrganization's aggregate workflow step's did not contain field profile 3!");

        assertEquals(2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "greatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!");
        assertFalse(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp), "greatGrandChildOrganization's aggregate workflow step's still contains field profile 1!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "greatGrandChildOrganization's aggregate workflow step's did not contain field profile 2!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "greatGrandChildOrganization's aggregate workflow step's did not contain field profile 3!");

        assertEquals(2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size(), "anotherGreatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!");
        assertFalse(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp), "anotherGreatGrandChildOrganization's aggregate workflow step's still contains field profile 1!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "anotherGreatGrandChildOrganization's aggregate workflow step's did not contain field profile 2!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "anotherGreatGrandChildOrganization's aggregate workflow step's did not contain field profile 3!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    @Test
    public void testReorderAggregateFieldProfiles() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        FieldPredicate fieldPredicate2 = fieldPredicateRepo.create("foo.bar", new Boolean(false));
        FieldPredicate fieldPredicate3 = fieldPredicateRepo.create("bar.foo", new Boolean(false));

        FieldProfile fp1 = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        FieldProfile fp2 = fieldProfileRepo.create(workflowStep, fieldPredicate2, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        FieldProfile fp3 = fieldProfileRepo.create(workflowStep, fieldPredicate3, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        Long fp1Id = fp1.getId();
        Long fp2Id = fp2.getId();
        Long fp3Id = fp3.getId();

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        assertEquals(fp1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The parentOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The parentOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The parentOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp1, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The organization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp2, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The organization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp3, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The organization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The grandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The grandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The grandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The greatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The greatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The greatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        fp1 = fieldProfileRepo.getById(fp1Id);
        fp2 = fieldProfileRepo.getById(fp2Id);

        workflowStep = workflowStepRepo.swapFieldProfiles(parentOrganization, workflowStep, fp1, fp2);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        assertEquals(fp2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The parentOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The parentOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The parentOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp2, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The organization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The organization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp3, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The organization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The grandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The grandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The grandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The greatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The greatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The greatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        fp2 = fieldProfileRepo.getById(fp2Id);
        fp3 = fieldProfileRepo.getById(fp3Id);

        workflowStep = workflowStepRepo.swapFieldProfiles(parentOrganization, workflowStep, fp2, fp3);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        assertEquals(fp3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The parentOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The parentOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The parentOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp3, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The organization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The organization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp2, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The organization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The grandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The grandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The grandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The greatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The greatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The greatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(fp3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        fp1 = fieldProfileRepo.getById(fp1Id);
        fp3 = fieldProfileRepo.getById(fp3Id);

        // creates a new workflow step
        workflowStepRepo.swapFieldProfiles(organization, workflowStep, fp1, fp3);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        WorkflowStep newWorkflowStep = organization.getOriginalWorkflowSteps().get(0);

        assertEquals(fp3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The parentOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The parentOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The parentOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        // make sure new workflow step contains all field profiles
        assertTrue(organization.getOriginalWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp1), "The organization's original workflow step's contains first field profile!");
        assertTrue(organization.getOriginalWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2), "The organization's original workflow step's contains second field profile!!");
        assertTrue(organization.getOriginalWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3), "The organization's original workflow step's contains third field profile!!");

        assertEquals(newWorkflowStep, organization.getAggregateWorkflowSteps().get(0), "The organization aggregate workflow steps does not have new workflow step from reorder on non originating organization!");

        assertEquals(fp1, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The organization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp3, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The organization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp2, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The organization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(newWorkflowStep, grandChildOrganization.getAggregateWorkflowSteps().get(0), "The grandChildOrganization did not inherit new workflow step from reorder on non originating organization!");

        assertEquals(fp1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The grandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The grandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The grandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(newWorkflowStep, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0), "The greatGrandChildOrganization did not inherit new workflow step from reorder on non originating organization!");

        assertEquals(fp1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The greatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The greatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The greatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

        assertEquals(newWorkflowStep, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0), "The anotherGreatGrandChildOrganization did not inherit new workflow step from reorder on non originating organization!");

        assertEquals(fp1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0), "The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!");
        assertEquals(fp3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1), "The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!");
        assertEquals(fp2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2), "The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!");

    }

    @Test
    public void testFieldProfileChangeAtChildOrg() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        Long originalFieldProfileId = fieldProfile.getId();

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals(1, parentOrganization.getOriginalWorkflowSteps().size(), "Parent organization has the incorrect number of workflow steps!");
        assertEquals(1, parentOrganization.getAggregateWorkflowSteps().size(), "Parent organization has wrong size of workflow!");

        assertEquals(0, organization.getOriginalWorkflowSteps().size(), "organization has the incorrect number of workflow steps!");
        assertEquals(1, organization.getAggregateWorkflowSteps().size(), "organization has wrong size of workflow!");

        assertEquals(0, grandChildOrganization.getOriginalWorkflowSteps().size(), "Grand child organization has the incorrect number of workflow steps!");
        assertEquals(1, grandChildOrganization.getAggregateWorkflowSteps().size(), "Grand child organization has wrong size of workflow!");

        assertEquals(0, greatGrandChildOrganization.getOriginalWorkflowSteps().size(), "Great grand child organization has the incorrect number of workflow steps!");
        assertEquals(1, greatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Great grand child organization has wrong size of workflow!");

        assertEquals(0, anotherGreatGrandChildOrganization.getOriginalWorkflowSteps().size(), "Another great grand child organization has the incorrect number of workflow steps!");
        assertEquals(1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().size(), "Another great grand child organization has wrong size of workflow!");

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        fieldProfile.setHelp("Changed Help Message");

        // request the change at the level of the child organization
        FieldProfile updatedFieldProfile = fieldProfileRepo.update(fieldProfile, organization);

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());

        // pointer to fieldProfile became updatedFieldProfile, must fetch it agains
        fieldProfile = fieldProfileRepo.getById(originalFieldProfileId);

        // There should be a new workflow step on the child organization that is distinct from the original workflowStep
        WorkflowStep updatedWorkflowStep = organization.getAggregateWorkflowSteps().get(0);
        assertFalse(workflowStep.getId().equals(updatedWorkflowStep.getId()), "The updatedWorkflowStep was just the same as the original from which it was derived when its field profile was updated!");

        // The new workflow step should contain the new updatedFieldProfile
        assertTrue(updatedWorkflowStep.getAggregateFieldProfiles().contains(updatedFieldProfile), "The updatedWorkflowStep didn't contain the new updatedFieldProfile");

        // The updatedFieldProfile should be distinct from the original fieldProfile
        assertFalse(fieldProfile.getId().equals(updatedFieldProfile.getId()), "The updatedFieldProfile was just the same as the original from which it was derived!");

        // the grandchild and great grandchildren should all be using the new workflow step and the updatedFieldProfile
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep), "The grandchild org didn't have the updatedWorkflowStep!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(updatedFieldProfile), "The grandchild org didn't have the updatedFieldProfile on the updatedWorkflowStep!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep), "The great grandchild org didn't have the updatedWorkflowStep!");
        assertTrue(greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(updatedFieldProfile), "The great grandchild org didn't have the updatedFieldProfile on the updatedWorkflowStep!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep), "Another great grandchild org didn't have the updatedWorkflowStep!");
        assertTrue(anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(updatedFieldProfile), "Another great grandchild org didn't have the updatedFieldProfile on the updatedWorkflowStep!");
    }

    @Test
    public void testMaintainFieldOrderWhenOverriding() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        FieldPredicate fieldPredicate2 = fieldPredicateRepo.create("foo.bar", new Boolean(false));
        FieldPredicate fieldPredicate3 = fieldPredicateRepo.create("bar.foo", new Boolean(false));

        fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        FieldProfile fp2 = fieldProfileRepo.create(workflowStep, fieldPredicate2, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        fieldProfileRepo.create(workflowStep, fieldPredicate3, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        // now, override the second step at the grandchild and ensure that the new step is the second step at the grandchild and at the great grandchildren
        fp2.setHelp("help!");
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        FieldProfile fp2Updated = fieldProfileRepo.update(fp2, grandChildOrganization);
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        WorkflowStep newWSWithNewFPViaAggregation = grandChildOrganization.getAggregateWorkflowSteps().get(0);
        WorkflowStep newWSWithNewFPViaOriginals = grandChildOrganization.getOriginalWorkflowSteps().get(0);
        assertEquals(newWSWithNewFPViaOriginals, newWSWithNewFPViaAggregation, "The new aggregated workflow step on the grandchild org was not the one the grandchild org just originated!");

        assertEquals(fp2Updated.getId(), grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1).getId(), "Updated field profile was in the wrong order!");

    }

    // TODO: this test is not done, development of the full feature deferred for now
    @Test
    public void testMakeFieldProfileNonOverrideable() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        Long wsId = workflowStep.getId();

        // put a field profile on the parent org's workflow step
        FieldPredicate fieldPredicate2 = fieldPredicateRepo.create("foo.bar", new Boolean(false));
        FieldPredicate fieldPredicate3 = fieldPredicateRepo.create("bar.foo", new Boolean(false));

        /* FieldProfile fp1 = */ fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(wsId);
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        FieldProfile fp2 = fieldProfileRepo.create(workflowStep, fieldPredicate2, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(wsId);
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        /* FieldProfile fp3 = */ fieldProfileRepo.create(workflowStep, fieldPredicate3, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(wsId);
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        long fp2Id = fp2.getId();
        fp2.setOverrideable(false);

        FieldProfile fp2updatedAtGrandchild = fieldProfileRepo.update(fp2, grandChildOrganization);

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        fp2 = fieldProfileRepo.getById(fp2Id);

        // ensure that a new note got made at the grandchild after this override (to non-overrideable :) )
        // old n2 should still be overrideable
        assertTrue(fp2.getOverrideable(), "FieldProfile updated at grandchild changed the note at the parent!");
        // old n2 should be different from the new n2 updated at the grandchild
        assertFalse(fp2.getId().equals(fp2updatedAtGrandchild.getId()), "FieldProfile updated at grandchild didn't get duplicated!");
        assertEquals(1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().size(), "A new FieldProfile didn't get originated at an org that overrode it!");
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp2updatedAtGrandchild), "A new FieldProfile didn't get originated at an org that overrode it!");

        organization = organizationRepo.getById(organization.getId());

        // TODO: make the note non-overrideable at the child org
        fp2.setOverrideable(false);
        FieldProfile fp2updatedAtChild = fieldProfileRepo.update(fp2, organization);

        organization = organizationRepo.getById(organization.getId());
        fp2 = fieldProfileRepo.getById(fp2Id);

        // ensure that a new note got made at the child after this override (to non-overrideable :) )
        // old n2 should still be overrideable
        assertTrue(fp2.getOverrideable(), "FieldProfile updated at child didn't get duplicated!");
        // old n2 should be different from the new n2 updated at the grandchild
        assertFalse(fp2.getId().equals(fp2updatedAtChild.getId()), "FieldProfile updated at child didn't get duplicated!");

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        assertFalse(grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp2updatedAtChild), "Grand child inherited original field profile from child organization");

        // ensure that the grandchild's new note is replaced by the child's non-overrideable one
        assertTrue(grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().isEmpty(), "FieldProfile made non-overrideable didn't blow away an inferior override at descendant org!");
        assertFalse(grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2updatedAtGrandchild), "FieldProfile made non-overrideable still stuck around at the grandchild who'd overridden it!");

        // TODO: make the note non-overrideable at the parent org
        // TOOO: ensure that the child's note is replaced by the original, non-overrideable at both the child and grandchild

        // now all is restored, except one of the notes is non-overrideable at the parent

        // TODO: remove a note at the grandchild
        // TODO: make the removed note non-overrideable at the parent
        // TODO: ensure that the removed note is reaggregated upon the grandchild who removed it

    }

    // TODO: this test is not done, development of the full feature deferred for now
    @Test
    public void testDeleteFPAtDescendantOrgAndDuplicateWSIsDeletedToo() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);
        organization = organizationRepo.getById(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.getById(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());
        organization = organizationRepo.getById(organization.getId());

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        greatGrandChildOrganization = organizationRepo.getById(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.getById(parentCategory.getId());

        anotherGreatGrandChildOrganization = organizationRepo.getById(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.getById(grandChildOrganization.getId());

        parentOrganization = organizationRepo.getById(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        // put a field profile on the parent org's workflow step
        // and go ahead and put on a couple more field profiles for good measure
        FieldPredicate fieldPredicate2 = fieldPredicateRepo.create("foo.bar", new Boolean(false));
        FieldPredicate fieldPredicate3 = fieldPredicateRepo.create("bar.foo", new Boolean(false));

        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        /* FieldProfile fp2 = */ fieldProfileRepo.create(workflowStep, fieldPredicate2, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        /* FieldProfile fp3 = */ fieldProfileRepo.create(workflowStep, fieldPredicate3, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.getById(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.getById(fieldPredicate.getId());

        organization = organizationRepo.getById(organization.getId());

        // override the field profile at the child
        fieldProfile.setHelp("help!");
        /* FieldProfile fpPrime = */ fieldProfileRepo.update(fieldProfile, organization);

        // TODO: make fieldProfile non-overrideable, check that fpPrime goes away and the new derivative step goes away

    }

    @AfterEach
    public void cleanUp() {

        fieldProfileRepo.findAll().forEach(fieldProfile -> {
            fieldProfileRepo.delete(fieldProfile);
        });

        submissionListColumnRepo.deleteAll();

        inputTypeRepo.deleteAll();

        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });

        organizationRepo.deleteAll();

        organizationCategoryRepo.deleteAll();

        fieldPredicateRepo.deleteAll();
        controlledVocabularyRepo.deleteAll();
    }

}
