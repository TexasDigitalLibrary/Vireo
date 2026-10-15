package org.tdl.vireo.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.User;

/**
 * Property 5: Eligibility predicate.
 *
 * Validates: Requirements 2.1, 2.2, 2.3, 2.8, 16.8
 *
 * The property is one {@link ParameterizedTest} fed by an {@link MethodSource} generator producing
 * well over one hundred cases, built from the cross product of the enumerated stored
 * {@code User.password} values, the enumerated stored {@code User.netid} values, and the enumerated
 * {@code User.shibbolethAffiliations} values. No property-based testing library is used and no new
 * Maven dependency is introduced.
 *
 * {@link PasswordEligibilityRule} is static and stateless, so this test starts no Spring context
 * and wires no mock.
 */
@ActiveProfiles(value = { "test", "isolated-test" })
public class PasswordEligibilityRuleTest {

    /**
     * The enumerated stored password values.
     *
     * The blank shapes are enumerated separately from the absent one because Requirement 2
     * criterion 2 routes null, empty, and whitespace-only alike to ineligible, and a rule written
     * against {@code isEmpty} rather than a trim would separate them. The non-breaking space is
     * included deliberately: {@code String.trim} removes characters at or below U+0020 only, so
     * U+00A0 is a non-whitespace character under the rule as stated and a password composed of one
     * is eligible.
     */
    private static final List<String> PASSWORDS = Arrays.asList(
        // Absent and blank: ineligible under criterion 2.
        null,
        "",
        " ",
        "   ",
        "\t\n",
        "\r\n ",

        // Non-blank: the eligible side of criterion 1, when the netid is null.
        "$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123",
        "x",
        "abc",
        "  padded  ",
        "\t spaced \n",
        "\u00a0"
    );

    /**
     * The enumerated stored netid values.
     *
     * The blank values are load-bearing: criterion 2 states that a non-null netid makes an Account
     * ineligible <em>including</em> an empty or whitespace-only netid, which a rule written as a
     * blank test rather than a null test would wrongly treat as eligible. The blank netid is
     * reachable in practice, because the account-creation branch of
     * {@code updateUserByCredentials} sets the netid with no emptiness guard.
     */
    private static final List<String> NETIDS = Arrays.asList(
        // The only eligible netid under criterion 1.
        null,

        // Non-null, so ineligible under criterion 2, blank or not.
        "",
        " ",
        "\t",
        "jdoe1",
        "JDOE1",
        "a-very-long-institutional-net-identifier-0123456789-0123456789"
    );

    /**
     * The enumerated {@code User.shibbolethAffiliations} values, varied across the case list while
     * the expected result never changes. That invariance is what pins Requirement 2 criterion 8:
     * the rule must consult this field in no part of the classification.
     *
     * The field is a {@code Set<String>}, and the set the {@link User} constructor installs is a
     * {@link TreeSet}, which holds no null element, so every element here is non-null.
     */
    private static final List<Set<String>> AFFILIATIONS = Arrays.asList(
        null,
        affiliations(),
        affiliations("student"),
        affiliations("staff", "student"),
        affiliations("alum", "faculty", "member", "staff", "student")
    );

    /**
     * Build an affiliation set in the shape the {@link User} constructor installs.
     *
     * @param values The affiliation values.
     *
     * @return The set.
     */
    private static Set<String> affiliations(String... values) {
        Set<String> set = new TreeSet<String>();

        set.addAll(Arrays.asList(values));

        return set;
    }

    /**
     * Each case is one stored password value crossed with one stored netid value crossed with one
     * affiliation value.
     *
     * The full cross product is generated rather than a sample, because the property is an "exactly
     * when" over the combination: only the non-blank-password-and-null-netid cells are expected
     * eligible, and every other cell is expected ineligible.
     *
     * @return The generated cases.
     */
    private static Stream<Arguments> providePasswordNetidAndAffiliationCombinations() {
        List<Arguments> cases = new ArrayList<Arguments>();

        for (String password : PASSWORDS) {
            for (String netid : NETIDS) {
                for (Set<String> affiliation : AFFILIATIONS) {
                    cases.add(Arguments.of(password, netid, affiliation));
                }
            }
        }

        return cases.stream();
    }

    /**
     * The independently computed predicate, written straight from Requirement 2 criterion 1: the
     * password holds at least one non-whitespace character and the netid is null.
     *
     * The affiliation value is deliberately not a parameter of this computation, which is the
     * statement of Requirement 2 criterion 8 in code.
     *
     * @param password The stored password value, which may be null.
     * @param netid    The stored netid value, which may be null.
     *
     * @return The expected classification.
     */
    private static boolean expectedEligible(String password, String netid) {
        return netid == null && password != null && password.trim().length() > 0;
    }

    /**
     * Construct an Account carrying the supplied stored field values, leaving every other field as
     * the constructor set it.
     *
     * @param password     The stored password value.
     * @param netid        The stored netid value.
     * @param affiliation  The affiliation value, which may be null.
     *
     * @return The Account.
     */
    private static User accountOf(String password, String netid, Set<String> affiliation) {
        User user = new User();

        user.setPassword(password);
        user.setNetid(netid);
        user.setShibbolethAffiliations(affiliation);

        return user;
    }

    /**
     * Property 5: Eligibility predicate.
     *
     * Validates: Requirements 2.1, 2.2, 2.3, 2.8, 16.8
     *
     * The rule returns eligible exactly when the stored password holds at least one non-whitespace
     * character and the stored netid is null, and ineligible for every other combination.
     *
     * The same case drives both overloads, so the field-level form and the Account form cannot
     * drift apart, and both are compared against the independently computed predicate rather than
     * against each other alone — two forms that agree with each other and disagree with the
     * requirement would otherwise pass.
     *
     * @param password    The stored password value under test.
     * @param netid       The stored netid value under test.
     * @param affiliation The affiliation value under test.
     */
    @ParameterizedTest(name = "[{index}] password=''{0}'' netid=''{1}'' affiliations={2}")
    @MethodSource("providePasswordNetidAndAffiliationCombinations")
    public void testEligibilityPredicate(String password, String netid, Set<String> affiliation) {
        boolean expected = expectedEligible(password, netid);

        String description = "Eligibility for password '" + password + "', netid '" + netid + "', affiliations " + affiliation;

        // Requirements 2.1, 2.2, 2.3: the Account form, classified from the stored fields at
        // evaluation time.
        assertEquals(
            Boolean.valueOf(expected),
            Boolean.valueOf(PasswordEligibilityRule.isEligible(accountOf(password, netid, affiliation))),
            description + " through the Account form");

        // Requirements 2.1, 2.2, 2.3: the field-level form, driven by the same case so the two
        // forms cannot drift.
        assertEquals(
            Boolean.valueOf(expected),
            Boolean.valueOf(PasswordEligibilityRule.isEligible(password, netid)),
            description + " through the field-level form");

        // Requirement 2.3: no tagging and no migration, so the same stored values classify the same
        // way on a repeat evaluation and across two separately constructed Accounts.
        assertEquals(
            Boolean.valueOf(expected),
            Boolean.valueOf(PasswordEligibilityRule.isEligible(accountOf(password, netid, affiliation))),
            description + " on a repeat evaluation of an equivalent Account");
    }

    /**
     * Property 5, the invariance half, stated as its own assertion rather than left to fall out of
     * the case list.
     *
     * Validates: Requirements 2.8, 16.8
     *
     * For one password and netid pair, every enumerated affiliation value is classified, and all of
     * the results are asserted equal to one another and equal to the field-level result — which
     * cannot see the field at all, because the field is not among its arguments. Varying the field
     * while the result is pinned to the value produced without the field is what makes a rule that
     * reads {@code User.shibbolethAffiliations} fail.
     *
     * @param password    The stored password value under test.
     * @param netid       The stored netid value under test.
     * @param affiliation Ignored here: this test varies the affiliation itself. The parameter keeps
     *                    the generator shared with the predicate test, so the two cannot drift.
     */
    @ParameterizedTest(name = "[{index}] password=''{0}'' netid=''{1}''")
    @MethodSource("providePasswordNetidAndAffiliationCombinations")
    public void testAffiliationsAreNotConsulted(String password, String netid, Set<String> affiliation) {
        // The result computed with no affiliation value in play at all: the field-level overload
        // takes the password and the netid only.
        boolean withoutAffiliations = PasswordEligibilityRule.isEligible(password, netid);

        Set<Boolean> observed = new LinkedHashSet<Boolean>();

        for (Set<String> candidate : AFFILIATIONS) {
            boolean actual = PasswordEligibilityRule.isEligible(accountOf(password, netid, candidate));

            observed.add(Boolean.valueOf(actual));

            // Requirement 2.8: the affiliation value changes nothing, including against the form
            // that has no access to it.
            assertEquals(
                Boolean.valueOf(withoutAffiliations),
                Boolean.valueOf(actual),
                "Affiliations " + candidate + " altered the classification for password '" + password + "' and netid '" + netid + "'");
        }

        // Requirement 2.8: one distinct result across every affiliation shape — null, empty,
        // single-element, and multi-element — so the field is provably not a term of the rule.
        assertEquals(
            1,
            observed.size(),
            "Distinct classifications observed across the affiliation shapes for password '" + password + "' and netid '" + netid + "'");

        // The classification still tracks the requirement, so the invariance above is not the
        // invariance of a rule that returns a constant.
        assertEquals(
            Boolean.valueOf(expectedEligible(password, netid)),
            Boolean.valueOf(withoutAffiliations),
            "Classification for password '" + password + "' and netid '" + netid + "'");
    }

    /**
     * Property 5, the null-Account case.
     *
     * Validates: Requirements 2.2
     *
     * No Account resolving for the supplied identifier is ineligible, which is the fail-closed
     * direction criterion 2 fixes.
     */
    @Test
    public void testNullAccountIsIneligible() {
        assertFalse(PasswordEligibilityRule.isEligible((User) null), "A null Account classified as eligible");
    }

}
