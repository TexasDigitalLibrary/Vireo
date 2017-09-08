package org.tdl.vireo.model.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;

import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.enums.DSpaceMETSKey;
import org.tdl.vireo.util.FileHelperUtility;
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
        for (DSpaceMETSKey key : DSpaceMETSKey.values()) {
            switch(key) {
            case AGENT:
                context.setVariable(key.name(), "Vireo DSpace METS packager");
                break;
            case APPROVAL_DATE:
                break;
            case COMMITTEE_APPROVAL_DATE:
                break;
            case FIELD_VALUES:
                context.setVariable(key.name(), submission.getFieldValues());
                break;
            case FILE_HELPER:
                context.setVariable(key.name(), new FileHelperUtility());
                break;
            case LICENSE_AGREEMENT_DATE:
                break;
            case LICENSE_DOCUMENT_FIELD_VALUES:
                break;
            case PRIMARY_DOCUMENT_FIELD_VALUE:
                break;
            case PRIMARY_DOCUMENT_MIMETYPE:
                break;
            case STUDENT_FULL_NAME_WITH_BIRTH_YEAR:
                context.setVariable(key.name(), submission.getStudentFullNameWithBirthYear());
                break;
            case STUDENT_SHORT_NAME:
                break;
            case SUBMISSION:
                context.setVariable(key.name(), submission);
                break;
            case SUBMISSION_DATE:
                break;
            case SUBMISSION_ID:
                context.setVariable(key.name(), submission.getId());
                break;
            case SUBMISSION_TYPE:
                break;
            case SUPPLEMENTAL_AND_SOURCE_DOCUMENT_FIELD_VALUES:
                break;
            case TIME:
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                context.setVariable(key.name(), format.format(new Date()));
                break;
            default:
                break;
            }
        }
    }

}
