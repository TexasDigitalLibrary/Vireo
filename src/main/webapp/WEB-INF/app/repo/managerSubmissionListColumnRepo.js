vireo.repo("ManagerSubmissionListColumnRepo", function ManagerSubmissionListColumnRepo(WsApi) {

	var managerSubmissionListColumnRepo = this;
	
	this.updateSubmissionListColumns = function(pageSize) {
		angular.extend(managerSubmissionListColumnRepo.mapping.update, {
			'method': 'update-user-columns/' + pageSize,
			'data': managerSubmissionListColumnRepo.getAll()
		});
		return WsApi.fetch(managerSubmissionListColumnRepo.mapping.update);
	};

	this.submissionListPageSize = function() {
		return WsApi.fetch(managerSubmissionListColumnRepo.mapping.pageSize);
	};

	this.resetSubmissionListColumns = function() {
		return WsApi.fetch(managerSubmissionListColumnRepo.mapping.reset);
	};

	return managerSubmissionListColumnRepo;

});
