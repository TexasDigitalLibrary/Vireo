package controllers.submit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import controllers.Security;
import controllers.Student;

/**
 * The second step of the license process where the user must accept the provided license.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 * @author <a href="bill-ingram.com">Bill Ingram</a>
 * @author Dan Galewsky
 */
public class License extends AbstractSubmitStep {

	/**
	 * The second step of the submission process. Where the user must accept the
	 * provided license.
	 * 
	 * After accepting the license the text is stapmed with the current user and
	 * agreement date. Then both the agreement date and stamped license text are
	 * saved to the submission.
	 * 
	 * @param subId The id of the submission.
	 */
	@Security(RoleType.STUDENT)
	public static void license(Long subId) {

		Submission sub = getSubmission();

		// Get the form data.
		String licenseAgreement = params.get("licenseAgreement");
		String licenseText = settingRepo.getConfig(Configuration.SUBMIT_LICENSE,DEFAULT_LICENSE);

		if (params.get("submit_next") == null) {
			licenseAgreement = sub.getLicenseAgreementDate() != null ? "true" : null; 
		}
		
		// If it's blank, make it null.
		if (licenseAgreement != null && licenseAgreement.trim().length() == 0)
			licenseAgreement = null;
		
		if (!"true".equals(flash.get("nextStep")))
			verify(licenseAgreement);

		
		// first time here?
		if (params.get("submit_next") != null) {

			if (licenseAgreement == null) {
				recordDisagreement(sub);
				
				sub.save();
			} else {
				recordAgreement(sub,licenseText);
				
				sub.save();

				flash.put("nextStep", "true");
				DocumentInfo.documentInfo(subId);
			}
		}

		// Format the license text for display
		licenseText = licenseText.replaceAll("  ", "&nbsp;&nbsp;");
		String[] paragraphs = licenseText.split("\n\\s*\n");
		licenseText = "";
		for (String paragraph : paragraphs) {
			licenseText += "<p>"+paragraph+"</p>";
		}

		licenseText = licenseText.replaceAll("\n", "<br/>");

		renderTemplate("Submit/license.html",subId,licenseText,licenseAgreement);

	}

	
	/**
	 * Verify the license agreement has been agreed to.
	 * 
	 * @param licenseAgreement
	 *            The license agreement field.
	 * @return True if the license has been agreed to, otherwise false.
	 */
	public static boolean verify(String licenseAgreement) {
		
		if (licenseAgreement == null) {
			validation.addError("laLabel", "You must agree to the license agreement before continuing.");
			return false;
		} else {
			return true;
		}
		
	}
	
	/**
	 * Record that the user has disagreed with the license. We'll allow the user
	 * to change their mind and remove their previous agreement however we'll
	 * still count that as an error.
	 * 
	 * 
	 * @param sub
	 *            The submission
	 */
	protected static void recordDisagreement(Submission sub) {
		
		// Remove the license agreement date
		sub.setLicenseAgreementDate(null);
		
		// Remove any license text
		for (Attachment attachment : sub.getAttachments()) {
			if (attachment.getType() == AttachmentType.LICENSE && attachment.getName() == "LICENSE.txt") {
				attachment.delete();
				break;
			}
		}
		
	}
	
	/**
	 * Record that the user has aggreed to the license. We save the date, and the current license text.
	 * 
	 * @param sub The submission.
	 * @param licenseText The license text agreed to.
	 */
	protected static void recordAgreement(Submission sub, String licenseText) {
		
		Date agreementDate = new Date();
		sub.setLicenseAgreementDate(agreementDate);


		// Check if another license has been selected been saved.
		for (Attachment attachment : sub.getAttachments()) {
			if (attachment.getType() == AttachmentType.LICENSE && attachment.getName() == "LICENSE.txt") {
				// Remove the old license, and save the new one.
				attachment.delete();
				break;
			}
		}

		licenseText = stampLicense(licenseText,agreementDate);

		// Save the text that the student aggreed too.
		try {
			sub.addAttachment(licenseText.getBytes(),"LICENSE.txt",AttachmentType.LICENSE).save();
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to save license aggreement.",ioe);
		}
	}
	
	/**
	 * Stamp the license with who is accepting the license and the current date.
	 * This way this information will be stored directly with the license text.
	 * 
	 * @param licenseText
	 *            The license text.
	 * @param agreementDate
	 *            The exact date of agreement.
	 * @return The stamped license text
	 */
	protected static String stampLicense(String licenseText, Date agreementDate) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm a");
		Person submitter = context.getPerson();

		licenseText += "\n\n--------------------------------------------------------------------------\n";
		licenseText += "The license above was accepted by "+submitter.getFullName()+" on "+formatter.format(agreementDate)+"\n";

		return licenseText;

	}

	/**
	 * This is the default license if no one has set any license yet under the
	 * application's setting tab. It's just the super generic version adopted by
	 * TDL.
	 */
	protected final static String DEFAULT_LICENSE = 
			"I grant the Texas Digital Library (hereafter called \"TDL\"), my home institution (hereafter called \"Institution\"), and my academic department (hereafter called \"Department\") the non-exclusive rights to copy, display, perform, distribute and publish the content I submit to this repository (hereafter called \"Work\") and to make the Work available in any format in perpetuity as part of a TDL, Institution or Department repository communication or distribution effort.\n" +
					"\n" +
					"I understand that once the Work is submitted, a bibliographic citation to the Work can remain visible in perpetuity, even if the Work is updated or removed.\n" +
					"\n" +
					"I understand that the Work's copyright owner(s) will continue to own copyright outside these non-exclusive granted rights.\n" +
					"\n" +
					"I warrant that:\n" +
					"\n" +
					"    1) I am the copyright owner of the Work, or\n" +
					"    2) I am one of the copyright owners and have permission from the other owners to submit the Work, or\n" +
					"    3) My Institution or Department is the copyright owner and I have permission to submit the Work, or\n" +
					"    4) Another party is the copyright owner and I have permission to submit the Work.\n" +
					"\n" +
					"Based on this, I further warrant to my knowledge:\n" +
					"\n" +
					"    1) The Work does not infringe any copyright, patent, or trade secrets of any third party,\n" +
					"    2) The Work does not contain any libelous matter, nor invade the privacy of any person or third party, and\n" +
					"    3) That no right in the Work has been sold, mortgaged, or otherwise disposed of, and is free from all claims.\n" +
					"\n" +
					"I agree to hold TDL, Institution, Department, and their agents harmless for any liability arising from any breach of the above warranties or any claim of intellectual property infringement arising from the exercise of these non-exclusive granted rights.\n"+
					"\n";
}
