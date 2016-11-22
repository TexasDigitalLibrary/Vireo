package org.tdl.vireo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.User;

@Service
public class TemplateUtility {
	
	
	private static final String FULL_NAME = "{{FULL_NAME}}";
	private static final String FIRST_NAME = "{{FIRST_NAME}}";
	private static final String LAST_NAME = "{{LAST_NAME}}";
	private static final String DOCUMENT_TITLE = "{{DOCUMENT_TITLE}}";
	private static final String SUBMISSION_TYPE = "{{SUBMISSION_TYPE}}";
	private static final String DEPOSIT_URI = "{{DEPOSIT_URI}}";
	private static final String GRAD_SEMESTER = "{{GRAD_SEMESTER}}";
	private static final String STUDENT_URL = "{{STUDENT_URL}}";
	private static final String SUBMISSION_URL = "{{SUBMISSION_URL}}";
	private static final String ADVISOR_URL = "{{ADVISOR_URL}}";
	private static final String SUBMISSION_STATUS = "{{SUBMISSION_STATUS}}";
	private static final String SUBMISSION_ASSIGNED_TO = "{{SUBMISSION_ASSIGNED_TO}}";
	private static final String REGISTRATION_URL = "{{REGISTRATION_URL}}";
	
    public String templateParameters(String content, Map<String, String> parameters) {
        for (String name : parameters.keySet()) {
            content = content.replaceAll("\\{"+name+"\\}", parameters.get(name));
        }
        return content;
    }
    
    public String templateParameters(String content, String[][] parameters) {
        for(String[] parameter : parameters) {
            content = content.replaceAll("\\{"+parameter[0]+"\\}", parameter[1]);
        }
        return content;
    }
    
    public String compileTemplate(EmailTemplate emailTemplate, Submission submission)  {
    	
    	User submitter = submission.getSubmitter();
    	Organization organization = submission.getOrganization();
    	
    	List<String> templateVariables = new ArrayList<String>();
    	
    	Matcher m = Pattern.compile("\\{\\{.*?}}").matcher(emailTemplate.getMessage());
		while (m.find()) {
			templateVariables.add(m.group());
		}
		
		for(String variable : templateVariables) {
			System.out.println(variable);
			
			switch(variable) {
			
				case FULL_NAME: {
					
					break;
				}
				case FIRST_NAME: {
					break;
				}
				case LAST_NAME: {
					break;
				}
				case DOCUMENT_TITLE: {
					break;
				}
				case SUBMISSION_TYPE: {
					break;
				}
				case DEPOSIT_URI: {
					break;
				}
				case GRAD_SEMESTER: {
					break;
				}
				case STUDENT_URL: {
					break;
				}
				case SUBMISSION_URL: {
					break;
				}
				case ADVISOR_URL: {
					break;
				}
				case SUBMISSION_STATUS: {
					break;
				}
				case SUBMISSION_ASSIGNED_TO: {
					break;
				}
				case REGISTRATION_URL: {
					break;
				}
				
				default: {}
			
			}
			
		}
    	
    	return null;
    }
    
    
}
