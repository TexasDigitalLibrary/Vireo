vireo.controller("ItemViewController", function ($controller, $location, $routeParams, $scope, SubmissionRepo) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	SubmissionRepo.findSubmissionById($routeParams.id).then(function(response) {
		
		$scope.submission = angular.fromJson(response.body).payload.Submission;
		
		$scope.getSubmissionTitle = function() {
			if($scope.submission !== undefined) {
				for(var i in $scope.submission.fieldValues) {
					if($scope.submission.fieldValues[i].fieldPredicate.value == 'title') {
						return $scope.submission.fieldValues[i].value;
					}
				}
			}
			return "No title";
		};
		
	});

});
