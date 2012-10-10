package org.tdl.vireo.job;

/**
 * Potential states a job may be in.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum JobStatus {

	// The job is being constructed and not yet ready to be executed.
	WAITING(true),

	// The job is waiting to be started.
	READY(true),

	// The job is currently running.
	RUNNING(true),

	// The job has completed with out error successfully.
	SUCCESS(false),

	// The job failed to complete without an error.
	FAILED(false),

	// The job was cancelled.
	CANCELLED(false);

	// Flag weather the status is active or inactive.
	private boolean active;

	/**
	 * Private status constructor
	 * 
	 * @param active
	 *            Weather the state is active or inactive.
	 */
	JobStatus(boolean active) {
		this.active = active;
	}

	/**
	 * @return true if the state is active, otherwise false.
	 */
	public boolean isActive() {
		return active;
	}

	// Helpfull definitions of all active and final states.
	public static JobStatus[] ACTIVE = { WAITING, READY, RUNNING };
	public static JobStatus[] COMPLETED = { SUCCESS, FAILED, CANCELLED };

}
