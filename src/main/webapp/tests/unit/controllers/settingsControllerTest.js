describe('controller: SettingsController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $injector, $rootScope, $timeout, $window, _ManagedConfigurationRepo_, _ModalService_, _RestApi_, _StorageService_, _StudentSubmissionRepo_, _UserService_, _WsApi_) {
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";

            controller = $controller('SettingsController', {
                $scope: scope,
                $injector: $injector,
                $timeout: $timeout,
                $window: $window,
                ManagedConfigurationRepo: _ManagedConfigurationRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                StudentSubmissionRepo: _StudentSubmissionRepo_,
                User: mockUser,
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
        module('mock.managedConfiguration');
        module('mock.managedConfigurationRepo');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.studentSubmission');
        module('mock.studentSubmissionRepo');
        module('mock.user');
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
