package org.tdl.vireo.model;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Arrays;

public class NamedSearchFilterTest extends AbstractEntityTest {

    // TODO: write missing tests!!

    @Before
    public void setUp() {
        creator = userRepo.create(TEST_USER_EMAIL, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME, TEST_USER_ROLE);
    }

    @Override
    public void testCreate() {

    }

    @Override
    public void testDuplication() {

    }

    @Override
    public void testDelete() {

    }

    @Override
    public void testCascade() {

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetActiveFilter() {

        long numberOfNamedSearchFilterGroups = namedSearchFilterGroupRepo.count();

        // user create implicitly creates a filter
        assertEquals("There already exists a named search filter group!", 1, numberOfNamedSearchFilterGroups);

        long numberOfNamedSearchFilters = namedSearchFilterRepo.count();

        assertEquals("There already exists a named search filter!", 0, numberOfNamedSearchFilters);

        InputType inputType = inputTypeRepo.create(TEST_FIELD_PROFILE_INPUT_TEXT_NAME);

        SubmissionListColumn submissionListColumn = submissionListColumnRepo.create("TEST_LABEL", Sort.ASC, Arrays.asList(new String[] { "test.path" }), inputType);

        NamedSearchFilter namedSearchFilter = namedSearchFilterRepo.create(submissionListColumn);

        numberOfNamedSearchFilters = namedSearchFilterRepo.count();

        assertEquals("There already exists a named search filter!", 1, numberOfNamedSearchFilters);

        namedSearchFilter.addFilter(filterCriterionRepo.create("FILTER_ONE"));
        namedSearchFilter.addFilter(filterCriterionRepo.create("FILTER_TWO"));

        assertEquals("There are more filter criterion than expected!", 2, filterCriterionRepo.count());

        Set<NamedSearchFilter> namedSearchFilters = new HashSet<NamedSearchFilter>();
        namedSearchFilters.add(namedSearchFilter);

        NamedSearchFilterGroup rawNamedSearchFilterGroup = new NamedSearchFilterGroup();

        rawNamedSearchFilterGroup.setUser(creator);

        rawNamedSearchFilterGroup.setNamedSearchFilters(namedSearchFilters);

        // NOTE: this method call also creates new named search filters
        NamedSearchFilterGroup namedSearchFilterGroup = namedSearchFilterGroupRepo.createFromFilter(rawNamedSearchFilterGroup);

        assertEquals("There are more named search filter groups than expected!", ++numberOfNamedSearchFilterGroups, namedSearchFilterGroupRepo.count());

        assertEquals("There are more named search filters than expected!", ++numberOfNamedSearchFilters, namedSearchFilterRepo.count());

        Set<NamedSearchFilter> persistedNamedSearchFilters = namedSearchFilterGroup.getNamedSearchFilters();

        assertEquals("Named search filter group had more named search filters than expected!", 1, persistedNamedSearchFilters.size());

        NamedSearchFilter persistedNamedSearchFilter = persistedNamedSearchFilters.toArray(new NamedSearchFilter[1])[0];

        Set<FilterCriterion> filterCriterion = persistedNamedSearchFilter.getFilters();

        assertEquals("Named search filter had more filter criterion than expected!", 2, filterCriterion.size());

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

        assertEquals("There are more filter criterion than expected!", 2, filterCriterionRepo.count());

    }

    @After
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
