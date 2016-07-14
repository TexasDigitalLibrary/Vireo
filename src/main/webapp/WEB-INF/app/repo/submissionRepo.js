vireo.repo("SubmissionRepo", function SubmissionRepo(WsApi) {

	var submissionRepo = this;

	// additional repo methods and variables

	this.findSubmissionById = function(id) {
		submissionRepo.clearValidationResults();
		angular.extend(this.mapping.one, {
			'method': 'get-one/' + id
		});
		var promise = WsApi.fetch(this.mapping.one);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.type == "INVALID") {
				angular.extend(submissionRepo, angular.fromJson(res.body).payload);
				console.log(submissionRepo);
			}
		});
		return promise;
	};
	
	return this;

});