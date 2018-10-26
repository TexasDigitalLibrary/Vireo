describe('controller: OrganizationSettingsController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.accordionService');
        module('mock.modalService');
        module('mock.organizationRepo');
        module('mock.restApi');
        module('mock.sidebarService');
        module('mock.storageService');
        module('mock.wsApi');

        inject(function ($controller, $rootScope, $window, _AccordionService_, _ModalService_, _OrganizationRepo_, _RestApi_, _SidebarService_, _StorageService_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

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
            scope.$digest();
        });
    });

    describe('Is the controller defined', function () {
        it('should be defined', function () {
            expect(controller).toBeDefined();
        });
    });

});
