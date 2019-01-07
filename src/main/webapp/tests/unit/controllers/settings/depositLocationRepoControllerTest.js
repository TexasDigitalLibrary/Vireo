describe('controller: DepositLocationRepoController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, _$q_, $rootScope, $window, _DepositLocationRepo_, _DragAndDropListenerFactory_, _ModalService_, _PackagerRepo_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('DepositLocationRepoController', {
                $q: _$q_,
                $scope: scope,
                $window: $window,
                DepositLocationRepo: _DepositLocationRepo_,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                PackagerRepo: _PackagerRepo_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
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
        module('mock.depositLocation');
        module('mock.depositLocationRepo');
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.packager');
        module('mock.packagerRepo');
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
