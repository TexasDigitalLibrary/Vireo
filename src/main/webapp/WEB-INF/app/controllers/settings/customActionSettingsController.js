vireo.controller("CustomActionSettingsController", function($controller, $scope, CustomActionSettings) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));


	$scope.customAction = CustomActionSettings.get();
	
	$scope.modalData = {
		newCustomAction: {},
		editCustomAction: {}
	};

	$scope.ready = CustomActionSettings.ready();

	$scope.ready.then(function() {

		$scope.createCustomActionSettings = function(newCustomAction) {

			CustomActionSettings.create(newCustomAction);

			$scope.modalData.newCustomAction.label = "";
			$scope.modalData.newCustomAction.studentVisible = false;
			
		};
		
		$scope.loadEditModal = function(customAction) {
			$scope.modalData.editCustomAction.label = customAction.label;
			$scope.modalData.editCustomAction.studentVisible = customAction.studentVisible;
		};
	});
});