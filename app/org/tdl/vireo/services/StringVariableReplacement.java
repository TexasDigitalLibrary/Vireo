package org.tdl.vireo.services;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Map;

import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Submission;

import play.mvc.Router;
import play.mvc.Router.ActionDefinition;

/**
 * This service allows for the manipulation of strings--both setting parameters and handling 
 * place holder replacements.
 *  
 * @author Micah Cooper
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 */
public class StringVariableReplacement {
	
	/**
	 * This generates a map of the strings to be replaced with a specific submissions meta data.
	 * 
	 * 
	 * @param sub
	 * 		The Submission object.
	 * 
	 * @return
	 * 		A key value Map of the Source String and the Replacement String.
	 * 
	 */
	public static Map<String, String> setParameters(Submission sub) {
		
		Map<String, String> parameters = new HashMap<String, String>();
		
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
		
		
		// URL for the student to view their submission(s)
		ActionDefinition studentAction = Router.reverse("Student.submissionList");
		studentAction.absolute();
		parameters.put("STUDENT_URL", studentAction.url);
		
		// URL for the submission directly
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("subId", String.valueOf(sub.getId()));
		ActionDefinition submissionAction = Router.reverse("Student.submissionView", params);
		submissionAction.absolute();
		parameters.put("SUBMISSION_URL", submissionAction.url);
		
		// Advisor url for reviews
		if (sub.getCommitteeEmailHash() != null) {
			Map<String,Object> routeArgs = new HashMap<String,Object>();
			routeArgs.put("token", sub.getCommitteeEmailHash());
			
			ActionDefinition advisorAction = Router.reverse("Advisor.review",routeArgs);
			advisorAction.absolute();
			
			parameters.put("ADVISOR_URL", advisorAction.url);
		}
		
		parameters.put("SEPARATOR", File.separator);
		
		return parameters;
		
	}
	
	
	/**
	 * This replaces placeholders within a string with the corresponding value in the parameters, if present. retuns null if not present.
	 * 
	 * @param string
	 * 		A string containing values to be replaced.
	 * @param parameters
	 * 		A map of the strings to be replaced with their corresponding value 
	 * @return
	 * 		The new string, else null
	 */
	public static String applyParameterSubstitution(String string, Map<String, String> parameters) {
		
		if(string == null)
			return null;
		
		for (String name : parameters.keySet()) {
			String value = parameters.get(name);
			
			string = string.replaceAll("\\{"+name+"\\}", value);
		}
	
		return string;
		
	}
	
}
