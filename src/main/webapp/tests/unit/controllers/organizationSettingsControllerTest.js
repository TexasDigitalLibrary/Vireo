describe("controller: OrganizationSettingsController", function () {

    var controller, q, scope, AccordionService, OrganizationRepo, WsApi;

    var initializeVariables = function(settings) {
        inject(function ($q, _WsApi_) {
            q = $q;

            WsApi = _WsApi_;
        });
    };

    var initializeController = function(settings) {
        inject(function ($controller, $rootScope, _AccordionService_, _ModalService_, _OrganizationRepo_, _RestApi_, _SidebarService_, _StorageService_) {
            scope = $rootScope.$new();

            AccordionService = _AccordionService_;
            OrganizationRepo = _OrganizationRepo_;

            sessionStorage.role = settings && settings.role ? settings.role : "ROLE_ADMIN";
            sessionStorage.token = settings && settings.token ? settings.token : "faketoken";

            controller = $controller("OrganizationSettingsController", {
                $scope: scope,
                $window: mockWindow(),
                AccordionService: _AccordionService_,
                ModalService: _ModalService_,
                OrganizationRepo: _OrganizationRepo_,
                RestApi: _RestApi_,
                SidebarService: _SidebarService_,
                StorageService: _StorageService_,
                WsApi: WsApi
            });

            // ensure that the isReady() is called.
            if (!scope.$$phase) {
                scope.$digest();
            }
        });
    };

    beforeEach(function() {
        module("core");
        module("vireo");
        module("mock.accordionService");
        module("mock.modalService");
        module("mock.organization");
        module("mock.organizationRepo");
        module("mock.restApi");
        module("mock.sidebarService");
        module("mock.storageService");
        module("mock.wsApi");

        installPromiseMatchers();
        initializeVariables();
        initializeController();
    });

    describe("Is the controller defined", function () {
        it("should be defined", function () {
            expect(controller).toBeDefined();
        });
    });

    describe("Are the scope methods defined", function () {
        it("activateManagementPane should be defined", function () {
            expect(scope.activateManagementPane).toBeDefined();
            expect(typeof scope.activateManagementPane).toEqual("function");
        });
        it("getSelectedOrganization should be defined", function () {
            expect(scope.getSelectedOrganization).toBeDefined();
            expect(typeof scope.getSelectedOrganization).toEqual("function");
        });
        it("managementPaneIsActive should be defined", function () {
            expect(scope.managementPaneIsActive).toBeDefined();
            expect(typeof scope.managementPaneIsActive).toEqual("function");
        });
        it("setDeleteDisabled should be defined", function () {
            expect(scope.setDeleteDisabled).toBeDefined();
            expect(typeof scope.setDeleteDisabled).toEqual("function");
        });
        it("setSelectedOrganization should be defined", function () {
            expect(scope.setSelectedOrganization).toBeDefined();
            expect(typeof scope.setSelectedOrganization).toEqual("function");
        });
    });

    describe("Do the scope methods work as expected", function () {
        it("activateManagementPane should activate the pane", function () {
            scope.activeManagementPane = null;
            scope.activateManagementPane(true);

            expect(scope.activeManagementPane).toBe(true);
        });
        it("getSelectedOrganization should get the selected organization", function () {
            var response = scope.getSelectedOrganization();

            expect(response).not.toBeDefined();

            OrganizationRepo.setSelectedOrganization(dataOrganization2);

            response = scope.getSelectedOrganization();
            expect(response.id).toBe(dataOrganization2.id);
        });
        it("managementPaneIsActive should return a boolean", function () {
            var response = scope.managementPaneIsActive("test");

            expect(response).toBe(false);

            scope.activateManagementPane("test");

            response = scope.managementPaneIsActive("test");
            expect(response).toBe(true);
        });
        it("setDeleteDisabled should assign delete disabled", function () {
            var organization = new mockOrganization(q);
            scope.deleteDisabled  = null;

            OrganizationRepo.setSelectedOrganization(organization);

            scope.setDeleteDisabled(organization.id);
            scope.$digest();
            expect(scope.deleteDisabled).toBe(false);

            OrganizationRepo.submissionsCount[organization.id] = 2;

            scope.setDeleteDisabled(organization.id);
            scope.$digest();
            expect(scope.deleteDisabled).toBe(true);
        });
        it("setSelectedOrganization should assign selected organization", function () {
            var organization = new mockOrganization(q);

            scope.setSelectedOrganization(organization);

            expect(OrganizationRepo.getSelectedOrganization().id).toBe(organization.id);

            spyOn(AccordionService, "closeAll");

            organization.mock(dataOrganization2);
            scope.setSelectedOrganization(organization);

            expect(AccordionService.closeAll).toHaveBeenCalled();
        });
    });

});
