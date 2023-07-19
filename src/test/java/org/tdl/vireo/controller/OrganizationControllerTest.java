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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.OrganizationCategory;
import org.tdl.vireo.model.repo.OrganizationRepo;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class OrganizationControllerTest extends AbstractControllerTest {

    @Mock
    private OrganizationRepo organizationRepo;

    @InjectMocks
    private OrganizationController organizationController;

    private Organization mockOrganization1;
    private Organization mockOrganization2;

    private OrganizationCategory mockOrganizationCategory1;
    private OrganizationCategory mockOrganizationCategory2;

    private static List<Organization> mockOrganizations;

    @BeforeEach
    public void setup() {
        mockOrganizationCategory1 = new OrganizationCategory("1");
        mockOrganizationCategory1.setId(1L);

        mockOrganizationCategory2 = new OrganizationCategory("2");
        mockOrganizationCategory2.setId(2L);

        mockOrganization1 = new Organization("Organization 1", mockOrganizationCategory1);
        mockOrganization1.setId(1L);

        mockOrganization2 = new Organization("Organization 2", mockOrganizationCategory2);
        mockOrganization2.setId(2L);

        mockOrganizations = new ArrayList<Organization>(Arrays.asList(new Organization[] { mockOrganization1 }));
    }

    @Test
    public void testAllOrganizations() {
        when(organizationRepo.findAllByOrderByIdAsc()).thenReturn(mockOrganizations);

        ApiResponse response = organizationController.allOrganizations();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<Organization>");
        assertEquals(mockOrganizations.size(), list.size());
    }

    @Test
    public void testGetOrganization() {
        when(organizationRepo.read(any(Long.class))).thenReturn(mockOrganization1);

        ApiResponse response = organizationController.getOrganization(mockOrganization1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Organization organization = (Organization) response.getPayload().get("Organization");
        assertEquals(mockOrganization1.getId(), organization.getId());
    }

    @Test
    public void testCreateOrganization() {
        when(organizationRepo.read(any(Long.class))).thenReturn(mockOrganization1);
        when(organizationRepo.create(any(String.class), any(Organization.class), any(OrganizationCategory.class))).thenReturn(mockOrganization2);

        ApiResponse response = organizationController.createOrganization(0L, mockOrganization1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Organization organization = (Organization) response.getPayload().get("Organization");
        assertEquals(mockOrganization2.getId(), organization.getId());
    }

    @Test
    public void testUpdateOrganization() {
        when(organizationRepo.read(any(Long.class))).thenReturn(mockOrganization2);
        when(organizationRepo.update(any(Organization.class))).thenReturn(mockOrganization2);

        ApiResponse response = organizationController.updateOrganization(mockOrganization1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Organization organization = (Organization) response.getPayload().get("Organization");
        assertEquals(mockOrganization2.getId(), organization.getId());
    }

    @Test
    public void testRemoveOrganization() {
        when(organizationRepo.read(any(Long.class))).thenReturn(mockOrganization1);
        doNothing().when(organizationRepo).delete(any(Organization.class));

        ApiResponse response = organizationController.deleteOrganization(mockOrganization1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        verify(organizationRepo, times(1)).delete(any(Organization.class));
    }

}
