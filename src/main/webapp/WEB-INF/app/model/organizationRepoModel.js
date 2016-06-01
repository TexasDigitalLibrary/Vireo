vireo.service("OrganizationRepo", function($route, $q, WsApi, AbstractModel) {

	var self;
	
	var OrganizationRepo = function(futureData) {
		self = this;
		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		self.unwrap(self, futureData);
	};

	var selectedOrganization = {};

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
	};

	OrganizationRepo.set = function(data) {
		self.unwrap(self, data);
	};

	OrganizationRepo.updateListener = WsApi.listen({
		endpoint: '/channel', 
		controller: 'organization', 
		method: '',
	}).then(null,null,function(rawApiResponse){
		var broadcastedOrg = JSON.parse(rawApiResponse.body).payload.Organization;
		if (broadcastedOrg.id == selectedOrganization.id) {
			OrganizationRepo.setSelectedOrganization(broadcastedOrg);
		}
	});

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
		
		OrganizationRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'organizations', 
			method: '',
		});
		
		OrganizationRepo.set(OrganizationRepo.listener);
		
		return OrganizationRepo.data;
		
	};

	OrganizationRepo.getSelectedOrganization = function() {
		return selectedOrganization;
	}

	OrganizationRepo.setSelectedOrganization = function(organization){
		OrganizationRepo.lazyFetch(organization.id).then(function(fetchedOrg) {
			extendWithOverwrite(selectedOrganization, fetchedOrg);
		});
		return selectedOrganization;
	}

	OrganizationRepo.findOrganizationById = function(id) {

		var matchedOrganization = null;

		angular.forEach(OrganizationRepo.data.list, function(orgToCompare) {
			if(orgToCompare.id === id) {
				matchedOrganization = orgToCompare;
			}
		});

		return matchedOrganization;
	};

	OrganizationRepo.lazyFetch = function(orgId) {

		var fetchedOrgDefer = new $q.defer();

		var getOrgPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'organization', 
			method: 'get/' + orgId
		});

		getOrgPromise.then(function(rawApiResponse) {
			
			var fetchedOrg = JSON.parse(rawApiResponse.body).payload.Organization;

			var workflowStepsPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'organization', 
				method: fetchedOrg.id + '/worflow'
			});

			workflowStepsPromise.then(function(data) {
				var aggregateWorkflowSteps = JSON.parse(data.body).payload.PersistentList;
				if(aggregateWorkflowSteps !== undefined) {
					fetchedOrg.aggregateWorkflowSteps = aggregateWorkflowSteps;
				}
				fetchedOrgDefer.resolve(fetchedOrg);
			});

		});

		return fetchedOrgDefer.promise;
	};

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

	OrganizationRepo.addWorkflowStep = function(org, workflowStepName) {

		var addWorkflowStepDefer = $q.defer();
		var addWorkflowStepPromise = WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'organization', 
			'method': org.id+'/create-workflow-step',
			'data': {
				'name': workflowStepName,
				'originating_organization_id': org.id,
				'overrideable': true
			}
		});

		addWorkflowStepPromise.then(function(rawRes) {
			addWorkflowStepDefer.resolve();
		});

		return addWorkflowStepDefer.promise;

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
	
	OrganizationRepo.updateWorkflowStep = function(requestingOrganization, workflowStepToUpdate) {
		var updateWorkflowStepDefer = $q.defer();
		var updateWorkflowStepPromise = WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'organization', 
			'method': requestingOrganization.id+'/update-workflow-step',
			'data': workflowStepToUpdate
		});

		updateWorkflowStepPromise.then(function(rawRes) {
			var updatedWorkflowStep = JSON.parse(rawRes.body).payload.WorkflowStep;
			updateWorkflowStepDefer.resolve(updatedWorkflowStep);
			angular.forEach(OrganizationRepo.data.list, function(org) {
				OrganizationRepo.getOrganizationsWorkflow(org);
			});			
		});

		return updateWorkflowStepDefer.promise;
	}

	OrganizationRepo.ready = function() {
		return OrganizationRepo.promise;
	};

	OrganizationRepo.listen = function() {
		return OrganizationRepo.listener;
	};

	var extendWithOverwrite = function(targetObj, srcObj) {
		var keys = Object.keys(srcObj);
		angular.forEach(keys, function(key){
			targetObj[key] = srcObj[key];
		});
	}
	
	return OrganizationRepo;
	
});
