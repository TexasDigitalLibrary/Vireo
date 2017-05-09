vireo.repo("ManagerFilterColumnRepo", function ManagerFilterColumnRepo(WsApi) {

	var managerFilterColumnRepo = this;

	managerFilterColumnRepo.updateFilterColumns = function(filterColumns) {
		angular.extend(managerFilterColumnRepo.mapping.update, {
			'method': 'update-user-filter-columns',
			'data': filterColumns
		});
		return WsApi.fetch(managerFilterColumnRepo.mapping.update);
	};
	
	return managerFilterColumnRepo;

});
