package org.tdl.vireo.search.impl;

import org.tdl.vireo.search.Indexer;

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
	@Override
	public void beforeInvocation() {
		// Start the transaction by clearing it out of any previous state that
		// may have been left over.
		try {
			Spring.getBeanOfType(Indexer.class).rollback();
		} catch (RuntimeException re) {
			Logger.warn(re,"LucenePlayPlugin is unable to start index transaction because the indexer is not defined.");
		}
	}

	@Override
	public void afterInvocation() {
		// The request succeeded, so commit the transaction. If there's an
		// exception here we want to throw it to blow up the rest of the
		// application, so everyone knows about the error.
		Spring.getBeanOfType(Indexer.class).commit(false);
	}

	@Override
	public void onInvocationException(Throwable e) {
		try {
			// There was an exception thrown, so rollback the transaction.
			Spring.getBeanOfType(Indexer.class).rollback();
		} catch (RuntimeException re) {
			Logger.warn(re,"LucenePlayPlugin unable to roll back index transaction on exception.");
		}
	}

	@Override
	public void invocationFinally() {
		try {
			// Either the after, or exception methods above should have been called.
			// Just to make sure that we don't leave a transaction laying around we
			// will rollback now.
			Spring.getBeanOfType(Indexer.class).rollback();
		} catch (RuntimeException re) {
			Logger.warn(re,"LucenePlayPlugin unable to roll back index transaction on exception.");
		}
	}

}
