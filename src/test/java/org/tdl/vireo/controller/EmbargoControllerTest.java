package org.tdl.vireo.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.model.Embargo;
import org.tdl.vireo.model.EmbargoGuarantor;
import org.tdl.vireo.model.repo.EmbargoRepo;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@ActiveProfiles("test")
public class EmbargoControllerTest extends AbstractControllerTest {

    protected static final String EMBARGO_NAME = "Embargo Name";
    protected static final String EMBARGO_DESCRIPTION = "Embargo Description";
    protected static final Integer EMBARGO_DURATION = 12;
    protected static final EmbargoGuarantor EMBARGO_GUARANTOR = EmbargoGuarantor.DEFAULT;
    protected static final Boolean EMBARGO_IS_ACTIVE = true;

    @Mock
    private EmbargoRepo embargoRepo;

    @InjectMocks
    private EmbargoController embargoController;

    private Embargo mockEmbargo;

    private static List<Embargo> mockEmbargoes;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockEmbargo = new Embargo(EMBARGO_NAME, EMBARGO_DESCRIPTION, EMBARGO_DURATION, EMBARGO_GUARANTOR, EMBARGO_IS_ACTIVE);
        mockEmbargoes = new ArrayList<Embargo>(Arrays.asList(new Embargo[] { mockEmbargo }));

        ReflectionTestUtils.setField(httpUtility, HTTP_DEFAULT_TIMEOUT_NAME, HTTP_DEFAULT_TIMEOUT_VALUE);
        ReflectionTestUtils.setField(cryptoService, SECRET_PROPERTY_NAME, SECRET_VALUE);
        ReflectionTestUtils.setField(tokenService, AUTH_SECRET_KEY_PROPERTY_NAME, AUTH_SECRET_KEY_VALUE);
        ReflectionTestUtils.setField(tokenService, AUTH_ISSUER_KEY_PROPERTY_NAME, AUTH_ISSUER_KEY_VALUE);
        ReflectionTestUtils.setField(tokenService, AUTH_DURATION_PROPERTY_NAME, AUTH_DURATION_VALUE);
        ReflectionTestUtils.setField(tokenService, AUTH_KEY_PROPERTY_NAME, AUTH_KEY_VALUE);

        TEST_CREDENTIALS.setFirstName(TEST_USER_FIRST_NAME);
        TEST_CREDENTIALS.setLastName(TEST_USER_LAST_NAME);
        TEST_CREDENTIALS.setEmail(TEST_USER_EMAIL);
        TEST_CREDENTIALS.setRole(TEST_USER_ROLE.toString());

        when(embargoRepo.findAll()).thenReturn(mockEmbargoes);
        when(embargoRepo.findAllByOrderByPositionAsc()).thenReturn(mockEmbargoes);
        when(embargoRepo.findAllByOrderByGuarantorAscPositionAsc()).thenReturn(mockEmbargoes);
        when(embargoRepo.findOne(any(Long.class))).thenReturn(mockEmbargo);
        when(embargoRepo.getOne(any(Long.class))).thenReturn(mockEmbargo);
        when(embargoRepo.create(any(String.class), any(String.class), any(Integer.class), any(EmbargoGuarantor.class), any(Boolean.class))).thenReturn(mockEmbargo);
        when(embargoRepo.update(any(Embargo.class))).thenReturn(mockEmbargo);

        doNothing().when(embargoRepo).remove(any(Embargo.class));
        doNothing().when(embargoRepo).reorder(any(Long.class), any(Long.class));
        doNothing().when(embargoRepo).reorder(any(Long.class), any(Long.class), any(EmbargoGuarantor.class));
        doNothing().when(embargoRepo).sort(any(String.class));
        doNothing().when(embargoRepo).sort(any(String.class), any(EmbargoGuarantor.class));
    }

    @Test
    public void testgetEmbargo() {
        ApiResponse response = embargoController.getEmbargoes();

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(embargoRepo, times(1)).findAllByOrderByGuarantorAscPositionAsc();
    }

    @Test
    public void testCreateEmbargo() {
        ApiResponse response = embargoController.createEmbargo(mockEmbargo);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(embargoRepo, times(1)).create(any(String.class), any(String.class), any(Integer.class), any(EmbargoGuarantor.class), any(Boolean.class));
    }

    @Test
    public void testUpdateEmbargo() {
        ApiResponse response = embargoController.updateEmbargo(mockEmbargo);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(embargoRepo, times(1)).update(any(Embargo.class));
    }

    @Test
    public void testRemoveEmbargo() {
        ApiResponse response = embargoController.removeEmbargo(mockEmbargo);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(embargoRepo, times(1)).remove(any(Embargo.class));
    }

    @Test
    public void testActivateEmbargo() {
        ApiResponse response = embargoController.activateEmbargo(mockEmbargo.getId());

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(embargoRepo, times(1)).update(any(Embargo.class));
    }

    @Test
    public void testDeactivateEmbargo() {
        ApiResponse response = embargoController.deactivateEmbargo(mockEmbargo.getId());

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(embargoRepo, times(1)).update(any(Embargo.class));
    }

    @Test
    public void testReorderEmbargos() {
        ApiResponse response = embargoController.reorderEmbargoes(EmbargoGuarantor.DEFAULT.toString(), 1L, 2L);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(embargoRepo, times(1)).reorder(any(Long.class), any(Long.class), any(EmbargoGuarantor.class));
    }

    @Test
    public void testSortEmbargos() {
        ApiResponse response = embargoController.sortEmbargoes("test", EmbargoGuarantor.DEFAULT.toString());

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(embargoRepo, times(1)).sort(any(String.class), any(EmbargoGuarantor.class));
    }

}
