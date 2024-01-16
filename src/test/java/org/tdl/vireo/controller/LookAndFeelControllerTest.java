package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.io.IOException;
import javax.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.service.AssetService;

@ActiveProfiles(value = { "test", "isolated-test" })
public class LookAndFeelControllerTest extends AbstractControllerTest {

    @Mock
    private ConfigurationRepo configurationRepo;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private LookAndFeelController lookAndFeelController;

    private ManagedConfiguration managedConfiguration;

    @BeforeEach
    public void setup() throws MessagingException {
        managedConfiguration = new ManagedConfiguration("name", "value", "type");
    }

    @Test
    public void testUploadLogo() throws IOException {
        MultipartFile file = new MockMultipartFile("name", "originalName", "text/plain", "".getBytes());

        when(configurationRepo.getByNameAndType(anyString(), anyString())).thenReturn(managedConfiguration);
        doNothing().when(assetService).write(any(byte[].class), anyString());
        when(configurationRepo.findByName(anyString())).thenReturn(managedConfiguration);
        when(configurationRepo.reset(any(ManagedConfiguration.class))).thenReturn(managedConfiguration);
        when(configurationRepo.create(anyString(), anyString(), anyString())).thenReturn(managedConfiguration);
        doNothing().when(simpMessagingTemplate).convertAndSend(anyString(), any(ApiResponse.class));

        ApiResponse response = lookAndFeelController.uploadLogo("setting", "text/plain", file);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUploadLogoWithoutConfiguration() throws IOException {
        MultipartFile file = new MockMultipartFile("name", "originalName", "text/plain", "".getBytes());

        when(configurationRepo.getByNameAndType(anyString(), anyString())).thenReturn(managedConfiguration);
        doNothing().when(assetService).write(any(byte[].class), anyString());
        when(configurationRepo.findByName(anyString())).thenReturn(null);
        when(configurationRepo.create(anyString(), anyString(), anyString())).thenReturn(managedConfiguration);
        doNothing().when(simpMessagingTemplate).convertAndSend(anyString(), any(ApiResponse.class));

        ApiResponse response = lookAndFeelController.uploadLogo("setting", "text/plain", file);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testResetLogo() {
        when(configurationRepo.getByNameAndType(anyString(), anyString())).thenReturn(managedConfiguration);
        when(configurationRepo.reset(any(ManagedConfiguration.class))).thenReturn(managedConfiguration);
        doNothing().when(simpMessagingTemplate).convertAndSend(anyString(), any(ApiResponse.class));

        ApiResponse response = lookAndFeelController.resetLogo("setting");

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ManagedConfiguration got = (ManagedConfiguration) response.getPayload().get("ManagedConfiguration");
        assertEquals(managedConfiguration, got, "Did not get expected Managed Configuration in the response.");
    }
}
