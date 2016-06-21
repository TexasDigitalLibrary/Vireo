vireo.service("SubmissionRepo", function($q, AbstractModel, WsApi) {

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

	SubmissionRepo.ready = function() {
		return SubmissionRepo.promise;
	};

	SubmissionRepo.findById = function(submissionId) {
		
		var findByIdDefer = $q.defer();

		var findByIdFetchPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'submission', 
			method: 'get-one/'+submissionId,
		});

		findByIdFetchPromise.then(function(rawRes) {
			var submission = JSON.parse(rawRes.body).payload.Submission;
			findByIdDefer.resolve(submission);
		});

		return findByIdDefer.promise;

	};

	SubmissionRepo.create = function(organizationId) {

		var creationDefer = $q.defer();
		
		var createSubmissionPromise = WsApi.fetch({
			endpoint: '/private/queue', 
			controller: 'submission', 
			method: 'create',
			data: {organizationId: organizationId}
		});

		createSubmissionPromise.then(function(rawData) {
			creationDefer.resolve(JSON.parse(rawData.body).payload.Submission.id);
		});

		return creationDefer.promise;

	}

	return SubmissionRepo;

});