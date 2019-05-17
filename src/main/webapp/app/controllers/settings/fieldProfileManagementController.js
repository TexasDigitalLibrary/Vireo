vireo.controller("FieldProfileManagementController", function ($q, $controller, $scope, $filter, DragAndDropListenerFactory, DocumentTypeRepo, FieldProfileRepo, OrganizationRepo, ControlledVocabularyRepo, FieldPredicateRepo, InputTypeRepo, WorkflowStepRepo, ManagedConfigurationRepo) {

    angular.extend(this, $controller("AbstractController", {
        $scope: $scope
    }));

    $scope.shibbolethAttributes = ManagedConfigurationRepo.getAllShibbolethConfigurations();

    $scope.organizationRepo = OrganizationRepo;

    $scope.workflowStepRepo = WorkflowStepRepo;

    $scope.fieldProfileRepo = FieldProfileRepo;

    $scope.fieldPredicateRepo = FieldPredicateRepo;

    $scope.controlledVocabularies = ControlledVocabularyRepo.getAll();

    $scope.fieldPredicates = FieldPredicateRepo.getAll();

    $scope.inputTypes = InputTypeRepo.getAll();

    $scope.documentTypes = DocumentTypeRepo.getAll();

    $scope.dragging = false;

    $scope.sortAction = "confirm";

    $scope.uploadAction = "confirm";

    $scope.filteredPredicates = {};

    $scope.documentData = {
        documentType: {}
    };

    $scope.forms = {};

    $scope.ready = $q.all([ManagedConfigurationRepo.ready(), ControlledVocabularyRepo.ready(), FieldPredicateRepo.ready(), InputTypeRepo.ready(), DocumentTypeRepo.ready()]);

    $scope.ready.then(function () {

        var resetModalData = function(data) {
            $scope.modalData = {
                enabled: true,
                overrideable: true,
                inputType: {
                    "id": 1,
                    "name": "INPUT_TEXT"
                },
                optional: true,
                repeatable: false,
                flagged: false,
                logged: false,
                hidden: false,
                gloss: ''
            };
            if(data !== undefined) {
                angular.extend($scope.modalData, data);
            }
        };

        $scope.$watch("step", function handleStepChanged(newStep, oldStep) {
            $scope.resetFieldProfiles();

            $scope.dragControlListeners.getListener().model = $scope.step.aggregateFieldProfiles;
            $scope.dragControlListeners.getListener().trash.id = 'field-profile-trash-' + $scope.step.id;
            $scope.dragControlListeners.getListener().confirm.remove.modal = '#fieldProfilesConfirmRemoveModal-' + $scope.step.id;
        });

        $scope.inputTypeChanged = function () {
            if ($scope.modalData.inputType.name === "INPUT_FILE") {
                $scope.inputFile = true;
                $scope.modalData.fieldPredicate = $scope.documentData.documentType.fieldPredicate;
            } else {
                $scope.inputFile = false;
            }
        };

        $scope.documentTypeChanged = function () {
            $scope.modalData.fieldPredicate = $scope.documentData.documentType.fieldPredicate;
        };

        $scope.resetFieldProfiles = function () {
            $scope.workflowStepRepo.clearValidationResults();
            $scope.fieldProfileRepo.clearValidationResults();
            $scope.fieldPredicateRepo.clearValidationResults();
            for (var key in $scope.forms) {
                if ($scope.forms[key] !== undefined && !$scope.forms[key].$pristine) {
                    $scope.forms[key].$setPristine();
                    $scope.forms[key].$setUntouched();
                }
            }

            var position = 1;

            angular.forEach($scope.step.aggregateFieldProfiles, function (fieldProfile) {
                fieldProfile.position = position;
                position++;
            });

            if ($scope.modalData !== undefined && $scope.modalData.refresh !== undefined) {
                $scope.modalData.refresh();
            }

            $scope.inputFile = false;

            resetModalData();

            angular.extend($scope.documentData.documentType, $scope.documentTypes[0]);

            $scope.closeModal();
        };

        $scope.resetFieldProfiles();

        $scope.changeLabel = function() {
            if(angular.isDefined($scope.modalData.gloss) && (angular.isUndefined($scope.modalData.fieldPredicate) || $scope.mustCreateFieldPredicate())) {
                $scope.modalData.fieldPredicate = $scope.modalData.gloss.toLowerCase().replace(/ /g, '_');
                for(var i in $scope.fieldPredicates) {
                    if($scope.fieldPredicates[i].value === $scope.modalData.fieldPredicate) {
                        $scope.modalData.fieldPredicate = $scope.fieldPredicates[i];
                        break;
                    }
                }
            } else if(angular.isUndefined($scope.modalData.gloss) && typeof $scope.modalData.fieldPredicate === 'string') {
                delete $scope.modalData.fieldPredicate;
            }
        };
        
        $scope.mustCreateFieldPredicate = function () {
            return typeof $scope.modalData.fieldPredicate === 'string';
        };
        
        $scope.canCreateFieldPredicate = function () {
            return $scope.mustCreateFieldPredicate() && $scope.modalData.fieldPredicate.length > 0;
        };

        $scope.createFieldPredicate = function () {
            return $q(function(resolve) {
                if($scope.mustCreateFieldPredicate()) {
                    FieldPredicateRepo.create({
                        value: $scope.modalData.fieldPredicate,
                        documentTypePredicate: false
                    }).then(function (response) {
                        var apiRes = angular.fromJson(response.body);
                        if (apiRes.meta.status === "SUCCESS") {
                            $scope.modalData.fieldPredicate = apiRes.payload.FieldPredicate;
                            resolve();
                        }
                    });
                } else {
                    resolve();
                }
            });
        };
        
        $scope.createFieldProfile = function () {
            $scope.createFieldPredicate().then(function() {
                WorkflowStepRepo.addFieldProfile($scope.step, $scope.modalData).then(function() {
                    resetModalData();
                });
            });
        };

        $scope.selectFieldProfile = function (index) {
            var fieldProfile = $scope.step.aggregateFieldProfiles[index];
            resetModalData(fieldProfile);
            if ($scope.modalData.fieldPredicate.documentTypePredicate) {
                angular.forEach($scope.documentTypes, function (documentType) {
                    if (documentType.fieldPredicate.id == $scope.modalData.fieldPredicate.id) {
                        angular.extend($scope.documentData.documentType, documentType);
                        $scope.inputTypeChanged();
                    }
                });
            }
        };

        $scope.editFieldProfile = function (index) {
            $scope.selectFieldProfile(index - 1);
            $scope.openModal('#fieldProfilesEditModal-' + $scope.step.id);
        };

        $scope.updateFieldProfile = function () {
            $scope.createFieldPredicate().then(function() {
                WorkflowStepRepo.updateFieldProfile($scope.step, $scope.modalData).then(function() {
                    resetModalData();
                });
            });
        };

        $scope.removeFieldProfile = function () {
            WorkflowStepRepo.removeFieldProfile($scope.step, $scope.modalData);
        };

        $scope.reorderFieldProfiles = function (src, dest) {
            return WorkflowStepRepo.reorderFieldProfiles($scope.step, src, dest);
        };

        $scope.isEditable = function (fieldProfile) {
            var editable = fieldProfile.overrideable;
            if (!editable) {
                editable = fieldProfile.originatingWorkflowStep == $scope.step.id && $scope.organizationRepo.getSelectedOrganization().originalWorkflowSteps.indexOf(fieldProfile.originatingWorkflowStep) > -1;
            }
            return editable;
        };

        $scope.openNewModal = function (id) {
            $scope.openModal('#fieldProfilesNewModal-' + id);
        };

        $scope.dragControlListeners = DragAndDropListenerFactory.buildDragControls({
            trashId: 'field-profile-trash-' + $scope.step.id,
            dragging: $scope.dragging,
            select: $scope.selectFieldProfile,
            model: $scope.step.aggregateFieldProfiles,
            confirm: '#fieldProfilesConfirmRemoveModal-' + $scope.step.id,
            reorder: $scope.reorderFieldProfiles,
            container: '#field-profiles'
        });

        $scope.buildFilteredPredicateList = function () {
            $scope.filteredPredicates = $filter('filter')($scope.fieldPredicates, function (predicate) {
                return !predicate.documentTypePredicate;
            });
        };

        FieldPredicateRepo.ready().then(function () {
            $scope.buildFilteredPredicateList();
        });

        FieldPredicateRepo.listen(function () {
            $scope.buildFilteredPredicateList();
        });

    });

});
