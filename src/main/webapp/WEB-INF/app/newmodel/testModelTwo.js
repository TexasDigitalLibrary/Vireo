vireo.service("TestModelTwo", function(AbstractAppModel) {

	var TestModelTwo = function(data) {
		var testModelTwo = this;
		angular.extend(testModelTwo, new AbstractAppModel(data));
		return testModelTwo;
	};
	
	return TestModelTwo;
});