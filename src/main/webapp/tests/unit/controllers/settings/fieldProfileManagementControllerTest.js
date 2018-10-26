describe('controller: FieldProfileManagementController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.controlledVocabularyRepo');
        module('mock.documentTypeRepo');
        module('mock.dragAndDropListenerFactory');
        module('mock.fieldPredicateRepo');
        module('mock.fieldProfileRepo');
        module('mock.inputTypeRepo');
        module('mock.managedConfigurationRepo');
        module('mock.modalService');
        module('mock.organizationRepo');
        module('mock.restApi');
        module('mock.workflowStepRepo');

        inject(function ($controller, $filter, $q, $rootScope, $window, _ControlledVocabularyRepo_, _DocumentTypeRepo_, _DragAndDropListenerFactory_, _FieldPredicateRepo_, _FieldProfileRepo_, _InputTypeRepo_, _ManagedConfigurationRepo_, _ModalService_, _OrganizationRepo_, _RestApi_, _WorkflowStepRepo_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('FieldProfileManagementController', {
                $filter: $filter,
                $q: $q,
                $scope: scope,
                $window: $window,
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
                WorkflowStepRepo: _WorkflowStepRepo_
            });

            // ensure that the isReady() is called.
            scope.$digest();
        });
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

});
