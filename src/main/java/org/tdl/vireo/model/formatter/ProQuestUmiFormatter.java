package org.tdl.vireo.model.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;

import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.enums.ProQuestUMIKey;
import org.tdl.vireo.util.FileHelperUtility;
import org.thymeleaf.context.Context;

@Entity
public class ProQuestUmiFormatter extends AbstractFormatter {

    public ProQuestUmiFormatter() {
        super();
        setName("ProQuestUMI");
        setTemplate("proquest_umi");
    }

    @Override
    public void populateContext(Context context, Submission submission) {
        for (ProQuestUMIKey key : ProQuestUMIKey.values()) {
            switch(key) {
            case AGENT:
                context.setVariable(key.name(), "Vireo ProQuest UMI packager");
                break;
            case APPROVAL_DATE:
                break;
            case COMMITTEE_APPROVAL_DATE:
                break;
            case FIELD_VALUES:
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
                break;
            case STUDENT_SHORT_NAME:
                break;
            case SUBMISSION:
                context.setVariable(key.name(), submission);
                break;
            case SUBMISSION_DATE:
                break;
            case SUBMISSION_ID:
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
