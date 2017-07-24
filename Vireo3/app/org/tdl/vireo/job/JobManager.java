package org.tdl.vireo.job;

import java.util.List;
import java.util.UUID;

import org.tdl.vireo.model.Person;

import play.jobs.Job;

/**
 * The job manager maintains a repository of currently active jobs or recently
 * active jobs. The repository of jobs can be searched by a variety of means.
 * Each time a background job starts up it should register itself with this
 * manager, and then maintain the job's state on that metadata object through to
 * a final state.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface JobManager {

	/**
	 * Locate an individual job's metadata by its UUID.
	 * 
	 * @param jobId
	 *            The UUID of the job.
	 * @return The job metadata found, or null if not found.
	 */
	public JobMetadata findJob(UUID jobId);

	/**
	 * @return Return all jobs registered with the manager. Note the manager
	 *         will periodically prune old jobs which have been completed.
	 */
	public List<JobMetadata> findAllJobs();

	/**
	 * Find all jobs with one of the provided status.
	 * 
	 * @param status
	 *            A varargs list of possible states to search for.
	 * @return A list of all jobs found.
	 */
	public List<JobMetadata> findJobsByStatus(JobStatus... status);

	/**
	 * Find all jobs by the provided person.
	 * 
	 * @param owner
	 *            The owner of the job.
	 * @return A list of all jobs found.
	 */
	public List<JobMetadata> findJobsByOwner(Person owner);

	/**
	 * Find all jobs of the provided type.
	 * 
	 * @param type
	 *            The type of jobs to locate.
	 * @return A list of all jobs found.
	 */
	public List<JobMetadata> findJobsByType(Class type);

	
	/**
	 * Yield the current thread until there are no more active jobs.
	 */
	public void waitForJobs();
	
	/**
	 * Yield the current thread until the specified job has completed.
	 * 
	 * @param id
	 *            The UUID of the job to wait for.
	 */
	public void waitForJobs(UUID id);

	/**
	 * Yield the current thread until all jobs by the specified owner has
	 * completed.
	 * 
	 * @param owner
	 *            The owner to wait for.
	 */
	public void waitForJobs(Person owner);

	/**
	 * Yield the current thread until all jobs of the specified type have
	 * completed.
	 * 
	 * @param type
	 *            The job type to wait for.
	 */
	public void waitForJobs(Class type);
	
	/**
	 * Register a new jobMetadata with the provided name and owned by the
	 * provided person.
	 * 
	 * @param name
	 *            The displayable name of the job.
	 * @param owner
	 *            The owner or initiator of the job.
	 * @return A new job metadata.
	 */
	public JobMetadata register(String name, Person owner);

	/**
	 * Register a new jobMedatata with the provided name. The job will be
	 * recorded as having no owner.
	 * 
	 * @param name
	 *            The displayable name of the job.
	 * @return A new job metadata.
	 */
	public JobMetadata register(String name);

	/**
	 * Deregister or remove this job's metadata from the manager. While the
	 * manager will periodically prune old jobs which have been completed. By
	 * calling deregister the job will be immediately removed.
	 * 
	 * @param job
	 *            The job to deregister.
	 */
	public void deregister(JobMetadata job);
}
