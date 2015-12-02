vireo.controller('SettingsController', function ($controller, $scope, $location, $routeParams, TabService, User) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	$scope.user  = User.get();
	User.ready().then(function(){
		$scope.user.displayName = $scope.user.firstName+ ' ' +$scope.user.lastName;
	});
});