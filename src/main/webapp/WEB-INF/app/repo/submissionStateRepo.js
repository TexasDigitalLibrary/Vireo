vireo.repo("SubmissionStateRepo", function SubmissionStateRepo() {

	var submissionStateRepo = this;

	// additional repo methods and variables
	
	SubmissionStateRepo.findById = function(id) {

		var foundState = null;

		angular.forEach(SubmissionStateRepo.findAll(), function(state) {
			if(state.id === id) foundState = state;
		});

		return foundState;
	};

	return submissionStateRepo;

});