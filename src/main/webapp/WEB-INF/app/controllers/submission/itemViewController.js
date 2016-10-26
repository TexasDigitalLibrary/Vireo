vireo.controller("ItemViewController", function ($controller, $scope, ItemViewService) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.submission = ItemViewService.getSelectedSubmission();
	
	$scope.getSubmissionTitle = function() {
		for(var i in $scope.submission.fieldValues) {
			if($scope.submission.fieldValues[i].fieldPredicate.value == 'title') {
				return $scope.submission.fieldValues[i].value;
			}
		}
		return "No title";
	};

});
