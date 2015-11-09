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
	protected final static String TEST_USER_ROLE        = "ROLE_USER";
	protected final static String TEST_USER_ROLE_UPDATE = "ROLE_ADMIN";
    
	protected User TEST_USER = new User(TEST_USER_EMAIL, TEST_USER_FIRST_NAME, TEST_USER_LAST_NAME, Role.USER);    
   
	protected final static String aggieJackEmail = "aggieJack@tamu.edu";
	protected final static String aggieJillEmail = "aggieJill@tamu.edu";
	protected final static String jimInnyEmail = "jimInny@tdl.org";
	
	protected User aggieJack = new User(aggieJackEmail, "Jack", "Daniels", Role.ADMINISTRATOR);
	protected User aggieJill = new User(aggieJillEmail, "Jill", "Daniels", Role.MANAGER);
	protected User jimInny = new User(jimInnyEmail, "Jim", "Inny", Role.USER);
    
}