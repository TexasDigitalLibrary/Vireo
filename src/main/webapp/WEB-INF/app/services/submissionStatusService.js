vireo.service("SubmissionStatusService", function(SubmissionStateRepo) {

	var SubmissionStatusService = this;

	SubmissionStateRepo.ready().then(function() {
		 SubmissionStateRepo.getAll().forEach(function(state) {
		 	console.log(state)
    		SubmissionStatusService[state.name] = state.name;
    		console.log(SubmissionStatusService)
   		});
	});

	return SubmissionStatusService;	
});