vireo.repo("ManagerSubmissionListColumnRepo", function ManagerSubmissionListColumnRepo(WsApi) {

	var managerSubmissionListColumnRepo = this;
	
	this.updateSubmissionListColumns = function() {
		angular.extend(managerSubmissionListColumnRepo.mapping.update, {
			'data': managerSubmissionListColumnRepo.getAll()
		});
		return WsApi.fetch(managerSubmissionListColumnRepo.mapping.update);
	};

	this.resetSubmissionListColumns = function() {
		return WsApi.fetch(managerSubmissionListColumnRepo.mapping.reset);
	};

	return managerSubmissionListColumnRepo;

});
