package org.tdl.vireo.model.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.Transient;

import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.enums.DefaultSettingKey;
import org.tdl.vireo.model.export.enums.GeneralKey;
import org.tdl.vireo.model.export.enums.SubmissionPropertyKey;
import org.tdl.vireo.utility.FileHelperUtility;
import org.tdl.vireo.utility.SubmissionHelperUtility;
import org.thymeleaf.context.Context;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
@Inheritance
public abstract class AbstractFormatter extends BaseEntity implements Formatter {

    protected final static String DEFAULT_TEMPLATE_KEY = "template";

    @Column(unique = true, nullable = false)
    private String name;

    @ElementCollection
    @Column(nullable = true)
    private Map<String, String> templates;

    @Transient
    protected SubmissionHelperUtility submissionHelperUtility;

    @Transient
    protected FileHelperUtility fileHelperUtility;

    @Override
    public void populateContext(Context context, Submission submission) {
        fileHelperUtility = new FileHelperUtility();
        submissionHelperUtility = new SubmissionHelperUtility(submission);

        for (GeneralKey key : GeneralKey.values()) {
            switch (key) {
            case FILE_HELPER:
                context.setVariable(key.name(), fileHelperUtility);
                break;
            case SUBMISSION_HELPER:
                context.setVariable(key.name(), submissionHelperUtility);
                break;
            case SUBMISSION:
                context.setVariable(key.name(), submission);
                break;
            case TIME:
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                context.setVariable(key.name(), format.format(new Date()));
                break;
            default:
                break;

            }
        }

        for (DefaultSettingKey key : DefaultSettingKey.values()) {
            switch (key) {
            case APPLICATION_GRANTOR:
                context.setVariable(key.name(), submissionHelperUtility.getGrantor());
                break;
            case EXPORT_RELEASE_STUDENT_CONTACT_INFORMATION:
                context.setVariable(key.name(), submissionHelperUtility.getReleaseStudentContactInformation());
                break;
            case PROQUEST_INDEXING:
                String proQuestIndexing = Boolean.valueOf(submissionHelperUtility.getProQuestIndexing()) ? "Y" : "N";
                context.setVariable(key.name(), proQuestIndexing);
                break;
            case PROQUEST_APPLY_FOR_COPYRIGHT:
                String proQuestApplyForCopyright = Boolean.valueOf(submissionHelperUtility.getProQuestApplyForCopyright()) ? "yes" : "no";
                context.setVariable(key.name(), proQuestApplyForCopyright);
                break;
            // NOTE: uses both a submission property, submission id, and a default setting
            case PROQUEST_EXTERNAL_ID:
                context.setVariable(key.name(), submissionHelperUtility.getProQuestExternalId());
                break;
            case PROQUEST_FORMAT_RESTRICTION_CODE:
                context.setVariable(key.name(), submissionHelperUtility.getProQuestFormatRestrictionCode());
                break;
            case PROQUEST_FORMAT_RESTRICTION_REMOVE:
                context.setVariable(key.name(), submissionHelperUtility.getProQuestFormatRestrictionRemove());
                break;
            case PROQUEST_INSTITUTION_CODE:
                context.setVariable(key.name(), submissionHelperUtility.getProQuestInstitutionCode());
                break;
            case PROQUEST_SALE_RESTRICTION_CODE:
                context.setVariable(key.name(), submissionHelperUtility.getProQuestSaleRestrictionCode());
                break;
            case PROQUEST_SALE_RESTRICTION_REMOVE:
                //context.setVariable(key.name(), submissionHelperUtility.getProQuestSaleRestrictionRemove());
                context.setVariable(key.name(), submissionHelperUtility.getProQuestFormatRestrictionRemove());
                break;
            default:
                break;

            }
        }

        for (SubmissionPropertyKey key : SubmissionPropertyKey.values()) {
            switch (key) {
            case SUBMISSION_ID:
                context.setVariable(key.name(), submission.getId());
                break;
            case FIELD_VALUES:
                context.setVariable(key.name(), submission.getFieldValues());
                break;

            case FORMATTED_APPLICATION_APPROVAL_DATE:
				//dspace_simple_dublin_core:dc.date.issued
                context.setVariable(key.name(), submissionHelperUtility.getApproveApplicationDate());
                break;

            case LICENSE_AGREEMENT_DATE:
                //dc.description.provenance = License Agreement Date : no license agreement date available so approximate with SubmissionDate
                context.setVariable(key.name(), submission.getSubmissionDate());
                break;
            case FORMATTED_LICENSE_AGREEMENT_DATE:
                //dc.description.provenance = License Agreement Date : no license agreement date available so approximate with SubmissionDate
                context.setVariable(key.name(), submissionHelperUtility.getSubmissionDateString());
                break;

            case SUBMISSION_DATE:
                //dc.description.provenance = Submission Date
                context.setVariable(key.name(), submission.getSubmissionDate());
                break;
            case FORMATTED_SUBMISSION_DATE:
                //dc.description.provenance = Submission Date
                context.setVariable(key.name(), submissionHelperUtility.getSubmissionDateString());
                break;

            case COMMITTEE_APPROVAL_DATE:
                //dc.description.provenance = Committee Approval Date
                context.setVariable(key.name(), submission.getApproveAdvisorDate());
                break;
            case FORMATTED_COMMITTEE_APPROVAL_DATE:
                //dc.description.provenance = Committee Approval Date
                context.setVariable(key.name(), submissionHelperUtility.getAdvisorApprovalDateString());
                break;

            case COMMITTEE_EMBARGO_APPROVAL_DATE:
                //dc.description.provenance = Committee Embargo Approval Date
                context.setVariable(key.name(), submission.getApproveEmbargoDate());
                break;
            case FORMATTED_COMMITTEE_EMBARGO_APPROVAL_DATE:
                //dc.description.provenance = Committee Embargo Approval Date
                context.setVariable(key.name(), submissionHelperUtility.getEmbargoApprovalDateString());
                break;

            case APPROVAL_DATE:
                context.setVariable(key.name(), submission.getApproveAdvisorDate());
                break;
            case FORMATTED_APPROVAL_DATE:
                context.setVariable(key.name(), submissionHelperUtility.getAdvisorApprovalDateString());
                break;

/***
            case ADVISOR_APPROVAL_DATE:
                context.setVariable(key.name(), submission.getApproveAdvisorDate());
                break;
            case FORMATTED_ADVISOR_APPROVAL_DATE:
                context.setVariable(key.name(), submissionHelperUtility.getAdvisorApprovalDateString());
                break;
***/



            default:
                break;
            }
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, String> template_map) {
        this.templates = template_map;
    }

}
