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

	OrganizationRepo.add = function(organization) {
		WsApi.fetch({
				'endpoint': '/private/queue', 
				'controller': 'organization', 
				'method': 'create',
				'data': {"name":organization.name}
		}).then(function(response) {
			OrganizationRepo.data.list.push(JSON.parse(response.body).payload.Organization);
		});
	};
	
	OrganizationRepo.ready = function() {
		return OrganizationRepo.promise;
	};

	OrganizationRepo.listen = function() {
		return OrganizationRepo.listener;
	};
	
	return OrganizationRepo;
	
});