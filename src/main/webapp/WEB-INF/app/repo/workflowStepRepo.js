vireo.repo("WorkflowStepRepo", function WorkfloStepRepo(OrganizationRepo, WsApi) {

	// additional repo methods and variables

	this.addFieldProfile = function(workflowStepId, fieldProfile) {
		angular.extend(fieldProfile, {
			requestingOrgId: OrganizationRepo.getSelectedOrganization().id
		});
		angular.extend(this.mapping.addFieldProfile, {
			'method': workflowStepId + '/add-field-profile',
			'data': fieldProfile
		});
		return WsApi.fetch(this.mapping.addFieldProfile);
	};

	this.updateFieldProfile = function(workflowStepId, fieldProfile) {
		angular.extend(fieldProfile, {
			requestingOrgId: OrganizationRepo.getSelectedOrganization().id
		});
		angular.extend(this.mapping.updateFieldProfile, {
			'method': workflowStepId + '/update-field-profile',
			'data': fieldProfile
		});
		return WsApi.fetch(this.mapping.updateFieldProfile);
	};

	this.removeFieldProfile = function(workflowStepId, fieldProfileId) {
		angular.extend(this.mapping.removeFieldProfile, {
			'method': workflowStepId+'/remove-field-profile/' + fieldProfileId,
			'data': {
				requestingOrgId: OrganizationRepo.getSelectedOrganization().id
			}
		});
		return WsApi.fetch(this.mapping.removeFieldProfile);
	};

	this.reorderFieldProfile = function(workflowStepId, src, dest) {
		angular.extend(this.mapping.reorderFieldProfile, {
			'method': workflowStepId+'/reorder-field-profiles/'+src+'/'+dest,
			'data': {
				requestingOrgId: OrganizationRepo.getSelectedOrganization().id
			}
		});
		return WsApi.fetch(this.mapping.reorderFieldProfile);
	};


	this.addNote = function(workflowStepId, note) {
		angular.extend(note, {
			requestingOrgId: OrganizationRepo.getSelectedOrganization().id
		});
		angular.extend(this.mapping.addNote, {
			'method': workflowStepId+'/add-note',
			'data': note
		});
		return WsApi.fetch(this.mapping.addNote);
	};

	this.updateNote = function(workflowStepId, note) {
		angular.extend(note, {
			requestingOrgId: OrganizationRepo.getSelectedOrganization().id
		});
		angular.extend(this.mapping.updateNote, {
			'method': workflowStepId+'/update-note',
			'data': note
		});
		return WsApi.fetch(this.mapping.updateNote);
	};

	this.removeNote = function(workflowStepId, noteId) {
		angular.extend(this.mapping.removeNote, {
			'method': workflowStepId+'/remove-note/' + noteId,
			'data': {
				requestingOrgId: OrganizationRepo.getSelectedOrganization().id
			}
		});
		return WsApi.fetch(this.mapping.removeNote);
	};

	this.reorderNote = function(workflowStepId, src, dest) {
		angular.extend(this.mapping.reorderNote, {
			'method': workflowStepId+'/reorder-notes/'+src+'/'+dest,
			'data': {
				requestingOrgId: OrganizationRepo.getSelectedOrganization().id
			}
		});
		return WsApi.fetch(this.mapping.reorderNote);
	};

	return this;

});