vireo.controller("CustomActionSettingsController", function($controller, $scope, CustomActionSettings) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));


	$scope.settings.customAction = CustomActionSettings.get();
	$scope.newCustomActions = {
		label: "",
		visiblity: false
	};

	$scope.ready = CustomActionSettings.ready();

	$scope.ready.then(function() {

		$scope.createCustomActionSettings = function(label,isStudentVisible) {
			
			$scope.newCustomActions.label = "";
			$scope.newCustomActions.visiblity = false;

			CustomActionSettings.create(label,isStudentVisible);
		};

	});

});