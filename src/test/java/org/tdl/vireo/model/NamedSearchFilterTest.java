package org.tdl.vireo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NamedSearchFilterTest extends AbstractEntityTest {

    // TODO: write missing tests!!

    @BeforeEach
    public void setUp() {
        creator = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
    }

    @Override
    @Test
    public void testCreate() {

    }

    @Override
    @Test
    public void testDuplication() {

    }

    @Override
    @Test
    public void testDelete() {

    }

    @Override
    @Test
    public void testCascade() {

    }

    @Test
    public void testSetActiveFilter() {

        long numberOfNamedSearchFilterGroups = namedSearchFilterGroupRepo.count();

        assertEquals(0, numberOfNamedSearchFilterGroups, "There already exists a named search filter group!");

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

    @AfterEach
    public void cleanUp() {
        namedSearchFilterGroupRepo.findAll().forEach(nsfg -> {
            namedSearchFilterGroupRepo.delete(nsfg);
        });
        namedSearchFilterRepo.findAll().forEach(nsf -> {
            namedSearchFilterRepo.delete(nsf);
        });
        filterCriterionRepo.deleteAll();
        submissionListColumnRepo.deleteAll();
        inputTypeRepo.deleteAll();
        userRepo.deleteAll();
    }

}
