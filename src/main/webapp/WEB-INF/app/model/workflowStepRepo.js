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

	WorkflowStepRepo.remove = function(workflowStepId, fieldProfileId) {

		var reorderPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'workflow-step', 
				method: workflowStepId+'/remove-field-profile/'+src+'/'+dest,
				data: {
					requestingOrgId: OrganizationRepo.getSelectedOrganization().id
				}
		});

		return reorderPromise;

	};

	WorkflowStepRepo.reorder = function(workflowStepId, src, dest) {

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
	
	WorkflowStepRepo.ready = function() {
		return WorkflowStepRepo.promise;
	};

	WorkflowStepRepo.listen = function() {
		return WorkflowStepRepo.listener;
	};
	
	return WorkflowStepRepo;
	
});