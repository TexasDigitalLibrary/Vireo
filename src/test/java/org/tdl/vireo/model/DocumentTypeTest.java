package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsExcception;

public class DocumentTypeTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        assertEquals("The document type name was wrong!", documentType.getName(), TEST_DOCUMENT_TYPE_NAME);
        assertEquals("The associated field predicate was wrong!", documentType.getFieldPredicate().getValue(), "_doctype_" + TEST_DOCUMENT_TYPE_NAME.toLowerCase().replace(' ', '_'));
    }

    @Override
    public void testDuplication() {
        documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        try {
            documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The document type was duplicated!", 1, documentTypeRepo.count());
    }

    @Override
    public void testDelete() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        documentTypeRepo.delete(documentType);
        assertEquals("The document type was not deleted!", 0, documentTypeRepo.count());
    }

    @Override
    public void testCascade() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        assertEquals("The document type was not created!", 1, documentTypeRepo.count());
        assertEquals("The field predicate was not created!", 1, fieldPredicateRepo.count());
        documentTypeRepo.delete(documentType);
        assertEquals("The document type was not deleted!", 0, documentTypeRepo.count());
        assertEquals("The field predicate was duplicated!", 0, fieldPredicateRepo.count());

    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testFieldPredicateDeleteFailure() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        fieldPredicateRepo.delete(documentType.getFieldPredicate());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testDeleteDocumentTypeWhileSubmissionReferencesPredicate() throws OrganizationDoesNotAcceptSubmissionsExcception {

        parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());

        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        assertEquals("The organization does not exist!", 1, organizationRepo.count());

        workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
        organization = organizationRepo.findOne(organization.getId());
        assertEquals("The workflow step does not exist!", 1, workflowStepRepo.count());

        submissionWorkflowStep = submissionWorkflowStepRepo.cloneWorkflowStep(workflowStep);

        inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_FILE_NAME);

        // Create a document type with implicitly created field predicate.
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);

        fieldPredicate = fieldPredicateRepo.findByValue("_doctype_" + TEST_DOCUMENT_TYPE_NAME.toLowerCase().replace(' ', '_'));

        fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED);
        assertEquals("The field profile does not exist!", 1, fieldProfileRepo.count());

        // Create a submitter.
        submitter = userRepo.create(TEST_SUBMISSION_SUBMITTER_EMAIL, TEST_SUBMISSION_SUBMITTER_FIRSTNAME, TEST_SUBMISSION_SUBMITTER_LASTNAME, TEST_SUBMISSION_SUBMITTER_ROLE);

        // Create a submission state
        submissionState = submissionStateRepo.create(TEST_SUBMISSION_STATE_NAME, TEST_SUBMISSION_STATE_ARCHIVED, TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, TEST_SUBMISSION_STATE_DELETABLE, TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATE_ACTIVE, null);

        assertEquals("The user does not exist!", 1, userRepo.count());

        // Create a Submission
        submissionRepo.create(submitter, organization, submissionState, getCredentials());

        documentTypeRepo.delete(documentType);
    }

    @After
    public void cleanUp() {
        submissionRepo.deleteAll();
        submissionStateRepo.deleteAll();
        customActionValueRepo.deleteAll();
        customActionDefinitionRepo.deleteAll();
        workflowStepRepo.findAll().forEach(workflowStep -> {
            workflowStepRepo.delete(workflowStep);
        });
        submissionWorkflowStepRepo.deleteAll();
        actionLogRepo.deleteAll();
        fieldValueRepo.deleteAll();
        organizationRepo.findAll().forEach(organization -> {
            organizationRepo.delete(organization);
        });
        organizationCategoryRepo.deleteAll();
        fieldProfileRepo.findAll().forEach(fieldProfile -> {
            fieldProfileRepo.delete(fieldProfile);
        });
        submissionFieldProfileRepo.findAll().forEach(fieldProfile -> {
            submissionFieldProfileRepo.delete(fieldProfile);
        });
        documentTypeRepo.deleteAll();
        fieldPredicateRepo.deleteAll();
        inputTypeRepo.deleteAll();
        embargoRepo.deleteAll();
        namedSearchFilterRepo.findAll().forEach(nsf -> {
            namedSearchFilterRepo.delete(nsf);
        });
        userRepo.deleteAll();
    }

}
