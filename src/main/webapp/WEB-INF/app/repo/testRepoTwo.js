vireo.service("TestRepoTwo", function TestRepoTwo(AbstractAppRepo, TestModelTwo) {

	var TestRepoTwo = this;

	angular.extend(this, new AbstractAppRepo(TestModelTwo));

	console.log(TestRepoTwo);

	setTimeout(function() {
		console.log(TestRepoTwo.getCache());
	}, 2000);	
	
	return this;
});