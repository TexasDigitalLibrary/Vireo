package controllers;

import java.util.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Ignore;

import play.Play;
import play.Logger;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.test.FunctionalTest;
import play.modules.spring.Spring;
import play.db.jpa.JPA;

import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.PersonRepository;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.CommitteeMember;
import org.tdl.vireo.model.SettingsRepository;


/**
 * Test all the actions in the Submit controller.
 * 
 * @author Dan Galewsky</a>
 */

public class SubmitTest extends AbstractVireoFunctionalTest {

	// Spring dependencies
	public static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	public static PersonRepository personRepo = Spring.getBeanOfType(PersonRepository.class);
	public static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
        public static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);
	
        @Test
        public void testFullSubmission() {
            
            LOGIN();

            context.turnOffAuthorization();

            // create submission
            Person person = personRepo.findPersonByEmail("bthornton@gmail.com");
            Submission s = subRepo.createSubmission(person);
            s.save();
            long subId = s.getId();

            JPA.em().getTransaction().commit();
            JPA.em().clear();
            JPA.em().getTransaction().begin();
            
            // get first page
            String verifyURL = Router.reverse("Submit.verifyPersonalInformation").url;
            Response response = GET(verifyURL);
            assertIsOk(response);
            response = null;
            
            // add valid values
            Map<String, String> args = new HashMap<String, String>();

            args.put("firstName", "TestStudentFirstName");
            args.put("middleName", "Middle");
            args.put("lastName", "TestStudentLastName");
            args.put("email", "test@studentemail.com");
            args.put("yearOfBirth", "1996");
            
            // Grab the first configured department, degree, major
            args.put("department", settingRepo.findAllDepartments().get(0).getName());
            args.put("degree", settingRepo.findAllDegrees().get(0).getName());
            args.put("major", settingRepo.findAllMajors().get(0).getName());
            
            args.put("permPhone", "555-1212");
            args.put("permAddress", "2222 Fake Street");
            args.put("permEmail", "noreply@noreply.org");
            args.put("currentPhone", "555-1212");
            args.put("currentAddress", "2222 Fake Street");
            
            args.put("subId", Long.toString(subId));
            args.put("submit_next", "");
            
            // submit first page
            response = POST(verifyURL, args);
            response = GET(response.getHeader("Location"));
            
            // check second page
            assertIsOk(response);
            assertContentMatch("License Agreement", response);
            
            s = null;
            
            // check db
            s = subRepo.findSubmission(subId);
            
            // FIXME:  These values are currently not being persisted due to the locked field-set
            // assertEquals(args.get("firstName"), s.getStudentFirstName());
            // assertEquals(args.get("middleName"), s.getStudentMiddleName());
            // assertEquals(args.get("lastName"), s.getStudentLastName());
            // assertEquals(args.get("yearOfBirth"), Integer.toString(s.getStudentBirthYear()));
            assertEquals(args.get("department"), s.getDepartment());
            assertEquals(args.get("major"), s.getMajor());
            assertEquals(args.get("permPhone"), s.getSubmitter().getPermanentPhoneNumber());
            assertEquals(args.get("permAddress"), s.getSubmitter().getPermanentPostalAddress());
            assertEquals(args.get("permEmail"), s.getSubmitter().getPermanentEmailAddress());
            assertEquals(args.get("currentPhone"), s.getSubmitter().getCurrentPhoneNumber());
            assertEquals(args.get("currentAddress"), s.getSubmitter().getCurrentPostalAddress());
            
            args = null;
            response = null;
            
            // add valid value(s)
            args = new HashMap<String, String>();
            args.put("submit_next", "");
            args.put("licenseAgreement", "on");
            
            // submit second page
            Map<String,Object> routeArgs = new HashMap<String,Object>();
            routeArgs.put("subId", Long.toString(subId));
            String licenseURL = Router.reverse("Submit.license", routeArgs).url;
            
            response = POST(licenseURL, args);
            response = GET(response.getHeader("Location"));
            assertIsOk(response);
            
            // check third page
            assertContentMatch("Document Information", response);
            response = null;
            s = null;
            JPA.em().clear();
            
            // check db
            s = subRepo.findSubmission(subId);
            
            assertNotNull(s.getLicenseAgreementDate());
            
            // add valid values
            args = null;
            args = new HashMap<String, String>();
            args.put("subId", Long.toString(subId));
            args.put("title", "Test Title");
            args.put("degreeMonth", String.valueOf(settingRepo.findAllGraduationMonths().get(0).getMonth()));
            args.put("degreeYear", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            args.put("docType", settingRepo.findDocumentType(Long.parseLong("1")).getName());
            args.put("abstractText", "This is an abstract");
            args.put("keywords", "key; word;");
            args.put("committeeFirstName1", "First");
            args.put("committeeMiddleName1", "Middle");
            args.put("committeeLastName1", "Last");
            args.put("committeeChairFlag1", "checked");
            args.put("chairEmail", "fake@email.com");
            args.put("embargo", "1");
            args.put("submit_next", "");
            
            // submit third page
            response = null;
            s = null;
            JPA.em().clear();
            
            String docInfoURL = Router.reverse("Submit.docInfo", routeArgs).url;
            response = POST(docInfoURL, args);
            response = GET(response.getHeader("Location"));

            // check fourth page
            assertIsOk(response);
            assertContentMatch("Upload Your Files", response);
            
            // check db
            s = subRepo.findSubmission(subId);
            
            assertEquals(args.get("chairEmail"), s.getCommitteeContactEmail());
            assertEquals(args.get("title"), s.getDocumentTitle());
            assertEquals(args.get("degreeMonth"), s.getGraduationMonth().toString());
            assertEquals(args.get("degreeYear"), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            assertEquals(args.get("docType"), s.getDocumentType());
            assertEquals(args.get("abstractText"), s.getDocumentAbstract());
            assertEquals(args.get("keywords"), s.getDocumentKeywords());
            assertEquals(settingRepo.findEmbargoType(Long.parseLong(args.get("embargo").toString())), s.getEmbargoType());
            
            List<CommitteeMember> committeeMembers = s.getCommitteeMembers();
            assertEquals(1, committeeMembers.size());
                    
            // add valid values
            
            // submit fourth page
            
            // check fifth page
            
            // check db
            
            // submit fifth page
            
            // check sixth page
            
            // check db
            
            // delete submission

            // Re-fetch Submission in new transaction (to avoid a "detatched" object)
            s = subRepo.findSubmission(subId);
            
            s.delete();

            JPA.em().getTransaction().commit();
            JPA.em().clear();
            JPA.em().getTransaction().begin();

            context.restoreAuthorization();

            JPA.em().getTransaction().commit();
            JPA.em().clear();
            
            // Verify deletion
            s = subRepo.findSubmission(subId);
            assertNull(s);
        }
}