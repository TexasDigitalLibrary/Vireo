package org.tdl.vireo.constant;



/**
 * Common Field Configuration Names
 * 
 * Stored in the SettingsRepository there are global configuration objects
 * consisting of name / value pairs. Various attributes about each field
 * are stored there such as the student display label, help text, and
 * whether the field is enabled or not. Each field is defined as an enum,
 * with those three properties underneath. So to find the values for the
 * FIRST_NAME field you would say:
 * 
 * FIRST_NAME.LABEL
 * FIRST_NAME.HELP
 * FIRST_NAME.ENABLED
 * 
 * Future developers, feel free to add additional configuration parameters to
 * this class as needed.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public enum FieldConfig {
	
	
	/** Verify Personal Information **/
	STUDENT_FIRST_NAME(
			"First Name",
			"submit_student_first_name_label", 
			"submit_student_first_name_help",
			"submit_student_first_name_enabled",
			"The author's first name. This field may be configured to be locked if supplied by the authentication plugin. If both the first and last name are marked as required, then only one will actualy be required because some people have only one name. However both fields will be displayed as required since most people have more than one name."),

	STUDENT_MIDDLE_NAME(
			"Middle Name",
			"submit_student_middle_name_label", 
			"submit_student_middle_name_help",
			"submit_student_middle_name_enabled",
			"The author's middle name or initial. This field may be configured to be locked if supplied by the authentication plugin. If both the first and last name are marked as required, then only one will actualy be required because some people have only one name. However both fields will be displayed as required since most people have more than one name."),

	STUDENT_LAST_NAME(
			"Last Name",
			"submit_student_last_name_label", 
			"submit_student_last_name_help",
			"submit_student_last_name_enabled",
			"The author's last name. This field may be configured to be locked if supplied by the authentication plugin."),
			
	STUDENT_EMAIL(
			"Email",
			"submit_student_email_label",
			"submit_student_email_help",
			"submit_student_email_enabled",
			"The student's email address. This field is always locked to the address the student used to authenticate with and may not be edited."
			),
			
	STUDENT_BIRTH_YEAR(
			"Birth Year",
			"submit_student_birth_year_label",
			"submit_student_birth_year_help",
			"submit_student_birth_year_enabled",
			"The year the author was born. This field may be configured to be locked if supplied by the authentication plugin."),

	STUDENT_ORCID(
			"ORCID author identifier",
			"submit_student_orcid_label",
			"submit_student_orcid_help",
			"submit_student_orcid_enabled",
			"The author's ORCID persistent digital identifier (orcid.org). It is usually optional."),

	/** Verify Personal Information :: Affiliations **/		
	GRANTOR(
			"Grantor",
			"submit_grantor_label",
			"submit_grantor_help",
			"submit_grantor_enabled",
			"The degree granting institution. This is a non-editable field and is only displayed on the submission process for clarification. The value of this field is shared between all submission and is set under the Application Settings tab."),
	
	COLLEGE(
			"College",
			"submit_college_label",
			"submit_college_help",
			"submit_college_enabled",
			"The college or other unit of institution the author belongs too. This field is selected from a pre-defined list of colleges, or if there are none then is free-form. This field may be configured to be locked if supplied by the authentication plugin."),
	
	PROGRAM(
			"Program",
			"submit_program_label",
			"submit_program_help",
			"submit_program_enabled",
			"The program the author belongs too. This field is selected from a pre-defined list of programs, or if there are none then is free-form."),
			
	DEPARTMENT(
			"Department",
			"submit_department_label",
			"submit_department_help",
			"submit_department_enabled",
			"The department or other unit of institution the author belongs too. This field is selected from a pre-defined list of departments, or if there are none then is free-form. This field may be configured to be locked if supplied by the authentication plugin."),

	DEGREE(
			"Degree",
			"submit_degree_label",
			"submit_degree_help",
			"submit_degree_enabled",
			"The degree being awarded to the author. This field is selected from a list of pre-defined degrees, from the selected degree the level of the degree is determined which will limit the choices of document types later in the submission process. This field may be configured to be locked if supplied by the authentication plugin."),
	
	MAJOR(
			"Major",
			"submit_major_label",
			"submit_major_help",
			"submit_major_enabled",
			"The major of the degree sought by the author. This field is selected from a pre-defined list of majors, or if there are none then is free-form. This field may be configured to be locked if supplied by the authentication plugin."),
			
	/** Verify Personal Information :: Phone & Address **/
			
	PERMANENT_PHONE_NUMBER(
			"Permanent Phone",
			"submit_permanent_phone_number_label",
			"submit_permanent_phone_number_help",
			"submit_permanent_phone_number_enabled",
			"The contact phone number where the author is reachable after leaving the institution. This field may be configured to be locked if supplied by the authentication plugin."),
	
	PERMANENT_POSTAL_ADDRESS(
			"Permanent Address",
			"submit_permanent_postal_address_label",
			"submit_permanent_postal_address_help",
			"submit_permanent_postal_address_enabled",
			"The mailing address where the author is reachable after leaving the institution. This field may be configured to be locked if supplied by the authentication plugin."),
	
	PERMANENT_EMAIL_ADDRESS(
			"Permanent Email",
			"submit_permanent_email_address_label",
			"submit_permanent_email_address_help",
			"submit_permanent_email_address_enabled",
			"The email address where the author is reachable after leaving the institution. This field may be configured to be locked if supplied by the authentication plugin."),

	CURRENT_PHONE_NUMBER(
			"Current Phone",
			"submit_current_phone_number_label",
			"submit_current_phone_number_help",
			"submit_current_phone_number_enabled",
			"The contact phone number where the author is reachable. This field may be configured to be locked if supplied by the authentication plugin."),

	
	CURRENT_POSTAL_ADDRESS(
			"Current Address",
			"submit_current_postal_address_label",
			"submit_current_postal_address_help",
			"submit_current_postal_address_enabled",
			"The mailing address where the author is reachable. This field may be configured to be locked if supplied by the authentication plugin."),

	/** License Agreement **/
			
	LICENSE_AGREEMENT(
			"License Agreement",
			"submit_license_agreement_label",
			"submit_license_agreement_help",
			"submit_license_agreement_enabled",
			"The standard license agreement that the author must agree to before continuing the submission. The text of the license is editable under the Application Settings Tab"),
	
	UMI_RELEASE(
			"UMI Release",
			"submit_umi_release_label",
			"submit_umi_release_help",
			"submit_umi_release_enabled",
			"Request release to the Proquest / UMI database."),
			
			
	/** Document Information **/
			
	DOCUMENT_TITLE(
			"Document Title",
			"submit_document_title_label",
			"submit_document_title_help",
			"submit_document_title_enabled",
			"The title of the document being submitted"),
	
	GRADUATION_DATE(
			"Graduation Date",
			"submit_graduation_date_label",
			"submit_graduation_date_help",
			"submit_graduation_date_enabled",
			"The date the submitter intends to graduate (month and year). The select months are available to choose from, and the year must be within 7 years plus or minus of the current year."),
	
	DEFENSE_DATE(
			"Defense Date",
			"submit_defense_date_label",
			"submit_defense_date_help",
			"submit_defense_date_enabled",
			"The date the submitter's defense."),
			
	DOCUMENT_TYPE(
			"Document Type",
			"submit_document_type_label",
			"submit_document_type_help",
			"submit_document_type_enabled",
			"The type of document being submitted. The options available are determined by the current degree level which is set by selecting a degree type on the first page."),
	
	DOCUMENT_ABSTRACT(
			"Abstract",
			"submit_document_abstract_label",
			"submit_document_abstract_help",
			"submit_document_abstract_enabled",
			"The abstract of the document being submitted."),
	
	DOCUMENT_KEYWORDS(
			"Keywords",
			"submit_document_keywords_label",
			"submit_document_keywords_help",
			"submit_document_keywords_enabled",
			"Any keywords associated with the submission separated by semi-colons."),
			
	DOCUMENT_SUBJECTS(
			"Subjects",
			"submit_document_subjects_label",
			"submit_document_subjects_help",
			"submit_document_subjects_enabled",
			"Up to three ProQuest / UMI subjects. If this field is marked as required only the primary subject will be required the additional subjects will be optional. The list of available subjects is provided by ProQuest and is found in conf/umi_subjects.xls."),
	
	DOCUMENT_LANGUAGE(
			"Language",
			"submit_language_label",
			"submit_language_help",
			"submit_language_enabled",
			"The primary language of the thesis or dissertation. This field is selected from a pre-defined list of languages."),
			
	/** Document Information :: Your Committee **/
			
	COMMITTEE(
			"Committee Members",
			"submit_committee_label",
			"submit_committee_help",
			"submit_committee_enabled",
			"The committee members who approved of this submission."),
	
	COMMITTEE_CONTACT_EMAIL(
			"Committee Contact Email",
			"submit_committee_contact_email_label",
			"submit_committee_contact_email_help",
			"submit_committee_contact_email_enabled",
			"The contact email address for the chair of the submitter's committee. This email address will be sent an email requisting approval of the submission."),
	
	/** Publication Options **/
	
	PUBLISHED_MATERIAL(
			"Published Material",
			"submit_published_material_label",
			"submit_published_material_help",
			"submit_published_material_enabled",
			"Allows the author to identify previously published material which may require additional copyright review. If the author answers positively then they will be required to identify the sections which materials have been previously published."),
		
	EMBARGO_TYPE(
			"Embargo Type",
			"submit_embargo_type_label",
			"submit_embargo_type_help",
			"submit_embargo_type_enabled",
			"Request a delay in publication. The options available are customizable under the Configurable Settings Tab."),	
			
	/** File Upload **/
			
	PRIMARY_ATTACHMENT(
			"Primary Manuscript",
			"submit_primary_attachment_label",
			"submit_primary_attachment_help",
			"submit_primary_attachment_enabled",
			"The primary manuscript, must be a single PDF file."),
	
	SUPPLEMENTAL_ATTACHMENT(
			"Supplemental Files",
			"submit_supplemental_attachment_label",
			"submit_supplemental_attachment_help",
			"submit_supplemental_attachment_enabled",
			"Any number of supplemental files in any format. Supplemental files are typically published with the primary document."),
	
	SOURCE_ATTACHMENT(
			"Source Files",
			"submit_source_attachment_label",
			"submit_source_attachment_help",
			"submit_source_attachment_enabled",
			"Any number of source files in any format. Depending on the destination the source files may be included for preservation purposes."),
	
	ADMINISTRATIVE_ATTACHMENT(
			"Administrative Files",
			"submit_administrative_attachment_label",
			"submit_administrative_attachment_help",
			"submit_administrative_attachment_enabled",
			"Any number of additional files in any format. These administrative files are typically never published with the document and are only available within Vireo.");

	/**
	 * Private internal method to construct a new field configuration
	 * 
	 * @param name
	 * 			  The canonical name of the field.
	 * @param label
	 *            The name of the configuration parameter where this field label
	 *            is stored in the database.
	 * @param help
	 *            The name of the configuration paramater where this field label
	 *            is stored in the database.
	 * @param enabled
	 *            The name of the configuration paramater where this field label
	 *            is stored in the database.
	 * @param note
	 *            An english description of how this field is used for
	 *            administrators.
	 */
	private FieldConfig(String name, String label, String help, String enabled,
			String note) {
		this.name = name;
		this.LABEL = label;
		this.HELP = help;
		this.ENABLED = enabled;
		this.note = note;
	}

	// Name of individual configuration parameters for this field:
	public final String LABEL;
	public final String HELP;
	public final String ENABLED;
	
	// Internal english description of the field
	public final String note;
	public final String name;
	
	/**
	 * @return The canonical name of the field.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return The internal administrative note about this field.
	 */
	public String getNote() {
		return note;
	}

	
	// Groups of fields
	
	public static final FieldConfig[] PERSONAL_INFO_FIELDS = {STUDENT_FIRST_NAME,STUDENT_MIDDLE_NAME,STUDENT_LAST_NAME,STUDENT_EMAIL,STUDENT_ORCID,STUDENT_BIRTH_YEAR,GRANTOR,COLLEGE,PROGRAM,DEPARTMENT,DEGREE,MAJOR,PERMANENT_PHONE_NUMBER,PERMANENT_POSTAL_ADDRESS,PERMANENT_EMAIL_ADDRESS,CURRENT_PHONE_NUMBER,CURRENT_POSTAL_ADDRESS};

	public static final FieldConfig[] LICENSE_AGREEMENT_FIELDS = {LICENSE_AGREEMENT, UMI_RELEASE};
	
	public static final FieldConfig[] DOCUMENT_INFO_FIELDS = {DOCUMENT_TITLE, GRADUATION_DATE, DEFENSE_DATE, DOCUMENT_TYPE, DOCUMENT_ABSTRACT, DOCUMENT_KEYWORDS, DOCUMENT_SUBJECTS, DOCUMENT_LANGUAGE, COMMITTEE, COMMITTEE_CONTACT_EMAIL, PUBLISHED_MATERIAL, EMBARGO_TYPE};

	public static final FieldConfig[] UPLOAD_FILES_FIELDS = {PRIMARY_ATTACHMENT, SUPPLEMENTAL_ATTACHMENT, SOURCE_ATTACHMENT, ADMINISTRATIVE_ATTACHMENT};
}
