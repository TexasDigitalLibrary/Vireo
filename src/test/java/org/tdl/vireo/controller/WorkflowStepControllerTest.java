package org.tdl.vireo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.tdl.vireo.exception.ComponentNotPresentOnOrgException;
import org.tdl.vireo.exception.HeritableModelNonOverrideableException;
import org.tdl.vireo.exception.WorkflowStepNonOverrideableException;
import org.tdl.vireo.model.FieldProfile;
import org.tdl.vireo.model.ManagedConfiguration;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.FieldPredicateRepo;
import org.tdl.vireo.model.repo.FieldProfileRepo;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;

@ActiveProfiles("test")
public class WorkflowStepControllerTest extends AbstractControllerTest {

    @Mock
    private ConfigurationRepo configurationRepo;

    @Mock
    private FieldPredicateRepo fieldPredicateRepo;

    @Mock
    private FieldProfileRepo fieldProfileRepo;

    @Mock
    private NoteRepo noteRepo;

    @Mock
    private OrganizationRepo organizationRepo;

    @Mock
    private WorkflowStepRepo workflowStepRepo;

    @InjectMocks
    private WorkflowStepController fieldPredicateController;

    private FieldProfile fieldProfile1;
    private FieldProfile fieldProfile2;

    private ManagedConfiguration managedConfiguration1;
    private ManagedConfiguration managedConfiguration2;

    private Note note1;
    private Note note2;

    private Organization organization1;
    private Organization organization2;

    private WorkflowStep workflowStep1;
    private WorkflowStep workflowStep2;

    private List<FieldProfile> fieldProfiles;
    private List<Organization> organizations;
    private List<WorkflowStep> workflowSteps;

    @BeforeEach
    public void setup() {
        fieldProfile1 = new FieldProfile();
        fieldProfile2 = new FieldProfile();
        managedConfiguration1 = new ManagedConfiguration("name1", "value1", "type1");
        managedConfiguration2 = new ManagedConfiguration("name2", "value2", "type2");
        note1 = new Note("name1", "text1");
        note2 = new Note("name2", "text2");
        organization1 = new Organization("Organization 1");
        organization2 = new Organization("Organization 2");
        workflowStep1 = new WorkflowStep("WorkflowStep 1");
        workflowStep2 = new WorkflowStep("WorkflowStep 2");

        fieldProfiles = new ArrayList<>();
        organizations = new ArrayList<>();
        workflowSteps = new ArrayList<>();

        fieldProfile1.setId(1L);
        fieldProfile2.setId(2L);
        managedConfiguration1.setId(1L);
        managedConfiguration1.setId(2L);
        note1.setId(1L);
        note2.setId(2L);
        organization1.setId(1L);
        organization2.setId(2L);
        workflowStep1.setId(1L);
        workflowStep2.setId(2L);

        fieldProfile1.setMappedShibAttribute(managedConfiguration1);
        fieldProfile1.setOriginating(fieldProfile1);
        fieldProfile2.setOriginating(fieldProfile2);
        fieldProfile1.setOriginatingWorkflowStep(workflowStep1);
        fieldProfile2.setOriginatingWorkflowStep(workflowStep2);

        workflowStep1.setOriginalFieldProfiles(fieldProfiles);
        workflowStep2.setOriginalFieldProfiles(fieldProfiles);
        workflowStep1.setOriginatingOrganization(organization1);
        workflowStep2.setOriginatingOrganization(organization2);

        fieldProfiles.add(fieldProfile1);
        organizations.add(organization1);
        workflowSteps.add(workflowStep1);
    }

    @Test
    public void testGetAll() {
        when(workflowStepRepo.findAll()).thenReturn(workflowSteps);

        ApiResponse response = fieldPredicateController.getAll();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        List<?> list = (ArrayList<?>) response.getPayload().get("ArrayList<WorkflowStep>");
        assertEquals(workflowSteps.size(), list.size());
    }

    @Test
    public void testGetStepById() {
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep1));

        ApiResponse response = fieldPredicateController.getStepById(1L);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());

        WorkflowStep fieldPredicate = (WorkflowStep) response.getPayload().get("WorkflowStep");
        assertEquals(workflowStep1.getId(), fieldPredicate.getId());
    }

    @Test
    public void testCreateFieldProfile() throws JsonProcessingException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep1));
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(fieldProfileRepo.create(any(WorkflowStep.class), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(fieldProfile1);
        when(organizationRepo.findAllByOrderByIdAsc()).thenReturn(organizations);
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.createFieldProfile(organization1.getId(), workflowStep1.getId(), fieldProfile1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testCreateFieldProfileUpdatingOrganization() throws JsonProcessingException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep2));
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(workflowStepRepo.update(any(WorkflowStep.class), any(Organization.class))).thenReturn(workflowStep2);
        when(fieldProfileRepo.create(any(WorkflowStep.class), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(fieldProfile1);
        when(organizationRepo.findAllByOrderByIdAsc()).thenReturn(organizations);
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.createFieldProfile(organization1.getId(), workflowStep1.getId(), fieldProfile1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdateFieldProfile() throws JsonProcessingException, WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(organizationRepo.findAllByOrderByIdAsc()).thenReturn(organizations);
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.updateFieldProfile(organization1.getId(), workflowStep1.getId(), fieldProfile1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdateFieldProfileWithNullShibAttribute() throws JsonProcessingException, WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        managedConfiguration1 = null;

        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(organizationRepo.findAllByOrderByIdAsc()).thenReturn(organizations);
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.updateFieldProfile(organization1.getId(), workflowStep1.getId(), fieldProfile1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdateFieldProfileWithNullShibAttributeId() throws JsonProcessingException, WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        managedConfiguration1.setId(null);

        when(configurationRepo.findByNameAndType(anyString(), anyString())).thenReturn(managedConfiguration1);
        when(fieldProfileRepo.update(any(FieldProfile.class), any(Organization.class))).thenReturn(fieldProfile1);
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(organizationRepo.findAllByOrderByIdAsc()).thenReturn(organizations);
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.updateFieldProfile(organization1.getId(), workflowStep1.getId(), fieldProfile1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdateFieldProfileWithNullShibAttributeIDAndNullPersistedAttribute() throws JsonProcessingException, WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        managedConfiguration1.setId(null);
        managedConfiguration2 = null;

        when(configurationRepo.findByNameAndType(anyString(), anyString())).thenReturn(managedConfiguration2);
        when(fieldProfileRepo.update(any(FieldProfile.class), any(Organization.class))).thenReturn(fieldProfile1);
        when(configurationRepo.create(any(ManagedConfiguration.class))).thenReturn(managedConfiguration1);
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(organizationRepo.findAllByOrderByIdAsc()).thenReturn(organizations);
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.updateFieldProfile(organization1.getId(), workflowStep1.getId(), fieldProfile1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testRemoveFieldProfile() throws JsonProcessingException, WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep1));
        when(fieldProfileRepo.findById(any(Long.class))).thenReturn(Optional.of(fieldProfile1));
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        doNothing().when(fieldProfileRepo).removeFromWorkflowStep(any(Organization.class), any(WorkflowStep.class), any(FieldProfile.class));
        when(organizationRepo.findAllByOrderByIdAsc()).thenReturn(organizations);
        doNothing().when(fieldProfileRepo).delete(any(FieldProfile.class));
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.removeFieldProfile(organization1.getId(), workflowStep1.getId(), fieldProfile1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testRemoveFieldProfileWithDifferentProfile() throws JsonProcessingException, WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep2));
        when(fieldProfileRepo.findById(any(Long.class))).thenReturn(Optional.of(fieldProfile1));
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        doNothing().when(fieldProfileRepo).removeFromWorkflowStep(any(Organization.class), any(WorkflowStep.class), any(FieldProfile.class));
        when(organizationRepo.findAllByOrderByIdAsc()).thenReturn(organizations);
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.removeFieldProfile(organization1.getId(), workflowStep1.getId(), fieldProfile1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testReorderFieldProfile() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep1));
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(workflowStepRepo.reorderFieldProfiles(any(Organization.class), any(WorkflowStep.class), anyInt(), anyInt())).thenReturn(workflowStep1);

        // Warning: Unlike other broadcasts for reorder functions, this one broadcasts using the ID rather than a list.
        doNothing().when(organizationRepo).broadcast(anyLong());

        ApiResponse response = fieldPredicateController.reorderFieldProfiles(organization1.getId(), workflowStep1.getId(), 1, 2);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testAddNote() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep1));
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(noteRepo.create(any(WorkflowStep.class), anyString(), anyString(), anyBoolean())).thenReturn(note1); 
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.addNote(organization1.getId(), workflowStep1.getId(), note1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testAddNoteUpdatingWorkflowStep() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        workflowStep1.setOriginatingOrganization(organization2);

        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep1));
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(workflowStepRepo.update(any(WorkflowStep.class), any(Organization.class))).thenReturn(workflowStep1);
        when(noteRepo.create(any(WorkflowStep.class), anyString(), anyString(), anyBoolean())).thenReturn(note1);
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.addNote(organization1.getId(), workflowStep1.getId(), note1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdateNoteUpdatingWorkflowStep() throws HeritableModelNonOverrideableException, WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(noteRepo.update(any(Note.class), any(Organization.class))).thenReturn(note1);
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.updateNote(organization1.getId(), workflowStep1.getId(), note1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testRemoveNote() throws NumberFormatException, WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        workflowStep1.setOriginatingOrganization(organization1);
        note1.setOriginatingWorkflowStep(workflowStep1);

        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep1));
        when(noteRepo.findById(any(Long.class))).thenReturn(Optional.of(note1));
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        doNothing().when(noteRepo).removeFromWorkflowStep(any(Organization.class), any(WorkflowStep.class), any(Note.class));
        doNothing().when(noteRepo).delete(any(Note.class));
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.removeNote(organization1.getId(), workflowStep1.getId(), note1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testRemoveNoteWithDifferentOriginatingWorkflowStep() throws NumberFormatException, WorkflowStepNonOverrideableException, HeritableModelNonOverrideableException, ComponentNotPresentOnOrgException {
        workflowStep1.setOriginatingOrganization(organization1);
        note1.setOriginatingWorkflowStep(workflowStep2);

        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep1));
        when(noteRepo.findById(any(Long.class))).thenReturn(Optional.of(note1));
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        doNothing().when(noteRepo).removeFromWorkflowStep(any(Organization.class), any(WorkflowStep.class), any(Note.class));
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.removeNote(organization1.getId(), workflowStep1.getId(), note1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testReorderNote() throws WorkflowStepNonOverrideableException, ComponentNotPresentOnOrgException {
        when(workflowStepRepo.findById(any(Long.class))).thenReturn(Optional.of(workflowStep1));
        when(organizationRepo.findById(any(Long.class))).thenReturn(Optional.of(organization1));
        when(workflowStepRepo.reorderNotes(any(Organization.class), any(WorkflowStep.class), anyInt(), anyInt())).thenReturn(workflowStep1); 
        doNothing().when(organizationRepo).broadcast(anyList());

        ApiResponse response = fieldPredicateController.reorderNotes(organization1.getId(), workflowStep1.getId(), 1, 2);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

}
