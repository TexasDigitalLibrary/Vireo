vireo.controller("CustomActionSettingsController", function($controller, $scope, CustomActionSettings) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));


	$scope.customAction = CustomActionSettings.get();

	$scope.modalData = {};

	$scope.ready = CustomActionSettings.ready();

	$scope.ready.then(function() {

		$scope.createCustomActionSettings = function(customAction) {
			
			$scope.modalData.newCustomAction = {};
			CustomActionSettings.create(customAction);
		};
		
		$scope.loadEditModal = function(customAction) {
			$scope.modalData.editCustomAction = customAction;
		};
	});
});