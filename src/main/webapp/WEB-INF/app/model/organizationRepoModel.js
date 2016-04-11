vireo.service("OrganizationRepo", function($route, WsApi, AbstractModel) {

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
		
		OrganizationRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'organization', 
			method: '',
		});
				
		OrganizationRepo.set(OrganizationRepo.listener);

		return OrganizationRepo.data;
	
	};

	OrganizationRepo.getChildren = function(id) {

		var childOrganizationsPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'organization', 
				method: 'get-children/' + id,
		});

		return childOrganizationsPromise;
	
	};

	OrganizationRepo.add = function(organization) {

		var addOrganizationPromise = WsApi.fetch({
				'endpoint': '/private/queue', 
				'controller': 'organization', 
				'method': 'create',
				'data': {
					"name": organization.name, 
					"categoryId": organization.categoryId,
					"parentOrganizationId": organization.parentId,
				}
		});

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