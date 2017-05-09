package org.tdl.vireo.email;


/**
 * Email service handling the sending of emails either in the background or in
 * the current thread.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface EmailService {

	/**
	 * Simple method to create a new blank vireo email. This is just for
	 * convenience, you could just ask spring for a new VireoEmail as well.
	 * 
	 * @return A blank email.
	 */
	public VireoEmail createEmail();

	/**
	 * Send an email.
	 * 
	 * @param email
	 *            The email, must have a message, body, and some recipients.
	 * @param wait
	 *            Whether to wait for the email to be sent, or send it in the
	 *            background. If true, then any errors encountered will be
	 *            thrown as runtime exceptions.
	 */
	public void sendEmail(VireoEmail email, boolean wait);

	
	/**
	 * @return True if a background email job is currently in the queue to be
	 *         run, or is executing concurrently.
	 */
	public boolean isJobRunning();
}
