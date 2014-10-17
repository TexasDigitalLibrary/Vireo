package controllers;

import java.util.Calendar;

import org.tdl.vireo.constant.AppConfig;
import org.tdl.vireo.model.Submission;

import play.Logger;
import play.mvc.With;

/**
 * This is a very simple controller that only handles the index page of the application.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * 
 */
@With(Authentication.class)
public class Application extends AbstractVireoController {

	/**
	 * The variable of where to place the start a submission button in the front page text.
	 */
	public static final String START_SUBMISSION = "{START_SUBMISSION}";

	/**
	 * Vireo index page.
	 */
	public static void index() {

		// Get the text for the front page.
		String instructions = settingRepo.getConfigValue(AppConfig.FRONT_PAGE_INSTRUCTIONS);
		instructions = text2html(instructions);

		// Split the text into two blocks, one block before the
		// "start submission" button, and the other after.
		String instructionsBefore = "";
		String instructionsAfter = "";

		int idx = instructions.indexOf(START_SUBMISSION);
		if (idx > -1) {

			instructionsBefore = instructions.substring(0, idx);
			instructionsAfter = instructions.substring(idx + START_SUBMISSION.length(), instructions.length());
		} else {
			instructionsBefore = instructions;
		}

		SubmissionStatus subStatus = new SubmissionStatus();

		// Render the Application/index.html template
		render(instructionsBefore, instructionsAfter, subStatus);
	}

	/**
	 * Helper class to pass submission statuses to template
	 * 
	 * @author <a href="mailto:gad.krumholz@austin.utexas.edu">Gad Krumholz</a>
	 */
	public static class SubmissionStatus {
		// see if current student has any submissions already started
		private Boolean submissionInProgress = false;
		private Boolean submissionNeedsCorrections = false;
		private Boolean submissionSubmittedCurrentSemester = false;
		// the id of the pending submission if it exists (otherwise 0)
		private Long ipSubmissionId = (long) 0;
		// the id of the needs corrections submission if it exists (otherwise 0)
		private Long ncSubmissionId = (long) 0;

		// from application settings
		private Boolean allowMultiple = settingRepo.getConfigBoolean(AppConfig.ALLOW_MULTIPLE_SUBMISSIONS);
		private Boolean submissionsOpen = settingRepo.getConfigBoolean(AppConfig.SUBMISSIONS_OPEN);
		private String currentSemester = settingRepo.getConfigValue(AppConfig.CURRENT_SEMESTER);

		public SubmissionStatus() {
			// go through all of the submissions for the currently authenticated user
			for (Submission submission : subRepo.findSubmission(context.getPerson())) {
//				Logger.debug("Sub Id: %s", submission.getId());
//				Logger.debug("isActive: %s", submission.getState().isActive()); // Submitted, Under Review, Needs Corrections, Corrections Received, Waiting on Req, On Hold, Approved
//				Logger.debug("isApproved: %s", submission.getState().isApproved()); // Approved
//				Logger.debug("isArchived: %s", submission.getState().isArchived());// Pending Pub, Published, Withdrawn, Cancelled
//				Logger.debug("isDeletable: %s", submission.getState().isDeletable()); // Withdrawn, Cancelled
//				Logger.debug("isDepositable: %s", submission.getState().isDepositable()); // Published
//				Logger.debug("isEditableByReviewer: %s", submission.getState().isEditableByReviewer()); // Submitted, Under Review, Needs Corrections, Corrections Received, Waiting on Req, On Hold, Approved, Pending Pub, Published, Withdrawn, Cancelled
//				Logger.debug("isEditableByStudent: %s", submission.getState().isEditableByStudent()); // Needs Corrections
//				Logger.debug("isInProgress: %s", submission.getState().isInProgress()); // In Progress
//				Logger.debug("isInWorkflow: %s", submission.getState().isInWorkflow()); // Submitted, Corrections Received, Approved, Pending Pub, Published

				// if submission has "In Progress" state
				if (submission.getState().isInProgress()) {
					submissionInProgress = true;
					ipSubmissionId = submission.getId();
				}
				// if submission has "Needs Corrections" state
				if (submission.getState().isEditableByStudent()) {
					submissionNeedsCorrections = true;
					ncSubmissionId = submission.getId();
				}
				// if submission is any other state this semester (except for "Cancelled" or "Withdrawn")
				submissionSubmittedCurrentSemester = IsSubmissionSubmittedCurrentSemester(currentSemester, submission);
			}
		} // end SubmissionStatus()

		public static Boolean IsSubmissionSubmittedCurrentSemester(String currentSemester, Submission submission) {
			Boolean ret = false;
			// get the parts of the current semester date
			String[] parts = currentSemester.split(" ");
			Integer csMonth = -1;
			Integer csYear = -1;
			if (parts.length == 2) {
				csMonth = SettingsTab.monthNameToInt(parts[0]); // 0-11
				csYear = Integer.valueOf(parts[1]); // 2014
			}

			// if submission is any other state this semester (except for "Cancelled" or "Withdrawn")
			if (!submission.getState().isDeletable() && submission.getSubmissionDate() != null) {
				if (parts.length == 2) {
					// submission's calendar
					Calendar subCal = Calendar.getInstance();
					subCal.setTime(submission.getSubmissionDate());
					// current semester should be over by the 28th of the csMonth/csYear
					Calendar csCal = Calendar.getInstance();
					csCal.set(csYear, csMonth, 28);
					// begining of semester was csDate - 5 months
					Calendar csStartCal = Calendar.getInstance();
					csStartCal.set(csYear, csMonth, 28);
					csStartCal.add(Calendar.MONTH, -5);
					// if the submission was published after the begining of the current semester AND before the end of the current semester
					if (subCal.after(csStartCal) && subCal.before(csCal)) {
						ret = true;
					}
				}
			}
			return ret;
		}

		public Boolean getAllowMultiple() {
			return allowMultiple;
		}

		public String getCurrentSemester() {
			return currentSemester;
		}

		public Long getNcSubmissionId() {
			return ncSubmissionId;
		}

		public Long getIpSubmissionId() {
			return ipSubmissionId;
		}

		public Boolean getSubmissionNeedsCorrections() {
			return submissionNeedsCorrections;
		}

		public Boolean getSubmissionInProgress() {
			return submissionInProgress;
		}

		public Boolean getSubmissionSubmittedCurrentSemester() {
			return submissionSubmittedCurrentSemester;
		}

		public Boolean getSubmissionsOpen() {
			return submissionsOpen;
		}
	}
}