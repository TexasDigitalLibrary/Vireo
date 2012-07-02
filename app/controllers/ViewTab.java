package controllers;

import play.mvc.Controller;

import org.apache.commons.lang.StringEscapeUtils;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.DegreeLevel;
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
		
		List<EmbargoType> embargos = settingRepo.findAllEmbargoTypes();
		
		String nav = "view";
		render(nav, submission, submitter, degreeLevel, gradMonth, actionLogs, embargos);
	}
	
	@Security(RoleType.REVIEWER)
	public static void updateJSON(Long subId, String field, String value){
		
		Submission submission = subRepo.findSubmission(subId);
		Person submitter = submission.getSubmitter();
		
		String status = "true";
		Object currentValue = null;
		String message = null;
		Boolean failed = false;
		
		
		//First Name
		if("firstName".equals(field)){
			if(value == null || value.trim().length() == 0) {
				status = "false";
				message = "First name is required.";
			} else {
				if(submission.getStudentFirstName()==null || !submission.getStudentFirstName().equals(value)) {
					submission.setStudentFirstName(value);
				}
			}
			currentValue = submission.getStudentFirstName();
		
		//Middle Name
		} else if("middleName".equals(field)){
			if(submission.getStudentMiddleName()==null || !submission.getStudentMiddleName().equals(value))
				submission.setStudentMiddleName(value);
			currentValue = submission.getStudentMiddleName();
		
		//Last Name
		} else if("lastName".equals(field)){
			if(value==null || value.trim().length()==0){
				status = "false";
				message = "Last name is required.";
			} else {
				if(submission.getStudentLastName()==null || !submission.getStudentLastName().equals(value)) {
					submission.setStudentLastName(value);
				}
			}
			currentValue = submission.getStudentLastName();	
		
		//Email
		} else if("email".equals(field)){
			if(value==null || value.trim().length()==0){
				status = "false";
				message = "Email is required.";
			} else {
				try {
					new InternetAddress(value).validate();
				} catch (AddressException ae) {
					status = "false";
					message = "The email address you provided is invalid.";
					failed = true;
				}
				if((submitter.getEmail()==null || !submitter.getEmail().equals(value)) && !failed)
					submitter.setEmail(value);
			}
			currentValue = submitter.getEmail();
		
		//Year of Birth
		} else if("birthYear".equals(field)){
			Integer birthYearInt = null;
			if(value!=null && value.trim().length()>0) {
				try{
					birthYearInt = Integer.valueOf(value);
				} catch (NumberFormatException nfe) {
					status = "false";
					message = "Your birth year is invalid.";
					failed = true;
				}
				if(!failed) {
					if (birthYearInt < 1900 || birthYearInt > Calendar.getInstance().get(Calendar.YEAR)) {
						status = "false";
						message = "Your birth year is invalid, please use a four digit year between 1900 and "+Calendar.getInstance().get(Calendar.YEAR)+".";
						failed = true;
					}
					if(!failed) {
						if(submission.getStudentBirthYear()==null || !submission.getStudentBirthYear().equals(Integer.valueOf(value))) {
							submission.setStudentBirthYear(Integer.valueOf(value));
						}
					}
				}
			} else {
				submission.setStudentBirthYear(null);
			}
			currentValue = submission.getStudentBirthYear();
		
		//Permanent Phone
		} else if("permPhone".equals(field)){
			if(value==null || value.trim().length()==0){
				status = "false";
				message = "Permanent Phone is required.";
			} else {
				if(submitter.getPermanentPhoneNumber()==null || !submitter.getPermanentPhoneNumber().equals(value))
					submitter.setPermanentPhoneNumber(value);
			}
			currentValue = submitter.getPermanentPhoneNumber();
		
		//Permanent Email
		} else if("permEmail".equals(field)){
			if(value!=null && value.trim().length()>0){
				try {
					new InternetAddress(value).validate();
				} catch (AddressException ae) {
					status = "false";
					message = "The email address you provided is invalid.";
					failed = true;
				}
				if((submitter.getPermanentEmailAddress()==null || !submitter.getPermanentEmailAddress().equals(value)) && !failed)
					submitter.setPermanentEmailAddress(value);
			}
			currentValue = submitter.getPermanentEmailAddress();
		
		//Permanent Address
		} else if("permAddress".equals(field)){
			if(value==null || value.trim().length()==0){
				status = "false";
				message = "Permanent Address is required.";
			} else {
				if(submitter.getPermanentPostalAddress()==null || !submitter.getPermanentPostalAddress().equals(value))
					submitter.setPermanentPostalAddress(value);
			}
			currentValue = submitter.getPermanentPostalAddress();
			
		//Current Phone
		} else if("currentPhone".equals(field)){
			if(submitter.getCurrentPhoneNumber()==null || !submitter.getCurrentPhoneNumber().equals(value))
				submitter.setCurrentPhoneNumber(value);
			currentValue = submitter.getCurrentPhoneNumber();
		
		//Current Address
		} else if("currentAddress".equals(field)){
			if(submitter.getCurrentPostalAddress()==null || !submitter.getCurrentPostalAddress().equals(value))
				submitter.setCurrentPostalAddress(value);
			currentValue = submitter.getCurrentPostalAddress();
			
		//Title
		} else if("title".equals(field)){
			if(value==null || value.trim().length()==0){
				status = "false";
				message = "Title is required.";
			} else {
				if(submission.getDocumentTitle()==null || !submission.getDocumentTitle().equals(value))
					submission.setDocumentTitle(value);
			}
			currentValue = submission.getDocumentTitle();
		
		//Embargo
		} else if("embargo".equals(field)){
			if(submission.getEmbargoType()==null || !submission.getEmbargoType().equals(settingRepo.findEmbargoType(Long.parseLong(value))))
				submission.setEmbargoType(settingRepo.findEmbargoType(Long.parseLong(value)));
			currentValue = submission.getEmbargoType();
			
		//UMI Release
		} else if("umiRelease".equals(field)){
			if(submission.getUMIRelease()==null || !submission.getUMIRelease().equals(Boolean.parseBoolean(value)))
				submission.setUMIRelease(Boolean.parseBoolean(value));
		
		//Document Type
		} else if("docType".equals(field)){
			if(submission.getDocumentType()==null || !submission.getDocumentType().equals(value))
				submission.setDocumentType(value);
		
		//Document Keywords
		} else if("keywords".equals(field)){
			if(submission.getDocumentKeywords()==null || !submission.getDocumentKeywords().equals(value))
				submission.setDocumentKeywords(value);
		
		//Document Abstract
		} else if("abstract".equals(field)){
			if(submission.getDocumentAbstract()==null || !submission.getDocumentAbstract().equals(value))
				submission.setDocumentAbstract(value);
		
		//College
		} else if("school".equals(field)){
			if(submission.getCollege()==null || !submission.getCollege().equals(value))
				submission.setCollege(value);
		
		//Department
		} else if("department".equals(field)){
			if(submission.getDepartment()==null || !submission.getDepartment().equals(value))
				submission.setDepartment(value);
		
		//Degree
		} else if("degree".equals(field)){
			if(submission.getDegree()==null || !submission.getDegree().equals(value))
				submission.setDegree(value);
		
		//Major
		} else if("major".equals(field)){
			if(submission.getMajor()==null || !submission.getMajor().equals(value))
				submission.setMajor(value);
		
		//Graduation Month
		} else if("gradMonth".equals(field)){
			if(submission.getGraduationMonth()==null || !submission.getGraduationMonth().equals(Integer.parseInt(value)))
				submission.setGraduationMonth(Integer.parseInt(value));
		
		//Graduation Year
		} else if("gradYear".equals(field)){
			if(submission.getGraduationYear()==null || !submission.getGraduationYear().equals(Integer.parseInt(value)))
				submission.setGraduationYear(Integer.parseInt(value));
		
		//Advisor Email
		} else if("advisorEmail".equals(field)){
			if(submission.getCommitteeContactEmail()==null || !submission.getCommitteeContactEmail().equals(value))
				submission.setCommitteeContactEmail(value);
		}
		submitter.save();
		submission.save();
		
		if(currentValue==null) {
			currentValue="";
		} else {
			currentValue = StringEscapeUtils.escapeJava(currentValue.toString());
		}
		
		String json = "{ \"success\": "+status+", \"value\": \""+currentValue+"\", \"message\": \""+message+"\" }";
		
		renderJSON(json);
		
	}

}
