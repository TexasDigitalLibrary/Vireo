vireo.controller('SettingsController', function ($controller, $scope, $location, $routeParams, UserSettings) {
	angular.extend(this, $controller('AbstractController', {$scope: $scope}));
	
	$scope.getTest = function() {
		console.log("foo");
	};
	
	$scope.settings = {};

	$scope.settings.user  = UserSettings.get();
	
	$scope.ready = UserSettings.ready;
	

	UserSettings.ready().then(function() {
		
		$scope.updateUserSetting = function(setting, timer) {

			timer = typeof timer == "undefined" ? 0 : timer;

			if($scope.typingTimer) clearTimeout($scope.typingTimer);
			$scope.typingTimer = setTimeout(function() {
				
				UserSettings.update(setting, $scope.settings.user[setting], $scope.settings.user['_'+setting]);
			
			}, timer);
			
		};
		
	});
	
});