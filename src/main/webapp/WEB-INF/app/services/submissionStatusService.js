vireo.service("SubmissionStatusService", function(SubmissionStateRepo) {

	var SubmissionStatusService = this;

	SubmissionStateRepo.ready().then(function() {
		 SubmissionStateRepo.getAll().forEach(function(state) {
    		SubmissionStatusService[state.name] = state.name;
   		});
	});

	return SubmissionStatusService;	
});
