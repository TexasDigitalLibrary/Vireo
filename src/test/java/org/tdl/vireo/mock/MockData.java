package org.tdl.vireo.mock;

import org.tdl.vireo.enums.Role;
import org.tdl.vireo.model.User;

public abstract class MockData {
	
	protected final static String EMAIL_VERIFICATION_TYPE = "EMAIL_VERIFICATION";
    
	protected final static String TEST_EMAIL = "test@email.com";
 
	protected final static String TEST_USER_EMAIL       = "testUser@email.com";
	protected final static String TEST_USER_FIRST_NAME  = "Test";
	protected final static String TEST_USER_LAST_NAME   = "User";
	protected final static String TEST_USER_PASSWORD    = "abc123";
	protected final static String TEST_USER_CONFIRM     = "abc123";
	protected final static String TEST_USER_ROLE        = "ROLE_STUDENT";
	protected final static String TEST_USER_ROLE_UPDATE = "ROLE_ADMIN";
	
	protected final static String TEST_REGISTRATION_EMAIL_TEMPLATE_NAME = "SYSTEM New User Registration";
	protected final static String TEST_EMAIL_TEMPLATE_NAME = "Test Email Template Name";
	protected final static String TEST_EMAIL_TEMPLATE_SUBJECT = "Test Email Template Subject";
	protected final static String TEST_EMAIL_TEMPLATE_MESSAGE = "Test Email Template Message";
    
	protected User TEST_USER = new User(TEST_USER_EMAIL, TEST_USER_FIRST_NAME, TEST_USER_LAST_NAME, Role.STUDENT);    
   
	protected final static String TEST_USER2_EMAIL = "aggieJack@tamu.edu";
	protected final static String TEST_USER3_EMAIL = "aggieJill@tamu.edu";
	protected final static String TEST_USER4_EMAIL = "jimInny@tdl.org";
	
	protected User TEST_USER2 = new User(TEST_USER2_EMAIL, "Jack", "Daniels", Role.ADMINISTRATOR);
	protected User TEST_USER3 = new User(TEST_USER3_EMAIL, "Jill", "Daniels", Role.MANAGER);
	protected User TEST_USER4 = new User(TEST_USER4_EMAIL, "Jim", "Inny", Role.STUDENT);
    
}