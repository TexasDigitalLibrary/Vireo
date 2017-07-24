/**
 * 
 */
package org.tdl.vireo.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.email.RecipientType;
import org.tdl.vireo.model.AbstractWorkflowRuleCondition;
import org.tdl.vireo.model.ConditionType;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.EmailWorkflowRule;
import org.tdl.vireo.model.SettingsRepository;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionRepository;
import org.tdl.vireo.model.jpa.JpaAdministrativeGroupImpl;
import org.tdl.vireo.security.SecurityContext;
import org.tdl.vireo.state.State;

import play.db.jpa.JPA;
import play.libs.Mail;
import play.modules.spring.Spring;
import play.test.UnitTest;

/**
 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
 *
 */
public class EmailRuleServiceTest extends UnitTest {
	private static SecurityContext context = Spring.getBeanOfType(SecurityContext.class);
	private static SubmissionRepository subRepo = Spring.getBeanOfType(SubmissionRepository.class);
	private static SettingsRepository settingRepo = Spring.getBeanOfType(SettingsRepository.class);

	private JpaAdministrativeGroupImpl adminGroup = null;
	List<EmailWorkflowRule> rules = new ArrayList<EmailWorkflowRule>();

	@Before
	public void setup() {
		// Turn off authentication for the test thread
		context.turnOffAuthorization();
		// create the test data
		createData();
	}

	@After
	public void cleanup() {
		// delete the test data
		deleteData();
		// Turn on authentication for the test thread
		context.restoreAuthorization();
	}

	private void createData() {
		// Create our Administrative Group to set as a recipient
		adminGroup = (JpaAdministrativeGroupImpl) settingRepo.createAdministrativeGroup("RuleService Test");
		adminGroup.addEmail("gad.krumholz@austin.utexas.edu");
		adminGroup.addEmail("gad.krumholz@utexas.edu");
		adminGroup.setDisplayOrder(0);
		adminGroup.save();

		Map<String, State> stateBeanMap = Spring.getBeansOfType(State.class);
		// create an email rule for every state that an Email Workflow Rule is allowed to have
		for (String key : stateBeanMap.keySet()) {
			State state = stateBeanMap.get(key);
			if (state != null && EmailRuleService.doesStateMatchEWFLRuleStates(state)) {
				EmailWorkflowRule rule = settingRepo.createEmailWorkflowRule(state);
				AbstractWorkflowRuleCondition condition = settingRepo.createEmailWorkflowRuleCondition(ConditionType.Always);
				condition.save();
				rule.setCondition(condition);
				EmailTemplate emailTemplate = settingRepo.findEmailTemplateByName("SYSTEM New User Registration");
				rule.setEmailTemplate(emailTemplate);
				rule.setRecipientType(RecipientType.AdminGroup);
				rule.setAdminGroupRecipient(adminGroup);
				rule.enable();
				rule.save();
				rules.add(rule);
			}
		}
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}

	private void deleteData() {
		// remove all the rules we created during setup
		for (EmailWorkflowRule rule : rules) {
			settingRepo.findEmailWorkflowRule(rule.getId()).delete();
		}
		// remove the administrative group we created during setup
		if (adminGroup != null) {
			settingRepo.findAdministrativeGroup(adminGroup.getId()).delete();
		}
		JPA.em().getTransaction().commit();
		JPA.em().clear();
		JPA.em().getTransaction().begin();
	}

	@Test
	public void testRunEmailRules() throws InterruptedException {

		// go through built-in submissions from testing and runEmailRules on each one
		Iterator<Submission> subIterator = subRepo.findAllSubmissions();
		while (subIterator.hasNext()) {
			Submission submission = subIterator.next();
			play.Logger.info("Running email rules for submission id: %d", submission.getId());
			EmailRuleService.runEmailRules(submission);
			// Wait for the email to be recieved (for up to 5000 miliseconds).
			String emailContent = null;
			for (int i = 0; i < 50; i++) {
				Thread.yield();
				Thread.sleep(100);
				emailContent = Mail.Mock.getLastMessageReceivedBy(adminGroup.getEmails().get(0));
				if (emailContent != null)
					break;
			}

			if (EmailRuleService.doesSubStateMatchEWFLRuleStates(submission)) {
				assertNotNull(emailContent);
			} else {
				assertNull(emailContent);
			}
			Mail.Mock.reset();
		}
	}
}
