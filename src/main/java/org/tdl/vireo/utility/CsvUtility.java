package org.tdl.vireo.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.User;

/**
 * Utility class for CSV file operations.
 * Provides methods to convert various data structures to CSV files.
 *
 */
public final class CsvUtility {

    /**
     * CSV header columns for action logs
     */
    private static final String[] ACTION_LOG_HEADERS = {
            "Action Date", "User Name", "Action Entry", "SubmissionState"
    };

    /**
     * Date format pattern for timestamps in CSV exports
     */
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssz";

    /**
     * Private constructor to prevent instantiation
     */
    private CsvUtility() {
        throw new IllegalStateException("Utility class - do not instantiate");
    }

    /**
     * Converts a set of ActionLog objects to a CSV file.
     * The method sorts the logs by action date in ascending order.
     *
     * @param logs     Set of ActionLog objects to be converted to CSV
     * @param fileName Base name for the temporary file (without extension)
     * @return A File object pointing to the generated CSV file
     * @throws IOException If there is an error writing to the file
     */
    public static File fromActionLog(Set<ActionLog> logs, String fileName) throws IOException {
        if (logs == null) {
            throw new IllegalArgumentException("Action logs cannot be null");
        }

        if (StringUtils.isBlank(fileName)) {
            fileName = "action_log.csv";
        }

        // Convert Set into sortable ArrayList to present action logs in order
        ArrayList<ActionLog> actionLog = new ArrayList<>(logs);
        actionLog.sort(Comparator.comparing(ActionLog::getActionDate));

        // Create temporary file
        File actionLogFile = File.createTempFile(fileName, null);
        actionLogFile.deleteOnExit();

        // Set up date formatter
        SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_PATTERN);

        // Use Commons CSV to write data
        try (
                FileWriter fileWriter = new FileWriter(actionLogFile, StandardCharsets.UTF_8);
                CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT
                        .builder()
                        .setHeader(ACTION_LOG_HEADERS)
                        .build())) {
            for (ActionLog actionLogEntry : actionLog) {
                csvPrinter.printRecord(
                        dateFormatter.format(actionLogEntry.getActionDate().getTime()),
                        getUserName(actionLogEntry),
                        actionLogEntry.getEntry(),
                        actionLogEntry.getSubmissionStatus().getName());
            }
        }

        return actionLogFile;
    }

    /**
     * Helper method to extract the user name from an ActionLog entry.
     * Returns an empty string if the user is null.
     *
     * @param actionLog The ActionLog from which to extract the user name
     * @return The user name or an empty string if user is null
     */
    private static String getUserName(ActionLog actionLog) {
        User user = actionLog.getUser();

        return user != null ? user.getName() : StringUtils.EMPTY;
    }

}
