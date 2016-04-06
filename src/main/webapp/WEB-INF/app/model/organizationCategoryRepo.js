vireo.service("OrganizationCategoryRepo", function($route, WsApi, AbstractModel) {

	var self;
	
	var OrganizationCategoryRepo = function(futureData) {
		self = this;

		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData);
		
	};
	
	OrganizationCategoryRepo.data = null;
	
	OrganizationCategoryRepo.listener = null;

	OrganizationCategoryRepo.promise = null;
	
	OrganizationCategoryRepo.set = function(data) {
		self.unwrap(self, data);
	};

	OrganizationCategoryRepo.get = function() {

		if(OrganizationCategoryRepo.promise) return OrganizationCategoryRepo.data;

		var newAllOrganizationsPromise = WsApi.fetch({
				endpoint: '/private/queue', 
				controller: 'organization-category', 
				method: 'all',
		});

		OrganizationCategoryRepo.promise = newAllOrganizationsPromise;

		if(OrganizationCategoryRepo.data) {
			newAllOrganizationsPromise.then(function(data) {
				OrganizationCategoryRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		}
		else {
			OrganizationCategoryRepo.data = new OrganizationCategoryRepo(newAllOrganizationsPromise);	
		}
		
		OrganizationCategoryRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'organization-category', 
			method: '',
		});
				
		OrganizationCategoryRepo.set(OrganizationCategoryRepo.listener);

		return OrganizationCategoryRepo.data;
	
	};

	
	OrganizationCategoryRepo.ready = function() {
		return OrganizationCategoryRepo.promise;
	};

	OrganizationCategoryRepo.listen = function() {
		return OrganizationCategoryRepo.listener;
	};
	
	return OrganizationCategoryRepo;
	
});