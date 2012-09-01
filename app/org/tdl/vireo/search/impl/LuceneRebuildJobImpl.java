package org.tdl.vireo.search.impl;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.LockObtainFailedException;
import org.tdl.vireo.model.Submission;

/**
 * This is the background job to rebuild the entire search index.
 * 
 * Rebuilding may take quite some time to complete depending upon the size of
 * the search index. This is a non-destructive rebuild, meaning that searches
 * may continue without effect while the rebuilding is occurring.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class LuceneRebuildJobImpl extends LuceneAbstractJobImpl {

	/**
	 * Construct a new rebuild index job.
	 */
	public LuceneRebuildJobImpl(LuceneIndexerImpl indexer) {
		super(indexer);
		progress = 0;
		total = (int) indexer.subRepo.findSubmissionsTotal();
	}

	@Override
	public String getLabel() {
		return "Rebuild Index";
	}

	@Override
	public LuceneAbstractJobImpl mergeJob(LuceneAbstractJobImpl job) {
		// we're rebuilding the entire index and haven't started, so we can
		// merge with any job.
		return this;
	}

	@Override
	public void writeIndex() throws CorruptIndexException,
	LockObtainFailedException, IOException, InterruptedException {

		progress = 0;
		total = (int) indexer.subRepo.findSubmissionsTotal();

		IndexWriterConfig writerConfig = new IndexWriterConfig(indexer.version,
				indexer.standardAnalyzer);
		IndexWriter writer = new IndexWriter(indexer.index, writerConfig);

		try {
			writer.deleteAll();

			Iterator<Submission> itr = indexer.subRepo.findAllSubmissions();

			int count = 0;
			while (itr.hasNext()) {
				indexSubmission(writer, itr.next());
				progress++;
				
				
				// Have we been asked to stop?
				if (cancel) {
					writer.rollback();
					writer = null;
					throw new InterruptedException("Lucene '"+this.getLabel()+"' job recieved a cancel request after processing "+progress+" number of submissions, rolling back changes.");
				}
				
				// Keep memory usage under control
				if (count > 100) {
					System.gc();
					count = 0;
				} else {
					count++;
				}
			}
		} finally {
			if (writer != null)
				writer.close();
		}
	}

}

