package org.tdl.vireo.utility;

import static java.lang.String.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tdl.vireo.model.Address;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.service.ProquestCodesService;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.tupilabs.human_name_parser.HumanNameParserParser;

import edu.tamu.weaver.context.SpringContext;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmissionHelperUtility {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionHelperUtility.class);

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private final static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    private final static SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
    private final static SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy");

    private final static SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");

    private final static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private final static String COMMA = ", ";
    private final static String HYPHEN = "-";
    private final static String SPACE = " ";
    private final static String NOTHING = "";

    // Patterns for Scott's Address Parse Algorithm
    private static final Pattern[] patterns = {
                    // Plan A: State and city are all separated with either a comma or new line and
                    Pattern.compile("^([^\\d]*?)[\\s\\n,]*" + // Country (optional)
                                    "([\\d-#]+)[\\s\\n,]+" + // Zip code (required)
                                    "([^,\\n]+?)\\s*[\\n,]+\\s*" + // State (required)
                                    "([^,\\n]+?)\\s*[\\n,]+\\s*" + // City (required)
                                    "(.+)$" // Address lines (required)
                                    , Pattern.DOTALL),

                    // Plan B: allow state & city to be separated by a space
                    Pattern.compile("^([^\\d]*?)[\\s\\n,]*" + // Country (optional)
                                    "([\\d-#]+)[\\s\\n,]+" + // Zip code (required)
                                    "([^,\\n]+?)[\\s\\n,]+" + // State (required)
                                    "([^,\\n]+?)\\s*[\\n,]+\\s*" + // City (required)
                                    "(.+)$" // Address lines (required)
                                    , Pattern.DOTALL),

                    // Plan C: allow everything to be separated by a space
                    Pattern.compile("^([^\\d]*?)[\\s\\n,]*" + // Country (optional)
                                    "([\\d-#]+)[\\s\\n,]+" + // Zip code (required)
                                    "([^,\\n]+?)[\\s\\n,]+" + // State (required)
                                    "([^,\\n]+?)[\\s\\n,]+" + // City (required)
                                    "(.+)$" // Address lines (required)
                                    , Pattern.DOTALL) };

    public static final Map<String, String> CAT = new HashMap<String, String>();
    static {
        CAT.put("application/word", "text");
        CAT.put("application/msword", "text");
        CAT.put("application/x-latex", "text");
        CAT.put("application/postscript", "text");
        CAT.put("application/x-tex", "text");
        CAT.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text");

        CAT.put("application/postscript", "image");

        CAT.put("text/html", "webpage");
        CAT.put("text/xhtml", "webpage");
        CAT.put("text/css", "webpage");
        CAT.put("text/javascript", "webpage");
        CAT.put("application/x-javascript", "webpage");
        CAT.put("application/xhtml", "webpage");
        CAT.put("application/xhtml+xml", "webpage");

        CAT.put("text/xml", "data");
        CAT.put("application/excel", "data");
        CAT.put("application/msexcel", "data");
        CAT.put("application/vnd.ms-excel", "data");
        CAT.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "data");
        CAT.put("application/vnd.openxmlformats-officedocument.spreadsheetml.template", "data");

        CAT.put("application/powerpoint", "presentation");
        CAT.put("application/ms-powerpoint", "presentation");
        CAT.put("application/vnd.ms-powerpoint", "presentation");
        CAT.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "presentation");

        CAT.put("text/xslt", "code/script");

        CAT.put("application/pdf", "pdf");
        CAT.put("application/x-pdf", "pdf");
        CAT.put("application/acrobat", "pdf");
        CAT.put("applications/vnd.pdf", "pdf");
        CAT.put("text/pdf", "pdf");
        CAT.put("text/x-pdf", "pdf");
    }

    private final Submission submission;

    public SubmissionHelperUtility(Submission submission) {
        this.submission = submission;
    }

    public Submission getSubmission() {
        return submission;
    }

    /**
     * Scott Phillips's address parsing algorithm.
     *
     * This algorithm works by searching backwards. Starting at the end of the address identify the zip code. Once you have that assume anything following the zip code is the country, and then the two tokens preceding the zip code are the city and state. We do this by performing a series of regular expressions on a reverse address string. The difference between the regular expressions is how linent they are for extracting the city and state. The first one in the list demands that city and state
     * are either separated by a new line or a comma. Each of the next versions back off of this by allowing spaces between these tokens. This sometimes breaks multi-word cities or state, but sometimes people just don't supply a city.
     *
     * This algorithm works on most international and American addresses. However it will sometimes miss identify components like getting the city or state wrong. It will often not identify the country if it is specified before the zip code.
     *
     *
     * @param fullAddress
     *            The full address
     * @return An address object if the parse was successful, otherwise return null.
     */
    private Address addressParsingAlgorithmByScott(String fullAddress) {
        // The address parts that we are trying to extract
        String addrline = "";
        String city = "";
        String state = "";
        String zip = "";
        String cntry = "";

        String reverseAddress = reverse(fullAddress);

        Matcher matcher = null;
        for (Pattern pattern : patterns) {
            matcher = pattern.matcher(reverseAddress);
            if (matcher.matches()) {
                break;
            }
        }

        if (matcher != null && matcher.matches()) {
            cntry = reverse(matcher.group(1));
            zip = reverse(matcher.group(2));
            state = reverse(matcher.group(3));
            city = reverse(matcher.group(4));
            addrline = reverse(matcher.group(5));
        }

        return new Address(addrline, "", city, state, zip, cntry);
    }

    /**
     * Reverse the string provided.
     */
    private String reverse(String string) {
        return new StringBuilder(string).reverse().toString();
    }

    public String categorize(String mimeType) {

        String category = "other";

        // Start with the simple stuff first
        if (mimeType.startsWith("audio/"))
            category = "audio";

        if (mimeType.startsWith("image/"))
            category = "image";

        if (mimeType.startsWith("text/"))
            category = "text";

        if (mimeType.startsWith("video/"))
            category = "video";

        if (CAT.containsKey(mimeType))
            category = CAT.get(mimeType);

        return category;
    }

    // NOTE: these come from the submission

    public String getSubmissionDateString() {
        return submission.getSubmissionDate() != null ? dateFormat.format(submission.getSubmissionDate().getTime()) : "";
    }

    public String getEmbargoApprovalDateString() {
        return submission.getApproveEmbargoDate() != null ? dateFormat.format(submission.getApproveEmbargoDate().getTime()) : "";
    }

    public String getFormattedCommitteeApprovedEmbargoLiftDateString() {
        Calendar appEmbDate = submission.getApproveEmbargoDate();
        if(appEmbDate==null){
            return "";
        }
        Optional<FieldValue> defaultEmbargo = getFirstFieldValueByPredicateValue("default_embargos");
        int monthIncr = 0;
        if(defaultEmbargo.isPresent()){
            String defEmbStr = defaultEmbargo.get().getValue();
            if(defEmbStr==null){
            }else if(defEmbStr.equals("None")){
            }else if(defEmbStr.equals("Journal Hold")){
                monthIncr = 12;
            }else if(defEmbStr.equals("Patent Hold")){
                monthIncr = 24;
            }else if(defEmbStr.equals("Other Embargo Period")){
                monthIncr = 24;//max?
            }
        }
        if(monthIncr>0){
            appEmbDate.add(Calendar.MONTH,monthIncr);
        }
        return dateFormat.format(appEmbDate.getTime());
    }

    public String getAdvisorApprovalDateString() {
        return submission.getApproveAdvisorDate() != null ? dateFormat.format(submission.getApproveAdvisorDate().getTime()) : "";
    }

    public String getApproveApplicationDate() {
        return submission.getApproveApplicationDate() != null ? dateFormat.format(submission.getApproveApplicationDate().getTime()) : "";
    }

    public String getUserOrcid() {
        //return submission.getSubmitter() != null ? submission.getSubmitter().getOrcid() : "";
        Optional<String> orcid = getFieldValueByPredicateValue("dc.identifier.orcid");
        return orcid.isPresent() ? orcid.get() : "";
    }

    public List<FieldValue> getLicenseAgreementFieldValues() {
        List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : submission.getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("license_agreement")) {
                fieldValues.add(fieldValue);
            }
        }
        return fieldValues;
    }

    public Optional<String> getFieldValueByPredicateValue(String predicateValue) {
        List<FieldValue> fieldValues = submission.getFieldValuesByPredicateValue(predicateValue);
        return fieldValues.size() > 0 ? Optional.of(fieldValues.get(0).getValue()) : Optional.empty();
    }

    public Optional<String> getFieldValueIdentifierByPredicateValue(String predicateValue) {
        List<FieldValue> fieldValues = submission.getFieldValuesByPredicateValue(predicateValue);
        return fieldValues.size() > 0 && fieldValues.get(0).getIdentifier() != null ? Optional.of(fieldValues.get(0).getIdentifier()) : Optional.empty();
    }

    public Optional<String> getFieldValueDefinitionByPredicateValue(String predicateValue) {
        List<FieldValue> fieldValues = submission.getFieldValuesByPredicateValue(predicateValue);
        return fieldValues.size() > 0 && fieldValues.get(0).getDefinition() != null ? Optional.of(fieldValues.get(0).getDefinition()) : Optional.empty();
    }

    public Optional<FieldValue> getFirstFieldValueByPredicateValue(String predicateValue) {
        List<FieldValue> fieldValues = submission.getFieldValuesByPredicateValue(predicateValue);
        return fieldValues.size() > 0 ? Optional.of(fieldValues.get(0)) : Optional.empty();
    }

    // NOTE: uses hard coded predicate values
    public String getGraduationDateString() {
        Optional<String> graduationDate = getFieldValueByPredicateValue("dc.date.issued");
        String date = "";
        if (graduationDate.isPresent()) {
            try {
                date = dateFormat.format(monthYearFormat.parse(graduationDate.get()));
            } catch (NumberFormatException | ParseException e) {
                logException(e, format("Failed to format graduation date %s for submission with id %s", graduationDate.get(), submission.getId()));
            }
        }
        return date;
    }



    // NOTE: uses hard coded predicate values
    public String getGraduationYearString() {
        Optional<String> graduationYear = getFieldValueByPredicateValue("dc.date.issued");
        String year = "";
        if (graduationYear.isPresent()) {
            try {
                year = yearFormat.format(monthYearFormat.parse(graduationYear.get()));
            } catch (NumberFormatException | ParseException e) {
                logException(e, format("Failed to format graduation year %s for submission with id %s", graduationYear.get(), submission.getId()));
            }
        }
        return year;
    }

    // NOTE: uses hard coded predicate values
    public String getGraduationYearMonthString() {
        Optional<String> graduationYearMonth = getFieldValueByPredicateValue("dc.date.issued");
        String yearMonth = "";
        if (graduationYearMonth.isPresent()) {
            try {
                yearMonth = yearMonthFormat.format(monthYearFormat.parse(graduationYearMonth.get()));
            } catch (NumberFormatException | ParseException e) {
                logException(e, format("Failed to format graduation year month %s for submission with id %s", graduationYearMonth.get(), submission.getId()));
            }
        }
        return yearMonth;
    }

    // NOTE: uses hard coded predicate values
    public String getGraduationMonthYearString() {
        Optional<String> graduationMonthYear = getFieldValueByPredicateValue("dc.date.issued");
        return graduationMonthYear.isPresent() ? graduationMonthYear.get() : "";
    }

    public String getSubmitterEmail() {
        Optional<String> email = getFieldValueByPredicateValue("email");
        return email.isPresent() ? email.get() : "";
    }

    public String getSubmitterPermanentEmail() {
        Optional<String> email = getFieldValueByPredicateValue("permanent_email");
        return email.isPresent() ? email.get() : "";
    }

    public String getSubmissionType() {
        Optional<String> submissionType = getFieldValueByPredicateValue("submission_type");
        return (submissionType.isPresent() ? submissionType.get() : NOTHING);
    }

    public String getStudentFullNameWithBirthYear() {
        String firstName = getSubmitterFirstName();
        String middleName = getSubmitterMiddleName();
        String lastName = getSubmitterLastName();
        String birthYear = getBirthYear();
        return (lastName.length() > 0 ? lastName + COMMA : NOTHING) + (firstName.length() > 0 ? firstName + SPACE : NOTHING) + (middleName.length() > 0 ? middleName + SPACE : NOTHING) + (birthYear.length() > 0 ? birthYear + HYPHEN : NOTHING);
    }

    public String getStudentFullName() {
        String firstName = getSubmitterFirstName();
        String middleName = getSubmitterMiddleName();
        String lastName = getSubmitterLastName();
        return (lastName.length() > 0 ? lastName + COMMA : NOTHING) + (firstName.length() > 0 ? firstName + SPACE : NOTHING) + (middleName.length() > 0 ? middleName + SPACE : NOTHING);
    }

    public String getStudentShortName() {
        String firstName = getSubmitterFirstName();
        String lastName = getSubmitterLastName();
        return (firstName.length() > 0 ? firstName + SPACE : NOTHING) + (lastName.length() > 0 ? lastName : NOTHING);
    }

    public String getSubmitterFirstName() {
        Optional<String> firstName = getFieldValueByPredicateValue("first_name");
        return firstName.isPresent() ? firstName.get() : "";
    }

    public String getSubmitterMiddleName() {
        Optional<String> firstName = getFieldValueByPredicateValue("middle_name");
        return firstName.isPresent() ? firstName.get() : "";
    }

    public String getSubmitterLastName() {
        Optional<String> firstName = getFieldValueByPredicateValue("last_name");
        return firstName.isPresent() ? firstName.get() : "";
    }

    public String getBirthYear() {
        Optional<String> birthYear = getFieldValueByPredicateValue("birth_year");
        return birthYear.isPresent() ? birthYear.get() : "";
    }

    public String getSubmitterCurrentPhoneNumber() {
        Optional<String> currentPhone = getFieldValueByPredicateValue("current_phone");
        return currentPhone.isPresent() ? currentPhone.get() : "";
    }

    public String getSubmitterPermanentPhoneNumber() {
        Optional<String> permanentPhone = getFieldValueByPredicateValue("permanent_phone");
        return permanentPhone.isPresent() ? permanentPhone.get() : "";
    }

    public String getPhoneNumber() {
        Optional<String> permanentPhone = getFieldValueByPredicateValue("permanent_phone");
        Optional<String> currentPhone = getFieldValueByPredicateValue("current_phone");
        return currentPhone.isPresent() ? currentPhone.get() : permanentPhone.isPresent() ? permanentPhone.get() : "";
    }

    public String getCountryCode(String number) {
        String code = "";
        PhoneNumber phoneNumber;
        try {
            phoneNumber = phoneUtil.parse(number, "US");
            code = String.valueOf(phoneNumber.getCountryCode());
        } catch (NumberParseException e) {
            logException(e, format("Failed to parse country code from phone number %s for submission with id %s", number, submission.getId()));
        }
        return code;
    }

    public String getAreaCode(String number) {
        Optional<String> areaCode = Optional.empty();
        try {
            PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
            String nationalSignificantNumber = phoneUtil.getNationalSignificantNumber(phoneNumber);
            int areaCodeLength = phoneUtil.getLengthOfGeographicalAreaCode(phoneNumber);
            if (areaCodeLength > 0) {
                areaCode = Optional.of(nationalSignificantNumber.substring(0, areaCodeLength));
            }
        } catch (NumberParseException e) {
            logException(e, format("Failed to parse area code from phone number %s for submission with id %s", number, submission.getId()));
        }
        return areaCode.isPresent() ? areaCode.get() : "";
    }

    public String getNumber(String number) {
        String fullNumber = "";
        try {
            PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
            fullNumber = phoneUtil.getNationalSignificantNumber(phoneNumber);
            if (fullNumber.length() > 7) {
                fullNumber = fullNumber.substring(3, fullNumber.length());
            }
        } catch (NumberParseException e) {
            logException(e, format("Failed to parse phone number %s for submission with id %s", number, submission.getId()));
        }

        return fullNumber;
    }

    public String getExt(String number) {
        String ext = "";
        try {
            PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
            ext = phoneNumber.getExtension();
        } catch (NumberParseException e) {
            logException(e, format("Failed to parse extension for phone number %s for submission with id %s", number, submission.getId()));
        }
        return ext;
    }

    public String getSubmitterCurrentAddress() {
        Optional<String> currentAddress = getFieldValueByPredicateValue("current_address");
        return currentAddress.isPresent() ? currentAddress.get() : "";
    }

    public String getSubmitterPermanentAddress() {
        Optional<String> permanentAddress = getFieldValueByPredicateValue("permanent_address");
        return permanentAddress.isPresent() ? permanentAddress.get() : "";
    }

    public String getMajor() {
        Optional<String> major = getFieldValueByPredicateValue("thesis.degree.major");
        return major.isPresent() ? major.get() : "";
    }

    public String getStreet(String address) {
        return addressParsingAlgorithmByScott(address).getAddress1();
    }

    public String getCity(String address) {
        return addressParsingAlgorithmByScott(address).getCity();
    }

    public String getState(String address) {
        return addressParsingAlgorithmByScott(address).getState();
    }

    public String getZip(String address) {
        return addressParsingAlgorithmByScott(address).getPostalCode();
    }

    public String getCountry(String address) {
        return addressParsingAlgorithmByScott(address).getCountry();
    }

    public String getDepartment() {
        Optional<String> department = getFieldValueByPredicateValue("thesis.degree.department");
        return department.isPresent() ? department.get() : "";
    }

    public String getAbstract() {
        Optional<String> descAbstract = getFieldValueByPredicateValue("dc.description.abstract");
        return descAbstract.isPresent() ? descAbstract.get() : "";
    }

    public String[] getAbstractLines() {
        String descAbstract = getAbstract();
        return descAbstract.length() > 0 ? descAbstract.split("\n") : new String[] {};
    }

    public String getDegreeName() {
        Optional<String> degreeName = getFieldValueByPredicateValue("thesis.degree.name");
        return degreeName.isPresent() ? degreeName.get() : "";
    }

    public String getDegreeLevel() {
        Optional<String> degreeLevel = getFieldValueIdentifierByPredicateValue("thesis.degree.name");
        return degreeLevel.isPresent() ? degreeLevel.get() : "";
    }

    public String getDegreeCodeLevelStr() {
        Optional<String> degreeCode = getFieldValueDefinitionByPredicateValue("thesis.degree.name");
        return degreeCode.isPresent() ? degreeCode.get().toLowerCase() : "";
    }

    public String getDegreeCodeStr() {
        Optional<String> degreeCode = getFieldValueDefinitionByPredicateValue("thesis.degree.name");
        return degreeCode.isPresent() ? degreeCode.get() : "";
    }

    public String getDegreeCodeProc() {
        Optional<String> degreeCodeProc = getFieldValueIdentifierByPredicateValue("thesis.degree.name");
        return degreeCodeProc.isPresent() ? degreeCodeProc.get().toUpperCase().substring(0,1) : "";
    }

    public String getDegreeCollege() {
        Optional<String> degreeCollege = getFieldValueByPredicateValue("thesis.degree.college");
        return degreeCollege.isPresent() ? degreeCollege.get() : "";
    }

    public String getDegreeSchool() {
        Optional<String> degreeSchool = getFieldValueByPredicateValue("thesis.degree.school");
        return degreeSchool.isPresent() ? degreeSchool.get() : "";
    }

    public String getDegreeProgram() {
        Optional<String> degreeProgram = getFieldValueByPredicateValue("thesis.degree.program");
        return degreeProgram.isPresent() ? degreeProgram.get() : "";
    }

    public String getTitle() {
        Optional<String> title = getFieldValueByPredicateValue("dc.title");
        return title.isPresent() ? title.get() : "";
    }

    public List<FieldValue> getCommitteeChairFieldValues() {
        return submission.getFieldValuesByPredicateValueStartsWith("dc.contributor.advisor");
    }

    public String getFirstName(String name) {
        try {
            HumanNameParserParser parser = new HumanNameParserParser(name);
            return parser.getFirst();
        } catch (com.tupilabs.human_name_parser.ParseException e) {
            return (name!=null) ? name : "";
        }
    }

    public String getMiddleName(String name) {
        try {
            HumanNameParserParser parser = new HumanNameParserParser(name);
            return parser.getMiddle();
        } catch (com.tupilabs.human_name_parser.ParseException e) {
            return (name!=null) ? name : "";
        }
    }

    public String getLastName(String name) {
        try {
            HumanNameParserParser parser = new HumanNameParserParser(name);
            return parser.getLast();
        } catch (com.tupilabs.human_name_parser.ParseException e) {
            return (name!=null) ? name : "";
        }
    }

    public List<FieldValue> getSubjectFieldValues() {
        return submission.getFieldValuesByPredicateValue("dc.subject");
    }

    public List<FieldValue> getKeywordFieldValues() {
        return submission.getFieldValuesByPredicateValue("keywords");
    }

    public List<FieldValue> getCommitteeMemberFieldValues() {
        return submission.getFieldValuesByPredicateValueStartsWith("dc.contributor.committeeMember");
    }

    public String getLanguageProQuestCode() {
        Optional<String> language = getFieldValueByPredicateValue("dc.language.iso");
        Optional<String> languageProQuestCode = language.isPresent() ? getProQuestCodeByNameAndType(language.get(), "languages") : Optional.empty();
        return languageProQuestCode.isPresent() ? languageProQuestCode.get() : "";
    }

    public String getDegreeProQuestCode() {
        Optional<String> degree = getFieldValueByPredicateValue("thesis.degree.name");
        Optional<String> degreeProQuestCode = degree.isPresent() ? getProQuestCodeByNameAndType(degree.get(), "degrees") : Optional.empty();
        return degreeProQuestCode.isPresent() ? degreeProQuestCode.get() : "";
    }

    public boolean isProQuestSubject(FieldValue subjectFieldValue) {
        return subjectFieldValue.getIdentifier() != null && subjectFieldValue.getIdentifier().length() > 0;
    }

    public int getEmbargoCode() {
        int embargoCode = 0;
        Optional<FieldValue> proquestEmbargo = getFirstFieldValueByPredicateValue("proquest_embargos");
        if (proquestEmbargo.isPresent()) {
          String fv_identifier = proquestEmbargo.get().getIdentifier();
          if(fv_identifier.equals("0")){
            embargoCode = 0;
          }else if(fv_identifier.equals("6")){
            embargoCode = 1;
          }else if(fv_identifier.equals("12")){
            embargoCode = 2;
          }else if(fv_identifier.equals("24")){
            embargoCode = 3;
          }else{
            embargoCode = 4;
          }
        }
        return embargoCode;
    }

    // NOTE: these come from the settings service


    public String getGrantor() {
      return getSettingByNameAndType("grantor","application").getValue();
    }

    public boolean getReleaseStudentContactInformation() {
        String grantor = getSettingByNameAndType("release_student_contact_information", "export").getValue();
        return grantor != null ? Boolean.valueOf(grantor) : false;
    }

    public String getProQuestExternalId() {
        Long id = submission.getId();
        String lastName = getSubmitterLastName();
        String externalIdPrefix = getSettingByNameAndType("external_id_prefix", "proquest_umi_degree_code").getValue();
        String institutionCode = getProQuestInstitutionCode();
        return String.join("", institutionCode, externalIdPrefix, String.valueOf(id), lastName);
    }

    public String getProQuestIndexing() {
        return getSettingByNameAndType("proquest_indexing", "proquest_umi_degree_code").getValue();
    }

    public String getProQuestApplyForCopyright() {
        return getSettingByNameAndType("apply_for_copyright", "proquest_umi_degree_code").getValue();
    }

    public String getProQuestSaleRestrictionCode() {
        return getSettingByNameAndType("sale_restriction_code", "proquest_umi_degree_code").getValue();
    }

    public String getProQuestSaleRestrictionRemove() {
        return getSettingByNameAndType("sale_restriction_remove", "proquest_umi_degree_code").getValue();
    }

    public String getProQuestFormatRestrictionCode() {
        return getSettingByNameAndType("format_restriction_code", "proquest_umi_degree_code").getValue();
    }

    public String getEmbargoLiftDate() {
        String embargoLiftDateStr = null;
        Optional<String> dateIssued = getFieldValueByPredicateValue("dc.date.issued");
        if(dateIssued.isPresent()){
            String dateIssuedStr = dateIssued.get();

            Optional<FieldValue> proquestEmbargo = getFirstFieldValueByPredicateValue("proquest_embargos");
            Optional<FieldValue> defaultEmbargo = getFirstFieldValueByPredicateValue("default_embargos");

            Optional<FieldValue> embargo = Optional.empty();

            Boolean proquestEmbargoCheck = proquestEmbargo.isPresent() && proquestEmbargo.get().getIdentifier() != null && Integer.valueOf(proquestEmbargo.get().getIdentifier()) > 0;
            Boolean defaultEmbargoCheck = defaultEmbargo.isPresent() && defaultEmbargo.get().getIdentifier() != null && Integer.valueOf(defaultEmbargo.get().getIdentifier()) > 0;

            if (proquestEmbargoCheck && defaultEmbargoCheck) {
                embargo = Integer.valueOf(proquestEmbargo.get().getIdentifier()) >= Integer.valueOf(defaultEmbargo.get().getIdentifier())
                    ? proquestEmbargo
                    : defaultEmbargo;
            } else if (proquestEmbargoCheck) {
                embargo = proquestEmbargo;
            } else if (defaultEmbargoCheck) {
                embargo = defaultEmbargo;
            }

            if (embargo.isPresent()) {
                String embargoDuration = embargo.get().getIdentifier();
                if (embargoDuration != null && embargoDuration.length() > 0) {
                    int duration = Integer.valueOf(embargoDuration);
                    try {
                        java.util.Date embargoLiftDate = DateUtils.addMonths(monthYearFormat.parse(dateIssuedStr), duration);
                        embargoLiftDateStr = iso8601Format.format(embargoLiftDate);
                    } catch (ParseException e) {
                        logException(e, format("Failed to format embargo lift date from duration %s for submission with id %s", duration, submission.getId()));
                    }
                }
            }
        }
        return embargoLiftDateStr;
    }

    public String getProQuestFormatRestrictionRemove() {
        String proquestLiftDateStr = "";
        Optional<String> dateIssued = getFieldValueByPredicateValue("dc.date.issued");
        if(dateIssued.isPresent()){
          String dateIssuedStr = dateIssued.get();
          Optional<FieldValue> proquestEmbargo = getFirstFieldValueByPredicateValue("proquest_embargos");
          if (proquestEmbargo.isPresent()) {
            String proquestDuration = proquestEmbargo.get().getIdentifier();
            if ((proquestDuration != null)&&(proquestDuration.length() > 0)) {
                int d = Integer.valueOf(proquestDuration);
                try {
                  java.util.Date proquestLiftDate = DateUtils.addMonths(monthYearFormat.parse(dateIssuedStr),d);
                  proquestLiftDateStr = dateFormat.format(proquestLiftDate);
                } catch (ParseException e) {
                    logException(e, format("Failed to format proquest lift date from duration %s for submission with id %s", d, submission.getId()));
                }
            }
          }
        }
        return proquestLiftDateStr;
    }

    public String getProQuestInstitutionCode() {
        return getSettingByNameAndType("proquest_institution_code", "proquest_umi_degree_code").getValue();
    }

    // NOTE: used context to get the default settings service

    public Configuration getSettingByNameAndType(String name, String type) {
        ConfigurationRepo configurationRepo = SpringContext.bean(ConfigurationRepo.class);
        return configurationRepo.getByNameAndType(name, type);
    }

    public Optional<String> getProQuestCodeByNameAndType(String name, String type) {
        ProquestCodesService proquestCodesService = SpringContext.bean(ProquestCodesService.class);
        Map<String, String> codes = proquestCodesService.getCodes(type);
        Optional<String> code = Optional.empty();
        for (Map.Entry<String, String> entry : codes.entrySet()) {
            String proquestCode = entry.getKey();
            String description = entry.getValue();
            if (description.equals(name)) {
                code = Optional.of(proquestCode);
                break;
            }
        }
        return code;
    }

    private void logException(Exception e, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, e);
        } else {
            logger.info(message);
        }
    }

}
