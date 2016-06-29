vireo.controller("SubmissionViewController", function ($controller, $scope, NgTableParams, SubmissionRepo) {

	SubmissionRepo.manualGetAllPromise().then(function(submissions){
		console.info('controller getCachePromise then submissions is ', submissions);
		$scope.submissions = submissions;
		$scope.tableParams = new NgTableParams({}, {filterDelay: 0, dataset: $scope.submissions}); 
		$scope.tableParams.reload();
	});

	$scope.print = function(){
		// $scope.submissions = SubmissionRepo.getAll(true);
		console.info($scope.submissions);
		// $scope.tableParams = new NgTableParams({}, {filterDelay: 0, dataset: $scope.submissions}); 
		// console.info('data: ', data);
		$scope.tableParams.reload();
	};
});
