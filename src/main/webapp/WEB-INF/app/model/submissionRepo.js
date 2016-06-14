vireo.service("SubmissionRepo", function(AbstractModel, WsApi) {

	var self;

	var SubmissionRepo = function(futureData) {
		self = this;

		//This causes our model to extend AbstractModel
		angular.extend(self, AbstractModel);
		
		self.unwrap(self, futureData);		

	};

	SubmissionRepo.data = null;
	
	SubmissionRepo.listener = null;

	SubmissionRepo.promise = null;
	
	SubmissionRepo.set = function(data) {
		self.unwrap(self, data);
	};

	SubmissionRepo.get = function() {

		if(SubmissionRepo.promise) return SubmissionRepo.data;

		var newSubmissionRepoPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'submission', 
			method: 'all',
		});

		SubmissionRepo.promise = newSubmissionRepoPromise;

		if(SubmissionRepo.data) {
			newSubmissionRepoPromise.then(function(data) {
				SubmissionRepo.set(JSON.parse(data.body).payload.HashMap);
			});
		}
		else {
			SubmissionRepo.data = new SubmissionRepo(newSubmissionRepoPromise);	
		}

		SubmissionRepo.listener = WsApi.listen({
			endpoint: '/channel', 
			controller: 'submission', 
			method: '',
		});
				
		SubmissionRepo.set(SubmissionRepo.listener);

		return SubmissionRepo.data;
	};

	SubmissionRepo.create = function(organizationId) {
		
		var createSubmissionPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'submission', 
			method: 'create',
			data: {organizationId: organizationId}
		});

		return createSubmissionPromise;

	}

	return SubmissionRepo;

});