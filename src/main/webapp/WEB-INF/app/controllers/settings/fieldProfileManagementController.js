vireo.controller("FieldProfileManagementController", function ($q, $controller, $scope, $filter, DragAndDropListenerFactory, AttachmentTypeRepo, FieldProfileRepo, OrganizationRepo, ControlledVocabularyRepo, FieldGlossRepo, FieldPredicateRepo, InputTypeRepo, WorkflowStepRepo) {

	angular.extend(this, $controller("AbstractController", {$scope: $scope}));

	$scope.workflowStepRepo = WorkflowStepRepo;	

	$scope.fieldProfileRepo = FieldProfileRepo;

	$scope.fieldPredicateRepo = FieldPredicateRepo;

	$scope.fieldGlossRepo = FieldGlossRepo;

	$scope.controlledVocabularies = ControlledVocabularyRepo.getAll();

	$scope.fieldPredicates = FieldPredicateRepo.getAll();

	$scope.fieldGlosses = FieldGlossRepo.getAll();

	$scope.inputTypes = InputTypeRepo.getAll();

	$scope.attachmentTypes = AttachmentTypeRepo.getAll();


	$scope.dragging = false;

	$scope.sortAction = "confirm";

	$scope.uploadAction = "confirm";

	
	$scope.advanced = false;
	
	$scope.controlledVocabulary = false;
	

	$scope.filteredPredicates = {};

	$scope.documentData = {
		attachmentType: {}
	};

	$scope.forms = {};

	$scope.ready = $q.all([
		ControlledVocabularyRepo.ready(),
		FieldPredicateRepo.ready(),
		FieldGlossRepo.ready(),
		InputTypeRepo.ready(),
		AttachmentTypeRepo.ready()
	]);

	$scope.ready.then(function() {

		$scope.$watch(
			"step",
			function handleStepChanged(newStep, oldStep) {
				$scope.resetFieldProfiles();

				$scope.dragControlListeners.getListener().model = $scope.step.aggregateFieldProfiles;
				$scope.dragControlListeners.getListener().trash.id = 'field-profile-trash-' + $scope.step.id;
				$scope.dragControlListeners.getListener().confirm.remove.modal = '#fieldProfilesConfirmRemoveModal-' + $scope.step.id;
			}
		);

		FieldPredicateRepo.ready().then(function(){
			$scope.buildFilteredPredicateList();
		});
		FieldPredicateRepo.listen(function(){
			$scope.buildFilteredPredicateList();
		});

		$scope.inputTypeChanged = function() {
			if($scope.modalData.inputType.name == "INPUT_FILE") {
				$scope.inputFile = true;
				$scope.modalData.fieldPredicate = $scope.documentData.attachmentType.fieldPredicate;
			}
			else {
				$scope.inputFile = false;
				delete $scope.modalData.fieldPredicate;
			}
		};

		$scope.attachmentTypeChanged = function() {
			$scope.modalData.fieldPredicate = $scope.documentData.attachmentType.fieldPredicate;
		};

		$scope.resetFieldProfiles = function() {
			$scope.workflowStepRepo.clearValidationResults();
			$scope.fieldProfileRepo.clearValidationResults();
			$scope.fieldPredicateRepo.clearValidationResults();
			$scope.fieldGlossRepo .clearValidationResults();
			for(var key in $scope.forms) {
				if($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
					$scope.forms[key].$setPristine();
				}
			}

			var position = 1;

			angular.forEach($scope.step.aggregateFieldProfiles, function(fieldProfile) {
				fieldProfile.position = position;
				position++;
			});

			if($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
				$scope.modalData.refresh();
			}

			$scope.inputFile = false;

			$scope.modalData = {
				enabled: true,
				overrideable: true,
				inputType: {
					"id": 1,
					"name": "INPUT_TEXT"
				},
				optional: true,
				repeatable: false,
				fieldGlosses: [],
				controlledVocabularies: []
			};

			angular.extend($scope.documentData.attachmentType, $scope.attachmentTypes[0]);

			$scope.closeModal();
		};

		$scope.resetFieldProfiles();

		$scope.createFieldGloss = function(glossValue) {
			// TODO set the language dynamically.
			// For now, the language must be 'English' so that's in name will match that existing on the server.
			$scope.modalData.fieldGlosses[0] = {
				'value': glossValue,
				'language': 'English'
			};
			FieldGlossRepo.create($scope.modalData.fieldGlosses[0]).then(function(response) {
				var body = angular.fromJson(response.body);
				if(body.meta.type == 'SUCCESS') {
					angular.extend($scope.modalData.fieldGlosses[0], body.payload.FieldGloss);
					if(!$scope.advanced) {
						$scope.modalData.fieldPredicate = body.payload.FieldGloss.value.toLowerCase();
						$scope.createFieldPredicate();
					}
				}
			});
		};

		$scope.createFieldPredicate = function() {
			FieldPredicateRepo.create({
				value: $scope.modalData.fieldPredicate,
				attachmentTypePredicate: false
			}).then(function(response) {
				var body = angular.fromJson(response.body);
				if(body.meta.type == "SUCCESS") {
					$scope.modalData.fieldPredicate = body.payload.FieldPredicate;
				}
			});
		};

		$scope.createFieldProfile = function() {
			WorkflowStepRepo.addFieldProfile($scope.step, $scope.modalData);
		};

		$scope.selectFieldProfile = function(index) {
			var fieldProfile = $scope.step.aggregateFieldProfiles[index];
			$scope.modalData = fieldProfile;
			

			if($scope.modalData.fieldPredicate.attachmentTypePredicate) {
				angular.forEach($scope.attachmentTypes, function(attachmentType) {
					if(attachmentType.fieldPredicate.id == $scope.modalData.fieldPredicate.id) {
						angular.extend($scope.documentData.attachmentType, attachmentType);
						$scope.inputTypeChanged();
					}
				});
			}
			
		};

		$scope.editFieldProfile = function(index) {
			$scope.selectFieldProfile(index - 1);
			$scope.openModal('#fieldProfilesEditModal-' + $scope.step.id);
		};

		$scope.updateFieldProfile = function() {
			WorkflowStepRepo.updateFieldProfile($scope.step, $scope.modalData);
		};

		$scope.removeFieldProfile = function() {
			WorkflowStepRepo.removeFieldProfile($scope.step, $scope.modalData);
		};

		$scope.reorderFieldProfiles = function(src, dest) {
			WorkflowStepRepo.reorderFieldProfile($scope.step, src, dest);
		};

		$scope.isEditable = function(fieldProfile) {
			var editable = fieldProfile.overrideable;
			if(!editable) {
				editable = fieldProfile.originatingWorkflowStep == $scope.step.id && OrganizationRepo.getSelectedOrganization().originalWorkflowSteps.indexOf(fieldProfile.originatingWorkflowStep) > -1;
			}
			return editable;
		};

		$scope.openNewModal = function(id) {
			$scope.openModal('#fieldProfilesNewModal-' + id);
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

		$scope.buildFilteredPredicateList = function(){
			$scope.filteredPredicates = $filter('filter')($scope.fieldPredicates, function(predicate){
				return !predicate.attachmentTypePredicate;
			});
		};

	});

});