vireo.repo("ManagerSubmissionViewColumnRepo", function ManagerSubmissionViewColumnRepo(WsApi) {

	var managerSubmissionViewColumnRepo = this;
	// additional repo methods and variables

	this.updateSubmissionViewColumns = function() {
		angular.extend(managerSubmissionViewColumnRepo.mapping.update, {
			'data': managerSubmissionViewColumnRepo.getAll()
		});
		return WsApi.fetch(managerSubmissionViewColumnRepo.mapping.update);
	};

	this.resetSubmissionViewColumns = function() {
		return WsApi.fetch(managerSubmissionViewColumnRepo.mapping.reset);
	};

	return managerSubmissionViewColumnRepo;

});
