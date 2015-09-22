package org.tdl.vireo.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.tdl.vireo.config.TestDataSourceConfiguration;
import org.tdl.vireo.model.repo.FieldGlossRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestDataSourceConfiguration.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    					  DirtiesContextTestExecutionListener.class,
    					  TransactionalTestExecutionListener.class })
public class FieldGlossTest {
	
	@Autowired
	private FieldGlossRepo fieldGlossRepo;
	
	@BeforeClass
    public static void init() {
		
    }
	
	@Before
	public void setUp() {

	}
	
	@Test
	public void testCreate() {
		
	}
	
	@Test
	public void testDuplication() {
		
	}
	
	@Test
	public void testFind() {
		
	}
	
	@Test
	public void testDelete() {
		
	}
	
	@Test
	public void testCascade() {
		
	}
	
	@After
	public void cleanUp() {
		fieldGlossRepo.deleteAll();
	}

}

