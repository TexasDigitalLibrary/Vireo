package org.tdl.vireo.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.tdl.vireo.model.Role;
import org.tdl.vireo.model.Sort;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.UserRepo;
import org.tdl.vireo.model.repo.specification.UserSpecification;
import org.tdl.vireo.model.request.FilteredPageRequest;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserControllerTest {

    private static final String TEST_USER_1_EMAIL = "User 1 email";
    private static final String TEST_USER_2_EMAIL = "User 2 email";
    private static final String TEST_USER_1_FIRST_NAME = "User 1 first name";
    private static final String TEST_USER_2_FIRST_NAME = "User 2 first name";
    private static final String TEST_USER_1_LAST_NAME = "User 1 last name";
    private static final String TEST_USER_2_LAST_NAME = "User 2 last name";
    private static final Role TEST_USER_1_ROLE = Role.ROLE_ADMIN;
    private static final Role TEST_USER_2_ROLE = Role.ROLE_REVIEWER;


    private static final User TEST_USER_1 = new User(TEST_USER_1_EMAIL, TEST_USER_1_FIRST_NAME, TEST_USER_1_LAST_NAME, TEST_USER_1_ROLE);
    private static final User TEST_USER_2 = new User(TEST_USER_2_EMAIL, TEST_USER_2_FIRST_NAME, TEST_USER_2_LAST_NAME, TEST_USER_2_ROLE);

    private static final List<User> TEST_USER_LIST = new ArrayList<User>(Arrays.asList(TEST_USER_1, TEST_USER_2));

    private static final Page<User> TEST_USER_PAGE = new PageImpl<User>(TEST_USER_LIST);

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserController userController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(userRepo.findAll(Matchers.<Specification<User>>any(), any(Pageable.class))).thenReturn(TEST_USER_PAGE);
    }

    @Test
    public void testPage() {
        FilteredPageRequest fpr = new FilteredPageRequest();
        fpr.setPageNumber(0);
        fpr.setPageSize(2);
        ApiResponse response = userController.page(fpr);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(userRepo, times(1)).findAll(Matchers.<Specification<User>>any(), any(Pageable.class));
    }

}
