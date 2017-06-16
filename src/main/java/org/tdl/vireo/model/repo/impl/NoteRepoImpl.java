package org.tdl.vireo.model.repo.impl;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.tdl.vireo.model.Note;
import org.tdl.vireo.model.WorkflowStep;
import org.tdl.vireo.model.inheritence.HeritableRepo;
import org.tdl.vireo.model.repo.NoteRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.WorkflowStepRepo;
import org.tdl.vireo.model.repo.custom.NoteRepoCustom;

import edu.tamu.framework.model.ApiResponse;

public class NoteRepoImpl extends HeritableRepo<Note, NoteRepo> implements NoteRepoCustom {

	@Autowired
	private NoteRepo noteRepo;

	@Autowired
	private WorkflowStepRepo workflowStepRepo;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	private OrganizationRepo organizationRepo;

	@Override
	public Note create(WorkflowStep originatingWorkflowStep, String name, String text) {
		Note note = noteRepo.save(new Note(originatingWorkflowStep, name, text));
		originatingWorkflowStep.addOriginalNote(note);
		workflowStepRepo.save(originatingWorkflowStep);
		simpMessagingTemplate.convertAndSend("/channel/organizations", new ApiResponse(SUCCESS, organizationRepo.findAll()));
		return noteRepo.findOne(note.getId());
	}

	@Override
	public Note create(WorkflowStep originatingWorkflowStep, String name, String text, Boolean overrideable) {
		Note note = noteRepo.save(new Note(originatingWorkflowStep, name, text, overrideable));
		originatingWorkflowStep.addOriginalNote(note);
		workflowStepRepo.save(originatingWorkflowStep);
		simpMessagingTemplate.convertAndSend("/channel/organizations", new ApiResponse(SUCCESS, organizationRepo.findAll()));
		return noteRepo.findOne(note.getId());
	}

}
