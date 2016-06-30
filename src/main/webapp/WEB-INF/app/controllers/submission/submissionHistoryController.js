vireo.controller('SubmissionHistoryController', function ($controller, $scope, NgTableParams, SubmissionRepo) {

	SubmissionRepo.manualGetAllPromise().then(function(submissions){
		$scope.submissions = submissions;
		$scope.tableParams = new NgTableParams({}, {filterDelay: 0, dataset: $scope.submissions}); 
		$scope.tableParams.reload();
	});
	
});
