vireo.controller("SettingsController", function ($controller, $scope, $location, $routeParams, User, UserSettings, ApplicationSettings) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));
	$scope.clicked=false;
	$scope.user = User.get();
	
	$scope.getTest = function() {
		console.log("foo");
	};

	$scope.hexcolor = { 'background': {'main':'#1b333f', 'highlight':'#43606e'},
						'submissionStepButonOn': {'main':'#1b333f', 'highlight':'#43606e'},
						'submissionStepButonOff': {'main':'#a6a18c', 'highlight':'#c7c2a9'}

	};
	$scope.resetHexColor = {
							'background': {'main':'#1b333f', 'highlight':'#43606e'},
							'submissionStepButonOn': {'main':'#1b333f', 'highlight':'#43606e'},
							'submissionStepButonOff': {'main':'#a6a18c', 'highlight':'#c7c2a9'}
	};


	// $scope.hexcolor = { background_main_color:"#1b333f",
	// 				background_highlight_color:"#43606e",
	// 				submissionStepButonOn_main_color:"#1b333f",
	// 				submissionStepButonOn_highlight_color:"#43606e",
	// 				submissionStepButonOff_main_color:"#a6a18c",
	// 				submissionStepButonOff_highlight_color:"#c7c2a9"
	// 				};

	// $scope.resetHexColor = { 	
	// 			background_main_color:"#1b333f",
	// 			background_highlight_color:"#43606e",
	// 			submissionStepButonOn_main_color:"#1b333f",
	// 			submissionStepButonOn_highlight_color:"#43606e",
	// 			submissionStepButonOff_main_color:"#a6a18c",
	// 			submissionStepButonOff_highlight_color:"#c7c2a9"
	// 						};

	$scope.settings = {};

	$scope.settings.user  = UserSettings.get();

	$scope.settings.application = ApplicationSettings.get();
	
	$scope.ready = UserSettings.ready;
	

	UserSettings.ready().then(function() {

		$scope.updateUserSetting = function(setting, timer) {

			if(Object.keys($scope.userSettingsForm.$error).length) return;

			timer = typeof timer == "undefined" ? 0 : timer;

			if($scope.typingTimer) clearTimeout($scope.typingTimer);
			$scope.typingTimer = setTimeout(function() {
				UserSettings.update(setting, $scope.settings.user[setting]);
			}, timer);
			
		};

		$scope.updateApplicationSettings = function(type, setting) {	
			ApplicationSettings.update(type, setting, $scope.settings.application[type][setting]);
		}


		
	});

	$scope.toggle = function(clicked) {
		$scope.clicked=!clicked;
	};

	$scope.editMode = function(prop) {
		$scope["edit"+prop] = true;
	};

	$scope.viewMode = function(prop) {

		$scope["edit"+prop] = false;
	}

	$scope.confirmEdit = function($event, prop) {
		if($event.keyCode == 13) $scope["edit"+prop] = false;
	}

	$scope.hasError = function(field) {

		if(!field) field = {};

		return Object.keys(field).length > 0;
	}

	$scope.change = function(data) {
		$scope.hexcolor=hexcolor;
		//console.log($scope.hexcolor);
	};

	$scope.reset = function(data) {

		ApplicationSettings.reset()

		$scope.hexcolor = angular.copy($scope.resetHexColor);
		console.log("IN RESET"+data);
		//console.log($scope.hexcolor);
	};

});