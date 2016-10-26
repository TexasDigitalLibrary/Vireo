vireo.controller("ItemViewController", function ($controller, $routeParams, $scope, ItemViewService) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.data = {};

	ItemViewService.selectSubmissionById($routeParams.id).then(function(submission) {
		
		$scope.submission = submission;
		
		for(var i in $scope.submission.fieldValues) {
			$scope.data[$scope.submission.fieldValues[i].fieldPredicate.value] = $scope.submission.fieldValues[i].value;
		}
		
		$scope.getViewTitle = function() {
			return $scope.data['last_name'] + ', ' + $scope.data['first_name'] + ' (' + $scope.data['department'] + ' - ' + $scope.data['degree'] + ')';
		};
		
		$scope.edit = function(key) {
			console.log('edit', key);
		};
		
	});
	
});
