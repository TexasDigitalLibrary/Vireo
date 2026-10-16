package org.tdl.vireo.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.utility.PasswordValidator.Result;

/**
 * Tests {@link PasswordValidator} with enumerated boundary values, whitespace padding and a
 * fixed-seed {@link Random}. The validator is static, so no Spring context is started.
 */
@ActiveProfiles(value = { "test", "isolated-test" })
public class PasswordValidatorTest {

    private static final long GENERATOR_SEED = 20250101L;

    private static final int RANDOM_CASE_COUNT = 64;

    // Every run trims to nothing, so a run on its own is a missing-password case.
    private static final List<String> WHITESPACE_RUNS = Arrays.asList(
        "",
        " ",
        "  ",
        "   ",
        "\t",
        "\n",
        "\r",
        " \t\n\r ",
        "        "
    );

    // Trimmed lengths 5 and 6 are covered across character classes, since only trimmed length may matter.
    private static final List<String> CANDIDATE_CORES = Arrays.asList(
        // Absent and empty.
        null,
        "",

        // Below the boundary.
        "a",
        "ab",
        "abc",
        "abcd",

        // Trimmed length 5: rejected.
        "abcde",
        "ABCDE",
        "12345",
        "!@#$%",
        "aB3$x",
        "\u00e9\u00e8\u00ea\u00eb\u00ec",

        // Trimmed length 6: accepted.
        "abcdef",
        "ABCDEF",
        "123456",
        "!@#$%^",
        "aB3$x.",
        "\u00e9\u00e8\u00ea\u00eb\u00ec\u00ed",

        // Above the boundary.
        "abcdefg",
        "1234567890",
        "correct horse battery staple",
        "\u4e2d\u6587\u5bc6\u7801\u5f88\u5f3a\u5927",

        // Interior whitespace is not trimmed, so these count at full length.
        "a b c",
        "a b cd",
        "a\tb\tc\td"
    );

    // Crosses each core with every padding shape so a trimmed-length rule differs from an untrimmed one.
    private static Stream<Arguments> provideCandidates() {
        Set<String> candidates = new LinkedHashSet<String>();

        boolean sawNull = false;

        for (String core : CANDIDATE_CORES) {
            if (core == null) {
                sawNull = true;

                continue;
            }

            candidates.add(core);

            for (String run : WHITESPACE_RUNS) {
                candidates.add(run + core);
                candidates.add(core + run);
                candidates.add(run + core + run);
            }
        }

        candidates.addAll(WHITESPACE_RUNS);

        Random random = new Random(GENERATOR_SEED);

        for (int i = 0; i < RANDOM_CASE_COUNT; i++) {
            candidates.add(randomCandidate(random));
        }

        List<Arguments> cases = new ArrayList<Arguments>();

        if (sawNull) {
            cases.add(Arguments.of((String) null));
        }

        for (String candidate : candidates) {
            cases.add(Arguments.of(candidate));
        }

        return cases.stream();
    }

    // Length 0 to 10 so both boundary lengths are reachable, in a random character class with random padding.
    private static String randomCandidate(Random random) {
        int length = random.nextInt(11);

        StringBuilder core = new StringBuilder();

        for (int i = 0; i < length; i++) {
            switch (random.nextInt(5)) {
                case 0:
                    core.append((char) ('a' + random.nextInt(26)));
                    break;
                case 1:
                    core.append((char) ('A' + random.nextInt(26)));
                    break;
                case 2:
                    core.append((char) ('0' + random.nextInt(10)));
                    break;
                case 3:
                    core.append((char) ('!' + random.nextInt(15)));
                    break;
                default:
                    core.append((char) (0x00c0 + random.nextInt(60)));
                    break;
            }
        }

        String leading = WHITESPACE_RUNS.get(random.nextInt(WHITESPACE_RUNS.size()));
        String trailing = WHITESPACE_RUNS.get(random.nextInt(WHITESPACE_RUNS.size()));

        return leading + core.toString() + trailing;
    }

    // Independent oracle: trim, then decide on trimmed length alone.
    private static Result expectedResult(String candidate) {
        if (candidate == null) {
            return Result.MISSING;
        }

        int trimmedLength = candidate.trim().length();

        if (trimmedLength == 0) {
            return Result.MISSING;
        }

        if (trimmedLength < 6) {
            return Result.TOO_SHORT;
        }

        return Result.ACCEPTED;
    }

    // The decision depends on trimmed length alone, and is the same via both overloads and on a repeat call.
    @ParameterizedTest(name = "[{index}] candidate=''{0}''")
    @MethodSource("provideCandidates")
    public void testSharedValidatorDecision(String candidate) {
        Result expected = expectedResult(candidate);

        String description = "Decision for candidate '" + candidate + "'";

        assertEquals(expected, PasswordValidator.validate(candidate), description);

        // Ties the oracle's literal 6 to the declared constant.
        assertEquals(6, PasswordValidator.MINIMUM_TRIMMED_LENGTH, "The declared minimum trimmed length");

        // A matching confirmation adds no rejection cause, so both overloads must agree.
        assertEquals(expected, PasswordValidator.validate(candidate, candidate), description + " through the confirming overload");

        // No reuse-history state, so a repeat call decides the same way.
        assertEquals(expected, PasswordValidator.validate(candidate), description + " on a repeat decision");
    }

    // Includes pairs differing only in case, in surrounding whitespace, or in the kind of trailing whitespace.
    private static final List<String> CONFIRMATION_VALUES = Arrays.asList(
        // Absent and whitespace-only.
        null,
        "",
        " ",
        "  ",
        "\t",

        // Below and at the boundary.
        "abcde",
        "abcde ",
        "abcdef",

        // Case-only differences from "abcdef".
        "ABCDEF",
        "Abcdef",
        "abcdeF",

        // Whitespace-only differences from "abcdef".
        " abcdef",
        "abcdef ",
        " abcdef ",
        "abcdef\t",
        "\nabcdef",

        // Unrelated accepted values, and a non-ASCII one.
        "123456",
        "!@#$%^",
        "correct horse battery staple",
        "\u00e9bcdef"
    );

    // Full cross product of the values, plus seeded random pairs including single-character near-misses.
    private static Stream<Arguments> provideConfirmationPairs() {
        List<Arguments> cases = new ArrayList<Arguments>();

        for (String candidate : CONFIRMATION_VALUES) {
            for (String confirmation : CONFIRMATION_VALUES) {
                cases.add(Arguments.of(candidate, confirmation));
            }
        }

        Random random = new Random(GENERATOR_SEED);

        for (int i = 0; i < RANDOM_CASE_COUNT; i++) {
            String candidate = randomCandidate(random);

            String confirmation;

            switch (random.nextInt(4)) {
                case 0:
                    // Same raw value, so the accepting side is drawn too.
                    confirmation = candidate;
                    break;
                case 1:
                    confirmation = perturb(candidate, random);
                    break;
                case 2:
                    confirmation = randomCandidate(random);
                    break;
                default:
                    confirmation = null;
                    break;
            }

            cases.add(Arguments.of(candidate, confirmation));
        }

        return cases.stream();
    }

    // Changes one character, or appends one to an empty value, so the result never equals the input.
    private static String perturb(String value, Random random) {
        if (value.length() == 0) {
            return "x";
        }

        char[] characters = value.toCharArray();

        int position = random.nextInt(characters.length);

        characters[position] = characters[position] == 'x' ? 'y' : 'x';

        return new String(characters);
    }

    // Independent oracle with the evaluation order: missing, then mismatch, then too short.
    private static Result expectedResult(String candidate, String confirmation) {
        if (candidate == null || candidate.trim().length() == 0) {
            return Result.MISSING;
        }

        if (!candidate.equals(confirmation)) {
            return Result.MISMATCH;
        }

        if (candidate.trim().length() < 6) {
            return Result.TOO_SHORT;
        }

        return Result.ACCEPTED;
    }

    // Mismatch is reported exactly when a present candidate differs from the confirmation on the raw strings, not the trimmed ones.
    @ParameterizedTest(name = "[{index}] candidate=''{0}'' confirmation=''{1}''")
    @MethodSource("provideConfirmationPairs")
    public void testConfirmationMismatch(String candidate, String confirmation) {
        Result expected = expectedResult(candidate, confirmation);

        String description = "Decision for candidate '" + candidate + "' and confirmation '" + confirmation + "'";

        Result actual = PasswordValidator.validate(candidate, confirmation);

        assertEquals(expected, actual, description);

        boolean candidatePresent = candidate != null && candidate.trim().length() > 0;

        assertEquals(
            Boolean.valueOf(candidatePresent && !candidate.equals(confirmation)),
            Boolean.valueOf(Result.MISMATCH.equals(actual)),
            description + " reported as a mismatch");

        if (Result.MISMATCH.equals(actual)) {
            assertEquals("The passwords do not match!", PasswordValidator.messageFor(actual), description + " message");
        }
    }

    // Each core has a non-blank trimmed value, with trimmed lengths on both sides of the boundary.
    private static final List<String> STORAGE_CORES = Arrays.asList(
        // Below the boundary.
        "a",
        "abc",
        "abcde",
        "12345",
        "!@#$%",

        // At and above the boundary.
        "abcdef",
        "ABCDEF",
        "123456",
        "!@#$%^",
        "aB3$x.",
        "abcdefg",
        "correct horse battery staple",
        "\u00e9\u00e8\u00ea\u00eb\u00ec\u00ed",
        "\u4e2d\u6587\u5bc6\u7801\u5f88\u5f3a"
    );

    // No empty run: each core is also emitted unpadded, so every padded case differs from its trimmed form.
    private static final List<String> STORAGE_PADDING = Arrays.asList(
        " ",
        "   ",
        "\t",
        "\n",
        "\r\n",
        " \t\n\r ",
        "          "
    );

    // Each core padded leading, trailing and both sides, plus seeded random padded draws.
    private static Stream<Arguments> provideStorageCandidates() {
        Set<String> candidates = new LinkedHashSet<String>();

        for (String core : STORAGE_CORES) {
            candidates.add(core);

            for (String padding : STORAGE_PADDING) {
                candidates.add(padding + core);
                candidates.add(core + padding);
                candidates.add(padding + core + padding);
            }
        }

        Random random = new Random(GENERATOR_SEED);

        for (int i = 0; i < RANDOM_CASE_COUNT; i++) {
            String core = STORAGE_CORES.get(random.nextInt(STORAGE_CORES.size()));

            String leading = STORAGE_PADDING.get(random.nextInt(STORAGE_PADDING.size()));
            String trailing = STORAGE_PADDING.get(random.nextInt(STORAGE_PADDING.size()));

            candidates.add(leading + core + trailing);
        }

        List<Arguments> cases = new ArrayList<Arguments>();

        for (String candidate : candidates) {
            cases.add(Arguments.of(candidate));
        }

        return cases.stream();
    }

    // Records the exact string handed to it; a real BCrypt encoder would only slow each case down.
    private static final class RecordingEncoder {

        private String encoded;

        private int invocations;

        private String encode(String raw) {
            this.encoded = raw;
            this.invocations++;

            return "{encoded}" + raw;
        }

    }

    // Validate the pair; on ACCEPTED encode the untrimmed candidate, and store nothing on rejection.
    private static Result validateThenStore(String candidate, String confirmation, RecordingEncoder encoder) {
        Result result = PasswordValidator.validate(candidate, confirmation);

        if (Result.ACCEPTED.equals(result)) {
            encoder.encode(candidate);
        }

        return result;
    }

    // Acceptance uses the trimmed length, but the encoder receives the untrimmed candidate itself.
    @ParameterizedTest(name = "[{index}] candidate=''{0}''")
    @MethodSource("provideStorageCandidates")
    public void testUntrimmedStorage(String candidate) {
        String trimmed = candidate.trim();

        String description = "Candidate '" + candidate + "'";

        RecordingEncoder encoder = new RecordingEncoder();

        Result result = validateThenStore(candidate, candidate, encoder);

        assertEquals(
            Boolean.valueOf(trimmed.length() >= PasswordValidator.MINIMUM_TRIMMED_LENGTH),
            Boolean.valueOf(Result.ACCEPTED.equals(result)),
            description + " accepted on its trimmed length");

        assertEquals(
            PasswordValidator.validate(trimmed, trimmed),
            result,
            description + " decided as its trimmed form is decided");

        if (!Result.ACCEPTED.equals(result)) {
            assertEquals(0, encoder.invocations, description + " handed to the encoder despite rejection");
            assertNull(encoder.encoded, description + " recorded despite rejection");

            return;
        }

        assertEquals(1, encoder.invocations, description + " encoder invocation count");
        assertSame(candidate, encoder.encoded, description + " handed to the encoder as the submitted instance");
        assertEquals(candidate, encoder.encoded, description + " handed to the encoder untrimmed");
        assertEquals(candidate.length(), encoder.encoded.length(), description + " length preserved into storage");

        if (!candidate.equals(trimmed)) {
            assertTrue(!trimmed.equals(encoder.encoded), description + " stored trimmed rather than untrimmed");
        }

        // The validator must offer no normalised form to store; messageFor is its only String-returning member.
        for (Method method : PasswordValidator.class.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers()) || method.isSynthetic()) {
                continue;
            }

            if (String.class.equals(method.getReturnType())) {
                assertEquals("messageFor", method.getName(), "The only String-returning member of the validator");
            }
        }
    }

}
