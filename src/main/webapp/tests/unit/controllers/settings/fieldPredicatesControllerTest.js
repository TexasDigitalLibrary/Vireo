describe('controller: FieldPredicatesController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($filter, _$q_, $controller, $rootScope, $timeout, $window, _DragAndDropListenerFactory_, _FieldPredicateRepo_, _ModalService_, _RestApi_, _SidebarService_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('FieldPredicatesController', {
                $filter: $filter,
                $q: _$q_,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                FieldPredicateRepo: _FieldPredicateRepo_,
                ModalService: _ModalService_,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
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
        module('mock.dragAndDropListenerFactory');
        module('mock.fieldPredicate');
        module('mock.fieldPredicateRepo');
        module('mock.modalService');
        module('mock.restApi');
        module('mock.sidebarService');
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
