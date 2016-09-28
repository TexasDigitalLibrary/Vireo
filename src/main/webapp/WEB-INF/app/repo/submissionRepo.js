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
	
	this.query = function(columns, page, size) {
		angular.extend(this.mapping.query, {
			'method': 'query/' + page + '/' + size,
			'data': columns
		});
		var promise = WsApi.fetch(this.mapping.query);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.type == "INVALID") {
				angular.extend(submissionRepo, angular.fromJson(res.body).payload);
				console.log(submissionRepo);
			}
		});
		return promise;
	};

	this.batchUpdateStatus = function(submissionState) {

		console.log(submissionState);

		angular.extend(this.mapping.batchUpdateSubmissionState, {
			'data': submissionState
		});
		var promise = WsApi.fetch(this.mapping.batchUpdateSubmissionState);
		
		return promise;

	};
	
	return this;

});