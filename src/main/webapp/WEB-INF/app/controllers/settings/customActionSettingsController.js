vireo.controller("CustomActionSettingsController", function ($controller, $scope, $q, $timeout, CustomActionDefinitionRepo, DragAndDropListenerFactory) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.customActionRepo = CustomActionDefinitionRepo;

	$scope.customActions = CustomActionDefinitionRepo.getAll();

	CustomActionDefinitionRepo.listen(function(data) {
        $scope.resetCustomAction();
	});
	
	$scope.ready = $q.all([CustomActionDefinitionRepo.ready()]);
	
	$scope.dragging = false;
	
	$scope.trashCanId = 'custom-action-trash';

	$scope.forms = {};
	
	$scope.ready.then(function() {

		$scope.resetCustomAction = function() {
			$scope.customActionRepo.clearValidationResults();
			for(var key in $scope.forms) {
    			if($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
    				$scope.forms[key].$setPristine();
    			}
    		}
			if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
    			$scope.modalData.refresh();
    		}
			$scope.modalData = { 
				isStudentVisible: false 
			};
			$scope.closeModal();
		};

		$scope.resetCustomAction();

		$scope.createCustomAction = function() {
			CustomActionDefinitionRepo.create($scope.modalData);
		};
		
		$scope.selectCustomAction = function(index) {
			$scope.modalData = $scope.customActions[index];
		};
		
		$scope.editCustomAction = function(index) {
			$scope.selectCustomAction(index - 1);
			$scope.openModal('#customActionEditModal');
		};
		
		$scope.updateCustomAction = function() {
			$scope.modalData.save();
		};

		$scope.removeCustomAction = function() {
			$scope.modalData.delete();
		};

		$scope.reorderCustomAction = function(src, dest) {
			CustomActionDefinitionRepo.reorder(src, dest);
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