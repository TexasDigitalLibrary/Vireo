vireo.service("OrganizationRepo", function($route, $q, WsApi, AbstractModel) {

	var self;
	
	var OrganizationRepo = function(futureData) {
		self = this;

		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData);
		
	};

	OrganizationRepo.data = null;
	OrganizationRepo.listener = null;
	OrganizationRepo.promise = null;

	OrganizationRepo.newOrganization = {};

	OrganizationRepo.resetNewOrganization = function() {
		for(var key in OrganizationRepo.newOrganization) {
			delete OrganizationRepo.newOrganization[key];
		}
	};

	OrganizationRepo.getNewOrganization = function() {
		return OrganizationRepo.newOrganization;
	}

	OrganizationRepo.set = function(data) {
		self.unwrap(self, data);
	};

	OrganizationRepo.get = function() {

		if(OrganizationRepo.promise) return OrganizationRepo.data;

		var newAllOrganizationsPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'organization', 
				method: 'all',
		});

		OrganizationRepo.promise = newAllOrganizationsPromise;

		if(OrganizationRepo.data) {
			newAllOrganizationsPromise.then(function(data) {
				OrganizationRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		}
		else {
			OrganizationRepo.data = new OrganizationRepo(newAllOrganizationsPromise);	
		}

		// OrganizationRepo.promise.then(function() {
		// 	angular.forEach(OrganizationRepo.data.list, function(org){
		// 		OrganizationRepo.getOrganizationsWorkflowStep(org);
		// 	});
		// });		
		
		OrganizationRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'organization', 
			method: '',
		});

		OrganizationRepo.set(OrganizationRepo.listener);

		// OrganizationRepo.listener.then(null,null,function() {
		// 	var newOrg = OrganizationRepo.data.list[OrganizationRepo.data.list.length-1];
		// 	OrganizationRepo.getOrganizationsWorkflowStep(newOrg);
		// });

		return OrganizationRepo.data;
	
	};

	OrganizationRepo.getOrganizationsWorkflowStep = function(org) {
		var workflowStepsPromise = WsApi.fetch({
						endpoint: '/private/queue', 
						controller: 'organization', 
						method: 'get/worflow-steps',
						data: org
		})

		workflowStepsPromise.then(function(data) {
			
			org.workflowSteps.length = 0;

			angular.forEach(JSON.parse(data.body).payload.PersistentBag, function(workflowStep) {
				org.workflowSteps.push(workflowStep);
			});
		
		});

		return workflowStepsPromise;
	}

	OrganizationRepo.getChildren = function(id) {

		var childOrganizationsPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'organization', 
				method: 'get-children/' + id,
		});

		return childOrganizationsPromise;
	
	};

	OrganizationRepo.add = function() {

		var addOrganizationPromise = WsApi.fetch({
				'endpoint': '/private/queue', 
				'controller': 'organization', 
				'method': 'create',
				'data': {
					"name": OrganizationRepo.newOrganization.name, 
					"category": OrganizationRepo.newOrganization.category,
					"parentOrganizationId": OrganizationRepo.newOrganization.parent.id,
				}
		});

		OrganizationRepo.resetNewOrganization();

		return addOrganizationPromise;

	};

	OrganizationRepo.update = function(organization) {

		var updateOrganizationPromise = WsApi.fetch({
				'endpoint': '/private/queue', 
				'controller': 'organization', 
				'method': 'update',
				'data': {
					"organization": organization
				}
		});

		return updateOrganizationPromise;

	};
        
    OrganizationRepo.ready = function() {
                return OrganizationRepo.promise;
	};

	OrganizationRepo.listen = function() {
		return OrganizationRepo.listener;
	};
	
	return OrganizationRepo;
	
});
