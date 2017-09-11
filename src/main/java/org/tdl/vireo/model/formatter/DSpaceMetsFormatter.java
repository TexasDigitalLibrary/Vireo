package org.tdl.vireo.model.formatter;

import java.util.function.Predicate;

import javax.persistence.Entity;

import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.enums.DSpaceMETSKey;
import org.thymeleaf.context.Context;

@Entity
public class DSpaceMetsFormatter extends AbstractFormatter {

    public DSpaceMetsFormatter() {
        super();
        setName("DSpaceMETS");
        setTemplate("dspace_mets");
    }

    @Override
    public void populateContext(Context context, Submission submission) {
        // TODO: in order to use mappings from an organization for this export,
        // the methods from the submission helper utility would have to be brought
        // the exporter and extract predicate values from the mapping to define
        // the value to be templated with the given key
        for (DSpaceMETSKey key : DSpaceMETSKey.values()) {
            switch (key) {
            case AGENT:
                context.setVariable(key.name(), "Vireo DSpace METS packager");
                break;
            case LICENSE_DOCUMENT_FIELD_VALUES:
                context.setVariable(key.name(), submission.getLicenseDocumentFieldValues());
                break;
            case PRIMARY_DOCUMENT_FIELD_VALUE:
                context.setVariable(key.name(), submission.getPrimaryDocumentFieldValue());
                break;
            case PRIMARY_DOCUMENT_MIMETYPE:
                String primaryDocumentType = "application/pdf";
                FieldValue primaryDocumentFieldValue = submission.getPrimaryDocumentFieldValue();
                if (primaryDocumentFieldValue != null) {
                    primaryDocumentType = fileHelperUtility.getMimeType(primaryDocumentFieldValue.getValue());
                }
                context.setVariable(key.name(), primaryDocumentType);
                break;
            case STUDENT_FULL_NAME_WITH_BIRTH_YEAR:
                context.setVariable(key.name(), submissionHelperUtility.getStudentFullNameWithBirthYear());
                break;
            case STUDENT_SHORT_NAME:
                context.setVariable(key.name(), submissionHelperUtility.getStudentShortName());
                break;
            case SUBMISSION_TYPE:
                context.setVariable(key.name(), submissionHelperUtility.getSubmissionType());
                break;
            case SUPPLEMENTAL_AND_SOURCE_DOCUMENT_FIELD_VALUES:
                context.setVariable(key.name(), submission.getSupplementalAndSourceDocumentFieldValues());
                break;
            case METS_FIELD_VALUES:
                context.setVariable(key.name(), submission.getFieldValues().parallelStream().filter(new Predicate<FieldValue>() {
                    @Override
                    public boolean test(FieldValue fv) {
                        return fv.getFieldPredicate().getSchema() == "dc" || fv.getFieldPredicate().getSchema() == "thesis" || fv.getFieldPredicate().getSchema() == "local";
                    }
                }));
                break;
            default:
                break;
            }
        }
    }

}
