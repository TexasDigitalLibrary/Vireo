package org.tdl.vireo.state;

import org.tdl.vireo.model.Submission;

/**
 * The state transition listener allows for modules to hook into events for when
 * submissions change state. Listeners may then take action after the
 * transition.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface StateTransitionListener {

	/**
	 * Notification that the submission has immediatly changed state. This
	 * method will be called *after* the transition but prior to the object
	 * being saved. This method may not take a considerable amount of time, if
	 * that is needed the a background process will need to be queued based upon
	 * this event and not handled within the currently active thread.
	 * 
	 * @param submission
	 *            The submission which just transitioned from one state to
	 *            another.
	 * @param previousState
	 *            The previous state the submission was in.
	 */
	public void transition(Submission submission, State previousState);

}
