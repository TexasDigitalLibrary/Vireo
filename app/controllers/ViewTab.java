package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FilenameUtils;
import org.tdl.vireo.email.EmailService;
import org.tdl.vireo.email.VireoEmail;
import org.tdl.vireo.export.DepositService;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.proquest.ProquestVocabularyRepository;
import org.tdl.vireo.state.State;

import play.Logger;
import play.db.jpa.JPA;
import play.modules.spring.Spring;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;
import play.mvc.With;

/**
 * The controller for the view tab.
 * 
 * @author Micah Cooper
 *
 */

@With(Authentication.class)
public class ViewTab extends AbstractVireoController {

	public static DepositService depositService = Spring.getBeanOfType(DepositService.class);
	public static EmailService emailService = Spring.getBeanOfType(EmailService.class);

	public static class AttachmentSortByDate implements Comparator<Attachment> {

		@Override
		public int compare(Attachment a, Attachment b) {
			if(a==null && b==null)
				return 0;
			
			if(a==null)
				return -1;
			
			if(b==null)
				return -1;
			
			return b.getDate().compareTo(a.getDate());
		}
		
	}	
	
	/**
	 * The main view method.
	 */
	@Security(RoleType.REVIEWER)
	public static void view() {			
		
		if(params.get("subId") != null){
			session.put("submission", params.get("subId"));
		}

		Long id = null;
		if(session.contains("submission")){
			id = Long.valueOf(session.get("submission"));
		} else {
			FilterTab.list();
		}
		Submission submission = subRepo.findSubmission(id);
		
		//Check for "Add Action Log Comment"
		if(params.get("addActionLogComment")!=null)
			addActionLogComment(submission);
		
		//Check for "Add File"
		if(params.get("addFile")!=null)
			addFile(submission);		
		
		//Check for "Edit File"
		if(params.get("editFile")!=null)
			editFile(submission);
		
		//Check for "Delete File"
		if(params.get("deleteFile")!=null)
			deleteFile(submission);
		
		JPA.em().detach(submission);
		submission = subRepo.findSubmission(id);
		
		Boolean isManager = context.isManager();		

		if(submission==null){
			FilterTab.list();
		}
		
		Person submitter = submission.getSubmitter();
		
		String advisorUrl = getAdvisorURL(submission);
		
		List<EmailTemplate> templates = settingRepo.findAllEmailTemplates();
		List<CustomActionDefinition> actions = settingRepo.findAllCustomActionDefinition();
		
		String gradMonth = null;		
		if(submission.getGraduationMonth() != null)
			gradMonth = new DateFormatSymbols().getMonths()[submission.getGraduationMonth()];
		
		List<ActionLog> actionLogs	= subRepo.findActionLog(submission);
		
		List<State> states = stateManager.getAllStates();
				
		List<State> transitions = submission.getState().getTransitions(submission);
		List<CustomActionValue> actionValues = submission.getCustomActions();
				
		List<Person> assignees = personRepo.findPersonsByRole(RoleType.REVIEWER);	
		
		List<DepositLocation> depositLocations = settingRepo.findAllDepositLocations();
		
		List<Attachment> attachments = submission.getAttachments();
		Collections.sort(attachments, new AttachmentSortByDate());
		
		List<String> attachmentTypes = new ArrayList<String>();
		for(AttachmentType type : AttachmentType.values()){
			attachmentTypes.add(type.toString());
		}
				
		String nav = "view";
		render(	nav,
				submission,
				submitter,
				isManager,
				advisorUrl,
				gradMonth, 
				actionLogs, 
				settingRepo, 
				states,
				assignees, 
				transitions, 
				templates, 
				actions, 
				actionValues,
				depositLocations,
				attachments,
				attachmentTypes
				);
	}

	/**
	 * A method to update the "Personal Info", "Document Info" and "Degree Info"
	 * excluding committee members.
	 * 
	 * @param subId (The submission id)
	 * @param field (The name of the form field being updated)
	 * @param value (The value being submitted)
	 */
	@Security(RoleType.REVIEWER)
	public static void updateJSON(Long subId, String field, String value){
		if(value!=null){
			value = value.trim();
		
			if("none".equals(value.toLowerCase()) || "null".equals(value.toLowerCase()))
				value=null;
		}
		
		Submission submission = subRepo.findSubmission(subId);
		Person submitter = submission.getSubmitter();

		Object currentValue = null;
		String message = null;
		DegreeLevel degreeLevel = null;

		try{

			//First Name
			if("firstName".equals(field)) {
				submission.setStudentFirstName(value);
				currentValue = submission.getStudentFirstName();

				//Middle Name
			} else if("middleName".equals(field)) {
				submission.setStudentMiddleName(value);
				currentValue = submission.getStudentMiddleName();

				//Last Name
			} else if("lastName".equals(field)) {
				submission.setStudentLastName(value);				
				currentValue = submission.getStudentLastName();	

				//Email
			} else if("email".equals(field)) {
				if(value==null || value.length()==0)
					throw new RuntimeException("Email is required.");

				try {
					new InternetAddress(value).validate();
				} catch (AddressException ae) {
					throw new RuntimeException("The Email provided is invalid.");
				}

				submitter.setCurrentEmailAddress(value);
				currentValue = submitter.getCurrentEmailAddress();

				//Year of Birth
			} else if("birthYear".equals(field)) {						
				if(value!=null && value.length()>0) {
					Integer birthYearInt = null;
					try{
						birthYearInt = Integer.valueOf(value);
					} catch (NumberFormatException nfe) {
						throw new RuntimeException("Your birth year is invalid.");
					}

					if (birthYearInt < 1900 || birthYearInt > Calendar.getInstance().get(Calendar.YEAR))
						throw new RuntimeException("Your birth year is invalid, please use a four digit year between 1900 and "+Calendar.getInstance().get(Calendar.YEAR)+".");

					submission.setStudentBirthYear(Integer.valueOf(value));			
				} else {
					submission.setStudentBirthYear(null);
				}
				currentValue = submission.getStudentBirthYear();

				//Permanent Phone
			} else if("permPhone".equals(field)){
				if(value==null || value.length()==0)
					throw new RuntimeException("Permanent Phone is required.");

				submitter.setPermanentPhoneNumber(value);
				currentValue = submitter.getPermanentPhoneNumber();

				//Permanent Email
			} else if("permEmail".equals(field)){
				if(value!=null && value.length()>0) {
					try {
						new InternetAddress(value).validate();
					} catch (AddressException ae) {
						throw new RuntimeException("The Email provided is invalid.");
					}
				}

				submitter.setPermanentEmailAddress(value);			
				currentValue = submitter.getPermanentEmailAddress();

				//Permanent Address
			} else if("permAddress".equals(field)){
				if(value==null || value.length()==0)
					throw new RuntimeException("Permanent Address is required.");

				submitter.setPermanentPostalAddress(value);
				currentValue = submitter.getPermanentPostalAddress();

				//Current Phone
			} else if("currentPhone".equals(field)){			
				submitter.setCurrentPhoneNumber(value);
				currentValue = submitter.getCurrentPhoneNumber();

				//Current Address
			} else if("currentAddress".equals(field)){
				submitter.setCurrentPostalAddress(value);
				currentValue = submitter.getCurrentPostalAddress();

				//Title
			} else if("title".equals(field)){
				if(value==null || value.length()==0)
					throw new RuntimeException("Title is required.");

				submission.setDocumentTitle(value);
				currentValue = submission.getDocumentTitle();

				//Embargo
			} else if("embargo".equals(field)){			
				submission.setEmbargoType(settingRepo.findEmbargoType(Long.parseLong(value)));
				currentValue = submission.getEmbargoType().getName();

				//UMI Release
			} else if("umiRelease".equals(field)){
				Boolean umi = null;
				if("yes".equals(value)) {
					umi = true;
				} else {
					umi = false;					
				}
				submission.setUMIRelease(umi);
				currentValue = submission.getUMIRelease() ? "yes" : "no";				

				//Document Type
			} else if("docType".equals(field)){
				submission.setDocumentType(value);
				currentValue = submission.getDocumentType();

				//Document Keywords
			} else if("keywords".equals(field)){			
				submission.setDocumentKeywords(value);
				currentValue = submission.getDocumentKeywords();
			
				//Document Subjects
			} else if("subjects".equals(field)){				
				String primary = params.get("primary");
				String secondary = params.get("secondary");
				String tertiary = params.get("tertiary");

				submission.getDocumentSubjects().clear();
				if (primary != null && primary.trim().length() > 0)
					submission.addDocumentSubject(primary);
				
				if (secondary != null && secondary.trim().length() > 0)
					submission.addDocumentSubject(secondary);
				
				if (tertiary != null && tertiary.trim().length() > 0)
					submission.addDocumentSubject(tertiary);
			
				//Document Language
			}else if("docLanguage".equals(field)){
				submission.setDocumentLanguage(value);
				currentValue = submission.getDocumentLanguageLocale().getDisplayName();
				
				//Published Material
			}else if("publishedMaterial".equals(field)){
				submission.setPublishedMaterial(value);
				currentValue = submission.getPublishedMaterial();
				
				//Document Abstract
			} else if("abstract".equals(field)){			
				submission.setDocumentAbstract(value);
				currentValue = submission.getDocumentAbstract();

				//College/School
			} else if("college".equals(field)){			
				submission.setCollege(value);
				currentValue = submission.getCollege();

				//Program
			} else if("program".equals(field)){
				submission.setProgram(value);
				currentValue = submission.getProgram();
				
				//Department
			} else if("department".equals(field)){
				submission.setDepartment(value);
				currentValue = submission.getDepartment();

				//Degree
			} else if("degree".equals(field)){			
				submission.setDegree(value);
				currentValue = submission.getDegree();
				degreeLevel = settingRepo.findDegreeByName(submission.getDegree()).getLevel();
				submission.setDegreeLevel(degreeLevel);

				//Major
			} else if("major".equals(field)){			
				submission.setMajor(value);
				currentValue = submission.getMajor();

				//Graduation Semester
			} else if("gradSemester".equals(field)){

				List<String> parsedGrad = parseGraduation(value);

				int month = monthNameToInt(parsedGrad.get(0));

				Integer year = null;
				try{
					year = Integer.valueOf(parsedGrad.get(1));
				} catch (NumberFormatException nfe) {
					throw new RuntimeException("The graduation year is invalid.");
				}

				submission.setGraduationMonth(month);
				submission.setGraduationYear(year);

				String gradMonth = new DateFormatSymbols().getMonths()[submission.getGraduationMonth()];

				currentValue = gradMonth + " " + submission.getGraduationYear().toString();

				//Defense Date
			} else if("defenseDate".equals(field)){
				Logger.info("Defense Date: "+value);
				Date defenseDate = null;
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				if(value!=null) {
					try {					
						defenseDate = (Date)formatter.parse(value);						
					} catch (ParseException e) {
						throw new RuntimeException("The defense date is invalid.");
					}
				}
				submission.setDefenseDate(defenseDate);
				
				if(submission.getDefenseDate()!=null)
					currentValue = formatter.format(submission.getDefenseDate());
				else
					currentValue = null;
				
				//Advisor Email
			} else if("advisorEmail".equals(field)){
				if(value==null || value.length()==0)
					throw new RuntimeException("Advisor Email is required.");

				try {
					new InternetAddress(value).validate();
				} catch (AddressException ae) {
					throw new RuntimeException("The Advisor Email provided is invalid.");
				}

				submission.setCommitteeContactEmail(value);
				currentValue = submission.getCommitteeContactEmail();
				
				// Reviewer Notes
			} else if("reviewerNotes".equals(field)){			
				submission.setReviewerNotes(value);
				currentValue = submission.getReviewerNotes();

			} else {
				throw new IllegalArgumentException("Unknown field type: "+field);
			}
			submitter.save();
			submission.save();

		} catch(RuntimeException re) {
			if(value==null){
				value="";
			}
			value = escapeJavaScript(value);
			message = re.getMessage();
			Logger.error("Unable to update "+field+" in the view tab of the admin section.");
			renderJSON("{ \"success\": false, \"value\": \""+value+"\", \"message\": \""+message+"\" }");
		}

		if(currentValue==null) {
			value = escapeJavaScript("");
		} else {
			value = escapeJavaScript(currentValue.toString());
		}

		String json;

		if(degreeLevel!=null){
			json = "{ \"success\": true, \"value\": \""+value+"\", \"degreeLevel\": \""+degreeLevel+"\" }";
		} else {
			json = "{ \"success\": true, \"value\": \""+value+"\" }";
		}

		renderJSON(json);
	}

	/**
	 * A method to add a new committee member.
	 * 
	 * @param subId (The submission id)
	 * @param firstName (The committee members first name)
	 * @param lastName (The committee members last name)
	 * @param middleName (The committee members middle name)
	 * @param roles (List of roles the committee member has) - optional
	 */
	@Security(RoleType.REVIEWER)
	public static void addCommitteeMemberJSON(Long subId, String firstName, String lastName, String middleName){
		if(firstName != null){
			firstName = firstName.trim();
		
			if("none".equals(firstName.toLowerCase()) || "null".equals(firstName.toLowerCase()))
				firstName=null;
		}
		if(lastName != null){
			lastName = lastName.trim();
		
			if("none".equals(lastName.toLowerCase()) || "null".equals(lastName.toLowerCase()))
				lastName=null;
		}
		if(middleName != null){
			middleName = middleName.trim();
		
			if("none".equals(middleName.toLowerCase()) || "null".equals(middleName.toLowerCase()))
				middleName=null;
		}
		
		String[] roles = params.get("roles", String[].class);

		
		Submission submission = subRepo.findSubmission(subId);		
		CommitteeMember newMember = null;
		
		try {

			if(firstName == null && lastName == null)
				throw new RuntimeException("Committee Member First or Last name is required.");

			newMember = submission.addCommitteeMember(firstName, lastName, middleName).save();
			if (roles != null) {
				for(String role : roles) {
					newMember.addRole(role);
				}
			}
			newMember.save();

		} catch (RuntimeException re) {
			firstName = escapeJavaScript(firstName);
			lastName = escapeJavaScript(lastName);
			middleName = escapeJavaScript(middleName);
			String rolesJSON = toJSON(roles);

			
			renderJSON("{ \"success\": false, \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"middleName\": \""+middleName+"\", \"roles\": "+rolesJSON+", \"message\": \""+re.getMessage()+"\" }");
		}

		submission.save();
		
		firstName = escapeJavaScript(firstName);
		lastName = escapeJavaScript(lastName);
		middleName = escapeJavaScript(middleName);
		Long id = newMember.getId();
		String rolesJSON = toJSON(roles);


		String json = "{ \"success\": true, \"id\": \""+id+"\", \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"middleName\": \""+middleName+"\", \"roles\": "+rolesJSON+" }";

		renderJSON(json);

	}

	/**
	 * A method to update a committee members information.
	 * 
	 * @param id (The committee member id)
	 * @param firstName (The committee members first name)
	 * @param lastName (The committee members last name)
	 * @param middleName (The committee members middle name)
	 * @param roles (List of roles the committee member has) - optional
	 */
	
	@Security(RoleType.REVIEWER)
	public static void updateCommitteeMemberJSON(Long id, String firstName, String lastName, String middleName){
		if(firstName != null){
			firstName = firstName.trim();
		
			if("none".equals(firstName.toLowerCase()) || "null".equals(firstName.toLowerCase()))
				firstName=null;
		}
		if(lastName != null){
			lastName = lastName.trim();
		
			if("none".equals(lastName.toLowerCase()) || "null".equals(lastName.toLowerCase()))
				lastName=null;
		}
		if(middleName != null){
			middleName = middleName.trim();
		
			if("none".equals(middleName.toLowerCase()) || "null".equals(middleName.toLowerCase()))
				middleName=null;
		}
		
		String[] roles = params.get("roles", String[].class);
		
		try {

			if(firstName == null && lastName == null)
				throw new RuntimeException("Committee Member First or Last name is required");

			CommitteeMember committeeMember = subRepo.findCommitteeMember(id);

			committeeMember.setFirstName(firstName);
			committeeMember.setLastName(lastName);
			committeeMember.setMiddleName(middleName);
			committeeMember.getRoles().clear();
			if (roles != null) {
				for(String role : roles) {
					committeeMember.addRole(role);
				}
			}

			committeeMember.save();

		} catch (RuntimeException re) {
			firstName = escapeJavaScript(firstName);
			lastName = escapeJavaScript(lastName);
			middleName = escapeJavaScript(middleName);
			String rolesJSON = toJSON(roles);

			renderJSON("{ \"success\": false, \"id\": \""+id+"\", \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"middleName\": \""+middleName+"\", \"roles\": "+rolesJSON+", \"message\": \""+re.getMessage()+"\" }");
		}

		firstName = escapeJavaScript(firstName);
		lastName = escapeJavaScript(lastName);
		middleName = escapeJavaScript(middleName);
		String rolesJSON = toJSON(roles);

		String json = "{ \"success\": true, \"id\": \""+id+"\", \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"middleName\": \""+middleName+"\", \"roles\": "+rolesJSON+" }";

		renderJSON(json);

	}

	/**
	 * A method to remove committee members.
	 * 
	 * @param id (The committee member id)
	 */
	@Security(RoleType.REVIEWER)
	public static void removeCommitteeMemberJSON(Long id){

		subRepo.findCommitteeMember(id).delete();

		renderJSON("{ \"success\": true, \"id\": \""+id+"\" }");

	}

	/**
	 * A method to refresh the content of the action log table with the latest data.
	 *  
	 * @param id (The submission id)
	 */
	@Security(RoleType.REVIEWER)
	public static void refreshActionLogTable(Long id){

		Submission submission = subRepo.findSubmission(id);

		List<ActionLog> actionLogs	= subRepo.findActionLog(submission);
		
		renderTemplate("ViewTab/actionLogTable.include", actionLogs, submission);
	}

	/**
	 * A method to refresh the content of the left column with the latest data.
	 * 
	 * @param id (The submission id)
	 */
	@Security(RoleType.REVIEWER)
	public static void refreshLeftColumn(Long id){

		Submission submission = subRepo.findSubmission(id);

		List<ActionLog> actionLogs	= subRepo.findActionLog(submission);
		List<CustomActionDefinition> actions = settingRepo.findAllCustomActionDefinition();
		List<CustomActionValue> actionValues = submission.getCustomActions();

		renderTemplate("ViewTab/leftColumn.include", actionLogs, submission, actions, actionValues);

	}
	
	/**
	 * A method to refresh the header with the latest data
	 * 
	 * @param id (The submission id)
	 */
	@Security(RoleType.REVIEWER)
	public static void refreshHeader(Long id){
		Submission submission = subRepo.findSubmission(id);
		
		renderTemplate("ViewTab/header.include", submission);
	}

	/**
	 * A method to change the status of a submission.
	 * 
	 * @param id (The submission id)
	 */
	
	@Security(RoleType.REVIEWER)
	public static void changeSubmissionStatus(Long id) {

		String beanName = params.get("submission-status");
		
		if(!params.get("special_value").isEmpty())
			beanName = params.get("special_value");
		
		Submission submission = subRepo.findSubmission(id);

		State state = null;
		
		if("deleteState".equals(beanName)) {
			submission.delete();
			controllers.FilterTab.list();
		} else {
			if("cancelState".equals(beanName)) {
				state = stateManager.getCancelState();
			} else {
				state = stateManager.getState(beanName);
			}
			
			Long depositLocationId = params.get("depositLocationId", Long.class);
			if (state.isDepositable() && depositLocationId != null) {
				// Deposit the item
				DepositLocation location = settingRepo.findDepositLocation(depositLocationId);
				depositService.deposit(location, submission, state, true);
				view();
			}
			
			// Normal state transition, update & save.
			submission.setState(state);
			submission.save();
			view();
		}
	}

	/**
	 * A method to change the assignee of a submission.
	 * 
	 * @param id (The submission id)
	 */
	
	@Security(RoleType.REVIEWER)
	public static void changeAssignedTo(Long id){

		Submission submission = subRepo.findSubmission(id);

		String assigneeString = params.get("assignee");
		Person assignee = null;
		
		if(!params.get("special_value").isEmpty())
			assigneeString = params.get("special_value");
		
		if(!"unassign".equals(assigneeString))
			assignee = personRepo.findPerson(Long.valueOf(assigneeString));
		
		submission.setAssignee(assignee);
		
		submission.save();

		view();

	}
	
	/**
	 * A method to change the submission date.
	 * 
	 * @param id (The submission id)
	 */
	@Security(RoleType.REVIEWER)
	public static void changeSubmissionDate(Long id){
		
		Submission submission = subRepo.findSubmission(id);
		
		try{
			String newDate = params.get("submission-date");
			DateFormat formatter;
			Date date;
			
			formatter = new SimpleDateFormat("MM/dd/yyyy");
			date = (Date)formatter.parse(newDate);
			
			submission.setSubmissionDate(date);
			submission.save();
		} catch (ParseException e) {
			validation.addError("changeSubmissionDate", "The date provided was not formatted correctly. Please format your date like MM/DD/YYYY.");
		}
		
		view();
		
	}
	
	/**
	 * A method to add an Action Log Comment and send an email if requested.
	 * 
	 * @param id (The submission id)
	 */
	
	@Security(RoleType.REVIEWER)
	private static void addActionLogComment(Submission submission){
				
		String subject = params.get("subject");
		String message = params.get("comment");
		
		if(params.get("email_student")!=null) {
			
			if(subject == null || subject.isEmpty())
				validation.addError("addActionLogSubject", "You must include a subject when sending an email.");
		
			if(message == null || message.isEmpty())
				validation.addError("addActionLogComment", "You must include a comment when sending an email.");
			
		}
		
		if(!validation.hasErrors()) {
			if(params.get("status_change") != null)
				submission.setState(stateManager.getState("NeedsCorrection"));
						
			VireoEmail email = emailService.createEmail();
			
			// Run the parameters
			email.addParameters(submission);
			email.setSubject(subject);
			email.setMessage(message);
			email.applyParameterSubstitution();
			
			//Create list of recipients
			email.addTo(submission.getSubmitter());
			
			//Create list of carbon copies
			if(params.get("cc_advisor") != null && submission.getCommitteeContactEmail() != null) {
				email.addCc(submission.getCommitteeContactEmail());
			}
			
			email.setFrom(context.getPerson());
			email.setReplyTo(context.getPerson());
						
			if(params.get("email_student") != null && "public".equals(params.get("visibility"))) {	
				// Send the email and log it after completion
				email.setLogOnCompletion(context.getPerson(), submission);
				emailService.sendEmail(email,false);
				
			} else {
				// Otherwise just log it.
				subject = email.getSubject();
				message = email.getMessage();
				
				String entry;
				if (subject != null && subject.trim().length() > 0)
					entry = subject+": "+message;
				else
					entry = message;
				
				ActionLog log = submission.logAction(entry);
				if("private".equals(params.get("visibility")))
					log.setPrivate(true);
				
				submission.save();
				log.save();
			}
		}
	}
	
	/**
	 * A method to update the add comment subject and comment with 
	 * a templates subject and message.
	 * 
	 * @param id (The id of the template.)
	 */
	@Security(RoleType.REVIEWER)
	public static void retrieveTemplateJSON(Long id) {
		
		String subject = "";
		String message = "";
		
		if(id != null) {
			EmailTemplate template = settingRepo.findEmailTemplate(id);
		
			subject = escapeJavaScript(template.getSubject());
			message = escapeJavaScript(template.getMessage());
		}
		
		String json = "{ \"success\": true, \"subject\": \""+subject+"\", \"message\": \""+message+"\" }";
		
		renderJSON(json);
		
	}
	
	/**
	 * A method to update the custom action values from the view tab.
	 * 	
	 * @param id (The submission id)
	 * @param action (The name of the custom action)
	 * @param value (The boolean value of the custom action)
	 */
	@Security(RoleType.REVIEWER)
	public static void updateCustomActionsJSON(Long id, String action, Boolean value) {
		
		Submission submission = subRepo.findSubmission(id);
				
		String actionIdString = action.replaceAll("custom_action_", "");
		Long actionId = Long.valueOf(actionIdString);
		
		CustomActionDefinition actionDef = settingRepo.findCustomActionDefinition(actionId);
		
		if(submission.getCustomAction(actionDef) == null) {
			submission.addCustomAction(actionDef, value);
		} else {
			submission.getCustomAction(actionDef).delete();
		}
		
		submission.save();
	}
	
	/**
	 * The method to add a file to the submission being viewed.
	 * This checks the type of file being uploaded (note, primary, supplement)
	 * and calls the appropriate private method.
	 * 
	 * @param subId (The submission id)
	 */
	@Security(RoleType.REVIEWER)
	private static void addFile(Submission sub){
		
		String uploadType = params.get("uploadType");		
		
		if("primary".equals(uploadType)) {
			uploadPrimary(sub);
		}else if("additional".equals(uploadType)){
			uploadAdditional(sub);
		}
		
		VireoEmail email = null;
		if(params.get("email_student") != null) {			
						
			String subject = params.get("subject");
			String comment = params.get("comment");
			
			if(subject==null || subject.isEmpty())
				validation.addError("addFileSubject", "You must include a subject when sending an email.");
			
			if(comment==null || comment.isEmpty())
				validation.addError("addFileComment", "You must include a comment when sending an email.");
			
			if(!validation.hasErrors()){
				email = emailService.createEmail();
				email.addParameters(sub);
				email.addTo(sub.getSubmitter());
				email.setFrom(context.getPerson());
				email.setReplyTo(context.getPerson());
				
				//Create list of carbon copies
				if(params.get("cc_advisor") != null && sub.getCommitteeContactEmail() != null) {
					email.addCc(sub.getCommitteeContactEmail());
				}
				
				email.setSubject(subject);
				email.setMessage(comment);
				
				email.setLogOnCompletion(context.getPerson(), sub);
			}			
		}		

		if(!validation.hasErrors()) {
			if(params.get("needsCorrection") != null)
				sub.setState(stateManager.getState("NeedsCorrection"));
						
			sub.save();
		}	
		
		if (email != null)
			emailService.sendEmail(email,false);
	}
	
	/**
	 * The private method to edit a file.
	 * 
	 * @param sub (The current submission)
	 * 
	 */
	@Security(RoleType.REVIEWER)
	private static void editFile(Submission sub){
		Attachment attachment = sub.findAttachmentById(Long.valueOf(params.get("attachmentId")));
		
		String newName = params.get("fileName");
		String newType = params.get("attachmentType");
		
		if(!newName.isEmpty() && newName != null && !newName.equals(attachment.getName()))
			attachment.setName(newName);
		
		if(!newType.isEmpty() && newType != null && attachment.getType()!=AttachmentType.valueOf(newType)){
			if("PRIMARY".equals(newType) && sub.getPrimaryDocument() != null) {
				validation.addError("editFilePrimary", "There can only be one primary document.");
				return;
			} else {
				attachment.setType(AttachmentType.valueOf(newType));
			}
		}
		
		if(!validation.hasErrors()) {
			attachment.save();
			sub.save();
		}
			
	}
	
	/**
	 * The private method to add a "supplement" file.
	 *  
	 * @param sub (The current submission)
	 * @return (A String containing the name of the file)
	 */
	@Security(RoleType.REVIEWER)
	private static void uploadAdditional(Submission sub){
		
		File attachment = params.get("additionalAttachment",File.class);
		AttachmentType type = AttachmentType.valueOf(params.get("attachmentType"));
		
		if(attachment == null) {
			validation.addError("additionalDocument", "There was no document selected.");
			return;
		}
		if(type == null) {
			validation.addError("additionalDocument", "There was no attachment type selected.");
			return;
		}
		
		try{
			sub.addAttachment(attachment, type).save();
			sub.save();
		} catch (IOException e) {
			validation.addError("supplementDocument","Error uploading supplemental document.");
		} catch (IllegalArgumentException e) {
			validation.addError("supplementDocument","Error uploading supplemental document.");
		}
	}
	
	/**
	 * The private method to delete a file.
	 * 
	 * @param sub (The current submission)
	 */
	@Security(RoleType.REVIEWER)
	private static void deleteFile(Submission sub) {
		Attachment attachment = sub.findAttachmentById(Long.valueOf(params.get("attachmentId")));
		
		attachment.delete();
		sub.save();
	}
	
	/**
	 * The private method to add a "primary" file. If a primary file already exists
	 * it will replace the file.
	 *  
	 * @param sub (The current submission)
	 * @return (A String containing the name of the file)
	 */
	@Security(RoleType.REVIEWER)
	private static void uploadPrimary(Submission sub){
		
		File attachment = params.get("primaryAttachment",File.class);
		
		if(attachment != null){
			if(!attachment.getName().toLowerCase().endsWith(".pdf")) {
				validation.addError("primaryDocument", "Primary document must be a PDF file.");
				return;
			}
		} else {
			validation.addError("primaryDocument", "There was no document selected.");
			return;
		}
		
		try{
			Attachment currentAttachment = sub.getPrimaryDocument();
			if(currentAttachment != null) {
				currentAttachment.archive();
				currentAttachment.save();
			}
			sub.addAttachment(attachment, AttachmentType.PRIMARY).save();
			sub.save();
		} catch (IOException e) {
			validation.addError("primaryDocument","Error uploading primary document.");
		} catch (IllegalArgumentException e) {
			validation.addError("primaryDocument","Error uploading primary document.");
		}		
	}
	
	/**
	 * A method for displaying/downloading the submission's attachments
	 * 
	 * @param id (The attachment id)
	 * @param name (The name of the file)
	 */
	@Security(RoleType.REVIEWER)
	public static void viewFile(Long id, String name){
		
		Long subId = null;
		if(session.contains("submission")){
			subId = Long.valueOf(session.get("submission"));
		} else {
			FilterTab.list();
		}
		
		Submission sub = subRepo.findSubmission(subId);
		
		Attachment attachment = sub.findAttachmentById(id);
		response.setContentTypeIfNotSet(attachment.getMimeType());
		
		// Fix problem with no-cache headers and ie8
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control","public");
		
		try {
			renderBinary(new FileInputStream(attachment.getFile()), attachment.getName(), attachment.getFile().length(), true);
		} catch (FileNotFoundException ex) {
			error("File not found");
		}
		
	}
	
	/**
	 * A method to send the Advisor Approval Email
	 * 
	 * @param id (The submission id)
	 */
	@Security(RoleType.REVIEWER)
	public static void sendAdvisorEmail(Long id){
		Submission submission = subRepo.findSubmission(id);
		
		String advisorEmail = submission.getCommitteeContactEmail();
		EmailTemplate template = settingRepo.findEmailTemplateByName("SYSTEM Advisor Review Request");

		
		VireoEmail email = emailService.createEmail();
		// Clear out all the recipients so we don't send this to anyone other than the advisor.
		email.getTo().clear();
		email.getCc().clear();
		email.getBcc().clear();
		
		email.addTo(advisorEmail);
		email.setFrom(context.getPerson());
		email.setReplyTo(context.getPerson());
						
		//Setup Params
		email.setTemplate(template);
		email.addParameters(submission);
		email.addParameter("ADVISOR_URL", getAdvisorURL(submission));
		
		email.setLogOnCompletion(context.getPerson(), submission);
		email.setSuccessLogMessage("Resent advisor request sent to "+advisorEmail);
		email.setFailureLogMessage("Failed to resend advisor request to "+advisorEmail);
		
		
		emailService.sendEmail(email,false);
		
		view();
	}

	/**
	 * Internal method to parse the Month and Year from the input
	 * 
	 * @param graduation (The graduation provided by the user. ie: "May 2013")
	 * 
	 * @return A list containing two strings. [0] = month, [1] = year
	 */
	protected static List<String> parseGraduation(String graduation){

		List<String> gradDate = new ArrayList<String>();

		if(graduation == null || graduation.trim().length() == 0)
			throw new IllegalArgumentException("graduation is required.");

		graduation = graduation.trim();		

		String[] strings = graduation.split(" ");

		if(strings.length != 2 || strings[1].length() != 4)
			throw new IllegalArgumentException("The graduation date "+graduation+" is invalid. The format must be 'May 2013'.");

		for(int i = 0; i < strings.length; i++) {
			String item = strings[i];
			gradDate.add(item);
		}

		return gradDate;
	}

	/**
	 * Internal method to translate the name of a month into it's integer value.
	 * 
	 * @param monthName (The name of a month)
	 * 
	 * @return The integer value of the month, january=0, december=11.
	 */
	protected static int monthNameToInt(String monthName) {

		if (monthName == null || monthName.trim().length() == 0)
			throw new IllegalArgumentException("monthName is required.");

		monthName = monthName.toLowerCase();

		String[] months = new DateFormatSymbols().getMonths();

		for (int i = 0; i <months.length; i++) {
			if (monthName.equalsIgnoreCase(months[i]))
				return i;			
		}

		throw new IllegalArgumentException("The month '"+monthName+"' is invalid, month names should be spelled out completely such as 'January', 'Feburary', etc...");
	}
	
	/**
	 * Retrieve the url where advisors may approve the submission.
	 * 
	 * @param sub
	 *            The submission.
	 * @return the url
	 */
	protected static String getAdvisorURL(Submission sub) {
		
		if (sub.getCommitteeEmailHash() == null)
			return null;
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("token", sub.getCommitteeEmailHash());
		
		ActionDefinition advisorAction = Router.reverse("Advisor.review",routeArgs);
		advisorAction.absolute();
		
		
		return advisorAction.url;
	}
}
