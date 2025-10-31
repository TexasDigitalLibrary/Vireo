package org.tdl.vireo.model.packager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdl.vireo.exception.UnsupportedFormatterException;
import org.tdl.vireo.model.CustomActionValue;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionListColumn;
import org.tdl.vireo.model.export.ExcelExportPackage;
import org.tdl.vireo.model.formatter.AbstractFormatter;
import org.tdl.vireo.model.ActionLog;
import edu.tamu.weaver.data.utility.EntityUtility;

@Entity
public class ExcelPackager extends AbstractPackager<ExcelExportPackage> {

    private static final Logger logger = LoggerFactory.getLogger(ExcelPackager.class);

    private static final DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public ExcelPackager() {

    }

    public ExcelPackager(String name) {
        setName(name);
    }

    public ExcelPackager(String name, AbstractFormatter formatter) {
        this(name);
        setFormatter(formatter);
    }

    @Override
    public String getMimeType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String getFileExtension() {
        return "xlsx";
    }

    @Override
    public ExcelExportPackage packageExport(Submission submission, String manifest) throws UnsupportedFormatterException {
        throw new UnsupportedFormatterException("Exporter does not support manifest!");
    }

    @Override
    public ExcelExportPackage packageExport(Submission submission, List<SubmissionListColumn> columns) {
        Map<String, String> row = new HashMap<String, String>();
        columns.forEach(column -> {
            Optional<String> predicate = Optional.ofNullable(column.getPredicate());
            if (predicate.isPresent()) {
                List<String> fieldValues = new ArrayList<String>();
                for (FieldValue fieldValue : submission.getFieldValues()) {
                    if (fieldValue.getFieldPredicate().getValue().equals(predicate.get().trim())) {
                        fieldValues.add(fieldValue.getValue());
                        row.put(column.getTitle(), String.join(", ", fieldValues));
                    } else {
                        row.put(column.getTitle(), String.join(", ", fieldValues));                    }
                }
            } else {
                if (column.getValuePath().size() > 0) {
                    String[] valuePath = column.getValuePath().toArray(new String[column.getValuePath().size()]);
                    try {
                        if(column.getValuePath().size() > 1){
                             valuePath = new String[] {valuePath[0]};
                        }
                        Object valueAsObject = EntityUtility.getValueFromPath(submission, valuePath);

                        String value = "";

                        if (valueAsObject instanceof Calendar) {
                            Calendar calendar = (Calendar) valueAsObject;
                            value = simpleDateFormat.format(calendar.getTime());
                        } else if (valueAsObject instanceof Date) {
                            Date date = (Date) valueAsObject;
                            value = simpleDateFormat.format(date);
                        } else if (valueAsObject instanceof Set && ((Set<?>) valueAsObject).stream().allMatch(o -> o instanceof CustomActionValue)) {
                            StringBuilder sb = new StringBuilder();
                            ((Set<?>) valueAsObject).forEach(o -> {
                                CustomActionValue customActionValue = (CustomActionValue) o;
                                if (customActionValue.getValue()) {
                                    sb.append("☑ ");
                                } else {
                                    sb.append("☐ ");
                                }
                                sb.append(customActionValue.getDefinition().getLabel()+"\n");
                            });
                            value = sb.toString();
                        } else if (valueAsObject instanceof SubmissionStatus){
                            SubmissionStatus submissionStatus = (SubmissionStatus) valueAsObject;
                            value = submissionStatus.getName().toString();
                        } else if (valueAsObject instanceof User){
                            User user = (User) valueAsObject;
                            value = user.getName().toString();
                        } else if (valueAsObject instanceof ActionLog){
                            ActionLog actionLog = (ActionLog) valueAsObject;
                            switch (column.getTitle()){
                                case "Event Time":
                                    Calendar actionDate = (Calendar) actionLog.getActionDate();
                                    value = simpleDateFormat.format(actionDate.getTime());
                                    break;
                                case "Last Event":
                                    value = actionLog.getEntry().toString();
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            value = valueAsObject.toString();
                        }
                        row.put(column.getTitle(), value.toString());
                    } catch (Exception exception) {
                        logger.warn("Unable to get value from " + String.join(",", valuePath));
                    }
                } else {
                    logger.warn("Column " + column.getTitle() + " has no predicate or value path!");
                }
            }
        });
        return new ExcelExportPackage(submission, "Excel", row);
    }

}
