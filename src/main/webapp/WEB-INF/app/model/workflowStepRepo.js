vireo.service("WorkflowStepRepo", function($route, $q, WsApi, OrganizationRepo, AbstractModel) {

	var self;
	
	var WorkflowStepRepo = function(futureData) {
		self = this;
		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);		
		self.unwrap(self, futureData);		
	};
		
	WorkflowStepRepo.data = null;
	
	WorkflowStepRepo.listener = null;

	WorkflowStepRepo.promise = null;
	
	WorkflowStepRepo.set = function(data) {
		self.unwrap(self, data);
	};

	WorkflowStepRepo.get = function() {

		if(WorkflowStepRepo.promise) return WorkflowStepRepo.data;

		var newAllOrganizationsPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'workflow-step', 
				method: 'all',
		});

		WorkflowStepRepo.promise = newAllOrganizationsPromise;

		if(WorkflowStepRepo.data) {
			newAllOrganizationsPromise.then(function(data) {
				WorkflowStepRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		}
		else {
			WorkflowStepRepo.data = new WorkflowStepRepo(newAllOrganizationsPromise);	
		}
		
		WorkflowStepRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'workflow-step', 
			method: '',
		});
				
		WorkflowStepRepo.set(WorkflowStepRepo.listener);

		return WorkflowStepRepo.data;
	
	};

	WorkflowStepRepo.removeFieldProfile = function(workflowStepId, fieldProfileId) {

		var removePromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'workflow-step', 
				method: workflowStepId+'/remove-field-profile/' + fieldProfileId,
				data: {
					requestingOrgId: OrganizationRepo.getSelectedOrganization().id
				}
		});

		return removePromise;

	};

	WorkflowStepRepo.reorderFieldProfile = function(workflowStepId, src, dest) {

		var reorderPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'workflow-step', 
				method: workflowStepId+'/reorder-field-profiles/'+src+'/'+dest,
				data: {
					requestingOrgId: OrganizationRepo.getSelectedOrganization().id
				}
		});

		return reorderPromise;

	};


	WorkflowStepRepo.addNote = function(workflowStepId, note) {

		var removePromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'workflow-step', 
				method: workflowStepId+'/add-note',
				data: {
					requestingOrgId: OrganizationRepo.getSelectedOrganization().id,
					noteName: note.name,
					noteText: note.text
				}
		});

		return removePromise;

	};

	WorkflowStepRepo.updateNote = function(workflowStepId, note) {

		var removePromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'workflow-step', 
				method: workflowStepId+'/update-note',
				data: {
					requestingOrgId: OrganizationRepo.getSelectedOrganization().id,
					noteId: note.id,
					noteName: note.name,
					noteText: note.text
				}
		});

		return removePromise;

	};

	WorkflowStepRepo.removeNote = function(workflowStepId, noteId) {

		var removePromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'workflow-step', 
				method: workflowStepId+'/remove-note/' + noteId,
				data: {
					requestingOrgId: OrganizationRepo.getSelectedOrganization().id
				}
		});

		return removePromise;

	};

	WorkflowStepRepo.reorderNote = function(workflowStepId, src, dest) {

		var reorderPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'workflow-step', 
				method: workflowStepId+'/reorder-notes/'+src+'/'+dest,
				data: {
					requestingOrgId: OrganizationRepo.getSelectedOrganization().id
				}
		});

		return reorderPromise;

	};
	
	WorkflowStepRepo.ready = function() {
		return WorkflowStepRepo.promise;
	};

	WorkflowStepRepo.listen = function() {
		return WorkflowStepRepo.listener;
	};
	
	return WorkflowStepRepo;
	
});