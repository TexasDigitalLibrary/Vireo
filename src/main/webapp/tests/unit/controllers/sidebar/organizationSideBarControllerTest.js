describe('controller: OrganizationSideBarController', function () {

    var controller, q, scope, OrganizationRepo;

    var initializeController = function(settings) {
        inject(function ($controller, $q, $rootScope, _ModalService_, _OrganizationCategoryRepo_, _OrganizationRepo_, _RestApi_, _StorageService_, _WsApi_) {
            q = $q;
            scope = $rootScope.$new();

            OrganizationRepo = _OrganizationRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller('OrganizationSideBarController', {
                $q: q,
                $scope: scope,
                $window: mockWindow(),
                ModalService: _ModalService_,
                OrganizationCategoryRepo: _OrganizationCategoryRepo_,
                OrganizationRepo: _OrganizationRepo_,
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
        module('mock.organizationCategory');
        module('mock.organizationCategoryRepo');
        module('mock.organization');
        module('mock.organizationRepo');
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

    describe('Are the scope methods defined', function () {
        it('createNewOrganization should be defined', function () {
            expect(scope.createNewOrganization).toBeDefined();
            expect(typeof scope.createNewOrganization).toEqual("function");
        });
        it('reset should be defined', function () {
            expect(scope.reset).toBeDefined();
            expect(typeof scope.reset).toEqual("function");
        });
    });

    describe('Do the scope methods work as expected', function () {
        it('createNewOrganization should create a new custom action', function () {
            scope.organizations = [ mockOrganization(q) ];
            scope.creatingNewOrganization = null;

            spyOn(scope, "reset");
            spyOn(OrganizationRepo, "create").and.callThrough();

            scope.createNewOrganization("false");
            scope.$digest();

            expect(scope.reset).toHaveBeenCalled();
            expect(OrganizationRepo.create).toHaveBeenCalled();
            expect(scope.creatingNewOrganization).toBe(false);
        });
        it('reset should reset the custom action', function () {
            var organization = new mockOrganization(q);
            scope.forms = [];
            scope.modalData = organization;

            spyOn(scope.organizationRepo, "clearValidationResults");
            spyOn(organization, "refresh");

            scope.reset();

            expect(scope.organizationRepo.clearValidationResults).toHaveBeenCalled();
            expect(scope.newOrganization.category).toBeDefined();
            expect(scope.newOrganization.parent).toBeDefined();

            scope.forms.myForm = mockForms();
            scope.reset();

            scope.forms.myForm.$pristine = false;
            scope.reset();
        });
    });

});
