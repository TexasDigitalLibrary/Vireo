vireo.repo("FieldPredicateRepo", function FieldPredicateRepo(WsApi) {

	// additional repo methods and variables

	var fieldPredicateRepo = this;

	this.findByValue = function(value) {
		angular.extend(this.mapping.one, {
			'method': value
		});
		return WsApi.fetch(this.mapping.one);
	};

	return fieldPredicateRepo;

});
