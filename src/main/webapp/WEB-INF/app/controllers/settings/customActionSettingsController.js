vireo.controller("CustomActionSettingsController", function($controller, $scope, $q, $timeout, CustomActionSettings, DragAndDropListenerFactory) {
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.customActions = CustomActionSettings.get();
	
	$scope.ready = $q.all([CustomActionSettings.ready()]);
	
	$scope.dragging = false;

	$scope.trashCanId = 'custom-action-trash';
	
	$scope.ready.then(function() {

		$scope.resetCustomAction = function() {
			$scope.modalData = { 
				isStudentVisible: false 
			};
		}

		$scope.resetCustomAction();

		$scope.createCustomAction = function() {
			CustomActionSettings.create($scope.modalData).then(function(data) {
				var errors = angular.fromJson(data.body).payload;
				console.log(errors);
				$scope.resetCustomAction();
			});
		};
		
		$scope.selectCustomAction = function(index) {
			$scope.modalData = $scope.customActions.list[index];
		};
		
		$scope.editCustomAction = function(index) {
			$scope.selectCustomAction(index - 1);
			angular.element('#customActionEditModal').modal('show');
		};
		
		$scope.updateCustomAction = function() {
			CustomActionSettings.update($scope.modalData).then(function(data) {
				var errors = angular.fromJson(data.body).payload;
				console.log(errors);
				$scope.resetCustomAction();
			});
		};
		
		$scope.reorderCustomAction = function(src, dest) {
			CustomActionSettings.reorder(src, dest).then(function(data) {
				var errors = angular.fromJson(data.body).payload;
				console.log(errors);
				$scope.resetCustomAction();
			});
		};
		
		$scope.removeCustomAction = function(index) {
			CustomActionSettings.remove(index).then(function(data) {
				var errors = angular.fromJson(data.body).payload;
				console.log(errors);
				$scope.resetCustomAction();
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