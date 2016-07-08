
// The prefix _MODEL_ 
vireo.repo("StudentSubmissionRepo", function StudentSubmissionRepo(_MODEL_Submission) {

	var StudentSubmissionRepo = this;

	StudentSubmissionRepo.findSubmissionById = function(id) {
		angular.extend(StudentSubmissionRepo.mapping.one, {
			'method': 'get-one/' + id
		});
		return WsApi.fetch(StudentSubmissionRepo.mapping.one);
	};

	return StudentSubmissionRepo;

});