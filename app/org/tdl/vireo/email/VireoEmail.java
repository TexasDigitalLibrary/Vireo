package org.tdl.vireo.email;

import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;

/**
 * The vireo email interface. This is a simple object that holds all of the
 * details about how to handle an email so that it can be sent by the
 * EmailService. It supports the basic fields: To, Cc, Bcc, From, and ReplyTo.
 * Along with a set of parameters that can be embedded in the subject or email
 * message. If configured the email message can log it's success or failure to a
 * submission.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public interface VireoEmail {

	// To

	/**
	 * @return A list of all "To" recipients of this email. This is the live
	 *         list and may be modified.
	 */
	public List<InternetAddress> getTo();

	/**
	 * Add a new "To" recipient.
	 * 
	 * @param email
	 *            An email address
	 */
	public void addTo(String email);

	/**
	 * Add a new "To" recipient.
	 * 
	 * @param email
	 *            An email address
	 * @param name
	 *            A descriptive name
	 */
	public void addTo(String email, String name);

	/**
	 * Add a new "To" recipient
	 * 
	 * @param person
	 *            The person object.
	 */
	public void addTo(Person person);
	
	/**
	 * Add a new "To" recipient.
	 * 
	 * @param address
	 *            The address
	 */
	public void addTo(InternetAddress address);

	// CC

	/**
	 * @return A list of all Carbon Copy recipients of this email. This is the
	 *         live list and may be modified.
	 */
	public List<InternetAddress> getCc();

	/**
	 * Add a new Carbon Copy recipient.
	 * 
	 * @param email
	 *            An email address
	 */
	public void addCc(String email);

	/**
	 * Add a new Carbon Copy recipient.
	 * 
	 * @param email
	 *            An email address
	 * @param name
	 *            A descriptive name
	 */
	public void addCc(String email, String name);

	/**
	 * Add a new Carbon Copy recipient
	 * 
	 * @param person
	 *            The person object.
	 */
	public void addCc(Person person);
	
	/**
	 * Add a new Carbon Copy recipient.
	 * 
	 * @param address
	 *            The address
	 */
	public void addCc(InternetAddress address);

	// BCC

	/**
	 * @return A list of all Blind Carbon Copy recipients of this email. This is
	 *         the live list and may be modified.
	 */
	public List<InternetAddress> getBcc();

	/**
	 * Add a new Blind Carbon Copy recipient.
	 * 
	 * @param email
	 *            An email address
	 */
	public void addBcc(String email);

	/**
	 * Add a new Blind Carbon Copy recipient.
	 * 
	 * @param email
	 *            An email address.
	 * @param name
	 *            A descriptive name
	 */
	public void addBcc(String email, String name);

	/**
	 * Add a new Carbon Copy recipient
	 * 
	 * @param person
	 *            The person object.
	 */
	public void addBcc(Person person);
	
	/**
	 * Add a new Blind Carbon Copy recipient
	 * 
	 * @param address
	 *            The address
	 */
	public void addBcc(InternetAddress address);

	// ReplyTo

	/**
	 * @return The reply to address for this email.
	 */
	public InternetAddress getReplyTo();

	/**
	 * Set the new reply to address.
	 * 
	 * @param email
	 *            An email address
	 */
	public void setReplyTo(String email);

	/**
	 * Set the new reply to address
	 * 
	 * @param email
	 *            An email address
	 * @param name
	 *            A descriptive name
	 */
	public void setReplyTo(String email, String name);

	/**
	 * Set the new reply to address
	 * 
	 * @param person
	 *            The person object.
	 */
	public void setReplyTo(Person person);
	
	/**
	 * Set the new reply to address.
	 * 
	 * @param address
	 *            The address
	 */
	public void setReplyTo(InternetAddress address);

	// From

	/**
	 * @return The from address for this email.
	 */
	public InternetAddress getFrom();

	/**
	 * Set the from address.
	 * 
	 * @param email
	 *            An email address
	 */
	public void setFrom(String email);

	/**
	 * Set the from address.
	 * 
	 * @param email
	 *            An email address
	 * @param name
	 *            A descriptive name.
	 */
	public void setFrom(String email, String name);

	/**
	 * Set the new from address
	 * 
	 * @param person
	 *            The person object.
	 */
	public void setFrom(Person person);
	
	/**
	 * Set the from address
	 * 
	 * @param address
	 *            The address
	 */
	public void setFrom(InternetAddress address);

	// Subject and Message

	/**
	 * @return The subject
	 */
	public String getSubject();

	/**
	 * Set the subject, which may contain variables parameters in the form
	 * {name}. The parameters will be replaced with values when
	 * applyParameterSubstitution is called.
	 * 
	 * @param subject
	 *            The new subject.
	 */
	public void setSubject(String subject);

	/**
	 * @return The message
	 */
	public String getMessage();

	/**
	 * Set the message, which may contain variable parameters in the form
	 * {name}. The parameteres will be replaced with values when
	 * applyParamaterSubstitution is called.
	 * 
	 * @param message
	 *            The message
	 */
	public void setMessage(String message);

	/**
	 * Set the subject and message based upon a vireo email template.
	 * 
	 * @param template
	 *            The template.
	 */
	public void setTemplate(EmailTemplate template);

	// Parameters

	/**
	 * @return A list of all proposed parameter substitutions.
	 */
	public Map<String, String> getParameters();

	/**
	 * Add a new parameter substitutions
	 * 
	 * @param name
	 *            The name of the new parameter. In the message or body this
	 *            needs to be encoded as "{name}" for the value to be
	 *            substituted.
	 * @param value
	 *            The value of the substitution.
	 */
	public void addParameter(String name, String value);

	/**
	 * Add the set of predefined parameters based upon the value of the
	 * submission. These are:
	 * 
	 * FULL_NAME 
	 * FIRST_NAME 
	 * LAST_NAME 
	 * DOCUMENT_TITLE 
	 * GRAD_SEMESTER
	 * SUBMISSION_STATUS 
	 * SUBMISSION_STATUS_ASSIGNED_TO
	 * 
	 * @param submission
	 *            The submission
	 */
	public void addParameters(Submission submission);

	/**
	 * Modify the email's subject and message to replace any parameters with
	 * their values. Parameters should be encoded in the message and subject as
	 * {name}, they will be replaced with the value of any defined parameter. If
	 * the parameter does not exist then the email will not be modified leaving
	 * {name} in the text.
	 */
	public void applyParameterSubstitution();

	// Action Logs

	/**
	 * @return The person object for whom is responsible for sending this email.
	 *         This person will be encoded in the log message as the sender.
	 */
	public Person getLogPerson();

	/**
	 * @return The submission object where this email should be logged.
	 */
	public Submission getLogSubmission();

	/**
	 * After this email message has been successfully set whether a log message
	 * should be set on the provided submission object. If no submission object
	 * is provided then no log message will be set. The specified person will be
	 * recorded as the person responsible for the log action, if no person is
	 * specified then no one will be specified as the log action.
	 * 
	 * @param person
	 *            The person responsible, or null if none.
	 * @param submission
	 *            The submission where completion should be logged, or null if
	 *            no logs should be generated.
	 */
	public void setLogOnCompletion(Person person, Submission submission);

	/**
	 * @return The successful log message.
	 */
	public String getSuccessLogMessage();

	/**
	 * @param message
	 *            Set a successful log message.
	 */
	public void setSuccessLogMessage(String message);

	/**
	 * @param reason
	 *            Optionally provide a reason why the email message failed.
	 * @return The failure log message
	 */
	public String getFailureLogMessage(String reason);

	/**
	 * @param message
	 *            Set the failure log message.
	 */
	public void setFailureLogMessage(String message);

}
