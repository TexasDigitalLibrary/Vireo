vireo.repo("SubmissionStateRepo", function SubmissionStateRepo() {

	var submissionStateRepo = this;

	// additional repo methods and variables
	
	submissionStateRepo.findById = function(id) {

		var foundState = null;

		angular.forEach(submissionStateRepo.getAll(), function(state) {
			if(state.id === id) foundState = state;
		});

		return foundState;
	};

	submissionStateRepo.findByName = function(name) {

		var foundState = null;

		angular.forEach(submissionStateRepo.getAll(), function(state) {
			if(state.name == name) foundState = state;
		});
		return foundState;
	};

	return submissionStateRepo;

});