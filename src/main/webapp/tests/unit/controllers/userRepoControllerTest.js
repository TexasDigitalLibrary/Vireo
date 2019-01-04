describe('controller: UserRepoController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $location, $route, _$q_, $rootScope, $timeout, $window, _ModalService_, _RestApi_, _StorageService_, _UserRepo_, _UserService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";

            controller = $controller('UserRepoController', {
                $location: $location,
                $q: _$q_,
                $route: $route,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                User: mockUser,
                UserRepo: _UserRepo_,
                UserService: _UserService_,
                WsApi: _WsApi_
            });

            // ensure that the isReady() is called.
            scope.$digest();
        });
    };

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.user');
        module('mock.userRepo');
        module('mock.userService');
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
