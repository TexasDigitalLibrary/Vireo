package controllers;

import org.tdl.vireo.constant.AppConfig;
import org.tdl.vireo.model.Configuration;

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

		// Render the Application/index.html template
		render(instructionsBefore, instructionsAfter);
	}

}