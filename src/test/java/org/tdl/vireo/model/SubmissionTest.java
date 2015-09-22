package org.tdl.vireo.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tdl.vireo.Application;
import org.tdl.vireo.model.repo.SubmissionRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SubmissionTest {
	
	@Autowired
	private SubmissionRepo submissionRepo;
	
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
		submissionRepo.deleteAll();
	}

}
