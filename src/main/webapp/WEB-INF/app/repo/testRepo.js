vireo.service("TestRepo", function TestRepo(AbstractAppRepo, TestModel) {

	var TestRepo = this;

	angular.extend(this, new AbstractAppRepo(TestModel));

	console.log(TestRepo);

	setTimeout(function() {
		console.log(TestRepo.getCache());
	}, 2000);

	return this;
});