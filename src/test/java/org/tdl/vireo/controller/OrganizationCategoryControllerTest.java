package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationCategoryRepo;

@ActiveProfiles(value = { "test", "isolated-test" })
public class OrganizationCategoryControllerTest extends AbstractControllerTest {

    @Mock
    private OrganizationCategoryRepo organizationCategoryRepo;

    @InjectMocks
    private OrganizationCategoryController organizationCategoryController;

    private OrganizationCategory mockOrganizationCategory1;
    private OrganizationCategory mockOrganizationCategory2;

    private static List<OrganizationCategory> mockOrganizationCategorys;

    @BeforeEach
    public void setup() {
        mockOrganizationCategory1 = new OrganizationCategory("OrganizationCategory 1");
        mockOrganizationCategory1.setId(1L);

        mockOrganizationCategory2 = new OrganizationCategory("OrganizationCategory 2");
        mockOrganizationCategory2.setId(2L);

        mockOrganizationCategorys = new ArrayList<OrganizationCategory>(Arrays.asList(new OrganizationCategory[] { mockOrganizationCategory1 }));
    }

    @Test
    public void testAllOrganizationCategorys() {
        when(organizationCategoryRepo.findAll()).thenReturn(mockOrganizationCategorys);

        ApiResponse response = organizationCategoryController.getOrganizationCategories();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<OrganizationCategory>");
        assertEquals(mockOrganizationCategorys.size(), list.size());
    }

    @Test
    public void testCreateOrganizationCategory() {
        when(organizationCategoryRepo.create(any(String.class))).thenReturn(mockOrganizationCategory2);

        ApiResponse response = organizationCategoryController.createOrganizationCategory(mockOrganizationCategory1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        OrganizationCategory organizationCategory = (OrganizationCategory) response.getPayload().get("OrganizationCategory");
        assertEquals(mockOrganizationCategory2.getId(), organizationCategory.getId());
    }

    @Test
    public void testUpdateOrganizationCategory() {
        when(organizationCategoryRepo.update(any(OrganizationCategory.class))).thenReturn(mockOrganizationCategory2);

        ApiResponse response = organizationCategoryController.updateOrganizationCategory(mockOrganizationCategory1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        OrganizationCategory organizationCategory = (OrganizationCategory) response.getPayload().get("OrganizationCategory");
        assertEquals(mockOrganizationCategory2.getId(), organizationCategory.getId());
    }

    @Test
    public void testRemoveOrganizationCategory() {
        doNothing().when(organizationCategoryRepo).delete(any(OrganizationCategory.class));

        ApiResponse response = organizationCategoryController.removeOrganizationCategory(mockOrganizationCategory1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(organizationCategoryRepo, times(1)).delete(any(OrganizationCategory.class));
    }

}
