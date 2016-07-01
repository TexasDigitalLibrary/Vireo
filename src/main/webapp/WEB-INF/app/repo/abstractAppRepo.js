vireo.service("AbstractAppRepo", function (AbstractRepo) {

	var AbstractAppRepo = function (model, mapping) {
		
		angular.extend(this, new AbstractRepo(model, mapping));

		// additional app level repo methods and variables

		return this;
	};

	return AbstractAppRepo;

});