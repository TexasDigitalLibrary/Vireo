package org.tdl.vireo.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.tdl.vireo.Application;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.runner.OrderedRunner;

@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class WorkflowTest {
	
	@BeforeClass
    public static void init() {
		
    }
	
	@Before
	public void setUp() {

	}
	
	@Test
	@Order(value = 1)
	public void testCreate() {
		
	}
	
	@Test
	@Order(value = 2)
	public void testDuplication() {
		
	}
	
	@Test
	@Order(value = 3)
	public void testFind() {
		
	}
	
	@Test
	@Order(value = 4)
	public void testDelete() {
		
	}
	
	@Test
	@Order(value = 5)
	public void testCascade() {
		
	}
	
	@After
	public void cleanUp() {
		
	}

}
