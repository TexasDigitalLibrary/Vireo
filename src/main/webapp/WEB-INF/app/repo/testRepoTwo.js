vireo.factory("TestRepoTwo", function(AbstractAppRepo, TestModelTwo) {

	var TestRepoTwo = function() {
		var testRepoTwo = this;
		angular.extend(testRepoTwo, new AbstractAppRepo(TestModelTwo));
		return testRepoTwo;
	}

	return new TestRepoTwo();
	
});