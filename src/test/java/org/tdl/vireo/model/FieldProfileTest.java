package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.HeritableModelNonOverrideableException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;

public class FieldProfileTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("The repository was not empty!", 0, languageRepo.count());
        assertEquals("The repository was not empty!", 0, fieldProfileRepo.count());
        assertEquals("The repository was not empty!", 0, fieldPredicateRepo.count());
        language = languageRepo.create(TEST_LANGUAGE);
        fieldPredicate = fieldPredicateRepo.create(TEST_FIELD_PREDICATE_VALUE, new Boolean(false));
        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.findOne(organization.getId());
        inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);
    }

    @Override
    public void testCreate() {
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        assertEquals("The repository did not save the entity!", 1, fieldProfileRepo.count());
        assertEquals("The field profile did not contain the correct perdicate value!", fieldPredicate, fieldProfile.getFieldPredicate());
        assertEquals("The field predicate did not contain the correct value!", inputType, fieldProfile.getInputType());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_USAGE, fieldProfile.getUsage());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_REPEATABLE, fieldProfile.getRepeatable());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_ENABLED, fieldProfile.getEnabled());
        assertEquals("The field predicate did not contain the correct value!", TEST_FIELD_PROFILE_OPTIONAL, fieldProfile.getOptional());
    }

    @Override
    public void testDuplication() {
        fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        try {
            fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The repository duplicated entity!", 1, fieldProfileRepo.count());
    }

    @Override
    public void testDelete() {
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        fieldProfileRepo.delete(fieldProfile);
        assertEquals("Entity did not delete!", 0, fieldProfileRepo.count());
    }

    @Override
    public void testCascade() {
        // create field profile
        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        // add glosses and controlled vocabularies
        FieldGloss fieldGloss = fieldGlossRepo.create(TEST_FIELD_GLOSS_VALUE, language);
        FieldGloss severableFieldGloss = fieldGlossRepo.create(TEST_SEVERABLE_FIELD_GLOSS_VALUE, language);
        ControlledVocabulary controlledVocabulary = controlledVocabularyRepo.create(TEST_CONTROLLED_VOCABULARY_NAME, language);
        ControlledVocabulary severablecontrolledVocabulary = controlledVocabularyRepo.create(TEST_SEVERABLE_CONTROLLED_VOCABULARY_NAME, language);
        fieldProfile.addFieldGloss(fieldGloss);
        fieldProfile.addControlledVocabulary(controlledVocabulary);
        fieldProfile.addFieldGloss(severableFieldGloss);
        fieldProfile.addControlledVocabulary(severablecontrolledVocabulary);
        fieldProfile = fieldProfileRepo.save(fieldProfile);

        // verify field glosses and controlled vocabularies
        assertEquals("The field profile did not contain the correct field gloss!", fieldGloss, fieldProfile.getFieldGlossByValueAndLanguage(TEST_FIELD_GLOSS_VALUE, language));
        assertEquals("The field profile did not contain the correct severable field gloss!", severableFieldGloss, fieldProfile.getFieldGlossByValueAndLanguage(TEST_SEVERABLE_FIELD_GLOSS_VALUE, language));
        assertEquals("The field profile did not contain the correct controlled vocabulary!", controlledVocabulary, fieldProfile.getControlledVocabularyByName(TEST_CONTROLLED_VOCABULARY_NAME));
        assertEquals("The field profile did not contain the correct severable controlled vocabulary!", severablecontrolledVocabulary, fieldProfile.getControlledVocabularyByName(TEST_SEVERABLE_CONTROLLED_VOCABULARY_NAME));

        // test remove severable gloss
        fieldProfile.removeFieldGloss(severableFieldGloss);
        fieldProfile = fieldProfileRepo.save(fieldProfile);
        assertEquals("The field profile had the incorrect number of glosses!", 1, fieldProfile.getFieldGlosses().size());

        // test remove severable controlled vocabularies
        fieldProfile.removeControlledVocabulary(severablecontrolledVocabulary);
        fieldProfile = fieldProfileRepo.save(fieldProfile);
        assertEquals("The field profile had the incorrect number of glosses!", 1, fieldProfile.getControlledVocabularies().size());

        // test delete profile
        fieldProfileRepo.delete(fieldProfile);
        assertEquals("An field profile was not deleted!", 0, fieldProfileRepo.count());
        assertEquals("The language was deleted!", 1, languageRepo.count());
        assertEquals("The field predicate was deleted!", 1, fieldPredicateRepo.count());
        assertEquals("The field glosses were deleted!", 2, fieldGlossRepo.count());
        assertEquals("The controlled vocabularies were deleted!", 2, controlledVocabularyRepo.count());

    }

    @Test
    @Transactional
    public void testInheritFieldProfileViaPointer() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        parentOrganization.addChildOrganization(childOrganization);
        parentOrganization = organizationRepo.save(parentOrganization);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());

        Organization grandchildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        grandchildOrganization = organizationRepo.findOne(grandchildOrganization.getId());

        childOrganization.addChildOrganization(grandchildOrganization);
        childOrganization = organizationRepo.save(childOrganization);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        WorkflowStep parentWorkflowStep = workflowStepRepo.create(TEST_PARENT_WORKFLOW_STEP_NAME, parentOrganization);

        FieldProfile fieldProfile = fieldProfileRepo.create(parentWorkflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        childOrganization = organizationRepo.findOne(childOrganization.getId());
        grandchildOrganization = organizationRepo.findOne(grandchildOrganization.getId());

        FieldProfile parentFieldProfile = parentOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().get(0);
        FieldProfile childFieldProfile = childOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().get(0);
        FieldProfile grandchildFieldProfile = grandchildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().get(0);

        assertTrue("The parent organization's workflow did not contain the original fieldProfile", parentOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fieldProfile));
        assertTrue("The child organization's workflow did not contain the original fieldProfile", childOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fieldProfile));
        assertTrue("The grandchild organization's workflow did not contain the original fieldProfile", grandchildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fieldProfile));

        assertTrue("The parent organization's workflow did not contain the aggregate fieldProfile", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fieldProfile));
        assertTrue("The child organization's workflow did not contain the aggregate fieldProfile", childOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fieldProfile));
        assertTrue("The grandchild organization's workflow did not contain the aggregate fieldProfile", grandchildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fieldProfile));

        assertEquals("The parent organization's workflow did not contain the fieldProfile's predicate", fieldProfile.getFieldPredicate().getId(), parentFieldProfile.getFieldPredicate().getId());
        assertEquals("The child organization's workflow did not contain the fieldProfile's predicate", fieldProfile.getFieldPredicate().getId(), childFieldProfile.getFieldPredicate().getId());
        assertEquals("The grandchild organization's workflow did not contain the fieldProfile's predicate", fieldProfile.getFieldPredicate().getId(), childFieldProfile.getFieldPredicate().getId());

        String updatedFieldPredicateValue = "updated.value";
        parentFieldProfile.getFieldPredicate().setValue(updatedFieldPredicateValue);

        fieldProfileRepo.update(parentFieldProfile, parentOrganization);

        childFieldProfile = fieldProfileRepo.findOne(childFieldProfile.getId());
        grandchildFieldProfile = fieldProfileRepo.findOne(grandchildFieldProfile.getId());

        // *********************************************************************************************************************************************************//
        // *********************************************************************************************************************************************************//
        // *********************************************************************************************************************************************************//
        assertEquals("The child fieldProfile's value did not recieve updated value", updatedFieldPredicateValue, childFieldProfile.getFieldPredicate().getValue());
        assertEquals("The grand child fieldProfile's value did not recieve updated value", updatedFieldPredicateValue, grandchildFieldProfile.getFieldPredicate().getValue());
        // *********************************************************************************************************************************************************//
        // *********************************************************************************************************************************************************//
        // *********************************************************************************************************************************************************//

    }

    @Test(expected = HeritableModelNonOverrideableException.class)
    public void testCantOverrideNonOverrideable() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        workflowStep = workflowStepRepo.findOne(workflowStep.getId());

        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        fieldProfile.setOverrideable(false);

        organization = organizationRepo.findOne(organization.getId());

        // actually set the note to non overrideable on parent first
        fieldProfile = fieldProfileRepo.update(fieldProfile, organization);

        assertFalse("The field profile was not made non-overrideable!", fieldProfile.getOverrideable());

        childOrganization = organizationRepo.findOne(childOrganization.getId());

        fieldProfile.getFieldPredicate().setValue("Updated Value");

        fieldProfileRepo.update(fieldProfile, childOrganization);
    }

    @Test(expected = WorkflowStepNonOverrideableException.class)
    public void testCantOverrideNonOverrideableWorkflowStep() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        Organization childOrganization = organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        workflowStep = workflowStepRepo.findOne(workflowStep.getId());

        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        organization = organizationRepo.findOne(organization.getId());
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());

        workflowStep.setOverrideable(false);

        workflowStep = workflowStepRepo.update(workflowStep, organization);

        fieldProfile = fieldProfileRepo.findOne(fieldProfile.getId());

        childOrganization = organizationRepo.findOne(childOrganization.getId());

        workflowStep = workflowStepRepo.findOne(workflowStep.getId());

        assertEquals("The field profile's originating workflow step is not the intended workflow step!", workflowStep, fieldProfile.getOriginatingWorkflowStep());

        assertFalse("The field profile's originating workflow step was not made non-overrideable!", fieldProfile.getOriginatingWorkflowStep().getOverrideable());

        assertFalse("The workflowstep was not made non-overrideable!", workflowStep.getOverrideable());

        fieldProfile.getFieldPredicate().setValue("Updated Value");

        fieldProfileRepo.update(fieldProfile, childOrganization);
    }

    @Test
    public void testCanOverrideNonOverrideableAtOriginatingOrg() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        organizationRepo.create(TEST_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        workflowStep = workflowStepRepo.findOne(workflowStep.getId());

        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        organization = organizationRepo.findOne(organization.getId());

        fieldProfile.setOverrideable(false);

        // actually set the note to non overrideable on parent first
        fieldProfile = fieldProfileRepo.update(fieldProfile, organization);

        String helpTest = "Help!";

        fieldProfile.setHelp(helpTest);

        assertTrue("The setter didn't work for help string on the FieldProfile!", fieldProfile.getHelp().equals(helpTest));

        assertFalse("The field profile didn't record that it was made non-overrideable!", fieldProfile.getOverrideable());

        fieldProfile = fieldProfileRepo.update(fieldProfile, organization);

        assertTrue("The field profile wasn't updated to include the changed help!", fieldProfile.getHelp().equals(helpTest));

    }

    @Test
    public void testInheritAndRemoveFieldProfiles() {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.findOne(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findOne(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals("parentOrganization's workflow step has the incorrect number of field profiles!", 0, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().size());
        assertEquals("parentOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!", 0, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());

        assertEquals("organization's aggregate workflow step has the incorrect number of aggregate field profiles!", 0, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());

        assertEquals("grandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!", 0, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        FieldPredicate fieldPredicate2 = fieldPredicateRepo.create("foo.bar", new Boolean(false));
        FieldPredicate fieldPredicate3 = fieldPredicateRepo.create("bar.foo", new Boolean(false));

        FieldProfile fp = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        FieldProfile fp2 = fieldProfileRepo.create(workflowStep, fieldPredicate2, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        FieldProfile fp3 = fieldProfileRepo.create(workflowStep, fieldPredicate3, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals("parentOrganization's workflow step has the incorrect number of field profiles!", 3, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().size());
        assertTrue("parentOrganization's workflow step's did not contain field profile 1!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp));
        assertTrue("parentOrganization's workflow step's did not contain field profile 2!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp2));
        assertTrue("parentOrganization's workflow step's did not contain field profile 3!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp3));

        assertEquals("parentOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!", 3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());
        assertTrue("parentOrganization's aggregate workflow step's did not contain field profile 1!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp));
        assertTrue("parentOrganization's aggregate workflow step's did not contain field profile 2!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("parentOrganization's aggregate workflow step's did not contain field profile 3!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        assertEquals("organization's aggregate workflow step has the incorrect number of aggregate field profiles!", 3, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());
        assertTrue("organization's aggregate workflow step's did not contain field profile 1!", organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp));
        assertTrue("organization's aggregate workflow step's did not contain field profile 2!", organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("organization's aggregate workflow step's did not contain field profile 3!", organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        assertEquals("grandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!", 3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());
        assertTrue("grandChildOrganization's aggregate workflow step's did not contain field profile 1!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp));
        assertTrue("grandChildOrganization's aggregate workflow step's did not contain field profile 2!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("grandChildOrganization's aggregate workflow step's did not contain field profile 3!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals("greatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!", 3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());
        assertTrue("greatGrandChildOrganization's aggregate workflow step's did not contain field profile 1!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp));
        assertTrue("greatGrandChildOrganization's aggregate workflow step's did not contain field profile 2!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("greatGrandChildOrganization's aggregate workflow step's did not contain field profile 3!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        assertEquals("anotherGreatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!", 3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());
        assertTrue("anotherGreatGrandChildOrganization's aggregate workflow step's did not contain field profile 1!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp));
        assertTrue("anotherGreatGrandChildOrganization's aggregate workflow step's did not contain field profile 2!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("anotherGreatGrandChildOrganization's aggregate workflow step's did not contain field profile 3!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        parentOrganization.getOriginalWorkflowSteps().get(0).removeOriginalFieldProfile(fp);
        parentOrganization = organizationRepo.save(parentOrganization);

        fieldProfileRepo.delete(fp);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals("parentOrganization's workflow step has the incorrect number of field profiles!", 2, parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().size());
        assertFalse("parentOrganization's workflow step's still contains field profile 1!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp));
        assertTrue("parentOrganization's workflow step's did not contain field profile 2!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp2));
        assertTrue("parentOrganization's workflow step's did not contain field profile 3!", parentOrganization.getOriginalWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp3));

        assertEquals("parentOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!", 2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());
        assertFalse("parentOrganization's aggregate workflow step's still contains field profile 1!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp));
        assertTrue("parentOrganization's aggregate workflow step's did not contain field profile 2!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("parentOrganization's aggregate workflow step's did not contain field profile 3!", parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        assertEquals("organization's aggregate workflow step has the incorrect number of aggregate field profiles!", 2, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());
        assertFalse("organization's aggregate workflow step's still contains field profile 1!", organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp));
        assertTrue("organization's aggregate workflow step's did not contain field profile 2!", organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("organization's aggregate workflow step's did not contain field profile 3!", organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        assertEquals("grandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!", 2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());
        assertFalse("grandChildOrganization's aggregate workflow step's still contains field profile 1!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp));
        assertTrue("grandChildOrganization's aggregate workflow step's did not contain field profile 2!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("grandChildOrganization's aggregate workflow step's did not contain field profile 3!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        assertEquals("greatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!", 2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());
        assertFalse("greatGrandChildOrganization's aggregate workflow step's still contains field profile 1!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp));
        assertTrue("greatGrandChildOrganization's aggregate workflow step's did not contain field profile 2!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("greatGrandChildOrganization's aggregate workflow step's did not contain field profile 3!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        assertEquals("anotherGreatGrandChildOrganization's aggregate workflow step has the incorrect number of aggregate field profiles!", 2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().size());
        assertFalse("anotherGreatGrandChildOrganization's aggregate workflow step's still contains field profile 1!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp));
        assertTrue("anotherGreatGrandChildOrganization's aggregate workflow step's did not contain field profile 2!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("anotherGreatGrandChildOrganization's aggregate workflow step's did not contain field profile 3!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    @Test
    public void testReorderAggregateFieldProfiles() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.findOne(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findOne(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        FieldPredicate fieldPredicate2 = fieldPredicateRepo.create("foo.bar", new Boolean(false));
        FieldPredicate fieldPredicate3 = fieldPredicateRepo.create("bar.foo", new Boolean(false));

        FieldProfile fp1 = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        FieldProfile fp2 = fieldProfileRepo.create(workflowStep, fieldPredicate2, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        FieldProfile fp3 = fieldProfileRepo.create(workflowStep, fieldPredicate3, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        Long fp1Id = fp1.getId();
        Long fp2Id = fp2.getId();
        Long fp3Id = fp3.getId();

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());

        assertEquals("The parentOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The parentOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The parentOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The organization's aggregate workflow step's first aggregate field profile was not as expected!", fp1, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The organization's aggregate workflow step's second aggregate field profile was not as expected!", fp2, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The organization's aggregate workflow step's third aggregate field profile was not as expected!", fp3, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The grandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The grandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The grandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The greatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        fp1 = fieldProfileRepo.findOne(fp1Id);
        fp2 = fieldProfileRepo.findOne(fp2Id);

        workflowStep = workflowStepRepo.swapFieldProfiles(parentOrganization, workflowStep, fp1, fp2);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());

        assertEquals("The parentOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The parentOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The parentOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The organization's aggregate workflow step's first aggregate field profile was not as expected!", fp2, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The organization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The organization's aggregate workflow step's third aggregate field profile was not as expected!", fp3, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The grandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The grandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The grandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The greatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        fp2 = fieldProfileRepo.findOne(fp2Id);
        fp3 = fieldProfileRepo.findOne(fp3Id);

        workflowStep = workflowStepRepo.swapFieldProfiles(parentOrganization, workflowStep, fp2, fp3);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());

        assertEquals("The parentOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The parentOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The parentOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The organization's aggregate workflow step's first aggregate field profile was not as expected!", fp3, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The organization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The organization's aggregate workflow step's third aggregate field profile was not as expected!", fp2, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The grandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The grandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The grandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The greatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        fp1 = fieldProfileRepo.findOne(fp1Id);
        fp3 = fieldProfileRepo.findOne(fp3Id);

        // creates a new workflow step
        workflowStepRepo.swapFieldProfiles(organization, workflowStep, fp1, fp3);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());

        WorkflowStep newWorkflowStep = organization.getOriginalWorkflowSteps().get(0);

        assertEquals("The parentOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp3, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The parentOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp1, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The parentOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp2, parentOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        // make sure new workflow step contains all field profiles
        assertTrue("The organization's original workflow step's contains first field profile!", organization.getOriginalWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp1));
        assertTrue("The organization's original workflow step's contains second field profile!!", organization.getOriginalWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2));
        assertTrue("The organization's original workflow step's contains third field profile!!", organization.getOriginalWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp3));

        assertEquals("The organization aggregate workflow steps does not have new workflow step from reorder on non originating organization!", newWorkflowStep, organization.getAggregateWorkflowSteps().get(0));

        assertEquals("The organization's aggregate workflow step's first aggregate field profile was not as expected!", fp1, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The organization's aggregate workflow step's second aggregate field profile was not as expected!", fp3, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The organization's aggregate workflow step's third aggregate field profile was not as expected!", fp2, organization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The grandChildOrganization did not inherit new workflow step from reorder on non originating organization!", newWorkflowStep, grandChildOrganization.getAggregateWorkflowSteps().get(0));

        assertEquals("The grandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The grandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp3, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The grandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp2, grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The greatGrandChildOrganization did not inherit new workflow step from reorder on non originating organization!", newWorkflowStep, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0));

        assertEquals("The greatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp1, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp3, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The greatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp2, greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

        assertEquals("The anotherGreatGrandChildOrganization did not inherit new workflow step from reorder on non originating organization!", newWorkflowStep, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0));

        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's first aggregate field profile was not as expected!", fp1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(0));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's second aggregate field profile was not as expected!", fp3, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1));
        assertEquals("The anotherGreatGrandChildOrganization's aggregate workflow step's third aggregate field profile was not as expected!", fp2, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(2));

    }

    @Test
    public void testFieldProfileChangeAtChildOrg() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.findOne(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findOne(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());

        organization.addChildOrganization(grandChildOrganization);
        organization = organizationRepo.save(organization);

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        grandChildOrganization.addChildOrganization(greatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        grandChildOrganization.addChildOrganization(anotherGreatGrandChildOrganization);
        grandChildOrganization = organizationRepo.save(grandChildOrganization);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);

        Long originalFieldProfileId = fieldProfile.getId();

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        assertEquals("Parent organization has the incorrect number of workflow steps!", 1, parentOrganization.getOriginalWorkflowSteps().size());
        assertEquals("Parent organization has wrong size of workflow!", 1, parentOrganization.getAggregateWorkflowSteps().size());

        assertEquals("organization has the incorrect number of workflow steps!", 0, organization.getOriginalWorkflowSteps().size());
        assertEquals("organization has wrong size of workflow!", 1, organization.getAggregateWorkflowSteps().size());

        assertEquals("Grand child organization has the incorrect number of workflow steps!", 0, grandChildOrganization.getOriginalWorkflowSteps().size());
        assertEquals("Grand child organization has wrong size of workflow!", 1, grandChildOrganization.getAggregateWorkflowSteps().size());

        assertEquals("Great grand child organization has the incorrect number of workflow steps!", 0, greatGrandChildOrganization.getOriginalWorkflowSteps().size());
        assertEquals("Great grand child organization has wrong size of workflow!", 1, greatGrandChildOrganization.getAggregateWorkflowSteps().size());

        assertEquals("Another great grand child organization has the incorrect number of workflow steps!", 0, anotherGreatGrandChildOrganization.getOriginalWorkflowSteps().size());
        assertEquals("Another great grand child organization has wrong size of workflow!", 1, anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().size());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        fieldProfile.setHelp("Changed Help Message");

        // request the change at the level of the child organization
        FieldProfile updatedFieldProfile = fieldProfileRepo.update(fieldProfile, organization);

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());

        // pointer to fieldProfile became updatedFieldProfile, must fetch it agains
        fieldProfile = fieldProfileRepo.findOne(originalFieldProfileId);

        // There should be a new workflow step on the child organization that is distinct from the original workflowStep
        WorkflowStep updatedWorkflowStep = organization.getAggregateWorkflowSteps().get(0);
        assertFalse("The updatedWorkflowStep was just the same as the original from which it was derived when its field profile was updated!", workflowStep.getId().equals(updatedWorkflowStep.getId()));

        // The new workflow step should contain the new updatedFieldProfile
        assertTrue("The updatedWorkflowStep didn't contain the new updatedFieldProfile", updatedWorkflowStep.getAggregateFieldProfiles().contains(updatedFieldProfile));

        // The updatedFieldProfile should be distinct from the original fieldProfile
        assertFalse("The updatedFieldProfile was just the same as the original from which it was derived!", fieldProfile.getId().equals(updatedFieldProfile.getId()));

        // the grandchild and great grandchildren should all be using the new workflow step and the updatedFieldProfile
        assertTrue("The grandchild org didn't have the updatedWorkflowStep!", grandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep));
        assertTrue("The grandchild org didn't have the updatedFieldProfile on the updatedWorkflowStep!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(updatedFieldProfile));
        assertTrue("The great grandchild org didn't have the updatedWorkflowStep!", greatGrandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep));
        assertTrue("The great grandchild org didn't have the updatedFieldProfile on the updatedWorkflowStep!", greatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(updatedFieldProfile));
        assertTrue("Another great grandchild org didn't have the updatedWorkflowStep!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().contains(updatedWorkflowStep));
        assertTrue("Another great grandchild org didn't have the updatedFieldProfile on the updatedWorkflowStep!", anotherGreatGrandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(updatedFieldProfile));
    }

    @Test
    public void testMaintainFieldOrderWhenOverriding() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.findOne(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findOne(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        FieldPredicate fieldPredicate2 = fieldPredicateRepo.create("foo.bar", new Boolean(false));
        FieldPredicate fieldPredicate3 = fieldPredicateRepo.create("bar.foo", new Boolean(false));

        fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        FieldProfile fp2 = fieldProfileRepo.create(workflowStep, fieldPredicate2, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        fieldProfileRepo.create(workflowStep, fieldPredicate3, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        // now, override the second step at the grandchild and ensure that the new step is the second step at the grandchild and at the great grandchildren
        fp2.setHelp("help!");
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        FieldProfile fp2Updated = fieldProfileRepo.update(fp2, grandChildOrganization);
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());

        WorkflowStep newWSWithNewFPViaAggregation = grandChildOrganization.getAggregateWorkflowSteps().get(0);
        WorkflowStep newWSWithNewFPViaOriginals = grandChildOrganization.getOriginalWorkflowSteps().get(0);
        assertEquals("The new aggregated workflow step on the grandchild org was not the one the grandchild org just originated!", newWSWithNewFPViaOriginals, newWSWithNewFPViaAggregation);

        assertEquals("Updated field profile was in the wrong order!", fp2Updated.getId(), grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().get(1).getId());

    }

    // TODO: this test is not done, development of the full feature deferred for now
    @Test
    public void testMakeFieldProfileNonOverrideable() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {

        // this test calls for adding a single workflowstep to the parent organization
        workflowStepRepo.delete(workflowStep);

        organization = organizationRepo.findOne(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findOne(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);
        Long wsId = workflowStep.getId();

        // put a field profile on the parent org's workflow step
        FieldPredicate fieldPredicate2 = fieldPredicateRepo.create("foo.bar", new Boolean(false));
        FieldPredicate fieldPredicate3 = fieldPredicateRepo.create("bar.foo", new Boolean(false));

        /* FieldProfile fp1 = */ fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(wsId);
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        FieldProfile fp2 = fieldProfileRepo.create(workflowStep, fieldPredicate2, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(wsId);
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        /* FieldProfile fp3 = */ fieldProfileRepo.create(workflowStep, fieldPredicate3, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(wsId);
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        long fp2Id = fp2.getId();
        fp2.setOverrideable(false);

        FieldProfile fp2updatedAtGrandchild = fieldProfileRepo.update(fp2, grandChildOrganization);

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        fp2 = fieldProfileRepo.findOne(fp2Id);

        // ensure that a new note got made at the grandchild after this override (to non-overrideable :) )
        // old n2 should still be overrideable
        assertTrue("FieldProfile updated at grandchild changed the note at the parent!", fp2.getOverrideable());
        // old n2 should be different from the new n2 updated at the grandchild
        assertFalse("FieldProfile updated at grandchild didn't get duplicated!", fp2.getId().equals(fp2updatedAtGrandchild.getId()));
        assertEquals("A new FieldProfile didn't get originated at an org that overrode it!", 1, grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().size());
        assertTrue("A new FieldProfile didn't get originated at an org that overrode it!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp2updatedAtGrandchild));

        organization = organizationRepo.findOne(organization.getId());

        // TODO: make the note non-overrideable at the child org
        fp2.setOverrideable(false);
        FieldProfile fp2updatedAtChild = fieldProfileRepo.update(fp2, organization);

        organization = organizationRepo.findOne(organization.getId());
        fp2 = fieldProfileRepo.findOne(fp2Id);

        // ensure that a new note got made at the child after this override (to non-overrideable :) )
        // old n2 should still be overrideable
        assertTrue("FieldProfile updated at child didn't get duplicated!", fp2.getOverrideable());
        // old n2 should be different from the new n2 updated at the grandchild
        assertFalse("FieldProfile updated at child didn't get duplicated!", fp2.getId().equals(fp2updatedAtChild.getId()));

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        assertFalse("Grand child inherited original field profile from child organization", grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().contains(fp2updatedAtChild));

        // ensure that the grandchild's new note is replaced by the child's non-overrideable one
        assertTrue("FieldProfile made non-overrideable didn't blow away an inferior override at descendant org!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getOriginalFieldProfiles().isEmpty());
        assertFalse("FieldProfile made non-overrideable still stuck around at the grandchild who'd overridden it!", grandChildOrganization.getAggregateWorkflowSteps().get(0).getAggregateFieldProfiles().contains(fp2updatedAtGrandchild));

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
        organization = organizationRepo.findOne(organization.getId());

        Organization parentOrganization = organizationRepo.create(TEST_PARENT_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        parentOrganization.addChildOrganization(organization);
        parentOrganization = organizationRepo.save(parentOrganization);

        organization = organizationRepo.findOne(organization.getId());

        Organization grandChildOrganization = organizationRepo.create(TEST_GRAND_CHILD_ORGANIZATION_NAME, organization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());
        organization = organizationRepo.findOne(organization.getId());

        Organization greatGrandChildOrganization = organizationRepo.create("TestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        greatGrandChildOrganization = organizationRepo.findOne(greatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        Organization anotherGreatGrandChildOrganization = organizationRepo.create("AnotherTestGreatGrandchildOrganizationName", grandChildOrganization, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());

        anotherGreatGrandChildOrganization = organizationRepo.findOne(anotherGreatGrandChildOrganization.getId());
        grandChildOrganization = organizationRepo.findOne(grandChildOrganization.getId());

        parentOrganization = organizationRepo.findOne(parentOrganization.getId());

        WorkflowStep workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, parentOrganization);

        // put a field profile on the parent org's workflow step
        // and go ahead and put on a couple more field profiles for good measure
        FieldPredicate fieldPredicate2 = fieldPredicateRepo.create("foo.bar", new Boolean(false));
        FieldPredicate fieldPredicate3 = fieldPredicateRepo.create("bar.foo", new Boolean(false));

        FieldProfile fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        /* FieldProfile fp2 = */ fieldProfileRepo.create(workflowStep, fieldPredicate2, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        /* FieldProfile fp3 = */ fieldProfileRepo.create(workflowStep, fieldPredicate3, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
        workflowStep = workflowStepRepo.findOne(workflowStep.getId());
        fieldPredicate = fieldPredicateRepo.findOne(fieldPredicate.getId());

        organization = organizationRepo.findOne(organization.getId());

        // override the field profile at the child
        fieldProfile.setHelp("help!");
        /* FieldProfile fpPrime = */ fieldProfileRepo.update(fieldProfile, organization);

        // TODO: make fieldProfile non-overrideable, check that fpPrime goes away and the new derivative step goes away

    }

    @After
    public void cleanUp() {

        fieldProfileRepo.findAll().forEach(fieldProfile -> {
            fieldProfileRepo.delete(fieldProfile);
        });

        inputTypeRepo.deleteAll();

        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });

        organizationCategoryRepo.deleteAll();

        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });

        fieldPredicateRepo.deleteAll();
        fieldGlossRepo.deleteAll();
        controlledVocabularyRepo.deleteAll();
        languageRepo.deleteAll();
    }

}
