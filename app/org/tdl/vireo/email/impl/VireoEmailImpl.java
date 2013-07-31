package org.tdl.vireo.email.impl;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.tdl.vireo.constant.AppPref;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.services.StringVariableReplacement;

import play.Play;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;

/**
 * Implementation of the vireo email object.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class VireoEmailImpl implements VireoEmail {

	// Spring dependencies
	public PersonRepository personRepo;
	public SubmissionRepository subRepo;

	// Dynamic Fields
	public List<InternetAddress> to = new ArrayList<InternetAddress>();
	public List<InternetAddress> cc = new ArrayList<InternetAddress>();
	public List<InternetAddress> bcc = new ArrayList<InternetAddress>();
	public InternetAddress _replyTo;
	public InternetAddress _from;

	public String subject;
	public String message;

	public Map<String, String> parameters = new HashMap<String, String>();

	public Long logPersonId;
	public Long logSubmissionId;

	public String logSuccessMessage;
	public String logFailureMessage;

	/**
	 * Construct a new vireo email impl. This should only be called by spring.
	 * 
	 * @param context
	 *            The security context.
	 * @param personRepo
	 *            The person repository.
	 * @param subRepo
	 *            The submisison repository.
	 */
	protected VireoEmailImpl(SecurityContext context, PersonRepository personRepo, SubmissionRepository subRepo) {
	
		// Check our play requirements
		if (Play.configuration.getProperty("mail.from") == null ||
			Play.configuration.getProperty("mail.replyto") == null)
			throw new IllegalArgumentException("The configuration parameters \"mail.from\" and \"mail.replyto\" are required for sending email and must be defined in the application.conf");
		
		this.personRepo = personRepo;
		this.subRepo = subRepo;
	
		// Set the default from address
		this.setFrom(Play.configuration.getProperty("mail.from"));
		this.setReplyTo(Play.configuration.getProperty("mail.replyto"));
		
		// Check to see if the current person want's to be CC'ed
		Person person = context.getPerson();
		if (person != null && person.getPreference(AppPref.CC_EMAILS) != null) {
			String email = person.getEmail();
			if (person.getCurrentEmailAddress() != null)
				email = person.getCurrentEmailAddress();
			
			this.addCc(email,person.getFormattedName(NameFormat.LAST_FIRST));
		}
	}

	@Override
	public List<InternetAddress> getTo() {
		return to;
	}

	@Override
	public void addTo(String email) {
		addTo(email, null);
	}

	@Override
	public void addTo(String email, String name) {
		addTo(createAddress(email, name));
	}

	@Override
	public void addTo(Person person) {
		addTo(createAddress(person));
	}
	
	@Override
	public void addTo(InternetAddress address) {
		to.add(validateAddress(address));
	}

	@Override
	public List<InternetAddress> getCc() {
		return cc;
	}

	@Override
	public void addCc(String email) {
		addCc(email, null);
	}

	@Override
	public void addCc(String email, String name) {
		addCc(createAddress(email, name));
	}
	
	@Override
	public void addCc(Person person) {
		addCc(createAddress(person));
	}
	
	@Override
	public void addCc(InternetAddress address) {
		cc.add(validateAddress(address));
	}

	@Override
	public List<InternetAddress> getBcc() {
		return bcc;
	}

	@Override
	public void addBcc(String email) {
		addBcc(email, null);
	}

	@Override
	public void addBcc(String email, String name) {
		addBcc(createAddress(email, name));
	}

	@Override
	public void addBcc(Person person) {
		addBcc(createAddress(person));
	}
	
	@Override
	public void addBcc(InternetAddress address) {
		bcc.add(validateAddress(address));
	}

	@Override
	public InternetAddress getReplyTo() {
		return _replyTo;
	}

	@Override
	public void setReplyTo(String email) {
		setReplyTo(email, null);
	}

	@Override
	public void setReplyTo(String email, String name) {
		setReplyTo(createAddress(email, name));
	}

	@Override
	public void setReplyTo(Person person) {
		setReplyTo(createAddress(person));
	}
	
	@Override
	public void setReplyTo(InternetAddress address) {
		_replyTo = validateAddress(address);
	}

	@Override
	public InternetAddress getFrom() {
		return _from;
	}

	@Override
	public void setFrom(String email) {
		setFrom(email, null);
	}

	@Override
	public void setFrom(String email, String name) {
		setFrom(createAddress(email, name));
	}

	@Override
	public void setFrom(Person person) {
		setFrom(createAddress(person));
	}
	
	@Override
	public void setFrom(InternetAddress address) {
		_from = validateAddress(address);
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void setTemplate(EmailTemplate template) {
		this.subject = template.getSubject();
		this.message = template.getMessage();
	}

	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
	public void addParameter(String name, String value) {
		parameters.put(name,value);
	}

	@Override
	public void addParameters(Submission sub) {
		
		this.parameters = StringVariableReplacement.setParameters(sub);

	}

	@Override
	public void applyParameterSubstitution() {

		subject = StringVariableReplacement.applyParameterSubstitution(subject, parameters);
		message = StringVariableReplacement.applyParameterSubstitution(message, parameters);
	
	}

	@Override
	public Person getLogPerson() {
		if (logPersonId == null)
			return null;
		
		return personRepo.findPerson(logPersonId);
	}

	@Override
	public Submission getLogSubmission() {
		if (logSubmissionId == null)
			return null;
		
		return subRepo.findSubmission(logSubmissionId);
	}

	@Override
	public void setLogOnCompletion(Person person, Submission submission) {
		
		if (person == null)
			logPersonId = null;
		else
			logPersonId = person.getId();
		
		if (submission == null)
			logSubmissionId = null;
		else
			logSubmissionId = submission.getId();
	}

	@Override
	public String getSuccessLogMessage() {
		
		if (logSuccessMessage != null) {
			return logSuccessMessage;
		}
		
		// Build a default success message
		String recipients = "";
		for (InternetAddress address : to) {
			if (recipients.length() != 0)
				recipients += ", ";
			if (address.getPersonal() != null)
				recipients += address.getPersonal();
			else
				recipients += address.getAddress();
		}
		
		return String.format("Email sent to %1s; %2s: '%3s'",recipients,subject,message);
		
	}

	@Override
	public void setSuccessLogMessage(String message) {
		logSuccessMessage = message;
	}

	@Override
	public String getFailureLogMessage(String reason) {
		if (logFailureMessage != null) {
			return logFailureMessage;
		}
		
		// Build a default success message
		String recipients = "";
		for (InternetAddress address : to) {
			if (recipients.length() != 0)
				recipients += ", ";
			if (address.getPersonal() != null)
				recipients += address.getPersonal();
			else
				recipients += address.getAddress();
		}
		
		String because = "";
		if (reason != null && reason.trim().length() > 0)
			because = " because '"+reason+"'";
		
		return String.format("Failed to send email to %1s; %2s: '%3s'%4s",recipients,subject,message,because);
	}

	@Override
	public void setFailureLogMessage(String message) {
		logFailureMessage = message;
	}

	/**
	 * Internal helper method to create an InternetAddress. This class will
	 * recast encoding errors as runtime exceptions while still allowing
	 * AddressExceptions to be passed through.
	 * 
	 * @param email
	 *            The email address.
	 * @param name
	 *            The address's name
	 * @return A new InternetAddress
	 */
	private InternetAddress createAddress(String email, String name) {
		try {

			InternetAddress address = new InternetAddress(email);

			if (name != null && name.trim().length() > 0)
				address.setPersonal(name);

			return address;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	/**
	 * Internal helper method to create InternedAddresses from people.
	 * 
	 * @param person The person
	 * @return A new InternetAddress
	 */
	private InternetAddress createAddress(Person person) {
		String email = person.getEmail();
		if (person.getCurrentEmailAddress() != null)
			email = person.getCurrentEmailAddress();
		
		String name = person.getFormattedName(NameFormat.FIRST_LAST);
		return createAddress(email,name);
	}
	
	/**
	 * Validate the provided email address, and throw a runtime exception if
	 * invalid.
	 * 
	 * @param address
	 *            The address to validate.
	 */
	private InternetAddress validateAddress(InternetAddress address) {
		try {
			address.validate();
			return address;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
