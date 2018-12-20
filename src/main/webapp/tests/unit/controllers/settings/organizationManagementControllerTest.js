describe('controller: OrganizationManagementController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.accordionService');
        module('mock.alertService');
        module('mock.modalService');
        module('mock.organization');
        module('mock.organizationRepo');
        module('mock.organizationCategory');
        module('mock.organizationCategoryRepo');
        module('mock.restApi');
        module('mock.storageService');
        module('mock.workflowStepRepo');
        module('mock.wsApi');

        inject(function ($controller, $location, _$q_, $rootScope, $route, $timeout, $window, _AccordionService_, _AlertService_, _ModalService_, _OrganizationRepo_, _OrganizationCategoryRepo_, _RestApi_, _StorageService_, _WorkflowStepRepo_, _WsApi_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('OrganizationManagementController', {
                $q: _$q_,
                $location: $location,
                $route: $route,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                AccordionService: _AccordionService_,
                AlertService: _AlertService_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                OrganizationCategoryRepo: _OrganizationCategoryRepo_,
                RestApi: _RestApi_,
                StorageService: _StorageService_,
                WorkflowStepRepo: _WorkflowStepRepo_,
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
