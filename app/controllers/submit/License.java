package controllers.submit;

import static org.tdl.vireo.constant.AppConfig.*;
import static org.tdl.vireo.constant.FieldConfig.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.AttachmentType;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.NameFormat;
import org.tdl.vireo.model.Person;
import org.tdl.vireo.model.RoleType;
import org.tdl.vireo.model.Submission;

import controllers.Security;

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
	 * After accepting the license the text is stamped with the current user and
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
		String licenseText = settingRepo.getConfigValue(SUBMIT_LICENSE_TEXT);

		String proquestAgreement = params.get("proquestAgreement");
		String proquestText = settingRepo.getConfigValue(PROQUEST_LICENSE_TEXT);
		
		// If it's blank, make it null.
		if (licenseAgreement != null && licenseAgreement.trim().length() == 0)
			licenseAgreement = null;
		
		if (proquestAgreement != null && proquestAgreement.trim().length() == 0)
			proquestAgreement = null;
		
		if ("license".equals(params.get("step"))) {
			// Save the state
			if (licenseAgreement == null) {
				recordDisagreement(sub, "LICENSE", true);
			} else {
				recordAgreement(sub, licenseText, "LICENSE", true);
			}
			if(proquestAgreement == null) {
				recordDisagreement(sub, "PROQUEST_LICENSE", false);
				sub.setUMIRelease(false);
			} else {
				recordAgreement(sub, proquestText,"PROQUEST_LICENSE", false);
				sub.setUMIRelease(true);
			}
			sub.save();
			
		} else {
			// load the state
			licenseAgreement = sub.getLicenseAgreementDate() != null ? "true" : null;
			proquestAgreement = sub.getUMIRelease() != null && sub.getUMIRelease() ? "true" : null;
		}
		
		// Verify the form if we are submitting or if jumping from the confirm step.
		if ("license".equals(params.get("step")) ||
			"confirm".equals(flash.get("from-step"))) {
			verify(sub);
		}
		

		if (params.get("submit_next") != null && !validation.hasErrors()) {
			// Display the license -- passing along the submission id
			DocumentInfo.documentInfo(subId);
		} 

		// Format the license text for display
		licenseText = text2html(licenseText);
		proquestText = text2html(proquestText);

		renderTemplate("Submit/license.html",subId,licenseText,licenseAgreement,proquestText,proquestAgreement);

	}

	/**
	 * Verify the license agreement has been accepted.
	 * 
	 * @param sub The submission to verify.
	 * @return True if no errors were found, otherwise false.
	 */
	public static boolean verify(Submission sub) {
		
		if (isFieldRequired(LICENSE_AGREEMENT)) {
			List<Attachment> licenses = sub.getAttachmentsByType(AttachmentType.LICENSE);
			
			boolean hasLicense = false;
			for(Attachment license : licenses) {
				if ("LICENSE.txt".equals(license.getName()))
					hasLicense = true;
			}
			
			if (hasLicense == false) {
				validation.addError("licenseAgreement","You must agree to the license agreement before continuing");				
			}
		} // isRequired
		
		if (isFieldRequired(UMI_RELEASE)) {
			List<Attachment> licenses = sub.getAttachmentsByType(AttachmentType.LICENSE);
			
			boolean hasLicense = false;
			for(Attachment license : licenses) {
				if ("PROQUEST_LICENSE.txt".equals(license.getName()))
					hasLicense = true;
			}
			
			if (hasLicense == false) {
				validation.addError("proquestAgreement","You must agree to the ProQuest license agreement before continuing");				
			}
		}
		
		if(validation.hasErrors()) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Record that the user has disagreed with a license. We'll allow the user
	 * to change their mind and remove their previous agreement however we'll
	 * still count that as an error.
	 * 
	 * 
	 * @param sub
	 *            The submission
	 * @param licenseName
	 * 			  The string to name the file
	 * @param primaryAgreement
	 * 			  A boolean to designate if this the primary license to remove the License Agreement Date 
	 * 			on the submission. There must always be one license that is the primary license
	 * 			if you don't want the License Agreement Date to be null.
	 */
	protected static void recordDisagreement(Submission sub, String licenseName, Boolean primaryAgreement) {
		
		// Remove the submission agreement date
		if (primaryAgreement)
			sub.setLicenseAgreementDate(null);
		
		licenseName += ".txt";
		
		// Remove any license text
		for (Attachment attachment : sub.getAttachments()) {
			if (attachment.getType() == AttachmentType.LICENSE && licenseName.equals(attachment.getName())) {
				attachment.delete();
				break;
			}
		}
		
	}
	
	/**
	 * Record that the user has agreed a license. We save the date, and the current license text.
	 * 
	 * @param sub
	 * 			  The submission.
	 * @param licenseText
	 * 			  The license text agreed to.
	 *  * @param licenseName
	 * 			  The string to name the file
	 * @param primaryAgreement
	 * 			  A boolean to designate if this the primary license to set the License Agreement Date 
	 * 			on the submission. There must always be one license that is the primary license
	 * 			if you don't want the License Agreement Date to be null.
	 */
	protected static void recordAgreement(Submission sub, String licenseText, String licenseName, Boolean primaryAgreement) {
		
		Date agreementDate = new Date();
		
		// Set submission agreement date
		if (primaryAgreement)
			sub.setLicenseAgreementDate(agreementDate);

		licenseName += ".txt";
		
		// Check if another license has been selected been saved.
		for (Attachment attachment : sub.getAttachments()) {
			if (attachment.getType() == AttachmentType.LICENSE && licenseName.equals(attachment.getName())) {
				// Remove the old license, and save the new one.
				attachment.delete();
				break;
			}
		}

		licenseText = stampLicense(licenseText,agreementDate);

		// Save the text that the student agreed too.
		try {
			sub.addAttachment(licenseText.getBytes(), licenseName, AttachmentType.LICENSE).save();
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
		licenseText += "The license above was accepted by "+submitter.getFormattedName(NameFormat.FIRST_LAST)+" on "+formatter.format(agreementDate)+"\n";

		return licenseText;

	}
}
