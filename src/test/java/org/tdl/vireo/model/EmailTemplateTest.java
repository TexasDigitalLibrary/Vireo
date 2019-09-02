package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.springframework.dao.DataIntegrityViolationException;

public class EmailTemplateTest extends AbstractEntityTest {

    @Before
    public void setUp() {
        assertEquals("There emailTemplateRepo is not empty!", 0, emailTemplateRepo.count());
    }

    @Override
    public void testCreate() {
        EmailTemplate emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        assertEquals("The repository did not save the emailTemplate!", 1, emailTemplateRepo.count());
        assertEquals("Saved submission did not contain the correct Name!", TEST_EMAIL_TEMPLATE_NAME, emailTemplate.getName());
        assertEquals("Saved submission did not contain the correct Message!", TEST_EMAIL_TEMPLATE_MESSAGE, emailTemplate.getMessage());
        assertEquals("Saved submission did not contain the correct Subject!", TEST_EMAIL_TEMPLATE_SUBJECT, emailTemplate.getSubject());
    }

    public void testUpdate() {
        EmailTemplate testEmailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        assertEquals("Embargo Repo did not save the email template!", 1, emailTemplateRepo.count());

        testEmailTemplate.setSystemRequired(!testEmailTemplate.getSystemRequired());
        testEmailTemplate.setName("Updated Name");
        testEmailTemplate.setMessage("Updated Message");
        testEmailTemplate.setSubject("Updated Subject");
        testEmailTemplate.setPosition(9000L);

        EmailTemplate updatedEmailTemplate = emailTemplateRepo.update(testEmailTemplate);
        assertEquals("Email Template Repo did not update the email template SystemRequired property!", testEmailTemplate.getSystemRequired(), updatedEmailTemplate.getSystemRequired());
        assertEquals("Email Template Repo did not update the email template Name property!", testEmailTemplate.getName(), updatedEmailTemplate.getName());
        assertEquals("Email Template Repo did not update the email template Message property!", testEmailTemplate.getMessage(), updatedEmailTemplate.getMessage());
        assertEquals("Email Template Repo did not update the email template Subject property!", testEmailTemplate.getSubject(), updatedEmailTemplate.getSubject());
        assertEquals("Email Template Repo did not update the email template Position property!", testEmailTemplate.getPosition(), updatedEmailTemplate.getPosition());
    }

    @Override
    public void testDuplication() {
        emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        assertEquals("The repository didn't persist emailTemplate!", 1, emailTemplateRepo.count());
        try {
            emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals("The repository did persist 2 emailTemplate!", 1, emailTemplateRepo.count());
    }

    @Override
    public void testDelete() {
        EmailTemplate emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        emailTemplateRepo.delete(emailTemplate);
        assertEquals("Did not delete the email template", 0, emailTemplateRepo.count());

    }

    @Override
    public void testCascade() {

    }

    @After
    public void cleanUp() {
        emailTemplateRepo.deleteAll();
    }

}
