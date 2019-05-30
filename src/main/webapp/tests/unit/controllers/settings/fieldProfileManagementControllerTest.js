describe('controller: FieldProfileManagementController', function () {

    var controller, q, scope, FieldPredicateRepo, FieldProfileRepo, WorkflowStepRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $filter, $q, $rootScope, _ControlledVocabularyRepo_, _DocumentTypeRepo_, _DragAndDropListenerFactory_, _FieldPredicateRepo_, _FieldProfileRepo_, _InputTypeRepo_, _ManagedConfigurationRepo_, _ModalService_, _OrganizationRepo_, _RestApi_, _StorageService_, _WorkflowStepRepo_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();

            FieldPredicateRepo = _FieldPredicateRepo_;
            FieldProfileRepo = _FieldProfileRepo_;
            WorkflowStepRepo = _WorkflowStepRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            // ensure scope.step is defined.
            if (!scope.step) {
                scope.step = {aggregateFieldProfiles: {}};
            }

            controller = $controller('FieldProfileManagementController', {
                $filter: $filter,
                $q: q,
                $scope: scope,
                $window: mockWindow(),
                ControlledVocabularyRepo: _ControlledVocabularyRepo_,
                DocumentTypeRepo: _DocumentTypeRepo_,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                FieldPredicateRepo: _FieldPredicateRepo_,
                FieldProfileRepo: _FieldProfileRepo_,
                InputTypeRepo: _InputTypeRepo_,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                WorkflowStepRepo: _WorkflowStepRepo_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.controlledVocabulary');
        module('mock.controlledVocabularyRepo');
        module('mock.documentType');
        module('mock.documentTypeRepo');
        module('mock.dragAndDropListenerFactory');
        module('mock.fieldPredicate');
        module('mock.fieldPredicateRepo');
        module('mock.fieldProfile');
        module('mock.fieldProfileRepo');
        module('mock.inputType');
        module('mock.inputTypeRepo');
        module('mock.managedConfiguration');
        module('mock.managedConfigurationRepo');
        module('mock.modalService');
        module('mock.organization');
        module('mock.organizationRepo');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.workflowStep');
        module('mock.workflowStepRepo');
        module('mock.wsApi');

        installPromiseMatchers();
        initializeController();
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

    describe('Are the scope methods defined', function () {
        it('buildFilteredPredicateList should be defined', function () {
            expect(scope.buildFilteredPredicateList).toBeDefined();
            expect(typeof scope.buildFilteredPredicateList).toEqual("function");
        });
        it('canCreateFieldPredicate should be defined', function () {
            expect(scope.canCreateFieldPredicate).toBeDefined();
            expect(typeof scope.canCreateFieldPredicate).toEqual("function");
        });
        it('changeLabel should be defined', function () {
            expect(scope.changeLabel).toBeDefined();
            expect(typeof scope.changeLabel).toEqual("function");
        });
        it('createFieldPredicate should be defined', function () {
            expect(scope.createFieldPredicate).toBeDefined();
            expect(typeof scope.createFieldPredicate).toEqual("function");
        });
        it('createFieldProfile should be defined', function () {
            expect(scope.createFieldProfile).toBeDefined();
            expect(typeof scope.createFieldProfile).toEqual("function");
        });
        it('documentTypeChanged should be defined', function () {
            expect(scope.documentTypeChanged).toBeDefined();
            expect(typeof scope.documentTypeChanged).toEqual("function");
        });
        it('editFieldProfile should be defined', function () {
            expect(scope.editFieldProfile).toBeDefined();
            expect(typeof scope.editFieldProfile).toEqual("function");
        });
        it('inputTypeChanged should be defined', function () {
            expect(scope.inputTypeChanged).toBeDefined();
            expect(typeof scope.inputTypeChanged).toEqual("function");
        });
        it('isEditable should be defined', function () {
            expect(scope.isEditable).toBeDefined();
            expect(typeof scope.isEditable).toEqual("function");
        });
        it('mustCreateFieldPredicate should be defined', function () {
            expect(scope.mustCreateFieldPredicate).toBeDefined();
            expect(typeof scope.mustCreateFieldPredicate).toEqual("function");
        });
        it('openNewModal should be defined', function () {
            expect(scope.openNewModal).toBeDefined();
            expect(typeof scope.openNewModal).toEqual("function");
        });
        it('removeFieldProfile should be defined', function () {
            expect(scope.removeFieldProfile).toBeDefined();
            expect(typeof scope.removeFieldProfile).toEqual("function");
        });
        it('reorderFieldProfiles should be defined', function () {
            expect(scope.reorderFieldProfiles).toBeDefined();
            expect(typeof scope.reorderFieldProfiles).toEqual("function");
        });
        it('resetFieldProfiles should be defined', function () {
            expect(scope.resetFieldProfiles).toBeDefined();
            expect(typeof scope.resetFieldProfiles).toEqual("function");
        });
        it('selectFieldProfile should be defined', function () {
            expect(scope.selectFieldProfile).toBeDefined();
            expect(typeof scope.selectFieldProfile).toEqual("function");
        });
        it('updateFieldProfile should be defined', function () {
            expect(scope.updateFieldProfile).toBeDefined();
            expect(typeof scope.updateFieldProfile).toEqual("function");
        });
    });
    describe('Do the scope methods work as expected', function () {
        it('buildFilteredPredicateList should build the field predicates', function () {
            scope.fieldPredicates = null;

            scope.buildFilteredPredicateList();
            scope.$digest();

            expect(scope.fieldPredicates).toBeDefined();
        });
        it('canCreateFieldPredicate should return a boolean', function () {
            var result;
            scope.modalData = new mockFieldProfile(q);
            scope.modalData.fieldPredicate = "test";

            spyOn(scope, "mustCreateFieldPredicate").and.callThrough();

            result = scope.canCreateFieldPredicate();
            expect(scope.mustCreateFieldPredicate).toHaveBeenCalled();
            expect(result).toBe(true);

            scope.modalData.fieldPredicate = "";

            result = scope.canCreateFieldPredicate();
            expect(result).toBe(false);

            scope.modalData.fieldPredicate = false;

            result = scope.canCreateFieldPredicate();
            expect(result).toBe(false);
        });
        it('changeLabel should update the field predicate', function () {
            scope.modalData = new mockFieldProfile(q);
            scope.modalData.fieldPredicate = "test";
            delete scope.modalData.gloss;

            scope.changeLabel();

            expect(scope.modalData.fieldPredicate).not.toBeDefined();

            scope.modalData.fieldPredicate = "test";
            scope.modalData.gloss = "Test This";
            scope.changeLabel();

            expect(scope.modalData.fieldPredicate).toEqual("test_this");
        });
        it('createFieldPredicate should create a new field predicate', function () {
            var originalMustCreateFieldPredicate = scope.mustCreateFieldPredicate;
            scope.modalData = new mockFieldProfile(q);
            scope.modalData.fieldPredicate = "test";

            spyOn(scope, "mustCreateFieldPredicate").and.returnValue(true);
            spyOn(FieldPredicateRepo, "create").and.callThrough();

            scope.createFieldPredicate();
            scope.$digest();
            expect(scope.modalData.fieldPredicate).not.toBe("test");

            scope.mustCreateFieldPredicate = originalMustCreateFieldPredicate;
            spyOn(scope, "mustCreateFieldPredicate").and.returnValue(false);

            scope.createFieldPredicate();
            scope.$digest();
        });
        it('createFieldProfile should create a new field profile', function () {
            scope.modalData = new mockFieldProfile(q);
            scope.modalData.fieldPredicate = "test";

            spyOn(scope, "createFieldPredicate").and.callThrough();
            spyOn(WorkflowStepRepo, "addFieldProfile").and.callThrough();
            spyOn(scope, "mustCreateFieldPredicate").and.returnValue(true);

            scope.createFieldProfile();
            scope.$digest();

            expect(scope.createFieldPredicate).toHaveBeenCalled();
            expect(WorkflowStepRepo.addFieldProfile).toHaveBeenCalled();
            expect(scope.modalData.fieldPredicate).not.toEqual("test");
        });
        it('documentTypeChanged should update the field predicate', function () {
            scope.modalData = new mockFieldProfile(q);
            scope.modalData.fieldPredicate = "test";
            scope.documentData.documentType.fieldPredicate = "different";

            scope.documentTypeChanged();

            expect(scope.modalData.fieldPredicate).toEqual("different");
        });
        it('editFieldProfile should open a modal', function () {
            scope.modalData = new mockFieldProfile(q);
            scope.modalData.fieldPredicate = "test";

            spyOn(scope, "selectFieldProfile");
            spyOn(scope, "openModal");

            scope.editFieldProfile(new mockFieldPredicate(q));

            expect(scope.selectFieldProfile).toHaveBeenCalled();
            expect(scope.openModal).toHaveBeenCalled();
        });
        it('inputTypeChanged should update the input file settings', function () {
            scope.modalData = new mockFieldProfile(q);
            scope.modalData.fieldPredicate = "test";
            scope.documentData.documentType.fieldPredicate = "different";
            scope.inputFile = null;

            scope.inputTypeChanged();
            expect(scope.inputFile).toBe(false);

            scope.modalData.inputType.name = "INPUT_FILE";

            scope.inputTypeChanged();
            expect(scope.inputFile).toBe(true);
        });
        it('isEditable should update the input file settings', function () {
            var response;
            var fieldProfile = new mockFieldProfile(q);
            fieldProfile.overrideable = true;

            response = scope.isEditable(fieldProfile);
            expect(response).toBe(true);

            fieldProfile.overrideable = false;
            fieldProfile.originatingWorkflowStep = -1;
            scope.step = {
                id: 0
            };

            response = scope.isEditable(fieldProfile);
            expect(response).toBe(false);

            fieldProfile.originatingWorkflowStep = 0;
            spyOn(scope.organizationRepo, "getSelectedOrganization").and.returnValue({
                originalWorkflowSteps: [0]
            });

            fieldProfile.originatingWorkflowStep = scope.step.id;

            response = scope.isEditable(fieldProfile);
            expect(response).toBe(true);
        });
        it('mustCreateFieldPredicate should return a boolean', function () {
            var response;
            scope.modalData = new mockFieldProfile(q);
            scope.modalData.fieldPredicate = null;

            response = scope.mustCreateFieldPredicate();
            expect(response).toBe(false);

            scope.modalData.fieldPredicate = "test";

            response = scope.mustCreateFieldPredicate();
            expect(response).toBe(true);
        });
        it('openNewModal should open a modal', function () {
            spyOn(scope, "openModal");

            scope.openNewModal();

            expect(scope.openModal).toHaveBeenCalled();
        });
        it('removeFieldProfile should delete a fieldPredicate', function () {
            spyOn(WorkflowStepRepo, "removeFieldProfile");

            scope.removeFieldProfile();

            expect(WorkflowStepRepo.removeFieldProfile).toHaveBeenCalled();
        });
        it('reorderFieldProfiles should reorder a field profile', function () {
            spyOn(WorkflowStepRepo, "reorderFieldProfiles");

            scope.reorderFieldProfiles("a", "b");

            expect(WorkflowStepRepo.reorderFieldProfiles).toHaveBeenCalled();
        });
        it('resetFieldProfiles should reset the field profile', function () {
            var fieldProfile = new mockFieldProfile(q);
            scope.forms = [];
            scope.modalData = fieldProfile;

            spyOn(scope.workflowStepRepo, "clearValidationResults");
            spyOn(scope.fieldProfileRepo, "clearValidationResults");
            spyOn(scope.fieldPredicateRepo, "clearValidationResults");
            spyOn(fieldProfile, "refresh");
            spyOn(scope, "closeModal");

            scope.resetFieldProfiles();

            expect(scope.workflowStepRepo.clearValidationResults).toHaveBeenCalled();
            expect(scope.fieldProfileRepo.clearValidationResults).toHaveBeenCalled();
            expect(scope.fieldPredicateRepo.clearValidationResults).toHaveBeenCalled();
            expect(fieldProfile.refresh).toHaveBeenCalled();
            expect(scope.closeModal).toHaveBeenCalled();
            expect(scope.modalData).toBeDefined();
        });
        it('selectFieldProfile should select a field profile', function () {
            // FIXME: this was confusing to write, review the method to ensure that the logic of the method makes sense.
            scope.modalData = null;
            scope.step = {
                aggregateFieldProfiles: [
                    new mockFieldProfile(q),
                    new mockFieldProfile(q)
                ]
            };
            scope.step.aggregateFieldProfiles[1].mock(dataFieldProfile2);
            scope.documentTypes = [
                new mockDocumentType(q),
                new mockDocumentType(q)
            ];
            scope.documentTypes[0].fieldPredicate = new mockFieldPredicate(q);
            scope.documentTypes[1].mock(dataDocumentType2);
            scope.documentTypes[1].fieldPredicate = new mockFieldPredicate(q);
            scope.documentTypes[1].fieldPredicate.mock(dataFieldPredicate2);

            spyOn(scope, "inputTypeChanged");

            scope.selectFieldProfile(1);
            expect(scope.inputTypeChanged).not.toHaveBeenCalled();

            scope.modalData = new mockFieldProfile(q);
            scope.step.aggregateFieldProfiles[1].fieldPredicate = scope.documentTypes[0].fieldPredicate;
            scope.step.aggregateFieldProfiles[1].fieldPredicate.documentTypePredicate = true;

            scope.selectFieldProfile(1);
            expect(scope.documentData.documentType.id).toBe(scope.documentTypes[0].id);
            expect(scope.inputTypeChanged).toHaveBeenCalled();
        });
        it('updateFieldProfile should should save a fieldPredicate', function () {
            scope.modalData = new mockFieldProfile(q);
            scope.modalData.notReset = true;

            spyOn(scope, "createFieldPredicate").and.callThrough();
            spyOn(WorkflowStepRepo, "updateFieldProfile").and.callThrough();

            scope.updateFieldProfile();
            scope.$digest();

            expect(scope.createFieldPredicate).toHaveBeenCalled();
            expect(WorkflowStepRepo.updateFieldProfile).toHaveBeenCalled();
            expect(scope.modalData.notReset).not.toBeDefined();
        });
    });

});
