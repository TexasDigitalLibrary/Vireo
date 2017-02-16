package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class DocumentTypeTest extends AbstractEntityTest {

	@Override
	public void testCreate() {
		AttachmentType docType = documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME);
        assertEquals("The document type name was wrong!", docType.getName(), TEST_DOCUMENT_TYPE_NAME);
        assertEquals("The associated field predicate was wrong!", docType.getFieldPredicate().getValue(), "_doctype_" + TEST_DOCUMENT_TYPE_NAME.toLowerCase().replace(' ', '_'));
	}

	@Override
	public void testDuplication() {
		documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME);
        try {
			documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The document type was duplicated!", 1, documentTypesRepo.count());
	}

	@Override
	public void testDelete() {
		AttachmentType docType = documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME);
        documentTypesRepo.delete(docType);
        assertEquals("The document type was not deleted!", 0, depositLocationRepo.count());
	}

	@Override
	public void testCascade() {
		AttachmentType docType = documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME);
        assertEquals("The document type was not created!", 1, documentTypesRepo.count());
        assertEquals("The field predicate was not created!", 1, fieldPredicateRepo.count());
        documentTypesRepo.delete(docType);
        assertEquals("The document type was not deleted!", 0, documentTypesRepo.count());
        assertEquals("The field predicate was duplicated!", 0, fieldPredicateRepo.count());
		
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	public void testFieldPredicateDeleteFailure() {
		AttachmentType docType = documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME);
		fieldPredicateRepo.delete(docType.getFieldPredicate());
		fail();
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	public void testDeleteDocumentTypeWhileSubmissionReferencesPredicate() {
		
		// Create a submitter.
		submitter = userRepo.create(
				TEST_SUBMISSION_SUBMITTER_EMAIL, 
				TEST_SUBMISSION_SUBMITTER_FIRSTNAME, 
				TEST_SUBMISSION_SUBMITTER_LASTNAME, 
				TEST_SUBMISSION_SUBMITTER_ROLE);
		
		// Create a submission state
		submissionState = submissionStateRepo.create(
				TEST_SUBMISSION_STATE_NAME, 
				TEST_SUBMISSION_STATE_ARCHIVED, 
				TEST_PARENT_SUBMISSION_STATE_PUBLISHABLE, 
				TEST_SUBMISSION_STATE_DELETABLE, 
				TEST_SUBMISSION_STATE_EDITABLE_BY_REVIEWER, 
				TEST_SUBMISSION_STATE_EDITABLE_BY_STUDENT, 
				TEST_SUBMISSION_STATE_ACTIVE);
		
        assertEquals("The user does not exist!", 1, userRepo.count());

        OrganizationCategory parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());

        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        assertEquals("The organization does not exist!", 1, organizationRepo.count());
       
        //Create a Submission
        Submission submission = submissionRepo.create(submitter, organization, submissionState, getCredentials());
		
		// Create a document type with implicitly created field predicate.
		AttachmentType docType = documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME);
		
		documentTypesRepo.delete(docType);
		fail();
	}
	
    @After
    public void cleanUp() {
    	fieldValueRepo.deleteAll();
        documentTypesRepo.deleteAll();
        submissionRepo.deleteAll();
        organizationCategoryRepo.deleteAll();
        organizationRepo.deleteAll();
        submissionStateRepo.deleteAll();
        namedSearchFilterRepo.findAll().forEach(nsf -> {
            namedSearchFilterRepo.delete(nsf);
        });
        userRepo.deleteAll();
    }


}
