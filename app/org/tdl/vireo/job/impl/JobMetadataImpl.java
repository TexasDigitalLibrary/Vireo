package org.tdl.vireo.job.impl;

import java.util.UUID;

import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;

import play.jobs.Job;
import play.modules.spring.Spring;

/**
 * Implementation of the meta job interface. This provides metadata about a job.
 * 
 * This implementation handles the very basics, like recording when the
 * operation was created, generating a unique id, and setting the id of the
 * owner.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JobMetadataImpl implements JobMetadata {

	// Final attributes
	public final UUID id;
	public final long startTime;
	public final Long ownerId;
	public final String name;

	// Dynamic attributes
	public JobStatus status = JobStatus.WAITING;
	public Progress progress = new Progress();
	public Job job = null;
	public String message = null;

	/**
	 * Construct a new job metadata without any owner.
	 */
	public JobMetadataImpl(String name) {
		this(name,null);
	}

	/**
	 * Construct a new job metadata with an owner.
	 * 
	 * @param ownerId
	 *            The person id who owns this background operation, or null if
	 *            there is no owner.
	 */
	public JobMetadataImpl(String name, Long ownerId) {
		this.id = UUID.randomUUID();
		this.startTime = System.currentTimeMillis();
		this.ownerId = ownerId;
		this.name = name;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public Long getOwnerId() {
		return ownerId;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public JobStatus getStatus() {
		return status;
	}
	
	@Override
	public void setStatus(JobStatus status) {
		this.status = status;
	}
	
	@Override
	public Progress getProgress() {
		return progress;
	}
	
	@Override
	public Job getJob() {
		return job;
	}
	
	@Override
	public void setJob(Job job) {
		this.job = job;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public void setMessage(String message) {
		this.message = message;
	}
}
