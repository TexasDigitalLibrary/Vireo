vireo.service("TestModel", function(AbstractAppModel) {

	var TestModel = function(data) {
		var testModel = this;
		angular.extend(testModel, new AbstractAppModel(data));
		return testModel;
	};

	return TestModel;
});