package org.tdl.vireo.search.impl;

import java.io.IOException;
import java.util.Set;

import net.sf.oval.internal.util.LinkedSet;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.NumericUtils;
import org.tdl.vireo.model.Submission;

import play.db.jpa.JPA;

/**
 * Update the index for a given list of modified submissions.
 * 
 * This is the 'normal' job that is used after each web request to update the
 * index. Unlike the rebuild job which looks at every item in vireo, this job
 * instead only re-indexes a subset of those submissions. Also like the rebuild
 * job searches may continue against the index while this job is processing.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public class LuceneUpdateJob extends LuceneAbstractJobImpl {

	// The list of submission ids to update in the index.
	public Set<Long> subIds;

	/**
	 * Construct a new update index job.
	 * 
	 * @param submissionIds The list of submissions to index.
	 */
	public LuceneUpdateJob(LuceneIndexerImpl indexer, Set<Long> submissionIds) {
		super(indexer);
		this.subIds = new LinkedSet<Long>();
		this.subIds.addAll(submissionIds);
		progress = 0; 
		total = submissionIds.size();
	}

	@Override
	public String getLabel() {
		return "Update Index";
	}

	@Override
	public LuceneAbstractJobImpl mergeJob(LuceneAbstractJobImpl job) {

		if (job instanceof LuceneRebuildJobImpl) {
			return job;
		} else if (job instanceof LuceneUpdateJob) {
			LuceneUpdateJob updateJob = (LuceneUpdateJob) job;
			this.subIds.addAll(updateJob.subIds);
			progress = 0;
			total = subIds.size();
			return this;

		} else {
			throw new IllegalArgumentException("Unable to merge index jobs because the other job is an unsupported: "+job.getClass().getName());
		}
	}

	@Override
	public void writeIndex() throws CorruptIndexException, LockObtainFailedException, IOException, InterruptedException {


		// Update the progress and total one last time before starting.
		progress = 0;
		total = subIds.size();

		IndexWriterConfig writerConfig = new IndexWriterConfig(indexer.version,indexer.standardAnalyzer);
		IndexWriter writer = new IndexWriter(indexer.index, writerConfig);
		try {
			for (Long id : subIds) {

				// First, delete everything from with this submission id
				// (submission and actionlogs!)
				writer.deleteDocuments(new TermQuery(new Term("subId",
						NumericUtils.longToPrefixCoded(id))));

				Submission sub = indexer.subRepo.findSubmission(id);
				if (sub != null)
					indexSubmission(writer, sub);

				JPA.em().detach(sub);

				progress++;
				
				// Have we been asked to stop?
				if (cancel) {
					writer.rollback();
					writer = null;
					throw new InterruptedException("Lucene '"+this.getLabel()+"' job recieved a cancel request after processing "+progress+" number of submissions, rolling back changes.");
				}
			}
		} finally {
			writer.close();
		}
	}
}
