package org.tdl.vireo.model.formatter;

import java.util.HashMap;

import javax.persistence.Entity;

import org.tdl.vireo.ApplicationConstants;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.enums.ProQuestUMIKey;
import org.thymeleaf.context.Context;

@Entity
public class ProQuestUmiFormatter extends AbstractFormatter {

    public ProQuestUmiFormatter() {
        super();
        setName("ProQuestUMI");
        HashMap<String, String> templates = new HashMap<String, String>();
        templates.put("proquest_umi.xml", "proquest_umi");
        setTemplates(templates);
    }

    @Override
    public void populateContext(Context context, Submission submission) {
        // NOTE: !important to get common export values
        super.populateContext(context, submission);
        // TODO: in order to use mappings from an organization for this export,
        // the methods from the submission helper utility would have to be brought
        // the exporter and extract predicate values from the mapping to define
        // the value to be templated with the given key
        for (ProQuestUMIKey key : ProQuestUMIKey.values()) {
            switch (key) {
            case AGENT:
                context.setVariable(key.name(), "Vireo ProQuest UMI packager");
                break;
            case EMBARGO_CODE:
                context.setVariable(key.name(), submissionHelperUtility.getEmbargoCode());
                break;
            case SUBMITTER_LAST_NAME:
                context.setVariable(key.name(), submissionHelperUtility.getSubmitterLastName());
                break;
            case SUBMITTER_FIRST_NAME:
                context.setVariable(key.name(), submissionHelperUtility.getSubmitterFirstName());
                break;
            case SUBMITTER_MIDDLE_NAME:
                context.setVariable(key.name(), submissionHelperUtility.getSubmitterMiddleName());
                break;
            case SUBMITTER_CURRENT_PHONE_NUMBER:
                context.setVariable(key.name(), submissionHelperUtility.getSubmitterCurrentPhoneNumber());
                break;
            case SUBMITTER_CURRENT_ADDRESS:
                context.setVariable(key.name(), submissionHelperUtility.getSubmitterCurrentAddress());
                break;
            case SUBMITTER_EMAIL:
                context.setVariable(key.name(), submissionHelperUtility.getSubmitterEmail());
                break;
            case SUBMITTER_GRADUATION_DATE:
                context.setVariable(key.name(), submissionHelperUtility.getGraduationDateString());
                break;
            case SUBMITTER_GRADUATION_YEAR:
                context.setVariable(key.name(), submissionHelperUtility.getGraduationYearString());
                break;
            case SUBMITTER_PERMANENT_PHONE_NUMBER:
                context.setVariable(key.name(), submissionHelperUtility.getSubmitterPermanentPhoneNumber());
                break;
            case SUBMITTER_PERMANENT_ADDRESS:
                context.setVariable(key.name(), submissionHelperUtility.getSubmitterPermanentAddress());
                break;
            case SUBMITTER_PERMANENT_EMAIL:
                context.setVariable(key.name(), submissionHelperUtility.getSubmitterPermanentEmail());
                break;
            case PROQUEST_DEGREE_CODE:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeProQuestCode());
                break;
            case PROQUEST_LANGUAGE_CODE:
                context.setVariable(key.name(), submissionHelperUtility.getLanguageProQuestCode());
                break;
            case DEGREE_LEVEL:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeLevel());
                break;
            case DEGREE_LEVEL_STR:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeCodeLevelStr());
                break;
            case DEGREE_CODE_STR:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeCodeStr());
                break;
            case DEGREE_LEVEL_PQ_PROCCODE:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeCodeProc());
                break;
            case DEPARTMENT:
                context.setVariable(key.name(), submissionHelperUtility.getDepartment());
                break;
            case TITLE:
                context.setVariable(key.name(), submissionHelperUtility.getTitle());
                break;
            case COMMITTEE_CHAIR_FIELD_VALUES:
                context.setVariable(key.name(), submissionHelperUtility.getCommitteeChairFieldValues());
                break;
            case COMMITTEE_MEMBER_FIELD_VALUES:
                context.setVariable(key.name(), submissionHelperUtility.getCommitteeMemberFieldValues());
                break;
            case SUBJECT_FIELD_VALUES:
                context.setVariable(key.name(), submissionHelperUtility.getSubjectFieldValues());
                break;
            case KEYWORD_FIELD_VALUES:
                context.setVariable(key.name(), submissionHelperUtility.getKeywordFieldValues());
                break;
            case ABSTRACT:
                context.setVariable(key.name(), submissionHelperUtility.getAbstract());
                break;
            case ABSTRACT_LINES:
                context.setVariable(key.name(), submissionHelperUtility.getAbstractLines());
                break;
            case PRIMARY_DOCUMENT_MIMETYPE:
                String primaryDocumentType = "Other";
                FieldValue primaryDocumentFieldValue = submission.getPrimaryDocumentFieldValue();
                if (primaryDocumentFieldValue != null) {
                    primaryDocumentType = fileHelperUtility.getMimeTypeOfAsset(primaryDocumentFieldValue.getValue());
                    if (primaryDocumentType.equals("application/pdf")) {
                        primaryDocumentType = "PDF";
                    }
                }
                context.setVariable(key.name(), primaryDocumentType);
                break;
            case PRIMARY_DOCUMENT_FIELD_VALUE:
                context.setVariable(key.name(), submission.getPrimaryDocumentFieldValue());
                break;
            case SUPPLEMENTAL_DOCUMENT_FIELD_VALUES:
                context.setVariable(key.name(), submission.getSupplementalDocumentFieldValues());
                break;
            case PROQUEST_PERSON_FILENAME:
                String lastName = submissionHelperUtility.getSubmitterLastName().trim().isEmpty()
                    ? ApplicationConstants.UNKNOWN : submissionHelperUtility.getSubmitterLastName().trim();
                lastName = lastName.substring(0,1).toUpperCase()+lastName.substring(1);
                String firstName = submissionHelperUtility.getSubmitterFirstName().trim().isEmpty()
                    ? ApplicationConstants.UNKNOWN : submissionHelperUtility.getSubmitterFirstName().trim();
                firstName = firstName.substring(0,1).toUpperCase()+firstName.substring(1);
                String ufnSuffix = ".pdf"; // default
                if(submission.getPrimaryDocumentFieldValue()!=null){
                    String uploadedFileName = submission.getPrimaryDocumentFieldValue().getFileName();
                    int ufnIndx;
                    if((ufnIndx = uploadedFileName.indexOf(".")) > 0){
                        ufnSuffix = uploadedFileName.substring(ufnIndx);
                    }
                }
                context.setVariable(key.name(), lastName+"_"+firstName+ufnSuffix);
                break;
            default:
                break;
            }
        }
    }

    @Override
    public String getSuffix() {
        return ".xml";
    }

    @Override
    public String getTemplateMode() {
        return "XML";
    }

}
