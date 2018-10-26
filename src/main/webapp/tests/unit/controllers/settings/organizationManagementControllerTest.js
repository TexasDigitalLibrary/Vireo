describe('controller: OrganizationManagementController', function () {

    var controller, scope;

    beforeEach(function() {
        module('core');
        module('vireo');
        module('mock.accordionService');
        module('mock.alertService');
        module('mock.apiResponseActions');
        module('mock.modalService');
        module('mock.organization');
        module('mock.organizationRepo');
        module('mock.organizationCategoryRepo');
        module('mock.restApi');
        module('mock.workflowStepRepo');

        inject(function ($controller, $location, $q, $rootScope, $route, $timeout, $window, _AccordionService_, _AlertService_, _ApiResponseActions_, _ModalService_, _Organization_, _OrganizationRepo_, _OrganizationCategoryRepo_, _RestApi_, _WorkflowStepRepo_) {
            installPromiseMatchers();
            scope = $rootScope.$new();

            controller = $controller('OrganizationManagementController', {
                $q: $q,
                $location: $location,
                $route: $route,
                $scope: scope,
                $timeout: $timeout,
                $window: $window,
                AccordionService: _AccordionService_,
                AlertService: _AlertService_,
                ApiResponseActions: _ApiResponseActions_,
                ModalService: _ModalService_,
                Organization: _Organization_,
                OrganizationRepo: _OrganizationRepo_,
                OrganizationCategoryRepo: _OrganizationCategoryRepo_,
                RestApi: _RestApi_,
                WorkflowStepRepo: _WorkflowStepRepo_
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
