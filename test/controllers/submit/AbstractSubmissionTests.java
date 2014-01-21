package controllers.submit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.constant.FieldConfig;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.College;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.Department;
import org.tdl.vireo.model.Major;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;
import org.tdl.vireo.state.StateManager;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.libs.Mail;
import play.modules.spring.Spring;
import play.mvc.Http.Response;
import play.mvc.Router;
import controllers.AbstractVireoFunctionalTest;

import static org.tdl.vireo.constant.AppConfig.*;


/**
 * This abstract submission tests provides a set of very useful utility
 * functions to make writing submission tests much easier. It's hard to test
 * each screen of the submission process individual so this allows individual
 * test cases to only focus on the particular area or test they are interested
 * in. Leaving the other steps to be handled by the methods in this abstract
 * class.
 * 
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author Micah Cooper
 * 
 */
public abstract class AbstractSubmissionTests extends AbstractVireoFunctionalTest {

	// Spring dependencies
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	public static StateManager stateManager = Spring.getBeanOfType(StateManager.class);

	// A regular expression to extract the subid from the url.
	public static Pattern SUBMISSION_ID_PATTERN = Pattern.compile("action=\"/submit/([0-9]+)/.*\"");

	// The submission id if the currently being worked on submission.
	public Long subId = null;

	// The original configuration, we will restore to these after the test.
	public Map<String,String> originalSettings = new HashMap<String,String>();

	/**
	 * Setup
	 * 
	 * Grab the original configuration settings, and store their values.
	 */
	@Before
	public void setup() {
		
		for(FieldConfig field : FieldConfig.values()) {
			Configuration label = settingRepo.findConfigurationByName(field.LABEL);
			if (label == null) {
				originalSettings.put(field.LABEL, null);
			} else {
				originalSettings.put(field.LABEL,label.getValue());
			}
			
			Configuration help = settingRepo.findConfigurationByName(field.HELP);
			if (help == null) {
				originalSettings.put(field.HELP, null);
			} else {
				originalSettings.put(field.HELP,help.getValue());
			}
			
			Configuration enabled = settingRepo.findConfigurationByName(field.ENABLED);
			if (enabled == null) {
				originalSettings.put(field.ENABLED, null);
			} else {
				originalSettings.put(field.ENABLED,enabled.getValue());
			}
		}

		Configuration allowMultiple = settingRepo.findConfigurationByName(ALLOW_MULTIPLE_SUBMISSIONS);
		if (allowMultiple == null) {
			originalSettings.put(ALLOW_MULTIPLE_SUBMISSIONS, null);
		} else {
			originalSettings.put(ALLOW_MULTIPLE_SUBMISSIONS,allowMultiple.getValue());
		}
		
		Configuration submissionsOpen = settingRepo.findConfigurationByName(SUBMISSIONS_OPEN);
		if (submissionsOpen == null) {
			originalSettings.put(SUBMISSIONS_OPEN, null);
		} else {
			originalSettings.put(SUBMISSIONS_OPEN,submissionsOpen.getValue());
		}
		
		Configuration delayAdvisorEmail = settingRepo.findConfigurationByName(EMAIL_DELAY_SENDING_ADVISOR_REQUEST);
		if (delayAdvisorEmail == null) {
			originalSettings.put(EMAIL_DELAY_SENDING_ADVISOR_REQUEST, null);
		} else {
			originalSettings.put(EMAIL_DELAY_SENDING_ADVISOR_REQUEST,delayAdvisorEmail.getValue());
		}
		
		// Turn off authentication for the test thread
		context.turnOffAuthorization();
		
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}

	/**
	 * Cleanup
	 * 
	 * Restore the original configuration settings, and delete the submission.
	 */
	@After
	public void cleanup() {
		
		// Restore our configuration.
		for (String name : originalSettings.keySet()) {
			setConfiguration(name,originalSettings.get(name));
		}

		// if we created a submission, delete it.
		if (subId != null) {
			JPA.em().getTransaction().commit();
			JPA.em().clear();
			JPA.em().getTransaction().begin();
			subRepo.findSubmission(subId).delete();
		}
		
		context.restoreAuthorization();
	}

	/**
	 * Set whether multiple submissions are allowed.
	 * 
	 * @param value
	 *            Allowed or Disallowed
	 */
	public void setAllowMultipleSubmissions(boolean value) {
		setConfiguration(ALLOW_MULTIPLE_SUBMISSIONS, value ? "true" : null);
	}
	
	/**
	 * Set whether submissions are opened or closed.
	 * 
	 * @param value open or closed.
	 */
	public void setSubmissionsOpen(boolean value) {
		setConfiguration(SUBMISSIONS_OPEN, value ? "true" : null);
	}
	
	/**
	 * Set whether to send the advisor email
	 * 
	 * @param value open or closed.
	 */
	public void setDelayAdvisorEmail(boolean value) {
		setConfiguration(EMAIL_DELAY_SENDING_ADVISOR_REQUEST, value ? "true" : null);
	}
	
	/**
	 * Disable all the provided fields.
	 * @param fields The fields to disable.
	 */
	public void disableFields(FieldConfig ... fields) {
		for (FieldConfig field : fields)
			setConfiguration(field.ENABLED,"disable");
	}
	
	/**
	 * Enable all the provide fields.
	 * @param fields The fields to enable.
	 */
	public void enableFields(FieldConfig ... fields) {
		for (FieldConfig field : fields)
			setConfiguration(field.ENABLED,"optional");
	}
	
	
	/**
	 * Require all the provided fields.
	 * @param fields The fields to require.
	 */
	public void requireFields(FieldConfig ... fields) {
		for (FieldConfig field : fields)
			setConfiguration(field.ENABLED,"required");
	}
	

	/**
	 * Generic method to set the configuration of a particular parameter.
	 * 
	 * @param name
	 *            The configuration name
	 * @param value
	 *            The value.
	 */
	public void setConfiguration(String name, String value) {

		if (value == null) {
			Configuration config = settingRepo.findConfigurationByName(name);
			if (config != null)
				config.delete();
		} else {
			Configuration config = settingRepo.findConfigurationByName(name);
			if (config == null)
				config = settingRepo.createConfiguration(name, value);
			else
				config.setValue(value);
			config.save();
		}
	}
	
	
	
	
	/**
	 * Handle the personal information step. If any value is null, then it will
	 * not be submitted with the form data. All non-null inputs will be verified
	 * that they were saved to the submission.
	 * 
	 * @param firstName
	 *            The student's first name.
	 * @param middleName
	 *            The student's middle name.
	 * @param lastName
	 *            The students's last name.
	 * @param birthYear
	 *            The student's birth year.
	 * @param program
	 * @param college
	 * @param department
	 * @param degree
	 * @param major
	 * @param permPhone
	 * @param permAddress
	 * @param permEmail
	 * @param currentPhone
	 * @param currentAddress
	 */
	public void personalInfo(String firstName, String middleName, String lastName, String birthYear, String program, String college, String department, String degree, String major, String permPhone, 
			String permAddress, String permEmail, String currentPhone, String currentAddress) {


		String PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo").url;
		
		// Get the form without posting any data first
		Response response = GET(PERSONAL_INFO_URL);
		assertIsOk(response);
		assertContentMatch("Verify Your Information",response);
		
		// Extract the submission id
		Matcher tokenMatcher = SUBMISSION_ID_PATTERN.matcher(getContent(response));
		assertTrue(tokenMatcher.find());
		String subIdString = tokenMatcher.group(1);
		assertNotNull(subIdString);
		Long foundSubId = Long.valueOf(subIdString);
		assertNotNull(foundSubId);
		assertTrue(foundSubId > 0);		
		
		// If this is the first pass, save the subId.
		if (subId == null)
			subId = foundSubId;
		else
			assertEquals(subId,foundSubId);
				
				
		// If we already have a subId assigned then get the url scoped to that submission.
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",subId);
		PERSONAL_INFO_URL = Router.reverse("submit.PersonalInfo.personalInfo",routeArgs).url;


		// Add valid values
		Map<String, String> params= new HashMap<String, String>();
		params.put("step","personalInfo");
		if (firstName != null)
			params.put("firstName", firstName);
		if (middleName != null)
			params.put("middleName", middleName);
		if (lastName != null)
			params.put("lastName", lastName);
		if (birthYear != null)
			params.put("birthYear", birthYear);

		if (program != null)
			params.put("program", program);
		if (college != null)
			params.put("college", college);
		if (department != null)
			params.put("department", department);
		if (degree != null)
			params.put("degree", degree);
		if (major != null)
			params.put("major", major);
		if (permPhone != null)
			params.put("permPhone", permPhone);
		if (permAddress != null)
			params.put("permAddress", permAddress);
		if (permEmail != null)
			params.put("permEmail", permEmail);
		if (currentPhone != null)
			params.put("currentPhone", currentPhone);
		if (currentAddress != null)
			params.put("currentAddress", currentAddress);

		params.put("submit_next", "Save and Continue");

		// Submit the page
		response = POST(PERSONAL_INFO_URL, params);


		// Verify the Submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);
		Person submitter = sub.getSubmitter();

		// Verify all the data
		if (firstName == null) 
			// It should be the default value
			assertEquals(submitter.getFirstName(), sub.getStudentFirstName());
		else
			// Or it should be the value we supplied.
			assertEquals(firstName,sub.getStudentFirstName());

		if (middleName == null)
			assertEquals(submitter.getMiddleName(), sub.getStudentMiddleName());
		else
			assertEquals(middleName,sub.getStudentMiddleName());

		if (lastName == null)
			assertEquals(submitter.getLastName(), sub.getStudentLastName());
		else
			assertEquals(lastName,sub.getStudentLastName());

		if (birthYear == null)
			assertEquals(submitter.getBirthYear(), sub.getStudentBirthYear());
		else
			assertEquals(Integer.valueOf(birthYear),sub.getStudentBirthYear());


		// Academic Affiliation
		if (program == null)
			assertEquals(submitter.getCurrentProgram(), sub.getProgram());
		else
			assertEquals(program,sub.getProgram());
		
		if (college == null)
			assertEquals(submitter.getCurrentCollege(), sub.getCollege());
		else
			assertEquals(college,sub.getCollege());

		if (department == null)
			assertEquals(submitter.getCurrentDepartment(),sub.getDepartment());
		else
			assertEquals(department,sub.getDepartment());

		if (major == null)
			assertEquals(submitter.getCurrentMajor(),sub.getMajor());
		else
			assertEquals(major, sub.getMajor());

		// Contact Information
		if (permPhone != null)
			assertEquals(permPhone,submitter.getPermanentPhoneNumber());
		if (permAddress != null)
			assertEquals(permAddress,submitter.getPermanentPostalAddress());
		if (permEmail != null)
			assertEquals(permEmail,submitter.getPermanentEmailAddress());
		if (currentPhone != null)
			assertEquals(currentPhone, submitter.getCurrentPhoneNumber());
		if (currentAddress != null)
			assertEquals(currentAddress, submitter.getCurrentPostalAddress());
	}

	/**
	 * Perform the license agreement step. The license will be agreed to, and
	 * the submission verified that it's state was updated. The license
	 * agreement date, and that a license attachment was added.
	 */
	public void license(Boolean umi) {

		int numAttachments = umi ? 2 : 1;
		
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",subId);
		final String LICENSE_URL = Router.reverse("submit.License.license",routeArgs).url;

		// Build the form data
		Map<String, String> params= new HashMap<String, String>();
		params.put("step","license");
		params.put("submit_next", "Save and Continue");
		
		// Apply License Agreement
		params.put("licenseAgreement","checked");		

		// Apply UMI Agreement
		if(umi)
			params.put("proquestAgreement", "checked");		
		
		// Post the form
		Response response = POST(LICENSE_URL,params);
		assertNotNull(response.getHeader("Location"));

		// Verify the Submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);

		// Check the license agreement date
		assertNotNull(sub.getLicenseAgreementDate());

		// Check for the license attachments.		
		assertEquals(numAttachments,sub.getAttachments().size());
		assertEquals(AttachmentType.LICENSE,sub.getAttachments().get(0).getType());
		
		if(umi) {			
			assertEquals(AttachmentType.LICENSE,sub.getAttachments().get(1).getType());
			assertTrue(sub.getUMIRelease());
		} else {
			assertFalse(sub.getUMIRelease());
		}
	}
	
	/**
	 * A helper method for not passing whether or not umi is accepted.	 * 
	 */
	public void license(){
		license(false);
	}

	/**
	 * Perform the document information step. All values are optionally null, if
	 * so they will not be supplied to the form. Any non-null values will be
	 * verified that they were updated on the submission.
	 * 
	 * @param title
	 *            The document title.
	 * @param degreeMonth
	 *            Graduation Month
	 * @param degreeYear
	 *            Graduation Year
	 * @param docType
	 *            Document Type
	 * @param abstractText
	 *            Document Abstract
	 * @param keywords
	 *            Document Keywords
	 * @param subjectPrimary
	 * 			  The primary ProQuest Subject
	 * @param subjectSecondary
	 * 			  The Secondary ProQuest Subject
	 * @param subjectTertiary
	 * 			  The Tertiary ProQuest Subject
	 * @param language
	 * 			  The document language
	 * @param committee
	 *            List of maps of committee members.
	 * @param chairEmail
	 *            The committee contact email.
	 * @param embargo
	 *            The embargo settings.
	 * @param umi
	 *            UMI release flag.
	 */
	public void documentInfo(String title, String degreeMonth, String degreeYear, String defenseDate, String docType, String abstractText, String keywords,
			String subjectPrimary, String subjectSecondary, String subjectTertiary, String language, List<Map<String,String>> committee, String chairEmail, 
			String publishedMaterial, String embargo)  {

		// Get our URL
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",subId);
		final String DOCUMENT_INFO_URL = Router.reverse("submit.DocumentInfo.documentInfo",routeArgs).url;

		// Build the form data
		Map<String, String> params= new HashMap<String, String>();
		params.put("step","documentInfo");
		if (title != null)
			params.put("title",title);
		if (degreeMonth != null)
			params.put("degreeMonth",degreeMonth);
		if (degreeYear != null)
			params.put("degreeYear",degreeYear);
		if (defenseDate != null)
			params.put("defenseDate", defenseDate);
		if (docType != null)
			params.put("docType", docType);
		if (abstractText != null)
			params.put("abstractText",abstractText);
		if (keywords != null)
			params.put("keywords",keywords);
		if (subjectPrimary != null)
			params.put("subject-primary",subjectPrimary);
		if (subjectSecondary != null)
			params.put("subject-secondary",subjectSecondary);
		if (subjectTertiary != null)
			params.put("subject-tertiary",subjectTertiary);
		if (language != null)
			params.put("docLanguage", language);
		if (chairEmail != null)
			params.put("chairEmail", chairEmail);
		if (publishedMaterial != null) {
			params.put("publishedMaterialFlag", "true");
			params.put("publishedMaterial", publishedMaterial);
		}
		if (embargo != null)
			params.put("embargo", embargo);		
		for (int i = 0; i < committee.size(); i++) {
			Map<String,String> member = committee.get(i);

			params.put("committeeFirstName"+(i+1), member.get("firstName"));
			params.put("committeeLastName"+(i+1), member.get("lastName"));
			if (member.get("middleName") != null)
				params.put("committeeMiddleName"+(i+1), member.get("middleName"));
			if (member.get("role") != null)
				params.put("committeeRoles"+(i+1),member.get("role"));
		}
		params.put("step","documentInfo");
		params.put("submit_next", "Save and Continue");

		// Post the form
		Response response = POST(DOCUMENT_INFO_URL,params);
		assertNotNull(response.getHeader("Location")); 

		// Verify the Submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);

		if (title != null)
			assertEquals(title,sub.getDocumentTitle());
		if (degreeMonth != null)
			assertEquals(Integer.valueOf(degreeMonth),sub.getGraduationMonth());
		if (degreeYear != null)
			assertEquals(Integer.valueOf(degreeYear),sub.getGraduationYear());
		if (defenseDate != null) {
			DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			assertEquals(defenseDate,formatter.format(sub.getDefenseDate()));
		}
		if (docType != null)
			assertEquals(docType,sub.getDocumentType());
		if (abstractText != null)
			assertEquals(abstractText,sub.getDocumentAbstract());
		if (keywords != null)
			assertEquals(keywords,sub.getDocumentKeywords());
		if (subjectPrimary != null)
			assertEquals(subjectPrimary,sub.getDocumentSubjects().get(0));
		if (subjectSecondary != null)
			assertEquals(subjectSecondary,sub.getDocumentSubjects().get(1));
		if (subjectTertiary != null)
			assertEquals(subjectTertiary,sub.getDocumentSubjects().get(2));
		if (language != null)
			assertEquals(language,sub.getDocumentLanguage());
		if (chairEmail != null)
			assertEquals(chairEmail, sub.getCommitteeContactEmail());
		if (publishedMaterial != null)
			assertEquals(publishedMaterial, sub.getPublishedMaterial());
		if (embargo != null)
			assertEquals(Long.valueOf(embargo), sub.getEmbargoType().getId());		
		
		assertEquals(committee.size(), sub.getCommitteeMembers().size());
	}

	/**
	 * Upload documents to the submission.
	 * 
	 * @param primary
	 *            The primary document to upload.
	 * @param supplementary
	 *            A list of supplementary documents to upload.
	 */
	public void fileUpload(String primary, String... supplementary)
			throws IOException {

		// Get our URL
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",subId);
		final String FILE_UPLOAD_URL = Router.reverse("submit.FileUpload.fileUpload",routeArgs).url;

		// Upload the primary documents
		Map<String, String> params= new HashMap<String, String>();
		params.put("step","fileUpload");
		params.put("uploadPrimary", "Upload");

		Map<String, File> fileParams = new HashMap<String,File>();
		File primaryFile = null;
		if (primary != null) {
			primaryFile = getResourceFile(primary);
			fileParams.put("primaryDocument", primaryFile);
		}

		Response response = POST(FILE_UPLOAD_URL,params,fileParams);
		assertIsOk(response);
		
		if (primaryFile != null)
			primaryFile.delete();

		// Upload each of the supplementary documents
		for (String supplement : supplementary) {
			params= new HashMap<String, String>();
			params.put("uploadAdditional", "Upload");
			params.put("attachmentType", "SUPPLEMENTAL");
			
			fileParams = new HashMap<String,File>();
			File supplementaryFile = getResourceFile(supplement);
			fileParams.put("additionalDocument", supplementaryFile);			

			response = POST(FILE_UPLOAD_URL,params,fileParams);
			assertIsOk(response);

			supplementaryFile.delete();
		}

		// Verify the submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);

		if (primaryFile != null)
			assertNotNull(sub.getPrimaryDocument());
		else 
			assertNull(sub.getPrimaryDocument());
		assertEquals(supplementary.length, sub.getSupplementalDocuments().size());


		// Now contiue on to the next page.
		params= new HashMap<String, String>();
		params.put("submit_next", "Save and Continue");

		// Post the form
		response = POST(FILE_UPLOAD_URL,params);
		assertNotNull(response.getHeader("Location")); 
	}

	/**
	 * Confirm the submission. We assume there will be no errors because each
	 * step before hand should have taken care of that.
	 * 
	 * @param studentEmail
	 *            The email address of the student (so we can verify they
	 *            received their email)
	 * @param advisorEmail
	 *            The email address of the advisor (so we can verify they
	 *            received their email) If the advisor email begins with a "!"
	 *            then this method will check that an email was NOT sent to that
	 *            address.
	 */
	public void confirm(String studentEmail, String advisorEmail) throws InterruptedException {
		Mail.Mock.reset();

		
		// Get our URL
		Map<String,Object> routeArgs = new HashMap<String,Object>();
		routeArgs.put("subId",subId);
		final String CONFIRM_URL = Router.reverse("submit.Confirm.confirm",routeArgs).url;


		Map<String,String> params= new HashMap<String, String>();
		params.put("submit_confirm", "Confirm and Save Submission");

		// Post the form
		Response response = POST(CONFIRM_URL,params);
		assertNotNull(response.getHeader("Location")); 

		// Verify the submission
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		Submission sub = subRepo.findSubmission(subId);

		State newState = sub.getState();
		assertTrue(newState != stateManager.getInitialState());
		
		
		// Get the completted submission page.
		response = GET(response.getHeader("Location"));
		assertContentMatch("Submittal Complete", response);
		
		
		// Verify the new state;
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
		sub = subRepo.findSubmission(subId);
		
		// Wait for the emails to be recieved.
		String studentContent = null;
		for (int i = 0; i < 50; i++) {
			Thread.yield();
			Thread.sleep(100);
			studentContent = Mail.Mock.getLastMessageReceivedBy(studentEmail);
			if (studentContent != null)
				break;
		}
		assertNotNull(studentContent);
		
		if (advisorEmail != null) {
			String advisorContent = null;
			boolean expectEmail = true;
			if (advisorEmail.startsWith("!")) {
				advisorEmail = advisorEmail.substring(1);
				expectEmail = false;
			}
			
			for (int i = 0; i < ( expectEmail ? 50 : 25); i++) {
				Thread.yield();
				Thread.sleep(100);
				advisorContent = Mail.Mock.getLastMessageReceivedBy(advisorEmail);
				if (advisorContent != null)
					break;
			}
			
			if (expectEmail) {
				// The email should have been sent.
				assertNotNull(advisorContent);
				assertTrue(advisorContent.contains(sub.getCommitteeEmailHash()));
			} else {
				// Confirm that no email was sent.
				assertNull(advisorContent);
			}
			
			// Either way the hash should have been assigned.
			assertNotNull(sub.getCommitteeEmailHash());
		}
		
	}



	/**
	 * Internal Helper Method
	 * 
	 * Extract the file from the jar and place it in a temporary location for
	 * the test to operate from. The caller needs to remember to delete the file
	 * after it's use.
	 * 
	 * @param filePath
	 *            The path, relative to the classpath, of the file to reference.
	 * @return A Java File object reference.
	 */
	protected static File getResourceFile(String filePath) throws IOException {

		File file = File.createTempFile("ingest-import-test", ".pdf");

		// While we're packaged by play we have to ask Play for the inputstream instead of the classloader.
		//InputStream is = DSpaceCSVIngestServiceImplTests.class
		//		.getResourceAsStream(filePath);
		InputStream is = Play.classloader.getResourceAsStream(filePath);
		OutputStream os = new FileOutputStream(file);

		// Copy the file out of the jar into a temporary space.
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) > 0) {
			os.write(buffer, 0, len);
		}
		is.close();
		os.close();

		return file;
	}
}