package controllers.submit;

import static org.tdl.vireo.constant.AppConfig.*;

import org.tdl.vireo.constant.FieldConfig;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;

import play.mvc.Before;
import play.mvc.With;
import controllers.AbstractVireoController;
import controllers.Authentication;
import controllers.Student;

/**
 * THIS CONTROLLER IS BEING REFACTORED. 
 * 
 * Please don't touch right now.
 */


@With(Authentication.class)
public class AbstractSubmitStep extends AbstractVireoController {
	
	/**
	 * Set up values needed to display submission status at the top of each page.
	 * If submissions are closed - always redirect to the submissionStatus page. 
	 */
	
	@Before(unless="submissionStatus")
	static void beforeSubmit() {
		
		if (settingRepo.findConfigurationByName(SUBMISSIONS_OPEN) == null)
			Student.submissionList();	
		
		renderArgs.put("SUBMISSIONS_OPEN", settingRepo.findConfigurationByName(SUBMISSIONS_OPEN));
		renderArgs.put("CURRENT_SEMESTER", settingRepo.getConfigValue(CURRENT_SEMESTER, "current"));
		
		for(FieldConfig field : FieldConfig.values()) {
			renderArgs.put(field.name(),field );
		}
	}
	
	/**
	 * @return the currently active submission checking for all the error
	 *         conditions.
	 */
	protected static Submission getSubmission() {
		
		// We require an sub id.
		Long subId = params.get("subId", Long.class);
		if (subId == null) {
		    error("Did not receive the expected submission id.");
		} 
		
		// And the submission must exist.
		Submission sub = subRepo.findSubmission(subId);
		if (sub == null) {
		    error("Unable to find the submission #"+subId);
		}
		
		// Check that we are the owner of the submission.
		Person submitter = context.getPerson();
		if (sub.getSubmitter() != submitter)
		    unauthorized();
		
		// And check that it is in the initial state.
		State initialState = stateManager.getInitialState();
		if (sub.getState() != initialState)
			error("This submission is no longer editable.");
		
		return sub;
	}
	
	/**
	 * Return whether the specified field is enabled. If it is then return true,
	 * otherwise false.
	 * 
	 * @param field
	 *            The field to check whether it is enabled.
	 * @return True if enabled, otherwise false.
	 */
	protected static boolean isFieldEnabled(FieldConfig field) {
		return !"disabled".equals(settingRepo.getConfigValue(field.ENABLED));
	}

	/**
	 * Return whether the specified field is required. If it is then return
	 * true, otherwise false.
	 * 
	 * @param field
	 *            The field to check whether it is required.
	 * @return True if required, otherwise false.
	 */
	protected static boolean isFieldRequired(FieldConfig field) {
		return "required".equals(settingRepo.getConfigValue(field.ENABLED));
	}
}