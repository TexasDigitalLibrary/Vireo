package org.tdl.vireo.search;

import org.tdl.vireo.model.AbstractModel;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Submission;

/**
 * Search indexer.
 * 
 * The purpose of this interface is to receive updates to various model objects
 * and update the search index. Typically the implementation of this interface
 * is also tied to the implementation of the sister interface: Searcher.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
public interface Indexer {

	/**
	 * This is a notification method that the provided model object has been
	 * updated (either created, saved, or deleted), but has not been committed
	 * to the database yet. The indexer will keep this proposed update until
	 * either rollback() or commit() is called from the same thread.
	 * 
	 * @param model
	 *            The model object which was created, saved, or deleted.
	 */
	public <T extends AbstractModel> void updated(T model);

	/**
	 * Roll back the current proposed changes to the model. This is typically
	 * called after an exception and database rollback. When called the indexer
	 * will forget the previous updated() models.
	 */
	public void rollback();

	/**
	 * Commit the updated models to the search index.
	 * 
	 * @param wait
	 *            If true the method will not return until rebuilding the index
	 *            is completed, otherwise rebuilding will take place in a
	 *            background thread.
	 */
	public void commit(boolean wait);

	/**
	 * Rebuild the entire search index from scratch.
	 * 
	 * This will remove all entries in the search index and rebuild it.
	 * 
	 * @param wait
	 *            If true the method will not return until rebuilding the index
	 *            is completed, otherwise rebuilding will take place in a
	 *            background thread.
	 */
	public void rebuild(boolean wait);
	
	/**
	 * Delete the entire search index and rebuild it from scratch. This will
	 * cancel any index jobs currently in progress, and the index will be
	 * irrevocably deleted. Following the deletion, the index will be rebuild.
	 * 
	 * While this index is rebuilding searches may not be performed against the
	 * index. Any search that does process will result in an exception.
	 * 
	 * 
	 * @param wait
	 *            If true the method will not return until rebuilding the index
	 *            is completed, otherwise rebuilding will take place in a
	 *            background thread.
	 */
	public void deleteAndRebuild(boolean wait);

	/////////////////
	// Job Management
	/////////////////
	
	/**
	 * @return True if a background process is currently updating the search
	 *         index.
	 */
	public boolean isJobRunning();

	/**
	 * @return The label of the current background process, either
	 *         "Updating Index" or "Rebuilding Index"
	 */
	public String getCurrentJobLabel();

	/**
	 * @return The current progress of how many submissions have been indexed so
	 *         far.
	 */
	public long getCurrentJobProgress();

	/**
	 * @return The total number of submissions that will be indexed by the
	 *         background process.
	 */
	public long getCurrentJobTotal();
}
