package org.tdl.vireo.job;

import java.util.UUID;

import play.jobs.Job;

/**
 * Metadata about a background job.
 * 
 * This is not the actual job, instead it only contains information about a job.
 * This allows the metadata to persist after the job has completed.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public interface JobMetadata {

	/**
	 * @return The unique ID assigned to this job. This id is set upon creation
	 *         of the object.
	 */
	public UUID getId();

	/**
	 * @return The start time of when this job was created.
	 */
	public long getStartTime();

	/**
	 * @return The owner or person responsible for this job. This may be null if
	 *         the job is iniated by a system event.
	 */
	public Long getOwnerId();

	/**
	 * @return The displayable name of the job.
	 */
	public String getName();

	/**
	 * @return The status of the job.
	 */
	public JobStatus getStatus();

	/**
	 * Set a new status for the job.
	 * 
	 * @param status
	 *            New job status.
	 */
	public void setStatus(JobStatus status);

	/**
	 * @return The job's progress object. This object maintains metadata about
	 *         how for along the job has completed processing.
	 */
	public Progress getProgress();

	/**
	 * @return The running job, this may be null. Most jobs are detached from
	 *         their metadata after the job has completed. But that is up to the
	 *         individual job to manage.
	 */
	public Job getJob();

	/**
	 * Set the running job.
	 * 
	 * @param job
	 *            The job.
	 */
	public void setJob(Job job);

	/**
	 * @return An optional message associated with this job. Typically this is
	 *         an error message resulting from an unexpected condition but it
	 *         may be any message.
	 */
	public String getMessage();

	/**
	 * Set the job's message.
	 * 
	 * @param message
	 *            The new message, or null for no message.
	 */
	public void setMessage(String message);

	/**
	 * A progress object which maintains the current progress of a running job.
	 */
	public class Progress {

		// How many things need to be done.
		public int total = 0;

		// How many things have been completed so far.
		public int completed = 0;

		/**
		 * @return The total number of "things" to be done.
		 */
		public int getTotal() {
			return total;
		}

		/**
		 * @return The number of "things" which have been completed so far.
		 */
		public int getCompleted() {
			return completed;
		}

		/**
		 * @return a represent the progress as a float.
		 */
		public float toFloat() {

			if (total == 0)
				return 0F;
			
			try {
				return ((float) completed) / ((float) total) * 100;
			} catch (RuntimeException re) {
				// Catch divide by zero
				return 0F;
			}
		}

		/**
		 * @return a representation as a string.
		 */
		public String toString() {
			float f = this.toFloat();
			if (f == 100)
				return "100%";

			return String.format("%.3f%%", this.toFloat());

		}
	}

}
