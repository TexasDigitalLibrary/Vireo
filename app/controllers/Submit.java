package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.Header;

import java.util.*;
import java.util.Map.Entry;

import org.tdl.vireo.model.RoleType;
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
		render("Submit/VerifyPersonalInformation.html");
	}
	
	
	// Process the verifyPersonalInformation form
	
	@Security(RoleType.STUDENT)
	public static void doVerifyPersonalInformation() {
		dumpParams();
		render("Submit/License.html");
	}

	@Security(RoleType.STUDENT)
	public static void license() {

		dumpParams();
		render("Submit/License.html");
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
	public static void confirmAndSubmit() {
		render("Submit/ConfirmAndSubmit.html");
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