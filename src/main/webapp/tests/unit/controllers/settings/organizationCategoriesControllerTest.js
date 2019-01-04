describe('controller: OrganizationCategoriesController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, _$q_, $rootScope, $window, _DragAndDropListenerFactory_, _ModalService_, _OrganizationCategoryRepo_, _RestApi_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";

            controller = $controller('OrganizationCategoriesController', {
                $q: _$q_,
                $scope: scope,
                $window: $window,
                DragAndDropListenerFactory: _DragAndDropListenerFactory_,
                ModalService: _ModalService_,
                OrganizationCategoryRepo: _OrganizationCategoryRepo_,
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
        module('mock.dragAndDropListenerFactory');
        module('mock.modalService');
        module('mock.organizationCategory');
        module('mock.organizationCategoryRepo');
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
