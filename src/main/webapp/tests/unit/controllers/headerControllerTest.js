describe('controller: HeaderController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $location, $rootScope, $timeout, $window, _AbstractRepo_, _AbstractAppRepo_, _AlertService_, _ManagedConfigurationRepo_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('HeaderController', {
                $scope: scope,
                $location: $location,
                $timeout: $timeout,
                $window: $window,
                AbstractRepo: _AbstractRepo_,
                AbstractAppRepo: _AbstractAppRepo_,
                AlertService: _AlertService_,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
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
        module('mock.abstractAppRepo');
        module('mock.abstractRepo');
        module('mock.alertService');
        module('mock.managedConfiguration');
        module('mock.managedConfigurationRepo');
        module('mock.modalService');
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
