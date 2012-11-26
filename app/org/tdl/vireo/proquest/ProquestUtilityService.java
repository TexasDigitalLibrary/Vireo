package org.tdl.vireo.proquest;

import org.tdl.vireo.model.Attachment;



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
	 * @param language Vireo's language. (may be null)
	 * @return The ProQuest code for the language or null if none found.
	 */
	public ProquestLanguage languageCode(String language);

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
			this.fullPhone = fullPhone;
			this.cntryCode = cntryCode;
			this.areaCode = areaCode;
			this.number = number;
			this.ext = ext;
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
			this.fullAddress = fullAddress;
			this.addrline = addrline;
			this.city = city;
			this.state = state;
			this.zip = zip;
			this.cntry = cntry;
		}

	}

}
