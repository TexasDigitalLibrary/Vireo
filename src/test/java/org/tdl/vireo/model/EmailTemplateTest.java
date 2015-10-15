package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.model.repo.EmailTemplateRepo;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class EmailTemplateTest {
	
	private static final String TEST_EMAIL_TEMPLATE_NAME = "Test Email Template Name";
	private static final String TEST_EMAIL_TEMPLATE_MESSAGE = "Test Email Template Message";
	private static final String TEST_EMAIL_TEMPLATE_SUBJECT = "Test Email Template Subject";
	
	@Autowired
    private EmailTemplateRepo emailTemplateRepo;
	
	@Before
    public void setUp() {
		assertEquals("There is no template existing in the emailTemplateRepo",0, emailTemplateRepo.count());
	}
	
	@Test
    @Order(value = 1)
    @Transactional
    public void testCreate() {
		EmailTemplate emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
		assertEquals("The repository did not save the emailTemplate",1, emailTemplateRepo.count());
		assertEquals("Saved submission did not contain the correct Name!",TEST_EMAIL_TEMPLATE_NAME,emailTemplate.getName());
		assertEquals("Saved submission did not contain the correct Message!",TEST_EMAIL_TEMPLATE_MESSAGE,emailTemplate.getMessage());
		assertEquals("Saved submission did not contain the correct Subject!",TEST_EMAIL_TEMPLATE_SUBJECT,emailTemplate.getSubject());
	}
	
	@Test
    @Order(value = 2)
    public void testDuplication() {
		emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
		assertEquals("The repository didn't persist emailTemplate!", 1, emailTemplateRepo.count());
		try{
		emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
		} catch(DataIntegrityViolationException e) {
			/* SUCCESS*/
		}
		assertEquals("The repository did persist 2 emailTemplate!", 1, emailTemplateRepo.count());
	}
	
	
	@Test
    @Order(value = 3)
    public void testFind() {
		emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
		EmailTemplate emailTemplate = emailTemplateRepo.findByName(TEST_EMAIL_TEMPLATE_NAME);
		assertEquals("Did not find the correct email template", TEST_EMAIL_TEMPLATE_NAME,emailTemplate.getName());
	}
	
	@Test
    @Order(value = 4)
    public void testDelete() {
		EmailTemplate emailTemplate = emailTemplateRepo.create(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
		emailTemplateRepo.delete(emailTemplate);
		assertEquals("Did not delete the email template",0,emailTemplateRepo.count());
		
	}
	
	@After
    public void cleanUp() {
		emailTemplateRepo.deleteAll();
	}
	
	

}
