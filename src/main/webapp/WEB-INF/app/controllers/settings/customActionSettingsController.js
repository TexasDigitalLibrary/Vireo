vireo.controller("CustomActionSettingsController", function($controller, $scope, $q, CustomActionSettings, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.ready = $q.all([CustomActionSettings.ready()]);
	
	$scope.customActions = CustomActionSettings.get();
	
	$scope.dragging = false;

	$scope.trashCanId = 'custom-action-trash';
	
	// defaults
	$scope.resetCustomAction = function() {
		$scope.modalData = { 
			isStudentVisible: false 
		};
	}

	$scope.resetCustomAction();

	$scope.ready.then(function() {		
		
		$scope.createCustomAction = function() {
			CustomActionSettings.create($scope.modalData);
			$scope.resetCustomAction();
		};
		
		$scope.selectCustomAction = function(index) {
			$scope.modalData = $scope.customActions.list[index];
		};
		
		$scope.editCustomAction = function(index) {
			$scope.selectCustomAction(index - 1);
			angular.element('#customActionEditModal').modal('show');
		};
		
		$scope.updateCustomAction = function() {
			CustomActionSettings.update($scope.modalData);
			$scope.resetCustomAction();
		};
		
		$scope.reorderCustomAction = function(src, dest) {
			CustomActionSettings.reorder(src, dest);
		};
		
		$scope.removeCustomAction = function(index) {
			CustomActionSettings.remove(index);
			$scope.resetCustomAction();
		};
		
		$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
			trashId: $scope.trashCanId,
			dragging: $scope.dragging,
			select: $scope.selectCustomAction,			
			list: $scope.customActions.list,
			confirm: '#customActionConfirmRemoveModal',
			reorder: $scope.reorderCustomAction
		});
		
	});
});