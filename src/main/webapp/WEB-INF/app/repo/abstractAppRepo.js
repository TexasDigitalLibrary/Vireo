vireo.service("AbstractAppRepo", function AbstractAppRepo(AbstractRepo) {

	var AbstractAppRepo = function (constructor, mapping) {
		var abstractAppRepo = this;
		angular.extend(abstractAppRepo, new AbstractRepo(constructor, mapping));

		// additional app level repo methods and variables

		return abstractAppRepo;
	};

	return AbstractAppRepo;

});