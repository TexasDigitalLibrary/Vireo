package org.tdl.vireo.email.impl;

import java.io.UnsupportedEncodingException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.SimpleEmail;
import org.hibernate.ejb.CurrentEntityManagerImpl;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Preference;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.security.SecurityContext;

import play.Play;

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
	protected VireoEmailImpl(SecurityContext context, PersonRepository personRepo, SubmissionRepository subRepo) throws AddressException {
		
		this.personRepo = personRepo;
		this.subRepo = subRepo;
		
		// Set the default from address
		this.setFrom(Play.configuration.getProperty("mail.from"));
		this.setReplyTo(Play.configuration.getProperty("mail.replyto"));
		
		// Check to see if the current person want's to be CC'ed
		Person person = context.getPerson();
		if (person != null && person.getPreference(Preference.CC_EMAILS) != null) {
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
	public void addTo(String email) throws AddressException {
		addTo(email, null);
	}

	@Override
	public void addTo(String email, String name) throws AddressException {
		addTo(createAddress(email, name));
	}

	@Override
	public void addTo(Person person) throws AddressException {
		addTo(createAddress(person));
	}
	
	@Override
	public void addTo(InternetAddress address) throws AddressException {
		address.validate();
		to.add(address);
	}

	@Override
	public List<InternetAddress> getCc() {
		return cc;
	}

	@Override
	public void addCc(String email) throws AddressException {
		addCc(email, null);
	}

	@Override
	public void addCc(String email, String name) throws AddressException {
		addCc(createAddress(email, name));
	}
	
	@Override
	public void addCc(Person person) throws AddressException {
		addCc(createAddress(person));
	}
	
	@Override
	public void addCc(InternetAddress address) throws AddressException {
		address.validate();
		cc.add(address);
	}

	@Override
	public List<InternetAddress> getBcc() {
		return bcc;
	}

	@Override
	public void addBcc(String email) throws AddressException {
		addBcc(email, null);
	}

	@Override
	public void addBcc(String email, String name) throws AddressException {
		addBcc(createAddress(email, name));
	}

	@Override
	public void addBcc(Person person) throws AddressException {
		addBcc(createAddress(person));
	}
	
	@Override
	public void addBcc(InternetAddress address) throws AddressException {
		address.validate();
		bcc.add(address);
	}

	@Override
	public InternetAddress getReplyTo() {
		return _replyTo;
	}

	@Override
	public void setReplyTo(String email) throws AddressException {
		setReplyTo(email, null);
	}

	@Override
	public void setReplyTo(String email, String name) throws AddressException {
		setReplyTo(createAddress(email, name));
	}

	@Override
	public void setReplyTo(Person person) throws AddressException {
		setReplyTo(createAddress(person));
	}
	
	@Override
	public void setReplyTo(InternetAddress address) throws AddressException {
		address.validate();
		_replyTo = address;
	}

	@Override
	public InternetAddress getFrom() {
		return _from;
	}

	@Override
	public void setFrom(String email) throws AddressException {
		setFrom(email, null);
	}

	@Override
	public void setFrom(String email, String name) throws AddressException {
		setFrom(createAddress(email, name));
	}

	@Override
	public void setFrom(Person person) throws AddressException {
		setFrom(createAddress(person));
	}
	
	@Override
	public void setFrom(InternetAddress address) throws AddressException {
		address.validate();
		_from = address;
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
		
		if (sub.getStudentFirstName() != null || sub.getStudentLastName() != null)
			parameters.put("FULL_NAME", sub.getStudentFormattedName(NameFormat.FIRST_LAST));
		
		if (sub.getStudentFirstName() != null)
			parameters.put("FIRST_NAME", sub.getStudentFirstName());
		
		if (sub.getStudentLastName() != null)
			parameters.put("LAST_NAME", sub.getStudentLastName());
		
		if (sub.getDocumentTitle() != null)
			parameters.put("DOCUMENT_TITLE", sub.getDocumentTitle());
		
		if (sub.getDocumentType() != null)
			parameters.put("DOCUMENT_TYPE", sub.getDocumentType());
		
		if (sub.getState() != null)
			parameters.put("SUBMISSION_STATUS",sub.getState().getDisplayName());
		
		if (sub.getGraduationYear() != null) {
			String gradSemester = String.valueOf(sub.getGraduationYear());
			if (sub.getGraduationMonth() != null) {
				Integer monthInt = sub.getGraduationMonth();
				String monthName = new DateFormatSymbols().getMonths()[monthInt];
				
				gradSemester = monthName+", "+gradSemester;
			}
			
			parameters.put("GRAD_SEMESTER", gradSemester);
		}
		
		if (sub.getAssignee() != null)
			parameters.put("SUBMISSION_ASSIGNED_TO",sub.getAssignee().getFormattedName(NameFormat.FIRST_LAST));
		else 
			parameters.put("SUBMISSION_ASSIGNED_TO", "n/a");
	}

	@Override
	public void applyParameterSubstitution() {

		for (String name : parameters.keySet()) {
			String value = parameters.get(name);
			
			subject = subject.replaceAll("\\{"+name+"\\}", value);
			message = message.replaceAll("\\{"+name+"\\}", value);
		}
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
	private InternetAddress createAddress(String email, String name)
			throws AddressException {
		try {

			InternetAddress address = new InternetAddress(email);

			if (name != null && name.trim().length() > 0)
				address.setPersonal(name);

			return address;
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException(uee);
		}

	}
	
	/**
	 * Internal helper method to create InternedAddresses from people.
	 * 
	 * @param person The person
	 * @return A new InternetAddress
	 */
	private InternetAddress createAddress(Person person) throws AddressException {
		String email = person.getEmail();
		if (person.getCurrentEmailAddress() != null)
			email = person.getCurrentEmailAddress();
		
		String name = person.getFormattedName(NameFormat.FIRST_LAST);
		return createAddress(email,name);
	}

}
