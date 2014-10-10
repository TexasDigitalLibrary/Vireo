package controllers;

import java.util.Date;

import org.tdl.vireo.constant.AppConfig;
import org.tdl.vireo.model.Submission;

import play.mvc.With;

/**
 * This is a very simple controller that only handles the index page of the
 * application.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
@With(Authentication.class)
public class Application extends AbstractVireoController {

	/**
	 * The variable of where to place the start a submission button in the front
	 * page text.
	 */
	public static final String START_SUBMISSION = "{START_SUBMISSION}";

	/**
	 * Vireo index page.
	 */
	public static void index() {

		// Get the text for the front page.
		String instructions = settingRepo
				.getConfigValue(AppConfig.FRONT_PAGE_INSTRUCTIONS);
		instructions = text2html(instructions);

		// Split the text into two blocks, one block before the
		// "start submission" button, and the other after.
		String instructionsBefore = "";
		String instructionsAfter = "";

		int idx = instructions.indexOf(START_SUBMISSION);
		if (idx > -1) {

			instructionsBefore = instructions.substring(0, idx);
			instructionsAfter = instructions.substring(
					idx + START_SUBMISSION.length(), instructions.length());
		} else {
			instructionsBefore = instructions;
		}

		// see if multiple submissions are enabled
		boolean allowMultiple = settingRepo.getConfigBoolean(AppConfig.ALLOW_MULTIPLE_SUBMISSIONS);

		// see if current student has any submissions already started for this semester
		String currentSemester = settingRepo.getConfigValue(AppConfig.CURRENT_SEMESTER); // in the format "Month Year"
		String[] parts = currentSemester.split(" ");

		boolean submissionStartedCurrentSemester = false;
        if (parts.length == 2) {
	        Integer month = ViewTab.monthNameToInt(parts[0]);
	        Integer year = Integer.valueOf(parts[1]);
	        
			for(Submission submission : subRepo.findSubmission(context.getPerson())) {
				//submission.getDefenseDate();
				if(submission.getGraduationMonth() != null && submission.getGraduationMonth() <= month && submission.getGraduationYear() != null && submission.getGraduationYear() <= year && submission.getState().isInProgress()) {
					submissionStartedCurrentSemester = true;
				}
				if ((submission.getGraduationMonth() == null || submission.getGraduationYear() == null) && submission.getState().isInProgress()){
					submissionStartedCurrentSemester = true;
				}
			}
        }
		// Render the Application/index.html template
		render(instructionsBefore, instructionsAfter, allowMultiple, submissionStartedCurrentSemester);
	}

}