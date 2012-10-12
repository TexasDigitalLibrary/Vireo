package org.tdl.vireo.export;

import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.search.SearchFilter;
import org.tdl.vireo.state.State;

/**
 * The deposit service handles the common tasks of depositing a submission or
 * multiple submissions into a remote repository. It will maintain a background
 * through for these tasks to proceed on, update the state of the submission,
 * and handle any errors. It is expected that there will only be one
 * implementation of this service.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface DepositService {

	/**
	 * Deposit one submission and if successfully change to the published state.
	 * 
	 * Deposits may take a substantial amount of time depending on how
	 * responsive the remote server is and the size of the submission. Most uses
	 * cases should use not wait for competition before returning.
	 * 
	 * As many errors as possible will be checked before separating into a
	 * separate thread as possible. I.e. the location will be checked for all
	 * the required attribute, the state of the submission object etc. However
	 * some errors may not occur until deposit time. When these errors occur the
	 * submission will remain in it's current state and an action log item will
	 * be generated detailing the error encountered.
	 * 
	 * @param location
	 *            The deposit location where this submission should be deposited
	 *            into. The location must be complete, containing a repository
	 *            URL, collection URL, and packager. It is also unwise if the
	 *            depositor is different than this implementation.
	 * 
	 * @param submission
	 *            The single submission to deposit.
	 * 
	 * @param successState
	 *            The state the submission should will be transition into upon
	 *            successfully depositing. If the deposit failed then the
	 *            submission will remain in it's original state.
	 * 
	 * @param wait
	 *            If true the method will not return until the deposit is
	 *            completed, otherwise depositing will take place in a
	 *            background thread.
	 * 
	 */
	public void deposit(DepositLocation location, Submission submission,
			State successState, boolean wait);

}
