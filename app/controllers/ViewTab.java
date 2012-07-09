package controllers;

import play.Logger;
import play.mvc.Controller;

import org.apache.commons.lang.StringEscapeUtils;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.DegreeLevel;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.DocumentType;
import org.tdl.vireo.model.EmbargoType;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import play.modules.spring.Spring;
import play.mvc.With;

import java.text.DateFormatSymbols;
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
		
		DegreeLevel degreeLevel = settingRepo.findDegreeByName(submission.getDegree()).getLevel();
		
		String gradMonth = new DateFormatSymbols().getMonths()[submission.getGraduationMonth()];
		
		List<ActionLog> actionLogs	= subRepo.findActionLog(submission);		
				
		String nav = "view";
		render(nav, submission, submitter, degreeLevel, gradMonth, actionLogs, settingRepo);
	}
	
	@Security(RoleType.REVIEWER)
	public static void updateJSON(Long subId, String field, String value){
		Logger.info("Started updating an item.");
		
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
		
		//Major
		} else if("major".equals(field)){			
			submission.setMajor(value);
			currentValue = submission.getMajor();
				
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
			message = re.getMessage();
			renderJSON("{ \"success\": false, \"value\": \""+value+"\", \"message\": \""+message+"\" }");
		}
		
		if(currentValue==null) {
			currentValue="";
		} else {
			currentValue = StringEscapeUtils.escapeJava(currentValue.toString());
		}
		
		String json;
		
		if(degreeLevel!=null){
			json = "{ \"success\": true, \"value\": \""+currentValue+"\", \"degreeLevel\": \""+degreeLevel+"\" }";
		} else {
			json = "{ \"success\": true, \"value\": \""+currentValue+"\" }";
		}
		
		renderJSON(json);
		
	}
	
	@Security(RoleType.REVIEWER)
	public static void updateGraduationDateJSON(Long subId, String month, String year){
		Logger.info("Started updating graduation semester.");
		
		Submission submission = subRepo.findSubmission(subId);
		
		//Graduation Month						
		submission.setGraduationMonth(Integer.parseInt(month));
				
		//Graduation Year						
		submission.setGraduationYear(Integer.parseInt(year));
		
		submission.save();
		
		String gradMonth = new DateFormatSymbols().getMonths()[submission.getGraduationMonth()];
		String currentValue = StringEscapeUtils.escapeJava(gradMonth+" "+submission.getGraduationYear().toString());
				
		String json = "{ \"success\": true, \"value\": \""+currentValue+"\" }";
		
		renderJSON(json);
		
	}
	
	@Security(RoleType.REVIEWER)
	public static void addCommitteeMemberJSON(Long subId, String firstName, String lastName, String middleName, Boolean chair){
		
		Logger.info("Started adding committee member.");
		
		Submission submission = subRepo.findSubmission(subId);
		
		try {
			
			if(firstName==null || firstName.trim().length()==0)
				throw new RuntimeException("Committee Member First Name is required.");
			
			if(lastName==null || lastName.trim().length()==0)
				throw new RuntimeException("Committee Member Last Name is required.");
						
			submission.addCommitteeMember(firstName, lastName, middleName, chair);
			
		} catch (RuntimeException re) {
			renderJSON("{ \"success\": false, \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"chair\": \""+chair+"\", \"message\": \""+re.getMessage()+"\" }");
		}
		
		String json = "{ \"success\": true, \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"chair\": \""+chair+"\" }";
		
		renderJSON(json);
		
	}
	
	@Security(RoleType.REVIEWER)
	public static void updateCommitteeMemberJSON(Long id, String firstName, String lastName, String middleName, Boolean chair){
		
		Logger.info("Started updating committee member.");
		Logger.info(id+" "+firstName+" "+middleName+" "+lastName+" "+chair);
		
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
			renderJSON("{ \"success\": false, \"id\": \""+id+"\", \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"middleName\": \""+middleName+"\", \"chair\": \""+chair+"\", \"message\": \""+re.getMessage()+"\" }");
		}
		
		String json = "{ \"success\": true, \"id\": \""+id+"\", \"firstName\": \""+firstName+"\", \"lastName\": \""+lastName+"\", \"middleName\": \""+middleName+"\", \"chair\": \""+chair+"\" }";
		
		renderJSON(json);
		
	}
	
	@Security(RoleType.REVIEWER)
	public static void removeCommitteeMemberJSON(Long subId, Long id){
		
		Logger.info("Started removing committee member.");
		
		subRepo.findCommitteeMember(id).delete();
		
		renderJSON("{ \"success\": true }");
		
	}
	
	@Security(RoleType.REVIEWER)
	public static void refreshActionLogTable(Long id){
		
		Submission submission = subRepo.findSubmission(id);
		
		List<ActionLog> actionLogs	= subRepo.findActionLog(submission);
		
		renderTemplate("ViewTab/actionLogTable.include", actionLogs);
	}

}
