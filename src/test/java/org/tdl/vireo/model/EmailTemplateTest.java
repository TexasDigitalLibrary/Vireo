package org.tdl.vireo.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.dao.DataIntegrityViolationException;

public class EmailTemplateTest extends AbstractEntityTest {

    @BeforeEach
    public void setUp() {
        assertEquals(0, emailTemplateRepo.count(), "There emailTemplateRepo is not empty!");
    }

    @Override
    public void testCreate() {
        EmailTemplate emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        assertEquals(1, emailTemplateRepo.count(), "The repository did not save the emailTemplate!");
        assertEquals(TEST_EMAIL_TEMPLATE_NAME, emailTemplate.getName(), "Saved submission did not contain the correct Name!");
        assertEquals(TEST_EMAIL_TEMPLATE_MESSAGE, emailTemplate.getMessage(), "Saved submission did not contain the correct Message!");
        assertEquals(TEST_EMAIL_TEMPLATE_SUBJECT, emailTemplate.getSubject(), "Saved submission did not contain the correct Subject!");
    }

    public void testUpdate() {
        EmailTemplate testEmailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        assertEquals(1, emailTemplateRepo.count(), "Embargo Repo did not save the email template!");

        testEmailTemplate.setSystemRequired(!testEmailTemplate.getSystemRequired());
        testEmailTemplate.setName("Updated Name");
        testEmailTemplate.setMessage("Updated Message");
        testEmailTemplate.setSubject("Updated Subject");
        testEmailTemplate.setPosition(9000L);

        EmailTemplate updatedEmailTemplate = emailTemplateRepo.update(testEmailTemplate);
        assertEquals(testEmailTemplate.getSystemRequired(), updatedEmailTemplate.getSystemRequired(), "Email Template Repo did not update the email template SystemRequired property!");
        assertEquals(testEmailTemplate.getName(), updatedEmailTemplate.getName(), "Email Template Repo did not update the email template Name property!");
        assertEquals(testEmailTemplate.getMessage(), updatedEmailTemplate.getMessage(), "Email Template Repo did not update the email template Message property!");
        assertEquals(testEmailTemplate.getSubject(), updatedEmailTemplate.getSubject(), "Email Template Repo did not update the email template Subject property!");
        assertEquals(testEmailTemplate.getPosition(), updatedEmailTemplate.getPosition(), "Email Template Repo did not update the email template Position property!");
    }

    @Override
    public void testDuplication() {
        emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        assertEquals(1, emailTemplateRepo.count(), "The repository didn't persist emailTemplate!");
        try {
            emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        } catch (DataIntegrityViolationException e) {
            /* SUCCESS */ }
        assertEquals(1, emailTemplateRepo.count(), "The repository did persist 2 emailTemplate!");
    }

    @Override
    public void testDelete() {
        EmailTemplate emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        emailTemplateRepo.delete(emailTemplate);
        assertEquals(0, emailTemplateRepo.count(), "Did not delete the email template");

    }

    @Override
    public void testCascade() {

    }

    @AfterEach
    public void cleanUp() {
        emailTemplateRepo.deleteAll();
    }

}
