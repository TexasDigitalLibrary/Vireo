vireo.controller("CustomActionSettingsController", function($controller, $scope, CustomActionSettings) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));


	$scope.customAction = CustomActionSettings.get();

	$scope.modalData = {};

	$scope.ready = CustomActionSettings.ready();

	$scope.ready.then(function() {

		$scope.createCustomActionSettings = function(customAction) {
			CustomActionSettings.create(customAction);
		};

		$scope.loadCreateModal = function() {
			$scope.modalData.newCustomAction = {
				isStudentVisible: false
			};
		};

		$scope.editCustomActionSettings = function(customAction) {

			console.log(customAction);

			CustomActionSettings.edit(customAction);
		};

		$scope.loadEditModal = function(customAction) {
			$scope.modalData.editCustomAction = customAction;
		};

	});
});