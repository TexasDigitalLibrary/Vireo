package org.tdl.vireo.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.REFRESH;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.tdl.vireo.model.validation.SubmissionValidator;
import org.tdl.vireo.service.DefaultSettingsService;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import edu.tamu.framework.SpringContext;
import edu.tamu.framework.model.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "submitter_id", "organization_id" }))
public class Submission extends BaseEntity {

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private final static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    private final static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private final static String COMMA = ", ";
    private final static String HYPHEN = "-";
    private final static String SPACE = " ";
    private final static String NOTHING = "";

    @ManyToOne(optional = false)
    private User submitter;

    @ManyToOne(cascade = { REFRESH }, fetch = EAGER, optional = true)
    private User assignee;

    @ManyToOne(cascade = { REFRESH })
    private SubmissionStatus submissionStatus;

    @ManyToOne(cascade = { REFRESH }, fetch = EAGER, optional = false)
    private Organization organization;

    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private Set<FieldValue> fieldValues;

    @ManyToMany(cascade = { REFRESH }, fetch = EAGER)
    @CollectionTable(uniqueConstraints = @UniqueConstraint(columnNames = { "submission_id", "submission_workflow_steps_id", "submissionWorkflowSteps_order" }))
    @OrderColumn
    private List<SubmissionWorkflowStep> submissionWorkflowSteps;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar approveEmbargoDate;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar submissionDate;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Calendar approvalDate;

    @Column(nullable = true)
    private boolean approveEmbargo;

    @Column(nullable = true)
    private boolean approveApplication;

    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private List<CustomActionValue> customActionValues;

    @JoinColumn
    @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
    private List<ActionLog> actionLogs;

    @Lob
    private String reviewerNotes;

    @Column(nullable = true)
    private String advisorAccessHash;

    @Column(nullable = true)
    private String advisorReviewURL;

    @Column(nullable = true)
    private String depositUri;

    public Submission() {
        setModelValidator(new SubmissionValidator());
        setFieldValues(new HashSet<FieldValue>());
        setSubmissionWorkflowSteps(new ArrayList<SubmissionWorkflowStep>());
        setActionLogs(new ArrayList<ActionLog>());
        setApproveApplication(false);
        setApproveEmbargo(false);
        setCustomActionValues(new ArrayList<CustomActionValue>());
    }

    /**
     * @param submitter
     * @param submissionStatus
     */
    public Submission(User submitter, Organization organization) {
        this();
        setSubmitter(submitter);
        setOrganization(organization);
        generateAdvisorAccessHash();
    }

    /**
     * @param submitter
     * @param submissionStatus
     */
    public Submission(User submitter, Organization organization, SubmissionStatus submissionStatus) {
        this(submitter, organization);
        setSubmissionStatus(submissionStatus);
    }

    /**
     *
     * @return the submitter
     */
    public User getSubmitter() {
        return submitter;
    }

    /**
     *
     * @param submitter
     */
    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    /**
     *
     * @return
     */
    public User getAssignee() {
        return assignee;
    }

    /**
     * @param assignee
     *            the assignee to set
     */
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    /**
     * @return the submissionStatus
     */
    public SubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    /**
     * @param submissionStatus
     *            the submissionStatus to set
     */
    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    /**
     * @return the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * @param organization
     *            the organization to set
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * @return the fieldvalues
     */
    public Set<FieldValue> getFieldValues() {
        return fieldValues;
    }

    /**
     * @param fieldvalues
     *            the fieldvalues to set
     */
    public void setFieldValues(Set<FieldValue> fieldvalues) {
        this.fieldValues = fieldvalues;
    }

    /**
     *
     * @param fieldValue
     */
    public void addFieldValue(FieldValue fieldValue) {
        getFieldValues().add(fieldValue);
    }

    /**
     *
     * @param fieldValue
     */
    public void removeFieldValue(FieldValue fieldValue) {
        getFieldValues().remove(fieldValue);
    }

    /**
     * @return the submissionWorkflowSteps
     */
    public List<SubmissionWorkflowStep> getSubmissionWorkflowSteps() {
        return submissionWorkflowSteps;
    }

    /**
     * @param list
     *            the submissionWorkflowSteps to set
     */
    public void setSubmissionWorkflowSteps(List<SubmissionWorkflowStep> list) {
        this.submissionWorkflowSteps = list;
    }

    /**
     *
     * @param submissionWorkflowStep
     */
    public void addSubmissionWorkflowStep(SubmissionWorkflowStep submissionWorkflowStep) {
        getSubmissionWorkflowSteps().add(submissionWorkflowStep);
    }

    /**
     *
     * @param submissionWorkflowStep
     */
    public void removeSubmissionWorkflowStep(SubmissionWorkflowStep submissionWorkflowStep) {
        getSubmissionWorkflowSteps().remove(submissionWorkflowStep);
    }

    /**
     * @return the submissionDate
     */
    public Calendar getSubmissionDate() {
        return submissionDate;
    }

    /**
     * @param submissionDate
     *            the submissionDate to set
     */
    public void setSubmissionDate(Calendar submissionDate) {
        this.submissionDate = submissionDate;
    }

    /**
     * 
     * @param approveEmbargoDate
     */
    public void setApproveEmbargoDate(Calendar approveEmbargoDate) {
        this.approveEmbargoDate = approveEmbargoDate;
    }

    /**
     * 
     * @return
     */
    public Calendar getApproveEmbargoDate() {
        return approveEmbargoDate;
    }

    /**
     * 
     * @return
     */
    public Calendar getApprovalDate() {
        return approvalDate;
    }

    /**
     * 
     * @param approvalDate
     */
    public void setApprovalDate(Calendar approvalDate) {
        this.approvalDate = approvalDate;
    }

    /**
     * 
     * @return
     */
    public boolean getApproveEmbargo() {
        return approveEmbargo;
    }

    /**
     * 
     * @param approveEmbargo
     */
    public void setApproveEmbargo(boolean approveEmbargo) {
        if (approveEmbargo) {
            this.approveEmbargoDate = Calendar.getInstance();
        } else {
            this.approveEmbargoDate = null;
        }
        this.approveEmbargo = approveEmbargo;
    }

    /**
     * 
     */
    public void clearApproveEmbargo() {
        this.approveEmbargoDate = null;
        this.approveEmbargo = false;
    }

    /**
     * 
     * @return
     */
    public boolean getApproveApplication() {
        return approveApplication;
    }

    /**
     * 
     * @param approveApplication
     */
    public void setApproveApplication(boolean approveApplication) {
        this.approveApplication = approveApplication;
    }

    /**
     * 
     */
    public void clearApproveApplication() {
        this.approvalDate = null;
        this.approveApplication = false;
    }

    /**
     * @return the actionLog
     */
    public List<ActionLog> getActionLogs() {
        return actionLogs;
    }

    /**
     * @param actionLog
     *            the actionLog to set
     */
    public void setActionLogs(List<ActionLog> actionLogs) {
        this.actionLogs = actionLogs;
    }

    /**
     *
     * @param actionLog
     */
    public void addActionLog(ActionLog actionLog) {
        getActionLogs().add(actionLog);
    }

    /**
     *
     * @param actionLog
     */
    public void removeActionLog(ActionLog actionLog) {
        getActionLogs().remove(actionLog);
    }

    /**
     *
     * @return
     */
    public String getReviewerNotes() {
        return reviewerNotes;
    }

    /**
     *
     * @param reviewerNotes
     */
    public void setReviewerNotes(String reviewerNotes) {
        this.reviewerNotes = reviewerNotes;
    }

    /**
     * 
     */
    private void generateAdvisorAccessHash() {
        setAdvisorAccessHash(UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * 
     * @param string
     */
    public void setAdvisorAccessHash(String string) {
        advisorAccessHash = string;
    }

    /**
     * 
     * @return
     */
    public String getAdvisorAccessHash() {
        return advisorAccessHash;
    }

    /**
     * 
     * @return
     */
    public String getDepositUri() {
        return depositUri;
    }

    /**
     * 
     * @param depositUri
     */
    public void setDepositUri(String depositUri) {
        this.depositUri = depositUri;
    }

    /**
     * @return the customActionValues
     */
    public List<CustomActionValue> getCustomActionValues() {
        return customActionValues;
    }

    /**
     * @param customActionValues
     *            the customActionValues to set
     */
    public void setCustomActionValues(List<CustomActionValue> customActionValues) {
        this.customActionValues = customActionValues;
    }

    /**
     * 
     * @param customActionValue
     */
    public void addCustomActionValue(CustomActionValue customActionValue) {
        this.customActionValues.add(customActionValue);
    }

    /**
     *
     * @param customActionValue
     * @return
     */
    public CustomActionValue editCustomActionValue(CustomActionValue customActionValue) {
        for (CustomActionValue cav : this.customActionValues) {
            if (cav.getId().equals(customActionValue.getId())) {
                cav.setDefinition(customActionValue.getDefinition());
                cav.setValue(customActionValue.getValue());
                return cav;
            }
        }
        this.customActionValues.add(customActionValue);
        return customActionValue;
    }

    @JsonIgnore
    public List<FieldValue> getFieldValuesByPredicate(FieldPredicate fieldPredicate) {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        getFieldValues().forEach(fieldValue -> {
            if (fieldValue.getFieldPredicate().equals(fieldPredicate)) {
                fielsValues.add(fieldValue);
            }
        });
        return fielsValues;
    }

    @JsonIgnore
    public List<FieldValue> getFieldValuesByPredicateValue(String predicateValue) {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        getFieldValues().forEach(fieldValue -> {
            if (fieldValue.getFieldPredicate().getValue().equals(predicateValue)) {
                fielsValues.add(fieldValue);
            }
        });
        return fielsValues;
    }

    @JsonIgnore
    public FieldValue getFieldValueByValueAndPredicate(String value, FieldPredicate fieldPredicate) {
        FieldValue foundFieldValue = null;
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getValue().equals(value) && fieldValue.getFieldPredicate().equals(fieldPredicate)) {
                foundFieldValue = fieldValue;
                break;
            }
        }
        return foundFieldValue;
    }

    @JsonIgnore
    public List<FieldValue> getFieldValuesByInputType(InputType inputType) {

        List<FieldValue> fieldValues = new ArrayList<FieldValue>();

        getSubmissionWorkflowSteps().forEach(submissionWorkflowSteps -> {
            submissionWorkflowSteps.getAggregateFieldProfiles().forEach(afp -> {
                if (afp.getInputType().equals(inputType)) {
                    List<FieldValue> foundFieldValues = getFieldValuesByPredicate(afp.getFieldPredicate());
                    fieldValues.addAll(foundFieldValues);
                }
            });
        });

        return fieldValues;
    }

    @JsonIgnore
    public List<FieldValue> getAllDocumentFieldValues() {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getDocumentTypePredicate()) {
                fielsValues.add(fieldValue);
            }
        }
        return fielsValues;
    }

    @JsonIgnore
    public FieldValue getPrimaryDocumentFieldValue() {
        FieldValue primaryDocumentFieldValue = null;
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("_doctype_primary")) {
                primaryDocumentFieldValue = fieldValue;
                break;
            }
        }
        return primaryDocumentFieldValue;
    }

    @JsonIgnore
    public List<FieldValue> getLicenseDocumentFieldValues() {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("_doctype_license")) {
                fielsValues.add(fieldValue);
            }
        }
        return fielsValues;
    }

    @JsonIgnore
    public List<FieldValue> getSupplementalAndSourceDocumentFieldValues() {
        List<FieldValue> fielsValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("_doctype_supplemental") || fieldValue.getFieldPredicate().getValue().equals("_doctype_source")) {
                fielsValues.add(fieldValue);
            }
        }
        return fielsValues;
    }

    @JsonIgnore
    public List<FieldValue> getLicenseAgreementFieldValues() {
        List<FieldValue> fieldValues = new ArrayList<FieldValue>();
        for (FieldValue fieldValue : getFieldValues()) {
            if (fieldValue.getFieldPredicate().getValue().equals("license_agreement")) {
                fieldValues.add(fieldValue);
            }
        }
        return fieldValues;
    }

    @JsonIgnore
    public List<SubmissionFieldProfile> getSubmissionFieldProfilesByInputTypeName(String inputType) {

        List<SubmissionFieldProfile> submissionFieldProfiles = new ArrayList<SubmissionFieldProfile>();

        getSubmissionWorkflowSteps().forEach(submissionWorkflowSteps -> {
            submissionWorkflowSteps.getAggregateFieldProfiles().forEach(afp -> {
                if (afp.getInputType().getName().equals(inputType)) {
                    submissionFieldProfiles.add(afp);
                }
            });
        });

        return submissionFieldProfiles;
    }

    public void generateAdvisorReviewUrl(String baseUrl) {
        this.advisorReviewURL = baseUrl + "/review/" + this.getAdvisorAccessHash();
    }

    public String getAdvisorReviewURL() {
        return advisorReviewURL;
    }

    // convenience methods for exporter templating

    // NOTE: uses hard coded predicate values

    @JsonIgnore
    public String getSubmissionDateString() {
        return submissionDate != null ? dateFormat.format(submissionDate.getTime()) : "";
    }

    @JsonIgnore
    public String getCommitteeEmbargoApprovalDateString() {
        return approveEmbargoDate != null ? dateFormat.format(approveEmbargoDate.getTime()) : "";
    }

    @JsonIgnore
    public String getApprovalDateString() {
        return approvalDate != null ? dateFormat.format(approvalDate.getTime()) : "";
    }

    @JsonIgnore
    public String getGraduationMonthString() throws ParseException {
        Optional<String> graduationMonth = getFieldValueByPredicateValue("dc.date.created");
        return graduationMonth.isPresent() ? dateFormat.format(dateTimeFormat.parse(graduationMonth.get())) : "";
    }

    @JsonIgnore
    public String getEmail() {
        Optional<String> email = getFieldValueByPredicateValue("email");
        return email.isPresent() ? email.get() : "";
    }

    @JsonIgnore
    public String getPermanentEmail() {
        Optional<String> email = getFieldValueByPredicateValue("permanent_email");
        return email.isPresent() ? email.get() : "";
    }

    @JsonIgnore
    public String getSubmissionType() {
        Optional<String> submissionType = getFieldValueByPredicateValue("submission_type");
        return (submissionType.isPresent() ? submissionType.get() : NOTHING);
    }

    @JsonIgnore
    public String getStudentFullNameWithBirthYear() {
        String firstName = getFirstName();
        String middleName = getMiddleName();
        String lastName = getLastName();
        String birthYear = getBirthYear();
        return (lastName.length() > 0 ? lastName + COMMA : NOTHING) + (firstName.length() > 0 ? firstName + SPACE : NOTHING) + (middleName.length() > 0 ? middleName + SPACE : NOTHING) + (birthYear.length() > 0 ? birthYear + HYPHEN : NOTHING);
    }

    @JsonIgnore
    public String getStudentFullName() {
        String firstName = getFirstName();
        String middleName = getMiddleName();
        String lastName = getLastName();
        return (lastName.length() > 0 ? lastName + COMMA : NOTHING) + (firstName.length() > 0 ? firstName + SPACE : NOTHING) + (middleName.length() > 0 ? middleName + SPACE : NOTHING);
    }

    @JsonIgnore
    public String getStudentShortName() {
        String firstName = getFirstName();
        String lastName = getLastName();
        return (firstName.length() > 0 ? firstName + SPACE : NOTHING) + (lastName.length() > 0 ? lastName : NOTHING);
    }

    @JsonIgnore
    public String getFirstName() {
        Optional<String> firstName = getFieldValueByPredicateValue("first_name");
        return firstName.isPresent() ? firstName.get() : "";
    }

    @JsonIgnore
    public String getMiddleName() {
        Optional<String> firstName = getFieldValueByPredicateValue("middle_name");
        return firstName.isPresent() ? firstName.get() : "";
    }

    @JsonIgnore
    public String getLastName() {
        Optional<String> firstName = getFieldValueByPredicateValue("last_name");
        return firstName.isPresent() ? firstName.get() : "";
    }

    @JsonIgnore
    public String getBirthYear() {
        Optional<String> birthYear = getFieldValueByPredicateValue("birth_year");
        return birthYear.isPresent() ? birthYear.get() : "";
    }

    @JsonIgnore
    public String getCurrentPhoneNumber() {
        Optional<String> currentPhone = getFieldValueByPredicateValue("current_phone");
        return currentPhone.isPresent() ? currentPhone.get() : "";
    }

    @JsonIgnore
    public String getPermanentPhoneNumber() {
        Optional<String> permanentPhone = getFieldValueByPredicateValue("permanent_phone");
        return permanentPhone.isPresent() ? permanentPhone.get() : "";
    }

    @JsonIgnore
    public String getPhoneNumber() {
        Optional<String> permanentPhone = getFieldValueByPredicateValue("permanent_phone");
        Optional<String> currentPhone = getFieldValueByPredicateValue("current_phone");
        return currentPhone.isPresent() ? currentPhone.get() : permanentPhone.isPresent() ? permanentPhone.get() : "";
    }

    @JsonIgnore
    public int getCountryCode(String number) throws NumberParseException {
        PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
        return phoneNumber.getCountryCode();
    }

    @JsonIgnore
    public String getAreaCode(String number) throws NumberParseException {
        Optional<String> areaCode = Optional.empty();
        PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
        String nationalSignificantNumber = phoneUtil.getNationalSignificantNumber(phoneNumber);
        int areaCodeLength = phoneUtil.getLengthOfGeographicalAreaCode(phoneNumber);
        if (areaCodeLength > 0) {
            areaCode = Optional.of(nationalSignificantNumber.substring(0, areaCodeLength));
        }
        return areaCode.isPresent() ? areaCode.get() : "";
    }

    @JsonIgnore
    public String getNumber(String number) throws NumberParseException {
        PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
        String fullNumber = phoneUtil.getNationalSignificantNumber(phoneNumber);
        if (fullNumber.length() > 7) {
            fullNumber = fullNumber.substring(3, fullNumber.length());
        }
        return fullNumber;
    }

    @JsonIgnore
    public String getExt(String number) throws NumberParseException {
        PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
        return phoneNumber.getExtension();
    }

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
                                    , Pattern.DOTALL), };

    /**
     * Reverse the string provided.
     */
    private String reverse(String string) {
        return new StringBuilder(string).reverse().toString();
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

        if (matcher.matches()) {
            cntry = reverse(matcher.group(1));
            zip = reverse(matcher.group(2));
            state = reverse(matcher.group(3));
            city = reverse(matcher.group(4));
            addrline = reverse(matcher.group(5));
        }

        return new Address(addrline, "", city, state, zip, cntry);
    }

    @JsonIgnore
    public String getCurrentAddress() {
        Optional<String> currentAddress = getFieldValueByPredicateValue("current_address");
        return currentAddress.isPresent() ? currentAddress.get() : "";
    }

    @JsonIgnore
    public String getPermanentAddress() {
        Optional<String> permanentAddress = getFieldValueByPredicateValue("permanent_address");
        return permanentAddress.isPresent() ? permanentAddress.get() : "";
    }

    @JsonIgnore
    public String getStreet(String address) {
        return addressParsingAlgorithmByScott(address).getAddress1();
    }

    @JsonIgnore
    public String getCity(String address) {
        return addressParsingAlgorithmByScott(address).getCity();
    }

    @JsonIgnore
    public String getState(String address) {
        return addressParsingAlgorithmByScott(address).getState();
    }

    @JsonIgnore
    public String getZip(String address) {
        return addressParsingAlgorithmByScott(address).getPostalCode();
    }

    @JsonIgnore
    public String getCountry(String address) {
        return addressParsingAlgorithmByScott(address).getCountry();
    }

    @JsonIgnore
    public String getDegreeLevel() {
        Optional<String> degreeLevel = getFieldValueIdentifierByPredicateValue("thesis.degree.name");
        return degreeLevel.isPresent() ? degreeLevel.get() : "";
    }

    @JsonIgnore
    public String getExternalId() {
        Long id = getId();
        String lastName = getLastName();
        String externalIdPrefix = getSettingByNameAndType("external_id_prefix", "proquest_umi_degree_code").getValue();
        String institutionCode = getSettingByNameAndType("proquest_institution_code", "proquest_umi_degree_code").getValue();
        return String.join("", institutionCode, externalIdPrefix, String.valueOf(id), lastName);
    }

    @JsonIgnore
    public String getApplyForCopyright() {
        return getSettingByNameAndType("apply_for_copyright", "proquest_umi_degree_code").getValue();
    }

    @JsonIgnore
    public int getEmbargoCode() {

        int embargoCode = 0;

        Optional<FieldValue> proquestEmbargo = getFirstFieldValueByPredicateValue("proquest_embargos");
        Optional<FieldValue> defaultEmbargo = getFirstFieldValueByPredicateValue("default_embargos");

        Optional<FieldValue> embargo = proquestEmbargo.isPresent() ? proquestEmbargo : defaultEmbargo.isPresent() ? defaultEmbargo : Optional.empty();

        if (embargo.isPresent()) {
            String duration = embargo.get().getIdentifier();
            if (duration != null) {
                int d = Integer.valueOf(duration);
                if (d == 0) {
                    embargoCode = 0;
                } else if (d <= 6) {
                    embargoCode = 1;
                } else if (d <= 12) {
                    embargoCode = 2;
                } else {
                    embargoCode = 3;
                }
            } else {
                if (proquestEmbargo.isPresent()) {
                    // proquest flexible delayed release, configured in SYSTEM_Defaults under proquest_umi_degree_code
                    embargoCode = 4;
                } else {
                    // The vireo embargo is tagged as indefinite, so the best we can do with UMI is 2 years.
                    embargoCode = 3;
                }
            }
        }
        return embargoCode;
    }

    @JsonIgnore
    public String getGrantor() {
        String grantor = getSettingByNameAndType("grantor", "application").getValue();
        return grantor != null ? grantor : "";
    }

    @JsonIgnore
    public boolean getReleaseStudentContactInformation() {
        String grantor = getSettingByNameAndType("release_student_contact_information", "export").getValue();
        return grantor != null ? Boolean.valueOf(grantor) : false;
    }

    @JsonIgnore
    public Optional<String> getFieldValueByPredicateValue(String predicateValue) {
        List<FieldValue> fieldValues = getFieldValuesByPredicateValue(predicateValue);
        return fieldValues.size() > 0 ? Optional.of(fieldValues.get(0).getValue()) : Optional.empty();
    }

    @JsonIgnore
    public Optional<String> getFieldValueIdentifierByPredicateValue(String predicateValue) {
        List<FieldValue> fieldValues = getFieldValuesByPredicateValue(predicateValue);
        return fieldValues.size() > 0 && fieldValues.get(0).getIdentifier() != null ? Optional.of(fieldValues.get(0).getIdentifier()) : Optional.empty();
    }

    @JsonIgnore
    public Optional<FieldValue> getFirstFieldValueByPredicateValue(String predicateValue) {
        List<FieldValue> fieldValues = getFieldValuesByPredicateValue(predicateValue);
        return fieldValues.size() > 0 ? Optional.of(fieldValues.get(0)) : Optional.empty();
    }

    // NOTE: used context to get the default settings service

    @JsonIgnore
    public DefaultConfiguration getSettingByNameAndType(String name, String type) {
        DefaultSettingsService defaultSettingsService = SpringContext.bean(DefaultSettingsService.class);
        return defaultSettingsService.getSettingByNameAndType(name, type);
    }

}
