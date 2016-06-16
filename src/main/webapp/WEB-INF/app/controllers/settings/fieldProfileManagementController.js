vireo.controller("FieldProfileManagementController", function ($q, $controller, $scope, DragAndDropListenerFactory, OrganizationRepo, ControlledVocabularyRepo, FieldGlossModel, FieldPredicateModel, InputTypeService, WorkflowStepRepo) {
	
	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.selectedOrganization = OrganizationRepo.getSelectedOrganization();
	
	// if we do not want to use a watch, 
	// the OrganizationRepo can notify a promise that is subscribed here
	$scope.$watch(
		"step",
		function handleStepChanged(newStep, oldStep) {
			$scope.resetFieldProfiles();

			$scope.dragControlListeners.getListener().model = $scope.step.aggregateFieldProfiles;
			$scope.dragControlListeners.getListener().trash.id = 'field-profile-trash-' + $scope.step.id;
			$scope.dragControlListeners.getListener().confirm.remove.modal = '#fieldProfilesConfirmRemoveModal-' + $scope.step.id;
        }
    );

	$scope.controlledVocabularies = ControlledVocabularyRepo.get();

	$scope.fieldPredicates = FieldPredicateModel.getAll();
	$scope.fieldGlosses = FieldGlossModel.getAll();

	$scope.inputTypes = InputTypeService.getAll();
	
	$scope.dragging = false;
	
	$scope.sortAction = "confirm";

	$scope.uploadAction = "confirm";
	
	$scope.resetFieldProfiles = function() {
		
		var position = 1;	
		angular.forEach($scope.step.aggregateFieldProfiles, function(fieldProfile) {
			fieldProfile.position = position;
			position++;
		});

		$scope.modalData = {
			inputType: 'INPUT_TEXT'
		};
	};

	$scope.resetFieldProfiles();

	$scope.createGloss = function(glossValue) {
		// TODO set the language dynamically.
		// For now, the language must be 'English' so that's in name will match that existing on the server.
		var gloss = {'value': glossValue,
					 'language': 'English'}

		FieldGlossModel.addGloss(gloss);

		//refresh the local cache with the new gloss
		//TODO use a broadcast to update this?
		FieldGlossModel.getAll(true);
	};

	$scope.createPredicate = function(predicateValue) {
		console.info('creating predicate with val ', predicateValue);
		// TODO set the language dynamically.
		// For now, the language must be 'English' so that's in name will match that existing on the server.
		var predicate = {'value': predicateValue}

		FieldPredicateModel.addPredicate(predicate);

		//refresh the local cache with the new predicate
		//TODO use a broadcast to update this?
		FieldPredicateModel.getAll(true);
	}
	
	$scope.createFieldProfile = function(newFieldProfile) {
		
	};
	
	$scope.selectFieldProfile = function(index) {
		$scope.modalData = $scope.step.aggregateFieldProfiles[index];
	};
	
	$scope.editFieldProfile = function(index) {
		$scope.selectFieldProfile(index - 1);
		angular.element('#fieldProfilesEditModal-' + $scope.step.id).modal('show');
	};
	
	$scope.updateFieldProfile = function() {
		
	};

	$scope.reorderFieldProfiles = function(src, dest) {
		WorkflowStepRepo.reorderFieldProfile($scope.step.id, src, dest).then(function() {

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
		WorkflowStepRepo.removeFieldProfile($scope.step.id, fieldProfileId).then(function() {

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

	$scope.predicateWithValueExists = function(fpValue){
		return FieldPredicateModel.predicateWithValue(fpValue) ? true : false;
	};

	$scope.glossWithValueExists = function(glossValue){
		return FieldGlossModel.glossWithValue(glossValue) ? true : false;
	};

});
