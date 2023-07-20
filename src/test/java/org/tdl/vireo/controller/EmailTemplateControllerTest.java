package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.tdl.vireo.model.EmailTemplate;
import org.tdl.vireo.model.repo.EmailTemplateRepo;

@ActiveProfiles("test")
public class EmailTemplateControllerTest extends AbstractControllerTest {

    @Mock
    private EmailTemplateRepo emailTemplateRepo;

    @InjectMocks
    private EmailTemplateController emailTemplateController;

    private EmailTemplate mockEmailTemplate;

    private static List<EmailTemplate> mockEmailTemplates;

    @BeforeEach
    public void setup() {
        mockEmailTemplate = new EmailTemplate(TEST_EMAIL_TEMPLATE_NAME, TEST_EMAIL_TEMPLATE_SUBJECT, TEST_EMAIL_TEMPLATE_MESSAGE);
        mockEmailTemplates = new ArrayList<EmailTemplate>(Arrays.asList(new EmailTemplate[] { mockEmailTemplate }));

        ReflectionTestUtils.setField(httpUtility, HTTP_DEFAULT_TIMEOUT_NAME, HTTP_DEFAULT_TIMEOUT_VALUE);
        ReflectionTestUtils.setField(cryptoService, SECRET_PROPERTY_NAME, SECRET_VALUE);

        TEST_CREDENTIALS.setFirstName(TEST_USER_FIRST_NAME);
        TEST_CREDENTIALS.setLastName(TEST_USER_LAST_NAME);
        TEST_CREDENTIALS.setEmail(TEST_USER_EMAIL);
        TEST_CREDENTIALS.setRole(TEST_USER_ROLE.toString());

        lenient().when(emailTemplateRepo.findAll()).thenReturn(mockEmailTemplates);
        lenient().when(emailTemplateRepo.findAllByOrderByPositionAsc()).thenReturn(mockEmailTemplates);
        lenient().when(emailTemplateRepo.getById(any(Long.class))).thenReturn(mockEmailTemplate);
        lenient().when(emailTemplateRepo.create(any(String.class), any(String.class), any(String.class))).thenReturn(mockEmailTemplate);
        lenient().when(emailTemplateRepo.update(any(EmailTemplate.class))).thenReturn(mockEmailTemplate);

        lenient().doNothing().when(emailTemplateRepo).remove(any(EmailTemplate.class));
        lenient().doNothing().when(emailTemplateRepo).reorder(any(Long.class), any(Long.class));
        lenient().doNothing().when(emailTemplateRepo).sort(any(String.class));
    }

    @Test
    public void testAllEmailTemplates() {
        ApiResponse response = emailTemplateController.allEmailTemplates();

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(emailTemplateRepo, times(1)).findAllByOrderByPositionAsc();
    }

    @Test
    public void testCreateEmailTemplate() {
        ApiResponse response = emailTemplateController.createEmailTemplate(mockEmailTemplate);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(emailTemplateRepo, times(1)).create(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void testUpdateEmailTemplate() {
        ApiResponse response = emailTemplateController.updateEmailTemplate(mockEmailTemplate);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(emailTemplateRepo, times(1)).update(any(EmailTemplate.class));
    }

    @Test
    public void testRemoveEmailTemplate() {
        ApiResponse response = emailTemplateController.removeEmailTemplate(mockEmailTemplate);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(emailTemplateRepo, times(1)).remove(any(EmailTemplate.class));
    }

    @Test
    public void testReorderEmailTemplates() {
        ApiResponse response = emailTemplateController.reorderEmailTemplates(1L, 2L);

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(emailTemplateRepo, times(1)).reorder(any(Long.class), any(Long.class));
    }

    @Test
    public void testSortEmailTemplates() {
        ApiResponse response = emailTemplateController.sortEmailTemplates("test");

        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
        verify(emailTemplateRepo, times(1)).sort(any(String.class));
    }

}
