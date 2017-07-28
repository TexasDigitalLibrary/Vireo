vireo.repo("SubmissionStatusRepo", function SubmissionStatusRepo() {

	var submissionStatusRepo = this;

	// additional repo methods and variables
	
	submissionStatusRepo.findById = function(id) {

		var foundStatus = null;

		angular.forEach(submissionStatusRepo.getAll(), function(status) {
			if(status.id === id) foundStatus = status;
		});

		return foundStatus;
	};

	submissionStatusRepo.findByName = function(name) {

		var foundStatus = null;

		angular.forEach(submissionStatusRepo.getAll(), function(status) {
			if(status.name == name) foundStatus = status;
		});
		return foundStatus;
	};

	return submissionStatusRepo;

});