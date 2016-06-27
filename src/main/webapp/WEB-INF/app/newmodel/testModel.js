vireo.service("TestModel", function(AbstractAppModel) {

	var TestModel = function(data) {
		angular.extend(this, new AbstractAppModel(data));
		return this;
	};

	return TestModel;
});