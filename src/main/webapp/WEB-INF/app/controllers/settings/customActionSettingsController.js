vireo.controller("CustomActionSettingsController", function($controller, $scope, CustomActionSettings) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));


	$scope.settings.customAction = CustomActionSettings.get();
	$scope.newCustomAction = {
		label: "",
		visiblity: false
	};
	
	$scope.editCustomAction = {
			label: "",
			studentVisible: false
		};

	$scope.ready = CustomActionSettings.ready();

	$scope.ready.then(function() {

		$scope.createCustomActionSettings = function(label,isStudentVisible) {
			
			$scope.newCustomAction.label = "";
			$scope.newCustomAction.visiblity = false;

			CustomActionSettings.create(label,isStudentVisible);
		};
		
		$scope.loadEditModal = function(customAction) {
			$scope.editCustomAction = customAction;
		};
	});
});