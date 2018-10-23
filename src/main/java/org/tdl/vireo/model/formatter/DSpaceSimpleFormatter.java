package org.tdl.vireo.model.formatter;

import java.util.HashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
			System.out.println("FSS KEY "+key);
            switch (key) {

		//DUBLIN_CORE	
            case STUDENT_FULL_NAME_WITH_BIRTH_YEAR:
                context.setVariable(key.name(), submissionHelperUtility.getStudentFullNameWithBirthYear());
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
            case COMMITTEE_CHAIR:
                context.setVariable(key.name(), submissionHelperUtility.getCommitteeChair());
                break;
            case COMMITTEE_MEMBER_FIELD_VALUES:
                context.setVariable(key.name(), submissionHelperUtility.getCommitteeMemberFieldValues());
                break;
            case SUBMITTER_GRADUATION_DATE:
                context.setVariable(key.name(), submissionHelperUtility.getGraduationDateString());
                break;
            case PRIMARY_DOCUMENT_MIMETYPE:
                String primaryDocumentType = "Other";
                FieldValue primaryDocumentFieldValue = submission.getPrimaryDocumentFieldValue();
                if (primaryDocumentFieldValue != null) {
                    primaryDocumentType = fileHelperUtility.getMimeTypeOfAsset(primaryDocumentFieldValue.getValue());
                    //if (primaryDocumentType.equals("application/pdf")) {
                    //    primaryDocumentType = "PDF";
                    //}
                }
                context.setVariable(key.name(), primaryDocumentType);
                break;
            case PROQUEST_LANGUAGE_CODE:
                context.setVariable(key.name(), submissionHelperUtility.getLanguageProQuestCode());
                break;
            case SUBMISSION_TYPE:
                context.setVariable(key.name(), submissionHelperUtility.getSubmissionType());
                break;
            case STUDENT_SHORT_NAME:
                context.setVariable(key.name(), submissionHelperUtility.getStudentShortName());
                break;
		//METADATA_THESIS
            case DEGREE_LEVEL:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeLevel());
                break;
            case DEPARTMENT:
                context.setVariable(key.name(), submissionHelperUtility.getDepartment());
                break;

		//METADATA_LOCAL

/****
            case EMBARGO_CODE:
                context.setVariable(key.name(), submissionHelperUtility.getEmbargoCode());
                break;
            case AGENT:
                context.setVariable(key.name(), "Vireo DSpace Simple Archive Format packager");
                break;
            case LICENSE_DOCUMENT_FIELD_VALUES:
                context.setVariable(key.name(), submission.getLicenseDocumentFieldValues());
                break;
            case PRIMARY_DOCUMENT_FIELD_VALUE:
                context.setVariable(key.name(), submission.getPrimaryDocumentFieldValue());
					System.out.println("FSS VAL "+submission.getPrimaryDocumentFieldValue());
                break;
            case PRIMARY_DOCUMENT_MIMETYPE:
                String primaryDocumentType = "application/pdf";
                FieldValue primaryDocumentFieldValue = submission.getPrimaryDocumentFieldValue();
                if (primaryDocumentFieldValue != null) {
                    primaryDocumentType = fileHelperUtility.getMimeType(primaryDocumentFieldValue.getValue());
                }
				System.out.println("FP "+fileHelperUtility.getAssetAbsolutePath(""));
                context.setVariable(key.name(), primaryDocumentType);
                break;
            case SUPPLEMENTAL_AND_SOURCE_DOCUMENT_FIELD_VALUES:
                context.setVariable(key.name(), submission.getSupplementalAndSourceDocumentFieldValues());
                break;
            case METS_FIELD_VALUES:
                context.setVariable(key.name(), submission.getFieldValues().parallelStream().filter(new Predicate<FieldValue>() {
                    @Override
                    public boolean test(FieldValue fv) {
                        return fv.getFieldPredicate().getSchema().equals("dc") || fv.getFieldPredicate().getSchema().equals("thesis") || fv.getFieldPredicate().getSchema().equals("local");
                    }
                }).collect(Collectors.toList()));
                break;
****/
            default:
System.out.println("FSS KEY DEFAULT "+key);
                break;
            }
        }
    }

}
