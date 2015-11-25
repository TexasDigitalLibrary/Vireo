vireo.controller('SettingsController', function ($controller, $scope, $location, $routeParams, UserSettings) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.getTest = function() {
		console.log("foo");
	}

	$scope.settings = {};

	$scope.settings.user  = UserSettings.get();

	UserSettings.ready().then(function() {
		console.log($scope.settings.user);
	});

	$scope.updateUserSetting = function(setting, timer) {

		timer = typeof timer == "undefined" ? 0 : timer;

		if($scope.typingTimer) clearTimeout($scope.typingTimer);
		$scope.typingTimer = setTimeout(function() {
			console.log($scope.settings.user[setting]);
			UserSettings.update(setting, $scope.settings.user[setting]);
			console.log($scope.settings.user);
		}, timer);
	}

});