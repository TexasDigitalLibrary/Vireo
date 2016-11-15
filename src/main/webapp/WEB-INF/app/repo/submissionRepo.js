vireo.repo("SubmissionRepo", function SubmissionRepo($q, WsApi) {

	var submissionRepo = this;

	// additional repo methods and variables

	submissionRepo.findSubmissionById = function(id) {

		var foundSubmission = submissionRepo.findById(id);

		var defer = $q.defer();
		
		if(!foundSubmission) {
			submissionRepo.clearValidationResults();
			angular.extend(submissionRepo.mapping.one, {
				'method': 'get-one/' + id
			});
			var fetchPromise = WsApi.fetch(submissionRepo.mapping.one);
			fetchPromise.then(function(res) {

				if(angular.fromJson(res.body).meta.type != "ERROR") {
					// angular.extend(submissionRepo.list, angular.fromJson(res.body).payload);
					foundSubmission = angular.fromJson(res.body).payload.Submission;
					submissionRepo.add(foundSubmission);
					defer.resolve(foundSubmission);
				}
			});	
		} else {
			defer.resolve(foundSubmission);
		}

		return defer.promise;
	};

	submissionRepo.query = function(columns, page, size) {
		angular.extend(submissionRepo.mapping.query, {
			'method': 'query/' + page + '/' + size,
			'data': columns
		});
		var promise = WsApi.fetch(submissionRepo.mapping.query);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.type != "ERROR") {
				angular.extend(submissionRepo, angular.fromJson(res.body).payload);
			}
		});
		return promise;
	};

	submissionRepo.batchUpdateStatus = function(submissionState) {

		console.log(submissionState);

		angular.extend(submissionRepo.mapping.batchUpdateSubmissionState, {
			'data': submissionState
		});
		var promise = WsApi.fetch(submissionRepo.mapping.batchUpdateSubmissionState);
		
		return promise;

	};

	submissionRepo.batchAssignTo = function(assignee) {

		console.log(assignee);

		angular.extend(submissionRepo.mapping.batchAssignTo, {
			'data': assignee
		});
		var promise = WsApi.fetch(submissionRepo.mapping.batchAssignTo);
		
		return promise;

	};

	submissionRepo.listen(function(res) {
		var submission = angular.fromJson(res.body).payload.Submission;
		console.log(submissionRepo.getContents());
	});
	
	return submissionRepo;

});	