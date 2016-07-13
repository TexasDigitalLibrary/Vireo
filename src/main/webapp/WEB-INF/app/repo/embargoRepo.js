vireo.repo("EmbargoRepo", function EmbargoRepo(WsApi) {

	var embargoRepo = this;

	this.sort = function(guarantor, facet) {
		embargoRepo.clearValidationResults();
		angular.extend(this.mapping.sort, {'method': 'sort/'+ guarantor +'/'+ facet});
		var promise = WsApi.fetch(this.mapping.sort);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.type == "INVALID") {
				angular.extend(embargoRepo, angular.fromJson(res.body).payload);
				console.log(embargoRepo);
			}
		});
		return promise;
	};

	this.reorder = function(guarantor, src, dest) {
		embargoRepo.clearValidationResults();
		angular.extend(this.mapping.reorder, {'method': 'reorder/'+ guarantor +'/'+ src + '/' + dest});
		var promise = WsApi.fetch(this.mapping.reorder);
		promise.then(function(res) {
			if(angular.fromJson(res.body).meta.type == "INVALID") {
				angular.extend(embargoRepo, angular.fromJson(res.body).payload);
				console.log(embargoRepo);
			}
		});
		return promise;
	}; 

	return this;
	
});