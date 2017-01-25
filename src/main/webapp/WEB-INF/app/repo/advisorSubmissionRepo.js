vireo.repo("AdvisorSubmissionRepo", function AdvisorSubmissionRepo(WsApi) {

	var AdvisorSubmissionRepo = this;

	AdvisorSubmissionRepo.findSubmissionByhash = function(hash) {

		console.log(hash);

		angular.extend(AdvisorSubmissionRepo.mapping.getByHash, {
			'method': 'advisor-review/' + hash
		});

		console.log(AdvisorSubmissionRepo.mapping.getByHash);
		var promise = WsApi.fetch(AdvisorSubmissionRepo.mapping.getByHash);

		promise.then(function(res) {
			console.log(res);
		});

		return promise;
	};

	return AdvisorSubmissionRepo;

});