package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.tdl.vireo.enums.DegreeLevel;

import edu.tamu.framework.model.Credentials;

public class DocumentTypeTest extends AbstractEntityTest{

	@Override
	public void testCreate() {
		DocumentType docType = documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME, DegreeLevel.MASTERS);
        assertEquals("The document type name was wrong!", docType.getName(), TEST_DOCUMENT_TYPE_NAME);
        assertEquals("The degree level enum was wrong!", docType.getDegreeLevel(), DegreeLevel.MASTERS);
        assertEquals("The associated field predicate was wrong!", docType.getFieldPredicate().getValue(), "_docType_" + TEST_DOCUMENT_TYPE_NAME);
	}

	@Override
	public void testDuplication() {
		documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME, DegreeLevel.MASTERS);
        try {
			documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME, DegreeLevel.MASTERS);
        } 
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The document type was duplicated!", 1, documentTypesRepo.count());
	}

	@Override
	public void testDelete() {
		DocumentType docType = documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME, DegreeLevel.MASTERS);
        documentTypesRepo.delete(docType);
        assertEquals("The document type was not deleted!", 0, depositLocationRepo.count());
	}

	@Override
	public void testCascade() {
		DocumentType docType = documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME, DegreeLevel.MASTERS);
        assertEquals("The document type was not created!", 1, documentTypesRepo.count());
        assertEquals("The field predicate was not created!", 1, fieldPredicateRepo.count());
        documentTypesRepo.delete(docType);
        assertEquals("The document type was not deleted!", 0, documentTypesRepo.count());
        assertEquals("The field predicate was duplicated!", 0, fieldPredicateRepo.count());
		
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	public void testFieldPredicateDeleteFailure() {
		DocumentType docType = documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME, DegreeLevel.MASTERS);
		fieldPredicateRepo.delete(docType.getFieldPredicate());
		fail();
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	public void testDeleteDocumentTypeWhileSubmissionReferencesPredicate() {
		// Create a submission.
		submitter = userRepo.create(TEST_SUBMISSION_SUBMITTER_EMAIL, TEST_SUBMISSION_SUBMITTER_FIRSTNAME, TEST_SUBMISSION_SUBMITTER_LASTNAME, TEST_SUBMISSION_SUBMITTER_ROLE);
        assertEquals("The user does not exist!", 1, userRepo.count());
		Credentials creds = new Credentials();
		creds.setEmail(submitter.getEmail());
        OrganizationCategory parentCategory = organizationCategoryRepo.create(TEST_CATEGORY_NAME);
        assertEquals("The category does not exist!", 1, organizationCategoryRepo.count());

        organization = organizationRepo.create(TEST_ORGANIZATION_NAME, parentCategory);
        parentCategory = organizationCategoryRepo.findOne(parentCategory.getId());
        assertEquals("The organization does not exist!", 1, organizationRepo.count());
		Submission submission = submissionRepo.create(creds, organization.getId());
		
		// Create a document type with implicitly created field predicate.
		DocumentType docType = documentTypesRepo.create(TEST_DOCUMENT_TYPE_NAME, DegreeLevel.MASTERS);
		
		// Create a field value using the document type's predicate and put in on the submission.
		FieldValue fieldValue = fieldValueRepo.create(docType.getFieldPredicate());
		submission.addFieldValue(fieldValue);
		
		documentTypesRepo.delete(docType);
		fail();
	}
	
    @After
    public void cleanUp() {
    	fieldValueRepo.deleteAll();
        documentTypesRepo.deleteAll();
        submissionRepo.deleteAll();
        organizationRepo.deleteAll();
    }


}
