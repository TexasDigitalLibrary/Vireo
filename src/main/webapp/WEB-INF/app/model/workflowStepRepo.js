vireo.service("WorkflowStepRepo", function($route, $q, WsApi, AbstractModel) {

	var self;
	
	var WorkflowStepRepo = function(futureData) {
		self = this;
		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);		
		self.unwrap(self, futureData);		
	};
	
	WorkflowStepRepo.currentWorkflowSteps = {};
	
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

	WorkflowStepRepo.getStepById = function(wsID) {

		console.log(wsID);

		var defer = $q.defer();

		if (WorkflowStepRepo.currentWorkflowSteps[wsID]) {
			defer.resolve(WorkflowStepRepo.currentWorkflowSteps[wsID]);
		} else {

			WorkflowStepRepo.currentWorkflowSteps[wsID] = "pending";

			var stepPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'workflow-step', 
				method: "get/"+wsID,
			});

			stepPromise.then(function(result){
				var workflowStep = JSON.parse(result.body).payload.WorkflowStep;
				WorkflowStepRepo.currentWorkflowSteps[wsID] = workflowStep;
				console.log(WorkflowStepRepo.currentWorkflowSteps);
				defer.resolve(workflowStep);
			});

      	}

      return defer.promise;	

	};

	WorkflowStepRepo.getCurrentWorkflowSteps = function() {
		return WorkflowStepRepo.currentWorkflowSteps;
	};
	
	WorkflowStepRepo.ready = function() {
		return WorkflowStepRepo.promise;
	};

	WorkflowStepRepo.listen = function() {
		return WorkflowStepRepo.listener;
	};
	
	return WorkflowStepRepo;
	
});