vireo.repo("OrganizationRepo", function OrganizationRepo($q, WsApi) {

	var selectedOrganization = {};

	// additional repo methods and variables

	this.newOrganization = {};

	var extendWithOverwrite = function(targetObj, srcObj) {
		var srcKeys = Object.keys(srcObj);
		angular.forEach(srcKeys, function(key){
			targetObj[key] = srcObj[key];
		});
		
		var targetKeys = Object.keys(targetObj);
		angular.forEach(targetKeys, function(key){
			if(typeof srcObj[key] === undefined) {
				delete targetObj[key];
			}
		});
	};

	this.resetNewOrganization = function() {
		for(var key in this.newOrganization) {
			delete this.newOrganization[key];
		}
	};

	this.getNewOrganization = function() {
		return this.newOrganization;
	};

	this.getSelectedOrganization = function() {
		return selectedOrganization;
	}

	this.setSelectedOrganization = function(organization){
		this.lazyFetch(organization.id).then(function(fetchedOrg) {
			extendWithOverwrite(selectedOrganization, fetchedOrg);
		});
		return selectedOrganization;
	}

	// TODO: replace with abstract findById
	this.findOrganizationById = function(id) {

		var matchedOrganization = null;

		angular.forEach(this.data.list, function(orgToCompare) {
			if(orgToCompare.id === id) {
				matchedOrganization = orgToCompare;
			}
		});

		return matchedOrganization;
	};

	this.lazyFetch = function(orgId) {

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
				method: fetchedOrg.id + '/workflow'
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

	this.getChildren = function(id) {
		return WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'organization', 
			method: 'get-children/' + id,
		});		
	};

	this.addWorkflowStep = function(newWorkflowStepName) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'organization', 
			'method': this.getSelectedOrganization().id + '/create-workflow-step/' + newWorkflowStepName
		});
	};

	this.updateWorkflowStep = function(workflowStepToUpdate) {		
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'organization', 
			'method': this.getSelectedOrganization().id+'/update-workflow-step',
			'data': workflowStepToUpdate
		});
	};

	this.reorderWorkflowStep = function(upOrDown, workflowStepID) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'organization', 
			'method': this.getSelectedOrganization().id + '/' + 'shift-workflow-step-'+upOrDown+'/' + workflowStepID,
		});
	};

	this.deleteWorkflowStep = function(workflowStepID) {
		return WsApi.fetch({
			'endpoint': '/private/queue', 
			'controller': 'organization', 
			'method': this.getSelectedOrganization().id + '/' + 'delete-workflow-step/' + workflowStepID,
		});
	};

	return this;

});