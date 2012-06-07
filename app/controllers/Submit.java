package controllers;

import play.*;
import play.modules.spring.Spring;
import play.mvc.*;
import play.mvc.Http.Header;

import java.util.*;
import java.util.Map.Entry;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;

import org.tdl.vireo.model.SubmissionRepository;


import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.jpa.JpaSubmissionRepositoryImpl;
import org.tdl.vireo.security.impl.SecurityContextImpl;

import com.google.gson.Gson;
/**
 * Submit controller
 * This controller manages the student submission forms for Vireo 
 * 
 * @author Dan Galewsky</a>
 */

@With(Authentication.class)
public class Submit extends Controller {

	// Return the VerifyPersonalInformation form
	
	@Security(RoleType.STUDENT)
	public static void verifyPersonalInformation() {
		
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
		Person currentPerson = context.getPerson();
			
		render(currentPerson);
	}
	
	
	// Process the verifyPersonalInformation form
	
	@Security(RoleType.STUDENT)
	public static void doVerifyPersonalInformation(String middleName,
			String yearOfBirth, 
			String department,
			String degree,
			String major,
			String permPhone,
			String permAddress,
			String permEmail,
			String currentPhone,
			String currentAddress
			) {
		
		dumpParams();
		
		// Get currently logged in person 
		
		SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
		Person currentPerson = context.getPerson();
		
		// Create an empty submission object
		
		SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
		Submission sub = subRepo.createSubmission(currentPerson);

		sub.setStudentFirstName(currentPerson.getFirstName());
		sub.setStudentLastName(currentPerson.getLastName());
		sub.setDepartment(department);
		sub.setDegree(degree);
		sub.setMajor(major);
		sub.setStudentMiddleName(middleName);
		
		sub.save();
		long subId = sub.getId();
		
		Logger.info("Submisson ID: " + String.valueOf(subId));
		
		Gson gson = new Gson();
		Logger.info("Submission" + gson.toJson(sub));
		
		
		Map<String,String> templateArgs = new HashMap<String,String>();
		templateArgs.put("submissionId", String.valueOf(subId));

		 
		render("Submit/License.html", templateArgs);
	}

	@Security(RoleType.STUDENT)
	public static void license() {

		dumpParams();
		render("Submit/License.html");
	}

	@Security(RoleType.STUDENT)
	public static void doLicense(String submissionId, String licenseAgreement) {

		dumpParams();
		
		Map<String,String> templateArgs = new HashMap<String,String>();
		templateArgs.put("submissionId", String.valueOf(submissionId));
		
		render("Submit/DocInfo.html");
	}
	

	@Security(RoleType.STUDENT)
	public static void docInfo() {
		render("Submit/DocInfo.html");
	}

	@Security(RoleType.STUDENT)
	public static void fileUpload() {
		render("Submit/FileUpload.html");
	}

	@Security(RoleType.STUDENT)
	public static void confirmAndSubmit(Long id) {
		SecurityContextImpl context = Spring
				.getBeanOfType(SecurityContextImpl.class);
		JpaSubmissionRepositoryImpl submissions = Spring
				.getBeanOfType(JpaSubmissionRepositoryImpl.class);
		
		if(id!=null){
			Submission submission = submissions.findSubmission(id);
			render(context, submission);
		} else {
			render(context);
		}
		
	}

	@Security(RoleType.STUDENT)
	public static void review() {
		render("Submit/Review.html");
	}

	public static void dump() {
		render("Submit/VerifyPersonalInformation.html");
	}



	private static void dumpParams() {

		Map<String, String> names = params.allSimple();

		Logger.info("Session: " + session.toString());

		Logger.info("Params:");
		
		for (Map.Entry<String, String> entry : names.entrySet())        {
			Logger.info(entry.getKey() + "= {" + entry.getValue() + "}");
		}
	}

}