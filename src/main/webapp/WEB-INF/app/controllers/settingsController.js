vireo.controller('SettingsController', function ($controller, $scope, $location, $routeParams, TabService, User) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	$scope.user  = User.get();

	$scope.clicked = '+';

	$scope.changeSymbol = function() {
		console.log("HERE");
		$scope.clicked = $scope.clicked=='+'? '-':'+';
	}

});