package controllers.submit;

import java.util.List;
import java.util.Map;

import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.state.State;

import play.Logger;

import controllers.Security;
import controllers.Student;

/**
 * This is the fifth, and last step, of the submission process. We allow
 * students to review all their information and make one final confirmation
 * before completing the submission.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author <a href="bill-ingram.com">Bill Ingram</a>
 * @author Dan Galewsky
 */
public class Confirm extends AbstractSubmitStep {

	/**
	 * Confirm the submission has passed the verification from all the previous
	 * steps. Then allow the user to review their information before confirming
	 * the submission.
	 * 
	 * @param subId
	 *            The submission's id.
	 */
	@Security(RoleType.STUDENT)
	public static void confirm(Long subId) {		

		// Submission configuration
		boolean requestCollege = (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_COLLEGE) != null) ? true : false;
		boolean requestBirth = (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_BIRTH) != null) ? true : false;
		boolean requestUMI = (settingRepo.getConfig(Configuration.SUBMIT_REQUEST_UMI) != null) ? true : false;

		// Locate the submission 
		Submission sub = getSubmission();
		Person submitter = context.getPerson();

		// Pull out information from the submission for verification.
		// PersonalInfo
		String firstName = sub.getStudentFirstName();
		String lastName = sub.getStudentLastName();
		String birthYear = (sub.getStudentBirthYear() != null) ? String.valueOf(sub.getStudentBirthYear()) : null;
		String college = sub.getCollege();
		String department = sub.getDepartment();
		String degree = sub.getDegree();
		String major = sub.getMajor();
		String permPhone = submitter.getPermanentPhoneNumber();
		String permAddress = submitter.getPermanentPostalAddress();
		String permEmail = submitter.getPermanentEmailAddress();

		// License
		String licenseAgreement = sub.getLicenseAgreementDate() != null ? "agreed" : null;

		// DocumentInfo
		String title = sub.getDocumentTitle();
		String degreeMonth = (sub.getGraduationMonth() != null) ? String.valueOf(sub.getGraduationMonth()) : null;
		String degreeYear = (sub.getGraduationYear() != null) ? String.valueOf(sub.getGraduationYear()) : null;
		String docType = sub.getDocumentType();
		String abstractText = sub.getDocumentAbstract();
		String keywords = sub.getDocumentKeywords();
		String chairEmail = sub.getCommitteeContactEmail();
		List<Map<String,String>> committee = DocumentInfo.loadCommitteeMembers(sub);
		String embargo = (sub.getEmbargoType() != null) ? String.valueOf(sub.getEmbargoType().getId()) : null;
		String umi = (sub.getUMIRelease() != null) ? String.valueOf(sub.getUMIRelease()) : null;

		// FileUpload
		Attachment primaryDocument = sub.getPrimaryDocument();


		if (!PersonalInfo.verify(firstName, lastName, birthYear, college, department, degree, major, permPhone, permAddress, permEmail)) {
			validation.addError("personalInfo", "There are errors on this page to correct.");
		}

		if (!License.verify(licenseAgreement)) {
			validation.addError("license","There are errors on this page to correct.");
		}

		if (!DocumentInfo.verify(title, degreeMonth, degreeYear, docType, abstractText, keywords, committee, chairEmail, embargo)) {
			validation.addError("documentInfo", "There are errors on this page to correct.");
		}

		if (!FileUpload.verify(primaryDocument)) {
			validation.addError("fileUpload", "There are errors on this page to correct.");
		}
		
		

		if (params.get("submit_confirm") != null && !validation.hasErrors()) {
		
			// TODO: Send emails
			// TODO: generate email hash
			// TODO: update filenames?
			try {
				context.turnOffAuthorization();
				State nextState = sub.getState().getTransitions(sub).get(0);
				sub.setState(nextState);
				sub.save();
				
			} finally {
				context.restoreAuthorization();
			}
			Student.review(subId);
			
		}
		

		List<ActionLog> logs = subRepo.findActionLog(sub);
		List<Attachment> supplementaryDocuments = sub.getSupplementalDocuments();
		String grantor = settingRepo.getConfig(Configuration.GRANTOR,"Unknown Institution");


		renderTemplate("Submit/confirm.html",subId, sub, grantor, submitter, logs, 

				primaryDocument,
				supplementaryDocuments,

				requestCollege, requestBirth, requestUMI);		
	}

}
