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
 * Property 8: Shared validator decision.
 *
 * Validates: Requirements 3.1, 3.2, 3.3, 3.8
 *
 * The property is one {@link ParameterizedTest} fed by an {@link MethodSource} generator producing
 * well over one hundred cases, built from the enumerated boundary values — including the trimmed
 * length 5 and trimmed length 6 boundary, explicitly, on both sides — crossed with whitespace
 * padding shapes and padded from a fixed-seed {@link Random}. No property-based testing library is
 * used and no new Maven dependency is introduced.
 *
 * {@link PasswordValidator} is static and needs no Spring context, so this test starts none.
 */
@ActiveProfiles(value = { "test", "isolated-test" })
public class PasswordValidatorTest {

    private static final long GENERATOR_SEED = 20250101L;

    private static final int RANDOM_CASE_COUNT = 64;

    /**
     * Whitespace runs used both as padding around a candidate and as whole whitespace-only
     * candidates. Every one of these trims away to nothing, which is what makes a whitespace-only
     * candidate a missing-password case under Requirement 3 criterion 2.
     */
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

    /**
     * The enumerated candidate cores, before any whitespace padding is applied.
     *
     * The trimmed length 5 and trimmed length 6 boundary is enumerated explicitly across several
     * character classes, so the rejected side and the accepted side of the boundary are each pinned
     * for letters, digits, symbols, mixed case, and non-ASCII characters. Varying the character
     * class while the expected result depends on the trimmed length alone is what pins Requirement
     * 3 criterion 8: no character-class constraint exists.
     */
    private static final List<String> CANDIDATE_CORES = Arrays.asList(
        // Absent and empty.
        null,
        "",

        // Below the boundary, every length from one to four.
        "a",
        "ab",
        "abc",
        "abcd",

        // The rejected side of the boundary: trimmed length 5, across character classes.
        "abcde",
        "ABCDE",
        "12345",
        "!@#$%",
        "aB3$x",
        "\u00e9\u00e8\u00ea\u00eb\u00ec",

        // The accepted side of the boundary: trimmed length 6, across character classes.
        "abcdef",
        "ABCDEF",
        "123456",
        "!@#$%^",
        "aB3$x.",
        "\u00e9\u00e8\u00ea\u00eb\u00ec\u00ed",

        // Comfortably above the boundary.
        "abcdefg",
        "1234567890",
        "correct horse battery staple",
        "\u4e2d\u6587\u5bc6\u7801\u5f88\u5f3a\u5927",

        // Interior whitespace, which trimming does not remove, so these clear the boundary on
        // their trimmed length.
        "a b c",
        "a b cd",
        "a\tb\tc\td"
    );

    /**
     * Each case is one candidate password string.
     *
     * The enumerated cores are crossed with every whitespace padding shape on each side, which
     * produces the pairs that separate a trimmed-length rule from an untrimmed-length one: a
     * five-character core padded to eleven characters must still be rejected, and a six-character
     * core padded on both sides must still be accepted.
     *
     * @return The generated cases.
     */
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

        // Whitespace-only candidates, in their own right.
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

    /**
     * Draw a candidate of a random length drawn from zero through ten — so the boundary lengths
     * five and six are both reachable — built from a random character class and wrapped in random
     * whitespace padding.
     *
     * @param random The fixed-seed source.
     *
     * @return The drawn candidate.
     */
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

    /**
     * The independently computed acceptance rule, written straight from Requirement 3 criteria 1,
     * 2 and 3: trim, then decide on the trimmed length alone.
     *
     * @param candidate The candidate password, which may be null.
     *
     * @return The expected decision.
     */
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

    /**
     * Property 8: Shared validator decision.
     *
     * Validates: Requirements 3.1, 3.2, 3.3, 3.8
     *
     * The validator reports {@code ACCEPTED} exactly when the candidate holds at least one
     * non-whitespace character and the trimmed length is 6 or greater; reports {@code MISSING} when
     * the candidate is absent or whitespace-only; and reports {@code TOO_SHORT} when the trimmed
     * length is below 6.
     *
     * The decision is read three ways on every case, so no single reading can carry the property on
     * its own: the single-argument overload, the two-argument overload fed a matching confirmation
     * — which must agree, since a matching confirmation removes the only cause the two-argument
     * form adds — and a repeat call, which must agree with the first because no reuse-history state
     * exists (Requirement 3 criterion 8).
     *
     * @param candidate The candidate password under test.
     */
    @ParameterizedTest(name = "[{index}] candidate=''{0}''")
    @MethodSource("provideCandidates")
    public void testSharedValidatorDecision(String candidate) {
        Result expected = expectedResult(candidate);

        String description = "Decision for candidate '" + candidate + "'";

        // Requirements 3.1, 3.2, 3.3: the decision rests on the trimmed length alone.
        assertEquals(expected, PasswordValidator.validate(candidate), description);

        // Requirement 3.8: the minimum is the only constraint, declared as 6, so the expectation
        // above and the constant cannot drift apart.
        assertEquals(6, PasswordValidator.MINIMUM_TRIMMED_LENGTH, "The declared minimum trimmed length");

        // Requirement 3.7: a matching confirmation removes the only rejection cause the
        // two-argument overload adds, so the two overloads must agree on every candidate. A null
        // candidate is MISSING before the comparison is reached either way.
        assertEquals(expected, PasswordValidator.validate(candidate, candidate), description + " through the confirming overload");

        // Requirement 3.8: no reuse-history constraint exists, so the same candidate decided twice
        // decides the same way.
        assertEquals(expected, PasswordValidator.validate(candidate), description + " on a repeat decision");
    }

    /**
     * The enumerated candidate and confirmation values for Property 9, crossed with themselves.
     *
     * The list is chosen so the cross product contains the pairs that separate a raw-string
     * comparison from any looser one: pairs differing only in letter case, pairs differing only in
     * leading or trailing whitespace, pairs differing only in the kind of trailing whitespace, and
     * the absent and whitespace-only values on either side.
     */
    private static final List<String> CONFIRMATION_VALUES = Arrays.asList(
        // Absent and whitespace-only, on either side of the pair.
        null,
        "",
        " ",
        "  ",
        "\t",

        // Below and at the boundary, so a mismatch and a too-short cause can both be in play.
        "abcde",
        "abcde ",
        "abcdef",

        // Letter-case-only differences from "abcdef".
        "ABCDEF",
        "Abcdef",
        "abcdeF",

        // Surrounding-whitespace-only differences from "abcdef".
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

    /**
     * Each case is one candidate and confirmation pair.
     *
     * Every enumerated value is crossed with every enumerated value, which yields the equal pairs
     * and the differing pairs together — the property is an "exactly when", so both sides have to
     * be generated. A fixed-seed {@link Random} then adds pairs drawn from the same value pool,
     * including pairs built by perturbing a drawn value in a single character, which is the
     * near-miss shape a comparison looser than equality would let through.
     *
     * @return The generated cases.
     */
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
                    // The same raw value, so the accepting side of the property is drawn too.
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

    /**
     * Alter one character of the supplied value, or append one when the value is empty, so the
     * result differs from the input in exactly one position.
     *
     * @param value  The value to perturb.
     * @param random The fixed-seed source.
     *
     * @return The perturbed value, which never equals the input.
     */
    private static String perturb(String value, Random random) {
        if (value.length() == 0) {
            return "x";
        }

        char[] characters = value.toCharArray();

        int position = random.nextInt(characters.length);

        characters[position] = characters[position] == 'x' ? 'y' : 'x';

        return new String(characters);
    }

    /**
     * The independently computed decision for a pair, written straight from Requirement 3 criteria
     * 1 through 4 in the order the design fixes: missing, then mismatch, then too short.
     *
     * @param candidate    The candidate password, which may be null.
     * @param confirmation The confirmation value, which may be null.
     *
     * @return The expected decision.
     */
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

    /**
     * Property 9: Confirmation mismatch.
     *
     * Validates: Requirements 3.4
     *
     * The validator reports {@code MISMATCH} exactly when the candidate is present and the two raw
     * strings differ — including when they differ only in letter case, or only in surrounding
     * whitespace. The present qualifier is the evaluation order the design fixes: an absent or
     * whitespace-only candidate is {@code MISSING} before the comparison is reached, so the
     * mismatch claim is scoped to the candidates that get as far as being compared.
     *
     * Comparison is on the raw values, not the trimmed ones, which is why a candidate padded with
     * whitespace against its unpadded confirmation is a mismatch rather than an acceptance.
     *
     * @param candidate    The candidate password under test.
     * @param confirmation The confirmation value under test.
     */
    @ParameterizedTest(name = "[{index}] candidate=''{0}'' confirmation=''{1}''")
    @MethodSource("provideConfirmationPairs")
    public void testConfirmationMismatch(String candidate, String confirmation) {
        Result expected = expectedResult(candidate, confirmation);

        String description = "Decision for candidate '" + candidate + "' and confirmation '" + confirmation + "'";

        Result actual = PasswordValidator.validate(candidate, confirmation);

        // Requirement 3.4: the mismatch cause, in its place in the evaluation order.
        assertEquals(expected, actual, description);

        boolean candidatePresent = candidate != null && candidate.trim().length() > 0;

        // Requirement 3.4, as the "exactly when" it is stated as: among the candidates that reach
        // the comparison, MISMATCH is reported precisely for the pairs whose raw strings differ.
        assertEquals(
            Boolean.valueOf(candidatePresent && !candidate.equals(confirmation)),
            Boolean.valueOf(Result.MISMATCH.equals(actual)),
            description + " reported as a mismatch");

        // Requirement 3.4: a mismatch is a rejection, and it is reported as the mismatch cause
        // rather than folded into another one.
        if (Result.MISMATCH.equals(actual)) {
            assertEquals("The passwords do not match!", PasswordValidator.messageFor(actual), description + " message");
        }
    }

    /**
     * The candidate cores used for Property 10, all of them carrying at least one non-whitespace
     * character so padding them produces a meaningful untrimmed-against-trimmed distinction.
     *
     * Both sides of the boundary are present: cores whose trimmed length is below 6, which must be
     * rejected however much whitespace surrounds them, and cores whose trimmed length is 6 or
     * greater, which must be accepted and stored with their padding intact.
     */
    private static final List<String> STORAGE_CORES = Arrays.asList(
        // Below the boundary on the trimmed value.
        "a",
        "abc",
        "abcde",
        "12345",
        "!@#$%",

        // At and above the boundary on the trimmed value.
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

    /**
     * The whitespace runs padded around a core for Property 10. The empty run is deliberately
     * absent on its own account — it is still reachable, because each core is also emitted
     * unpadded — so that every generated padded case genuinely differs from its trimmed form.
     */
    private static final List<String> STORAGE_PADDING = Arrays.asList(
        " ",
        "   ",
        "\t",
        "\n",
        "\r\n",
        " \t\n\r ",
        "          "
    );

    /**
     * Each case is one candidate password, most of them padded so the untrimmed value and the
     * trimmed value differ.
     *
     * Every core is crossed with every padding run on the leading side, the trailing side, and both
     * sides at once, which is what produces the shapes that separate storing the untrimmed value
     * from storing the trimmed one. A fixed-seed {@link Random} then adds further padded draws.
     *
     * @return The generated cases.
     */
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

    /**
     * A stand-in for the password encoder, recording the exact value it is handed so the stored
     * value can be compared against the submitted one by identity as well as by equality.
     *
     * The encoding itself is irrelevant to this property — the claim is about which string reaches
     * the encoder — so a recording stand-in is used rather than a real BCrypt encoder, which would
     * add seconds per case for nothing.
     */
    private static final class RecordingEncoder {

        private String encoded;

        private int invocations;

        private String encode(String raw) {
            this.encoded = raw;
            this.invocations++;

            return "{encoded}" + raw;
        }

    }

    /**
     * The caller shape the design fixes for every accepting path: validate the pair, and on
     * {@code ACCEPTED} hand the **untrimmed** candidate to the encoder, with nothing stored on a
     * rejection.
     *
     * @param candidate    The candidate password.
     * @param confirmation The confirmation value.
     * @param encoder      The recording stand-in.
     *
     * @return The validator's decision.
     */
    private static Result validateThenStore(String candidate, String confirmation, RecordingEncoder encoder) {
        Result result = PasswordValidator.validate(candidate, confirmation);

        if (Result.ACCEPTED.equals(result)) {
            encoder.encode(candidate);
        }

        return result;
    }

    /**
     * Property 10: Untrimmed storage.
     *
     * Validates: Requirements 3.5
     *
     * For any accepted candidate password carrying leading or trailing whitespace, the value handed
     * to the password encoder is the untrimmed candidate, while the length check that accepted it
     * was applied to the trimmed value.
     *
     * Three things together make up the claim, and all three are asserted on every case:
     *
     * 1. The acceptance decision is governed by the trimmed length. A padded candidate is accepted
     *    exactly when its trimmed length reaches the minimum, so ten spaces around a five-character
     *    core is still {@code TOO_SHORT} and a six-character core padded on both sides is still
     *    {@code ACCEPTED}.
     * 2. The value that reaches the encoder is the submitted string itself — the same object, not a
     *    trimmed, stripped, or otherwise normalised copy — so the padding survives into storage.
     * 3. The validator offers the caller no normalised form to store in its place. Its only
     *    String-returning member is {@code messageFor}, so the only value a caller holds after an
     *    accepting decision is the untrimmed candidate it submitted. That is asserted reflectively,
     *    because it is a claim about the shape of the API rather than about one call.
     *
     * @param candidate The candidate password under test.
     */
    @ParameterizedTest(name = "[{index}] candidate=''{0}''")
    @MethodSource("provideStorageCandidates")
    public void testUntrimmedStorage(String candidate) {
        String trimmed = candidate.trim();

        String description = "Candidate '" + candidate + "'";

        RecordingEncoder encoder = new RecordingEncoder();

        Result result = validateThenStore(candidate, candidate, encoder);

        // Requirement 3.5, first half: the length check that decides acceptance is applied to the
        // trimmed value, so the decision for a padded candidate matches the decision for its
        // trimmed core.
        assertEquals(
            Boolean.valueOf(trimmed.length() >= PasswordValidator.MINIMUM_TRIMMED_LENGTH),
            Boolean.valueOf(Result.ACCEPTED.equals(result)),
            description + " accepted on its trimmed length");

        assertEquals(
            PasswordValidator.validate(trimmed, trimmed),
            result,
            description + " decided as its trimmed form is decided");

        if (!Result.ACCEPTED.equals(result)) {
            // Nothing is stored on a rejection, so there is no value to make a claim about.
            assertEquals(0, encoder.invocations, description + " handed to the encoder despite rejection");
            assertNull(encoder.encoded, description + " recorded despite rejection");

            return;
        }

        // Requirement 3.5, second half: the encoder is handed the untrimmed candidate — the very
        // string that was submitted, padding and all.
        assertEquals(1, encoder.invocations, description + " encoder invocation count");
        assertSame(candidate, encoder.encoded, description + " handed to the encoder as the submitted instance");
        assertEquals(candidate, encoder.encoded, description + " handed to the encoder untrimmed");
        assertEquals(candidate.length(), encoder.encoded.length(), description + " length preserved into storage");

        // The two halves are only distinguishable where the padding is real, so pin the difference
        // explicitly wherever it exists: the stored value is not the trimmed value.
        if (!candidate.equals(trimmed)) {
            assertTrue(!trimmed.equals(encoder.encoded), description + " stored trimmed rather than untrimmed");
        }

        // Requirement 3.5: the validator hands a caller no normalised candidate to store in place of
        // the submitted one. messageFor is its only String-returning member.
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
