package org.tdl.vireo.model;

/**
 * An abstract parent object for all mock objects. This class just keeps tracks
 * and assigns each instance a unique id.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public abstract class AbstractMock {

	/* Static sequence of ids, so each object recieves a unique id */
	public static long idSequence = 1L;

	/* Unique id for this instance */
	public Long id;

	/**
	 * Construct a new mock object assigning it a unique id.
	 */
	public AbstractMock() {
		id = idSequence++;
	}
	
	public Long getId() {
		return id;
	}

}
