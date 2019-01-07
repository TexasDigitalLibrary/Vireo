describe('controller: GraduationMonthRepoController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, _$q_, $rootScope, $window, _DragAndDropListenerFactory_, _GraduationMonthRepo_, _ModalService_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('GraduationMonthRepoController', {
                $scope: scope,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                GraduationMonthRepo: _GraduationMonthRepo_,
                ModalService: _ModalService_,
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
        module('mock.dragAndDropListenerFactory');
        module('mock.graduationMonth');
        module('mock.graduationMonthRepo');
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
