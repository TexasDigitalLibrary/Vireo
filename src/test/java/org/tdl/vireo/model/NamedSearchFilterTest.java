package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public class NamedSearchFilterTest extends AbstractEntityTest {

    // TODO: write missing tests!!

    @BeforeEach
    public void setUp() {
        creator = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
    }

    @Override
    @Test
    @Disabled
    public void testCreate() {

    }

    @Override
    @Test
    @Disabled
    public void testDuplication() {

    }

    @Override
    @Test
    @Disabled
    public void testDelete() {

    }

    @Override
    @Test
    @Disabled
    public void testCascade() {

    }

    @Test
    @Disabled // FIXME: see problem notes below, createFromFilter() appears to be only used in tests.
    public void testSetActiveFilter() {

        long numberOfNamedSearchFilterGroups = namedSearchFilterGroupRepo.count();

        // user create implicitly creates a filter
        assertEquals(1, numberOfNamedSearchFilterGroups, "There already exists a named search filter group!");

        long numberOfNamedSearchFilters = namedSearchFilterRepo.count();

        assertEquals(0, numberOfNamedSearchFilters, "There already exists a named search filter!");

        InputType inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);

        SubmissionListColumn submissionListColumn = submissionListColumnRepo.create("TEST_LABEL", Sort.ASC, Arrays.asList(new String[] { "test.path" }), inputType);

        NamedSearchFilter namedSearchFilter = namedSearchFilterRepo.create(submissionListColumn);

        numberOfNamedSearchFilters = namedSearchFilterRepo.count();

        assertEquals(1, numberOfNamedSearchFilters, "There already exists a named search filter!");

        namedSearchFilter.addFilter(filterCriterionRepo.create("FILTER_ONE"));
        namedSearchFilter.addFilter(filterCriterionRepo.create("FILTER_TWO"));

        assertEquals(2, filterCriterionRepo.count(), "There are more filter criterion than expected!");

        Set<NamedSearchFilter> namedSearchFilters = new HashSet<NamedSearchFilter>();
        namedSearchFilters.add(namedSearchFilter);

        NamedSearchFilterGroup rawNamedSearchFilterGroup = new NamedSearchFilterGroup();

        rawNamedSearchFilterGroup.setUser(creator);

        rawNamedSearchFilterGroup.setNamedSearchFilters(namedSearchFilters);

        // NOTE: this method call also creates new named search filters
        // FIXME: namedSearchFilterGroupRepo.createFromFilter() throws UnsupportedOperation when in a transaction when calling namedSearchFilterRepo.save().
        NamedSearchFilterGroup namedSearchFilterGroup = namedSearchFilterGroupRepo.createFromFilter(rawNamedSearchFilterGroup);

        assertEquals(++numberOfNamedSearchFilterGroups, namedSearchFilterGroupRepo.count(), "There are more named search filter groups than expected!");

        assertEquals(++numberOfNamedSearchFilters, namedSearchFilterRepo.count(), "There are more named search filters than expected!");

        Set<NamedSearchFilter> persistedNamedSearchFilters = namedSearchFilterGroup.getNamedSearchFilters();

        assertEquals(1, persistedNamedSearchFilters.size(), "Named search filter group had more named search filters than expected!");

        NamedSearchFilter persistedNamedSearchFilter = persistedNamedSearchFilters.toArray(new NamedSearchFilter[1])[0];

        Set<FilterCriterion> filterCriterion = persistedNamedSearchFilter.getFilters();

        assertEquals(2, filterCriterion.size(), "Named search filter had more filter criterion than expected!");

        creator.setActiveFilter(namedSearchFilterGroup);

        creator = userRepo.save(creator);

        // Actual test case
        NamedSearchFilterGroup activeFilter = creator.getActiveFilter();
        activeFilter = namedSearchFilterGroupRepo.clone(activeFilter, namedSearchFilterGroup);

        if (activeFilter.getColumnsFlag()) {
            creator.getSubmissionViewColumns().clear();
            creator.getSubmissionViewColumns().addAll(creator.getActiveFilter().getSavedColumns());
        }

        creator = userRepo.save(creator);

        assertEquals(2, filterCriterionRepo.count(), "There are more filter criterion than expected!");
    }

}
