vireo.controller("FieldProfileManagementController", function ($q, $controller, $scope, DragAndDropListenerFactory, FieldProfileRepo, OrganizationRepo, ControlledVocabularyRepo, FieldGlossRepo, FieldPredicateRepo, InputTypeRepo, WorkflowStepRepo) {
    
    angular.extend(this, $controller("AbstractController", {$scope: $scope}));

    $scope.workflowStepRepo = WorkflowStepRepo;

    $scope.selectedOrganization = OrganizationRepo.getSelectedOrganization();
    
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

    $scope.fieldProfileRepo = FieldProfileRepo;

    $scope.fieldPredicateRepo = FieldPredicateRepo;

    $scope.fieldPredicates = FieldPredicateRepo.getAll();

    $scope.fieldGlossRepo = FieldGlossRepo;

    $scope.fieldGlosses = FieldGlossRepo.getAll();

    $scope.inputTypes = InputTypeRepo.getAll();
    
    $scope.dragging = false;
    
    $scope.sortAction = "confirm";

    $scope.uploadAction = "confirm";

    $scope.forms = {};
    
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
        $scope.modalData = {
            overrideable: true,
            inputType: {
                "id": 1,
                "name": "INPUT_TEXT"
            },
            repeatable: false,
            fieldGlosses: [],
            controlledVocabularies: []
        };

        $scope.closeModal();
    };

    $scope.resetFieldProfiles();

    $scope.createGloss = function(glossValue) {
        // TODO set the language dynamically.
        // For now, the language must be 'English' so that's in name will match that existing on the server.
    	$scope.modalData.fieldGlosses[0] = {
        	'value': glossValue, 
            'language': 'English'
        };
        FieldGlossRepo.create($scope.modalData.fieldGlosses[0]).then(function(response) {
        	angular.extend($scope.modalData.fieldGlosses[0], angular.fromJson(response.body).payload.FieldGloss);
        });
    };

    $scope.createFieldPredicate = function() {
        FieldPredicateRepo.create($scope.modalData.fieldPredicate).then(function(response) {
            if(angular.fromJson(response.body).meta.type == "SUCCESS") {
                $scope.modalData.fieldPredicate = angular.fromJson(response.body).payload.FieldPredicate;
            }
        });
    };
    
    $scope.createFieldProfile = function() {
        WorkflowStepRepo.addFieldProfile($scope.step, $scope.modalData);
    };
    
    $scope.selectFieldProfile = function(index) {
        var fieldProfile = $scope.step.aggregateFieldProfiles[index];
        $scope.modalData = fieldProfile;
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
            editable = fieldProfile.originatingWorkflowStep == $scope.step.id && 
                       $scope.selectedOrganization.originalWorkflowSteps.indexOf(fieldProfile.originatingWorkflowStep) > -1;
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

});
