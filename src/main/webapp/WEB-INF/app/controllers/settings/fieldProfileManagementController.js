vireo.controller("FieldProfileManagementController", function ($q, $controller, $scope, DragAndDropListenerFactory, OrganizationRepo, ControlledVocabularyRepo, InputTypeService) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.selectedOrganization = OrganizationRepo.getSelectedOrganization();
	
	$scope.$watch(
		"step",
		function handleStepChanged(newStep, oldStep) {
			// console.log(newStep)
			// console.log(oldStep)
			$scope.resetFieldProfiles();

			$scope.dragControlListeners.getListener().trash.id = 'field-profile-trash-' + $scope.step.id;
			$scope.dragControlListeners.getListener().confirm.remove.modal = '#fieldProfilesConfirmRemoveModal-' + $scope.step.id;
        }
    );

	$scope.controlledVocabularies = ControlledVocabularyRepo.get();

	//Working!!!!
	$scope.inputTypes = InputTypeService.inputTypes();
	
	console.info('value: VVVV');
	console.info($scope.inputTypes);
	
	$scope.dragging = false;
	
	$scope.sortAction = "confirm";

	$scope.uploadAction = "confirm";
	
	$scope.resetFieldProfiles = function() {
		var position = 1;	
		angular.forEach($scope.step.aggregateFieldProfiles, function(fieldProfile) {
			fieldProfile.position = position;
			position++;
		});
	};

	$scope.resetFieldProfiles();
	
	$scope.createFieldProfile = function(newFieldProfile) {
		console.log(newFieldProfile);
		console.log('create field profile');
	};
	
	$scope.selectFieldProfile = function(index) {
		$scope.modalData = $scope.step.aggregateFieldProfiles[index];
	};
	
	$scope.editFieldProfile = function(index) {
		$scope.selectFieldProfile(index - 1);
		angular.element('#fieldProfilesEditModal-' + $scope.step.id).modal('show');
	};
	
	$scope.updateFieldProfile = function() {
		// TODO
		console.log('update field profile');
	};

	$scope.reorderFieldProfiles = function(src, dest) {
		WorkflowStepRepo.reorder($scope.step.id, src, dest).then(function() {
			$scope.resetFieldProfiles();
		});
	};

	$scope.sortFieldProfiles = function(column) {
		
		if($scope.sortAction == 'confirm') {
			$scope.sortAction = 'sort';
		}
		else if($scope.sortAction == 'sort') {
			// TODO
			console.log('sort field profile');
		}
	};

	$scope.removeFieldProfile = function(fieldProfileId) {
		WorkflowStepRepo.remove($scope.step.id, fieldProfileId).then(function() {
     		$scope.resetFieldProfiles();
     	});
	};

	$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
		trashId: 'field-profile-trash-' + $scope.step.id,
		dragging: $scope.dragging,
		select: $scope.selectFieldProfile,
		model: $scope.step.aggregateFieldProfiles,
		confirm: '#fieldProfilesConfirmRemoveModal-' + $scope.step.id, 
		reorder: $scope.reorderFieldProfiles,
		container: '#fieldProfiles'
	});

	$scope.resetFieldProfiles();

});
