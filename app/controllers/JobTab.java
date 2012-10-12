package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.tdl.vireo.batch.TransitionService;
import org.tdl.vireo.export.ChunkStream;
import org.tdl.vireo.export.DepositService;
import org.tdl.vireo.export.ExportService;
import org.tdl.vireo.export.Packager;
import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.NamedSearchFilter;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.ActiveSearchFilter;
import org.tdl.vireo.search.SearchDirection;
import org.tdl.vireo.search.SearchFacet;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.search.SearchOrder;
import org.tdl.vireo.search.SearchResult;
import org.tdl.vireo.state.State;

import play.Logger;
import play.libs.F.Promise;
import play.modules.spring.Spring;
import play.mvc.Catch;
import play.mvc.Http.Cookie;
import play.mvc.With;

/**
 * This is a simple controller to show a progress bar to track any log running background job.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
@With(Authentication.class)
public class JobTab extends AbstractVireoController {

	// Spring dependency
	public static JobManager jobManager = Spring.getBeanOfType(JobManager.class);
	
	/**
	 * Display the progress bar. If the job is currently active then javascript
	 * will repeataly update the state until the job is no longer active.
	 * 
	 * @param jobId
	 *            The UUID of the job.
	 */
	@Security(RoleType.REVIEWER)
	public static void adminStatus(String jobId) {
		
		notFoundIfNull(jobId);
		
		UUID id = null;
		try {
			id = UUID.fromString(jobId);
		} catch (RuntimeException re) {/* ignore */}
		notFoundIfNull(id);
		
		JobMetadata job = jobManager.findJob(id);
		notFoundIfNull(job);
		
		for(JobStatus status : JobStatus.values()) {
			renderArgs.put(status.name(), status);
		}
		
		renderTemplate("JobTab/adminProgress.html",job);
	}
	
	/**
	 * Get the most up-to-date status about a job and return it as a JSON
	 * payload.
	 * 
	 * @param jobId
	 *            The UUID of the job.
	 */
	@Security(RoleType.REVIEWER)
	public static void updateJSON(String jobId) {
		
		UUID id = UUID.fromString(jobId);
		notFoundIfNull(id);
		
		JobMetadata job = jobManager.findJob(id);
		notFoundIfNull(job);
		
		jobId = escapeJavaScript(jobId);
		String name = escapeJavaScript(job.getName());
		String status = escapeJavaScript(job.getStatus().name());
		String progress = escapeJavaScript(job.getProgress().toString());
		String message = escapeJavaScript(job.getMessage());
		
		
		renderJSON("{ "+
				"\"id\" : \"" + jobId + "\", " +
				"\"name\" : \"" + name + "\", "+
				"\"status\" : \"" + status + "\", "+
				"\"progress\" : \"" + progress + "\", "+
				"\"message\" : \"" + message + "\" "+
				"}");
	}
	
}
