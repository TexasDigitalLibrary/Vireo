package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;

public class DeprecatedAttachmentTypeTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("AttachmentType repo was not empty!", 0, deprecatedAttachmentTypeRepo.count());
    }

    @Override
    public void testCreate() {
        DeprecatedAttachmentType testAttachmentType = deprecatedAttachmentTypeRepo.create(TEST_ATTACHMENT_TYPE_NAME);
        assertEquals("Embargo Repo did not save the embargo!", 1, deprecatedAttachmentTypeRepo.count());
        assertEquals("Embargo Repo did not save the correct embargo name!", TEST_ATTACHMENT_TYPE_NAME, testAttachmentType.getName());
     }

    @Override
    public void testDuplication() {
        deprecatedAttachmentTypeRepo.create(TEST_ATTACHMENT_TYPE_NAME);
        assertEquals("The repository didn't persist attachment type!", 1, deprecatedAttachmentTypeRepo.count());
        try {
            deprecatedAttachmentTypeRepo.create(TEST_ATTACHMENT_TYPE_NAME);
        }
        catch (DataIntegrityViolationException e) { /* SUCCESS */ }
        assertEquals("The repository didn't persist attachment type!", 1, deprecatedAttachmentTypeRepo.count());
    }

    @Override
    public void testDelete() {
        DeprecatedAttachmentType testAttachmentType = deprecatedAttachmentTypeRepo.create(TEST_ATTACHMENT_TYPE_NAME);
        deprecatedAttachmentTypeRepo.delete(testAttachmentType);
        assertEquals("AttachmentType did not delete!", 0, deprecatedAttachmentTypeRepo.count());
    }

    @Override
    public void testCascade() {
        // nothing to cascade
    }

    @After
    public void cleanUp() {
        deprecatedAttachmentTypeRepo.deleteAll();
    }

}
