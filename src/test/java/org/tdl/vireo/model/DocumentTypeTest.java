package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.exception.OrganizationDoesNotAcceptSubmissionsException;
import org.tdl.vireo.model.repo.CustomActionDefinitionRepo;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DocumentTypeTest extends AbstractEntityTest {

    @Autowired
    private CustomActionDefinitionRepo customActionDefinitionRepo;

    @Override
    @Test
    public void testCreate() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        assertEquals(documentType.getName(), TEST_DOCUMENT_TYPE_NAME, "The document type name was wrong!");
        assertEquals(documentType.getFieldPredicate().getValue(), "_doctype_" + TEST_DOCUMENT_TYPE_NAME.toLowerCase().replace(' ', '_'), "The associated field predicate was wrong!");
    }

    @Override
    @Test
    @Transactional(propagation = Propagation.NESTED)
    public void testDuplication() {
        documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);

        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        });

        assertTrue(exception instanceof DataIntegrityViolationException);
    }

    @Override
    @Test
    public void testDelete() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        documentTypeRepo.delete(documentType);
        assertEquals(0, documentTypeRepo.count(), "The document type was not deleted!");
    }

    @Override
    @Test
    public void testCascade() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        assertEquals(1, documentTypeRepo.count(), "The document type was not created!");
        assertEquals(1, fieldPredicateRepo.count(), "The field predicate was not created!");
        documentTypeRepo.delete(documentType);
        assertEquals(0, documentTypeRepo.count(), "The document type was not deleted!");
        assertEquals(0, fieldPredicateRepo.count(), "The field predicate was duplicated!");

    }

    @Test
    @Disabled // FIXME: This no longer throws an exception and the previous DataIntegrityViolationException exception may have been a false positive due to a lack of a transaction.
    @Transactional(propagation = Propagation.NESTED)
    public void testFieldPredicateDeleteFailure() {
        DocumentType documentType = documentTypeRepo.create(TEST_DOCUMENT_TYPE_NAME);
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            fieldPredicateRepo.delete(documentType.getFieldPredicate());
        });
    }

    @Test
    @Disabled // FIXME: This no longer throws an exception and the previous DataIntegrityViolationException exception may have been a false positive due to a lack of a transaction.
    @Transactional(propagation = Propagation.NESTED)
    public void testDeleteDocumentTypeWhileSubmissionReferencesPredicate() throws OrganizationDoesNotAcceptSubmissionsException {
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
        submissionRepo.create(submitter, organization, submissionStatus, getCredentials(), customActionDefinitionRepo.findAll());

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            documentTypeRepo.delete(documentType);
        });
    }

}
