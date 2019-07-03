package org.tdl.vireo.model.formatter;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.export.enums.MarcXML21Key;
import org.thymeleaf.context.Context;

@Entity
public class MarcXML21Formatter extends AbstractFormatter {

    public MarcXML21Formatter() {
        super();
        setName("MARC21XML");
        HashMap<String, String> templates = new HashMap<String, String>();
        templates.put("marc21_xml.xml", "marc21_xml");
        setTemplates(templates);
    }

    @Override
    public void populateContext(Context context, Submission submission) {
        super.populateContext(context, submission);
        for (MarcXML21Key key : MarcXML21Key.values()) {
            switch (key) {
            case SUBMITTER_GRADUATION_YEAR:
                context.setVariable(key.name(), submissionHelperUtility.getGraduationYearString());
                break;
            case STUDENT_FULL_NAME_WITH_BIRTH_YEAR:
                context.setVariable(key.name(), submissionHelperUtility.getStudentFullNameWithBirthYear());
                break;
            case TITLE:
                context.setVariable(key.name(), submissionHelperUtility.getTitle());
                break;
            case TITLE_IND2:
                String title = submissionHelperUtility.getTitle();
                String ind2 = "0";
                if (title.startsWith("A ")) {
                    ind2 = "2";
                } else if (title.startsWith("An ")) {
                    ind2 = "3";
                } else if (title.startsWith("The ")) {
                    ind2 = "4";
                }
                context.setVariable(key.name(), ind2);
                break;
            case DEGREE_LEVEL:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeLevel());
                break;
            case DEGREE_NAME:
                context.setVariable(key.name(), submissionHelperUtility.getDegreeProQuestCode());
                break;
            case MAJOR:
                context.setVariable(key.name(), submissionHelperUtility.getMajor());
                break;
            case DEPOSIT_URL:
                context.setVariable(key.name(), submission.getDepositURL());
                break;
            case ABSTRACT:
                context.setVariable(key.name(), submissionHelperUtility.getAbstract());
                break;
            case KEYWORD_FIELD_VALUES:
                context.setVariable(key.name(), submissionHelperUtility.getKeywordFieldValues());
                break;
            case DEPARTMENT:
                context.setVariable(key.name(), submissionHelperUtility.getDepartment());
                break;
            case COMMITTEE_CHAIR_FIELD_VALUES:
                List<String> chairs = submissionHelperUtility.getCommitteeChairFieldValues().stream().map(chair -> formatName(chair.getValue())).collect(Collectors.toList());
                context.setVariable(key.name(), chairs);
                break;
            case COMMITTEE_MEMBER_FIELD_VALUES:
                List<String> members = submissionHelperUtility.getCommitteeMemberFieldValues().stream().map(member -> formatName(member.getValue())).collect(Collectors.toList());
                context.setVariable(key.name(), members);
                break;
            case PRIMARY_DOCUMENT_MIMETYPE:
                String primaryDocumentType = "Other";
                FieldValue primaryDocumentFieldValue = submission.getPrimaryDocumentFieldValue();
                if (primaryDocumentFieldValue != null) {
                    primaryDocumentType = fileHelperUtility.getMimeTypeOfAsset(primaryDocumentFieldValue.getValue());
                }
                context.setVariable(key.name(), primaryDocumentType);
                break;
            }
        }
    }

    private String formatName(String name) {
        String[] names = name.split(" ");
        if (names.length == 3) {
            name = String.format("%s, %s %s", names[2], names[0], names[1]);
        } else if (names.length == 2) {
            name = String.format("%s, %s", names[1], names[0]);
        }
        return name;
    }
}
