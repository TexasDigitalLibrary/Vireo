
vireo.controller("SettingsController", function ($controller, $scope, $timeout, UserSettings, ConfigurableSettings) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.settings = {};
		
	$scope.settings.configurable = ConfigurableSettings.get();

	if(!$scope.isAnonymous()) {

		$scope.settings.user  = UserSettings.get();

		UserSettings.ready().then(function() {
			$scope.updateUserSetting = function(name, timer) {
				if($scope.userSettingsForm && Object.keys($scope.userSettingsForm.$error).length) return;

				timer = typeof timer == "undefined" ? 0 : timer;

				if($scope.typingTimer) clearTimeout($scope.typingTimer);
				$scope.typingTimer = setTimeout(function() {
					UserSettings.update(name, $scope.settings.user[name]);
				}, timer);
			};
		});
	}

	var filterHtml = function(html) {
		var temp = document.createElement("div");
    	if (!html) {
      		return "";
    	}
    	temp.innerHTML = html;
		return temp.textContent || temp.innerText || "";
  	};
  	
	ConfigurableSettings.ready().then(function() {

		//TODO:  check these update config settings methods for redundancy and clean up.
		$scope.delayedUpdateConfigurableSettings = function(type,name) {

			if($scope.pendingUpdate) $timeout.cancel($scope.updateTimeout);

			$scope.pendingUpdate = true;

			$scope.updateTimeout = $timeout(function() {
				$scope.updateConfigurableSettings(type,name,$scope.settings.configurable[type][name]);
				$scope.pendingUpdate = false;
			}, 500);

		};

		$scope.updateConfigurableSettingsPlainText = function(type,name) {
			ConfigurableSettings.update(type,name,filterHtml($scope.settings.configurable[type][name])).then(function(data) {
				var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
				console.log(validationResponse);
			});
		};

		$scope.updateConfigurableSettings = function(type,setting) {
			ConfigurableSettings.update(type,setting,$scope.settings.configurable[type][setting]).then(function(data) { 
				var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
				console.log(validationResponse);
			});
		};

		$scope.resetConfigurableSettings = function(type,name) {
			ConfigurableSettings.reset(type,name,$scope.settings.configurable[type][name]).then(function(data) {
				var validationResponse = angular.fromJson(data.body).payload.ValidationResponse;
				console.log(validationResponse);
			});
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
