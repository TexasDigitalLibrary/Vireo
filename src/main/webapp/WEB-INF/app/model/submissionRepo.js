vireo.service("SubmissionRepo", function($q, WsApi, VireoAbstractModel) {

	var SubmissionRepo = this;
	angular.extend(SubmissionRepo, VireoAbstractModel);

	var cache = {
		list : [],
		ready: false
	};

	var api = {
		request: {
			endpoint  : '/private/queue',
			controller: 'submission',
			method    : ''
		}
	};

	SubmissionRepo.getAll = function(sync) {
		cache.ready = sync ? !sync : cache.ready;
		SubmissionRepo.getAllPromise(api, cache);
		return cache.list;
	};

	SubmissionRepo.findById = function(submissionId) {
		return WsApi.fetch(SubmissionRepo.buildRequest(api, 'get-one/' + submissionId))
			.then(function(rawResponse){
				return $q.when(JSON.parse(rawResponse.body).payload.Submission);
			});
	};

	SubmissionRepo.create = function(organizationId) {
		return WsApi.fetch(SubmissionRepo.buildRequest(api, 'create', {organizationId: organizationId}))
			.then(function(rawResponse){
				return $q.when(JSON.parse(rawResponse.body).payload.Submission.id);
			});
	};

});
