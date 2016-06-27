vireo.service("AbstractAppRepo", function(AbstractRepo) {

	var AppAbstractRepo = function (constructor) {
		angular.extend(this, new AbstractRepo(constructor));
		return this;
	};

	return AppAbstractRepo;

});