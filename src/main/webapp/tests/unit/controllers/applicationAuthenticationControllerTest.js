describe('controller: ApplicationAuthenticationController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.user');
        module('mock.userService');
        module('mock.validationStore');
        module('mock.wsApi');

        inject(function ($controller, $location, $rootScope, $window, _ModalService_, _RestApi_, _StorageService_, _UserService_, _ValidationStore_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('ApplicationAuthenticationController', {
                $location: $location,
                $scope: scope,
                $window: $window,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                UserService: _UserService_,
                ValidationStore: _ValidationStore_,
                WsApi: _WsApi_
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
