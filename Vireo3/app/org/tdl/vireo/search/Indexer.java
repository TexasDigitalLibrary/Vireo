package org.tdl.vireo.search;

import java.util.List;

import org.tdl.vireo.model.AbstractModel;
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
	 * This is a notification method to update the index for the provided
	 * submission Id. This works the same as the updated(model) varient however
	 * this may be more efficient in situations where the full object is not
	 * available. The indexer will keep track of these submissions until either
	 * rollback() or commit() is called from the same thread.
	 * 
	 * @param submissionId The id of the submission which was created, saved, or deleted.
	 */
	public void updated(Long submissionId);
	
	/**
	 * This is a notification method to update the index for the provided list
	 * of submission ids. This works the same as the updated(model) varient
	 * however this may be more efficient in situations where the full object is
	 * not available. The indexer will keep track of these submissions until
	 * either rollback() or commit() is called from the same thread.
	 * 
	 * @param submissionIds
	 *            A list of submission ids which were created, saved, or
	 *            deleted.
	 */
	public void updated(List<Long> submissionIds);
	
	/**
	 * @param submissionId
	 *            The id of a submission object.
	 * @return True if the identified submission is included in the current
	 *         transaction.
	 */
	public boolean isUpdated(Long submissionId);

	/**
	 * @param submission
	 *            A submission object.
	 * @return True if the submission object is included in the current
	 *         transaction.
	 */
	public boolean isUpdated(Submission submission);

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
