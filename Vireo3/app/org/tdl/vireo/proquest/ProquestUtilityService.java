package org.tdl.vireo.proquest;

import java.util.Locale;

import org.tdl.vireo.model.Attachment;
import org.tdl.vireo.model.Submission;



/**
 * This is services is a set of helpfull computational methods usefull for
 * generating a proquest export. The ability to parse phone numbers and postal
 * addresses into individual components expected by proquest. This allows these
 * complex algorithms to be moved outside the export format itself into a place
 * that is more easiely tested.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */

public interface ProquestUtilityService {

	/**
	 * Parse the provided phone number into the components expected by ProQuest
	 * / UMI's format.
	 * 
	 * @param fullPhone
	 *            The full phone number.
	 * @return A parsed phone number
	 */
	public Phone parsePhone(String fullPhone);

	/**
	 * Parse the provided postal address into the components expected by
	 * ProQuest / UMI's format.
	 * 
	 * @param fullAddress
	 *            The full postal address on multiple lines.
	 * @return A parsed postal address.
	 */
	public Address parseAddress(String fullAddress);

	/**
	 * Determine the ProQuest category of this attachment. The proquest defined
	 * categories are:
	 * 
	 * audio
	 * code/script
	 * image
	 * pdf
	 * presentation
	 * spreadsheet
	 * text
	 * video
	 * webpage
	 * data
	 * other
	 * 
	 * 
	 * @param attachment
	 *            The attachment to categorize.
	 * @return The category, if none was able to be determined then "other" is
	 *         returned.
	 */
	public String categorize(Attachment attachment);
	
	/**
	 * Translate Vireo's ISO 1799 language into ProQuest's seemingly random language codes.
	 * 
	 * @param locale Vireo's language locale. (may be null)
	 * @return The ProQuest code for the language or null if none found.
	 */
	public ProquestLanguage languageCode(Locale locale);

	/**
	 * Translate Vireo's full degree name into a ProQuest degree code.
	 * 
	 * @param degree The degree to translate.
	 * @return The degree code, or the full degree name if not found.
	 */
	public String degreeCode(String degree);
	
	/**
	 * The individual components of a proquest phone number.
	 */
	public static class Phone {
		
		public final String fullPhone;
		public final String cntryCode;
		public final String areaCode;
		public final String number;
		public final String ext;

		public Phone(String fullPhone, String cntryCode, String areaCode,
				String number, String ext) {
			
			this.fullPhone = _sanatize(fullPhone);
			this.cntryCode = _sanatize(cntryCode);
			this.areaCode = _sanatize(areaCode);
			this.number = _sanatize(number);
			this.ext = _sanatize(ext);
		}
		
		private static String _sanatize(String input) {
			if (input == null)
				return null;
			input = input.trim();
			if (input.length() == 0)
				return null;
				
		    return input;	
		}
	}

	/**
	 * The individual components of a proquest address.
	 */
	public static class Address {

		public final String fullAddress;
		public final String addrline;
		public final String city;
		public final String state;
		public final String zip;
		public final String cntry;

		public Address(String fullAddress, String addrline, String city,
				String state, String zip, String cntry) {
			
			this.fullAddress = _sanatize(fullAddress);
			this.addrline = _sanatize(addrline);
			this.city = _sanatize(city);
			this.state = _sanatize(state);
			this.zip = _sanatize(zip);
			this.cntry = _sanatize(cntry);
		}
		
		private static String _sanatize(String input) {
			
			if (input == null)
				return null;
			input = input.trim();
			if (input.length() == 0)
				return null;
		    return input;
			
		}
		
		/**
		 * Note the cntry component is explicitly ignored by this check.
		 * 
		 * @return True if the the address was successfully parsed into components. 
		 */
		public boolean isParsed() {
			if (addrline != null && city != null && state != null && zip != null)
				return true;
			return false;
		}

	}

}
