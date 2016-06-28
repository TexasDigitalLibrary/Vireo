vireo.service("AbstractAppRepo", function AbstractAppRepo(AbstractRepo) {

	var AbstractAppRepo = function (constructor) {
		var abstractAppRepo = this;
		angular.extend(abstractAppRepo, new AbstractRepo(constructor));

		// additional app level repo methods and variables

		return abstractAppRepo;
	};

	return AbstractAppRepo;

});