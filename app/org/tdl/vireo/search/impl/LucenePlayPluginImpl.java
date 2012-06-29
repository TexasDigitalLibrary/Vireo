package org.tdl.vireo.search.impl;

import play.Logger;
import play.PlayPlugin;
import play.modules.spring.Spring;

/**
 * Lucene Play Plugin
 * 
 * This class handles the transactional nature of web requests. This Play Plugin
 * listens for all new requests (Invocations), and waits for them to be
 * completed. If the request is successful then the index transaction is
 * committed, otherwise it is rollbacked.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class LucenePlayPluginImpl extends PlayPlugin {

	// We can't load the indexer right away because when this plugin get's
	// constructed Spring is not yet available.
	public static LuceneIndexerImpl indexer = null;

	/**
	 * @return A statically cached copy of the indexer.
	 */
	private static LuceneIndexerImpl getIndexer() {
		if (indexer == null)
			indexer = Spring.getBeanOfType(LuceneIndexerImpl.class);
		return indexer;
	}

	@Override
	public void beforeInvocation() {
		// Start the transaction by clearing it out of any previous state that
		// may have been left over.
		getIndexer().rollback();
	}

	@Override
	public void afterInvocation() {
		// The request succeeded, so commit the transaction.
		getIndexer().commit();
	}

	@Override
	public void onInvocationException(Throwable e) {
		// There was an exception thrown, so rollback the transaction.
		getIndexer().rollback();
	}

	@Override
	public void invocationFinally() {
		// Either the after, or exception methods above should have been called.
		// Just to make sure that we don't leave a transaction laying around we
		// will rollback now.
		getIndexer().rollback();
	}

}
