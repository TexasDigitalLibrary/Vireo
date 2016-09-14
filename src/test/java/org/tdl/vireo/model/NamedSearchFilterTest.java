package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;

public class NamedSearchFilterTest extends AbstractEntityTest {

    // TODO: rewrite tests!!
    
    @Before
    public void setUp() {
        assertEquals("SearchFilter Repo is not empty", 0, namedSearchFilterRepo.count());

        creator = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);

       
    }

    @Override
    public void testCreate() {
        
    }

    @Override
    public void testDuplication() {
        
    }

    @Override
    public void testDelete() {
        
    }

    @Override
    public void testCascade() {
        
    }

    @After
    public void cleanUp() {
        namedSearchFilterRepo.deleteAll();        
        userRepo.deleteAll();        
    }

}
