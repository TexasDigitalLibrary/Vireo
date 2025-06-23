package org.tdl.vireo.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.tdl.vireo.model.Action;
import org.tdl.vireo.model.ActionLog;
import org.tdl.vireo.model.SubmissionStatus;
import org.tdl.vireo.model.User;

/**
 * Unit tests for CsvUtility class.
 * Tests focus on the fromActionLog method with various inputs.
 */
class CsvUtilityTest {


    /**
     * Test data provider for parameterized tests.
     * Provides various combinations of action logs with different characteristics.
     */
    private static Stream<Arguments> provideActionLogData() {
        return Stream.of(
                // Single action log with user
                Arguments.of(
                        createActionLogSet(1, true, false),
                        "single_log_with_user"),
                // Single action log without user
                Arguments.of(
                        createActionLogSet(1, false, false),
                        "single_log_without_user"),
                // Multiple action logs chronologically ordered
                Arguments.of(
                        createActionLogSet(3, true, false),
                        "multiple_logs_ordered"),
                // Multiple action logs reverse ordered (to test sorting)
                Arguments.of(
                        createActionLogSet(3, true, true),
                        "multiple_logs_reverse_ordered"),
                // Mixed logs (some with users, some without)
                Arguments.of(
                        createMixedActionLogSet(),
                        "mixed_logs"));
    }

    /**
     * Parameterized test for fromActionLog method with various valid inputs.
     * Tests that CSV files are correctly generated for different sets of action
     * logs.
     */
    @ParameterizedTest
    @MethodSource("provideActionLogData")
    @DisplayName("Test fromActionLog with various valid inputs")
    void testFromActionLog(Set<ActionLog> logs, String fileName) throws IOException {
        // Act
        File csvFile = CsvUtility.fromActionLog(logs, fileName);

        // Assert
        assertTrue(csvFile.exists(), "CSV file should exist");
        assertTrue(csvFile.length() > 0, "CSV file should not be empty");

        // Verify CSV content
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile, StandardCharsets.UTF_8))) {
            // Verify header
            String header = reader.readLine();
            assertEquals("Action Date,User Name,Action Entry,SubmissionState", header);

            // Verify data rows (same count as input logs)
            int lineCount = 0;
            while (reader.readLine() != null) {
                lineCount++;
            }
            assertEquals(logs.size(), lineCount, "CSV should have same number of data rows as input logs");
        }
    }

    /**
     * Test that verifies the correct handling of null or empty input logs.
     */
    @Test
    @DisplayName("Test fromActionLog with null logs throws IllegalArgumentException")
    void testFromActionLogWithNull() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            CsvUtility.fromActionLog(null, "test");
        }, "Should throw IllegalArgumentException when logs is null");
    }

    /**
     * Parameterized test for fromActionLog method with invalid file names.
     * Tests that default file name is used when input is null, empty, or blank.
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   " })
    @DisplayName("Test fromActionLog with invalid file names")
    void testFromActionLogWithInvalidFileName(String fileName) throws IOException {
        // Arrange
        Set<ActionLog> logs = createActionLogSet(1, true, false);

        // Act
        File csvFile = CsvUtility.fromActionLog(logs, fileName);

        // Assert
        assertTrue(csvFile.exists(), "CSV file should exist despite invalid file name");
        assertTrue(csvFile.getName().startsWith("action_log"),
                "CSV file should use default name when input is invalid");
    }

    /**
     * Test that verifies the correct sorting of action logs by date.
     */
    @Test
    @DisplayName("Test action logs are sorted by date in CSV")
    void testActionLogsAreSortedByDate() throws IOException {
        // Arrange
        Set<ActionLog> logs = createActionLogSet(5, true, true); // Create reversed logs

        // Act
        File csvFile = CsvUtility.fromActionLog(logs, "sorted_test");

        // Assert
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile, StandardCharsets.UTF_8))) {
            // Skip header
            reader.readLine();

            // Read all lines and check they're in ascending date order
            String line;
            String previousDate = null;

            while ((line = reader.readLine()) != null) {
                String currentDate = line.split(",")[0]; // First column is date

                if (previousDate != null) {
                    assertTrue(currentDate.compareTo(previousDate) >= 0,
                            "Dates should be in ascending order");
                }

                previousDate = currentDate;
            }
        }
    }

    /**
     * Test that verifies the correct CSV formatting of special characters.
     */
    @Test
    @DisplayName("Test CSV properly handles special characters and quotes")
    void testCsvHandlesSpecialCharacters() throws IOException {
        // Arrange
        Set<ActionLog> logs = new HashSet<>();
        logs.add(createActionLogWithSpecialChars());

        // Act
        File csvFile = CsvUtility.fromActionLog(logs, "special_chars_test");

        // Assert
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile, StandardCharsets.UTF_8))) {
            // Skip header
            reader.readLine();

            // Read data line
            String line = reader.readLine();

            // Verify special characters are properly escaped
            assertTrue(line.contains("\"Entry with, commas and \"\"quotes\"\"\""),
                    "Quotes and commas should be properly escaped in CSV");
        }
    }

    // Helper methods to create test data

    /**
     * Creates a set of action logs with specified characteristics.
     */
    private static Set<ActionLog> createActionLogSet(int count, boolean withUser, boolean reverseOrder) {
        Set<ActionLog> logs = new HashSet<>();

        for (int i = 0; i < count; i++) {
            // Create calendar instances with increasing or decreasing dates
            Calendar cal = Calendar.getInstance();
            if (reverseOrder) {
                cal.add(Calendar.DAY_OF_MONTH, count - i);
            } else {
                cal.add(Calendar.DAY_OF_MONTH, i);
            }

            // Create mock objects
            SubmissionStatus status = Mockito.mock(SubmissionStatus.class);
            Mockito.when(status.getName()).thenReturn("Status" + i);

            ActionLog log = new ActionLog(Action.UNDETERMINED);
            log.setActionDate(cal);
            log.setEntry("Test entry " + i);
            log.setSubmissionStatus(status);
            log.setPrivateFlag(false);

            if (withUser) {
                User user = Mockito.mock(User.class);
                Mockito.when(user.getName()).thenReturn("User" + i);
                log.setUser(user);
            }

            logs.add(log);
        }

        return logs;
    }

    /**
     * Creates a mixed set of action logs (some with users, some without).
     */
    private static Set<ActionLog> createMixedActionLogSet() {
        Set<ActionLog> logs = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, i);

            SubmissionStatus status = Mockito.mock(SubmissionStatus.class);
            Mockito.when(status.getName()).thenReturn("Status" + i);

            ActionLog log = new ActionLog(Action.UNDETERMINED);
            log.setActionDate(cal);
            log.setEntry("Test entry " + i);
            log.setSubmissionStatus(status);
            log.setPrivateFlag(false);

            // Add user only to even-indexed logs
            if (i % 2 == 0) {
                User user = Mockito.mock(User.class);
                Mockito.when(user.getName()).thenReturn("User" + i);
                log.setUser(user);
            }

            logs.add(log);
        }

        return logs;
    }

    /**
     * Creates an action log with special characters in the entry.
     */
    private static ActionLog createActionLogWithSpecialChars() {
        Calendar cal = Calendar.getInstance();

        SubmissionStatus status = Mockito.mock(SubmissionStatus.class);
        Mockito.when(status.getName()).thenReturn("SpecialStatus");

        User user = Mockito.mock(User.class);
        Mockito.when(user.getName()).thenReturn("User with \"quotes\"");

        ActionLog log = new ActionLog(Action.UNDETERMINED);
        log.setActionDate(cal);
        log.setEntry("Entry with, commas and \"quotes\"");
        log.setSubmissionStatus(status);
        log.setUser(user);
        log.setPrivateFlag(false);

        return log;
    }

}
