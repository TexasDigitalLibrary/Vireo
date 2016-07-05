vireo.controller("FieldProfileManagementController", function ($q, $controller, $scope, DragAndDropListenerFactory, OrganizationRepo, ControlledVocabularyRepo, FieldGlossRepo, FieldPredicateRepo, InputTypeRepo, WorkflowStepRepo) {
	
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

	$scope.controlledVocabularies = ControlledVocabularyRepo.getAll();

	$scope.fieldPredicates = FieldPredicateRepo.getAll();

	$scope.fieldGlosses = FieldGlossRepo.getAll();

	$scope.inputTypes = InputTypeRepo.getAll();
	
	$scope.dragging = false;
	
	$scope.sortAction = "confirm";

	$scope.uploadAction = "confirm";
	
	$scope.resetFieldProfiles = function() {
		
		var position = 1;

		angular.forEach($scope.step.aggregateFieldProfiles, function(fieldProfile) {
			fieldProfile.position = position;
			// TODO: needs multi glosses
			fieldProfile.gloss = fieldProfile.fieldGlosses[0] ? fieldProfile.fieldGlosses[0] : null;
			// TODO: needs multi controlled vocabulary
			fieldProfile.controlledVocabulary = fieldProfile.controlledVocabularies[0] ? fieldProfile.controlledVocabularies[0] : null;
			position++;
		});

		$scope.modalData = {
			overrideable: true,
			inputType: {
				"id": 1,
				"name": "INPUT_TEXT"
			},
			repeatable: false
		};
	};

	$scope.resetFieldProfiles();

	$scope.createGloss = function(glossValue) {
		// TODO set the language dynamically.
		// For now, the language must be 'English' so that's in name will match that existing on the server.
		var gloss = {
			'value': glossValue, 
			'language': 'English'
		};

		FieldGlossRepo.create(gloss).then(function(response){
			$scope.modalData.gloss = angular.fromJson(response.body).payload.FieldGloss;
		});

	};

	$scope.createPredicate = function(predicateValue) {
		var predicate = {'value': predicateValue}

		FieldPredicateRepo.create(predicate).then(function(response){
			$scope.modalData.predicate = angular.fromJson(response.body).payload.FieldPredicate;
		});

	}
	
	$scope.createFieldProfile = function() {
		WorkflowStepRepo.addFieldProfile($scope.step.id, $scope.modalData).then(function() {

		});
	};
	
	$scope.selectFieldProfile = function(index) {
		var fieldProfile = $scope.step.aggregateFieldProfiles[index];
		$scope.modalData = fieldProfile;

		// TODO: needs multi glosses
		$scope.modalData.gloss = fieldProfile.fieldGlosses[0] ? fieldProfile.fieldGlosses[0] : null;
		// TODO: needs multi controlled vocabulary
		$scope.modalData.controlledVocabulary = fieldProfile.controlledVocabularies[0] ? fieldProfile.controlledVocabularies[0] : null;
	};
	
	$scope.editFieldProfile = function(index) {
		$scope.selectFieldProfile(index - 1);
		angular.element('#fieldProfilesEditModal-' + $scope.step.id).modal('show');
	};
	
	$scope.updateFieldProfile = function() {
		WorkflowStepRepo.updateFieldProfile($scope.step.id, $scope.modalData).then(function() {

		});
	};

	$scope.reorderFieldProfiles = function(src, dest) {
		WorkflowStepRepo.reorderFieldProfile($scope.step.id, src, dest).then(function() {

		});
	};

	$scope.removeFieldProfile = function(fieldProfileId) {
		WorkflowStepRepo.removeFieldProfile($scope.step.id, fieldProfileId).then(function() {

     	});
	};

	$scope.isEditable = function(fieldProfile) {
		var editable = fieldProfile.overrideable;
		if(!editable) {
			editable = fieldProfile.originatingWorkflowStep == $scope.step.id && 
					   $scope.selectedOrganization.originalWorkflowSteps.indexOf(fieldProfile.originatingWorkflowStep) > -1;
		}
		return editable;
	}

	$scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
		trashId: 'field-profile-trash-' + $scope.step.id,
		dragging: $scope.dragging,
		select: $scope.selectFieldProfile,
		model: $scope.step.aggregateFieldProfiles,
		confirm: '#fieldProfilesConfirmRemoveModal-' + $scope.step.id, 
		reorder: $scope.reorderFieldProfiles,
		container: '#fieldProfiles'
	});

});
