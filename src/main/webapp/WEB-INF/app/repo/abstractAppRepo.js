vireo.service("AbstractAppRepo", function (AbstractRepo) {

	var AbstractAppRepo = function (model, mapping) {
		var abstractAppRepo = this;
		angular.extend(abstractAppRepo, new AbstractRepo(model, mapping));

		// additional app level repo methods and variables

		return abstractAppRepo;
	};

	return AbstractAppRepo;

});