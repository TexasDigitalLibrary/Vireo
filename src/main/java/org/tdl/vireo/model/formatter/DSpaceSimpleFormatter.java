package org.tdl.vireo.model.formatter;

import java.util.HashMap;

import javax.persistence.Entity;

import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.enums.DSpaceSimpleKey;
import org.thymeleaf.context.Context;

@Entity
public class DSpaceSimpleFormatter extends AbstractFormatter {

    public DSpaceSimpleFormatter() {
        super();
        setName("DSpaceSimple");
        HashMap<String, String> templates = new HashMap<String, String>();
        templates.put("metadata_local.xml", "dspace_simple_metadata_local");
        templates.put("metadata_thesis.xml", "dspace_simple_metadata_thesis");
        templates.put("dublin_core.xml", "dspace_simple_dublin_core");
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
        for (DSpaceSimpleKey key : DSpaceSimpleKey.values()) {
            switch (key) {

            // DUBLIN_CORE
            case STUDENT_FULL_NAME_WITH_BIRTH_YEAR:
                context.setVariable(key.name(), submissionHelperUtility.getStudentFullNameWithBirthYear());
                break;
            case USER_ORCID:
                context.setVariable(key.name(), submissionHelperUtility.getUserOrcid());
                break;
            case TITLE:
                context.setVariable(key.name(), submissionHelperUtility.getTitle());
                break;
            case ABSTRACT:
                context.setVariable(key.name(), submissionHelperUtility.getAbstract());
                break;
            case ABSTRACT_LINES:
                context.setVariable(key.name(), submissionHelperUtility.getAbstractLines());
                break;
            case SUBJECT_FIELD_VALUES:
                context.setVariable(key.name(), submissionHelperUtility.getSubjectFieldValues());
                break;
            case COMMITTEE_CHAIR_FIELD_VALUES:
                context.setVariable(key.name(), submissionHelperUtility.getCommitteeChairFieldValues());
                break;
            case COMMITTEE_MEMBER_FIELD_VALUES:
                context.setVariable(key.name(), submissionHelperUtility.getCommitteeMemberFieldValues());
                break;
            case GRADUATION_DATE_YEAR_MONTH_STRING:
                context.setVariable(key.name(), submissionHelperUtility.getGraduationYearMonthString());
                break;
            case GRADUATION_DATE_MONTH_YEAR_STRING:
                context.setVariable(key.name(), submissionHelperUtility.getGraduationMonthYearString());
                break;
            case PRIMARY_DOCUMENT_MIMETYPE:
                String primaryDocumentType = "Other";
                FieldValue primaryDocumentFieldValue = submission.getPrimaryDocumentFieldValue();
                if (primaryDocumentFieldValue != null) {
                    primaryDocumentType = fileHelperUtility.getMimeTypeOfAsset(primaryDocumentFieldValue.getValue());
                }
                context.setVariable(key.name(), primaryDocumentType);
                break;
            case PROQUEST_LANGUAGE_CODE:
                context.setVariable(key.name(), submissionHelperUtility.getLanguageProQuestCode());
                break;
            case SUBMISSION_TYPE:
                context.setVariable(key.name(), submissionHelperUtility.getSubmissionType());
                break;
            case DEPOSIT_URL:
                context.setVariable(key.name(), submission.getDepositURL());
                break;
            case STUDENT_SHORT_NAME:
                context.setVariable(key.name(), submissionHelperUtility.getStudentShortName());
                break;
            // METADATA_THESIS
            case DEGREE_MAJOR:
                context.setVariable(key.name(), submissionHelperUtility.getMajor());
                break;
            case DEGREE_NAME:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeName());
                break;
            case DEGREE_LEVEL:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeLevel());
                break;
            case DEPARTMENT:
                context.setVariable(key.name(), submissionHelperUtility.getDepartment());
                break;
            case DEGREE_COLLEGE:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeCollege());
                break;
            case DEGREE_SCHOOL:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeSchool());
                break;
            case DEGREE_PROGRAM:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeProgram());
                break;
            case FORMATTED_COMMITTEE_APPROVED_EMBARGO_LIFT_DATE:
                context.setVariable(key.name(), submissionHelperUtility.getFormattedCommitteeApprovedEmbargoLiftDateString());
                break;

            // METADATA_LOCAL
            // case EMBARGO_LIFT_DATE:
            // context.setVariable(key.name(), submissionHelperUtility.getEmbargoApprovalDateString());
            // break;
            //case EMBARGO_CODE:
            //    context.setVariable(key.name(), submissionHelperUtility.getEmbargoCode());
            //    break;

            default:
                break;
            }
        }
    }

}
