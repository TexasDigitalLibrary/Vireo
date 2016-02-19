vireo.controller("SettingsController", function ($controller, $scope, $q, $location, $routeParams, User, UserSettings, ConfigurableSettings, SidebarService) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.user = User.get();

	$scope.settings = {};
	
	$scope.ready = $q.all([UserSettings.ready(), ConfigurableSettings.ready()]);
		
	$scope.settings.user  = UserSettings.get();
	$scope.settings.configurable = ConfigurableSettings.get();

	$scope.ready.then(function() {

		$scope.updateUserSetting = function(setting, timer) {
			if(Object.keys($scope.userSettingsForm.$error).length) return;

			timer = typeof timer == "undefined" ? 0 : timer;

			if($scope.typingTimer) clearTimeout($scope.typingTimer);
			$scope.typingTimer = setTimeout(function() {
				UserSettings.update(setting, $scope.settings.user[setting]);
			}, timer);
		};

		$scope.updateConfigurableSettings = function(type,setting) {	
			ConfigurableSettings.update(type,setting,$scope.settings.configurable[type][setting]);
		};

		$scope.resetConfigurableSettings = function(type,setting) {
			ConfigurableSettings.reset(type,setting);
		};

	});	

	$scope.editMode = function(prop) {
		$scope["edit"+prop] = true;
	};

	$scope.viewMode = function(prop) {
		$scope["edit"+prop] = false;
	}

	$scope.confirmEdit = function($event, prop) {
		if($event.which == 13) {
			
			if(prop) $scope["edit"+prop] = false;
			
			$event.target.blur();

		}
	}

	$scope.hasError = function(field) {
		if(!field) field = {};
		return Object.keys(field).length > 0;
	}

	/**
	 * Toggle options
	 * 
	 * {evaluation: gloss}
	 * 
	 */
	
	//Submission Availability pane
	$scope.submissionsOpenOptions = [
		{"true": "Open"}, 
		{"false": "Closed"}
	];

	$scope.allowMultipleSubmissionsOptions = [
		{"true": "Yes"}, 
		{"false": "No"}
	];

	//Orcid settings pane
	$scope.orcidValidationOptions = [
		{"true": "Yes"},
		{"false": "No"}
	];

	$scope.orcidAuthenticationOptions = [
		{"true": "Yes"},
		{"false": "No"}
	];

});
