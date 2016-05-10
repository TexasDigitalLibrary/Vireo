vireo.controller("CustomActionSettingsController", function($controller, $scope, $q, $timeout, CustomActionSettings, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.customActions = CustomActionSettings.get();
	
	$scope.ready = $q.all([CustomActionSettings.ready()]);
	
	$scope.dragging = false;
	
	$scope.serverErrors = [];

	$scope.trashCanId = 'custom-action-trash';
	
	$scope.ready.then(function() {

		$scope.resetCustomAction = function() {
			$scope.modalData = { 
				isStudentVisible: false 
			};
		}
		
		$scope.closeModal = function(modalId) {
			angular.element('#' + modalId).modal('hide');
			// clear all errors, but not infos or warnings
			if($scope.serverErrors !== undefined) {
				$scope.serverErrors.errors = undefined;
			}
		}

		$scope.resetCustomAction();

		$scope.createCustomAction = function() {
			CustomActionSettings.create($scope.modalData).then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetCustomAction();
					$scope.closeModal("customActionNewModal");
				}
			});
		};
		
		$scope.selectCustomAction = function(index) {
			$scope.modalData = $scope.customActions.list[index];
		};
		
		$scope.editCustomAction = function(index) {
			$scope.serverErrors = [];
			$scope.selectCustomAction(index - 1);
			angular.element('#customActionEditModal').modal('show');
		};
		
		$scope.updateCustomAction = function() {
			CustomActionSettings.update($scope.modalData).then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetCustomAction();
					$scope.closeModal("customActionEditModal");
				}
			});
		};
		
		$scope.reorderCustomAction = function(src, dest) {
			CustomActionSettings.reorder(src, dest).then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetCustomAction();
				}
			});
		};
		
		$scope.removeCustomAction = function(index) {
			CustomActionSettings.remove(index).then(function(data) {
				$scope.serverErrors = angular.fromJson(data.body).payload.ValidationResponse;
				if($scope.serverErrors === undefined || $scope.serverErrors.errors.length == 0) {
					$scope.resetCustomAction();
					$scope.closeModal("customActionConfirmRemoveModal");
				}
			});
		};

		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectCustomAction,			
			model: $scope.customActions,
			confirm: '#customActionConfirmRemoveModal',
			reorder: $scope.reorderCustomAction,
			container: '#custom-action'
		});
		
	});
});