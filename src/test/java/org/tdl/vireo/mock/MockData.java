package org.tdl.vireo.mock;

import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.User;

public abstract class MockData {
	
	protected final static String EMAIL_VERIFICATION_TYPE = "EMAIL_VERIFICATION";
    
	protected final static String TEST_EMAIL = "test@email.com";
 
	protected final static String TEST_USER_EMAIL       = "testUser@email.com";
	protected final static String TEST_USER_FIRST_NAME  = "Test";
	protected final static String TEST_USER_LAST_NAME   = "User";
	protected final static String TEST_USER_PASSWORD    = "abc123";
	protected final static String TEST_USER_CONFIRM     = "abc123";
	protected final static AppRole TEST_USER_ROLE        = AppRole.valueOf("STUDENT");
	protected final static AppRole TEST_USER_ROLE_UPDATE = AppRole.valueOf("ADMINISTRATOR");
	
	protected final static String TEST_REGISTRATION_EMAIL_TEMPLATE_NAME = "SYSTEM New User Registration";
	protected final static String TEST_EMAIL_TEMPLATE_NAME = "Test Email Template Name";
	protected final static String TEST_EMAIL_TEMPLATE_SUBJECT = "Test Email Template Subject";
	protected final static String TEST_EMAIL_TEMPLATE_MESSAGE = "Test Email Template Message";
	
	protected final static String TEST_LANGUAGE_NAME1 = "English";
	protected final static String TEST_LANGUAGE_NAME2 = "Spanish";
	protected final static String TEST_LANGUAGE_NAME3 = "French";
	
	protected final static String TEST_CONTROLLED_VOCABULARY_NAME1 = "CCVTest1";
    protected final static String TEST_CONTROLLED_VOCABULARY_NAME2 = "BCVTest2";
    protected final static String TEST_CONTROLLED_VOCABULARY_NAME3 = "ACVTest3";
    
    protected final static String TEST_VOCABULARY_WORD_NAME1 = "Hello";
    protected final static String TEST_VOCABULARY_WORD_NAME2 = "World";
    protected final static String TEST_VOCABULARY_WORD_NAME3 = "TAMU";
    
    protected final static String TEST_VOCABULARY_WORD_DEFINITION1 = "A greeting.";
    protected final static String TEST_VOCABULARY_WORD_DEFINITION2 = "The earth.";
    protected final static String TEST_VOCABULARY_WORD_DEFINITION3 = "Awesome!";
    
    protected final static String TEST_VOCABULARY_WORD_IDENTIFIER1 = "http://google.com/Hello";
    protected final static String TEST_VOCABULARY_WORD_IDENTIFIER2 = "http://nasa.gov";
    protected final static String TEST_VOCABULARY_WORD_IDENTIFIER3 = "http://library.tamu.edu";
    
	protected User TEST_USER = new User(TEST_USER_EMAIL, TEST_USER_FIRST_NAME, TEST_USER_LAST_NAME, AppRole.STUDENT);    
   
	protected final static String TEST_USER2_EMAIL = "aggieJack@tamu.edu";
	protected final static String TEST_USER3_EMAIL = "aggieJill@tamu.edu";
	protected final static String TEST_USER4_EMAIL = "jimInny@tdl.org";
	
	protected User TEST_USER2 = new User(TEST_USER2_EMAIL, "Jack", "Daniels", AppRole.ADMINISTRATOR);
	protected User TEST_USER3 = new User(TEST_USER3_EMAIL, "Jill", "Daniels", AppRole.MANAGER);
	protected User TEST_USER4 = new User(TEST_USER4_EMAIL, "Jim", "Inny", AppRole.STUDENT);
    
}