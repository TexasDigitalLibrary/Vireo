package org.tdl.vireo.model.packager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Sort;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.export.ExcelExportPackage;
import org.springframework.test.util.ReflectionTestUtils;

public class ExcelPackagerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testPackageExportUsesRequestedDelimiterForExactPredicateMatches() {
        ExcelPackager packager = new ExcelPackager();
        Submission submission = new Submission();
        SubmissionListColumn column = new SubmissionListColumn("Keywords", Sort.NONE, "dc.subject");

        Set<FieldValue> fieldValues = new LinkedHashSet<>();
        fieldValues.add(createFieldValue("dc.subject", "alpha"));
        fieldValues.add(createFieldValue("dc.subject", "beta"));
        fieldValues.add(createFieldValue("dc.title", "ignored"));

        ReflectionTestUtils.setField(submission, "fieldValues", fieldValues);

        ExcelExportPackage exportPackage = packager.packageExport(submission, Arrays.asList(column), "|");
        Map<String, String> row = (Map<String, String>) exportPackage.getPayload();

        assertEquals("alpha|beta", row.get("Keywords"), "Repeatable values should be joined once with the requested delimiter.");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPackageExportMatchesSuffixedPredicates() {
        ExcelPackager packager = new ExcelPackager();
        Submission submission = new Submission();
        SubmissionListColumn column = new SubmissionListColumn("Non-Chairing Committee Members", Sort.NONE, "dc.contributor.committeeMember");

        Set<FieldValue> fieldValues = new LinkedHashSet<>();
        fieldValues.add(createFieldValue("dc.contributor.committeeMember.0", "Alice Smith"));
        fieldValues.add(createFieldValue("dc.contributor.committeeMember.1", "Bob Jones"));

        ReflectionTestUtils.setField(submission, "fieldValues", fieldValues);

        ExcelExportPackage exportPackage = packager.packageExport(submission, Arrays.asList(column), "|");
        Map<String, String> row = (Map<String, String>) exportPackage.getPayload();

        assertEquals("Alice Smith|Bob Jones", row.get("Non-Chairing Committee Members"), "Suffixed repeatable predicates should be included in the same exported cell.");
    }

    private FieldValue createFieldValue(String predicateValue, String value) {
        FieldPredicate fieldPredicate = new FieldPredicate();
        fieldPredicate.setId((long) predicateValue.hashCode());
        fieldPredicate.setValue(predicateValue);

        FieldValue fieldValue = new FieldValue();
        fieldValue.setId((long) (predicateValue + value).hashCode());
        fieldValue.setFieldPredicate(fieldPredicate);
        fieldValue.setValue(value);

        return fieldValue;
    }

}