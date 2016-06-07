vireo.controller("FieldProfileManagementController", function ($controller, $scope, DragAndDropListenerFactory, OrganizationRepo, ControlledVocabularyRepo) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.selectedOrganization = OrganizationRepo.getSelectedOrganization();
	
	$scope.fieldProfiles = {
		list: $scope.step.aggregateFieldProfiles
	};

	// TODO: remove after refactoring out arrays put onto map with list as key
	$scope.$watch(
        "step.aggregateFieldProfiles",
        function handleStepChanged(newStepFieldProfiles, oldStepFieldProfiles) {
            $scope.fieldProfiles.list = newStepFieldProfiles;
            $scope.resetFieldProfiles();
        }
    );

	$scope.controlledVocabularies = ControlledVocabularyRepo.get();
	console.info($scope.controlledVocabularies);
	
	$scope.dragging = false;

	$scope.trashCanId = 'field-profile-trash-' + $scope.step.id;
	
	$scope.sortAction = "confirm";

	$scope.uploadAction = "confirm";
	
	$scope.resetFieldProfiles = function() {
		var position = 1;	
		angular.forEach($scope.fieldProfiles.list, function(fieldProfile) {
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
		trashId: $scope.trashCanId,
		dragging: $scope.dragging,
		select: $scope.selectFieldProfile,
		model: $scope.fieldProfiles,
		confirm: '#fieldProfilesConfirmRemoveModal-' + $scope.step.id, 
		reorder: $scope.reorderFieldProfiles,
		container: '#fieldProfiles'
	});

	$scope.resetFieldProfiles();

});
