describe('controller: FieldProfileManagementController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $filter, _$q_, $rootScope, $window, _ControlledVocabularyRepo_, _DocumentTypeRepo_, _DragAndDropListenerFactory_, _FieldPredicateRepo_, _FieldProfileRepo_, _InputTypeRepo_, _ManagedConfigurationRepo_, _ModalService_, _OrganizationRepo_, _RestApi_, _StorageService_, _WorkflowStepRepo_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('FieldProfileManagementController', {
                $filter: $filter,
                $q: _$q_,
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
                StorageService: _StorageService_,
                WorkflowStepRepo: _WorkflowStepRepo_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            scope.$digest();
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

});
