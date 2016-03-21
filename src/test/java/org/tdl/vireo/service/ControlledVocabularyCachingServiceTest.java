package org.tdl.vireo.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.runner.OrderedRunner;

@WebAppConfiguration
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles({"test"})
public class ControlledVocabularyCachingServiceTest {

	@Before
	public void setup() {

	}
	
    @Test
    @Order(value = 1)
    public void testAddControlledVocabularyCache() {
        
    }
    
    @Test
    @Order(value = 2)
    public void testRemoveControlledVocabularyCache() {
        
    }
    
    @Test
    @Order(value = 3)
    public void testGetControlledVocabularyCache() {
        
    }
    
    @Test
    @Order(value = 4)
    public void testIsControlledVocabularyBeingImported() {
        
    }
    
    @Test
    @Order(value = 5)
    public void testCleanCache() {
        
    }
    
    @After
	public void cleanup() {

	}
    
}
