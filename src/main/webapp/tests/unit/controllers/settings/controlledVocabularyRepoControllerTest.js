describe('controller: ControlledVocabularyRepoController', function () {

    var controller, scope, NgTableParams;

    var initializeController = function(settings) {
        inject(function ($controller, _$q_, $rootScope, $timeout, $window, _ControlledVocabularyRepo_, _DragAndDropListenerFactory_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('ControlledVocabularyRepoController', {
                $q: _$q_,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                ControlledVocabularyRepo: _ControlledVocabularyRepo_,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                NgTableParams: mockNgTableParams,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
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
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.ngTableParams');
        module('mock.restApi');
        module('mock.storageService');
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
