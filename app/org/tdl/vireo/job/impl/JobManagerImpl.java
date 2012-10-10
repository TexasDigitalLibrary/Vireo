package org.tdl.vireo.job.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.tdl.vireo.job.JobManager;
import org.tdl.vireo.job.JobMetadata;
import org.tdl.vireo.job.JobStatus;
import org.tdl.vireo.model.Person;

/**
 * Implementation of the job manager interface.
 * 
 * This class provides synchronized access to a list of background operations.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class JobManagerImpl implements JobManager {

	// Maximum operation size
	public int maxSize = 50;
	
	// The list of currently running background operations
	protected List<JobMetadata> jobs = Collections.synchronizedList(new LinkedList());	
	
	@Override
	public synchronized JobMetadata findJob(UUID jobId) {
		for (JobMetadata job: jobs) {
			if (job.getId() == jobId)
				return job;
		}
		
		return null;
	}

	@Override
	public synchronized List<JobMetadata> findAllJobs() {
		List<JobMetadata> all = new ArrayList<JobMetadata>(jobs);
		Collections.reverse(all);
		return all;
	}
	
	@Override
	public synchronized List<JobMetadata> findJobsByStatus(JobStatus ... statusList) {
		
		List<JobMetadata> found = new ArrayList<JobMetadata>();
		
		for (JobMetadata job: jobs) {
			for (JobStatus status : statusList) {
				if (job.getStatus() == status) {
					found.add(job);
				}
			}
		}
		
		Collections.reverse(found);
		return found;
	}

	@Override
	public synchronized List<JobMetadata> findJobsByOwner(Person owner) {
		if (owner == null || owner.getId() == null)
			throw new IllegalArgumentException("Unable to find background operations from a specific owner without the owner specified.");
		
		List<JobMetadata> found = new ArrayList<JobMetadata>();
		
		Long personId = owner.getId();
		
		for (JobMetadata job: jobs) {
			if (personId.equals(job.getOwnerId())) {
				found.add(job);
			}
		}
		
		Collections.reverse(found);
		return found;
	}

	@Override
	public synchronized List<JobMetadata> findJobsByType(Class type) {
		if ( type == null )
			throw new IllegalArgumentException("Unable to find background operations of a specific type without the type specified.");
		
		List<JobMetadata> found = new ArrayList<JobMetadata>();
		
		for (JobMetadata job: jobs) {
			if (type.isInstance(job)) {
				found.add(job);
			}
		}
		
		Collections.reverse(found);
		return found;
	}

	@Override
	public synchronized JobMetadata register(String name, Person owner) {
		
		Long ownerId = null;
		if (owner != null && owner.getId() != null)
			ownerId = owner.getId();
		
		JobMetadataImpl job = new JobMetadataImpl(name, ownerId);

		jobs.add(job);
		_prune();


		
		return job;
	}
	
	@Override
	public JobMetadata register(String name) {
		
		return register(name,null);
	}
	
	@Override
	public synchronized void deregister(JobMetadata job) {
		
		jobs.remove(job);

	}
	
	
	/**
	 * Set the maximum size for the operation queue.
	 * 
	 * @param size The maximum operation queue size.
	 */
	public void setMaxSize(int size) {
		this.maxSize = size;
	}
	
	/**
	 * Internal private method to prune background operations if they have
	 * completed. This condition should never occur because the background
	 * operations should always deregister themselves no matter what. But if it
	 * does happen then it's nice to be able to recover without keeping a
	 * dangling memory reference around forever.
	 */
	private void _prune() {
		
		// Prune if we are too big.
		while (jobs.size() > maxSize) {
			
			// Find the last completed operation and remove that.
			Iterator<JobMetadata> itr = jobs.iterator();
			while (itr.hasNext()) {
				if (!itr.next().getStatus().isActive()) {
					itr.remove();
					break;
				}
			}
			
			// if we exhausted the list and didn't find any to prune then just return. There is nothing we can do here.
			if (!itr.hasNext())
				return;
			
		}
	}

}
