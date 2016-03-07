
vireo.controller("SettingsController", function ($controller, $scope, $timeout, UserSettings, ConfigurableSettings) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.settings = {};
		
	$scope.settings.configurable = ConfigurableSettings.get();

	if(!$scope.isAnonymous()) {

		$scope.settings.user  = UserSettings.get();

		UserSettings.ready().then(function() {
			$scope.updateUserSetting = function(setting, timer) {
				if(Object.keys($scope.userSettingsForm.$error).length) return;

				timer = typeof timer == "undefined" ? 0 : timer;

				if($scope.typingTimer) clearTimeout($scope.typingTimer);
				$scope.typingTimer = setTimeout(function() {
					UserSettings.update(setting, $scope.settings.user[setting]);
				}, timer);
			};
		});
	}

	ConfigurableSettings.ready().then(function() {

		$scope.delayedUpdateConfigurableSettings = function(type,setting) {

			if($scope.pendingUpdate) $timeout.cancel($scope.updateTimeout);

			$scope.pendingUpdate = true;

			$scope.updateTimeout = $timeout(function() {
				$scope.updateConfigurableSettings(type,setting,$scope.settings.configurable[type][setting]);
				$scope.pendingUpdate = false;
			}, 500);

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
	
	// SUBMISSION AVAILABILITY
	$scope.submissionsOpenOptions = [
		{"true": "Open"}, 
		{"false": "Closed"}
	];

	$scope.allowMultipleSubmissionsOptions = [
		{"true": "Yes"}, 
		{"false": "No"}
	];

    // PROQUEST / UMI SETTINGS / DEGREE CODE
	$scope.proquestIndexingOptions = [
		{"true": "Yes"}, 
        {"false": "No"}
    ];

	// ORCID
	$scope.orcidValidationOptions = [
		{"true": "Yes"},
		{"false": "No"}
	];

	$scope.orcidAuthenticationOptions = [
		{"true": "Yes"},
		{"false": "No"}
	];

});
