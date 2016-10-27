vireo.controller("ItemViewController", function ($controller, $routeParams, $scope, ItemViewService) {

	angular.extend(this, $controller('AbstractController', {$scope: $scope}));

	$scope.data = {};

	ItemViewService.selectSubmissionById($routeParams.id).then(function(submission) {

		$scope.submission = submission;
		
		for(var i in $scope.submission.fieldValues) {
			var fieldValue = $scope.submission.fieldValues[i];
			$scope.data[fieldValue.fieldPredicate.value] = {
				value: fieldValue.value,
				save: function(value) {
					var key = fieldValue.fieldPredicate.value;
					console.log(key, value);
				}
			};
		}
		
		$scope.getViewTitle = function() {
			var lastName = $scope.data['last_name'] !== undefined ? $scope.data['last_name'].value : 'Unknown';
			var firstName = $scope.data['first_name'] !== undefined ? $scope.data['first_name'].value : 'Unknown';
			var department = $scope.data['department'] !== undefined ? $scope.data['department'].value : 'Unknown';
			var degree = $scope.data['degree'] !== undefined ? $scope.data['degree'].value : 'Unknown';
			return lastName + ', ' + firstName + ' (' + department + ' - ' + degree + ')';
		};
		
		$scope.edit = function(key) {
			console.log('edit', key);
			
			if($scope.data[key] != undefined) {
				$scope.data[key].editing = true;
				//$scope.data[key].save('Hello, World!');
			}

		};
		
		$scope.getTabPath = function(path) {
			return path + "/" + $scope.submission.id;
		};
		
	});
	
});
