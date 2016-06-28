vireo.factory("TestRepo", function(AbstractAppRepo, TestModel) {

	var TestRepo = function() {
		var testRepo = this;
		angular.extend(testRepo, new AbstractAppRepo(TestModel));
		return testRepo;
	}

	return new TestRepo();
	
});