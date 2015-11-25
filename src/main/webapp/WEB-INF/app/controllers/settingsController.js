vireo.controller('SettingsController', function ($controller, $scope, $location, $routeParams, UserSettings) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	$scope.userSettings  = UserSettings.get();

	UserSettings.ready().then(function() {
		console.log($scope.userSettings);
	});

});