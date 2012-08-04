package controllers;

import play.Logger;
import play.mvc.Controller;

import org.tdl.vireo.deposit.DepositService;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.CustomActionDefinition;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DepositLocation;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.services.EmailService;
import org.tdl.vireo.services.EmailService.TemplateParameters;
import org.tdl.vireo.services.impl.EmailServiceImpl;
import org.tdl.vireo.state.State;

import com.google.gson.Gson;

import play.modules.spring.Spring;
import play.mvc.With;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * The controller for the view tab.
 * 
 * @author Micah Cooper
 *
 */

@With(Authentication.class)
public class ViewTab extends AbstractVireoController {

	public static DepositService depositService = Spring.getBeanOfType(DepositService.class);
	
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
		Person submitter = submission.getSubmitter();
	
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

		String nav = "view";
		render(	nav,
				submission,
				submitter,  
				gradMonth, 
				actionLogs, 
				settingRepo, 
				states,
				assignees, 
				transitions, 
				templates, 
				actions, 
				actionValues,
				depositLocations
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

		Submission submission = subRepo.findSubmission(subId);
		Person submitter = submission.getSubmitter();

		Object currentValue = null;
		String message = null;
		DegreeLevel degreeLevel = null;

		try{

			//First Name
			if("firstName".equals(field)) {
				if(value == null || value.trim().length() == 0)
					throw new RuntimeException("First Name is required.");

				submission.setStudentFirstName(value);
				currentValue = submission.getStudentFirstName();

				//Middle Name
			} else if("middleName".equals(field)) {
				submission.setStudentMiddleName(value);
				currentValue = submission.getStudentMiddleName();

				//Last Name
			} else if("lastName".equals(field)) {
				if(value==null || value.trim().length()==0)
					throw new RuntimeException("Last Name is required.");

				submission.setStudentLastName(value);				
				currentValue = submission.getStudentLastName();	

				//Email
			} else if("email".equals(field)) {
				if(value==null || value.trim().length()==0)
					throw new RuntimeException("Email is required.");

				try {
					new InternetAddress(value).validate();
				} catch (AddressException ae) {
					throw new RuntimeException("The Email provided is invalid.");
				}

				submitter.setEmail(value);
				currentValue = submitter.getEmail();

				//Year of Birth
			} else if("birthYear".equals(field)) {						
				if(value!=null && value.trim().length()>0) {
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
				if(value==null || value.trim().length()==0)
					throw new RuntimeException("Permanent Phone is required.");

				submitter.setPermanentPhoneNumber(value);
				currentValue = submitter.getPermanentPhoneNumber();

				//Permanent Email
			} else if("permEmail".equals(field)){
				if(value!=null && value.trim().length()>0) {
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
				if(value==null || value.trim().length()==0)
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
				if(value==null || value.trim().length()==0)
					throw new RuntimeException("Title is required.");

				submission.setDocumentTitle(value);
				currentValue = submission.getDocumentTitle();

				//Embargo
			} else if("embargo".equals(field)){			
				submission.setEmbargoType(settingRepo.findEmbargoType(Long.parseLong(value)));
				currentValue = submission.getEmbargoType().getName();

				//UMI Release
			} else if("umiRelease".equals(field)){			
				submission.setUMIRelease(Boolean.parseBoolean(value));
				currentValue = submission.getUMIRelease();

				//Document Type
			} else if("docType".equals(field)){
				submission.setDocumentType(value);
				currentValue = submission.getDocumentType();

				//Document Keywords
			} else if("keywords".equals(field)){			
				submission.setDocumentKeywords(value);
				currentValue = submission.getDocumentKeywords();

				//Document Abstract
			} else if("abstract".equals(field)){			
				submission.setDocumentAbstract(value);
				currentValue = submission.getDocumentAbstract();

				//College/School
			} else if("college".equals(field)){			
				submission.setCollege(value);
				currentValue = submission.getCollege();

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

				//Advisor Email
			} else if("advisorEmail".equals(field)){
				if(value==null || value.trim().length()==0)
					throw new RuntimeException("Advisor Email is required.");

				try {
					new InternetAddress(value).validate();
				} catch (AddressException ae) {
					throw new RuntimeException("The Advisor Email provided is invalid.");
				}

				submission.setCommitteeContactEmail(value);
				currentValue = submission.getCommitteeContactEmail();
			}
			submitter.save();
			submission.save();

		} catch(RuntimeException re) {
			if(value==null){
				value="";
			}
			value = escapeJavaScript(value);
			message = re.getMessage();
			Logger.info("JSON Failed");
			renderJSON("{ \"success\": false, \"value\": \""+value+"\", \"message\": \""+message+"\" }");
		}

		if(currentValue==null) {
			value = escapeJavaScript("");
		} else {
			value = escapeJavaScript(currentValue.toString());
		}

		String json;

		if(degreeLevel!=null){
			Logger.info("JSON Success: Degree");
			json = "{ \"success\": true, \"value\": \""+value+"\", \"degreeLevel\": \""+degreeLevel+"\" }";
		} else {
			Logger.info("JSON Success");
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
	 * @param chair (A boolean to flag if the committee member is a chair)
	 */
	@Security(RoleType.REVIEWER)
	public static void addCommitteeMemberJSON(Long subId, String firstName, String lastName, String middleName, Boolean chair){

		Submission submission = subRepo.findSubmission(subId);
		
		CommitteeMember newMember = null;
		
		try {

			if(firstName==null || firstName.trim().length()==0)
				throw new RuntimeException("Committee Member First Name is required.");

			if(lastName==null || lastName.trim().length()==0)
				throw new RuntimeException("Committee Member Last Name is required.");

			newMember = submission.addCommitteeMember(firstName, lastName, middleName, chair);

		} catch (RuntimeException re) {
			firstName = escapeJavaScript(firstName);
			lastName = escapeJavaScript(lastName);
			middleName = escapeJavaScript(middleName);
			
			renderJSON("{ \"success\": false, \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"middleName\": \""+middleName+"\", \"chair\": \""+chair+"\", \"message\": \""+re.getMessage()+"\" }");
		}

		submission.save();
		
		firstName = escapeJavaScript(firstName);
		lastName = escapeJavaScript(lastName);
		middleName = escapeJavaScript(middleName);
		Long id = newMember.getId();

		String json = "{ \"success\": true, \"id\": \""+id+"\", \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"middleName\": \""+middleName+"\", \"chair\": \""+chair+"\" }";

		renderJSON(json);

	}

	/**
	 * A method to update a committee members information.
	 * 
	 * @param id (The committee member id)
	 * @param firstName (The committee members first name)
	 * @param lastName (The committee members last name)
	 * @param middleName (The committee members middle name)
	 * @param chair (A boolean to flag if a committee member is a chair)
	 */
	
	@Security(RoleType.REVIEWER)
	public static void updateCommitteeMemberJSON(Long id, String firstName, String lastName, String middleName, Boolean chair){

		try {

			if(firstName==null || firstName.trim().length()==0)
				throw new RuntimeException("Committee Member First Name is required.");

			if(lastName==null || lastName.trim().length()==0)
				throw new RuntimeException("Committee Member Last Name is required.");

			CommitteeMember committeeMember = subRepo.findCommitteeMember(id);

			committeeMember.setFirstName(firstName);
			committeeMember.setLastName(lastName);
			committeeMember.setMiddleName(middleName);
			committeeMember.setCommitteeChair(chair);

			committeeMember.save();

		} catch (RuntimeException re) {
			firstName = escapeJavaScript(firstName);
			lastName = escapeJavaScript(lastName);
			middleName = escapeJavaScript(middleName);

			renderJSON("{ \"success\": false, \"id\": \""+id+"\", \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"middleName\": \""+middleName+"\", \"chair\": \""+chair+"\", \"message\": \""+re.getMessage()+"\" }");
		}

		firstName = escapeJavaScript(firstName);
		lastName = escapeJavaScript(lastName);
		middleName = escapeJavaScript(middleName);

		String json = "{ \"success\": true, \"id\": \""+id+"\", \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"middleName\": \""+middleName+"\", \"chair\": \""+chair+"\" }";

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

		renderTemplate("ViewTab/actionLogTable.include", actionLogs);
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
	 * A method to add an Action Log Comment and send an email if requested.
	 * 
	 * @param id (The submission id)
	 */
	
	@Security(RoleType.REVIEWER)
	public static void addActionLogComment(Long id){
		
		Submission submission = subRepo.findSubmission(id);
		EmailService emailService = Spring.getBeanOfType(EmailServiceImpl.class);
		
		String subject = params.get("subject");
		String comment = params.get("comment");
		
		if(params.get("status_change") != null)
			submission.setState(stateManager.getState("NeedsCorrection"));
		
		ActionLog actionLog = submission.logAction(comment);
		
		if("private".equals(params.get("visibility")))
			actionLog.setPrivate(true);
		
		if(params.get("email_student") != null && "public".equals(params.get("visibility"))) {			
			
			//Setup Params
			TemplateParameters emailParams = new TemplateParameters(submission);
			
			//Create list of recipients
			List<String> recipients = new ArrayList<String>();
			recipients.add(submission.getSubmitter().getCurrentEmailAddress());
			
			//Create list of carbon copies
			List<String> carbonCopies = new ArrayList<String>();
			if(params.get("cc_advisor") != null) {
				carbonCopies.add(submission.getCommitteeContactEmail());
			}
			
			String replyTo = context.getPerson().getCurrentEmailAddress();
			
			emailService.sendEmail(subject, comment, emailParams, recipients, replyTo, carbonCopies);
		}
		
		submission.save();
		actionLog.save();
		
		view();
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
}
