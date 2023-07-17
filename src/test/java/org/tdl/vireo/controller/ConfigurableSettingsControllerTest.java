package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.DefaultConfiguration;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.service.VireoThemeManagerService;

@ActiveProfiles("test")
public class ConfigurableSettingsControllerTest extends AbstractControllerTest {

    @Mock
    private ConfigurationRepo configurationRepo;

    @Mock
    private VireoThemeManagerService themeManagerService;

    @InjectMocks
    private ConfigurableSettingsController configurableSettingsController;

    private static Map<String, List<Configuration>> mockConfiguration;
    private static List<Configuration> defaultConfigurations;
    private static List<Configuration> managedConfigurations;
    private static List<Configuration> managedLookAndFeelConfigurations;
    private static DefaultConfiguration defaultConfiguration;
    private static ManagedConfiguration managedConfiguration;
    private static ManagedConfiguration managedLookAndFeelConfiguration;

    @BeforeEach
    public void setup() {
        defaultConfiguration = new DefaultConfiguration("default name", "value", "default");
        managedConfiguration = new ManagedConfiguration("managed name", "value", "managed");
        managedLookAndFeelConfiguration = new ManagedConfiguration("managed name", "value", "lookAndFeel");
        defaultConfigurations = new ArrayList<>();
        managedConfigurations = new ArrayList<>();
        managedLookAndFeelConfigurations = new ArrayList<>();
        mockConfiguration = new HashMap<>();

        defaultConfigurations.add(defaultConfiguration);
        managedConfigurations.add(managedConfiguration);
        managedLookAndFeelConfigurations.add(managedLookAndFeelConfiguration);

        mockConfiguration.put("default", defaultConfigurations);
        mockConfiguration.put("managed", managedConfigurations);
        mockConfiguration.put("lookAndFeel", managedLookAndFeelConfigurations);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetSettings() {
        when(configurationRepo.getCurrentConfigurations()).thenReturn(mockConfiguration);

        ApiResponse response = configurableSettingsController.getSettings();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        Map<String, List<Configuration>> map = (HashMap<String, List<Configuration>>) response.getPayload().get("HashMap");
        assertNotNull(mockConfiguration, "Payload response is not a HashMap.");
        assertEquals(mockConfiguration.size(), map.size(), "Payload response map is the wrong length.");
        assertEquals(mockConfiguration.get("default"), map.get("default"), "Map['default'] is not the correct configurations array.");
    }

    @Test
    public void testUpdateSettings() {
        when(configurationRepo.save(any(ManagedConfiguration.class))).thenReturn(managedConfiguration);
        doNothing().when(simpMessagingTemplate).convertAndSend(anyString(), any(ApiResponse.class));

        ApiResponse response = configurableSettingsController.updateSetting(managedConfiguration);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ManagedConfiguration managed = (ManagedConfiguration) response.getPayload().get("ManagedConfiguration");
        assertNotNull(managed, "Payload response is not a ManagedConfiguration.");
        assertEquals(managedConfiguration, managed, "Payload response is not the correct configuration.");

        Mockito.verify(themeManagerService, Mockito.never()).refreshCurrentTheme();
        Mockito.verify(simpMessagingTemplate, Mockito.only()).convertAndSend(anyString(), any(ApiResponse.class));
    }

    @Test
    public void testUpdateSettingsForLookAndFeelType() {
        when(configurationRepo.save(any(ManagedConfiguration.class))).thenReturn(managedLookAndFeelConfiguration);
        doNothing().when(simpMessagingTemplate).convertAndSend(anyString(), any(ApiResponse.class));

        ApiResponse response = configurableSettingsController.updateSetting(managedLookAndFeelConfiguration);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        ManagedConfiguration managed = (ManagedConfiguration) response.getPayload().get("ManagedConfiguration");
        assertNotNull(managed, "Payload response is not a ManagedConfiguration.");
        assertEquals(managedLookAndFeelConfiguration, managed, "Payload response is not the correct configuration.");

        Mockito.verify(themeManagerService, Mockito.only()).refreshCurrentTheme();
        Mockito.verify(simpMessagingTemplate, Mockito.only()).convertAndSend(anyString(), any(ApiResponse.class));
    }

    @Test
    public void testResetSettings() {
        when(configurationRepo.reset(any(ManagedConfiguration.class))).thenReturn(defaultConfiguration);
        doNothing().when(simpMessagingTemplate).convertAndSend(anyString(), any(ApiResponse.class));

        ApiResponse response = configurableSettingsController.resetSetting(managedConfiguration);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        DefaultConfiguration defaultConfig = (DefaultConfiguration) response.getPayload().get("DefaultConfiguration");
        assertNotNull(defaultConfig, "Payload response is not a DefaultConfiguration.");
        assertEquals(defaultConfiguration, defaultConfig, "Payload response is not the correct configuration.");

        Mockito.verify(themeManagerService, Mockito.never()).refreshCurrentTheme();
        Mockito.verify(simpMessagingTemplate, Mockito.only()).convertAndSend(anyString(), any(ApiResponse.class));
    }

    @Test
    public void testResetSettingsForLookAndFeelType() {
        when(configurationRepo.reset(any(ManagedConfiguration.class))).thenReturn(defaultConfiguration);
        doNothing().when(simpMessagingTemplate).convertAndSend(anyString(), any(ApiResponse.class));
        doNothing().when(themeManagerService).refreshCurrentTheme();

        ApiResponse response = configurableSettingsController.resetSetting(managedLookAndFeelConfiguration);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        DefaultConfiguration defaultConfig = (DefaultConfiguration) response.getPayload().get("DefaultConfiguration");
        assertNotNull(defaultConfig, "Payload response is not a DefaultConfiguration.");
        assertEquals(defaultConfiguration, defaultConfig, "Payload response is not the correct configuration.");

        Mockito.verify(themeManagerService, Mockito.only()).refreshCurrentTheme();
        Mockito.verify(simpMessagingTemplate, Mockito.only()).convertAndSend(anyString(), any(ApiResponse.class));
    }

}
