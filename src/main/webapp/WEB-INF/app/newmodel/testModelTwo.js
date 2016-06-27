vireo.service("TestModelTwo", function TestModelTwo(AbstractAppModel) {

	var TestModelTwo = function(data) {
		angular.extend(this, new AbstractAppModel(data));
		return this;
	};
	
	return TestModelTwo;
});