describe('controller: OrganizationSettingsController', function () {

    var controller, scope;

    var initializeController = function(settings) {
        inject(function ($controller, $rootScope, $window, _AccordionService_, _ModalService_, _OrganizationRepo_, _RestApi_, _SidebarService_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('OrganizationSettingsController', {
                $scope: scope,
                $window: $window,
                AccordionService: _AccordionService_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
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
        module('mock.accordionService');
        module('mock.modalService');
        module('mock.organization');
        module('mock.organizationRepo');
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
