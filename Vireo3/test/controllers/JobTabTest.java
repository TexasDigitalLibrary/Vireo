package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.tdl.vireo.export.DepositService;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.search.Indexer;
import org.tdl.vireo.search.SearchFacet;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.security.SecurityContext;

import play.db.jpa.JPA;
import play.i18n.Messages;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;

/**
 * Tests for viewing the status of a job in progress.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JobTabTest extends AbstractVireoFunctionalTest {
	
	// Spring dependencies
	public static JobManager jobManager = Spring.getBeanOfType(JobManager.class);
	
	/**
	 * Test displaying the static job status page.
	 */
	@Test
	public void testAdminStatus() {
		
		// Create a job.
		JobMetadata job = jobManager.register("Test Job");
		job.setMessage("This is a test Job status message");
		job.getProgress().total = 100;
		job.getProgress().completed = 50;
		
		// Login as administratior
		LOGIN();
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("jobId", job.getId().toString());
		final String STATUS_URL = Router.reverse("JobTab.adminStatus",routeArgs).url;
		
		Response response = GET(STATUS_URL);
			
		assertContentMatch("Test Job",response);
		assertContentMatch("progress-info active",response);
		assertContentMatch("This is a test Job status message",response);
		
		jobManager.deregister(job);
	}
	
	/**
	 * Test retrieving updates about a job's progress via JSON
	 */
	@Test
	public void testUpdateJSON() {
		
		// Create a job.
		JobMetadata job = jobManager.register("Test Job");
		job.setMessage("This is a test Job status message");
		job.getProgress().total = 100;
		job.getProgress().completed = 50;
		job.setStatus(JobStatus.RUNNING);
		
		// Login as administratior
		LOGIN();
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("jobId", job.getId().toString());
		final String STATUS_URL = Router.reverse("JobTab.updateJSON",routeArgs).url;
		
		Response response = GET(STATUS_URL);
			
		assertContentMatch("Test Job",response);
		assertContentMatch("50.000%",response);
		assertContentMatch("This is a test Job status message",response);
		assertContentMatch("RUNNING",response);
		
		jobManager.deregister(job);
	}
	
	

}
