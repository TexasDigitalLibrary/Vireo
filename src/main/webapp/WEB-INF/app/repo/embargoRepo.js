core.repo("EmbargoRepo", function EmbargoRepo(WsApi) {

	var EmbargoRepo = this;

	EmbargoRepo.sort = function(guarantor, facet) {
		angular.extend(EmbargoRepo.mapping.sort, {'method': 'sort/'+ guarantor +'/'+ facet});
		return WsApi.fetch(EmbargoRepo.mapping.sort);
	};

	EmbargoRepo.reorder = function(guarantor, src, dest) {
		angular.extend(EmbargoRepo.mapping.reorder, {'method': 'reorder/'+ guarantor +'/'+ src + '/' + dest});
		return WsApi.fetch(EmbargoRepo.mapping.reorder);
	}; 

	return EmbargoRepo;
	
});