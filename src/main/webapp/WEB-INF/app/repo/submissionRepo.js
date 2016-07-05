vireo.repo("SubmissionRepo", function SubmissionRepo(WsApi) {

	// additional repo methods and variables

	this.findSubmissionById = function(id) {
		angular.extend(this.mapping.one, {
			'method': 'get-one/' + id
		});
		return WsApi.fetch(this.mapping.one);
	};

	return this;

});