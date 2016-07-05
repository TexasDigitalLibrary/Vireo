vireo.controller("SubmissionViewController", function ($controller, $scope, NgTableParams, SubmissionRepo) {

	// SubmissionRepo.manualGetAllPromise().then(function(submissions){
	// 	$scope.submissions = submissions;
	// 	$scope.tableParams = new NgTableParams({}, {filterDelay: 0, dataset: $scope.submissions}); 
	// 	$scope.tableParams.reload();
	// });

  console.info(SubmissionRepo);

  $scope.submissions = SubmissionRepo.getAll();

  $scope.tableParams = new NgTableParams({}, {filterDelay: 0, dataset: $scope.submissions}); 

  SubmissionRepo.ready().then(function(){
	console.info('ready!!');
	console.info($scope.submissions);
  })

  SubmissionRepo.listen(function(){
	console.info($scope.submissions);
	$scope.tableParams.reload();
  });

});
