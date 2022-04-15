package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;

public class DocumentTypeTest extends AbstractEntityTest {

    @Override
    public void testCreate() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        assertEquals(documentType.getName(), TEST_DOCUMENT_TYPE_NAME, "The document type name was wrong!");
        assertEquals(documentType.getFieldPredicate().getValue(), "_doctype_" + TEST_DOCUMENT_TYPE_NAME.toLowerCase().replace(' ', '_'), "The associated field predicate was wrong!");
    }

    @Override
    public void testDuplication() {
        documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        try {
            documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals(1, documentTypeRepo.count(), "The document type was duplicated!");
    }

    @Override
    public void testDelete() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        documentTypeRepo.delete(documentType);
        assertEquals(0, documentTypeRepo.count(), "The document type was not deleted!");
    }

    @Override
    public void testCascade() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        assertEquals(1, documentTypeRepo.count(), "The document type was not created!");
        assertEquals(1, fieldPredicateRepo.count(), "The field predicate was not created!");
        documentTypeRepo.delete(documentType);
        assertEquals(0, documentTypeRepo.count(), "The document type was not deleted!");
        assertEquals(0, fieldPredicateRepo.count(), "The field predicate was duplicated!");

    }

    @Test
    public void testFieldPredicateDeleteFailure() {
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
            fieldPredicateRepo.delete(documentType.getFieldPredicate());
        });
    }

    @Test
    public void testDeleteDocumentTypeWhileSubmissionReferencesPredicate() throws OrganizationDoesNotAcceptSubmissionsException {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
            assertEquals(1, organizationCategoryRepo.count(), "The category does not exist!");

            organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
            parentCategory = organizationCategoryRepo.findById(parentCategory.getId()).get();
            assertEquals(1, organizationRepo.count(), "The organization does not exist!");

            workflowStep = workflowStepRepo.create(TEST_WORKFLOW_STEP_NAME, organization);
            organization = organizationRepo.findById(organization.getId()).get();
            assertEquals(1, workflowStepRepo.count(), "The workflow step does not exist!");

            submissionWorkflowStep = submissionWorkflowStepRepo.cloneWorkflowStep(workflowStep);

            inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_FILE_NAME);

            // Create a document type with implicitly created field predicate.
            DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);

            fieldPredicate = fieldPredicateRepo.findByValue("_doctype_" + TEST_DOCUMENT_TYPE_NAME.toLowerCase().replace(' ', '_'));

            fieldProfile = fieldProfileRepo.create(workflowStep, fieldPredicate, inputType, TEST_FIELD_PROFILE_USAGE, TEST_GLOSS, TEST_FIELD_PROFILE_REPEATABLE, TEST_FIELD_PROFILE_OVERRIDEABLE, TEST_FIELD_PROFILE_ENABLED, TEST_FIELD_PROFILE_OPTIONAL, TEST_FIELD_PROFILE_FLAGGED, TEST_FIELD_PROFILE_LOGGED, TEST_FIELD_PROFILE_DEFAULT_VALUE);
            assertEquals(1, fieldProfileRepo.count(), "The field profile does not exist!");

            // Create a submitter.
            submitter = userRepo.create(TEST_SUBMISSION_SUBMITTER_EMAIL, TEST_SUBMISSION_SUBMITTER_FIRSTNAME, TEST_SUBMISSION_SUBMITTER_LASTNAME, TEST_SUBMISSION_SUBMITTER_ROLE);

            // Create a submission state
            submissionStatus = submissionStatusRepo.create(TEST_SUBMISSION_STATUS_NAME, TEST_SUBMISSION_STATUS_ARCHIVED, TEST_PARENT_SUBMISSION_STATUS_PUBLISHABLE, TEST_SUBMISSION_STATUS_DELETABLE, TEST_SUBMISSION_STATUS_EDITABLE_BY_REVIEWER, TEST_SUBMISSION_STATUS_EDITABLE_BY_STUDENT, TEST_SUBMISSION_STATUS_ACTIVE, null);

            assertEquals(1, userRepo.count(), "The user does not exist!");

            // Create a Submission
            submissionRepo.create(submitter, organization, submissionStatus, getCredentials());

            documentTypeRepo.delete(documentType);
        });
    }

    @AfterEach
    public void cleanUp() {
        submissionRepo.deleteAll();
        submissionStatusRepo.deleteAll();
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
        submissionListColumnRepo.deleteAll();
        inputTypeRepo.deleteAll();
        embargoRepo.deleteAll();
        namedSearchFilterGroupRepo.findAll().forEach(nsf -> {
            namedSearchFilterGroupRepo.delete(nsf);
        });
        userRepo.deleteAll();
    }

}
